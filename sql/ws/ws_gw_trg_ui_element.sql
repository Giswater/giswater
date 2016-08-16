/*
This file is part of Giswater 2.0
The program is free software: you can redistribute it and/or modify it under the terms of the GNU General Public License as published by the Free Software Foundation, either version 3 of the License, or (at your option) any later version.
This version of Giswater is provided by Giswater Association
*/


CREATE OR REPLACE FUNCTION SCHEMA_NAME.gw_trg_ui_element()
  RETURNS trigger AS
$BODY$
DECLARE 
    element_table varchar;
    v_sql varchar;
    
BEGIN

    EXECUTE 'SET search_path TO '||quote_literal(TG_TABLE_SCHEMA)||', public';
    element_table:= TG_ARGV[0];

		IF TG_OP = 'INSERT' THEN
		--	PERFORM audit_function(1); 
			RETURN NEW;
    
		ELSIF TG_OP = 'UPDATE' THEN
		--	PERFORM audit_function(2); 
			RETURN NEW;

		ELSIF TG_OP = 'DELETE' THEN
			v_sql:= 'DELETE FROM '||element_table||' WHERE id = '||quote_literal(OLD.id)||';';
			EXECUTE v_sql;
		--	PERFORM audit_function(3); 
			RETURN NULL;
		
		END IF;
    
END;
$BODY$
  LANGUAGE plpgsql VOLATILE
  COST 100;

CREATE TRIGGER gw_trg_ui_element_x_node INSTEAD OF INSERT OR DELETE OR UPDATE ON "SCHEMA_NAME".v_ui_element_x_node
FOR EACH ROW EXECUTE PROCEDURE "SCHEMA_NAME".gw_trg_ui_element(element_x_node);

CREATE TRIGGER gw_trg_ui_element_x_arc INSTEAD OF INSERT OR DELETE OR UPDATE ON "SCHEMA_NAME".v_ui_element_x_arc
FOR EACH ROW EXECUTE PROCEDURE "SCHEMA_NAME".gw_trg_ui_element(element_x_arc);

CREATE TRIGGER gw_trg_ui_elememt_x_connec INSTEAD OF INSERT OR DELETE OR UPDATE ON "SCHEMA_NAME".v_ui_element_x_connec
FOR EACH ROW EXECUTE PROCEDURE "SCHEMA_NAME".gw_trg_ui_element(element_x_connec);

      