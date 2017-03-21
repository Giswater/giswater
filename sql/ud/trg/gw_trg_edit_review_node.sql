CREATE OR REPLACE FUNCTION "SCHEMA_NAME".gw_trg_edit_review_node()
  RETURNS trigger AS
$BODY$
BEGIN
EXECUTE 'SET search_path TO '||quote_literal(TG_TABLE_SCHEMA)||', public';

	IF TG_OP = 'UPDATE' THEN
	
		UPDATE review_audit_node
		SET checked=NEW.checked 
		WHERE node_id = OLD.node_id;
	
	END IF;	
	RETURN NULL;
		

END;
$BODY$
  LANGUAGE plpgsql VOLATILE
  COST 100;



DROP TRIGGER IF EXISTS gw_trg_edit_review_node ON "SCHEMA_NAME".v_edit_review_node;
CREATE TRIGGER gw_trg_edit_review_node INSTEAD OF UPDATE ON "SCHEMA_NAME".v_edit_review_node FOR EACH ROW EXECUTE PROCEDURE "SCHEMA_NAME".gw_trg_edit_review_node(review_audit_node);

