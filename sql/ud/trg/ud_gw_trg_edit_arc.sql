﻿/*
This file is part of Giswater 3
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
	v_sql2 varchar;
	man_table_2 varchar;
	arc_id_seq int8;
	expl_id_int integer;

BEGIN

    EXECUTE 'SET search_path TO '||quote_literal(TG_TABLE_SCHEMA)||', public';
    
    IF TG_OP = 'INSERT' THEN
      
			-- Arc ID
			IF (NEW.arc_id IS NULL) THEN
				--PERFORM setval('urn_id_seq', gw_fct_urn(),true);
				NEW.arc_id:= (SELECT nextval('urn_id_seq'));
			END IF;

			-- code
			IF (NEW.code IS NULL) THEN
				NEW.code = NEW.arc_id;
			END IF;

         -- Arc type
        IF (NEW.arc_type IS NULL) THEN
            IF ((SELECT COUNT(*) FROM arc_type) = 0) THEN
                RETURN audit_function(140,760);  
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
					RETURN audit_function(145,840); 
				END IF; 
				NEW.arccat_id:= (SELECT "value" FROM config_param_user WHERE "parameter"='arccat_vdefault' AND "cur_user"="current_user"());
				IF (NEW.arccat_id IS NULL) THEN
					NEW.arccat_id := (SELECT arccat_id from arc WHERE ST_DWithin(NEW.the_geom, arc.the_geom,0.001) LIMIT 1);
				END IF;
				IF (NEW.arccat_id IS NULL) THEN
						NEW.arccat_id := (SELECT id FROM cat_arc LIMIT 1);
				END IF;       
			END IF;
        
        -- Sector ID
        IF (NEW.sector_id IS NULL) THEN
            IF ((SELECT COUNT(*) FROM sector) = 0) THEN
                RETURN audit_function(115,760); 
            END IF;
            NEW.sector_id := (SELECT sector_id FROM sector WHERE ST_DWithin(NEW.the_geom, sector.the_geom,0.001) LIMIT 1);
            IF (NEW.sector_id IS NULL) THEN
                RETURN audit_function(120,760); 
            END IF;
        END IF;
        
        -- Dma ID
        IF (NEW.dma_id IS NULL) THEN
            IF ((SELECT COUNT(*) FROM dma) = 0) THEN
                RETURN audit_function(125,760); 
            END IF;
            NEW.dma_id := (SELECT dma_id FROM dma WHERE ST_DWithin(NEW.the_geom, dma.the_geom,0.001) LIMIT 1);
            IF (NEW.dma_id IS NULL) THEN
                RETURN audit_function(130,760); 
            END IF;
        END IF;
		
	-- Verified
        IF (NEW.verified IS NULL) THEN
            NEW.verified := (SELECT "value" FROM config_param_user WHERE "parameter"='verified_vdefault' AND "cur_user"="current_user"());
            IF (NEW.verified IS NULL) THEN
                NEW.verified := (SELECT id FROM value_verified limit 1);
            END IF;
        END IF;

		-- State
        IF (NEW.state IS NULL) THEN
            NEW.state := (SELECT "value" FROM config_param_user WHERE "parameter"='state_vdefault' AND "cur_user"="current_user"());
            IF (NEW.state IS NULL) THEN
                NEW.state := (SELECT id FROM value_state limit 1);
            END IF;
        END IF;
		
		-- Workcat_id
        IF (NEW.workcat_id IS NULL) THEN
            NEW.workcat_id := (SELECT "value" FROM config_param_user WHERE "parameter"='workcat_vdefault' AND "cur_user"="current_user"());
            IF (NEW.workcat_id IS NULL) THEN
                NEW.workcat_id := (SELECT id FROM cat_work limit 1);
            END IF;
        END IF;
		

		--Builtdate
		IF (NEW.builtdate IS NULL) THEN
			NEW.builtdate :=(SELECT "value" FROM config_param_user WHERE "parameter"='builtdate_vdefault' AND "cur_user"="current_user"());
		END IF;    

		--Inventory
		IF (NEW.inventory IS NULL) THEN
			NEW.inventory :='TRUE';
		END IF; 
		
	--Exploitation ID
            IF ((SELECT COUNT(*) FROM exploitation) = 0) THEN
                --PERFORM audit_function(125,340);
				RETURN NULL;				
            END IF;
            expl_id_int := (SELECT expl_id FROM exploitation WHERE ST_DWithin(NEW.the_geom, exploitation.the_geom,0.001) LIMIT 1);
            IF (expl_id_int IS NULL) THEN
                --PERFORM audit_function(130,340);
				RETURN NULL; 
            END IF;
		
        -- FEATURE INSERT
				INSERT INTO arc (arc_id, code, node_1, node_2, y1, custom_y1, elev1, custom_elev1, y2, custom_y2, elev2, custom_elev2, arc_type, arccat_id, epa_type,
				sector_id, "state", annotation, observ, "comment", inverted_slope, custom_length, dma_id, soilcat_id, function_type,
				category_type, fluid_type, location_type, workcat_id, workcat_id_end, buildercat_id, builtdate, enddate, ownercat_id, 
				address_01, address_02, address_03, descript, link, verified, the_geom, undelete,label_x,label_y, 
				label_rotation, expl_id, publish, inventory, uncertain, num_value) 
				VALUES (NEW.arc_id, NEW.code, null, null, NEW.y1, NEW.custom_y1, NEW.elev1, NEW.custom_elev1, NEW.y2, NEW.custom_y2, NEW.elev2, NEW.custom_elev2, NEW.arc_type, NEW.arccat_id, NEW.epa_type, 
				NEW.sector_id, NEW.state, NEW.annotation, NEW.observ, NEW.comment, NEW.inverted_slope, NEW.custom_length, NEW.dma_id, NEW.soilcat_id, NEW.function_type, 
				NEW.category_type, NEW.fluid_type, NEW.location_type, NEW.workcat_id, NEW.workcat_id_end, NEW.buildercat_id, NEW.builtdate, NEW.enddate, NEW.ownercat_id, 
				NEW.address_01, NEW.address_02, NEW.address_03, NEW.descript, NEW.link, NEW.verified, NEW.the_geom,NEW.undelete,NEW.label_x,NEW.label_y, 
				NEW.label_rotation, expl_id_int, NEW.publish, NEW.inventory, NEW.uncertain, NEW.num_value);
				
						
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

        IF (NEW.epa_type != OLD.epa_type) THEN    
         
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
			inp_table := NULL;

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
		SET arc_id=NEW.arc_id, code=NEW.code, y1=NEW.y1, custom_y1=NEW.custom_y1, elev1=NEW.elev1, custom_elev1=NEW.custom_elev1, y2=NEW.y2, custom_y2=NEW.custom_y2, elev2=NEW.elev2, custom_elev2=NEW.custom_elev2,
		arc_type=NEW.arc_type, arccat_id=NEW.arccat_id, epa_type=NEW.epa_type, sector_id=NEW.sector_id, "state"=NEW.state, 
		annotation= NEW.annotation, "observ"=NEW.observ,"comment"=NEW.comment, inverted_slope=NEW.inverted_slope, custom_length=NEW.custom_length, dma_id=NEW.dma_id, 
		soilcat_id=NEW.soilcat_id, function_type=NEW.function_type, category_type=NEW.category_type, fluid_type=NEW.fluid_type,location_type=NEW.location_type, workcat_id=NEW.workcat_id, workcat_id_end=NEW.workcat_id_end,
		buildercat_id=NEW.buildercat_id, builtdate=NEW.builtdate, enddate=NEW.enddate, ownercat_id=NEW.ownercat_id, address_01=NEW.address_01, address_02=NEW.address_02, 
		address_03=NEW.address_03, descript=NEW.descript, link=NEW.link, verified=NEW.verified, the_geom=NEW.the_geom, undelete=NEW.undelete,label_x=NEW.label_x,label_y=NEW.label_y, label_rotation=NEW.label_rotation,
		publish=NEW.publish, inventory=NEW.inventory, uncertain=NEW.uncertain, expl_id=NEW.expl_id, num_value=NEW.num_value
		WHERE arc_id=OLD.arc_id;	

		--PERFORM audit_function (2,760);
        RETURN NEW;

     ELSIF TG_OP = 'DELETE' THEN
        DELETE FROM arc WHERE arc_id = OLD.arc_id;

		--PERFORM audit_function (3,760);
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

      