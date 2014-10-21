/*
This file is part of Giswater
The program is free software: you can redistribute it and/or modify it under the terms of the GNU General Public License as published by the Free Software Foundation, either version 3 of the License, or (at your option) any later version.
This version of Giswater is provided by Giswater Association
*/

--------------------------------------------------------------------------------------------------
-- The purpose of this file is to be a storage of scripts in order to allow the possibility of update epaswmm schemas created in older version from this version to newest version
-- Changes incorporated in version 1.1.101 - 09/09/2014 
--------------------------------------------------------------------------------------------------

-- WARNING: PGadmin message: Query executed with one row discarted. IT'S OK. The discarted row is show the result of f() update_infiltration (number one). Doesn't matter


----------------------------------------------------------------
-- FIXED BUGS
----------------------------------------------------------------

-------------------------------
-- Bugs on v_inp_usage when LID is used
-------------------------------

-- Fixed integer values of 'number' & 'toperv' for epaswmm.sql scripts of 1.0 release. Transparent for epaswmm.sql up to 1.0 release

DROP VIEW "SCHEMA_NAME"."v_inp_lidusage";
CREATE VIEW "SCHEMA_NAME"."v_inp_lidusage" AS SELECT inp_lidusage_subc_x_lidco.subc_id, inp_lidusage_subc_x_lidco.lidco_id, inp_lidusage_subc_x_lidco."number"::integer, inp_lidusage_subc_x_lidco.area, inp_lidusage_subc_x_lidco.width, inp_lidusage_subc_x_lidco.initsat, inp_lidusage_subc_x_lidco.fromimp, inp_lidusage_subc_x_lidco.toperv::integer, inp_lidusage_subc_x_lidco.rptfile, sector_selection.sector_id FROM ((SCHEMA_NAME.sector_selection JOIN SCHEMA_NAME.subcatchment ON (((subcatchment.sector_id)::text = (sector_selection.sector_id)::text))) JOIN SCHEMA_NAME.inp_lidusage_subc_x_lidco ON (((inp_lidusage_subc_x_lidco.subc_id)::text = (subcatchment.subc_id)::text)));


-------------------------------
-- Bugs on runoff quantity table when LID is used
-------------------------------

ALTER TABLE "SCHEMA_NAME"."rpt_runoff_quant"
ADD COLUMN "initlid_sto" numeric(12,4);

-- ------------------------------------------------------------
-- Incorporation of hydrology catalog
-- Views, tables & fields needed
-- ------------------------------------------------------------

-- ----------------------------
-- Table structure for cat_hydrology
-- ----------------------------

CREATE TABLE "SCHEMA_NAME"."cat_hydrology" (
"id" varchar(20) COLLATE "default" NOT NULL,
"infiltration" varchar(20) COLLATE "default" NOT NULL,
"descript" varchar(255) COLLATE "default",
CONSTRAINT "cat_hydrology_pkey" PRIMARY KEY ("id"),
CONSTRAINT "cat_hydrology_infiltration_fkey" FOREIGN KEY ("infiltration") REFERENCES "SCHEMA_NAME"."inp_value_options_in" ("id") ON DELETE RESTRICT ON UPDATE CASCADE
)
WITH (OIDS=FALSE);

-- ----------------------------
-- Table structure for hydrology_selection
-- ----------------------------

CREATE TABLE "SCHEMA_NAME"."hydrology_selection" (
"hydrology_id" varchar(20) COLLATE "default" NOT NULL,
CONSTRAINT "hydrology_selection_pkey" PRIMARY KEY ("hydrology_id")
)
WITH (OIDS=FALSE);

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
-- MODIFICATIONS OF EPA SWMM 5.1006
-- New options and new report capabilities
-- ------------------------------------------------------------

-- ------------------------------------------------------------
-- Incorporation of 4 new columns on inp_options table 
-- ------------------------------------------------------------

ALTER TABLE "SCHEMA_NAME"."inp_options"
ADD COLUMN "max_trials" numeric(12,4);
ALTER TABLE "SCHEMA_NAME"."inp_options"
ADD COLUMN "head_tolerance" numeric(12,4);
ALTER TABLE "SCHEMA_NAME"."inp_options"
ADD COLUMN "sys_flow_tol" numeric(12,4);
ALTER TABLE "SCHEMA_NAME"."inp_options"
ADD COLUMN "lat_flow_tol" numeric(12,4);

DELETE FROM "SCHEMA_NAME"."inp_options";
INSERT INTO "SCHEMA_NAME"."inp_options" VALUES ('CMS', 'DYNWAVE', 'DEPTH', 'H-W', 'NO', 'NO', 'NO', 'NO', 'NO', 'NO', '01/01/2001', '00:00:00', '01/01/2001', '05:00:00', '01/01/2001', '00:00:00', '01/01', '12/31', '10', '00:15:00', '00:05:00', '01:00:00', '00:00:02', null, null, 'NONE', 'BOTH', '0', '0', 'YES', null, '0','0','5','5');


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
    inp_options.tempdir,
	inp_options.max_trials,
	inp_options.head_tolerance,
	inp_options.sys_flow_tol,
	inp_options.lat_flow_tol
	
   FROM SCHEMA_NAME.inp_options,
    (SCHEMA_NAME.hydrology_selection
   JOIN SCHEMA_NAME.cat_hydrology ON (((hydrology_selection.hydrology_id)::text = (cat_hydrology.id)::text)));


-- ------------------------------------------------------------
-- Incorporation of 3 new columns on rpt_result_cat
-- ------------------------------------------------------------

ALTER TABLE "SCHEMA_NAME"."rpt_result_cat"
ADD COLUMN "var_time_step" varchar(3);
ALTER TABLE "SCHEMA_NAME"."rpt_result_cat"
ADD COLUMN "max_trials" numeric(4,2);
ALTER TABLE "SCHEMA_NAME"."rpt_result_cat"
ADD COLUMN "head_tolerance" varchar(12);



-- ------------------------------------------------------------
-- Incorporation of 6 new columns on rpt_arcflow_sum table 
-- ------------------------------------------------------------

ALTER TABLE "SCHEMA_NAME"."rpt_arcflow_sum"
ADD COLUMN "max_shear" numeric(12,4);
ALTER TABLE "SCHEMA_NAME"."rpt_arcflow_sum"
ADD COLUMN "day_max" varchar(10);
ALTER TABLE "SCHEMA_NAME"."rpt_arcflow_sum"
ADD COLUMN "time_max" varchar(10);
ALTER TABLE "SCHEMA_NAME"."rpt_arcflow_sum"
ADD COLUMN "min_shear" numeric(12,4);
ALTER TABLE "SCHEMA_NAME"."rpt_arcflow_sum"
ADD COLUMN "day_min" varchar(10);
ALTER TABLE "SCHEMA_NAME"."rpt_arcflow_sum"
ADD COLUMN "time_min" varchar(10);


-- ----------------------------
-- View structure for v_rpt_arcflow_sum
-- ----------------------------
DROP VIEW "SCHEMA_NAME"."v_rpt_arcflow_sum";
CREATE VIEW "SCHEMA_NAME"."v_rpt_arcflow_sum" AS 
SELECT rpt_arcflow_sum.id, result_selection.result_id, rpt_arcflow_sum.arc_id, rpt_arcflow_sum.arc_type, rpt_arcflow_sum.max_flow, rpt_arcflow_sum.time_days, rpt_arcflow_sum.time_hour, rpt_arcflow_sum.max_veloc, rpt_arcflow_sum.mfull_flow, rpt_arcflow_sum.mfull_dept, rpt_arcflow_sum.max_shear, rpt_arcflow_sum.day_max, rpt_arcflow_sum.time_max, rpt_arcflow_sum.min_shear,rpt_arcflow_sum.day_min, rpt_arcflow_sum.time_min, arc.sector_id, arc.the_geom FROM ((SCHEMA_NAME.arc JOIN SCHEMA_NAME.rpt_arcflow_sum ON ((((rpt_arcflow_sum.arc_id)::text = (arc.arc_id)::text) AND ((arc.arc_id)::text = (rpt_arcflow_sum.arc_id)::text)))) JOIN SCHEMA_NAME.result_selection ON (((rpt_arcflow_sum.result_id)::text = (result_selection.result_id)::text)));


-- ------------------------------------------------------------
-- Incorporation of 2 new columns on rpt_flowrouting_cont
-- ------------------------------------------------------------

ALTER TABLE "SCHEMA_NAME"."rpt_flowrouting_cont"
ADD COLUMN "evap_losses" numeric(6,4);
ALTER TABLE "SCHEMA_NAME"."rpt_flowrouting_cont"
ADD COLUMN "seepage_losses" numeric(6,4);

-- ----------------------------
-- View structure for v_rpt_flowrouting_cont
-- ----------------------------
DROP VIEW "SCHEMA_NAME"."v_rpt_flowrouting_cont";
CREATE VIEW "SCHEMA_NAME"."v_rpt_flowrouting_cont" AS 
SELECT rpt_flowrouting_cont.id, rpt_flowrouting_cont.result_id, rpt_flowrouting_cont.dryw_inf, rpt_flowrouting_cont.wetw_inf, rpt_flowrouting_cont.ground_inf, rpt_flowrouting_cont.rdii_inf, rpt_flowrouting_cont.ext_inf, rpt_flowrouting_cont.ext_out, rpt_flowrouting_cont.int_out, rpt_flowrouting_cont.evap_losses, rpt_flowrouting_cont.seepage_losses, rpt_flowrouting_cont.stor_loss, rpt_flowrouting_cont.initst_vol, rpt_flowrouting_cont.finst_vol, rpt_flowrouting_cont.cont_error FROM (SCHEMA_NAME.result_selection JOIN SCHEMA_NAME.rpt_flowrouting_cont ON (((result_selection.result_id)::text = (rpt_flowrouting_cont.result_id)::text)));


-- ------------------------------------------------------------
-- Incorporation of 1 new column on rpt_nodeinflow_sum
-- ------------------------------------------------------------

ALTER TABLE "SCHEMA_NAME"."rpt_nodeinflow_sum"
ADD COLUMN "flow_balance_error" numeric(6,4);


-- ----------------------------
-- View structure for v_rpt_nodeinflow_sum
-- ----------------------------
DROP VIEW "SCHEMA_NAME"."v_rpt_nodeinflow_sum";
CREATE VIEW "SCHEMA_NAME"."v_rpt_nodeinflow_sum" AS 
SELECT rpt_nodeinflow_sum.id, rpt_nodeinflow_sum.result_id, rpt_nodeinflow_sum.node_id, rpt_nodeinflow_sum.swnod_type, rpt_nodeinflow_sum.max_latinf, rpt_nodeinflow_sum.max_totinf, rpt_nodeinflow_sum.time_days, rpt_nodeinflow_sum.time_hour, rpt_nodeinflow_sum.latinf_vol, rpt_nodeinflow_sum.totinf_vol, rpt_nodeinflow_sum.flow_balance_error, node.sector_id, node.the_geom FROM ((SCHEMA_NAME.result_selection JOIN SCHEMA_NAME.rpt_nodeinflow_sum ON (((result_selection.result_id)::text = (rpt_nodeinflow_sum.result_id)::text))) JOIN SCHEMA_NAME.node ON (((rpt_nodeinflow_sum.node_id)::text = (node.node_id)::text)));


-- ----------------------------
-- Sequence structure for rpt_arcpolload_sum_id_seq
-- --------------------------

CREATE SEQUENCE "SCHEMA_NAME"."rpt_arcpolload_sum_id_seq"
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;

-- ----------------------------
-- Table structure for rpt_arcpolload_sum
-- ----------------------------
CREATE TABLE "SCHEMA_NAME"."rpt_arcpolload_sum" (
"id" int4 DEFAULT nextval('"SCHEMA_NAME".rpt_arcpolload_sum_id_seq'::regclass) NOT NULL,
"result_id" varchar(16) COLLATE "default",
"arc_id" varchar(16) COLLATE "default",
"poll_id" varchar(16) COLLATE "default"
)
WITH (OIDS=FALSE);


-- ----------------------------
-- View structure for v_rpt_arcpolload_sum
-- ----------------------------
CREATE VIEW "SCHEMA_NAME"."v_rpt_arcpolload_sum" AS 
SELECT rpt_arcpolload_sum.id, rpt_arcpolload_sum.result_id, rpt_arcpolload_sum.arc_id, rpt_arcpolload_sum.poll_id, arc.sector_id, arc.the_geom FROM ("SCHEMA_NAME".result_selection JOIN "SCHEMA_NAME".rpt_arcpolload_sum ON (((result_selection.result_id)::text = (rpt_arcpolload_sum.result_id)::text))) JOIN "SCHEMA_NAME".arc ON (((rpt_arcpolload_sum.arc_id)::text = (arc.arc_id)::text));


-- ------------------------------------------------------------
-- MODIFICATIONS OF EPA SWMM 5.1006 2D
-- New fields and views needed
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
SELECT rpt_subcathrunoff_sum.id, rpt_subcathrunoff_sum.result_id, rpt_subcathrunoff_sum.subc_id, rpt_subcathrunoff_sum.tot_precip, rpt_subcathrunoff_sum.tot_runon, rpt_subcathrunoff_sum.tot_evap, rpt_subcathrunoff_sum.tot_infil, rpt_subcathrunoff_sum.tot_runoff, rpt_subcathrunoff_sum.tot_runofl, rpt_subcathrunoff_sum.peak_runof, rpt_subcathrunoff_sum.runoff_coe,  rpt_subcathrunoff_sum.hmax,  rpt_subcathrunoff_sum.vxmax,  rpt_subcathrunoff_sum.vymax, subcatchment.sector_id, subcatchment.the_geom FROM (("SCHEMA_NAME".result_selection JOIN "SCHEMA_NAME".rpt_subcathrunoff_sum ON (((result_selection.result_id)::text = (rpt_subcathrunoff_sum.result_id)::text))) JOIN "SCHEMA_NAME".subcatchment ON (((rpt_subcathrunoff_sum.subc_id)::text = (subcatchment.subc_id)::text)));


-- ------------------------------------------------------------
-- Clone
-- schema
-- ------------------------------------------------------------

CREATE OR REPLACE FUNCTION "SCHEMA_NAME".clone_schema(source_schema text, dest_schema text) RETURNS void AS
$$
 
DECLARE
	rec_view record;
	rec_fk record;
	rec_table text;
	tablename text;
	default_ text;
	column_ text;
	msg text;
BEGIN

	-- Create destination schema
	EXECUTE 'CREATE SCHEMA ' || dest_schema ;
	 
	-- Sequences
	FOR rec_table IN
		SELECT sequence_name FROM information_schema.SEQUENCES WHERE sequence_schema = source_schema
	LOOP
		EXECUTE 'CREATE SEQUENCE ' || dest_schema || '.' || rec_table;
	END LOOP;
	 
	-- Tables
	FOR rec_table IN
		SELECT table_name FROM information_schema.TABLES WHERE table_schema = source_schema AND table_type = 'BASE TABLE' ORDER BY table_name
	LOOP
	  
	  	-- Create table in destination schema
		tablename := dest_schema || '.' || rec_table;
		EXECUTE 'CREATE TABLE ' || tablename || ' (LIKE ' || source_schema || '.' || rec_table || ' INCLUDING CONSTRAINTS INCLUDING INDEXES INCLUDING DEFAULTS)';
		
		-- Set contraints
		FOR column_, default_ IN
			SELECT column_name, REPLACE(column_default, source_schema, dest_schema) 
			FROM information_schema.COLUMNS 
			WHERE table_schema = dest_schema AND table_name = rec_table AND column_default LIKE 'nextval(%' || source_schema || '%::regclass)'
		LOOP
			EXECUTE 'ALTER TABLE ' || tablename || ' ALTER COLUMN ' || column_ || ' SET DEFAULT ' || default_;
		END LOOP;
		
		-- Copy table contents to destination schema
		EXECUTE 'INSERT INTO ' || tablename || ' SELECT * FROM ' || source_schema || '.' || rec_table; 	
		
	END LOOP;
	  
	-- Loop again trough tables in order to set Foreign Keys
	FOR rec_table IN
		SELECT table_name FROM information_schema.TABLES WHERE table_schema = source_schema AND table_type = 'BASE TABLE' ORDER BY table_name
	LOOP	  
	  
		tablename := dest_schema || '.' || rec_table;	  
		FOR rec_fk IN
			SELECT tc.constraint_name, tc.constraint_schema, tc.table_name, kcu.column_name,
			ccu.table_name AS parent_table, ccu.column_name AS parent_column,
			rc.update_rule AS on_update, rc.delete_rule AS on_delete
			FROM information_schema.table_constraints tc
				LEFT JOIN information_schema.key_column_usage kcu
				ON tc.constraint_catalog = kcu.constraint_catalog
				AND tc.constraint_schema = kcu.constraint_schema
				AND tc.constraint_name = kcu.constraint_name
			LEFT JOIN information_schema.referential_constraints rc
				ON tc.constraint_catalog = rc.constraint_catalog
				AND tc.constraint_schema = rc.constraint_schema
				AND tc.constraint_name = rc.constraint_name
			LEFT JOIN information_schema.constraint_column_usage ccu
				ON rc.unique_constraint_catalog = ccu.constraint_catalog
				AND rc.unique_constraint_schema = ccu.constraint_schema
				AND rc.unique_constraint_name = ccu.constraint_name
			WHERE tc.constraint_type = 'FOREIGN KEY' AND tc.constraint_schema = source_schema AND tc.table_name = rec_table
		LOOP
			msg:= 'ALTER TABLE '||tablename||' ADD CONSTRAINT '||rec_fk.constraint_name||' FOREIGN KEY('||rec_fk.column_name||') 
				REFERENCES '||dest_schema||'.'||rec_fk.parent_table||'('||rec_fk.parent_column||') ON DELETE '||rec_fk.on_delete||' ON UPDATE '||rec_fk.on_update;
			EXECUTE msg;
		END LOOP;		
		
	END LOOP;			
		
	-- Views
	FOR rec_view IN
		SELECT table_name, REPLACE(view_definition, source_schema, dest_schema) as definition FROM information_schema.VIEWS WHERE table_schema = source_schema
	LOOP
		EXECUTE 'CREATE VIEW ' || dest_schema || '.' || rec_view.table_name || ' AS ' || rec_view.definition;
	END LOOP;
 
END;
 
$$ LANGUAGE plpgsql VOLATILE;