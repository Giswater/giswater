/*
This file is part of Giswater
The program is free software: you can redistribute it and/or modify it under the terms of the GNU General Public License as published by the Free Software Foundation, either version 3 of the License, or (at your option) any later version.
This version of Giswater is provided by Giswater Association
*/

--------------------------------------------------------------------------------------------------
-- The purpose of this file is to be a storage of scripts in order to allow the possibility of update epaswmm schemas created in older version from this version to newest version
-- Changes incorporated in version 1.1.101 - 09/09/2014 
--------------------------------------------------------------------------------------------------

-- ------------------------------------------------------------
-- Incorporation of hydrology catalog
-- Views, tables & fields needed
-- ------------------------------------------------------------

-- ----------------------------
-- Table structure for cat_hydrology
-- ----------------------------

CREATE TABLE "SCHEMA_NAME"."cat_hydrology" (
"id" varchar(20) COLLATE "default" NOT NULL,
"infiltration" varchar(20) COLLATE "default",
"descript" varchar(255) COLLATE "default",
CONSTRAINT "cat_hydrology_pkey" PRIMARY KEY ("id"),
CONSTRAINT "cat_hydrology_infiltration_fkey" FOREIGN KEY ("infiltration") REFERENCES "SCHEMA_NAME"."inp_value_options_in" ("id") ON DELETE RESTRICT ON UPDATE CASCADE
)
WITH (OIDS=FALSE)
;

-- ----------------------------
-- Table structure for hydrology_selection
-- ----------------------------

CREATE TABLE "SCHEMA_NAME"."hydrology_selection" (
"hydrology_id" varchar(20) COLLATE "default" NOT NULL,
CONSTRAINT "hydrology_selection_pkey" PRIMARY KEY ("hydrology_id")
)
WITH (OIDS=FALSE)
;

-- ----------------------------
-- View structure for v_inp_options
-- ----------------------------

DROP VIEW "SCHEMA_NAME"."v_inp_options";
CREATE VIEW "SCHEMA_NAME"."v_inp_options" AS 
 SELECT inp_options.flow_units,
    cat_hydrology.infiltration,
    inp_options.flow_routing,
    inp_options.link_offsets,
    inp_options.force_main_equation,
    inp_options.ignore_rainfall,
    inp_options.ignore_snowmelt,
    inp_options.ignore_groundwater,
    inp_options.ignore_routing,
    inp_options.ignore_quality,
    inp_options.skip_steady_state,
    inp_options.start_date,
    inp_options.start_time,
    inp_options.end_date,
    inp_options.end_time,
    inp_options.report_start_date,
    inp_options.report_start_time,
    inp_options.sweep_start,
    inp_options.sweep_end,
    inp_options.dry_days,
    inp_options.report_step,
    inp_options.wet_step,
    inp_options.dry_step,
    inp_options.routing_step,
    inp_options.lengthening_step,
    inp_options.variable_step,
    inp_options.inertial_damping,
    inp_options.normal_flow_limited,
    inp_options.min_surfarea,
    inp_options.min_slope,
    inp_options.allow_ponding,
    inp_options.tempdir
   FROM SCHEMA_NAME.inp_options,
    (SCHEMA_NAME.hydrology_selection
   JOIN SCHEMA_NAME.cat_hydrology ON (((hydrology_selection.hydrology_id)::text = (cat_hydrology.id)::text)));

-- ----------------------------
-- Table structure modification for subcatchment
-- ----------------------------
   
ALTER TABLE "SCHEMA_NAME"."subcatchment"
ADD COLUMN "hydrology_id" varchar(20),
ADD FOREIGN KEY ("hydrology_id") REFERENCES "SCHEMA_NAME"."cat_hydrology" ("id") ON DELETE RESTRICT ON UPDATE CASCADE;


-- ----------------------------
-- View structure for v_inp_infiltration_cu
-- ----------------------------

DROP VIEW "SCHEMA_NAME"."v_inp_infiltration_cu";
CREATE VIEW "SCHEMA_NAME"."v_inp_infiltration_cu" AS 
 SELECT subcatchment.subc_id,subcatchment.curveno,subcatchment.conduct_2,subcatchment.drytime_2,sector_selection.sector_id,cat_hydrology.infiltration
   FROM (((SCHEMA_NAME.subcatchment
   JOIN SCHEMA_NAME.sector_selection ON (((subcatchment.sector_id)::text = (sector_selection.sector_id)::text)))
   JOIN SCHEMA_NAME.cat_hydrology ON (((subcatchment.hydrology_id)::text = (cat_hydrology.id)::text)))
   JOIN SCHEMA_NAME.hydrology_selection ON (((subcatchment.hydrology_id)::text = (hydrology_selection.hydrology_id)::text)))
  WHERE ((cat_hydrology.infiltration)::text = 'CURVE_NUMBER'::text);

  -- ----------------------------
-- View structure for v_inp_infiltration_gr
-- ----------------------------

DROP VIEW "SCHEMA_NAME"."v_inp_infiltration_gr";
CREATE VIEW "SCHEMA_NAME"."v_inp_infiltration_gr" AS 
SELECT subcatchment.subc_id, subcatchment.suction, subcatchment.conduct, subcatchment.initdef, sector_selection.sector_id, cat_hydrology.infiltration 
FROM (SCHEMA_NAME.subcatchment 
JOIN SCHEMA_NAME.sector_selection ON (((subcatchment.sector_id)::text = (sector_selection.sector_id)::text))
JOIN SCHEMA_NAME.cat_hydrology ON (((subcatchment.hydrology_id)::text = (cat_hydrology.id)::text))
JOIN SCHEMA_NAME.hydrology_selection ON (((subcatchment.hydrology_id)::text = (hydrology_selection.hydrology_id)::text))) 
WHERE ((cat_hydrology.infiltration)::text = 'GREEN_AMPT'::text);

-- ----------------------------
-- View structure for v_inp_infiltration_ho
-- ----------------------------

DROP VIEW "SCHEMA_NAME"."v_inp_infiltration_ho";
CREATE VIEW "SCHEMA_NAME"."v_inp_infiltration_ho" AS 
SELECT subcatchment.subc_id, subcatchment.maxrate, subcatchment.minrate, subcatchment.decay, subcatchment.drytime, subcatchment.maxinfil, sector_selection.sector_id, cat_hydrology.infiltration 
FROM (SCHEMA_NAME.subcatchment 
JOIN SCHEMA_NAME.sector_selection ON (((subcatchment.sector_id)::text = (sector_selection.sector_id)::text))
JOIN SCHEMA_NAME.cat_hydrology ON (((subcatchment.hydrology_id)::text = (cat_hydrology.id)::text))
JOIN SCHEMA_NAME.hydrology_selection ON (((subcatchment.hydrology_id)::text = (hydrology_selection.hydrology_id)::text)))
WHERE ((cat_hydrology.infiltration)::text = 'HORTON'::text);


-- ----------------------------
-- Default value of hydrology records
-- ----------------------------

CREATE OR REPLACE FUNCTION SCHEMA_NAME.update_infiltration() RETURNS INT AS $$
DECLARE 
	optionsRecord Record;
 BEGIN 
	SELECT * INTO optionsRecord FROM "SCHEMA_NAME".inp_options LIMIT 1;
	INSERT INTO "SCHEMA_NAME"."cat_hydrology" VALUES ('hc_default', optionsRecord.infiltration, 'Default value of infiltration');
RETURN 1;
END; 
$$ LANGUAGE plpgsql;

SELECT "SCHEMA_NAME".update_infiltration();
DROP FUNCTION "SCHEMA_NAME".update_infiltration();

UPDATE "SCHEMA_NAME"."subcatchment" SET "hydrology_id"='hc_default';
INSERT INTO "SCHEMA_NAME"."hydrology_selection" VALUES ('hc_default');

ALTER TABLE "SCHEMA_NAME"."inp_options" DROP COLUMN "infiltration";




-- ------------------------------------------------------------
-- 	1D/2D model
-- Fields and views needed
-- ------------------------------------------------------------

-- ----------------------------
-- Structure modification of rpt_subcathrunoff_sum
-- ----------------------------

ALTER TABLE "SCHEMA_NAME"."rpt_subcathrunoff_sum"
ADD COLUMN "hmax" numeric(12,4),
ADD COLUMN "vxmax" numeric(12,4),
ADD COLUMN "vymax" numeric(12,4);

-- ----------------------------
-- View structure for v_rpt_subcatchrunoff_sum
-- ----------------------------

DROP VIEW "SCHEMA_NAME"."v_rpt_subcatchrunoff_sum";
CREATE VIEW "SCHEMA_NAME"."v_rpt_subcatchrunoff_sum" AS 
SELECT rpt_subcathrunoff_sum.id, rpt_subcathrunoff_sum.result_id, rpt_subcathrunoff_sum.subc_id, rpt_subcathrunoff_sum.tot_precip, rpt_subcathrunoff_sum.tot_runon, rpt_subcathrunoff_sum.tot_evap, rpt_subcathrunoff_sum.tot_infil, rpt_subcathrunoff_sum.tot_runoff, rpt_subcathrunoff_sum.tot_runofl, rpt_subcathrunoff_sum.peak_runof, rpt_subcathrunoff_sum.runoff_coe,  rpt_subcathrunoff_sum.hmax,  rpt_subcathrunoff_sum.vxmax,  rpt_subcathrunoff_sum.vymax, subcatchment.sector_id, subcatchment.the_geom FROM ((SCHEMA_NAME.result_selection JOIN SCHEMA_NAME.rpt_subcathrunoff_sum ON (((result_selection.result_id)::text = (rpt_subcathrunoff_sum.result_id)::text))) JOIN SCHEMA_NAME.subcatchment ON (((rpt_subcathrunoff_sum.subc_id)::text = (subcatchment.subc_id)::text)));




-- ------------------------------------------------------------
-- i18n - Internationalitzation - BRAZIL
-- Views for calculate 'tensao trativa'
-- ------------------------------------------------------------

-- ----------------------------
-- View structure for i18n_br_v_arc_x_n1
-- ----------------------------

CREATE VIEW "SCHEMA_NAME"."i18n_br_v_arc_x_n1" AS 
 SELECT arc.arc_id,
    node.node_id,
    node.top_elev,
    node.ymax,
    arc.z1
   FROM (SCHEMA_NAME.arc
   JOIN SCHEMA_NAME.node ON (((arc.node_1)::text = (node.node_id)::text)));

-- ----------------------------
-- View structure for i18n_br_v_arc_x_n2
-- ----------------------------
  
CREATE VIEW "SCHEMA_NAME"."i18n_br_v_arc_x_n2" AS 
 SELECT arc.arc_id,
    node.node_id,
    node.top_elev,
    node.ymax,
    arc.z2
   FROM (SCHEMA_NAME.arc
   JOIN SCHEMA_NAME.node ON (((arc.node_2)::text = (node.node_id)::text)));
   
-- ----------------------------
-- View structure for i18n_br_v_arc_x_node
-- ----------------------------

CREATE VIEW "SCHEMA_NAME"."i18n_br_v_arc_x_node" AS 
 SELECT i18n_br_v_arc_x_n1.arc_id,
    i18n_br_v_arc_x_n1.node_id AS node1,
    i18n_br_v_arc_x_n1.top_elev AS top_elev1,
    i18n_br_v_arc_x_n1.ymax AS ymax1,
    i18n_br_v_arc_x_n1.z1,
    i18n_br_v_arc_x_n2.node_id AS node2,
    i18n_br_v_arc_x_n2.top_elev AS top_elev2,
    i18n_br_v_arc_x_n2.ymax AS ymax2,
    i18n_br_v_arc_x_n2.z2,
    ((((i18n_br_v_arc_x_n1.top_elev - i18n_br_v_arc_x_n1.ymax) + i18n_br_v_arc_x_n1.z1) - ((i18n_br_v_arc_x_n2.top_elev - i18n_br_v_arc_x_n2.ymax) + i18n_br_v_arc_x_n2.z2)) / (st_length2d(arc.the_geom))::numeric(16,3)) AS slope,
    arc.the_geom
   FROM ((SCHEMA_NAME.i18n_br_v_arc_x_n1
   JOIN SCHEMA_NAME.i18n_br_v_arc_x_n2 ON (((i18n_br_v_arc_x_n2.arc_id)::text = (i18n_br_v_arc_x_n1.arc_id)::text)))
   JOIN SCHEMA_NAME.arc ON (((arc.arc_id)::text = (i18n_br_v_arc_x_n2.arc_id)::text)));

-- ----------------------------
-- View structure for i18n_br_v_rpt_nbr
-- ----------------------------

CREATE VIEW "SCHEMA_NAME"."i18n_br_v_rpt_nbr" AS 
 SELECT i18n_br_v_arc_x_node.arc_id,
    i18n_br_v_arc_x_node.slope,
    rpt_arcflow_sum.max_flow,
    rpt_arcflow_sum.max_veloc,
    rpt_arcflow_sum.mfull_dept,
    (i18n_br_v_arc_x_node.slope * (((rpt_arcflow_sum.max_veloc * cat_mat.n) / (i18n_br_v_arc_x_node.slope ^ 0.5)) ^ ((3 / 2))::numeric)) AS t_trativa
   FROM (((SCHEMA_NAME.i18n_br_v_arc_x_node
   JOIN SCHEMA_NAME.rpt_arcflow_sum ON (((rpt_arcflow_sum.arc_id)::text = (i18n_br_v_arc_x_node.arc_id)::text)))
   JOIN SCHEMA_NAME.arc ON (((arc.arc_id)::text = (rpt_arcflow_sum.arc_id)::text)))
   JOIN SCHEMA_NAME.cat_mat ON (((arc.matcat_id)::text = (cat_mat.id)::text)));


