﻿/*
This file is part of Giswater 3
The program is free software: you can redistribute it and/or modify it under the terms of the GNU General Public License as published by the Free Software Foundation, either version 3 of the License, or (at your option) any later version.
This version of Giswater is provided by Giswater Association
*/


SET search_path = "SCHEMA_NAME", public;


-- ----------------------------
-- REVIEW TOPOLOGY TOOLS
-- ----------------------------

 
CREATE TABLE "anl_review_node" (
id serial NOT NULL PRIMARY KEY,
node_id varchar (16),
node_type varchar (30),
state integer,
num_arcs integer,
node_id_aux varchar (16),
expl_id integer,
context varchar (30),
cur_user varchar (30) DEFAULT "current_user"(),
the_geom public.geometry (POINT, SRID_VALUE)
);


CREATE TABLE "anl_review_connec" (
id serial NOT NULL PRIMARY KEY,
connec_id varchar (16),
connec_type varchar (30),
state integer,
connec_id_aux varchar (16),
expl_id integer,
context varchar (30),
cur_user varchar (30) DEFAULT "current_user"(),
the_geom public.geometry (POINT, SRID_VALUE)
);



CREATE TABLE "anl_review_arc" (
id serial NOT NULL PRIMARY KEY,
arc_id varchar (16),
arc_type varchar (30),
state integer,
arc_id_aux varchar (16),
expl_id integer,
context varchar (30),
cur_user varchar (30) DEFAULT "current_user"(),
the_geom public.geometry (LINESTRING, SRID_VALUE),
the_geom_p public.geometry (POINT, SRID_VALUE)
);



CREATE TABLE "anl_review_arc_x_node" (
id serial NOT NULL PRIMARY KEY,
arc_id varchar (16),
node_id varchar (16),
arc_type varchar (30),
state integer,
expl_id integer,
context varchar (30),
cur_user varchar (30) DEFAULT "current_user"(),
the_geom public.geometry (LINESTRING, SRID_VALUE),
the_geom_p public.geometry (POINT, SRID_VALUE)
);


CREATE INDEX anl_review_node_index   ON anl_review_node   USING gist(the_geom);
CREATE INDEX anl_review_arc_index   ON anl_review_arc USING gist(the_geom);
CREATE INDEX anl_review_arc_x_node_index   ON anl_review_arc_x_node USING gist(the_geom);

