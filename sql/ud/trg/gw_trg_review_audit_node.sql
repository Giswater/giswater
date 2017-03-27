		CREATE OR REPLACE FUNCTION "SCHEMA_NAME".gw_trg_review_audit_node() RETURNS trigger AS
		$BODY$
		
		DECLARE

		r "SCHEMA_NAME".review_node%rowtype;
		
		BEGIN
			EXECUTE 'SET search_path TO '||quote_literal(TG_TABLE_SCHEMA)||', public';
				SELECT * into r FROM review_node;
				
				FOR  r IN SELECT field_checked FROM review_node WHERE NEW.field_checked is TRUE LOOP
				
					IF EXISTS (SELECT node_id FROM review_audit_node WHERE node_id=NEW.node_id) THEN
						
							UPDATE review_audit_node SET node_id=NEW.node_id, geom=NEW.geom, top_elev=NEW.top_elev, ymax=NEW.ymax, node_type=NEW.node_type,
							cat_matcat=NEW.cat_matcat, dimensions=NEW.dimensions, annotation=NEW.annotation, observ=NEW.observ, verified=NEW.verified, field_checked=NEW.field_checked, office_checked=NEW.office_checked
							WHERE node_id=OLD.node_id;
							RETURN NEW;
						
					ELSE
					
						INSERT INTO review_audit_node VALUES (NEW.node_id, NEW.geom, NEW.top_elev, NEW.ymax, NEW.node_type, NEW.cat_matcat, NEW.dimensions, NEW.annotation, NEW.observ, NEW.verified, NEW.field_checked, NEW.office_checked);
						RETURN NEW;	
						
					END IF;
					
				END LOOP;
				
			RETURN NEW;
		END;
		$BODY$
		  LANGUAGE plpgsql VOLATILE
		  COST 100;
		  
		DROP TRIGGER IF EXISTS gw_trg_review_audit_node ON "SCHEMA_NAME".review_node;
		CREATE TRIGGER gw_trg_review_audit_node AFTER UPDATE ON "SCHEMA_NAME".review_node FOR EACH ROW EXECUTE PROCEDURE "SCHEMA_NAME".gw_trg_review_audit_node();
		
