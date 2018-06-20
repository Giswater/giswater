﻿CREATE OR REPLACE FUNCTION "SCHEMA_NAME"."gw_fct_updatesearch"(search_data json) RETURNS pg_catalog.json AS $BODY$DECLARE

--    Variables
    response_json json;
    response_json2 json;
    name_arg varchar;
    id_arg varchar;
    text_arg varchar;
    search_json json;
    tab_arg varchar;
    combo1 json;
    edit1 json;
    edit2 json;
    id_column varchar;
    catid varchar;
    states varchar[];
    api_version json;
    project_type_aux character varying;
    v_count integer;
    query_text text;

    -- network
    p_network_layer_arc varchar;
    p_network_layer_node varchar;
    p_network_layer_connec varchar;
    p_network_layer_gully varchar;
    p_network_layer_element varchar;

    p_network_search_field_arc_id varchar;
    p_network_search_field_node_id varchar;
    p_network_search_field_connec_id varchar;
    p_network_search_field_gully_id varchar;
    p_network_search_field_element_id varchar;

    p_network_search_field_arc_cat varchar; 
    p_network_search_field_node_cat varchar;
    p_network_search_field_connec_cat varchar;
    p_network_search_field_gully_cat varchar;
    p_network_search_field_element_cat varchar;

    p_network_search_field_arc varchar;
    p_network_search_field_node varchar;
    p_network_search_field_connec varchar;
    p_network_search_field_gully varchar;
    p_network_search_field_element varchar;
  
    --muni
    v_muni_layer varchar;
    v_muni_id_field varchar;
    v_muni_display_field varchar;
    v_muni_geom_field varchar;

    -- street
    v_street_layer varchar;
    v_street_id_field varchar;
    v_street_display_field varchar;
    v_street_muni_id_field varchar;
    v_street_geom_field varchar;

    -- adress
    v_adress_layer varchar;
    v_adress_id_field varchar;
    v_adress_display_field varchar;
    v_adress_street_id_field varchar;
    v_adress_geom_id_field varchar;

    --hydro    
    v_hydro_layer varchar;
    v_hydro_id_field varchar;
    v_hydro_search_field_1 varchar;
    v_hydro_search_field_2 varchar;
    v_hydro_search_field_3 varchar;
    v_hydro_parent_field varchar;

    --workcat
    v_workcat_layer varchar;
    v_workcat_id_field varchar;
    v_workcat_display_field varchar;
    v_workcat_geom_field varchar;

    --psector
    v_psector_layer varchar;
    v_psector_id_field varchar;
    v_psector_display_field varchar;
    v_psector_parent_field varchar;
    v_psector_geom_field varchar;

    --exploitation
    v_exploitation_layer varchar;
    v_exploitation_id_field varchar;
    v_exploitation_display_field varchar;
    v_exploitation_geom_field varchar;


BEGIN

--   Set search path to local schema
     SET search_path = "SCHEMA_NAME", public;

--   Set api version
     EXECUTE 'SELECT row_to_json(row) FROM (SELECT value FROM config_param_system WHERE parameter=''ApiVersion'') row'
        INTO api_version;

--   Get project type
     SELECT wsoftware INTO project_type_aux FROM version LIMIT 1;

--   Get tab
     tab_arg := search_data->>'tabName';


-- Network tab
--------------
IF tab_arg = 'network' THEN

    -- Layers to search:
    SELECT ((value::json)->>'sys_table_id') INTO p_network_layer_arc FROM SCHEMA_NAME.config_param_system WHERE context='api_search_network' AND parameter='api_search_arc';
    SELECT ((value::json)->>'sys_table_id') INTO p_network_layer_node FROM SCHEMA_NAME.config_param_system WHERE context='api_search_network' AND parameter='api_search_node';
    SELECT ((value::json)->>'sys_table_id') INTO p_network_layer_connec FROM SCHEMA_NAME.config_param_system WHERE context='api_search_network' AND parameter='api_search_connec';
    SELECT ((value::json)->>'sys_table_id') INTO p_network_layer_gully FROM SCHEMA_NAME.config_param_system WHERE context='api_search_network' AND parameter='api_search_gully';
    SELECT ((value::json)->>'sys_table_id') INTO p_network_layer_element FROM SCHEMA_NAME.config_param_system WHERE context='api_search_network' AND parameter='api_search_element';

    -- Layer's primary key;
    SELECT ((value::json)->>'sys_id_field') INTO p_network_search_field_arc_id FROM SCHEMA_NAME.config_param_system WHERE context='api_search_network' AND parameter='api_search_arc';
    SELECT ((value::json)->>'sys_id_field') INTO p_network_search_field_node_id FROM SCHEMA_NAME.config_param_system WHERE context='api_search_network' AND parameter='api_search_node';
    SELECT ((value::json)->>'sys_id_field') INTO p_network_search_field_connec_id FROM SCHEMA_NAME.config_param_system WHERE context='api_search_network' AND parameter='api_search_connec';
    SELECT ((value::json)->>'sys_id_field') INTO p_network_search_field_gully_id FROM SCHEMA_NAME.config_param_system WHERE context='api_search_network' AND parameter='api_search_gully';
    SELECT ((value::json)->>'sys_id_field') INTO p_network_search_field_element_id FROM SCHEMA_NAME.config_param_system WHERE context='api_search_network' AND parameter='api_search_element';

    -- Layer's field to search:
    SELECT ((value::json)->>'sys_search_field') INTO p_network_search_field_arc FROM SCHEMA_NAME.config_param_system WHERE context='api_search_network' AND parameter='api_search_arc';
    SELECT ((value::json)->>'sys_search_field') INTO p_network_search_field_node FROM SCHEMA_NAME.config_param_system WHERE context='api_search_network' AND parameter='api_search_node';
    SELECT ((value::json)->>'sys_search_field') INTO p_network_search_field_connec FROM SCHEMA_NAME.config_param_system WHERE context='api_search_network' AND parameter='api_search_connec';
    SELECT ((value::json)->>'sys_search_field') INTO p_network_search_field_gully FROM SCHEMA_NAME.config_param_system WHERE context='api_search_network' AND parameter='api_search_gully';
    SELECT ((value::json)->>'sys_search_field') INTO p_network_search_field_element FROM SCHEMA_NAME.config_param_system WHERE context='api_search_network' AND parameter='api_search_element';
   
    -- Layer's catalog field;
    SELECT ((value::json)->>'cat_field') INTO p_network_search_field_arc_cat FROM SCHEMA_NAME.config_param_system WHERE context='api_search_network' AND parameter='api_search_arc';
    SELECT ((value::json)->>'cat_field') INTO p_network_search_field_node_cat FROM SCHEMA_NAME.config_param_system WHERE context='api_search_network' AND parameter='api_search_node';
    SELECT ((value::json)->>'cat_field') INTO p_network_search_field_connec_cat FROM SCHEMA_NAME.config_param_system WHERE context='api_search_network' AND parameter='api_search_connec';
    SELECT ((value::json)->>'cat_field') INTO p_network_search_field_gully_cat FROM SCHEMA_NAME.config_param_system WHERE context='api_search_network' AND parameter='api_search_gully';
    SELECT ((value::json)->>'cat_field') INTO p_network_search_field_element_cat FROM SCHEMA_NAME.config_param_system WHERE context='api_search_network' AND parameter='api_search_element';

    
    -- Text to search
    combo1 := search_data->>'net_type';
    id_arg := combo1->>'id';
    name_arg := combo1->>'name';
    edit1 := search_data->>'net_code';
    text_arg := concat('%',edit1->>'text' ,'%');


   IF p_network_layer_arc IS NOT NULL THEN
    query_text=concat ('SELECT ',p_network_search_field_arc_id,' AS sys_id, ',
                 p_network_search_field_arc,' AS search_field, ', 
                 p_network_search_field_arc_cat,' AS cat_id, ',
                 p_network_search_field_arc_id, ' AS sys_idname, ',
                 quote_literal(p_network_layer_arc), '::text AS sys_table_id FROM ', p_network_layer_arc);

   END IF;
   IF p_network_layer_node IS NOT NULL THEN
        IF query_text IS NOT NULL THEN query_text=concat(query_text,' UNION '); END IF;
        query_text=concat (query_text, 'SELECT ',p_network_search_field_node_id,' AS sys_id, ',
                 p_network_search_field_node,' AS search_field, ', 
                 p_network_search_field_node_cat,' AS cat_id, ',
                 p_network_search_field_node_id, ' AS sys_idname, ',
                quote_literal(p_network_layer_node), '::text AS sys_table_id FROM ', p_network_layer_node);

   END IF;
   IF p_network_layer_connec IS NOT NULL THEN
           IF query_text IS NOT NULL THEN query_text=concat(query_text,' UNION '); END IF;
        query_text=concat (query_text, 'SELECT ',p_network_search_field_connec_id,' AS sys_id, ',
                 p_network_search_field_connec,' AS search_field, ', 
                 p_network_search_field_connec_cat,' AS cat_id, ',
                 p_network_search_field_connec_id, ' AS sys_idname, ',
                 quote_literal(p_network_layer_connec), '::text AS sys_table_id FROM ', p_network_layer_connec);
   END IF;

   IF p_network_layer_gully IS NOT NULL THEN
           IF query_text IS NOT NULL THEN query_text=concat(query_text,' UNION '); END IF;
        query_text=concat (query_text, 'SELECT ',p_network_search_field_gully_id,' AS sys_id, ',
                 p_network_search_field_gully,' AS search_field, ', 
                 p_network_search_field_gully_cat,' AS cat_id, ',
                 p_network_search_field_gully_id, ' AS sys_idname, ',
                 quote_literal(p_network_layer_gully), '::text AS sys_table_id FROM ', p_network_layer_gully);
   END IF;
  
   IF p_network_layer_element IS NOT NULL THEN 
           IF query_text IS NOT NULL THEN query_text=concat(query_text,' UNION '); END IF;
        query_text=concat (query_text, 'SELECT ',p_network_search_field_element_id,' AS sys_id, ',
                 p_network_search_field_element,' AS search_field, ', 
                 p_network_search_field_element_cat,' AS cat_id, ',
                 p_network_search_field_element_id, ' AS sys_idname, ',
                 quote_literal(p_network_layer_element), '::text AS sys_table_id FROM ', p_network_layer_element);
   END IF;

    IF id_arg = '' THEN 
        -- Get Ids for type combo
        EXECUTE 'SELECT array_to_json(array_agg(row_to_json(a))) FROM (SELECT sys_id, sys_table_id, 
                    CONCAT (sys_id, '' : '', cat_id) AS display_name, sys_idname FROM ('||query_text||')b
                    WHERE search_field::text ILIKE ' || quote_literal(text_arg) || ' 
                    ORDER BY search_field LIMIT 10) a'
                    INTO response_json;
    ELSE 
        -- Get Ids for type combo
        EXECUTE 'SELECT array_to_json(array_agg(row_to_json(a))) FROM (SELECT sys_id, sys_table_id, 
                    CONCAT (sys_id, '' : '', cat_id) AS display_name, sys_idname FROM ('||query_text||')b
                    WHERE search_field::text ILIKE ' || quote_literal(text_arg) || ' AND sys_table_id = '||quote_literal(id_arg)||'
                    ORDER BY search_field LIMIT 10) a'
                    INTO response_json;
    END IF;
    
-- Adress
---------
ELSIF tab_arg = 'adress' THEN

    -- Parameters of the municipality layer
    SELECT ((value::json)->>'sys_table_id') INTO v_muni_layer FROM SCHEMA_NAME.config_param_system WHERE parameter='api_search_muni';
    SELECT ((value::json)->>'sys_id_field') INTO v_muni_id_field FROM SCHEMA_NAME.config_param_system WHERE parameter='api_search_muni';
    SELECT ((value::json)->>'sys_search_field') INTO v_muni_display_field FROM SCHEMA_NAME.config_param_system WHERE parameter='api_search_muni';
    SELECT ((value::json)->>'sys_geom_field') INTO v_muni_geom_field FROM SCHEMA_NAME.config_param_system WHERE parameter='api_search_muni';

    -- Parameters of the street layer
    SELECT ((value::json)->>'sys_table_id') INTO v_street_layer FROM SCHEMA_NAME.config_param_system WHERE parameter='api_search_street';
    SELECT ((value::json)->>'sys_id_field') INTO v_street_id_field FROM SCHEMA_NAME.config_param_system WHERE parameter='api_search_street';
    SELECT ((value::json)->>'sys_search_field') INTO v_street_display_field FROM SCHEMA_NAME.config_param_system WHERE parameter='api_search_street';
    SELECT ((value::json)->>'sys_parent_field') INTO v_street_muni_id_field FROM SCHEMA_NAME.config_param_system WHERE parameter='api_search_street';
    SELECT ((value::json)->>'sys_geom_field') INTO v_street_geom_field FROM SCHEMA_NAME.config_param_system WHERE parameter='api_search_street';

    --Text to search
    combo1 := search_data->>'add_muni';
    id_arg := combo1->>'id';
    name_arg := combo1->>'name';
    edit1 := search_data->>'add_street';
    text_arg := concat('%', edit1->>'text' ,'%');

    -- Fix municipality vdefault
    DELETE FROM config_param_user WHERE parameter='search_municipality_vdefault' AND cur_user=current_user;
    INSERT INTO config_param_user (parameter, value, cur_user) VALUES ('search_municipality_vdefault',id_arg, current_user);

    -- Get street
    EXECUTE 'SELECT array_to_json(array_agg(row_to_json(a))) 
        FROM (SELECT '||v_street_layer||'.'||v_street_display_field||' as display_name, st_astext(st_envelope('||v_street_layer||'.'||v_street_geom_field||'))
        FROM '||v_street_layer||'
        JOIN '||v_muni_layer||' ON '||v_muni_layer||'.'||v_muni_id_field||' = '||v_street_layer||'.'||v_street_muni_id_field ||'
        WHERE '||v_muni_layer||'.'||v_muni_display_field||' = '||quote_literal(name_arg)||'
        AND '||v_street_layer||'.'||v_street_display_field||' ILIKE '''||text_arg||''' LIMIT 10 )a'
        INTO response_json;

-- Hydro tab
------------
    ELSIF tab_arg = 'hydro' THEN

        -- Parameters of the hydro layer
    SELECT ((value::json)->>'sys_table_id') INTO v_hydro_layer FROM SCHEMA_NAME.config_param_system WHERE parameter='api_search_hydrometer';
    SELECT ((value::json)->>'sys_id_field') INTO v_hydro_id_field FROM SCHEMA_NAME.config_param_system WHERE parameter='api_search_hydrometer';
    SELECT ((value::json)->>'sys_search_field_1') INTO v_hydro_search_field_1 FROM SCHEMA_NAME.config_param_system WHERE parameter='api_search_hydrometer';
    SELECT ((value::json)->>'sys_search_field_2') INTO v_hydro_search_field_2 FROM SCHEMA_NAME.config_param_system WHERE parameter='api_search_hydrometer';
    SELECT ((value::json)->>'sys_search_field_3') INTO v_hydro_search_field_3 FROM SCHEMA_NAME.config_param_system WHERE parameter='api_search_hydrometer';
    SELECT ((value::json)->>'sys_parent_field') INTO v_exploitation_display_field FROM SCHEMA_NAME.config_param_system WHERE parameter='api_search_hydrometer';

    -- Text to search
    combo1 := search_data->>'hydro_expl';
    id_arg := combo1->>'id';
    name_arg := combo1->>'name';
    edit1 := search_data->>'hydro_search';
    text_arg := concat('%', edit1->>'text' ,'%');

    -- Fix exploitation vdefault
    DELETE FROM config_param_user WHERE parameter='search_exploitation_vdefault' AND cur_user=current_user;
    INSERT INTO config_param_user (parameter, value, cur_user) VALUES ('search_exploitation_vdefault',id_arg, current_user);

    
    -- Get hydrometer 
    EXECUTE 'SELECT array_to_json(array_agg(row_to_json(a))) FROM (
        SELECT v_rtc_hydrometer.hydrometer_id AS sys_id, ST_X(connec.the_geom) AS sys_x, ST_Y(connec.the_geom) AS sys_y, 
        concat ('||v_hydro_search_field_1||','' - '','||v_hydro_search_field_2||','' - '','||v_hydro_search_field_3||') AS display_name, ''v_rtc_hydrometer'' AS sys_table_id, ''hydrometer_id'' AS sys_idname
        FROM v_rtc_hydrometer
        JOIN connec ON (connec.connec_id = v_rtc_hydrometer.connec_id)
        WHERE v_rtc_hydrometer.'||v_exploitation_display_field||' = '||quote_literal(name_arg)||'
                AND concat ('||v_hydro_search_field_1||','' - '','||v_hydro_search_field_2||','' - '','||v_hydro_search_field_3||') ILIKE '||quote_literal(text_arg)||'
                LIMIT 10) a'
        INTO response_json; 

-- Workcat tab
--------------
    ELSIF tab_arg = 'workcat' THEN

        -- Parameters of the workcat layer
    SELECT ((value::json)->>'sys_table_id') INTO v_workcat_layer FROM SCHEMA_NAME.config_param_system WHERE parameter='api_search_workcat';
    SELECT ((value::json)->>'sys_id_field') INTO v_workcat_id_field FROM SCHEMA_NAME.config_param_system WHERE parameter='api_search_workcat';
    SELECT ((value::json)->>'sys_search_field') INTO v_workcat_display_field FROM SCHEMA_NAME.config_param_system WHERE parameter='api_search_workcat';
    SELECT ((value::json)->>'sys_geom_field') INTO v_workcat_geom_field FROM SCHEMA_NAME.config_param_system WHERE parameter='api_search_workcat';
    
    -- Text to search
    edit1 := search_data->>'workcat_search';
    text_arg := concat('%', edit1->>'text' ,'%');

    --  Search in the workcat
    EXECUTE 'SELECT array_to_json(array_agg(row_to_json(a))) 
        FROM (SELECT '||v_workcat_display_field||' as display_name, '||quote_literal(v_workcat_layer)||' AS sys_table_id , 
        '||(v_workcat_id_field)||' AS sys_id, '||quote_literal(v_workcat_layer)||' 
        AS sys_idname FROM '||v_workcat_layer||'  
        WHERE '||v_workcat_display_field||' ILIKE '''||text_arg||''' LIMIT 10 )a'
        INTO response_json;

-- Psector tab
--------------
    ELSIF tab_arg = 'psector' THEN

        -- Parameters of the exploitation layer
    SELECT ((value::json)->>'sys_table_id') INTO v_exploitation_layer FROM SCHEMA_NAME.config_param_system WHERE parameter='api_search_exploitation';
    SELECT ((value::json)->>'sys_id_field') INTO v_exploitation_id_field FROM SCHEMA_NAME.config_param_system WHERE parameter='api_search_exploitation';
    SELECT ((value::json)->>'sys_search_field') INTO v_exploitation_display_field FROM SCHEMA_NAME.config_param_system WHERE parameter='api_search_exploitation';
    SELECT ((value::json)->>'sys_geom_field') INTO v_exploitation_geom_field FROM SCHEMA_NAME.config_param_system WHERE parameter='api_search_exploitation';
 
        -- Parameters of the psector layer
    SELECT ((value::json)->>'sys_table_id') INTO v_psector_layer FROM SCHEMA_NAME.config_param_system WHERE parameter='api_search_psector';
    SELECT ((value::json)->>'sys_id_field') INTO v_psector_id_field FROM SCHEMA_NAME.config_param_system WHERE parameter='api_search_psector';
    SELECT ((value::json)->>'sys_search_field') INTO v_psector_display_field FROM SCHEMA_NAME.config_param_system WHERE parameter='api_search_psector';
    SELECT ((value::json)->>'sys_parent_field') INTO v_psector_parent_field FROM SCHEMA_NAME.config_param_system WHERE parameter='api_search_psector';
    SELECT ((value::json)->>'sys_geom_field') INTO v_psector_geom_field FROM SCHEMA_NAME.config_param_system WHERE parameter='api_search_psector';
    
   
    --Text to search
    combo1 := search_data->>'psector_expl';
    id_arg := combo1->>'id';
    name_arg := combo1->>'name';
    edit1 := search_data->>'psector_search';
    text_arg := concat('%', edit1->>'text' ,'%');

    -- Fix exploitation vdefault
    DELETE FROM config_param_user WHERE parameter='search_exploitation_vdefault' AND cur_user=current_user;
    INSERT INTO config_param_user (parameter, value, cur_user) VALUES ('search_exploitation_vdefault',id_arg, current_user);

    -- Get psector (improved version)
    EXECUTE 'SELECT array_to_json(array_agg(row_to_json(a))) 
        FROM (SELECT '||v_psector_layer||'.'||v_psector_display_field||' as display_name, '||quote_literal(v_psector_layer)||' AS sys_table_id , 
        '||v_psector_layer||'.'||(v_psector_id_field)||' AS sys_id, '||quote_literal(v_psector_layer)||' AS sys_idname 
        FROM '||v_psector_layer||'  
        JOIN '||v_exploitation_layer||' ON '||v_exploitation_layer||'.'||v_exploitation_id_field||' = '||v_psector_layer||'.'||v_psector_parent_field||'
        WHERE '||v_exploitation_layer||'.'||v_exploitation_display_field||' = '||quote_literal(name_arg)||'
        AND '||v_psector_layer||'.'||v_psector_display_field||' ILIKE '''||text_arg||''' LIMIT 10 )a'
        INTO response_json;
    ELSE
        RETURN ('{"status":"Failed","SQLERR":"Only tab Network, Hydro and WorkCat are available","SQLSTATE":"NULL"}')::json;

    END IF;

  --    Control NULL's
    response_json := COALESCE(response_json, '{}');

--    Return
    RETURN ('{"status":"Accepted"' ||
        ', "apiVersion":'|| api_version ||
        ', "data":' || response_json ||      
        '}')::json;

--    Exception handling
    --EXCEPTION WHEN OTHERS THEN 
    --    RETURN ('{"status":"Failed","SQLERR":' || to_json(SQLERRM) || ', "apiVersion":'|| api_version || ',"SQLSTATE":' || to_json(SQLSTATE) || '}')::json;


END;$BODY$
LANGUAGE 'plpgsql' VOLATILE COST 100;
