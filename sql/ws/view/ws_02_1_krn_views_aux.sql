﻿SET search_path = "SCHEMA_NAME", public, pg_catalog;



-------------------------------------------------------
-- STATE VIEWS & JOINED WITH MASTERPLAN (ALTERNATIVES)
-------------------------------------------------------
----------------------------------------------------

DROP VIEW IF EXISTS v_state_arc CASCADE;;
CREATE VIEW v_state_arc AS
SELECT 
	arc_id
	FROM selector_state,arc
	WHERE arc.state=selector_state.state_id
	AND selector_state.cur_user=current_user

EXCEPT SELECT
	arc_id
	FROM selector_psector,plan_arc_x_psector
	WHERE plan_arc_x_psector.psector_id=selector_psector.psector_id
	AND selector_psector.cur_user=current_user AND state=0

UNION SELECT
	arc_id
	FROM selector_psector,plan_arc_x_psector
	WHERE plan_arc_x_psector.psector_id=selector_psector.psector_id
	AND selector_psector.cur_user=current_user AND state=1;
	


DROP VIEW IF EXISTS v_state_node CASCADE;;
CREATE VIEW v_state_node AS
SELECT 
	node_id
	FROM selector_state,node
	WHERE node.state=selector_state.state_id
	AND selector_state.cur_user=current_user

EXCEPT SELECT
	node_id
	FROM selector_psector,plan_node_x_psector
	WHERE plan_node_x_psector.psector_id=selector_psector.psector_id
	AND selector_psector.cur_user=current_user AND state=0

UNION SELECT
	node_id
	FROM selector_psector,plan_node_x_psector
	WHERE plan_node_x_psector.psector_id=selector_psector.psector_id
	AND selector_psector.cur_user=current_user AND state=1;
	
	
	

DROP VIEW IF EXISTS v_state_connec CASCADE;;
CREATE VIEW v_state_connec AS
SELECT 
	connec_id
	FROM selector_state,connec
	WHERE connec.state=selector_state.state_id
	AND selector_state.cur_user=current_user;



	
-------------------------------------------------------
-- AUX VIEWS
-------------------------------------------------------
----------------------------------------------------

		
	
	
	
DROP VIEW IF EXISTS v_arc CASCADE;
CREATE OR REPLACE VIEW v_arc AS 
SELECT 
arc.arc_id, 
arc.node_1, 
arc.node_2, 
cat_arc.arctype_id,
arc.arccat_id, 
cat_arc.matcat_id, 
arc.epa_type, 
arc.sector_id, 
arc.state, 
arc.annotation, 
arc.custom_length, 
arc.soilcat_id, 
CASE
	WHEN arc.builtdate IS NOT NULL THEN arc.builtdate
	ELSE now()::date
	END AS builtdate, 
CASE
	WHEN arc.custom_length IS NOT NULL THEN arc.custom_length::numeric(12,3)
	ELSE st_length2d(arc.the_geom)::numeric(12,3)
	END AS length, 
arc.the_geom
FROM arc
	JOIN cat_arc ON arc.arccat_id = cat_arc.id
	JOIN v_state_arc ON arc.arc_id=v_state_arc.arc_id;
  
  

DROP VIEW IF EXISTS v_node CASCADE;
CREATE OR REPLACE VIEW v_node AS 
SELECT 
node.node_id,
node.elevation,
node.depth,
cat_node.nodetype_id,
node.nodecat_id,
node.epa_type,
node.annotation,
node.soilcat_id,
node.sector_id,
node.state,
node.the_geom
FROM node
	JOIN cat_node on nodecat_id=id
   	JOIN v_state_node ON node.node_id=v_state_node.node_id;

   
DROP VIEW IF EXISTS  v_arc_x_node1 CASCADE;
CREATE OR REPLACE VIEW v_arc_x_node1 AS 
SELECT 
v_arc.arc_id,
v_arc.node_1,
elevation AS elevation1,
depth AS depth1
FROM v_arc
	JOIN v_node ON v_arc.node_1 = v_node.node_id;

	
DROP VIEW IF EXISTS  v_arc_x_node2 CASCADE;
CREATE OR REPLACE VIEW v_arc_x_node2 AS 
SELECT 
v_arc.arc_id,
v_arc.node_2,
elevation AS elevation2,
depth AS depth2
FROM v_arc
	JOIN v_node ON v_arc.node_2 = v_node.node_id;


	

DROP VIEW IF EXISTS v_arc_x_node CASCADE;
CREATE OR REPLACE VIEW v_arc_x_node AS 
SELECT 
v_arc.arc_id,
v_arc_x_node1.node_1,
v_arc_x_node1.elevation1,
v_arc_x_node1.depth1,
v_arc_x_node2.node_2,
v_arc_x_node2.elevation2,
v_arc_x_node2.depth2,
v_arc.arctype_id,
v_arc.arccat_id,
v_arc.state,
v_arc.sector_id,
v_arc.annotation,
v_arc.soilcat_id,
v_arc.custom_length,
v_arc.length,
v_arc.the_geom
   FROM v_arc
     LEFT JOIN v_arc_x_node1 ON v_arc_x_node1.arc_id::text = v_arc.arc_id::text
     LEFT JOIN v_arc_x_node2 ON v_arc_x_node2.arc_id::text = v_arc.arc_id::text;




