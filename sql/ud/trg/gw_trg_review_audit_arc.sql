		CREATE OR REPLACE FUNCTION "SCHEMA_NAME".gw_trg_review_audit_arc() RETURNS trigger AS
		$BODY$
		
		DECLARE

		r "SCHEMA_NAME".review_arc%rowtype;
		
		BEGIN
			EXECUTE 'SET search_path TO '||quote_literal(TG_TABLE_SCHEMA)||', public';
				SELECT * into r FROM review_arc;
				
				FOR  r IN SELECT field_checked FROM review_arc WHERE NEW.field_checked is TRUE LOOP
				
					IF EXISTS (SELECT arc_id FROM review_audit_arc WHERE arc_id=NEW.arc_id) THEN
						
							UPDATE review_audit_arc SET arc_id=NEW.arc_id, geom=NEW.geom, y1=NEW.y1, y2=NEW.y2, arc_type=NEW.arc_type,
							arccat_id=NEW.arccat_id, annotation=NEW.annotation, verified=NEW.verified, field_checked=NEW.field_checked,"operation"='UPDATE',"user"=user,date_field=CURRENT_TIMESTAMP, office_checked=NEW.office_checked
							WHERE arc_id=OLD.arc_id;
							RETURN NEW;
						
					ELSE
					
						INSERT INTO review_audit_arc VALUES (NEW.arc_id, NEW.geom, NEW.y1, NEW.y2, NEW.arc_type, NEW.arccat_id, NEW.annotation, NEW.verified, NEW.field_checked,'INSERT', user, CURRENT_TIMESTAMP, NEW.office_checked);
						RETURN NEW;	
						
					END IF;
					
				END LOOP;
				
			RETURN NEW;
		END;
		$BODY$
		  LANGUAGE plpgsql VOLATILE
		  COST 100;
		  
		DROP TRIGGER IF EXISTS gw_trg_review_audit_arc ON "SCHEMA_NAME".review_arc;
		CREATE TRIGGER gw_trg_review_audit_arc AFTER UPDATE ON "SCHEMA_NAME".review_arc FOR EACH ROW EXECUTE PROCEDURE "SCHEMA_NAME".gw_trg_review_audit_arc();
		
