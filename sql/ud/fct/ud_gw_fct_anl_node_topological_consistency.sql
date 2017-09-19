﻿/*
This file is part of Giswater 3
The program is free software: you can redistribute it and/or modify it under the terms of the GNU General Public License as published by the Free Software Foundation, either version 3 of the License, or (at your option) any later version.
This version of Giswater is provided by Giswater Association
*/



DROP FUNCTION IF EXISTS SCHEMA_NAME.gw_fct_anl_node_topological_consistency();
CREATE OR REPLACE FUNCTION SCHEMA_NAME.gw_fct_anl_node_topological_consistency() RETURNS integer AS $BODY$
DECLARE
    rec_node record;
    rec record;

BEGIN

    SET search_path = "SCHEMA_NAME", public;

    -- Reset values
    DELETE FROM anl_node WHERE cur_user="current_user"() AND context='Node topological consistency';
    
	-- Computing process
    INSERT INTO anl_node (node_id, node_type, expl_id, num_arcs, context, the_geom)
    SELECT node_id, node_type, node.expl_id, COUNT(*), 'Node topological consistency', node.the_geom 
    FROM node INNER JOIN arc ON arc.node_1 = node.node_id OR arc.node_2 = node.node_id 
    WHERE node.node_type != 'OUTFALL' GROUP BY node.node_id HAVING COUNT(*) = 1;

    RETURN 1;

END;
$BODY$
  LANGUAGE plpgsql VOLATILE
  COST 100;


