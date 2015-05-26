/*
This file is part of Giswater
The program is free software: you can redistribute it and/or modify it under the terms of the GNU General Public License as published by the Free Software Foundation, either version 3 of the License, or (at your option) any later version.
This version of Giswater is provided by Giswater Association
*/

--------------------------------------------------------------------------------------------------
-- The purpose of this file is to be a storage of scripts in order to allow the possibility of update epanet schemas created in older version from this version to newest version
-- Changes incorporated in version 1.1.182 - 23/04/2015 
--------------------------------------------------------------------------------------------------

-- BUG FIX on update_t_inp_arc_insert() function
--------------------------------------------------------------------------------------------------

CREATE OR REPLACE FUNCTION SCHEMA_NAME.update_t_inp_arc_insert() RETURNS trigger AS
$BODY$

DECLARE
nodeRecord1 Record;
nodeRecord2 Record;
optionsRecord Record;
z1 double precision;
z2 double precision;
z_aux double precision;


BEGIN
	EXECUTE 'SET search_path TO '||quote_literal(TG_TABLE_SCHEMA)||', public';

    SELECT * INTO nodeRecord1 FROM node WHERE node.the_geom && ST_Expand(ST_startpoint(NEW.the_geom), 0.5)
	ORDER BY ST_Distance(node.the_geom, ST_startpoint(NEW.the_geom)) LIMIT 1;

    SELECT * INTO nodeRecord2 FROM node WHERE node.the_geom && ST_Expand(ST_endpoint(NEW.the_geom), 0.5)
    ORDER BY ST_Distance(node.the_geom, ST_endpoint(NEW.the_geom)) LIMIT 1;

    SELECT * INTO optionsRecord FROM inp_options LIMIT 1;

--  Control of length line
    IF (nodeRecord1.node_id IS NOT NULL) AND (nodeRecord2.node_id IS NOT NULL) THEN
		
--  Control of same node initial and final
    IF (nodeRecord1.node_id = nodeRecord2.node_id) THEN
	RAISE EXCEPTION 'One or more features has the same Node as Node1 and Node2. Please check your project and repair it!';
	ELSE
	
--  Update coordinates
    NEW.the_geom := ST_SetPoint(NEW.the_geom, 0, nodeRecord1.the_geom);
    NEW.the_geom := ST_SetPoint(NEW.the_geom, ST_NumPoints(NEW.the_geom) - 1, nodeRecord2.the_geom);

		IF (optionsRecord.link_offsets = 'DEPTH') THEN
            z1 := (nodeRecord1.top_elev - nodeRecord1.ymax + NEW.z1);
            z2 := (nodeRecord2.top_elev - nodeRecord2.ymax + NEW.z2);
        ELSE
			z1 := NEW.z1;
            z2 := NEW.z2;    
        END IF;

		IF (z1 > z2) THEN
            NEW.node_1 := nodeRecord1.node_id;
            NEW.node_2 := nodeRecord2.node_id;
        ELSE
--  Update conduit direction
			NEW.the_geom := ST_reverse(NEW.the_geom);
			z_aux := NEW.z1;
			NEW.z1 := NEW.z2;
			NEW.z2 := z_aux;

--  Update topology info
			NEW.node_1 := nodeRecord2.node_id;
			NEW.node_2 := nodeRecord1.node_id;
		END IF;
        RETURN NEW;
	END IF;
	ELSE
    RETURN NULL;
    END IF;
END;
$BODY$
LANGUAGE plpgsql VOLATILE
COST 100;