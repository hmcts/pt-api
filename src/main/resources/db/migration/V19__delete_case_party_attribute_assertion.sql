DROP TABLE IF EXISTS case_party_attribute_assertion;
DROP TABLE IF EXISTS case_party_event;
DROP TABLE IF EXISTS case_party_flag;
DROP TABLE IF EXISTS application_event;

ALTER TABLE market_rent_case ADD COLUMN market_rent_case_type VARCHAR(100);
ALTER TABLE case_application DROP COLUMN case_type_id;
ALTER TABLE pt_case DROP COLUMN case_type_id;
DROP TABLE IF EXISTS case_type;
ALTER TABLE pt_case DROP COLUMN status;

DELETE FROM market_rent_case a
  USING market_rent_case b
  WHERE a.pt_case_id = b.pt_case_id AND a.id > b.id;
ALTER TABLE market_rent_case ADD CONSTRAINT market_rent_case_pt_case_id_key UNIQUE (pt_case_id);

ALTER TABLE tenancy_details ADD COLUMN market_rent_case_id BIGINT;

ALTER TABLE tenancy_details
  ADD FOREIGN KEY (market_rent_case_id)
  REFERENCES market_rent_case (id);

CREATE INDEX tenancy_details_market_rent_case_id_idx
  ON tenancy_details (market_rent_case_id);

ALTER TABLE tenancy_details DROP COLUMN pt_case_id;

ALTER TABLE notice_of_rent_change ADD COLUMN market_rent_case_id BIGINT;

ALTER TABLE notice_of_rent_change
  ADD FOREIGN KEY (market_rent_case_id)
  REFERENCES market_rent_case (id);

CREATE INDEX notice_of_rent_change_market_rent_case_id_idx
  ON notice_of_rent_change (market_rent_case_id);

ALTER TABLE notice_of_rent_change DROP COLUMN pt_case_id;

DROP TABLE IF EXISTS case_evidence;
