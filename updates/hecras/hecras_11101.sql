/*
This file is part of Giswater
The program is free software: you can redistribute it and/or modify it under the terms of the GNU General Public License as published by the Free Software Foundation, either version 3 of the License, or (at your option) any later version.
This version of Giswater is provided by Giswater Association
*/

--------------------------------------------------------------------------------------------------
-- The purpose of this file is to be a storage of scripts in order to allow the possibility of update epanet schemas created in older version from this version to newest version
-- Changes incorporated in version 1.1.101 - 09/09/2014 
--------------------------------------------------------------------------------------------------


SET search_path = "SCHEMA_NAME", public, pg_catalog;


--
-- TOC entry 1687 (class 1255 OID 30312)
-- Name: gr_export_geo(character varying, boolean, boolean, boolean, boolean, boolean); Type: FUNCTION; Schema: SCHEMA_NAME; Owner: postgres
--

CREATE OR REPLACE FUNCTION "SCHEMA_NAME".gr_export_geo("filename" character varying, "SA" boolean, "IA" boolean, "Levees" boolean, "BO" boolean, "Manning" boolean) RETURNS integer
    LANGUAGE "plpgsql"
    AS $$DECLARE

	numRows integer;

BEGIN

	--Clear log
	PERFORM gr_clear_log();

	--Log
	INSERT INTO log VALUES ('gr_export_geo()', 'Empty log', CURRENT_TIMESTAMP);

	--Clear error
	PERFORM gr_clear_error();

	--Compute stream length
	PERFORM gr_stream_length();

	--XS station
	PERFORM gr_xs_station();

	--XS banks
	PERFORM gr_xs_banks();

	--XS flowpaths
	PERFORM gr_xs_lengths();

	--Check errors
	SELECT INTO numRows count(*) FROM error;

	IF numRows > 0 THEN
		INSERT INTO log VALUES ('gr_export_geo()', 'Sdf export error!', CURRENT_TIMESTAMP);
		RETURN -10;
	END IF;

	--Dump river & XS data to outfile and export to sdf
	PERFORM gr_dump_sdf(filename);

	--Check errors
	SELECT INTO numRows count(*) FROM error;

	IF numRows > 0 THEN
		INSERT INTO log VALUES ('gr_export_geo()', 'Sdf export error!', CURRENT_TIMESTAMP);
		RETURN -20;
	END IF;

	--Update 3d layers
	PERFORM gr_fill_3d_tables();

	--Log
	INSERT INTO log VALUES ('gr_export_geo()', 'Sdf file finished', CURRENT_TIMESTAMP);

	--Return
	RETURN 0;

END;
$$;


-- ------------------------------------------------------------
-- Clone
-- schema
-- ------------------------------------------------------------

CREATE OR REPLACE FUNCTION "SCHEMA_NAME".clone_schema(source_schema text, dest_schema text) RETURNS void LANGUAGE plpgsql AS $$
 
DECLARE
	rec_view record;
	rec_fk record;
	rec_table text;
	rec_function record;
	rec_parameters record;
	tablename text;
	default_ text;
	column_ text;
	msg text;
	parameters_text text;

	on_delete_text text;
	on_update_text text;
BEGIN

	-- Create destination schema
	EXECUTE 'CREATE SCHEMA ' || dest_schema ;
	 
	-- Sequences
	FOR rec_table IN
		SELECT sequence_name FROM information_schema.SEQUENCES WHERE sequence_schema = source_schema
	LOOP
		EXECUTE 'CREATE SEQUENCE ' || dest_schema || '.' || quote_ident(rec_table);
	END LOOP;
	 
	-- Tables
	FOR rec_table IN
		SELECT table_name FROM information_schema.TABLES WHERE table_schema = source_schema AND table_type = 'BASE TABLE' ORDER BY table_name
	LOOP
	  
	  	-- Create table in destination schema
		tablename := dest_schema || '.' || quote_ident(rec_table);
		EXECUTE 'CREATE TABLE ' || tablename || ' (LIKE ' || source_schema || '.' || quote_ident(rec_table) || ' INCLUDING CONSTRAINTS INCLUDING INDEXES INCLUDING DEFAULTS)';
		
		-- Set contraints
		FOR column_, default_ IN
			SELECT column_name, REPLACE(column_default, source_schema, dest_schema) 
			FROM information_schema.COLUMNS 
			WHERE table_schema = dest_schema AND table_name = quote_ident(rec_table) AND column_default LIKE 'nextval(%' || source_schema || '%::regclass)'
		LOOP
			EXECUTE 'ALTER TABLE ' || tablename || ' ALTER COLUMN ' || column_ || ' SET DEFAULT ' || default_;
		END LOOP;
		
		-- Copy table contents to destination schema
		EXECUTE 'INSERT INTO ' || tablename || ' SELECT * FROM ' || source_schema || '.' || quote_ident(rec_table); 	
		
	END LOOP;
	  
	-- Loop again trough tables in order to set Foreign Keys
	FOR rec_table IN
		SELECT table_name FROM information_schema.TABLES WHERE table_schema = source_schema AND table_type = 'BASE TABLE' ORDER BY table_name
	LOOP	  
	  
		tablename := dest_schema || '.' || quote_ident(rec_table);	  
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
			WHERE tc.constraint_type = 'FOREIGN KEY' AND tc.constraint_schema = source_schema AND tc.table_name = quote_ident(rec_table)
		LOOP

--			Check action
			IF (rec_fk.on_delete = 'NO ACTION') THEN
				on_delete_text := 'NO ACTION';
			ELSE
				on_delete_text := quote_ident(rec_fk.on_delete);
			END IF;
			
			IF (rec_fk.on_update = 'NO ACTION') THEN
				on_update_text := 'NO ACTION';
			ELSE
				on_update_text := quote_ident(rec_fk.on_update);
			END IF;
						
			msg:= 'ALTER TABLE '||tablename||' ADD CONSTRAINT '||quote_ident(rec_fk.constraint_name)||' FOREIGN KEY('||quote_ident(rec_fk.column_name)||') 
				REFERENCES '||dest_schema||'.'||quote_ident(rec_fk.parent_table)||'('||quote_ident(rec_fk.parent_column)||') ON DELETE '||on_delete_text||' ON UPDATE '||on_update_text;

			EXECUTE msg;
		END LOOP;		
		
	END LOOP;			
		
	-- Views
	FOR rec_view IN
		SELECT table_name, REPLACE(view_definition, source_schema, dest_schema) as definition FROM information_schema.VIEWS WHERE table_schema = source_schema
	LOOP
		EXECUTE 'CREATE VIEW ' || dest_schema || '.' || quote_ident(rec_view.table_name) || ' AS ' || rec_view.definition;
	END LOOP;

	-- Functions
	SET check_function_bodies = false;
	
	FOR rec_function IN
		SELECT routine_name, REPLACE(routine_definition, source_schema, dest_schema) as definition, type_udt_name, external_language, specific_name FROM information_schema.ROUTINES WHERE routine_schema = source_schema
	LOOP

		-- Get function parameters
		parameters_text := '';

		FOR rec_parameters IN
			SELECT parameter_mode, parameter_name, udt_name FROM information_schema.PARAMETERS WHERE specific_name = rec_function.specific_name ORDER BY ordinal_position
		LOOP
			parameters_text := parameters_text || rec_parameters.parameter_mode || ' ' || rec_parameters.parameter_name || ' ' || rec_parameters.udt_name || ', ';
		END LOOP;

		IF (char_length(parameters_text) > 0) THEN
			parameters_text := substring(parameters_text from 1 for (char_length(parameters_text) - 2));
		END IF;

		IF (rec_function.type_udt_name != 'trigger') THEN
			msg := 'CREATE FUNCTION ' || dest_schema || '.' || quote_ident(rec_function.routine_name) || '(' ||parameters_text|| ') RETURNS ' || rec_function.type_udt_name || ' AS $BODY' || '$' || rec_function.definition || '$BODY' || '$'
			|| ' LANGUAGE '|| rec_function.external_language ||' VOLATILE COST 100;';
			EXECUTE msg;
		END IF;

	END LOOP;	

END;
$$;


SET search_path = public, pg_catalog;

-- Completed on 2013-12-17 23:15:03

--
-- PostgreSQL database dump complete
--