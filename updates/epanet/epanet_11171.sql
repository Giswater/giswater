/*
This file is part of Giswater
The program is free software: you can redistribute it and/or modify it under the terms of the GNU General Public License as published by the Free Software Foundation, either version 3 of the License, or (at your option) any later version.
This version of Giswater is provided by Giswater Association
*/

--------------------------------------------------------------------------------------------------
-- The purpose of this file is to be a storage of scripts in order to allow the possibility of update epanet schemas created in older version from this version to newest version
-- Changes incorporated in version 1.1.171 - 03/03/2015 
--------------------------------------------------------------------------------------------------

-- BUG FIX on v_rpt_energy_usage
--------------------------------------------------------------------------------------------------


DROP VIEW IF EXISTS "SCHEMA_NAME"."v_rpt_energy_usage";

DROP TABLE IF EXISTS "SCHEMA_NAME"."rpt_energy_usage";
CREATE TABLE "SCHEMA_NAME"."rpt_energy_usage" (
"id" int4 DEFAULT nextval('"SCHEMA_NAME".rpt_energy_usage_id_seq'::regclass) NOT NULL,
"result_id" varchar(16) COLLATE "default" NOT NULL,
"pump_id" varchar(16),
"usage_fact" numeric,
"avg_effic" numeric,
"kwhr_mgal" numeric,
"avg_kw" numeric,
"peak_kw" numeric,
"cost_day" numeric,
CONSTRAINT "rpt_energy_usage_pkey" PRIMARY KEY ("id")
)
WITH (OIDS=FALSE);

CREATE VIEW "SCHEMA_NAME"."v_rpt_energy_usage" AS 
SELECT rpt_energy_usage.id, rpt_energy_usage.result_id, rpt_energy_usage.pump_id, rpt_energy_usage.usage_fact, rpt_energy_usage.avg_effic, rpt_energy_usage.kwhr_mgal, rpt_energy_usage.avg_kw, rpt_energy_usage.peak_kw, rpt_energy_usage.cost_day FROM (SCHEMA_NAME.result_selection JOIN SCHEMA_NAME.rpt_energy_usage ON (((result_selection.result_id)::text = (rpt_energy_usage.result_id)::text)));
