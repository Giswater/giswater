/*
This file is part of Giswater
The program is free software: you can redistribute it and/or modify it under the terms of the GNU General Public License as published by the Free Software Foundation, either version 3 of the License, or (at your option) any later version.
This version of Giswater is provided by Giswater Association
*/

--------------------------------------------------------------------------------------------------
-- The purpose of this file is to be a storage of scripts in order to allow the possibility of update epanet schemas created in older version from this version to newest version
-- Changes incorporated in version 1.1.101 - 09/09/2014 
--------------------------------------------------------------------------------------------------

-- ------------------------------------------------------------
-- Quality parameters added on result node tables (bug on projects of Giswater version 1.0)
-- New field needed and views modified
-- ------------------------------------------------------------

-- ----------------------------
-- Table modified structure for rpt_result_cat
-- ----------------------------
ALTER TABLE "SCHEMA_NAME"."rpt_result_cat"
ADD COLUMN "q_timestep" numeric,
ADD COLUMN "q_tolerance" numeric;

-- ----------------------------
-- Table modified structure for rpt_node
-- ----------------------------
ALTER TABLE "SCHEMA_NAME"."rpt_node"
ADD COLUMN "quality" numeric;

-- ----------------------------
-- View structure for v_rpt_node
-- ----------------------------
DROP VIEW "SCHEMA_NAME"."v_rpt_node";
CREATE VIEW "SCHEMA_NAME"."v_rpt_node" AS 
SELECT node.node_id, result_selection.result_id, max(rpt_node.elevation) AS elevation, max(rpt_node.demand) AS max_demand, min(rpt_node.demand) AS min_demand, max(rpt_node.head) AS max_head, min(rpt_node.head) AS min_head, max(rpt_node.press) AS max_pressure, min(rpt_node.press) AS min_pressure, max(rpt_node.quality) AS max_quality, min(rpt_node.quality) AS min_quality, node.the_geom FROM ((SCHEMA_NAME.node JOIN SCHEMA_NAME.rpt_node ON (((rpt_node.node_id)::text = (node.node_id)::text))) JOIN SCHEMA_NAME.result_selection ON (((rpt_node.result_id)::text = (result_selection.result_id)::text))) GROUP BY node.node_id, result_selection.result_id, node.the_geom ORDER BY node.node_id;


