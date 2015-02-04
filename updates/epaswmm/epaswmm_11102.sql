/*
This file is part of Giswater
The program is free software: you can redistribute it and/or modify it under the terms of the GNU General Public License as published by the Free Software Foundation, either version 3 of the License, or (at your option) any later version.
This version of Giswater is provided by Giswater Association
*/

--------------------------------------------------------------------------------------------------
-- The purpose of this file is to be a storage of scripts in order to allow the possibility of update epaswmm schemas created in older version from this version to newest version
-- Changes incorporated in version 1.1.102 - 02/02/2015 
--------------------------------------------------------------------------------------------------


-- ------------------------------------------------------------
-- MODIFICATIONS OF EPA SWMM 5.1006
-- New options and new report capabilities
-- ------------------------------------------------------------

-- ----------------------------
-- View structure for v_inp_infiltration_mh
-- ----------------------------

DROP VIEW "SCHEMA_NAME"."v_inp_infiltration_mh";
CREATE VIEW "SCHEMA_NAME"."v_inp_infiltration_mh" AS 
SELECT subcatchment.subc_id, subcatchment.maxrate, subcatchment.minrate, subcatchment.decay, subcatchment.drytime, subcatchment.maxinfil, sector_selection.sector_id, cat_hydrology.infiltration 
FROM (SCHEMA_NAME.subcatchment 
JOIN SCHEMA_NAME.sector_selection ON (((subcatchment.sector_id)::text = (sector_selection.sector_id)::text))
JOIN SCHEMA_NAME.cat_hydrology ON (((subcatchment.hydrology_id)::text = (cat_hydrology.id)::text))
JOIN SCHEMA_NAME.hydrology_selection ON (((subcatchment.hydrology_id)::text = (hydrology_selection.hydrology_id)::text)))
WHERE ((cat_hydrology.infiltration)::text = 'MODIFIED_HORTON'::text);

-- ------------------------------------------------------------
-- Incorporation of 1 new columns on inp_conduit table
-- ------------------------------------------------------------

ALTER TABLE "SCHEMA_NAME"."inp_conduit"
ADD COLUMN "seepage" numeric (12,4);

DROP VIEW "SCHEMA_NAME"."v_inp_losses";
CREATE VIEW "SCHEMA_NAME"."v_inp_losses" AS 
SELECT inp_conduit.arc_id, inp_conduit.kentry, inp_conduit.kexit, inp_conduit.kavg, inp_conduit.flap, inp_conduit.seepage, sector_selection.sector_id FROM ((SCHEMA_NAME.inp_conduit JOIN SCHEMA_NAME.arc ON (((inp_conduit.arc_id)::text = (arc.arc_id)::text))) JOIN SCHEMA_NAME.sector_selection ON (((arc.sector_id)::text = (sector_selection.sector_id)::text))) WHERE ((((inp_conduit.kentry > (0)::numeric) OR (inp_conduit.kexit > (0)::numeric)) OR (inp_conduit.kavg > (0)::numeric)) OR ((inp_conduit.flap)::text = 'YES'::text)) OR ((inp_conduit.seepage) > 0::numeric);

-- ------------------------------------------------------------
-- Incorporation of 1 new columns on inp_weir
-- ------------------------------------------------------------

ALTER TABLE "SCHEMA_NAME"."inp_weir"
ADD COLUMN "surcharge" varchar (3);

DROP VIEW "SCHEMA_NAME"."v_inp_weir";
CREATE VIEW "SCHEMA_NAME"."v_inp_weir" AS 
SELECT arc.arc_id, v_inp_arc_x_node.node_1, v_inp_arc_x_node.node_2, inp_weir.weir_type, inp_weir."offset", inp_weir.cd, inp_weir.flap, inp_weir.ec, inp_weir.cd2, inp_value_weirs.shape, inp_weir.geom1, inp_weir.geom2, inp_weir.geom3, inp_weir.geom4, inp_weir.surcharge, sector_selection.sector_id FROM ((((SCHEMA_NAME.arc JOIN SCHEMA_NAME.sector_selection ON (((arc.sector_id)::text = (sector_selection.sector_id)::text))) JOIN SCHEMA_NAME.inp_weir ON (((inp_weir.arc_id)::text = (arc.arc_id)::text))) JOIN SCHEMA_NAME.inp_value_weirs ON (((inp_weir.weir_type)::text = (inp_value_weirs.id)::text))) JOIN SCHEMA_NAME.v_inp_arc_x_node ON (((v_inp_arc_x_node.arc_id)::text = (arc.arc_id)::text)));

-- ------------------------------------------------------------
-- Incorporation of 1 new columns on inp_aquifer
-- ------------------------------------------------------------

ALTER TABLE "SCHEMA_NAME"."inp_aquifer"
ADD COLUMN "pattern_id" varchar (16);


-- ------------------------------------------------------------
-- Incorporation of 1 new columns on inp_groundwater
-- ------------------------------------------------------------

ALTER TABLE "SCHEMA_NAME"."inp_groundwater"
ADD COLUMN "fl_eq_lat" varchar (50);

ALTER TABLE "SCHEMA_NAME"."inp_groundwater"
ADD COLUMN "fl_eq_deep" varchar (50);

DROP VIEW "SCHEMA_NAME"."v_inp_groundwater";
CREATE VIEW "SCHEMA_NAME"."v_inp_groundwater" AS 
SELECT inp_groundwater.subc_id, inp_groundwater.aquif_id, inp_groundwater.node_id, inp_groundwater.surfel, inp_groundwater.a1, inp_groundwater.b1, inp_groundwater.a2, inp_groundwater.b2, inp_groundwater.a3, inp_groundwater.tw, inp_groundwater.h, (('LATERAL'::text || ' '::text) || (inp_groundwater.fl_eq_lat)::text) AS fl_eq_lat, (('DEEP'::text || ' '::text) || (inp_groundwater.fl_eq_lat)::text) AS fl_eq_deep, sector_selection.sector_id FROM ((SCHEMA_NAME.subcatchment JOIN SCHEMA_NAME.inp_groundwater ON (((inp_groundwater.subc_id)::text = (subcatchment.subc_id)::text))) JOIN SCHEMA_NAME.sector_selection ON (((subcatchment.sector_id)::text = (sector_selection.sector_id)::text)));


-- ------------------------------------------------------------
-- Incorporation 1 new register into options_in
-- ------------------------------------------------------------
INSERT INTO "SCHEMA_NAME"."inp_value_options_in" VALUES ('MODIFIED_HORTON'); 


-- ------------------------------------------------------------
-- Incorporation of adjustments table
-- ------------------------------------------------------------

CREATE TABLE "SCHEMA_NAME"."inp_adjustments" (
"adj_type" varchar(16) COLLATE "default" NOT NULL,
"value_1" numeric(12,4),
"value_2" numeric(12,4),
"value_3" numeric(12,4),
"value_4" numeric(12,4),
"value_5" numeric(12,4),
"value_6" numeric(12,4),
"value_7" numeric(12,4),
"value_8" numeric(12,4),
"value_9" numeric(12,4),
"value_10" numeric(12,4),
"value_11" numeric(12,4),
"value_12" numeric(12,4))
WITH (OIDS=FALSE);

ALTER TABLE "SCHEMA_NAME"."inp_adjustments" ADD PRIMARY KEY ("adj_type");