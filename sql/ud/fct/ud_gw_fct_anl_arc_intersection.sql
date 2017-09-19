﻿/*
This file is part of Giswater 3
The program is free software: you can redistribute it and/or modify it under the terms of the GNU General Public License as published by the Free Software Foundation, either version 3 of the License, or (at your option) any later version.
This version of Giswater is provided by Giswater Association
*/


CREATE OR REPLACE FUNCTION "SCHEMA_NAME".gw_fct_anl_arc_intersection() RETURNS integer AS $BODY$
DECLARE


BEGIN

    SET search_path = "SCHEMA_NAME", public;

    -- Reset values
    DELETE FROM anl_arc WHERE cur_user="current_user"() AND context='Arc intersection';
    
	-- Computing process
    INSERT INTO anl_arc (arc_id, expl_id, context, arc_id_aux, the_geom_p)
    SELECT a.arc_id AS arc_id_1, a.expl_id, 'Arc intersection', b.arc_id AS arc_id_2, (ST_Dumppoints(ST_Multi(ST_Intersection(a.the_geom, b.the_geom)))).geom AS the_geom
    FROM arc AS a, arc AS b 
    WHERE ST_Intersects(a.the_geom, b.the_geom) AND a.arc_id != b.arc_id AND NOT ST_Touches(a.the_geom, b.the_geom)
    AND a.the_geom is not null and b.the_geom is not null;

    RETURN 1;
        
END;
$BODY$
  LANGUAGE plpgsql VOLATILE
  COST 100;