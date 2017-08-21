﻿/*
This file is part of Giswater 3
The program is free software: you can redistribute it and/or modify it under the terms of the GNU General Public License as published by the Free Software Foundation, either version 3 of the License, or (at your option) any later version.
This version of Giswater is provided by Giswater Association
*/


CREATE OR REPLACE FUNCTION "SCHEMA_NAME".gw_fct_anl_node_flowregulator() RETURNS integer AS $BODY$
DECLARE
    node_id_var    text;
    point_aux      public.geometry;
    srid_schema    text;

BEGIN


    SET search_path = "SCHEMA_NAME", public;

    -- Reset values
    DELETE FROM anl_review_arc WHERE cur_user="current_user"() AND context='Node flow regulator';
    
	-- Computing process
	INSERT INTO anl_review_node (node_id, expl_id, context, num_arcs, the_geom)
	SELECT node_1 as node_id, node.expl_id, 'Node flow regulator'::text, count(node_1) as num_arcs, node.the_geom 
	FROM arc JOIN node ON node_id=node_1 
	GROUP BY node_1, node.expl_id, node.the_geom 
	HAVING count(node_1)> 1 
	ORDER BY 2 desc;

    RETURN 1;
  
        
END;
$BODY$
  LANGUAGE plpgsql VOLATILE
  COST 100;