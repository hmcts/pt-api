ALTER TABLE pt_case ADD COLUMN case_sub_type VARCHAR(100);
ALTER TABLE pt_case ADD COLUMN case_type VARCHAR(100);

ALTER TABLE market_rent_case
  RENAME TO case_property_details;

ALTER TABLE case_property_details RENAME CONSTRAINT market_rent_case_pt_case_id_key TO case_property_details_pt_case_id_key;
ALTER TABLE case_property_details ADD COLUMN decision_without_hearing YES_NO;
ALTER TABLE case_property_details ADD COLUMN decision_without_hearing_reason VARCHAR (500);
ALTER TABLE case_property_details ADD COLUMN additional_rental_varying_service_charges_details VARCHAR (500);
ALTER TABLE case_property_details ADD COLUMN case_property_address_id BIGINT;

-- manage tenancy_details FK: Add new column, copy old data, remove old column, reset FK
ALTER TABLE tenancy_details ADD COLUMN case_property_details_id BIGINT;


UPDATE tenancy_details td
SET case_property_details_id = cp.id
FROM case_property_details cp
WHERE cp.id = td.market_rent_case_id;

ALTER TABLE tenancy_details DROP COLUMN market_rent_case_id;

ALTER TABLE tenancy_details
  ADD CONSTRAINT tenancy_details_case_property_id_fkey
  FOREIGN KEY (case_property_details_id)
  REFERENCES case_property_details (id);

CREATE INDEX tenancy_details_case_property_details_id_idx
  ON tenancy_details (case_property_details_id);

-- manage notice_of_rent_change FK: Add new column, copy old data, remove old column, reset FK
ALTER TABLE notice_of_rent_change ADD COLUMN case_property_details_id BIGINT;

UPDATE notice_of_rent_change nr
SET case_property_details_id = cp.id
FROM case_property_details cp
WHERE cp.id = nr.market_rent_case_id;

ALTER TABLE notice_of_rent_change DROP COLUMN market_rent_case_id;

ALTER TABLE notice_of_rent_change
  ADD CONSTRAINT notice_of_rent_change_case_property_id_fkey
  FOREIGN KEY (case_property_details_id)
  REFERENCES case_property_details (id);

CREATE INDEX notice_of_rent_change_case_property_details_id_idx
  ON notice_of_rent_change (case_property_details_id);

-- drop market_rent_case_id FROM application_fee
ALTER TABLE application_fee DROP COLUMN market_rent_case_id;
ALTER TABLE application_fee ADD COLUMN need_help_with_application_fee YES_NO;

-- application_statement_of_truth
ALTER TABLE application_statement_of_truth RENAME TO statement_of_truth;
ALTER TABLE case_property_details ADD COLUMN statement_of_truth_id BIGINT;

ALTER TABLE statement_of_truth DROP CONSTRAINT application_statement_of_truth_market_rent_case_id_key;
ALTER TABLE statement_of_truth DROP COLUMN market_rent_case_id;

ALTER TABLE case_property_details
  ADD CONSTRAINT case_property_details_statement_of_truth_id_fkey
  FOREIGN KEY (statement_of_truth_id)
  REFERENCES statement_of_truth (id);

-- respondent_property_details
DROP INDEX respondent_property_details_market_rent_case_id_idx;
ALTER TABLE respondent_property_details RENAME COLUMN market_rent_case_id TO case_property_details_id;

ALTER TABLE respondent_property_details DROP CONSTRAINT respondent_property_details_market_rent_case_id_fkey;
ALTER TABLE respondent_property_details
  ADD CONSTRAINT respondent_property_details_case_property_details_id_fkey
  FOREIGN KEY (case_property_details_id)
  REFERENCES case_property_details (id);

CREATE INDEX respondent_property_details_case_property_details_id_idx
  ON respondent_property_details (case_property_details_id);

