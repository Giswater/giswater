/*
This file is part of Giswater 2.0
The program is free software: you can redistribute it and/or modify it under the terms of the GNU General Public License as published by the Free Software Foundation, either version 3 of the License, or (at your option) any later version.
This version of Giswater is provided by Giswater Association
*/

SET search_path = "SCHEMA_NAME", public, pg_catalog;
----------------------------
--    GIS EDITING VIEWS
----------------------------

DROP VIEW IF EXISTS v_edit_node CASCADE;
CREATE OR REPLACE VIEW v_edit_node AS
SELECT 
node.node_id, 
node.elevation, 
node.depth, 
node.node_type,
node.nodecat_id,
cat_node.matcat_id AS "cat_matcat_id",
cat_node.pnom AS "cat_pnom",
cat_node.dnom AS "cat_dnom",
node.epa_type,
node.sector_id, 
node."state", 
node.annotation, 
node.observ, 
node."comment",
node.dma_id,
dma.presszonecat_id,
node.soilcat_id,
node.category_type,
node.fluid_type,
node.location_type,
node.workcat_id,
node.buildercat_id,
node.builtdate,
node.ownercat_id,
node.adress_01,
node.adress_02,
node.adress_03,
node.descript,
cat_node.svg AS "cat_svg",
node.rotation,
node.link,
node.verified,
node.the_geom,
node.workcat_id_end,
node.undelete,
node.label_x,
node.label_y,
node.label_rotation,
node.code,
node.publish,
node.inventory,
node.end_date,
node.macrodma_id,
exploitation.descript AS expl_name
FROM expl_selector, node
LEFT JOIN cat_node ON ((node.nodecat_id)::text = (cat_node.id)::text)
LEFT JOIN dma ON (((node.dma_id)::text = (dma.dma_id)::text))
JOIN exploitation ON node.expl_id=exploitation.expl_id
WHERE ((node.expl_id)::text=(expl_selector.expl_id)::text
AND expl_selector.cur_user="current_user"()::text);


DROP VIEW IF EXISTS v_edit_arc CASCADE;
CREATE OR REPLACE VIEW v_edit_arc AS
SELECT 
arc.arc_id,
arc.node_1,
arc.node_2,
arc.arccat_id, 
cat_arc.arctype_id AS "cat_arctype_id",
cat_arc.matcat_id AS "cat_matcat_id",
cat_arc.pnom AS "cat_pnom",
cat_arc.dnom AS "cat_dnom",
st_length2d(arc.the_geom)::numeric(12,2) AS gis_length,
arc.epa_type,
arc.sector_id, 
arc."state", 
arc.annotation, 
arc.observ, 
arc."comment",
arc.custom_length,
arc.dma_id,
dma.presszonecat_id,
arc.soilcat_id,
arc.category_type,
arc.fluid_type,
arc.location_type,
arc.workcat_id,
arc.buildercat_id,
arc.builtdate,
arc.ownercat_id,
arc.adress_01,
arc.adress_02,
arc.adress_03,
arc.descript,
cat_arc.svg AS "cat_svg",
arc.rotation,
arc.link,
arc.verified,
arc.the_geom,
arc.workcat_id_end,
arc.undelete,
arc.label_x,
arc.label_y,
arc.label_rotation,
arc.code,
arc.publish,
arc.inventory,
arc.end_date,
arc.macrodma_id,
exploitation.descript AS expl_name
FROM expl_selector,arc 
LEFT JOIN cat_arc ON (((arc.arccat_id)::text = (cat_arc.id)::text))
LEFT JOIN dma ON (((arc.dma_id)::text = (dma.dma_id)::text))
JOIN exploitation ON arc.expl_id=exploitation.expl_id
WHERE ((arc.expl_id)::text=(expl_selector.expl_id)::text
AND expl_selector.cur_user="current_user"()::text);




DROP VIEW IF EXISTS v_edit_link CASCADE;
CREATE OR REPLACE VIEW v_edit_link AS
SELECT 
link.link_id,
link.connec_id,
link.vnode_id,
st_length2d(link.the_geom) as gis_length,
link.custom_length,
connec.connecat_id,
link.the_geom
FROM ("SCHEMA_NAME".link 
LEFT JOIN connec ON (((connec.connec_id)::text = (link.connec_id)::text))
);




DROP VIEW IF EXISTS v_edit_man_pipe CASCADE;
CREATE OR REPLACE VIEW v_edit_man_pipe AS
SELECT 
arc.arc_id,
arc.node_1,
arc.node_2,
arc.arccat_id, 
cat_arc.arctype_id AS "cat_arctype_id",
cat_arc.matcat_id AS "cat_matcat_id",
cat_arc.pnom AS "cat_pnom",
cat_arc.dnom AS "cat_dnom",
st_length2d(arc.the_geom)::numeric(12,2) AS gis_length,
arc.epa_type,
arc.sector_id, 
arc."state", 
arc.annotation, 
arc.observ, 
arc."comment",
arc.custom_length,
arc.dma_id,
dma.presszonecat_id,
arc.soilcat_id,
arc.category_type,
arc.fluid_type,
arc.location_type,
arc.workcat_id,
arc.buildercat_id,
arc.builtdate,
arc.ownercat_id,
arc.adress_01,
arc.adress_02,
arc.adress_03,
arc.descript,
cat_arc.svg AS "cat_svg",
arc.rotation,
arc.link,
arc.verified,
arc.the_geom,
arc.workcat_id_end,
arc.undelete,
arc.label_x,
arc.label_y,
arc.label_rotation,
arc.code,
arc.publish,
arc.inventory,
arc.end_date,
arc.macrodma_id,
exploitation.descript AS expl_name
FROM expl_selector,arc 
LEFT JOIN cat_arc ON (((arc.arccat_id)::text = (cat_arc.id)::text))
LEFT JOIN dma ON (((arc.dma_id)::text = (dma.dma_id)::text))
JOIN exploitation ON arc.expl_id=exploitation.expl_id
WHERE ((arc.expl_id)::text=(expl_selector.expl_id)::text
AND expl_selector.cur_user="current_user"()::text);



DROP VIEW IF EXISTS v_edit_man_hydrant CASCADE;
CREATE OR REPLACE VIEW v_edit_man_hydrant AS 
 SELECT node.node_id,
    node.elevation AS hydrant_elevation,
    node.depth AS hydrant_depth,
    node.node_type,
    node.nodecat_id,
    cat_node.matcat_id AS cat_matcat_id,
    cat_node.pnom AS cat_pnom,
    cat_node.dnom AS cat_dnom,
    node.epa_type,
    node.sector_id,
    node.state AS hydrant_state,
    node.annotation AS hydrant_annotation,
    node.observ AS hydrant_observ,
    node.comment AS hydrant_comment,
    node.dma_id,
    dma.presszonecat_id,
    node.soilcat_id AS hydrant_soilcat_id,
    node.category_type AS hydrant_category_type,
    node.fluid_type AS hydrant_fluid_type,
    node.location_type AS hydrant_location_type,
    node.workcat_id AS hydrant_workcat_id,
    node.workcat_id_end AS hydrant_workcat_id_end,
    node.buildercat_id AS hydrant_buildercat_id,
    node.builtdate AS hydrant_builtdate,
    node.ownercat_id AS hydrant_ownercat_id,
    node.adress_01 AS hydrant_adress_01,
    node.adress_02 AS hydrant_adress_02,
    node.adress_03 AS hydrant_adress_03,
    node.descript AS hydrant_descript,
    cat_node.svg AS cat_svg,
    node.rotation AS hydrant_rotation,
    node.link AS hydrant_link,
    node.verified,
    node.the_geom,
    node.undelete,
    node.label_x AS hydrant_label_x,
    node.label_y AS hydrant_label_y,
    node.label_rotation AS hydrant_label_rotation,
    man_hydrant.communication AS hydrant_communication,
    man_hydrant.valve AS hydrant_valve,
    man_hydrant.valve_diam AS hydrant_valve_diam,
    man_hydrant.distance_left AS hydrant_distance_left,
    man_hydrant.distance_right AS hydrant_distance_right,
    man_hydrant.distance_perpendicular AS hydrant_distance_perpendicular,
    man_hydrant.location AS hydrant_location,
    man_hydrant.location_sign AS hydrant_location_sign,
	node.code AS hydrant_code,
	node.publish,
	node.inventory,
	node.end_date AS hydrant_end_date,
	node.macrodma_id,
	exploitation.descript AS expl_name
FROM expl_selector, node
	LEFT JOIN cat_node ON ((node.nodecat_id)::text = (cat_node.id)::text)
	LEFT JOIN dma ON (((node.dma_id)::text = (dma.dma_id)::text))
	 JOIN man_hydrant ON man_hydrant.node_id::text = node.node_id::text
	 JOIN exploitation ON node.expl_id=exploitation.expl_id
	WHERE ((node.expl_id)::text=(expl_selector.expl_id)::text
	AND expl_selector.cur_user="current_user"()::text);
  
    
	 

	 
DROP VIEW IF EXISTS v_edit_man_junction CASCADE;
CREATE OR REPLACE VIEW v_edit_man_junction AS 
 SELECT node.node_id,
    node.elevation AS junction_elevation,
    node.depth AS junction_depth,
    node.node_type,
    node.nodecat_id,
    cat_node.matcat_id AS cat_matcat_id,
    cat_node.pnom AS cat_pnom,
    cat_node.dnom AS cat_dnom,
    node.epa_type,
    node.sector_id,
    node.state AS junction_state,
    node.annotation AS junction_annotation,
    node.observ AS junction_observ,
    node.comment AS junction_comment,
    node.dma_id,
    dma.presszonecat_id,
    node.soilcat_id AS junction_soilcat_id,
    node.category_type AS junction_category_type,
    node.fluid_type AS junction_fluid_type,
    node.location_type AS junction_location_type,
    node.workcat_id AS junction_workcat_id,
    node.workcat_id_end AS junction_workcat_id_end,
    node.buildercat_id AS junction_buildercat_id,
    node.builtdate AS junction_builtdate,
    node.ownercat_id AS junction_ownercat_id,
    node.adress_01 AS junction_adress_01,
    node.adress_02 AS junction_adress_02,
    node.adress_03 AS junction_adress_03,
    node.descript AS junction_descript,
    cat_node.svg AS cat_svg,
    node.rotation AS junction_rotation,
    node.label_x AS junction_label_x,
    node.label_y AS junction_label_y,
    node.label_rotation AS junction_label_rotation,
    node.link AS junction_link,
    node.verified,
    node.the_geom,
    node.undelete,
	node.code AS junction_code,
	node.publish,
	node.inventory,
	node.end_date AS junction_end_date,
	node.macrodma_id,
	exploitation.descript AS expl_name
FROM expl_selector, node
	LEFT JOIN cat_node ON ((node.nodecat_id)::text = (cat_node.id)::text)
	LEFT JOIN dma ON (((node.dma_id)::text = (dma.dma_id)::text))
	JOIN man_junction ON node.node_id::text = man_junction.node_id::text
	JOIN exploitation ON node.expl_id=exploitation.expl_id
	WHERE ((node.expl_id)::text=(expl_selector.expl_id)::text
	AND expl_selector.cur_user="current_user"()::text);
  

	 
DROP VIEW IF EXISTS v_edit_man_manhole CASCADE;
CREATE OR REPLACE VIEW v_edit_man_manhole AS 
 SELECT node.node_id,
    node.elevation AS manhole_elevation,
    node.depth AS manhole_depth,
    node.node_type,
    node.nodecat_id,
    cat_node.matcat_id AS cat_matcat_id,
    cat_node.pnom AS cat_pnom,
    cat_node.dnom AS cat_dnom,
    node.epa_type,
    node.sector_id,
    node.state AS manhole_state,
    node.annotation AS manhole_annotation,
    node.observ AS manhole_observ,
    node.comment AS manhole_comment,
    node.dma_id,
    dma.presszonecat_id,
    node.soilcat_id AS manhole_soilcat_id,
    node.category_type AS manhole_category_type,
    node.fluid_type AS manhole_fluid_type,
    node.location_type AS manhole_location_type,
    node.workcat_id AS manhole_workcat_id,
    node.workcat_id_end AS manhole_workcat_id_end,
    node.buildercat_id AS manhole_buildercat_id,
    node.builtdate AS manhole_builtdate,
    node.ownercat_id AS manhole_ownercat_id,
    node.adress_01 AS manhole_adress_01,
    node.adress_02 AS manhole_adress_02,
    node.adress_03 AS manhole_adress_03,
    node.descript AS manhole_descript,
    cat_node.svg AS cat_svg,
    node.rotation AS manhole_rotation,
    node.label_x AS manhole_label_x,
    node.label_y AS manhole_label_y,
    node.label_rotation AS manhole_label_rotation,
    node.link AS manhole_link,
    node.verified,
    node.the_geom,
    node.undelete,
	node.code AS manhole_code,
	node.publish,
	node.inventory,
	node.end_date AS manhole_end_date,
	node.macrodma_id,
	exploitation.descript AS expl_name
FROM expl_selector, node
	LEFT JOIN cat_node ON ((node.nodecat_id)::text = (cat_node.id)::text)
	LEFT JOIN dma ON (((node.dma_id)::text = (dma.dma_id)::text))
    JOIN man_manhole ON node.node_id::text = man_manhole.node_id::text
	JOIN exploitation ON node.expl_id=exploitation.expl_id
	WHERE ((node.expl_id)::text=(expl_selector.expl_id)::text
	AND expl_selector.cur_user="current_user"()::text);




DROP VIEW IF EXISTS v_edit_man_meter CASCADE;
CREATE OR REPLACE VIEW v_edit_man_meter AS 
 SELECT node.node_id,
    node.elevation AS meter_elevation,
    node.depth AS meter_depth,
    node.node_type,
    node.nodecat_id,
    cat_node.matcat_id AS cat_matcat_id,
    cat_node.pnom AS cat_pnom,
    cat_node.dnom AS cat_dnom,
    node.epa_type,
    node.sector_id,
    node.state AS meter_state,
    node.annotation AS meter_annotation,
    node.observ AS meter_observ,
    node.comment AS meter_comment,
    node.dma_id,
    dma.presszonecat_id,
    node.soilcat_id AS meter_soilcat_id,
    node.category_type AS meter_category_type,
    node.fluid_type AS meter_fluid_type,
    node.location_type AS meter_location_type,
    node.workcat_id AS meter_workcat_id,
    node.workcat_id_end AS meter_workcat_id_end,
    node.buildercat_id AS meter_buildercat_id,
    node.builtdate AS meter_builtdate,
    node.ownercat_id AS meter_ownercat_id,
    node.adress_01 AS meter_adress_01,
    node.adress_02 AS meter_adress_02,
    node.adress_03 AS meter_adress_03,
    node.descript AS meter_descript,
    cat_node.svg AS cat_svg,
    node.rotation AS meter_rotation,
    node.link AS meter_link,
    node.label_x AS meter_label_x,
    node.label_y AS meter_label_y,
    node.label_rotation AS meter_label_rotation,
    node.verified,
    node.the_geom,
    node.undelete,
	node.code AS meter_code,
	node.publish,
	node.inventory,
	node.end_date AS meter_end_date,
	node.macrodma_id,
	exploitation.descript AS expl_name
FROM expl_selector, node
	LEFT JOIN cat_node ON ((node.nodecat_id)::text = (cat_node.id)::text)
	LEFT JOIN dma ON (((node.dma_id)::text = (dma.dma_id)::text))
	JOIN man_meter ON man_meter.node_id::text = node.node_id::text
	JOIN exploitation ON node.expl_id=exploitation.expl_id
	WHERE ((node.expl_id)::text=(expl_selector.expl_id)::text
	AND expl_selector.cur_user="current_user"()::text);


	 
DROP VIEW IF EXISTS v_edit_man_pump CASCADE;
CREATE OR REPLACE VIEW v_edit_man_pump AS 
 SELECT node.node_id,
    node.elevation AS pump_elevation,
    node.depth AS pump_depth,
    node.node_type,
    node.nodecat_id,
    cat_node.matcat_id AS cat_matcat_id,
    cat_node.pnom AS cat_pnom,
    cat_node.dnom AS cat_dnom,
    node.epa_type,
    node.sector_id,
    node.state AS pump_state,
    node.annotation AS pump_annotation,
    node.observ AS pump_observ,
    node.comment AS pump_comment,
    node.dma_id,
    dma.presszonecat_id,
    node.soilcat_id AS pump_soilcat_id,
    node.category_type AS pump_category_type,
    node.fluid_type AS pump_fluid_type,
    node.location_type AS pump_location_type,
    node.workcat_id AS pump_workcat_id,
    node.workcat_id_end AS pump_workcat_id_end,
    node.buildercat_id AS pump_buildercat_id,
    node.builtdate AS pump_builtdate,
    node.ownercat_id AS pump_ownercat_id,
    node.adress_01 AS pump_adress_01,
    node.adress_02 AS pump_adress_02,
    node.adress_03 AS pump_adress_03,
    node.descript AS pump_descript,
    cat_node.svg AS cat_svg,
    node.rotation AS pump_rotation,
    node.label_x AS pump_label_x,
    node.label_y AS pump_label_y,
    node.label_rotation AS pump_label_rotation,
    node.link AS pump_link,
    node.verified,
    node.the_geom,
    node.undelete,
	node.code AS pump_code,
	node.publish,
	node.inventory,
	node.end_date AS pump_end_date,
	node.macrodma_id,
	man_pump.elev_height,
	exploitation.descript AS expl_name
FROM expl_selector, node
	LEFT JOIN cat_node ON ((node.nodecat_id)::text = (cat_node.id)::text)
	LEFT JOIN dma ON (((node.dma_id)::text = (dma.dma_id)::text))
    JOIN man_pump ON man_pump.node_id::text = node.node_id::text
	JOIN exploitation ON node.expl_id=exploitation.expl_id
	WHERE ((node.expl_id)::text=(expl_selector.expl_id)::text
	AND expl_selector.cur_user="current_user"()::text);


	
	
DROP VIEW IF EXISTS v_edit_man_reduction CASCADE;
CREATE OR REPLACE VIEW v_edit_man_reduction AS 
 SELECT node.node_id,
    node.elevation AS reduction_elevation,
    node.depth AS reduction_depth,
    node.node_type,
    node.nodecat_id,
    cat_node.matcat_id AS cat_matcat_id,
    cat_node.pnom AS cat_pnom,
    cat_node.dnom AS cat_dnom,
    node.epa_type,
    node.sector_id,
    node.state AS reduction_state,
    node.annotation AS reduction_annotation,
    node.observ AS reduction_observ,
    node.comment AS reduction_comment,
    node.dma_id,
    dma.presszonecat_id,
    node.soilcat_id AS reduction_soilcat_id,
    node.category_type AS reduction_category_type,
    node.fluid_type AS reduction_fluid_type,
    node.location_type AS reduction_location_type,
    node.workcat_id AS reduction_workcat_id,
    node.workcat_id_end AS reduction_workcat_id_end,
    node.buildercat_id AS reduction_buildercat_id,
    node.builtdate AS reduction_builtdate,
    node.ownercat_id AS reduction_ownercat_id,
    node.adress_01 AS reduction_adress_01,
    node.adress_02 AS reduction_adress_02,
    node.adress_03 AS reduction_adress_03,
    node.descript AS reduction_descript,
    cat_node.svg AS cat_svg,
    node.rotation AS reduction_rotation,
    node.link AS reduction_link,
    node.verified,
    node.the_geom,
    node.undelete,
    node.label_x AS reduction_label_x,
    node.label_y AS reduction_label_y,
    node.label_rotation AS reduction_label_rotation,
    man_reduction.diam_initial AS reduction_diam_initial,
    man_reduction.diam_final AS reduction_diam_final,
	node.code AS reduction_code,
	node.publish,
	node.inventory,
	node.end_date AS reduction_end_date,
	node.macrodma_id,
	exploitation.descript AS expl_name
FROM expl_selector, node
	LEFT JOIN cat_node ON ((node.nodecat_id)::text = (cat_node.id)::text)
	LEFT JOIN dma ON (((node.dma_id)::text = (dma.dma_id)::text))
	JOIN man_reduction ON man_reduction.node_id::text = node.node_id::text
	JOIN exploitation ON node.expl_id=exploitation.expl_id
	WHERE ((node.expl_id)::text=(expl_selector.expl_id)::text
	AND expl_selector.cur_user="current_user"()::text);
	 


DROP VIEW IF EXISTS v_edit_man_source CASCADE;
CREATE OR REPLACE VIEW v_edit_man_source AS 
 SELECT node.node_id,
    node.elevation AS source_elevation,
    node.depth AS source_depth,
    node.node_type,
    node.nodecat_id,
    cat_node.matcat_id AS cat_matcat_id,
    cat_node.pnom AS cat_pnom,
    cat_node.dnom AS cat_dnom,
    node.epa_type,
    node.sector_id,
    node.state AS source_state,
    node.annotation AS source_annotation,
    node.observ AS source_observ,
    node.comment AS source_comment,
    node.dma_id,
    dma.presszonecat_id,
    node.soilcat_id AS source_soilcat_id,
    node.category_type AS source_category_type,
    node.fluid_type AS source_fluid_type,
    node.location_type AS source_location_type,
    node.workcat_id AS source_workcat_id,
    node.workcat_id_end AS source_workcat_id_end,
    node.buildercat_id AS source_buildercat_id,
    node.builtdate AS source_builtdate,
    node.ownercat_id AS source_ownercat_id,
    node.adress_01 AS source_adress_01,
    node.adress_02 AS source_adress_02,
    node.adress_03 AS source_adress_03,
    node.descript AS source_descript,
    cat_node.svg AS cat_svg,
    node.rotation AS source_rotation,
    node.link AS source_link,
    node.verified,
    node.the_geom,
    node.undelete,
    node.label_x AS source_label_x,
    node.label_y AS source_label_y,
    node.label_rotation AS source_label_rotation,
	node.code AS source_code,
	node.publish,
	node.inventory,
	node.end_date AS source_end_date,
	node.macrodma_id,
	exploitation.descript AS expl_name
FROM expl_selector, node
	LEFT JOIN cat_node ON ((node.nodecat_id)::text = (cat_node.id)::text)
	LEFT JOIN dma ON (((node.dma_id)::text = (dma.dma_id)::text))
	JOIN man_source ON node.node_id::text = man_source.node_id::text
	JOIN exploitation ON node.expl_id=exploitation.expl_id
	WHERE ((node.expl_id)::text=(expl_selector.expl_id)::text
	AND expl_selector.cur_user="current_user"()::text);
	
 
	 
DROP VIEW IF EXISTS v_edit_man_valve CASCADE;
CREATE OR REPLACE VIEW v_edit_man_valve AS 
 SELECT node.node_id,
    node.elevation AS valve_elevation,
    node.depth AS valve_depth,
    node.node_type,
    node.nodecat_id,
    cat_node.matcat_id AS cat_matcat_id,
    cat_node.pnom AS cat_pnom,
    cat_node.dnom AS cat_dnom,
    node.epa_type,
    node.sector_id,
    node.state AS valve_state,
    node.annotation AS valve_annotation,
    node.observ AS valve_observ,
    node.comment AS valve_comment,
    node.dma_id,
    dma.presszonecat_id,
    node.soilcat_id AS valve_soilcat_id,
    node.category_type AS valve_category_type,
    node.fluid_type AS valve_fluid_type,
    node.location_type AS valve_location_type,
    node.workcat_id AS valve_workcat_id,
    node.workcat_id_end AS valve_workcat_id_end,
    node.buildercat_id AS valve_buildercat_id,
    node.builtdate AS valve_builtdate,
    node.ownercat_id AS valve_ownercat_id,
    node.adress_01 AS valve_adress_01,
    node.adress_02 AS valve_adress_02,
    node.adress_03 AS valve_adress_03,
    node.descript AS valve_descript,
    cat_node.svg AS cat_svg,
    node.rotation AS valve_rotation,
    node.link AS valve_link,
    node.verified,
    node.the_geom,
    node.undelete,
    node.label_x AS valve_label_x,
    node.label_y AS valve_label_y,
    node.label_rotation AS valve_label_rotation,
    man_valve.type AS valve_type,
    man_valve.opened AS valve_opened,
    man_valve.acessibility AS valve_acessibility,
    man_valve.broken AS valve_broken,
    man_valve.mincut_anl AS valve_mincut_anl,
    man_valve.hydraulic_anl AS valve_hydraulic_anl,
    man_valve.burried AS valve_burried,
    man_valve.irrigation_indicator AS valve_irrigation_indicator,
    man_valve.pression_entry AS valve_pression_entry,
    man_valve.pression_exit AS valve_pression_exit,
    man_valve.depth_valveshaft AS valve_depth_valveshaft,
    man_valve.regulator_situation AS valve_regulator_situation,
    man_valve.regulator_location AS valve_regulator_location,
    man_valve.regulator_observ AS valve_regulator_observ,
    man_valve.lin_meters AS valve_lin_meters,
    man_valve.exit_type AS valve_exit_type,
    man_valve.exit_code AS valve_exit_code,
    man_valve.valve AS valve_valve,
    man_valve.valve_diam AS valve_valve_diam,
    man_valve.drive_type AS valve_drive_type,
    man_valve.location AS valve_location,
	node.code AS valve_code,
	node.publish,
	node.inventory,
	node.end_date AS valve_end_date,
	node.macrodma_id,
	man_valve.cat_valve2 AS valve_cat_valve2,
	exploitation.descript AS expl_name
FROM expl_selector, node
	LEFT JOIN cat_node ON ((node.nodecat_id)::text = (cat_node.id)::text)
	LEFT JOIN dma ON (((node.dma_id)::text = (dma.dma_id)::text))
    JOIN man_valve ON man_valve.node_id::text = node.node_id::text
	JOIN exploitation ON node.expl_id=exploitation.expl_id
	WHERE ((node.expl_id)::text=(expl_selector.expl_id)::text
	AND expl_selector.cur_user="current_user"()::text);
	

	 
DROP VIEW IF EXISTS v_edit_man_waterwell CASCADE;
CREATE OR REPLACE VIEW v_edit_man_waterwell AS 
 SELECT node.node_id,
    node.elevation AS waterwell_elevation,
    node.depth AS waterwell_depth,
    node.node_type,
    node.nodecat_id,
    cat_node.matcat_id AS cat_matcat_id,
    cat_node.pnom AS cat_pnom,
    cat_node.dnom AS cat_dnom,
    node.epa_type,
    node.sector_id,
    node.state AS waterwell_state,
    node.annotation AS waterwell_annotation,
    node.observ AS waterwell_observ,
    node.comment AS waterwell_comment,
    node.dma_id,
    dma.presszonecat_id,
    node.soilcat_id AS waterwell_soilcat_id,
    node.category_type AS waterwell_category_type,
    node.fluid_type AS waterwell_fluid_type,
    node.location_type AS waterwell_location_type,
    node.workcat_id AS waterwell_workcat_id,
    node.workcat_id_end AS waterwell_workcat_id_end,
    node.buildercat_id AS waterwell_buildercat_id,
    node.builtdate AS waterwell_builtdate,
    node.ownercat_id AS waterwell_ownercat_id,
    node.adress_01 AS waterwell_adress_01,
    node.adress_02 AS waterwell_adress_02,
    node.adress_03 AS waterwell_adress_03,
    node.descript AS waterwell_descript,
    cat_node.svg AS cat_svg,
    node.rotation AS waterwell_rotation,
    node.link AS waterwell_link,
    node.verified,
    node.the_geom,
    node.undelete,
    node.label_x AS waterwell_label_x,
    node.label_y AS waterwell_label_y,
    node.label_rotation AS waterwell_label_rotation,
	node.code AS waterwell_code,
	node.publish,
	node.inventory,
	node.end_date AS waterwell_end_date,
	node.macrodma_id,
	exploitation.descript AS expl_name
FROM expl_selector, node
	LEFT JOIN cat_node ON ((node.nodecat_id)::text = (cat_node.id)::text)
	LEFT JOIN dma ON (((node.dma_id)::text = (dma.dma_id)::text))
	JOIN man_waterwell ON node.node_id::text = man_waterwell.node_id::text
	JOIN exploitation ON node.expl_id=exploitation.expl_id
	WHERE ((node.expl_id)::text=(expl_selector.expl_id)::text
	AND expl_selector.cur_user="current_user"()::text);
	
   


DROP VIEW IF EXISTS v_edit_man_filter CASCADE;
CREATE OR REPLACE VIEW v_edit_man_filter AS 
 SELECT node.node_id,
    node.elevation AS filter_elevation,
    node.depth AS filter_depth,
    node.node_type,
    node.nodecat_id,
    cat_node.matcat_id AS cat_matcat_id,
    cat_node.pnom AS cat_pnom,
    cat_node.dnom AS cat_dnom,
    node.epa_type,
    node.sector_id,
    node.state AS filter_state,
    node.annotation AS filter_annotation,
    node.observ AS filter_observ,
    node.comment AS filter_comment,
    node.dma_id,
    dma.presszonecat_id,
    node.soilcat_id AS filter_soilcat_id,
    node.category_type AS filter_category_type,
    node.fluid_type AS filter_fluid_type,
    node.location_type AS filter_location_type,
    node.workcat_id AS filter_workcat_id,
    node.workcat_id_end AS filter_workcat_id_end,
    node.buildercat_id AS filter_buildercat_id,
    node.builtdate AS filter_builtdate,
    node.ownercat_id AS filter_ownercat_id,
    node.adress_01 AS filter_adress_01,
    node.adress_02 AS filter_adress_02,
    node.adress_03 AS filter_adress_03,
    node.descript AS filter_descript,
    cat_node.svg AS cat_svg,
    node.rotation AS filter_rotation,
    node.label_x AS filter_label_x,
    node.label_y AS filter_label_y,
    node.label_rotation AS filter_label_rotation,
    node.link AS filter_link,
    node.verified,
    node.the_geom,
    node.undelete,
	node.code AS filter_code,
	node.publish,
	node.inventory,
	node.end_date AS filter_end_date,
	node.macrodma_id,
	exploitation.descript AS expl_name
FROM expl_selector, node
	LEFT JOIN cat_node ON ((node.nodecat_id)::text = (cat_node.id)::text)
	LEFT JOIN dma ON (((node.dma_id)::text = (dma.dma_id)::text))
	JOIN man_filter ON node.node_id::text = man_filter.node_id::text
	JOIN exploitation ON node.expl_id=exploitation.expl_id
	WHERE ((node.expl_id)::text=(expl_selector.expl_id)::text
	AND expl_selector.cur_user="current_user"()::text);
	
		
	DROP VIEW IF EXISTS v_edit_sector CASCADE;
CREATE VIEW v_edit_sector AS SELECT
	sector.sector_id,
	sector.descript,
	sector.the_geom,
	sector.undelete,
	exploitation.descript AS expl_name
FROM expl_selector,sector 
JOIN exploitation ON sector.expl_id=exploitation.expl_id
WHERE ((sector.expl_id)::text=(expl_selector.expl_id)::text
AND expl_selector.cur_user="current_user"()::text);


DROP VIEW IF EXISTS v_edit_dma CASCADE;
CREATE VIEW v_edit_dma AS SELECT
	dma.dma_id,
	dma.sector_id,
	dma.presszonecat_id,
	dma.descript,
	dma.observ,
	dma.the_geom,
	dma.undelete,
	dma.macrodma_id,
	exploitation.descript AS expl_name
	FROM expl_selector, dma 
	JOIN exploitation ON dma.expl_id=exploitation.expl_id
WHERE ((dma.expl_id)::text=(expl_selector.expl_id)::text
AND expl_selector.cur_user="current_user"()::text);
  
  
DROP VIEW IF EXISTS v_edit_macrodma CASCADE;
CREATE VIEW v_edit_macrodma AS SELECT
	macrodma.macrodma_id,
	macrodma.descript,
	macrodma.the_geom,
	macrodma.undelete,
	exploitation.descript AS expl_name
FROM expl_selector, macrodma 
JOIN exploitation ON macrodma.expl_id=exploitation.expl_id
WHERE ((macrodma.expl_id)::text=(expl_selector.expl_id)::text
AND expl_selector.cur_user="current_user"()::text);
  
  
DROP VIEW IF EXISTS v_edit_presszone CASCADE;
CREATE VIEW v_edit_presszone AS SELECT
	presszone.id,
	presszone.the_geom,
	presszone.presszonecat_id,
	presszone.sector,
	presszone.text,
	presszone.undelete,
	exploitation.descript AS expl_name
FROM expl_selector,presszone
JOIN exploitation ON presszone.expl_id=exploitation.expl_id
WHERE ((presszone.expl_id)::text=(expl_selector.expl_id)::text
AND expl_selector.cur_user="current_user"()::text);


DROP VIEW IF EXISTS v_edit_polygon CASCADE;
CREATE VIEW v_edit_polygon AS SELECT
	pol_id,
	text,
	polygon.the_geom,
	polygon.undelete,
	exploitation.descript AS expl_name
FROM expl_selector, polygon
JOIN exploitation ON polygon.expl_id=exploitation.expl_id
WHERE ((polygon.expl_id)::text=(expl_selector.expl_id)::text
AND expl_selector.cur_user="current_user"()::text);


DROP VIEW IF EXISTS v_edit_vnode CASCADE;
CREATE VIEW v_edit_vnode AS SELECT
	vnode_id,
	userdefined_pos,
	vnode_type,
	sector_id,
	state,
	annotation,
	vnode.the_geom,
	exploitation.descript AS expl_name
FROM expl_selector,vnode
JOIN exploitation ON vnode.expl_id=exploitation.expl_id
WHERE ((vnode.expl_id)::text=(expl_selector.expl_id)::text
AND expl_selector.cur_user="current_user"()::text);


DROP VIEW IF EXISTS v_edit_point CASCADE;
CREATE VIEW v_edit_point AS SELECT
	point_id,
	point_type,
	observ,
	text,
	link,
	point.the_geom,
	exploitation.descript AS expl_name
FROM expl_selector,point
JOIN exploitation ON point.expl_id=exploitation.expl_id
WHERE ((point.expl_id)::text=(expl_selector.expl_id)::text
AND expl_selector.cur_user="current_user"()::text);


DROP VIEW IF EXISTS v_edit_pond CASCADE;
CREATE VIEW v_edit_pond AS SELECT
	pond_id,
	connec_id,
	code_comercial,
	pond.the_geom,
	exploitation.descript AS expl_name
FROM expl_selector,pond
JOIN exploitation ON pond.expl_id=exploitation.expl_id
WHERE ((pond.expl_id)::text=(expl_selector.expl_id)::text
AND expl_selector.cur_user="current_user"()::text);


DROP VIEW IF EXISTS v_edit_pool CASCADE;
CREATE VIEW v_edit_pool AS SELECT
	pool_id,
	connec_id,
	code_comercial,
	pool.the_geom,
	exploitation.descript AS expl_name
FROM expl_selector,pool
JOIN exploitation ON pool.expl_id=exploitation.expl_id
WHERE ((pool.expl_id)::text=(expl_selector.expl_id)::text
AND expl_selector.cur_user="current_user"()::text);


DROP VIEW IF EXISTS v_edit_samplepoint CASCADE;
CREATE VIEW v_edit_samplepoint AS SELECT
	sample_id,
	state,
	rotation,
	code_lab,
	element_type,
	workcat_id,
	workcat_id_end,
	street1,
	street2,
	place,
	element_code,
	cabinet,
	dma_id2,
	observations,
	samplepoint.the_geom,
	exploitation.descript AS expl_name
FROM expl_selector,samplepoint
JOIN exploitation ON samplepoint.expl_id=exploitation.expl_id
WHERE ((samplepoint.expl_id)::text=(expl_selector.expl_id)::text
AND expl_selector.cur_user="current_user"()::text);


DROP VIEW IF EXISTS v_edit_element CASCADE;
CREATE VIEW v_edit_element AS SELECT
	element_id,
	elementcat_id,
	state,
	annotation,
	observ,
	comment,
	location_type,
	workcat_id,
	buildercat_id,
	builtdate,
	ownercat_id,
	enddate,
	rotation,
	link,
	verified,
	workcat_id_end,
	code,
	element.the_geom,
	exploitation.descript AS expl_name
FROM expl_selector,element
JOIN exploitation ON element.expl_id=exploitation.expl_id
WHERE ((element.expl_id)::text=(expl_selector.expl_id)::text
AND expl_selector.cur_user="current_user"()::text);


DROP VIEW IF EXISTS v_edit_review_node CASCADE;
CREATE VIEW v_edit_review_node AS 
 SELECT node.node_id,
    node.top_elev,
    node.ymax,
    review_audit_node.top_elev AS cota_tapa,
    review_audit_node.ymax AS profunditat,
    review_audit_node.annotation,
	review_audit_node.moved_geom,
    review_audit_node.office_checked,
	node.the_geom	
   FROM node
     JOIN review_audit_node ON node.node_id::text = review_audit_node.node_id::text
  WHERE review_audit_node.field_checked IS TRUE AND review_audit_node.office_checked IS NOT TRUE;
  
DROP VIEW IF EXISTS v_edit_review_arc CASCADE;
  CREATE VIEW v_edit_review_arc AS 
 SELECT arc.arc_id,
	arc.arccat_id,
    arc.y1,
    arc.y2,
    review_audit_arc.arccat_id AS seccio,
    review_audit_arc.y1 AS sonda_ini,
	review_audit_arc.y2 AS sonda_fi,
	review_audit_arc.annotation,
	review_audit_arc.moved_geom,
    review_audit_arc.office_checked,
	arc.the_geom
   FROM arc
     JOIN review_audit_arc ON arc.arc_id::text = review_audit_arc.arc_id::text
  WHERE review_audit_arc.field_checked IS TRUE AND review_audit_arc.office_checked IS NOT TRUE;