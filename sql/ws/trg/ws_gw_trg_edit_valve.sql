/*
This file is part of Giswater 2.0
The program is free software: you can redistribute it and/or modify it under the terms of the GNU General Public License as published by the Free Software Foundation, either version 3 of the License, or (at your option) any later version.
This version of Giswater is provided by Giswater Association
*/


CREATE OR REPLACE FUNCTION "SCHEMA_NAME".gw_trg_edit_anl_valve() RETURNS trigger LANGUAGE plpgsql AS $$
DECLARE 
    v_sql varchar;

BEGIN

    EXECUTE 'SET search_path TO '||quote_literal(TG_TABLE_SCHEMA)||', public';
    
    -- Control insertions ID
    IF TG_OP = 'INSERT' THEN
        PERFORM audit_function(165,390); 
        RETURN NULL;

    ELSIF TG_OP = 'UPDATE' THEN

        -- UPDATE position 
        IF  (NEW.the_geom IS DISTINCT FROM OLD.the_geom) OR 
            (NEW.node_id IS DISTINCT FROM OLD.node_id) OR 
            (NEW.nodetype_id IS DISTINCT FROM OLD.nodetype_id) OR
            (NEW.type IS DISTINCT FROM OLD.type) THEN    
            PERFORM audit_function(170,390);  
            RETURN NULL;
            
        END IF;

        UPDATE man_valve 
        SET opened=NEW.opened, acessibility=NEW.acessibility, "broken"=NEW."broken", "mincut_anl"=NEW."mincut_anl", "hydraulic_anl"=NEW."hydraulic_anl"
        WHERE node_id = OLD.node_id;

      --  PERFORM audit_function(2,390);  
        RETURN NEW;

    ELSIF TG_OP = 'DELETE' THEN
        PERFORM audit_function(175,390); 
        RETURN NULL;
   
    END IF;

END;
$$;


DROP TRIGGER IF EXISTS gw_trg_edit_anl_valve ON "SCHEMA_NAME".v_edit_anl_valve;
CREATE TRIGGER gw_trg_edit_anl_valve INSTEAD OF INSERT OR DELETE OR UPDATE ON "SCHEMA_NAME".v_edit_anl_valve FOR EACH ROW EXECUTE PROCEDURE "SCHEMA_NAME".gw_trg_edit_anl_valve();

