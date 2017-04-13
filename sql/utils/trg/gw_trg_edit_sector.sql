-- DROP FUNCTION "SCHEMA_NAME".gw_trg_edit_man_arc();

CREATE OR REPLACE FUNCTION "SCHEMA_NAME".gw_trg_edit_area()
  RETURNS trigger AS
$BODY$
DECLARE 



BEGIN

    EXECUTE 'SET search_path TO '||quote_literal(TG_TABLE_SCHEMA)||', public';

	
    IF TG_OP = 'INSERT' THEN
    	--Exploitation ID
	  	IF (NEW.expl_id IS NULL) THEN
			IF ((SELECT COUNT(*) FROM exploitation) = 0) THEN
				RETURN audit_function(125,430);
			END IF;
			NEW.expl_id := (SELECT expl_id FROM exploitation WHERE ST_DWithin(NEW.the_geom, exploitation.the_geom,0.001) LIMIT 1);
			IF (NEW.expl_id IS NULL) THEN
				RETURN audit_function(130,430);  
			END IF;            
		END IF;
			
        -- FEATURE INSERT
		INSERT INTO sector (sector_id, descript, the_geom, undelete, expl_id)
		VALUES (NEW.sector_id, NEW.descript, NEW.the_geom, NEW.undelete, NEW.expl_id);
		
		RETURN NEW;
		

    ELSIF TG_OP = 'UPDATE' THEN
   -- FEATURE UPDATE		
		UPDATE sector
		SET sector_id=NEW.sector_id, descript=NEW.descript, the_geom=NEW.the_geom, undelete=NEW.undelete, expl_id=NEW.expl_id
		WHERE sector_id=OLD.sector_id;
				
        PERFORM audit_function(2,340); 
        RETURN NEW;


     ELSIF TG_OP = 'DELETE' THEN  
	-- FEATURE DELETE 
		DELETE FROM sector WHERE sector_id = OLD.sector_id;
		
				
		
		PERFORM audit_function(3,340); 		
		RETURN NULL;
     
     END IF;

END;
$BODY$
  LANGUAGE plpgsql VOLATILE
  COST 100;
  
  
  


DROP TRIGGER IF EXISTS gw_trg_edit_sector ON "SCHEMA_NAME".v_edit_sector;
CREATE TRIGGER gw_trg_edit_sector INSTEAD OF INSERT OR DELETE OR UPDATE ON "SCHEMA_NAME".v_edit_sector FOR EACH ROW EXECUTE PROCEDURE "SCHEMA_NAME".gw_trg_edit_area('sector');




-- DROP FUNCTION "SCHEMA_NAME".gw_trg_edit_man_arc();

CREATE OR REPLACE FUNCTION "SCHEMA_NAME".gw_trg_edit_area()
  RETURNS trigger AS
$BODY$
DECLARE 



BEGIN

    EXECUTE 'SET search_path TO '||quote_literal(TG_TABLE_SCHEMA)||', public';

	
    IF TG_OP = 'INSERT' THEN
    	--Exploitation ID
	  	IF (NEW.expl_id IS NULL) THEN
			IF ((SELECT COUNT(*) FROM exploitation) = 0) THEN
				RETURN audit_function(125,430);
			END IF;
			NEW.expl_id := (SELECT expl_id FROM exploitation WHERE ST_DWithin(NEW.the_geom, exploitation.the_geom,0.001) LIMIT 1);
			IF (NEW.expl_id IS NULL) THEN
				RETURN audit_function(130,430);  
			END IF;            
		END IF;
			
        -- FEATURE INSERT
		INSERT INTO sector (sector_id, descript, the_geom, undelete, expl_id)
		VALUES (NEW.sector_id, NEW.descript, NEW.the_geom, NEW.undelete, NEW.expl_id);
		
		RETURN NEW;
		

    ELSIF TG_OP = 'UPDATE' THEN
   -- FEATURE UPDATE		
		UPDATE sector
		SET sector_id=NEW.sector_id, descript=NEW.descript, the_geom=NEW.the_geom, undelete=NEW.undelete, expl_id=NEW.expl_id
		WHERE sector_id=OLD.sector_id;
				
        PERFORM audit_function(2,340); 
        RETURN NEW;


     ELSIF TG_OP = 'DELETE' THEN  
	-- FEATURE DELETE 
		DELETE FROM sector WHERE sector_id = OLD.sector_id;
		
				
		
		PERFORM audit_function(3,340); 		
		RETURN NULL;
     
     END IF;

END;
$BODY$
  LANGUAGE plpgsql VOLATILE
  COST 100;
  
  
  


DROP TRIGGER IF EXISTS gw_trg_edit_sector ON "SCHEMA_NAME".v_edit_sector;
CREATE TRIGGER gw_trg_edit_sector INSTEAD OF INSERT OR DELETE OR UPDATE ON "SCHEMA_NAME".v_edit_sector FOR EACH ROW EXECUTE PROCEDURE "SCHEMA_NAME".gw_trg_edit_area('sector');




