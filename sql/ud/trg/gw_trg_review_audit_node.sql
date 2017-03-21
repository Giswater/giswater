CREATE OR REPLACE FUNCTION "SCHEMA_NAME".gw_trg_review_audit_node()
  RETURNS trigger AS
$BODY$
BEGIN
EXECUTE 'SET search_path TO '||quote_literal(TG_TABLE_SCHEMA)||', public';

	IF TG_OP = 'INSERT' THEN
	
			
		INSERT INTO review_audit_node (id, geom, node_id, top_elev, ymax, node_type, cat_matcat, dimensions, annotation, observ, verified)
		SELECT id, geom, node_id, top_elev, ymax, node_type, cat_matcat, dimensions, annotation, observ, verified 
		FROM review_node
		ON CONFLICT (id) DO NOTHING;
		
	ELSIF TG_OP = 'UPDATE' THEN
		
		UPDATE review_audit_node
		SET id=review_node.id, geom=review_node.geom, node_id=review_node.node_id, top_elev=review_node.top_elev, ymax=review_node.ymax, node_type=review_node.node_type, cat_matcat=review_node.cat_matcat, 
		dimensions=review_node.dimensions, annotation=review_node.annotation, observ=review_node.annotation, verified=review_node.verified
		FROM review_node
		WHERE review_node.id=review_audit_node.id;
		
	END IF;
	RETURN NULL;

END;
$BODY$
  LANGUAGE plpgsql VOLATILE
  COST 100;



DROP TRIGGER IF EXISTS gw_trg_review_audit_node ON "SCHEMA_NAME".review_node;
CREATE TRIGGER gw_trg_review_audit_node AFTER INSERT OR UPDATE ON "SCHEMA_NAME".review_node FOR EACH ROW EXECUTE PROCEDURE "SCHEMA_NAME".gw_trg_review_audit_node();