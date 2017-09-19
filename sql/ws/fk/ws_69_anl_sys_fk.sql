﻿/*
This file is part of Giswater 3
The program is free software: you can redistribute it and/or modify it under the terms of the GNU General Public License as published by the Free Software Foundation, either version 3 of the License, or (at your option) any later version.
This version of Giswater is provided by Giswater Association
*/

SET search_path = "SCHEMA_NAME", public, pg_catalog;


-- ----------------------------
-- MINCUT CATALOG
-- ----------------------------
ALTER TABLE "anl_mincut_result_node" DROP CONSTRAINT IF EXISTS "anl_mincut_result_node_node_id_fkey";
ALTER TABLE "anl_mincut_result_arc" DROP CONSTRAINT IF EXISTS "anl_mincut_result_arc_arc_id_fkey";
ALTER TABLE "anl_mincut_result_connec" DROP CONSTRAINT IF EXISTS "anl_mincut_result_connec_connec_id_fkey";
ALTER TABLE "anl_mincut_result_valve" DROP CONSTRAINT IF EXISTS "anl_mincut_result_valve_node_id_fkey";
ALTER TABLE "anl_mincut_result_hydrometer" DROP CONSTRAINT IF EXISTS "anl_mincut_result_hydrometer_hydrometer_id_fkey";
ALTER TABLE "anl_mincut_result_polygon" DROP CONSTRAINT IF EXISTS "anl_mincut_result_polygon_result_id_fkey";
ALTER TABLE "anl_mincut_result_node" DROP CONSTRAINT IF EXISTS "anl_mincut_result_node_result_id_fkey";
ALTER TABLE "anl_mincut_result_arc" DROP CONSTRAINT IF EXISTS "anl_mincut_result_arc_result_id_fkey";
ALTER TABLE "anl_mincut_result_connec" DROP CONSTRAINT IF EXISTS "anl_mincut_result_connec_result_id_fkey";
ALTER TABLE "anl_mincut_result_valve" DROP CONSTRAINT IF EXISTS "anl_mincut_result_valve_result_id_fkey";
ALTER TABLE "anl_mincut_result_hydrometer" DROP CONSTRAINT IF EXISTS "anl_mincut_result_hydrometer_result_id_fkey";
ALTER TABLE "anl_mincut_result_selector" DROP CONSTRAINT IF EXISTS "anl_mincut_result_selector_id_fkey";
ALTER TABLE anl_mincut_result_valve DROP CONSTRAINT IF EXISTS "anl_mincut_result_valve_status_type_fkey";
ALTER TABLE anl_mincut_result_cat DROP CONSTRAINT IF EXISTS "anl_mincut_result_cat_cause_anl_cause_fkey";
ALTER TABLE anl_mincut_result_cat DROP CONSTRAINT IF EXISTS "anl_mincut_result_cat_mincut_type_fkey";
ALTER TABLE anl_mincut_result_cat DROP CONSTRAINT IF EXISTS "anl_mincut_result_cat_mincut_state_fkey";
ALTER TABLE anl_mincut_result_cat DROP CONSTRAINT IF EXISTS "anl_mincut_result_cat_mincut_class_fkey";



ALTER TABLE "anl_mincut_result_node" ADD CONSTRAINT "anl_mincut_result_node_node_id_fkey" 
FOREIGN KEY ("node_id") REFERENCES "node" ("node_id") ON DELETE CASCADE ON UPDATE CASCADE;

ALTER TABLE "anl_mincut_result_arc" ADD CONSTRAINT "anl_mincut_result_arc_arc_id_fkey" 
FOREIGN KEY ("arc_id") REFERENCES "arc" ("arc_id") ON DELETE CASCADE ON UPDATE CASCADE;

ALTER TABLE "anl_mincut_result_connec" ADD CONSTRAINT "anl_mincut_result_connec_connec_id_fkey" 
FOREIGN KEY ("connec_id") REFERENCES "connec" ("connec_id") ON DELETE CASCADE ON UPDATE CASCADE;

ALTER TABLE "anl_mincut_result_node" ADD CONSTRAINT "anl_mincut_result_valve_node_id_fkey" 
FOREIGN KEY ("node_id") REFERENCES "node" ("node_id") ON DELETE CASCADE ON UPDATE CASCADE;


ALTER TABLE "anl_mincut_result_polygon" ADD CONSTRAINT "anl_mincut_result_polygon_result_id_fkey" 
FOREIGN KEY ("result_id") REFERENCES "anl_mincut_result_cat" ("id") ON DELETE CASCADE ON UPDATE CASCADE;

ALTER TABLE "anl_mincut_result_node" ADD CONSTRAINT "anl_mincut_result_node_result_id_fkey" 
FOREIGN KEY ("result_id") REFERENCES "anl_mincut_result_cat" ("id") ON DELETE CASCADE ON UPDATE CASCADE;

ALTER TABLE "anl_mincut_result_arc" ADD CONSTRAINT "anl_mincut_result_arc_result_id_fkey" 
FOREIGN KEY ("result_id") REFERENCES "anl_mincut_result_cat" ("id") ON DELETE CASCADE ON UPDATE CASCADE;

ALTER TABLE "anl_mincut_result_connec" ADD CONSTRAINT "anl_mincut_result_connec_result_id_fkey" 
FOREIGN KEY ("result_id") REFERENCES "anl_mincut_result_cat" ("id") ON DELETE CASCADE ON UPDATE CASCADE;

ALTER TABLE "anl_mincut_result_valve" ADD CONSTRAINT "anl_mincut_result_valve_result_id_fkey" 
FOREIGN KEY ("result_id") REFERENCES "anl_mincut_result_cat" ("id") ON DELETE CASCADE ON UPDATE CASCADE;

ALTER TABLE "anl_mincut_result_hydrometer" ADD CONSTRAINT "anl_mincut_result_hydrometer_result_id_fkey" 
FOREIGN KEY ("result_id") REFERENCES "anl_mincut_result_cat" ("id") ON DELETE CASCADE ON UPDATE CASCADE;


ALTER TABLE "anl_mincut_result_selector" ADD CONSTRAINT "anl_mincut_result_selector_id_fkey" 
FOREIGN KEY ("result_id") REFERENCES "anl_mincut_result_cat" ("id") ON DELETE CASCADE ON UPDATE CASCADE;

ALTER TABLE anl_mincut_result_cat  ADD CONSTRAINT anl_mincut_result_cat_cause_anl_cause_fkey 
FOREIGN KEY (anl_cause) REFERENCES anl_mincut_cat_cause (id) MATCH SIMPLE ON UPDATE CASCADE ON DELETE RESTRICT;

ALTER TABLE anl_mincut_result_cat  ADD CONSTRAINT anl_mincut_result_cat_mincut_type_fkey 
FOREIGN KEY (mincut_type) REFERENCES anl_mincut_cat_type (id) MATCH SIMPLE ON UPDATE CASCADE ON DELETE RESTRICT;

ALTER TABLE anl_mincut_result_cat  ADD CONSTRAINT anl_mincut_result_cat_mincut_state_fkey 
FOREIGN KEY (mincut_state) REFERENCES anl_mincut_cat_state (id) MATCH SIMPLE ON UPDATE CASCADE ON DELETE RESTRICT;

ALTER TABLE anl_mincut_result_cat  ADD CONSTRAINT anl_mincut_result_cat_mincut_class_fkey 
FOREIGN KEY (mincut_class) REFERENCES anl_mincut_cat_state (id) MATCH SIMPLE ON UPDATE CASCADE ON DELETE RESTRICT;


