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