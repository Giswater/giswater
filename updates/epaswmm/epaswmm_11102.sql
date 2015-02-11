/*
This file is part of Giswater
The program is free software: you can redistribute it and/or modify it under the terms of the GNU General Public License as published by the Free Software Foundation, either version 3 of the License, or (at your option) any later version.
This version of Giswater is provided by Giswater Association
*/

--------------------------------------------------------------------------------------------------
-- The purpose of this file is to be a storage of scripts in order to allow the possibility of update epaswmm schemas created in older version from this version to newest version
-- Changes incorporated in version 1.1.102 - 02/02/2015 
--------------------------------------------------------------------------------------------------


-- ------------------------------------------------------------
-- MODIFICATIONS OF EPA SWMM 5.1007
-- New options and new report capabilities
-- ------------------------------------------------------------

-- ------------------------------------------------------------
-- Incorporation 1 new register into options_in
-- ------------------------------------------------------------
INSERT INTO "SCHEMA_NAME"."inp_value_options_in" VALUES ('MODIFIED_HORTON'); 

-- ----------------------------
-- Incorporation of View structure for v_inp_infiltration_mh
-- ----------------------------

DROP VIEW "SCHEMA_NAME"."v_inp_infiltration_ho";
CREATE VIEW "SCHEMA_NAME"."v_inp_infiltration_ho" AS 
SELECT subcatchment.subc_id, subcatchment.maxrate, subcatchment.minrate, subcatchment.decay, subcatchment.drytime, subcatchment.maxinfil, sector_selection.sector_id, cat_hydrology.infiltration 
FROM (SCHEMA_NAME.subcatchment 
JOIN SCHEMA_NAME.sector_selection ON (((subcatchment.sector_id)::text = (sector_selection.sector_id)::text))
JOIN SCHEMA_NAME.cat_hydrology ON (((subcatchment.hydrology_id)::text = (cat_hydrology.id)::text))
JOIN SCHEMA_NAME.hydrology_selection ON (((subcatchment.hydrology_id)::text = (hydrology_selection.hydrology_id)::text)))
WHERE ((cat_hydrology.infiltration)::text = 'MODIFIED_HORTON'::text) or ((cat_hydrology.infiltration)::text = 'HORTON'::text);



-- ----------------------------
-- Modification of trigger of v_inp_edit_junction
-------------------------------

CREATE OR REPLACE FUNCTION SCHEMA_NAME.update_v_inp_edit_junction() RETURNS trigger LANGUAGE plpgsql AS $$

DECLARE 
	numNodes numeric;
	sectorRecord record;
	auxNode_ID varchar;

BEGIN

	EXECUTE 'SET search_path TO '||quote_literal(TG_TABLE_SCHEMA)||', public';
	
    --	Control insertions ID	
	IF TG_OP = 'INSERT' THEN

--		Existing nodes
		numNodes := (SELECT COUNT(*) FROM node nodeOld WHERE nodeOld.the_geom && ST_Expand(NEW.the_geom, 0.1));

--		If there is an existing node closer than 0.5 meters --> error
		IF (numNodes = 0) THEN

--			Node ID
			IF (NEW.node_id IS NULL) THEN
				NEW.node_id := (SELECT nextval('inp_node_id_seq'));
			END IF;
			
--			top_elev, ymax
			IF (NEW.top_elev IS NULL) THEN 
			    NEW.top_elev = 0;
			END IF;
			IF (NEW.ymax IS NULL) THEN 
			    NEW.ymax = 0;
			END IF;
			
--			y0, ysur, apond
			IF (NEW.y0 IS NULL) THEN 
			    NEW.y0 = 0;
			END IF;
			IF (NEW.ysur IS NULL) THEN 
			    NEW.ysur = 0;
			END IF;
			IF (NEW.apond IS NULL) THEN 
			    NEW.apond = 0;
			END IF;

--			Sector ID
			IF (NEW.sector_id IS NULL) THEN
				IF ((SELECT COUNT(*) FROM sector) = 0) THEN
					RAISE EXCEPTION 'There are no sectors defined in the model, define at least one.';
				END IF;
				NEW.sector_id := (SELECT sector_id FROM sector LIMIT 1);
			END IF;

--		Trigger error				
		ELSE
			SELECT node_id INTO auxNode_ID FROM node nodeOld WHERE nodeOld.the_geom && ST_Expand(NEW.the_geom, 0.1) LIMIT 1;
			RAISE EXCEPTION 'Existing node closer than 0.1 m, node_id = (%)', node_ID;
		END IF;

		INSERT INTO node VALUES(NEW.node_id,NEW.top_elev,NEW.ymax,'JUNCTION'::TEXT,NEW.sector_id,NEW.the_geom);
		INSERT INTO inp_junction VALUES(NEW.node_id,NEW.y0,NEW.ysur,NEW.apond);
		RETURN NEW;
    
	ELSIF TG_OP = 'UPDATE' THEN
		UPDATE node SET node_id=NEW.node_id,top_elev=NEW.top_elev,ymax=NEW.ymax,sector_id=NEW.sector_id,the_geom=NEW.the_geom WHERE node_id=OLD.node_id;
		UPDATE inp_junction SET node_id=NEW.node_id,y0=NEW.y0,ysur=NEW.ysur,apond=NEW.apond WHERE node_id=OLD.node_id;
		RETURN NEW;
    
	ELSIF TG_OP = 'DELETE' THEN
		DELETE FROM node WHERE node_id=OLD.node_id;
		DELETE FROM inp_junction WHERE node_id=OLD.node_id;
	    RETURN NULL;
    
	END IF;
    RETURN NEW;
END;
$$;



-- ------------------------------------------------------------
-- Incorporation of new columns on inp_conduit table
-- ------------------------------------------------------------

ALTER TABLE "SCHEMA_NAME"."inp_conduit"
ADD COLUMN "seepage" numeric (12,4);

DROP VIEW "SCHEMA_NAME"."v_inp_losses";
CREATE VIEW "SCHEMA_NAME"."v_inp_losses" AS 
SELECT inp_conduit.arc_id, inp_conduit.kentry, inp_conduit.kexit, inp_conduit.kavg, inp_conduit.flap, inp_conduit.seepage, sector_selection.sector_id FROM ((SCHEMA_NAME.inp_conduit JOIN SCHEMA_NAME.arc ON (((inp_conduit.arc_id)::text = (arc.arc_id)::text))) JOIN SCHEMA_NAME.sector_selection ON (((arc.sector_id)::text = (sector_selection.sector_id)::text))) WHERE ((((inp_conduit.kentry > (0)::numeric) OR (inp_conduit.kexit > (0)::numeric)) OR (inp_conduit.kavg > (0)::numeric)) OR ((inp_conduit.flap)::text = 'YES'::text)) OR ((inp_conduit.seepage) > 0::numeric);

DROP VIEW "SCHEMA_NAME"."v_inp_edit_conduit";
CREATE VIEW "SCHEMA_NAME"."v_inp_edit_conduit" AS 
SELECT arc.arc_id, arc.z1, arc.z2, arc.arccat_id, arc.matcat_id, inp_conduit.barrels, inp_conduit.culvert, inp_conduit.kentry, inp_conduit.kexit, inp_conduit.kavg, inp_conduit.flap, inp_conduit.q0, inp_conduit.qmax, inp_conduit.seepage, arc.sector_id, arc.the_geom FROM (SCHEMA_NAME.arc JOIN SCHEMA_NAME.inp_conduit ON (((arc.arc_id)::text = (inp_conduit.arc_id)::text)));

CREATE OR REPLACE FUNCTION SCHEMA_NAME.update_v_inp_edit_conduit() RETURNS trigger LANGUAGE plpgsql AS $$

DECLARE 
	numNodes numeric;
	sectorRecord record;
	auxNode_ID varchar;

BEGIN

	EXECUTE 'SET search_path TO '||quote_literal(TG_TABLE_SCHEMA)||', public';
	
    IF TG_OP = 'INSERT' THEN
--			Arc ID
			IF (NEW.arc_id IS NULL) THEN
				NEW.arc_id := (SELECT nextval('inp_arc_id_seq'));
			END IF;
			
--			z1, z2
			IF (NEW.z1 IS NULL) THEN 
			    NEW.z1 = 0;
			END IF;
			IF (NEW.z2 IS NULL) THEN 
			    NEW.z2 = 0;
			END IF;
			
-- 			barrels
			IF (NEW.barrels IS NULL) THEN 
			    NEW.barrels = 1;
			END IF;
			
--			kentry, kexit, kavg		
			IF (NEW.kentry IS NULL) THEN 
			    NEW.kentry = 0;
			END IF;
			IF (NEW.kexit IS NULL) THEN 
			    NEW.kexit = 0;
			END IF;
			IF (NEW.kavg IS NULL) THEN 
			    NEW.kavg = 0;
			END IF;
			
--			q0, qmax
			IF (NEW.q0 IS NULL) THEN 
			    NEW.q0 = 0;
			END IF;
			IF (NEW.qmax IS NULL) THEN 
			    NEW.qmax = 0;
			END IF;

--			flap_gate
			IF (NEW.flap IS NULL) THEN 
			    NEW.flap = 'NO';
			END IF;

--			seepage
			IF (NEW.seepage IS NULL) THEN 
			    NEW.seepage = 0;
			END IF;
						
--			Sector ID
			IF (NEW.sector_id IS NULL) THEN
				IF ((SELECT COUNT(*) FROM sector) = 0) THEN
					RAISE EXCEPTION 'There are no sectors defined in the model, define at least one.';
				END IF;
				NEW.sector_id := (SELECT sector_id FROM sector LIMIT 1);
			END IF;

--			Arc catalog ID
			IF (NEW.arccat_id IS NULL) THEN
				IF ((SELECT COUNT(*) FROM cat_arc) = 0) THEN
					RAISE EXCEPTION 'There are no arc catalog defined in the model, define at least one.';
				END IF;
				NEW.arccat_id := (SELECT id FROM cat_arc LIMIT 1);
			END IF;
		
--			Material catalog ID
			IF (NEW.matcat_id IS NULL) THEN
				IF ((SELECT COUNT(*) FROM cat_mat) = 0) THEN
					RAISE EXCEPTION 'There are no materials catalog defined in the model, define at least one.';
				END IF;			
				NEW.matcat_id := (SELECT id FROM cat_mat LIMIT 1);
			END IF;
	
		INSERT INTO arc VALUES(NEW.arc_id,'', '', NEW.z1,NEW.z2,NEW.arccat_id,NEW.matcat_id,'CONDUIT'::TEXT,NEW.sector_id,NEW.the_geom);
		INSERT INTO inp_conduit VALUES(NEW.arc_id,NEW.barrels,NEW.culvert,NEW.kentry,NEW.kexit,NEW.kavg,NEW.flap,NEW.q0,NEW.qmax, NEW.seepage);
		RETURN NEW;
	
    ELSIF TG_OP = 'UPDATE' THEN
		UPDATE arc SET arc_id=NEW.arc_id,z1=NEW.z1,z2=NEW.z2,arccat_id=NEW.arccat_id,matcat_id=NEW.matcat_id,sector_id=NEW.sector_id,the_geom=NEW.the_geom WHERE arc_id=OLD.arc_id;
		UPDATE inp_conduit SET arc_id=NEW.arc_id,barrels=NEW.barrels,culvert=NEW.culvert,kentry=NEW.kentry,kexit=NEW.kexit,kavg=NEW.kavg,flap=NEW.flap,q0=NEW.q0,qmax=NEW.qmax, seepage=NEW.seepage WHERE arc_id=OLD.arc_id;
		RETURN NEW;
	  
    ELSIF TG_OP = 'DELETE' THEN
		DELETE FROM arc WHERE arc_id=OLD.arc_id;
		DELETE FROM inp_conduit WHERE arc_id=OLD.arc_id;
		RETURN NULL;
    
	END IF;
    RETURN NEW;
END;
$$;

CREATE TRIGGER "update_v_inp_edit_conduit" INSTEAD OF INSERT OR UPDATE OR DELETE ON "SCHEMA_NAME"."v_inp_edit_conduit"
FOR EACH ROW
EXECUTE PROCEDURE "SCHEMA_NAME"."update_v_inp_edit_conduit"();




-- ------------------------------------------------------------
-- Incorporation of new columns on inp_weir
-- ------------------------------------------------------------

ALTER TABLE "SCHEMA_NAME"."inp_weir"
ADD COLUMN "surcharge" varchar (3);

DROP VIEW "SCHEMA_NAME"."v_inp_weir";
CREATE VIEW "SCHEMA_NAME"."v_inp_weir" AS 
SELECT arc.arc_id, v_inp_arc_x_node.node_1, v_inp_arc_x_node.node_2, inp_weir.weir_type, inp_weir."offset", inp_weir.cd, inp_weir.flap, inp_weir.ec, inp_weir.cd2, inp_value_weirs.shape, inp_weir.geom1, inp_weir.geom2, inp_weir.geom3, inp_weir.geom4, inp_weir.surcharge, sector_selection.sector_id FROM ((((SCHEMA_NAME.arc JOIN SCHEMA_NAME.sector_selection ON (((arc.sector_id)::text = (sector_selection.sector_id)::text))) JOIN SCHEMA_NAME.inp_weir ON (((inp_weir.arc_id)::text = (arc.arc_id)::text))) JOIN SCHEMA_NAME.inp_value_weirs ON (((inp_weir.weir_type)::text = (inp_value_weirs.id)::text))) JOIN SCHEMA_NAME.v_inp_arc_x_node ON (((v_inp_arc_x_node.arc_id)::text = (arc.arc_id)::text)));

DROP VIEW "SCHEMA_NAME"."v_inp_edit_weir";
CREATE VIEW "SCHEMA_NAME"."v_inp_edit_weir" AS 
SELECT arc.arc_id, arc.z1, arc.z2, inp_weir.weir_type, inp_weir."offset", inp_weir.cd, inp_weir.ec, inp_weir.cd2, inp_weir.flap, inp_weir.geom1, inp_weir.geom2, inp_weir.geom3, inp_weir.geom4, inp_weir.surcharge, arc.sector_id, arc.the_geom FROM (SCHEMA_NAME.arc JOIN SCHEMA_NAME.inp_weir ON (((arc.arc_id)::text = (inp_weir.arc_id)::text)));

CREATE OR REPLACE FUNCTION SCHEMA_NAME.update_v_inp_edit_weir() RETURNS trigger LANGUAGE plpgsql AS $$

DECLARE 
	numNodes numeric;
	sectorRecord record;
	auxNode_ID varchar;

BEGIN

	EXECUTE 'SET search_path TO '||quote_literal(TG_TABLE_SCHEMA)||', public';
	
    IF TG_OP = 'INSERT' THEN
--			Arc ID
			IF (NEW.arc_id IS NULL) THEN
				NEW.arc_id := (SELECT nextval('inp_arc_id_seq'));
			END IF;

--			z1, z2
			IF (NEW.z1 IS NULL) THEN 
			    NEW.z1 = 0;
			END IF;
			IF (NEW.z2 IS NULL) THEN 
			    NEW.z2 = 0;
			END IF;
		
--			Sector ID
			IF (NEW.sector_id IS NULL) THEN
				IF ((SELECT COUNT(*) FROM sector) = 0) THEN
					RAISE EXCEPTION 'There are no sectors defined in the model, define at least one.';
				END IF;
				NEW.sector_id := (SELECT sector_id FROM sector LIMIT 1);
			END IF;
    
	   INSERT INTO arc VALUES(NEW.arc_id,'','',NEW.z1,NEW.z2,DEFAULT,DEFAULT,'WEIR'::TEXT,NEW.sector_id,NEW.the_geom);
	   INSERT INTO inp_weir VALUES(NEW.arc_id,NEW.weir_type,NEW."offset",NEW.cd,NEW.ec,NEW.cd2,NEW.flap,NEW.geom1,NEW.geom2,NEW.geom3,NEW.geom4);
	   RETURN NEW;
    
	ELSIF TG_OP = 'UPDATE' THEN
       UPDATE arc SET arc_id=NEW.arc_id,z1=NEW.z1,z2=NEW.z2,sector_id=NEW.sector_id,the_geom=NEW.the_geom WHERE arc_id=OLD.arc_id;
	   UPDATE inp_weir SET arc_id=NEW.arc_id,weir_type=NEW.weir_type,"offset"=NEW."offset",cd=NEW.cd,ec=NEW.ec,cd2=NEW.cd2,flap=NEW.flap,geom1=NEW.geom1,geom2=NEW.geom2,geom3=NEW.geom3,geom4=NEW.geom4 WHERE arc_id=OLD.arc_id;
       RETURN NEW;
    
	ELSIF TG_OP = 'DELETE' THEN
       DELETE FROM arc WHERE arc_id=OLD.arc_id;
	   DELETE FROM inp_weir WHERE arc_id=OLD.arc_id;
	   RETURN NULL;
    
	END IF;
    RETURN NEW;
END;
$$;


CREATE TRIGGER "update_v_inp_edit_weir" INSTEAD OF INSERT OR UPDATE OR DELETE ON "SCHEMA_NAME"."v_inp_edit_weir"
FOR EACH ROW
EXECUTE PROCEDURE "SCHEMA_NAME"."update_v_inp_edit_weir"();


-- ------------------------------------------------------------
-- Incorporation of 1 new columns on inp_aquifer
-- ------------------------------------------------------------

ALTER TABLE "SCHEMA_NAME"."inp_aquifer"
ADD COLUMN "pattern_id" varchar (16);


-- ------------------------------------------------------------
-- Incorporation of 1 new columns on inp_groundwater
-- ------------------------------------------------------------

ALTER TABLE "SCHEMA_NAME"."inp_groundwater"
ADD COLUMN "fl_eq_lat" varchar (50);

ALTER TABLE "SCHEMA_NAME"."inp_groundwater"
ADD COLUMN "fl_eq_deep" varchar (50);

DROP VIEW "SCHEMA_NAME"."v_inp_groundwater";
CREATE VIEW "SCHEMA_NAME"."v_inp_groundwater" AS 
SELECT inp_groundwater.subc_id, inp_groundwater.aquif_id, inp_groundwater.node_id, inp_groundwater.surfel, inp_groundwater.a1, inp_groundwater.b1, inp_groundwater.a2, inp_groundwater.b2, inp_groundwater.a3, inp_groundwater.tw, inp_groundwater.h, (('LATERAL'::text || ' '::text) || (inp_groundwater.fl_eq_lat)::text) AS fl_eq_lat, (('DEEP'::text || ' '::text) || (inp_groundwater.fl_eq_lat)::text) AS fl_eq_deep, sector_selection.sector_id FROM ((SCHEMA_NAME.subcatchment JOIN SCHEMA_NAME.inp_groundwater ON (((inp_groundwater.subc_id)::text = (subcatchment.subc_id)::text))) JOIN SCHEMA_NAME.sector_selection ON (((subcatchment.sector_id)::text = (sector_selection.sector_id)::text)));


-- ------------------------------------------------------------
-- Incorporation of adjustments table
-- ------------------------------------------------------------

CREATE TABLE "SCHEMA_NAME"."inp_adjustments" (
"adj_type" varchar(16) COLLATE "default" NOT NULL,
"value_1" numeric(12,4),
"value_2" numeric(12,4),
"value_3" numeric(12,4),
"value_4" numeric(12,4),
"value_5" numeric(12,4),
"value_6" numeric(12,4),
"value_7" numeric(12,4),
"value_8" numeric(12,4),
"value_9" numeric(12,4),
"value_10" numeric(12,4),
"value_11" numeric(12,4),
"value_12" numeric(12,4))
WITH (OIDS=FALSE);

ALTER TABLE "SCHEMA_NAME"."inp_adjustments" ADD PRIMARY KEY ("adj_type");