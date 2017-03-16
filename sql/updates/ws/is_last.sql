/*
This file is part of Giswater 2.0
The program is free software: you can redistribute it and/or modify it under the terms of the GNU General Public License as published by the Free Software Foundation, either version 3 of the License, or (at your option) any later version.
This version of Giswater is provided by Giswater Association
*/


SET search_path = "SCHEMA_NAME", public, pg_catalog;

CREATE SEQUENCE pol_id_seq
  INCREMENT 1
  MINVALUE 1
  MAXVALUE 9223372036854775807
  START 1
  CACHE 1;
  
CREATE TABLE polygon(
  pol_id character varying(16) NOT NULL PRIMARY KEY,
  text character varying(254),
  the_geom geometry(Polygon,25831),
  undelete boolean
);

CREATE TABLE macrodma(
macrodma_id character varying(50) NOT NULL PRIMARY KEY,
descript character varying(100),
the_geom geometry(MultiPolygon,25831),
undelete boolean
);

CREATE SEQUENCE doc_x_tag_seq
  INCREMENT 1
  MINVALUE 1
  MAXVALUE 9223372036854775807
  START 1
  CACHE 1;


CREATE TABLE doc_x_tag(
  id bigint NOT NULL DEFAULT nextval('doc_x_tag_seq'::regclass) PRIMARY KEY,
  doc_id character varying(30),
  tag_id character varying(16)
);



CREATE TABLE expl_selector (
expl_id character varying(50) NOT NULL PRIMARY KEY,
descript character varying(100),
the_geom geometry(MultiPolygon,25831),
undelete boolean,
cur_user text
);

ALTER TABLE arc ADD COLUMN expl_id character varying(50);
ALTER TABLE node ADD COLUMN expl_id character varying(50);
ALTER TABLE connec ADD COLUMN expl_id character varying(50);


ALTER TABLE node ADD COLUMN code varchar(30);
ALTER TABLE arc ADD COLUMN code varchar(30);
ALTER TABLE element ADD COLUMN code varchar(30);

ALTER TABLE node ADD COLUMN publish boolean;
ALTER TABLE arc ADD COLUMN publish boolean;
ALTER TABLE connec ADD COLUMN publish boolean;
ALTER TABLE element ADD COLUMN publish boolean;

ALTER TABLE node ADD COLUMN inventory boolean;
ALTER TABLE arc ADD COLUMN inventory boolean;
ALTER TABLE connec ADD COLUMN inventory boolean;
ALTER TABLE element ADD COLUMN inventory boolean;

ALTER TABLE node ADD COLUMN end_date date;
ALTER TABLE arc ADD COLUMN end_date date;
ALTER TABLE connec ADD COLUMN end_date date;

ALTER TABLE element ADD COLUMN the_geom geometry(Point,25831);

ALTER TABLE man_pump ADD COLUMN elev_height numeric(12,4);

ALTER TABLE man_valve ADD COLUMN cat_valve2 character varying(30);
ALTER TABLE man_tap ADD COLUMN cat_valve2 character varying(30);
ALTER TABLE man_wjoin ADD COLUMN cat_valve2 character varying(30);

ALTER TABLE man_fountain ADD COLUMN linked_connec character varying(16);
ALTER TABLE man_tap ADD COLUMN linked_connec character varying(16);
ALTER TABLE man_greentap ADD COLUMN linked_connec character varying(16);

ALTER TABLE man_fountain ADD COLUMN the_geom_pol geometry(Polygon,25831);

ALTER TABLE cat_work ADD COLUMN workid_key1 character varying(30);
ALTER TABLE cat_work ADD COLUMN workid_key2 character varying(30);
ALTER TABLE cat_work ADD COLUMN builtdate date;

ALTER TABLE dma ADD COLUMN macrodma_id character varying(500);

ALTER TABLE node ADD COLUMN macrodma_id character varying(50);
ALTER TABLE arc ADD COLUMN macrodma_id character varying(50);
ALTER TABLE connec ADD COLUMN macrodma_id character varying(50);

ALTER TABLE om_visit ADD COLUMN  webclient_id character varying(50);

ALTER TABLE cat_node ADD COLUMN active boolean;
ALTER TABLE cat_arc ADD COLUMN active boolean;
ALTER TABLE cat_connec ADD COLUMN active boolean;
ALTER TABLE cat_element ADD COLUMN active boolean;

ALTER TABLE cat_node ADD COLUMN made_by character varying(100);
ALTER TABLE cat_node ADD COLUMN model character varying(100);

ALTER TABLE cat_element ADD COLUMN made_by character varying(100);
ALTER TABLE cat_element ADD COLUMN model character varying(100);

ALTER TABLE man_tank ADD COLUMN pol_id character varying(16);

ALTER TABLE doc_x_tag ADD CONSTRAINT doc_x_tag_tag_id_fkey FOREIGN KEY (tag_id) REFERENCES cat_tag (id) MATCH SIMPLE ON UPDATE CASCADE ON DELETE CASCADE;
ALTER TABLE doc_x_tag ADD CONSTRAINT doc_x_tag_doc_id_fkey FOREIGN KEY (doc_id) REFERENCES doc (id) MATCH SIMPLE ON UPDATE CASCADE ON DELETE CASCADE;

ALTER TABLE arc  ADD CONSTRAINT arc_expl_id_fkey FOREIGN KEY (expl_id) REFERENCES expl_selector (expl_id) MATCH SIMPLE ON UPDATE CASCADE ON DELETE RESTRICT;
ALTER TABLE node  ADD CONSTRAINT node_expl_id_fkey FOREIGN KEY (expl_id) REFERENCES expl_selector (expl_id) MATCH SIMPLE ON UPDATE CASCADE ON DELETE RESTRICT;
ALTER TABLE connec  ADD CONSTRAINT connec_expl_id_fkey FOREIGN KEY (expl_id) REFERENCES expl_selector (expl_id) MATCH SIMPLE ON UPDATE CASCADE ON DELETE RESTRICT;

ALTER TABLE man_valve  ADD CONSTRAINT cat_node_cat_valve2_fkey FOREIGN KEY (cat_valve2) REFERENCES cat_node (id) MATCH SIMPLE ON UPDATE CASCADE ON DELETE RESTRICT;
ALTER TABLE man_tap  ADD CONSTRAINT cat_node_cat_valve2_fkey FOREIGN KEY (cat_valve2) REFERENCES cat_node (id) MATCH SIMPLE ON UPDATE CASCADE ON DELETE RESTRICT;
ALTER TABLE man_wjoin  ADD CONSTRAINT cat_node_cat_valve2_fkey FOREIGN KEY (cat_valve2) REFERENCES cat_node (id) MATCH SIMPLE ON UPDATE CASCADE ON DELETE RESTRICT;

ALTER TABLE man_fountain  ADD CONSTRAINT connec_linked_connec_fkey FOREIGN KEY (linked_connec) REFERENCES connec (connec_id) MATCH SIMPLE ON UPDATE CASCADE ON DELETE RESTRICT;
ALTER TABLE man_tap  ADD CONSTRAINT connec_linked_connec_fkey FOREIGN KEY (linked_connec) REFERENCES connec (connec_id) MATCH SIMPLE ON UPDATE CASCADE ON DELETE RESTRICT;
ALTER TABLE man_greentap  ADD CONSTRAINT connec_linked_connec_fkey FOREIGN KEY (linked_connec) REFERENCES connec (connec_id) MATCH SIMPLE ON UPDATE CASCADE ON DELETE RESTRICT;