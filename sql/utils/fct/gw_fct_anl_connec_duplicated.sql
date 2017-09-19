﻿/*
This file is part of Giswater 3
The program is free software: you can redistribute it and/or modify it under the terms of the GNU General Public License as published by the Free Software Foundation, either version 3 of the License, or (at your option) any later version.
This version of Giswater is provided by Giswater Association
*/


CREATE OR REPLACE FUNCTION "SCHEMA_NAME".gw_fct_anl_connec_duplicated() RETURNS void AS $BODY$ 
DECLARE
    rec_connec record;
    rec record;

BEGIN

    SET search_path = "SCHEMA_NAME", public;

    -- Get data from config table
    SELECT * INTO rec FROM config; 

    -- Reset values
	DELETE FROM anl_connec WHERE cur_user="current_user"() AND context='Connec duplicated';
		
    -- Computing process
    INSERT INTO anl_connec (connec_id, state, connec_id_aux, expl_id, context, the_geom)
    SELECT DISTINCT t1.connec_id, t1.state, t2.connec_id, t1.expl_id, 'Connec duplicated', t1.the_geom
    FROM connec AS t1 JOIN connec AS t2 ON ST_Dwithin(t1.the_geom, t2.the_geom,(rec.connec_duplicated_tolerance)) 
    WHERE t1.connec_id != t2.connec_id  
    ORDER BY t1.connec_id;
    
    RETURN;  
    
END;
$BODY$
  LANGUAGE plpgsql VOLATILE
  COST 100;
