/*
This file is part of Giswater
The program is free software: you can redistribute it and/or modify it under the terms of the GNU General Public License as published by the Free Software Foundation, either version 3 of the License, or (at your option) any later version.
This version of Giswater is provided by Giswater Association
*/

--------------------------------------------------------------------------------------------------
-- The purpose of this file is to be a storage of scripts in order to allow the possibility of update schemas created in older version from this version to newest version
-- Changes incorporated in version 1.1.226 - 23/09/2015 
--------------------------------------------------------------------------------------------------

--------------------------------------------------------------------------------------------------
-- Bug fix in update_v_inp_edit_orifice() function
--------------------------------------------------------------------------------------------------

CREATE OR REPLACE FUNCTION SCHEMA_NAME.update_v_inp_edit_orifice() RETURNS trigger LANGUAGE plpgsql AS $$

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
			
--			geom2, geom3, geom4
			IF (NEW.geom2 IS NULL) THEN 
			    NEW.geom2 = 0;
			END IF;
		    NEW.geom3 = 0;
			NEW.geom4 = 0;
					
--			Sector ID
			IF (NEW.sector_id IS NULL) THEN
				IF ((SELECT COUNT(*) FROM sector) = 0) THEN
					RAISE EXCEPTION 'There are no sectors defined in the model, define at least one.';
				END IF;
				NEW.sector_id := (SELECT sector_id FROM sector LIMIT 1);
			END IF;
			
		INSERT INTO arc VALUES(NEW.arc_id,'','',NEW.z1,NEW.z2,DEFAULT,DEFAULT,'ORIFICE'::TEXT,NEW.sector_id,NEW.the_geom);
		INSERT INTO inp_orifice VALUES(NEW.arc_id,NEW.ori_type,NEW.offset,NEW.cd,NEW.orate,NEW.flap,NEW.shape,NEW.geom1,NEW.geom2,NEW.geom3,NEW.geom4);
		RETURN NEW;
    
	ELSIF TG_OP = 'UPDATE' THEN
		UPDATE arc SET arc_id=NEW.arc_id,z1=NEW.z1,z2=NEW.z2,sector_id=NEW.sector_id,the_geom=NEW.the_geom WHERE arc_id=OLD.arc_id;
		UPDATE inp_orifice SET arc_id=NEW.arc_id,ori_type=NEW.ori_type,"offset"=NEW."offset",cd=NEW.cd,orate=NEW.orate,flap=NEW.flap,shape=NEW.shape,geom1=NEW.geom1,geom2=NEW.geom2,geom3=NEW.geom3,geom4=NEW.geom4 WHERE arc_id=OLD.arc_id;
		RETURN NEW;
    
	ELSIF TG_OP = 'DELETE' THEN
		DELETE FROM arc WHERE arc_id=OLD.arc_id;
		DELETE FROM inp_orifice WHERE arc_id=OLD.arc_id;
	    RETURN NULL;
    
	END IF;
    RETURN NEW;
END;
$$;