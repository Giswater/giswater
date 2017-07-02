/*
This file is part of Giswater 2.0
The program is free software: you can redistribute it and/or modify it under the terms of the GNU General Public License as published by the Free Software Foundation, either version 3 of the License, or (at your option) any later version.
This version of Giswater is provided by Giswater Association
*/




CREATE OR REPLACE FUNCTION SCHEMA_NAME.gw_trg_edit_arc() RETURNS trigger AS
$BODY$
DECLARE 
    inp_table varchar;
    man_table varchar;
    new_man_table varchar;
    old_man_table varchar;
    v_sql varchar;

BEGIN

    EXECUTE 'SET search_path TO '||quote_literal(TG_TABLE_SCHEMA)||', public';
    
    IF TG_OP = 'INSERT' THEN
      
		-- Arc ID
        IF (NEW.arc_id IS NULL) THEN
            NEW.arc_id:= (SELECT nextval('arc_id_seq'));
        END IF;

         -- Arc type
        IF (NEW.arc_type IS NULL) THEN
            IF ((SELECT COUNT(*) FROM arc_type) = 0) THEN
                PERFORM audit_function(140,760);  
            END IF;
            NEW.arc_type:= (SELECT id FROM arc_type LIMIT 1);     
        END IF;

         -- Epa type
        IF (NEW.epa_type IS NULL) THEN
			NEW.epa_type:= (SELECT epa_default FROM arc_type WHERE arc_type.id=NEW.arc_type)::text;   
		END IF;
        
        -- Arc catalog ID
        IF (NEW.arccat_id IS NULL) THEN
            IF ((SELECT COUNT(*) FROM cat_arc) = 0) THEN
                PERFORM audit_function(145,760); 
            END IF; 
        END IF;
        
        -- Sector ID
        IF (NEW.sector_id IS NULL) THEN
            IF ((SELECT COUNT(*) FROM sector) = 0) THEN
                PERFORM audit_function(115,760); 
            END IF;
            NEW.sector_id := (SELECT sector_id FROM sector WHERE ST_DWithin(NEW.the_geom, sector.the_geom,0.001) LIMIT 1);
            IF (NEW.sector_id IS NULL) THEN
                PERFORM audit_function(120,760); 
            END IF;
        END IF;
        
        -- Dma ID
        IF (NEW.dma_id IS NULL) THEN
            IF ((SELECT COUNT(*) FROM dma) = 0) THEN
                PERFORM audit_function(125,760); 
            END IF;
            NEW.dma_id := (SELECT dma_id FROM dma WHERE ST_DWithin(NEW.the_geom, dma.the_geom,0.001) LIMIT 1);
            IF (NEW.dma_id IS NULL) THEN
                PERFORM audit_function(130,760); 
            END IF;
        END IF;
    
        -- FEATURE INSERT
		INSERT INTO arc (arc_id, node_1, node_2, y1, y2, arc_type, arccat_id, epa_type, sector_id, "state", annotation, observ, "comment", inverted_slope, custom_length, dma_id, soilcat_id, category_type, fluid_type, 
                        location_type, workcat_id, buildercat_id, builtdate, ownercat_id, adress_01, adress_02, adress_03, descript, est_y1, est_y2, rotation, link, verified, the_geom,workcat_id_end,undelete,label_x,
						label_y, label_rotation) 
                VALUES (NEW.arc_id, null, null, NEW.y1, NEW.y2, NEW.arc_type, NEW.arccat_id, NEW.epa_type, NEW.sector_id, NEW."state", NEW.annotation, NEW."observ", NEW."comment", NEW.inverted_slope, NEW.custom_length, 
				NEW.dma_id, NEW.soilcat_id, NEW.category_type, NEW.fluid_type, NEW.location_type, NEW.workcat_id, NEW.buildercat_id, NEW.builtdate, NEW.ownercat_id, NEW.adress_01, NEW.adress_02, NEW.adress_03, NEW.descript,
				NEW.est_y1,NEW.est_y2, NEW.rotation, NEW.link, NEW.verified, NEW.the_geom,NEW.workcat_id_end,NEW.undelete,NEW.label_x,NEW.label_y, NEW.label_rotation);
				
						
        -- EPA INSERT
        IF (NEW.epa_type = 'CONDUIT') THEN 
            inp_table:= 'inp_conduit';
        ELSIF (NEW.epa_type = 'PUMP') THEN 
            inp_table:= 'inp_pump';
		ELSIF (NEW.epa_type = 'ORIFICE') THEN 
			inp_table:= 'inp_orifice';
		ELSIF (NEW.epa_type = 'WEIR') THEN 
            inp_table:= 'inp_weir';
		ELSIF (NEW.epa_type = 'OUTLET') THEN 
            inp_table:= 'inp_outlet';
        END IF;
        v_sql:= 'INSERT INTO '||inp_table||' (arc_id) VALUES ('||quote_literal(NEW.arc_id)||')';
        EXECUTE v_sql;
        
        -- MAN INSERT      
        man_table := (SELECT arc_type.man_table FROM arc_type WHERE arc_type.id=NEW.arc_type);
        v_sql:= 'INSERT INTO '||man_table||' (arc_id) VALUES ('||quote_literal(NEW.arc_id)||')';    
        EXECUTE v_sql;
        
		--PERFORM audit_function (1,760);
        RETURN NEW;
    
    ELSIF TG_OP = 'UPDATE' THEN

        IF (NEW.epa_type <> OLD.epa_type) THEN    
         
            IF (OLD.epa_type = 'CONDUIT') THEN 
            inp_table:= 'inp_conduit';
			ELSIF (OLD.epa_type = 'PUMP') THEN 
            inp_table:= 'inp_pump';
			ELSIF (OLD.epa_type = 'ORIFICE') THEN 
			inp_table:= 'inp_orifice';
			ELSIF (OLD.epa_type = 'WEIR') THEN 
            inp_table:= 'inp_weir';
			ELSIF (OLD.epa_type = 'OUTLET') THEN 
            inp_table:= 'inp_outlet';
			END IF;
            v_sql:= 'DELETE FROM '||inp_table||' WHERE arc_id = '||quote_literal(OLD.arc_id);
            EXECUTE v_sql;

			IF (NEW.epa_type = 'CONDUIT') THEN 
            inp_table:= 'inp_conduit';
			ELSIF (NEW.epa_type = 'PUMP') THEN 
			inp_table:= 'inp_pump';
			ELSIF (NEW.epa_type = 'ORIFICE') THEN 
			inp_table:= 'inp_orifice';
			ELSIF (NEW.epa_type = 'WEIR') THEN 
            inp_table:= 'inp_weir';
			ELSIF (NEW.epa_type = 'OUTLET') THEN 
            inp_table:= 'inp_outlet';
			END IF;
            v_sql:= 'INSERT INTO '||inp_table||' (arc_id) VALUES ('||quote_literal(NEW.arc_id)||')';
            EXECUTE v_sql;

        END IF;

     -- UPDATE management values
	IF (NEW.arc_type <> OLD.arc_type) THEN 
		new_man_table:= (SELECT arc_type.man_table FROM arc_type WHERE arc_type.id = NEW.arc_type);
		old_man_table:= (SELECT arc_type.man_table FROM arc_type WHERE arc_type.id = OLD.arc_type);
		IF new_man_table IS NOT NULL THEN
			v_sql:= 'DELETE FROM '||old_man_table||' WHERE arc_id= '||quote_literal(OLD.arc_id);
			EXECUTE v_sql;
			v_sql:= 'INSERT INTO '||new_man_table||' (arc_id) VALUES ('||quote_literal(NEW.arc_id)||')';
			EXECUTE v_sql;
		END IF;
	END IF;
    
        UPDATE arc 
        SET arc_id=NEW.arc_id, y1=NEW.y1, y2=NEW.y2, arc_type=NEW.arc_type, arccat_id=NEW.arccat_id, epa_type=NEW.epa_type, sector_id=NEW.sector_id, "state"=NEW."state", annotation= NEW.annotation, "observ"=NEW."observ", 
            "comment"=NEW."comment", inverted_slope=NEW.inverted_slope, custom_length=NEW.custom_length, dma_id=NEW.dma_id, soilcat_id=NEW.soilcat_id, category_type=NEW.category_type, fluid_type=NEW.fluid_type, 
            location_type=NEW.location_type, workcat_id=NEW.workcat_id, buildercat_id=NEW.buildercat_id, builtdate=NEW.builtdate, ownercat_id=NEW.ownercat_id, adress_01=NEW.adress_01, adress_02=NEW.adress_02, 
			adress_03=NEW.adress_03, descript=NEW.descript,rotation=NEW.rotation, link=NEW.link, est_y1=NEW.est_y1, est_y2=NEW.est_y2, verified=NEW.verified, the_geom=NEW.the_geom, undelete=NEW.undelete,
			label_x=NEW.label_x,label_y=NEW.label_y, label_rotation=NEW.label_rotation,
            workcat_id_end=NEW.workcat_id_end
        WHERE arc_id=OLD.arc_id;

	--	PERFORM audit_function (2,760);
        RETURN NEW;

     ELSIF TG_OP = 'DELETE' THEN
        DELETE FROM arc WHERE arc_id = OLD.arc_id;

	--	PERFORM audit_function (3,760);
        RETURN NULL;
     
     END IF;

END;
$BODY$
  LANGUAGE plpgsql VOLATILE
  COST 100;



DROP TRIGGER IF EXISTS gw_trg_edit_arc ON "SCHEMA_NAME".v_edit_arc;
CREATE TRIGGER gw_trg_edit_arc INSTEAD OF INSERT OR DELETE OR UPDATE ON "SCHEMA_NAME".v_edit_arc
FOR EACH ROW EXECUTE PROCEDURE "SCHEMA_NAME".gw_trg_edit_arc();

/*
DROP TRIGGER IF EXISTS gw_trg_edit_plan_arc ON "SCHEMA_NAME".v_edit_plan_arc;
CREATE TRIGGER gw_trg_edit_plan_arc INSTEAD OF INSERT OR DELETE OR UPDATE ON "SCHEMA_NAME".v_edit_plan_arc
FOR EACH ROW EXECUTE PROCEDURE "SCHEMA_NAME".gw_trg_edit_arc();
*/

      