/*
This file is part of Giswater 2.0
The program is free software: you can redistribute it and/or modify it under the terms of the GNU General Public License as published by the Free Software Foundation, either version 3 of the License, or (at your option) any later version.
This version of Giswater is provided by Giswater Association
*/

SET search_path = "SCHEMA_NAME", public, pg_catalog;
-- ----------------------------
-- View structure for v_node
-- ----------------------------

DROP VIEW IF EXISTS v_edit_inp_junction CASCADE;
CREATE VIEW v_edit_inp_junction AS
SELECT 
node.node_id, node.top_elev, node.ymax, node.sander, node.top_elev-node.ymax as elev, 
node.nodecat_id, node.sector_id, node."state", node.annotation, node.observ, node.comment, node.dma_id, node.rotation, node.link, node.est_top_elev, node.est_ymax, node.verified, node.the_geom,
inp_junction.y0, inp_junction.ysur, inp_junction.apond
FROM (node
JOIN inp_junction ON (((inp_junction.node_id)::text = (node.node_id)::text)));


DROP VIEW IF EXISTS v_edit_inp_divider CASCADE;
CREATE VIEW v_edit_inp_divider AS
SELECT 
node.node_id, node.top_elev, node.ymax, node.sander, node.top_elev-node.ymax as elev,
node.nodecat_id, node.sector_id, node."state", node.annotation, node.observ, node.comment, node.dma_id, node.rotation, node.link, node.est_top_elev, node.est_ymax, node.verified, node.the_geom,
inp_divider.divider_type, inp_divider.arc_id, inp_divider.curve_id, inp_divider.qmin, inp_divider.ht, inp_divider.cd, inp_divider.y0, inp_divider.ysur, inp_divider.apond
FROM (node
JOIN inp_divider ON (((node.node_id)::text = (inp_divider.node_id)::text)));


DROP VIEW IF EXISTS v_edit_inp_outfall CASCADE;
CREATE VIEW v_edit_inp_outfall AS
SELECT 
node.node_id, node.top_elev, node.ymax, node.sander, node.top_elev-node.ymax as elev,  
node.nodecat_id, node.sector_id, node."state", node.annotation, node.observ, node.comment, node.dma_id, node.rotation, node.link, node.est_top_elev, node.est_ymax, node.verified, node.the_geom,
inp_outfall.outfall_type, inp_outfall.stage, inp_outfall.curve_id, inp_outfall.timser_id, inp_outfall.gate
FROM (node
JOIN inp_outfall ON (((node.node_id)::text = (inp_outfall.node_id)::text)));


DROP VIEW IF EXISTS v_edit_inp_storage CASCADE;
CREATE VIEW v_edit_inp_storage AS
SELECT 
node.node_id, node.top_elev, node.ymax, node.sander, node.top_elev-node.ymax as elev, 
node.nodecat_id, node.sector_id, node."state", node.annotation, node.observ, node.comment, node.dma_id, node.rotation, node.link, node.est_top_elev, node.est_ymax, node.verified, node.the_geom,
inp_storage.storage_type, inp_storage.curve_id, inp_storage.a1, inp_storage.a2, inp_storage.a0, inp_storage.fevap, inp_storage.sh, inp_storage.hc, inp_storage.imd, inp_storage.y0, inp_storage.ysur, inp_storage.apond
FROM (node
JOIN inp_storage ON (((node.node_id)::text = (inp_storage.node_id)::text)));





-- ----------------------------
-- View structure for v_arc
-- ----------------------------

DROP VIEW IF EXISTS v_edit_inp_conduit CASCADE;
CREATE VIEW v_edit_inp_conduit AS
SELECT 
arc.arc_id, arc.y1, arc.y2, arc.arccat_id, cat_arc.matcat_id AS "cat_matcat_id", cat_arc.shape AS "cat_shape",arc.sector_id, arc."state", arc.annotation, arc.observ, arc.comment, arc.dma_id, arc.custom_length, arc.rotation, arc.link, arc.est_y1, arc.est_y2, arc.verified, arc.the_geom,
inp_conduit.barrels, inp_conduit.culvert, inp_conduit.kentry, inp_conduit.kexit, inp_conduit.kavg, 
inp_conduit.flap, inp_conduit.q0, inp_conduit.qmax, inp_conduit.seepage, inp_conduit.custom_n
FROM ((arc
JOIN inp_conduit ON (((arc.arc_id)::text = (inp_conduit.arc_id)::text)))
JOIN cat_arc ON (((arc.arccat_id)::text = (cat_arc.id)::text)));

DROP VIEW IF EXISTS v_edit_inp_orifice CASCADE;
CREATE VIEW v_edit_inp_orifice AS
SELECT
arc.arc_id, arc.y1, arc.y2, arc.arccat_id, arc.sector_id, arc."state", arc.annotation, arc.observ, arc.comment, arc.dma_id, arc.custom_length, arc.rotation, arc.link, arc.est_y1, arc.est_y2, arc.verified, arc.the_geom,
inp_orifice.ori_type, inp_orifice."offset", inp_orifice.cd, inp_orifice.orate, inp_orifice.flap, inp_orifice.shape, inp_orifice.geom1, inp_orifice.geom2, inp_orifice.geom3, inp_orifice.geom4
FROM (arc
JOIN inp_orifice ON (((arc.arc_id)::text = (inp_orifice.arc_id)::text)));

DROP VIEW IF EXISTS v_edit_inp_outlet CASCADE;
CREATE VIEW v_edit_inp_outlet AS
SELECT 
arc.arc_id, arc.y1, arc.y2, arc.arccat_id, arc.sector_id, arc."state", arc.annotation, arc.observ, arc.comment, arc.dma_id, arc.custom_length, arc.rotation, arc.link, arc.est_y1, arc.est_y2, arc.verified, arc.the_geom,
inp_outlet.outlet_type, inp_outlet."offset", inp_outlet.curve_id, inp_outlet.cd1, inp_outlet.cd2, inp_outlet.flap
FROM (arc
JOIN inp_outlet ON (((arc.arc_id)::text = (inp_outlet.arc_id)::text)));

DROP VIEW IF EXISTS v_edit_inp_pump CASCADE;
CREATE VIEW v_edit_inp_pump AS
SELECT 
arc.arc_id, arc.y1, arc.y2, arc.arccat_id, arc.sector_id, arc."state", arc.annotation, arc.observ, arc.comment, arc.dma_id, arc.custom_length, arc.rotation, arc.link, arc.est_y1, arc.est_y2, arc.verified, arc.the_geom,
inp_pump.curve_id, inp_pump.status, inp_pump.startup, inp_pump.shutoff
FROM (arc
JOIN inp_pump ON (((arc.arc_id)::text = (inp_pump.arc_id)::text)));

DROP VIEW IF EXISTS v_edit_inp_weir CASCADE;
CREATE VIEW v_edit_inp_weir AS
SELECT 
arc.arc_id, arc.y1, arc.y2, arc.arccat_id, arc.sector_id, arc."state", arc.annotation, arc.observ, arc.comment, arc.dma_id, arc.custom_length, arc.rotation, arc.link, arc.est_y1, arc.est_y2, arc.verified, arc.the_geom,
inp_weir.weir_type, inp_weir."offset", inp_weir.cd, inp_weir.ec, inp_weir.cd2, inp_weir.flap, inp_weir.geom1, inp_weir.geom2, inp_weir.geom3, inp_weir.geom4, inp_weir.surcharge
FROM (arc
JOIN inp_weir ON (((arc.arc_id)::text = (inp_weir.arc_id)::text)));

  


  DROP VIEW IF EXISTS v_edit_raingage CASCADE;
CREATE VIEW v_edit_raingage AS SELECT
	rg_id,
	form_type,
	intvl,
	scf,
	rgage_type,
	timser_id,
	fname,
	sta,
	units,
	raingage.the_geom,
	exploitation.descript AS expl_name
FROM expl_selector,raingage
JOIN exploitation ON raingage.expl_id=exploitation.expl_id
WHERE ((raingage.expl_id)::text=(expl_selector.expl_id)::text
AND expl_selector.cur_user="current_user"()::text);


DROP VIEW IF EXISTS v_edit_subcatchment CASCADE;
CREATE VIEW v_edit_subcatchment AS SELECT
	subc_id,
	node_id,
	rg_id,
	area,
	imperv,
	width,
	slope,
	clength,
	snow_id,
	nimp,
	nperv,
	simp,
	sperv,
	zero,
	routeto,
	rted,
	maxrate,
	minrate,
	decay,
	drytime,
	maxinfil,
	suction,
	conduct,
	initdef,
	curveno,
	conduct_2,
	drytime_2,
	sector_id,
	hydrology_id,
	subcatchment.the_geom,
	exploitation.descript AS expl_name
FROM expl_selector,subcatchment
JOIN exploitation ON subcatchment.expl_id=exploitation.expl_id
WHERE ((subcatchment.expl_id)::text=(expl_selector.expl_id)::text
AND expl_selector.cur_user="current_user"()::text);
   