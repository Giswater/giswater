/*
This file is part of Giswater 2.0
The program is free software: you can redistribute it and/or modify it under the terms of the GNU General Public License as published by the Free Software Foundation, either version 3 of the License, or (at your option) any later version.
This version of Giswater is provided by Giswater Association
*/


SET search_path = "SCHEMA_NAME", public, pg_catalog;



-------------
-- ALTER TABLES
-------------

-- ELEV STRATEGY
ALTER TABLE arc ADD COLUMN elev1 numeric(12,3);
ALTER TABLE arc ADD COLUMN elev2 numeric(12,3);
ALTER TABLE arc ADD COLUMN est_elev1 numeric(12,3);
ALTER TABLE arc ADD COLUMN est_elev2 numeric(12,3);

ALTER TABLE node ADD COLUMN elev numeric(12,3);
ALTER TABLE node ADD COLUMN est_elev numeric(12,3);


-- REHABILITACIÓ
ALTER TABLE gully ADD COLUMN connec_length numeric(12,3);
ALTER TABLE gully ADD COLUMN connec_depth numeric(12,3);




-- MORE TOPOLOGY FUNCTIONS
 
 CREATE TABLE "anl_arc_intersection"(
arc_id character varying(16),
the_geom geometry(LINESTRING,25829),
CONSTRAINT anl_arc_intersection_pkey PRIMARY KEY (arc_id)
);

CREATE TABLE anl_node_flowregulator
(
  node_id character varying(16) NOT NULL,
  the_geom geometry(Point,25829),
  CONSTRAINT anl_node_floregulator_pkey PRIMARY KEY (node_id)
);


  
  -- DWF ANALISYS
  
CREATE TABLE anl_dwf_cat_scenario(
  scenario_id character varying(30) NOT NULL,
  descript text,
  text text,
  tstamp timestamp with time zone DEFAULT now(),
  CONSTRAINT anl_dwf_cat_scenario_pkey PRIMARY KEY (scenario_id));

  
CREATE TABLE anl_dwf_connec_x_uses(
  id integer NOT NULL DEFAULT nextval('connec_x_uses_id_seq'::regclass),
  connec_id character varying(30),
  type_use character varying(30),
  m2 double precision,
  CONSTRAINT connec_x_uses_pkey PRIMARY KEY (id),
  CONSTRAINT anl_dwf_connec_x_uses_connec_id_pkey FOREIGN KEY (connec_id)
      REFERENCES connec (connec_id) MATCH SIMPLE
      ON UPDATE CASCADE ON DELETE CASCADE,
  CONSTRAINT anl_dwf_connec_x_uses_type_use_fkey FOREIGN KEY (type_use)
      REFERENCES anl_dwf_type_catastro_uses (id) MATCH SIMPLE
      ON UPDATE CASCADE ON DELETE CASCADE);


CREATE TABLE anl_dwf_connec_x_uses_value(
  id serial NOT NULL,
  scenario_id character varying(30),
  connec_id character varying(30),
  m3dia double precision,
  CONSTRAINT anl_dwf_connec_x_uses_value_pkey PRIMARY KEY (id),
  CONSTRAINT anl_dwf_connec_x_uses_value_connec_id_fkey FOREIGN KEY (connec_id)
      REFERENCES connec (connec_id) MATCH SIMPLE
      ON UPDATE CASCADE ON DELETE CASCADE,
  CONSTRAINT anl_dwf_connec_x_uses_value_scenario_id_pkey FOREIGN KEY (scenario_id)
      REFERENCES anl_dwf_cat_scenario (scenario_id) MATCH SIMPLE
      ON UPDATE CASCADE ON DELETE CASCADE);


CREATE TABLE anl_dwf_selector_scenario(
  scenario_id character varying(16) NOT NULL,
  CONSTRAINT anl_dwf_selector_scenario_pkey PRIMARY KEY (scenario_id));


CREATE TABLE anl_dwf_type_catastro_uses(
  id character varying(16) NOT NULL,
  descript text,
  water_generator boolean,
  value double precision,
  type_value character varying,
  CONSTRAINT ext_catastro_type_use2_pkey PRIMARY KEY (id));

  
CREATE TABLE anl_dwf_config_float(
  id serial NOT NULL,
  parameter text,
  value double precision,
  context text,
  descript text,
  CONSTRAINT anl_dwf_config_float_pkey PRIMARY KEY (id));
  
  
  CREATE TABLE anl_dwf_cat_result(
  result_id character varying(30) NOT NULL,
  scenario_id character varying(30),
  result_type character varying(30),
  descript text,
  text text,
  tstamp timestamp with time zone DEFAULT now(),
  CONSTRAINT anl_dwf_cat_result_pkey PRIMARY KEY (result_id))
  
  
  CREATE TABLE anl_dwf_rpt_arc(
  id bigserial NOT NULL,
  result_id character varying(16),
  arc_id character varying(50),
  r1 double precision,
  r2 double precision,
  r3 double precision,
  r4 double precision,
  r5 double precision,
  r6 double precision,
  CONSTRAINT anl_dwf_rpt_arc_pkey PRIMARY KEY (id),
  CONSTRAINT anl_dwf_rpt_arc_result_id_fkey FOREIGN KEY (result_id)
      REFERENCES anl_dwf_cat_result (result_id) MATCH SIMPLE
      ON UPDATE CASCADE ON DELETE CASCADE);



CREATE TABLE anl_dwf_rpt_node(
  id bigserial NOT NULL,
  result_id character varying(16),
  node_id character varying(50),
  r1 double precision,
  r2 double precision,
  r3 double precision,
  r4 double precision,
  r5 double precision,
  r6 double precision,
  CONSTRAINT anl_dwf_rpt_node_pkey PRIMARY KEY (id),
  CONSTRAINT anl_dwf_rpt_node_result_id_fkey FOREIGN KEY (result_id)
      REFERENCES anl_dwf_cat_result (result_id) MATCH SIMPLE
      ON UPDATE CASCADE ON DELETE CASCADE);
	  
	  
	 

ALTER TABLE "anl_dwf_connec_x_uses" DROP CONSTRAINT IF EXISTS "anl_dwf_connec_x_uses_connec_id_pkey";
ALTER TABLE "anl_dwf_connec_x_uses" ADD CONSTRAINT "anl_dwf_connec_x_uses_connec_id_pkey" FOREIGN KEY ("connec_id") REFERENCES "connec" ("connec_id") ON DELETE CASCADE ON UPDATE CASCADE;
ALTER TABLE "anl_dwf_connec_x_uses" DROP CONSTRAINT IF EXISTS "anl_dwf_connec_x_uses_type_use_fkey";
ALTER TABLE "anl_dwf_connec_x_uses" ADD CONSTRAINT "anl_dwf_connec_x_uses_type_use_fkey" FOREIGN KEY ("type_use") REFERENCES "anl_dwf_type_catastro_uses" ("id") ON DELETE CASCADE ON UPDATE CASCADE;


ALTER TABLE "anl_dwf_connec_x_uses_value" DROP CONSTRAINT IF EXISTS "anl_dwf_connec_x_uses_value_scenario_id_pkey";
ALTER TABLE "anl_dwf_connec_x_uses_value" ADD CONSTRAINT "anl_dwf_connec_x_uses_value_scenario_id_pkey" FOREIGN KEY ("scenario_id") REFERENCES "anl_dwf_cat_scenario" ("scenario_id") ON DELETE CASCADE ON UPDATE CASCADE;
ALTER TABLE "anl_dwf_connec_x_uses_value" DROP CONSTRAINT IF EXISTS "anl_dwf_connec_x_uses_value_connec_id_fkey";
ALTER TABLE "anl_dwf_connec_x_uses_value" ADD CONSTRAINT "anl_dwf_connec_x_uses_value_connec_id_fkey" FOREIGN KEY ("connec_id") REFERENCES "connec" ("connec_id") ON DELETE CASCADE ON UPDATE CASCADE;




-------------
-- INDEX
-------------

CREATE INDEX anl_arc_intersection_index   ON anl_arc_intersection   USING gist   (the_geom);
CREATE INDEX anl_node_flowregulator_index   ON anl_node_flowregulator  USING gist  (the_geom);


-------------
-- UPDATE DATA
-------------

UPDATE inp_options SET link_offsets='ELEVATION';
