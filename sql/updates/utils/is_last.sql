/*
This file is part of Giswater 2.0
The program is free software: you can redistribute it and/or modify it under the terms of the GNU General Public License as published by the Free Software Foundation, either version 3 of the License, or (at your option) any later version.
This version of Giswater is provided by Giswater Association
*/



SET search_path = "SCHEMA_NAME", public, pg_catalog;


CREATE TABLE om_traceability (
id serial,
type character varying(50),
arc_id character varying(16),
arc_id1 character varying(16),
arc_id2 character varying(16),
node_id character varying(16),
tstamp timestamp(6) without time zone,
"user" character varying(50)
  CONSTRAINT om_traceability_pkey PRIMARY KEY (id)
);



CREATE TABLE config_client_dvalue(
  id serial NOT NULL,
  table_id text,
  column_id text,
  dv_table text,
  dv_key_column text,
  dv_value_column text,
  orderby_value boolean,
  allow_null boolean,
  CONSTRAINT config_client_dvalue_pkey PRIMARY KEY (id)
);

ALTER TABLE mollet_ide_ud.config_client_dvalue ADD CONSTRAINT config_client_value_origin_id_fkey FOREIGN KEY (table_id,column_id) 
REFERENCES mollet_ide_ud.db_cat_table_x_column (db_cat_table_id,column_name) MATCH SIMPLE ON UPDATE CASCADE ON DELETE RESTRICT;

ALTER TABLE mollet_ide_ud.config_client_dvalue ADD CONSTRAINT config_client_value_colunm_id_fkey FOREIGN KEY (dv_table,dv_key_column) 
REFERENCES mollet_ide_ud.db_cat_table_x_column (db_cat_table_id,column_name) MATCH SIMPLE ON UPDATE CASCADE ON DELETE RESTRICT;

ALTER TABLE mollet_ide_ud.db_cat_table_x_column ADD CONSTRAINT db_cat_table_x_column_db_cat_table_id_column_name_key UNIQUE(db_cat_table_id, column_name);


-- TO DO:
-- cal fer una funció que fa un 'update' de les taules de catàleg per tenir-ho sempre actualitzat