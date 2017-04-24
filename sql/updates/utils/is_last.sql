/*
This file is part of Giswater 2.0
The program is free software: you can redistribute it and/or modify it under the terms of the GNU General Public License as published by the Free Software Foundation, either version 3 of the License, or (at your option) any later version.
This version of Giswater is provided by Giswater Association
*/


SET search_path = "SCHEMA_NAME", public, pg_catalog;



CREATE TABLE om_traceability (
id serial NOT NULL PRIMARY KEY,
type character varying(50),
arc_id character varying(16),
arc_id1 character varying(16),
arc_id2 character varying(16),
node_id character varying(16),
tstamp timestamp(6) without time zone,
"user" character varying(50)
);


CREATE TABLE review_arc
(  arc_id character varying(16),
  the_geom geometry(MULTILINESTRING,SRID_VALUE),
  y1 numeric(12,3),
  y2 numeric(12,3),
  arc_type character varying(16),
  arccat_id character varying(30),
  annotation character varying(254),
  verified character varying(16),
  field_checked boolean,
  office_checked boolean,
  CONSTRAINT review_arc_pkey PRIMARY KEY (arc_id)
);


CREATE TABLE review_node
( node_id character varying(16),
  the_geom geometry(MULTIPOINT,SRID_VALUE),
  top_elev numeric(12,3),
  ymax numeric(12,3),
  node_type character varying(16),
  cat_matcat character varying(16),
  dimensions character varying(16),
  annotation character varying(254),
  observ character varying(254),
  verified character varying(16),
  field_checked boolean,
  office_checked boolean,
  CONSTRAINT review_node_pkey PRIMARY KEY (node_id)
  );
  

CREATE TABLE review_audit_arc
(  arc_id character varying(16) NOT NULL,
  the_geom geometry(MULTILINESTRING,SRID_VALUE),
  y1 numeric(12,3),
  y2 numeric(12,3),
  arc_type character varying(16),
  arccat_id character varying(30),
  annotation character varying(254),
  verified character varying(16),
   moved_geom boolean,
  field_checked boolean,
  "operation" character varying(25),
  "user" varchar (50),  
  date_field timestamp (6) without time zone,
  office_checked boolean,
  CONSTRAINT review_audit_arc_pkey PRIMARY KEY (arc_id)
  );
  

CREATE TABLE review_audit_node
(  node_id character varying(16),
  the_geom geometry(MULTIPOINT,SRID_VALUE),
  top_elev numeric(12,3),
  ymax numeric(12,3),
  node_type character varying(16),
  cat_matcat character varying(16),
  dimensions character varying(16),
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
  
  