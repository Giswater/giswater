/*
This file is part of Giswater 2.0
The program is free software: you can redistribute it and/or modify it under the terms of the GNU General Public License as published by the Free Software Foundation, either version 3 of the License, or (at your option) any later version.
This version of Giswater is provided by Giswater Association
*/


SET search_path = "SCHEMA_NAME", public, pg_catalog;


CREATE SEQUENCE doc_x_tag_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;

	
CREATE SEQUENCE pol_id_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;
  
  
CREATE SEQUENCE pond_id_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;
	
CREATE SEQUENCE pool_id_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;

CREATE SEQUENCE polygon_id_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;

  
CREATE TABLE polygon(
  pol_id character varying(16) NOT NULL PRIMARY KEY,
  text character varying(254),
  the_geom geometry(POLYGON,SRID_VALUE),
  undelete boolean
);


CREATE TABLE macrodma(
macrodma_id character varying(50) NOT NULL PRIMARY KEY,
descript character varying(100),
the_geom geometry(POLYGON,SRID_VALUE),
undelete boolean,
expl_id integer
);


CREATE TABLE doc_x_tag(
  id bigint NOT NULL DEFAULT nextval('doc_x_tag_seq'::regclass) PRIMARY KEY,
  doc_id character varying(30),
  tag_id character varying(16)
);


CREATE TABLE cat_feature
(
  id character varying(50) NOT NULL,
  feature_type character varying(18),
  CONSTRAINT cat_feature_pkey PRIMARY KEY (id)
);

-- ----------------------------
-- EXPLOTITATION STRATEGY
-- ----------------------------


CREATE TABLE exploitation(
expl_id integer  NOT NULL PRIMARY KEY,
short_descript character varying(50) NOT NULL,
descript character varying(100),
the_geom geometry(POLYGON,SRID_VALUE),
undelete boolean
);


CREATE TABLE expl_selector (
expl_id integer NOT NULL PRIMARY KEY,
cur_user text
);

ALTER TABLE ext_streetaxis ADD COLUMN expl_id integer;

-- ANALYSIS

DROP TABLE IF EXISTS anl_node_topological_consistency CASCADE;
CREATE TABLE anl_node_topological_consistency (
  node_id character varying(16) NOT NULL,
  node_type character varying(30),
  num_arcs integer,  
  the_geom geometry(Point,SRID_VALUE),
  CONSTRAINT anl_node_topological_consistency_pkey PRIMARY KEY (node_id)
);



DROP TABLE IF EXISTS anl_node_geometric_consistency CASCADE;
CREATE TABLE anl_node_geometric_consistency(
  node_id character varying(16) NOT NULL,
  node_type character varying(30),
  the_geom geometry(Point,SRID_VALUE),
  CONSTRAINT anl_node_topological_geometric_pkey PRIMARY KEY (node_id)
);


-- mincut

CREATE TABLE "anl_mincut_result_cat_cause" (
id varchar(30) NOT NULL,
descript text,
CONSTRAINT mincut_result_cat_cause_pkey PRIMARY KEY (id)
);


CREATE TABLE "anl_mincut_cat_status_type"(
  id smallint NOT NULL,
  descript text,
  CONSTRAINT mincut_cat_status_type_pkey PRIMARY KEY (id)
);

CREATE TABLE "anl_mincut_connec" (
  connec_id character varying(16) NOT NULL,
  the_geom geometry(Point,SRID_VALUE),
  CONSTRAINT anl_mincut_connec_pkey PRIMARY KEY (connec_id),
  CONSTRAINT anl_mincut_connec_connec_id_fkey FOREIGN KEY (connec_id)
      REFERENCES connec (connec_id) MATCH SIMPLE
      ON UPDATE CASCADE ON DELETE CASCADE
);

CREATE TABLE "anl_mincut_hydrometer"(
  hydrometer_id character varying(16) NOT NULL,
  CONSTRAINT anl_mincut_hydrometer_pkey PRIMARY KEY (hydrometer_id),
  CONSTRAINT anl_mincut_hydrometer_hydrometer_id_fkey FOREIGN KEY (hydrometer_id)
      REFERENCES rtc_hydrometer (hydrometer_id) MATCH SIMPLE
      ON UPDATE CASCADE ON DELETE CASCADE
);

CREATE TABLE "anl_mincut_result_valve_unaccess"(
  id serial NOT NULL,
  result_cat_id character varying(16) NOT NULL,
  valve_id character varying(16) NOT NULL,
  CONSTRAINT anl_mincut_valve_status_pkey PRIMARY KEY (id),
  CONSTRAINT anl_mincut_valve_status_result_cat_id_fkey FOREIGN KEY (result_cat_id)
      REFERENCES anl_mincut_result_cat (id) MATCH SIMPLE
      ON UPDATE CASCADE ON DELETE CASCADE,
  CONSTRAINT anl_mincut_valve_status_valve_id_fkey FOREIGN KEY (valve_id)
      REFERENCES node (node_id) MATCH SIMPLE
      ON UPDATE CASCADE ON DELETE CASCADE
);




ALTER TABLE anl_mincut_result_valve ADD COLUMN status_type integer;
ALTER TABLE anl_mincut_valve ADD COLUMN status_type integer;

ALTER TABLE anl_mincut_valve DROP CONSTRAINT IF EXISTS "anl_mincut_valve_status_type_fkey";
ALTER TABLE anl_mincut_valve  ADD CONSTRAINT anl_mincut_valve_status_type_fkey FOREIGN KEY (status_type) REFERENCES anl_mincut_cat_status_type (id) MATCH SIMPLE  ON UPDATE CASCADE ON DELETE RESTRICT;

ALTER TABLE anl_mincut_result_valve DROP CONSTRAINT IF EXISTS "anl_mincut_result_valve_status_type_fkey";
ALTER TABLE anl_mincut_result_valve ADD CONSTRAINT anl_mincut_result_valve_status_type_fkey FOREIGN KEY (status_type) REFERENCES anl_mincut_cat_status_type (id) MATCH SIMPLE ON UPDATE CASCADE ON DELETE RESTRICT;


-- ----------------------------
-- BUG ON CONTROLS / RULES
-- ----------------------------

ALTER TABLE inp_controls RENAME TO _inp_controls
ALTER TABLE inp_rules RENAME TO _inp_rules


CREATE TABLE "inp_controls_x_node" (
"id" serial,
"node_id" varchar(16),
"text" text,
 CONSTRAINT id PRIMARY KEY (id));
);


CREATE TABLE "inp_rules_x_node" (
"id" serial,
"node_id" varchar(16),
"text" text,
 CONSTRAINT id PRIMARY KEY (id));
);


CREATE TABLE "inp_controls_x_arc" (
"id" serial,
"arc_id" varchar(16),
"text" text,
 CONSTRAINT id PRIMARY KEY (id));
);


CREATE TABLE "inp_rules_x_arc" (
"id" serial,
"arc_id" varchar(16),
"text" text,
 CONSTRAINT id PRIMARY KEY (id));
);











-- ----------------------------
-- REVIEW AND UPDATE DATA ON WEB/MOBILE CLIENT
-- ----------------------------

	
DROP TABLE IF EXISTS review_arc;
CREATE TABLE review_arc
(  arc_id character varying(16) NOT NULL,
  the_geom geometry(LINESTRING,SRID_VALUE),
  arc_type character varying(16),
  arccat_id character varying(30),
  annotation character varying(254),
  verified character varying(16),
  field_checked boolean,
  office_checked boolean,
  CONSTRAINT review_arc_pkey PRIMARY KEY (arc_id)
);

DROP TABLE IF EXISTS review_node;
CREATE TABLE review_node
( node_id character varying(16) NOT NULL,
  the_geom geometry(POINT,SRID_VALUE),
  elevation numeric(12,3),
  "depth" numeric(12,3),
  nodecat_id character varying(30),
  annotation character varying(254),
  observ character varying(254),
  verified character varying(16),
  field_checked boolean,
  office_checked boolean,
  CONSTRAINT review_node_pkey PRIMARY KEY (node_id)
  );

DROP TABLE IF EXISTS review_connec;
CREATE TABLE review_connec
( connec_id character varying(16) NOT NULL,
  the_geom geometry(POINT,SRID_VALUE),
  elevation numeric(12,3),
  "depth" numeric(12,3),
  connec_type character varying(16),
  connecat_id character varying(30),
  annotation character varying(254),
  observ character varying(254),
  verified character varying(16),
  field_checked boolean,
  office_checked boolean,
  CONSTRAINT review_connec_pkey PRIMARY KEY (connec_id)
  );
  
DROP TABLE IF EXISTS review_audit_arc;
CREATE TABLE review_audit_arc
(  arc_id character varying(16) NOT NULL,
  the_geom geometry(LINESTRING,SRID_VALUE),
  arc_type character varying(16),
  arccat_id character varying(30),
  annotation character varying(254),
  observ character varying(254),
  verified character varying(16),
   moved_geom boolean,
  field_checked boolean,
  "operation" character varying(25),
  "user" varchar (50),  
  date_field timestamp (6) without time zone,
  office_checked boolean,
  CONSTRAINT review_audit_arc_pkey PRIMARY KEY (arc_id)
  );
  
DROP TABLE IF EXISTS review_audit_node;
CREATE TABLE review_audit_node
(  node_id character varying(16) NOT NULL,
  the_geom geometry(POINT,SRID_VALUE),
  elevation numeric(12,3),
  "depth" numeric(12,3),
  nodecat_id character varying(30),
  annotation character varying(254),
  observ character varying(254),
  verified character varying(16),
  moved_geom boolean,
  field_checked boolean,
  "operation" character varying(25),
  "user" varchar (50),  
  date_field timestamp (6) without time zone,
  office_checked boolean,
  CONSTRAINT review_audit_node_pkey PRIMARY KEY (node_id)
  );
  
DROP TABLE IF EXISTS review_audit_connec;
CREATE TABLE review_audit_connec
(  connec_id character varying(16) NOT NULL,
  the_geom geometry(POINT,SRID_VALUE),
  elevation numeric(12,3),
  "depth" numeric(12,3),
  connec_type character varying(16),
  connecat_id character varying(30),
  annotation character varying(254),
  observ character varying(254),
  verified character varying(16),
  moved_geom boolean,
  field_checked boolean,
  "operation" character varying(25),
  "user" varchar (50),  
  date_field timestamp (6) without time zone,
  office_checked boolean,
  CONSTRAINT review_audit_connec_pkey PRIMARY KEY (connec_id)
  );
  
   
-------------
-- ALTER TABLES
-------------

ALTER TABLE man_pump ADD COLUMN flow numeric(12,4);
ALTER TABLE man_pump ADD COLUMN "power" numeric(12,4);

ALTER TABLE pond ADD COLUMN "state" character varying(16);
ALTER TABLE pool ADD COLUMN "state" character varying(16);

ALTER TABLE pond DROP CONSTRAINT IF EXISTS "pond_state_fkey";
ALTER TABLE pond ADD CONSTRAINT pond_state_fkey FOREIGN KEY (state)  REFERENCES value_state (id) MATCH SIMPLE  ON UPDATE CASCADE ON DELETE RESTRICT;
ALTER TABLE pool DROP CONSTRAINT IF EXISTS "pool_state_fkey";
ALTER TABLE pool ADD CONSTRAINT pool_state_fkey FOREIGN KEY (state)  REFERENCES value_state (id) MATCH SIMPLE  ON UPDATE CASCADE ON DELETE RESTRICT;


ALTER TABLE node ADD COLUMN hemisphere float;
ALTER TABLE node_type ADD COLUMN choose_hemisphere boolean;



ALTER TABLE anl_mincut_result_cat ADD COLUMN anl_cause character varying (30);
ALTER TABLE anl_mincut_result_cat ADD COLUMN anl_the_geom public.geometry(POINT, SRID_VALUE);
ALTER TABLE anl_mincut_result_cat ADD COLUMN exec_the_geom public.geometry(POINT, SRID_VALUE);
ALTER TABLE anl_mincut_result_cat ADD COLUMN exec_depth float;
ALTER TABLE anl_mincut_result_cat ADD COLUMN exec_limit_distance float;
ALTER TABLE anl_mincut_result_cat ADD COLUMN exec_appropiate boolean;	
ALTER TABLE anl_mincut_result_cat ADD COLUMN received_date date;	
ALTER TABLE anl_mincut_result_cat ADD COLUMN address_num character varying (30);

ALTER TABLE arc ADD COLUMN expl_id integer;
ALTER TABLE node ADD COLUMN expl_id integer;
ALTER TABLE connec ADD COLUMN expl_id integer;

ALTER TABLE polygon ADD COLUMN expl_id integer;
ALTER TABLE vnode ADD COLUMN expl_id integer;
ALTER TABLE link ADD COLUMN expl_id integer;
ALTER TABLE point ADD COLUMN expl_id integer;
ALTER TABLE pond ADD COLUMN expl_id integer;
ALTER TABLE pool ADD COLUMN expl_id integer;
ALTER TABLE samplepoint ADD COLUMN expl_id integer;
ALTER TABLE om_visit ADD COLUMN expl_id integer;
ALTER TABLE plan_psector ADD COLUMN expl_id integer;
ALTER TABLE element ADD COLUMN expl_id integer;

ALTER TABLE sector ADD COLUMN expl_id integer;
ALTER TABLE dma ADD COLUMN expl_id integer;
ALTER TABLE presszone ADD COLUMN expl_id integer;

ALTER TABLE node ADD COLUMN code varchar(30);
ALTER TABLE arc ADD COLUMN code varchar(30);
ALTER TABLE element ADD COLUMN code varchar(30);

ALTER TABLE node ADD COLUMN publish boolean;
ALTER TABLE arc ADD COLUMN publish boolean;
ALTER TABLE connec ADD COLUMN publish boolean;


ALTER TABLE node ADD COLUMN inventory boolean;
ALTER TABLE arc ADD COLUMN inventory boolean;
ALTER TABLE connec ADD COLUMN inventory boolean;


ALTER TABLE node ADD COLUMN end_date date;
ALTER TABLE arc ADD COLUMN end_date date;
ALTER TABLE connec ADD COLUMN end_date date;

ALTER TABLE man_pump ADD COLUMN elev_height numeric(12,4);

ALTER TABLE man_valve ADD COLUMN cat_valve2 character varying(30);
ALTER TABLE man_tap ADD COLUMN cat_valve2 character varying(30);
ALTER TABLE man_wjoin ADD COLUMN cat_valve2 character varying(30);

ALTER TABLE man_fountain ADD COLUMN linked_connec character varying(16);
ALTER TABLE man_tap ADD COLUMN linked_connec character varying(16);
ALTER TABLE man_greentap ADD COLUMN linked_connec character varying(16);

ALTER TABLE man_fountain ADD COLUMN the_geom_pol geometry(POLYGON,SRID_VALUE);

ALTER TABLE cat_work ADD COLUMN workid_key1 character varying(30);
ALTER TABLE cat_work ADD COLUMN workid_key2 character varying(30);
ALTER TABLE cat_work ADD COLUMN builtdate date;

ALTER TABLE dma ADD COLUMN macrodma_id character varying(50);

ALTER TABLE om_visit ADD COLUMN  webclient_id character varying(50);

ALTER TABLE om_visit_event ADD COLUMN  picture_id character varying(50);

ALTER TABLE cat_node ADD COLUMN active boolean;
ALTER TABLE cat_arc ADD COLUMN active boolean;
ALTER TABLE cat_connec ADD COLUMN active boolean;
ALTER TABLE cat_element ADD COLUMN active boolean;

ALTER TABLE cat_node ADD COLUMN madeby character varying(100);
ALTER TABLE cat_node ADD COLUMN model character varying(100);

ALTER TABLE cat_element ADD COLUMN madeby character varying(100);
ALTER TABLE cat_element ADD COLUMN model character varying(100);

ALTER TABLE man_tank ADD COLUMN pol_id character varying(16);



ALTER TABLE doc_x_tag DROP CONSTRAINT IF EXISTS "doc_x_tag_tag_id_fkey";
ALTER TABLE doc_x_tag DROP CONSTRAINT IF EXISTS "doc_x_tag_doc_id_fkey";

ALTER TABLE arc DROP CONSTRAINT IF EXISTS "arc_expl_id_fkey";
ALTER TABLE node DROP CONSTRAINT IF EXISTS "node_expl_id_fkey";
ALTER TABLE connec DROP CONSTRAINT IF EXISTS "connec_expl_id_fkey";

ALTER TABLE polygon DROP CONSTRAINT IF EXISTS "polygon_expl_id_fkey";
ALTER TABLE vnode DROP CONSTRAINT IF EXISTS "vnode_expl_id_fkey";
ALTER TABLE link DROP CONSTRAINT IF EXISTS "link_expl_id_fkey";
ALTER TABLE point DROP CONSTRAINT IF EXISTS "point_expl_id_fkey";
ALTER TABLE pond DROP CONSTRAINT IF EXISTS "pond_expl_id_fkey";
ALTER TABLE pool DROP CONSTRAINT IF EXISTS "pool_expl_id_fkey";
ALTER TABLE samplepoint DROP CONSTRAINT IF EXISTS "samplepoint_expl_id_fkey";
ALTER TABLE om_visit DROP CONSTRAINT IF EXISTS "om_visit_expl_id_fkey";
ALTER TABLE plan_psector DROP CONSTRAINT IF EXISTS "plan_psector_expl_id_fkey";

ALTER TABLE man_valve DROP CONSTRAINT IF EXISTS "cat_node_cat_valve2_fkey";
ALTER TABLE man_tap DROP CONSTRAINT IF EXISTS "cat_node_cat_valve2_fkey";
ALTER TABLE man_wjoin DROP CONSTRAINT IF EXISTS "cat_node_cat_valve2_fkey";

ALTER TABLE man_fountain DROP CONSTRAINT IF EXISTS "connec_linked_connec_fkey";
ALTER TABLE man_tap DROP CONSTRAINT IF EXISTS "connec_linked_connec_fkey";
ALTER TABLE man_greentap DROP CONSTRAINT IF EXISTS "connec_linked_connec_fkey";

ALTER TABLE anl_mincut_result_cat DROP CONSTRAINT IF EXISTS "anl_mincut_result_cat_cause_anl_cause_fkey";
ALTER TABLE anl_mincut_result_cat DROP CONSTRAINT IF EXISTS "anl_mincut_result_cat_type_mincut_result_type_fkey";
ALTER TABLE anl_mincut_result_cat DROP CONSTRAINT IF EXISTS "anl_mincut_result_cat_state_mincut_result_state_fkey";


ALTER TABLE doc_x_tag ADD CONSTRAINT doc_x_tag_tag_id_fkey FOREIGN KEY (tag_id) REFERENCES cat_tag (id) MATCH SIMPLE ON UPDATE CASCADE ON DELETE CASCADE;
ALTER TABLE doc_x_tag ADD CONSTRAINT doc_x_tag_doc_id_fkey FOREIGN KEY (doc_id) REFERENCES doc (id) MATCH SIMPLE ON UPDATE CASCADE ON DELETE CASCADE;

ALTER TABLE arc  ADD CONSTRAINT arc_expl_id_fkey FOREIGN KEY (expl_id) REFERENCES expl_selector (expl_id) MATCH SIMPLE ON UPDATE CASCADE ON DELETE RESTRICT;
ALTER TABLE node  ADD CONSTRAINT node_expl_id_fkey FOREIGN KEY (expl_id) REFERENCES expl_selector (expl_id) MATCH SIMPLE ON UPDATE CASCADE ON DELETE RESTRICT;
ALTER TABLE connec  ADD CONSTRAINT connec_expl_id_fkey FOREIGN KEY (expl_id) REFERENCES expl_selector (expl_id) MATCH SIMPLE ON UPDATE CASCADE ON DELETE RESTRICT;

ALTER TABLE polygon  ADD CONSTRAINT polygon_expl_id_fkey FOREIGN KEY (expl_id) REFERENCES exploitation (expl_id) MATCH SIMPLE ON UPDATE CASCADE ON DELETE RESTRICT;
ALTER TABLE vnode  ADD CONSTRAINT vnode_expl_id_fkey FOREIGN KEY (expl_id) REFERENCES exploitation (expl_id) MATCH SIMPLE ON UPDATE CASCADE ON DELETE RESTRICT;
ALTER TABLE link  ADD CONSTRAINT link_expl_id_fkey FOREIGN KEY (expl_id) REFERENCES exploitation (expl_id) MATCH SIMPLE ON UPDATE CASCADE ON DELETE RESTRICT;
ALTER TABLE point  ADD CONSTRAINT point_expl_id_fkey FOREIGN KEY (expl_id) REFERENCES exploitation (expl_id) MATCH SIMPLE ON UPDATE CASCADE ON DELETE RESTRICT;
ALTER TABLE pond  ADD CONSTRAINT pond_expl_id_fkey FOREIGN KEY (expl_id) REFERENCES exploitation (expl_id) MATCH SIMPLE ON UPDATE CASCADE ON DELETE RESTRICT;
ALTER TABLE pool  ADD CONSTRAINT pool_expl_id_fkey FOREIGN KEY (expl_id) REFERENCES exploitation (expl_id) MATCH SIMPLE ON UPDATE CASCADE ON DELETE RESTRICT;
ALTER TABLE samplepoint  ADD CONSTRAINT samplepoint_expl_id_fkey FOREIGN KEY (expl_id) REFERENCES exploitation (expl_id) MATCH SIMPLE ON UPDATE CASCADE ON DELETE RESTRICT;
ALTER TABLE om_visit  ADD CONSTRAINT om_visit_expl_id_fkey FOREIGN KEY (expl_id) REFERENCES exploitation (expl_id) MATCH SIMPLE ON UPDATE CASCADE ON DELETE RESTRICT;
ALTER TABLE plan_psector  ADD CONSTRAINT plan_psector_expl_id_fkey FOREIGN KEY (expl_id) REFERENCES exploitation (expl_id) MATCH SIMPLE ON UPDATE CASCADE ON DELETE RESTRICT;

ALTER TABLE man_valve  ADD CONSTRAINT cat_node_cat_valve2_fkey FOREIGN KEY (cat_valve2) REFERENCES cat_node (id) MATCH SIMPLE ON UPDATE CASCADE ON DELETE RESTRICT;
ALTER TABLE man_tap  ADD CONSTRAINT cat_node_cat_valve2_fkey FOREIGN KEY (cat_valve2) REFERENCES cat_node (id) MATCH SIMPLE ON UPDATE CASCADE ON DELETE RESTRICT;
ALTER TABLE man_wjoin  ADD CONSTRAINT cat_node_cat_valve2_fkey FOREIGN KEY (cat_valve2) REFERENCES cat_node (id) MATCH SIMPLE ON UPDATE CASCADE ON DELETE RESTRICT;

ALTER TABLE man_fountain  ADD CONSTRAINT connec_linked_connec_fkey FOREIGN KEY (linked_connec) REFERENCES connec (connec_id) MATCH SIMPLE ON UPDATE CASCADE ON DELETE RESTRICT;
ALTER TABLE man_tap  ADD CONSTRAINT connec_linked_connec_fkey FOREIGN KEY (linked_connec) REFERENCES connec (connec_id) MATCH SIMPLE ON UPDATE CASCADE ON DELETE RESTRICT;
ALTER TABLE man_greentap  ADD CONSTRAINT connec_linked_connec_fkey FOREIGN KEY (linked_connec) REFERENCES connec (connec_id) MATCH SIMPLE ON UPDATE CASCADE ON DELETE RESTRICT;


ALTER TABLE anl_mincut_result_cat  ADD CONSTRAINT anl_mincut_result_cat_cause_anl_cause_fkey FOREIGN KEY (anl_cause) REFERENCES anl_mincut_result_cat_cause (id) MATCH SIMPLE ON UPDATE CASCADE ON DELETE RESTRICT;
ALTER TABLE anl_mincut_result_cat  ADD CONSTRAINT anl_mincut_result_cat_type_mincut_result_type_fkey FOREIGN KEY (mincut_result_type) REFERENCES anl_mincut_result_cat_type (id) MATCH SIMPLE ON UPDATE CASCADE ON DELETE RESTRICT;
ALTER TABLE anl_mincut_result_cat  ADD CONSTRAINT anl_mincut_result_cat_state_mincut_result_state_fkey FOREIGN KEY (mincut_result_state) REFERENCES anl_mincut_result_cat_state (id) MATCH SIMPLE ON UPDATE CASCADE ON DELETE RESTRICT;


ALTER TABLE "inp_controls_x_node" DROP CONSTRAINT IF EXISTS "inp_controls_x_node_id_fkey"
ALTER TABLE "inp_controls_x_node" ADD CONSTRAINT "inp_controls_x_node_id_fkey" FOREIGN KEY ("node_id") REFERENCES "node" ("node_id") ON DELETE RESTRICT ON UPDATE CASCADE;

ALTER TABLE "inp_rules_x_node" DROP CONSTRAINT IF EXISTS "inp_rules_x_node_id_fkey"
ALTER TABLE "inp_rules_x_node" ADD CONSTRAINT "inp_rules_x_node_id_fkey" FOREIGN KEY ("node_id") REFERENCES "node" ("node_id") ON DELETE RESTRICT ON UPDATE CASCADE;

ALTER TABLE "inp_controls_x_arc" DROP CONSTRAINT IF EXISTS "inp_controls_x_arc_id_fkey"
ALTER TABLE "inp_controls_x_arc" ADD CONSTRAINT "inp_controls_x_arc_id_fkey" FOREIGN KEY ("arc_id") REFERENCES "arc" ("arc_id") ON DELETE RESTRICT ON UPDATE CASCADE;

ALTER TABLE "inp_rules_x_arc" DROP CONSTRAINT IF EXISTS "inp_rules_x_arc_id_fkey"
ALTER TABLE "inp_rules_x_arc" ADD CONSTRAINT "inp_rules_x_arc_id_fkey" FOREIGN KEY ("arc_id") REFERENCES "arc" ("arc_id") ON DELETE RESTRICT ON UPDATE CASCADE;



