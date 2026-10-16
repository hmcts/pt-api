DROP TABLE IF EXISTS case_party_attribute_assertion;
DROP TABLE IF EXISTS case_party_event;
DROP TABLE IF EXISTS case_party_flag;
DROP TABLE IF EXISTS application_event;

ALTER TABLE market_rent_case ADD COLUMN market_rent_case_type VARCHAR(100);
ALTER TABLE case_application DROP COLUMN case_type_id;
ALTER TABLE pt_case DROP COLUMN case_type_id;
DROP TABLE IF EXISTS case_type;
ALTER TABLE pt_case DROP COLUMN status;

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

ALTER TABLE notice_of_rent_change ADD COLUMN hardship_details VARCHAR(100);
DROP TABLE IF EXISTS hardship_consideration;

ALTER TABLE market_rent_case ADD COLUMN language VARCHAR(100);

ALTER TABLE application_fee ADD COLUMN market_rent_case_id BIGINT;
ALTER TABLE application_fee
  ADD FOREIGN KEY (market_rent_case_id)
  REFERENCES market_rent_case (id);
CREATE INDEX application_fee_market_rent_case_id_idx
  ON application_fee (market_rent_case_id);
ALTER TABLE application_fee DROP COLUMN case_application_id;

ALTER TABLE application_statement_of_truth ADD COLUMN market_rent_case_id BIGINT;
ALTER TABLE application_statement_of_truth
  ADD FOREIGN KEY (market_rent_case_id)
  REFERENCES market_rent_case (id);
CREATE INDEX application_statement_of_truth_market_rent_case_id_idx
  ON application_statement_of_truth (market_rent_case_id);
ALTER TABLE application_statement_of_truth DROP COLUMN case_application_id;

ALTER TABLE document DROP COLUMN case_application_id;
ALTER TABLE market_rent_case DROP COLUMN case_application_id;
DROP TABLE IF EXISTS case_application;

ALTER TABLE tenancy_details
  ADD CONSTRAINT tenancy_details_market_rent_case_id_key UNIQUE (market_rent_case_id);

ALTER TABLE notice_of_rent_change
  ADD CONSTRAINT notice_of_rent_change_market_rent_case_id_key UNIQUE (market_rent_case_id);

ALTER TABLE application_statement_of_truth
  ADD CONSTRAINT application_statement_of_truth_market_rent_case_id_key UNIQUE (market_rent_case_id);
