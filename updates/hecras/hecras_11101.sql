/*
This file is part of Giswater
The program is free software: you can redistribute it and/or modify it under the terms of the GNU General Public License as published by the Free Software Foundation, either version 3 of the License, or (at your option) any later version.
This version of Giswater is provided by Giswater Association
*/

--------------------------------------------------------------------------------------------------
-- The purpose of this file is to be a storage of scripts in order to allow the possibility of update epanet schemas created in older version from this version to newest version
-- Changes incorporated in version 1.1.101 - 09/09/2014 
--------------------------------------------------------------------------------------------------

--
-- TOC entry 1687 (class 1255 OID 30312)
-- Name: gr_export_geo(character varying, boolean, boolean, boolean, boolean, boolean); Type: FUNCTION; Schema: SCHEMA_NAME; Owner: postgres
--
DROP FUNCTION "SCHEMA_NAME"."gr_export_geo"();
DROP FUNCTION "SCHEMA_NAME"."gr_export_geo"(filename varchar);

CREATE FUNCTION "SCHEMA_NAME"."gr_export_geo"("filename" character varying, "SA" boolean, "IA" boolean, "Levees" boolean, "BO" boolean, "Manning" boolean) RETURNS integer
    LANGUAGE "plpgsql"
    AS 'DECLARE

	numRows integer;

BEGIN

	--Clear log
	PERFORM "SCHEMA_NAME".gr_clear_log();

	--Log
	INSERT INTO "SCHEMA_NAME".log VALUES (''gr_export_geo()'', ''Empty log'', CURRENT_TIMESTAMP);

	--Clear error
	PERFORM "SCHEMA_NAME".gr_clear_error();

	--Compute stream length
	PERFORM "SCHEMA_NAME".gr_stream_length();

	--XS station
	PERFORM "SCHEMA_NAME".gr_xs_station();

	--XS banks
	PERFORM "SCHEMA_NAME".gr_xs_banks();

	--XS flowpaths
	PERFORM "SCHEMA_NAME".gr_xs_lengths();

	--Check errors
	SELECT INTO numRows count(*) FROM "SCHEMA_NAME".error;

	IF numRows > 0 THEN
		INSERT INTO "SCHEMA_NAME".log VALUES (''gr_export_geo()'', ''Sdf export error!'', CURRENT_TIMESTAMP);
		RETURN -10;
	END IF;

	--Dump river & XS data to outfile and export to sdf
	PERFORM "SCHEMA_NAME".gr_dump_sdf(filename);

	--Check errors
	SELECT INTO numRows count(*) FROM "SCHEMA_NAME".error;

	IF numRows > 0 THEN
		INSERT INTO "SCHEMA_NAME".log VALUES (''gr_export_geo()'', ''Sdf export error!'', CURRENT_TIMESTAMP);
		RETURN -20;
	END IF;

	--Update 3d layers
	PERFORM "SCHEMA_NAME".gr_fill_3d_tables();

	--Log
	INSERT INTO "SCHEMA_NAME".log VALUES (''gr_export_geo()'', ''Sdf file finished'', CURRENT_TIMESTAMP);

	--Return
	RETURN 0;

END;
';


-- ------------------------------------------------------------
-- Clone
-- schema
-- ------------------------------------------------------------

CREATE OR REPLACE FUNCTION "SCHEMA_NAME".clone_schema(source_schema text, dest_schema text) RETURNS void LANGUAGE plpgsql AS $$
 
DECLARE
	rec_view record;
	rec_fk record;
	rec_table text;
	tablename text;
	default_ text;
	column_ text;
	msg text;
BEGIN

	-- Create destination schema
	EXECUTE 'CREATE SCHEMA ' || dest_schema ;
	 
	-- Sequences
	FOR rec_table IN
		SELECT sequence_name FROM information_schema.SEQUENCES WHERE sequence_schema = source_schema
	LOOP
		EXECUTE 'CREATE SEQUENCE ' || dest_schema || '.' || rec_table;
	END LOOP;
	 
	-- Tables
	FOR rec_table IN
		SELECT table_name FROM information_schema.TABLES WHERE table_schema = source_schema AND table_type = 'BASE TABLE' ORDER BY table_name
	LOOP
	  
	  	-- Create table in destination schema
		tablename := dest_schema || '.' || rec_table;
		EXECUTE 'CREATE TABLE ' || tablename || ' (LIKE ' || source_schema || '.' || rec_table || ' INCLUDING CONSTRAINTS INCLUDING INDEXES INCLUDING DEFAULTS)';
		
		-- Set contraints
		FOR column_, default_ IN
			SELECT column_name, REPLACE(column_default, source_schema, dest_schema) 
			FROM information_schema.COLUMNS 
			WHERE table_schema = dest_schema AND table_name = rec_table AND column_default LIKE 'nextval(%' || source_schema || '%::regclass)'
		LOOP
			EXECUTE 'ALTER TABLE ' || tablename || ' ALTER COLUMN ' || column_ || ' SET DEFAULT ' || default_;
		END LOOP;
		
		-- Copy table contents to destination schema
		EXECUTE 'INSERT INTO ' || tablename || ' SELECT * FROM ' || source_schema || '.' || rec_table; 	
		
	END LOOP;
	  
	-- Loop again trough tables in order to set Foreign Keys
	FOR rec_table IN
		SELECT table_name FROM information_schema.TABLES WHERE table_schema = source_schema AND table_type = 'BASE TABLE' ORDER BY table_name
	LOOP	  
	  
		tablename := dest_schema || '.' || rec_table;	  
		FOR rec_fk IN
			SELECT tc.constraint_name, tc.constraint_schema, tc.table_name, kcu.column_name,
			ccu.table_name AS parent_table, ccu.column_name AS parent_column,
			rc.update_rule AS on_update, rc.delete_rule AS on_delete
			FROM information_schema.table_constraints tc
				LEFT JOIN information_schema.key_column_usage kcu
				ON tc.constraint_catalog = kcu.constraint_catalog
				AND tc.constraint_schema = kcu.constraint_schema
				AND tc.constraint_name = kcu.constraint_name
			LEFT JOIN information_schema.referential_constraints rc
				ON tc.constraint_catalog = rc.constraint_catalog
				AND tc.constraint_schema = rc.constraint_schema
				AND tc.constraint_name = rc.constraint_name
			LEFT JOIN information_schema.constraint_column_usage ccu
				ON rc.unique_constraint_catalog = ccu.constraint_catalog
				AND rc.unique_constraint_schema = ccu.constraint_schema
				AND rc.unique_constraint_name = ccu.constraint_name
			WHERE tc.constraint_type = 'FOREIGN KEY' AND tc.constraint_schema = source_schema AND tc.table_name = rec_table
		LOOP
			msg:= 'ALTER TABLE '||tablename||' ADD CONSTRAINT '||rec_fk.constraint_name||' FOREIGN KEY('||rec_fk.column_name||') 
				REFERENCES '||dest_schema||'.'||rec_fk.parent_table||'('||rec_fk.parent_column||') ON DELETE '||rec_fk.on_delete||' ON UPDATE '||rec_fk.on_update;
			EXECUTE msg;
		END LOOP;		
		
	END LOOP;			
		
	-- Views
	FOR rec_view IN
		SELECT table_name, REPLACE(view_definition, source_schema, dest_schema) as definition FROM information_schema.VIEWS WHERE table_schema = source_schema
	LOOP
		EXECUTE 'CREATE VIEW ' || dest_schema || '.' || rec_view.table_name || ' AS ' || rec_view.definition;
	END LOOP;
 
END;
$$;