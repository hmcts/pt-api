-- Remove testing db
DROP TABLE IF EXISTS pt_case;
DROP INDEX IF EXISTS pt_case_applicant_idam_user_id_idx;

-- Create new db
CREATE TYPE YES_NO AS ENUM ('YES', 'NO');

CREATE TABLE application_event (
  id BIGINT GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
  case_application_id BIGINT,

  created_date TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
  last_modified_date TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
  last_modified_by VARCHAR(100)
);

CREATE TABLE application_fee (
  id BIGINT GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
  case_application_id BIGINT,
  request_reference VARCHAR(100),
  request_date TIMESTAMP,
  amount NUMERIC,
  status VARCHAR(100),
  external_reference VARCHAR(100),

  created_date TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
  last_modified_date TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
  last_modified_by VARCHAR(100)
);

CREATE TABLE application_retention (
  id BIGINT GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
  case_application_id BIGINT,
  application_type VARCHAR(100),

  created_date TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
  last_modified_date TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
  last_modified_by VARCHAR(100)
);

CREATE TABLE application_statement_of_truth (
  id BIGINT GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
  case_application_id BIGINT,
  completed_date TIMESTAMP,
  completed_by VARCHAR(100),
  accepted YES_NO,
  full_name VARCHAR(100),
  firm_name VARCHAR(100),
  position_held VARCHAR(100),
  pt_case_id BIGINT,

  created_date TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
  last_modified_date TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
  last_modified_by VARCHAR(100)
);

CREATE TABLE application_status (
  id BIGINT GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
  case_application_id BIGINT,

  created_date TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
  last_modified_date TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
  last_modified_by VARCHAR(100)
);

CREATE TABLE case_application (
  id BIGINT GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
  case_type_id BIGINT,
  submitted_date TIMESTAMP,
  issued_date TIMESTAMP,
  status VARCHAR(100),
  language VARCHAR(100),
  case_party_id BIGINT,
  dx_number VARCHAR(100),

  created_date TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
  last_modified_date TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
  last_modified_by VARCHAR(100)
);

CREATE TABLE case_event (
  id BIGINT GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
  pt_case_id BIGINT,

  created_date TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
  last_modified_date TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
  last_modified_by VARCHAR(100)
);

CREATE TABLE case_evidence (
  id BIGINT GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
  case_application_id BIGINT,
  evidence_type VARCHAR(100),
  pt_case_id BIGINT,

  created_date TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
  last_modified_date TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
  last_modified_by VARCHAR(100)
);

CREATE TABLE case_flag (
  id BIGINT GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
  pt_case_id BIGINT,
  flag_code INTEGER,
  sub_type_key INTEGER,
  sub_type_value VARCHAR(100),
  sub_type_value_cy VARCHAR(100),
  other_description VARCHAR(100),
  other_description_cy VARCHAR(100),
  flag_comment VARCHAR(100),
  flag_comment_cy VARCHAR(100),
  flag_update_comment VARCHAR(100),
  flag_update_comment_cy VARCHAR(100),
  flag_path VARCHAR(100),
  start_date TIMESTAMP,
  end_date TIMESTAMP,

  created_date TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
  last_modified_date TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
  last_modified_by VARCHAR(100)
);


CREATE TABLE case_hearing (
  id BIGINT GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
  case_application_id BIGINT,
  hearing_type VARCHAR(100),
  hearing_date TIMESTAMP,
  pt_case_id BIGINT,

  created_date TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
  last_modified_date TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
  last_modified_by VARCHAR(100)
);

CREATE TABLE case_mediation (
  id BIGINT GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
  case_application_id BIGINT,
  mediation_type VARCHAR(100),
  pt_case_id BIGINT,

  created_date TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
  last_modified_date TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
  last_modified_by VARCHAR(100)
);

CREATE TABLE case_note (
  id BIGINT GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
  pt_case_id BIGINT,
  status VARCHAR(100),

  created_date TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
  last_modified_date TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
  last_modified_by VARCHAR(100)
);

CREATE TABLE case_notification (
  id BIGINT GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
  pt_case_id BIGINT,
  type VARCHAR(100),
  scheduled_date TIMESTAMP,
  status VARCHAR(100),

  created_date TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
  last_modified_date TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
  last_modified_by VARCHAR(100)
);

CREATE TABLE case_order (
  id BIGINT GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
  case_application_id BIGINT,
  order_type VARCHAR(100),
  description VARCHAR(100),
  pt_case_id BIGINT,

  created_date TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
  last_modified_date TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
  last_modified_by VARCHAR(100)
);

CREATE TABLE case_party (
  id BIGINT GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
  pt_case_id BIGINT,
  first_name VARCHAR(100),
  last_name VARCHAR(100),
  organisation_name VARCHAR(100),
  phone_number VARCHAR(20),
  mobile_phone_number VARCHAR(20),
  email_address VARCHAR(100),
  date_of_birth DATE,
  reference_number INTEGER,
  case_party_role_id BIGINT,
  case_party_type_id BIGINT,

  created_date TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
  last_modified_date TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
  last_modified_by VARCHAR(100)
);

CREATE TABLE case_party_access (
  id BIGINT GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
  case_party_id BIGINT,
  idam_id UUID,
  access_role VARCHAR(100),
  access_code INTEGER,

  created_date TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
  last_modified_date TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
  last_modified_by VARCHAR(100)
);

CREATE TABLE case_party_address (
  id BIGINT GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
  case_party_id BIGINT,
  address_line_1 VARCHAR(100),
  address_line_2 VARCHAR(100),
  address_line_3 VARCHAR(100),
  post_town VARCHAR(100),
  county VARCHAR(100),
  postcode VARCHAR(10),
  country VARCHAR(100),

  created_date TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
  last_modified_date TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
  last_modified_by VARCHAR(100)
);

CREATE TABLE case_party_attribute_assertion (
  id BIGINT GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
  case_party_id BIGINT,
  attribute_name VARCHAR(100),
  original_value VARCHAR(100),
  asserted_value VARCHAR(100),
  asserted_by VARCHAR(100),
  status VARCHAR(100),
  decided_date TIMESTAMP,

  created_date TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
  last_modified_date TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
  last_modified_by VARCHAR(100)
);

CREATE TABLE case_party_contact_preference (
  id BIGINT GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
  case_party_id BIGINT,
  preference_type VARCHAR(100),
  enabled YES_NO,

  created_date TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
  last_modified_date TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
  last_modified_by VARCHAR(100)
);

CREATE TABLE case_party_event (
  id BIGINT GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
  case_party_id BIGINT,

  created_date TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
  last_modified_date TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
  last_modified_by VARCHAR(100)
);

CREATE TABLE case_party_flag (
  id BIGINT GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
  case_party_id BIGINT,

  created_date TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
  last_modified_date TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
  last_modified_by VARCHAR(100)
);

CREATE TABLE case_party_representative (
  id BIGINT GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
  case_party_id BIGINT,
  start_date TIMESTAMP,
  end_date TIMESTAMP,
  is_primary YES_NO,
  active YES_NO,

  created_date TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
  last_modified_date TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
  last_modified_by VARCHAR(100)
);

CREATE TABLE case_party_role (
  id BIGINT GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
  role_name VARCHAR(100),

  created_date TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
  last_modified_date TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
  last_modified_by VARCHAR(100)
);

CREATE TABLE case_party_type (
  id BIGINT GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
  type_name VARCHAR(100),

  created_date TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
  last_modified_date TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
  last_modified_by VARCHAR(100)
);

CREATE TABLE case_property (
  id BIGINT GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
  pt_case_id BIGINT,
  address_line_1 VARCHAR(100),
  address_line_2 VARCHAR(100),
  address_line_3 VARCHAR(100),
  -- post_town VARCHAR(100),
  county VARCHAR(100),
  postcode VARCHAR(10),
  country VARCHAR(100),

  created_date TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
  last_modified_date TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
  last_modified_by VARCHAR(100)
);

CREATE TABLE case_state (
  id BIGINT GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
  pt_case_id BIGINT,

  created_date TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
  last_modified_date TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
  last_modified_by VARCHAR(100)
);

CREATE TABLE case_task (
  id BIGINT GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
  pt_case_id BIGINT,
  task_name VARCHAR(100),
  task_description VARCHAR(100),
  status VARCHAR(100),

  created_date TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
  last_modified_date TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
  last_modified_by VARCHAR(100)
);

CREATE TABLE case_type (
  id BIGINT GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
  application_type_name VARCHAR(100),

  created_date TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
  last_modified_date TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
  last_modified_by VARCHAR(100)
);

CREATE TABLE claim (
  id BIGINT GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
  claim_type_id BIGINT,
  claim_reason VARCHAR(100),
  pt_case_id BIGINT,

  created_date TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
  last_modified_date TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
  last_modified_by VARCHAR(100)
);

CREATE TABLE decision_appeal (
  id BIGINT GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
  hearing_decision_id BIGINT,
  appeal_type VARCHAR(100),
  description VARCHAR(100),
  permission VARCHAR(100),

  created_date TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
  last_modified_date TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
  last_modified_by VARCHAR(100)
);

CREATE TABLE document (
  id BIGINT GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
  case_application_id BIGINT,
  category_id BIGINT,
  url VARCHAR(100),
  file_name VARCHAR(100),
  binary_url VARCHAR(100),
  content_type VARCHAR(100),
  description VARCHAR(100),
  size INTEGER,
  pt_case_id BIGINT,

  created_date TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
  last_modified_date TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
  last_modified_by VARCHAR(100)
);

CREATE TABLE fee_help_with_fees (
  id BIGINT GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
  application_fee_id BIGINT,
  reference_number INTEGER,

  created_date TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
  last_modified_date TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
  last_modified_by VARCHAR(100)
);

CREATE TABLE flag_ref_data (
  id BIGINT GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
  case_flag_id BIGINT,
  flag_code INTEGER,
  flag_name VARCHAR(100),
  flag_name_cy VARCHAR(100),
  available_externally VARCHAR(10),
  visibility VARCHAR(10),
  hearing_relevant VARCHAR(100),

  created_date TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
  last_modified_date TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
  last_modified_by VARCHAR(100)
);

CREATE TABLE hardship_consideration (
  id BIGINT GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
  hardship_details VARCHAR(100),
  hardship_description VARCHAR(100),
  evidence_document_uploaded YES_NO,
  claim_id BIGINT,

  created_date TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
  last_modified_date TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
  last_modified_by VARCHAR(100)
);

CREATE TABLE hearing_decision (
  id BIGINT GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
  description VARCHAR(100),
  decision_date DATE,
  case_hearing_id BIGINT,

  created_date TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
  last_modified_date TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
  last_modified_by VARCHAR(100)
);

CREATE TABLE hearing_inspection (
  id BIGINT GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
  description VARCHAR(100),
  inspection_date DATE,
  case_hearing_id BIGINT,

  created_date TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
  last_modified_date TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
  last_modified_by VARCHAR(100)
);

CREATE TABLE market_rent_case (
  id BIGINT GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
  case_application_id BIGINT,
  method VARCHAR(100),
  channel VARCHAR(100),
  version INTEGER,
  status VARCHAR(100),
  submitted_date TIMESTAMP,
  issued_date TIMESTAMP,
  pt_case_id BIGINT,

  created_date TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
  last_modified_date TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
  last_modified_by VARCHAR(100)
);

CREATE TABLE non_rent_case (
  id BIGINT GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
  case_application_id BIGINT,
  method VARCHAR(100),
  channel VARCHAR(100),
  version INTEGER,
  status VARCHAR(100),
  submitted_date TIMESTAMP,
  issued_date TIMESTAMP,
  pt_case_id BIGINT,

  created_date TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
  last_modified_date TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
  last_modified_by VARCHAR(100)
);

CREATE TABLE notice_of_rent_change (
  id BIGINT GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
  document_id BIGINT,
  document_uploaded YES_NO,
  reason_document_not_uploaded VARCHAR(100),
  has_notice_of_increase_validity_challenge YES_NO,
  notice_of_increase_validity_challenge_document_id BIGINT,
  notice_of_increase_validity_challenge_document_uploaded YES_NO,
  reason_notice_of_increase_validity_document_not_uploaded VARCHAR(100),
  pt_case_id BIGINT,

  created_date TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
  last_modified_date TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
  last_modified_by VARCHAR(100)
);

CREATE TABLE pt_case (
  id BIGINT GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
  case_reference BIGINT NOT NULL,
  legislative_country VARCHAR(100),
  status VARCHAR(100),
  landlord_type VARCHAR(100),
  case_type_id BIGINT,

  created_date TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
  last_modified_date TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
  last_modified_by VARCHAR(100)
);

CREATE TABLE claim_type (
  id BIGINT GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
  claim_type VARCHAR(100),

  created_date TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
  last_modified_date TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
  last_modified_by VARCHAR(100)
);

CREATE TABLE representative (
  id BIGINT GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
  case_party_representative_id BIGINT,
  representative_type_id BIGINT,
  first_name VARCHAR(100),
  last_name VARCHAR(100),
  organisation_name VARCHAR(100),
  email_address VARCHAR(100),
  phone_number VARCHAR(20),

  created_date TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
  last_modified_date TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
  last_modified_by VARCHAR(100)
);

CREATE TABLE representative_type (
  id BIGINT GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
  type_name VARCHAR(100),

  created_date TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
  last_modified_date TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
  last_modified_by VARCHAR(100)
);

CREATE TABLE tenancy_details (
  id BIGINT GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
  pt_case_id BIGINT,
  document_id BIGINT,
  document_uploaded YES_NO,

  created_date TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
  last_modified_date TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
  last_modified_by VARCHAR(100)
);

ALTER TABLE application_event
  ADD FOREIGN KEY (case_application_id)
  REFERENCES case_application (id);

CREATE INDEX application_event_case_application_id_idx
  ON application_event (case_application_id);

ALTER TABLE application_fee
  ADD FOREIGN KEY (case_application_id)
  REFERENCES case_application (id);

CREATE INDEX application_fee_case_application_id_idx
  ON application_fee (case_application_id);

ALTER TABLE application_retention
  ADD FOREIGN KEY (case_application_id)
  REFERENCES case_application (id);

CREATE INDEX application_retention_case_application_id_idx
  ON application_retention (case_application_id);

ALTER TABLE application_statement_of_truth
  ADD FOREIGN KEY (case_application_id)
  REFERENCES case_application (id);

CREATE INDEX application_statement_of_truth_case_application_id_idx
  ON application_statement_of_truth (case_application_id);

ALTER TABLE application_statement_of_truth
  ADD FOREIGN KEY (pt_case_id)
  REFERENCES pt_case (id);

CREATE INDEX application_statement_of_truth_pt_case_id_idx
  ON application_statement_of_truth (pt_case_id);

ALTER TABLE application_status
  ADD FOREIGN KEY (case_application_id)
  REFERENCES case_application (id);

CREATE INDEX application_status_case_applcation_id_idx
  ON application_status (case_application_id);

ALTER TABLE case_application
  ADD FOREIGN KEY (case_party_id)
  REFERENCES case_party (id);

CREATE INDEX case_application_case_party_id_idx
  ON case_application (case_party_id);

ALTER TABLE case_application
  ADD FOREIGN KEY (case_type_id)
  REFERENCES case_type (id);

CREATE INDEX case_application_case_type_id_idx
  ON case_application (case_type_id);

ALTER TABLE case_event
  ADD FOREIGN KEY (pt_case_id)
  REFERENCES pt_case (id);

CREATE INDEX case_event_pt_case_id_idx
  ON case_event (pt_case_id);

ALTER TABLE case_evidence
  ADD FOREIGN KEY (case_application_id)
  REFERENCES case_application (id);

CREATE INDEX case_evidence_case_application_id_idx
  ON case_evidence (case_application_id);

ALTER TABLE case_evidence
  ADD FOREIGN KEY (pt_case_id)
  REFERENCES pt_case (id);

CREATE INDEX case_evidence_pt_case_id_idx
  ON case_evidence (pt_case_id);

ALTER TABLE case_flag
  ADD FOREIGN KEY (pt_case_id)
  REFERENCES pt_case (id);

CREATE INDEX case_flag_pt_case_id_idx
  ON case_flag (pt_case_id);

ALTER TABLE case_hearing
  ADD FOREIGN KEY (case_application_id)
  REFERENCES case_application (id);

CREATE INDEX case_hearing_case_application_id_idx
  ON case_hearing (case_application_id);

ALTER TABLE case_hearing
  ADD FOREIGN KEY (pt_case_id)
  REFERENCES pt_case (id);

CREATE INDEX case_hearing_pt_case_id_idx
  ON case_hearing (pt_case_id);

ALTER TABLE case_mediation
  ADD FOREIGN KEY (case_application_id)
  REFERENCES case_application (id);

CREATE INDEX case_mediation_case_application_id_idx
  ON case_mediation (case_application_id);

ALTER TABLE case_mediation
  ADD FOREIGN KEY (pt_case_id)
  REFERENCES pt_case (id);

CREATE INDEX case_mediation_pt_case_id_idx
  ON case_mediation (pt_case_id);

ALTER TABLE case_note
  ADD FOREIGN KEY (pt_case_id)
  REFERENCES pt_case (id);

CREATE INDEX case_note_pt_case_id_idx
  ON case_note (pt_case_id);

ALTER TABLE case_notification
  ADD FOREIGN KEY (pt_case_id)
  REFERENCES pt_case (id);

CREATE INDEX case_notification_pt_case_id_idx
  ON case_notification (pt_case_id);

ALTER TABLE case_order
  ADD FOREIGN KEY (case_application_id)
  REFERENCES case_application (id);

CREATE INDEX case_order_case_application_id_idx
  ON case_order (case_application_id);

ALTER TABLE case_order
  ADD FOREIGN KEY (pt_case_id)
  REFERENCES pt_case (id);

CREATE INDEX case_order_pt_case_id_idx
  ON case_order (pt_case_id);

ALTER TABLE case_party
  ADD FOREIGN KEY (case_party_role_id)
  REFERENCES case_party_role (id);

CREATE INDEX case_party_case_party_role_id_idx
  ON case_party (case_party_role_id);

ALTER TABLE case_party
  ADD FOREIGN KEY (case_party_type_id)
  REFERENCES case_party_type (id);

CREATE INDEX case_party_case_party_type_id_idx
  ON case_party (case_party_type_id);

ALTER TABLE case_party
  ADD FOREIGN KEY (pt_case_id)
  REFERENCES pt_case (id);

CREATE INDEX case_party_pt_case_id_idx
  ON case_party (pt_case_id);

ALTER TABLE case_party_access
  ADD FOREIGN KEY (case_party_id)
  REFERENCES case_party (id);

CREATE INDEX case_party_access_case_party_id_idx
  ON case_party_access (case_party_id);

ALTER TABLE case_party_address
  ADD FOREIGN KEY (case_party_id)
  REFERENCES case_party (id);

CREATE INDEX case_party_address_case_party_id_idx
  ON case_party_address (case_party_id);

ALTER TABLE case_party_attribute_assertion
  ADD FOREIGN KEY (case_party_id)
  REFERENCES case_party (id);

CREATE INDEX case_party_attribute_assertion_case_party_id_idx
  ON case_party_attribute_assertion (case_party_id);

ALTER TABLE case_party_contact_preference
  ADD FOREIGN KEY (case_party_id)
  REFERENCES case_party (id);

CREATE INDEX case_party_contact_preference_case_party_id_idx
  ON case_party_contact_preference (case_party_id);

ALTER TABLE case_party_event
  ADD FOREIGN KEY (case_party_id)
  REFERENCES case_party (id);

CREATE INDEX case_party_event_case_party_id_idx
  ON case_party_event (case_party_id);

ALTER TABLE case_party_flag
  ADD FOREIGN KEY (case_party_id)
  REFERENCES case_party (id);

CREATE INDEX case_party_flag_case_party_id_idx
  ON case_party_flag (case_party_id);

ALTER TABLE case_party_representative
  ADD FOREIGN KEY (case_party_id)
  REFERENCES case_party (id);

CREATE INDEX case_party_representative_case_party_id_idx
  ON case_party_representative (case_party_id);

ALTER TABLE case_property
  ADD FOREIGN KEY (pt_case_id)
  REFERENCES pt_case (id);

CREATE INDEX case_property_pt_case_id_idx
  ON case_property (pt_case_id);

ALTER TABLE case_state
  ADD FOREIGN KEY (pt_case_id)
  REFERENCES pt_case (id);

CREATE INDEX case_state_pt_case_id_idx
  ON case_state (pt_case_id);

ALTER TABLE case_task
  ADD FOREIGN KEY (pt_case_id)
  REFERENCES pt_case (id);

CREATE INDEX case_task_pt_case_id_idx
  ON case_task (pt_case_id);

ALTER TABLE claim
  ADD FOREIGN KEY (pt_case_id)
  REFERENCES pt_case (id);

CREATE INDEX claim_pt_case_id_idx
  ON claim (pt_case_id);

ALTER TABLE claim
  ADD FOREIGN KEY (claim_type_id)
  REFERENCES claim_type (id);

CREATE INDEX claim_claim_type_id_idx
  ON claim (claim_type_id);

ALTER TABLE decision_appeal
  ADD FOREIGN KEY (hearing_decision_id)
  REFERENCES hearing_decision (id);

CREATE INDEX decision_appeal_hearing_decision_id_idx
  ON decision_appeal (hearing_decision_id);

ALTER TABLE document
  ADD FOREIGN KEY (case_application_id)
  REFERENCES case_application (id);

CREATE INDEX document_case_application_id_idx
  ON document (case_application_id);

ALTER TABLE document
  ADD FOREIGN KEY (pt_case_id)
  REFERENCES pt_case (id);

CREATE INDEX document_pt_case_id_idx
  ON document (pt_case_id);

ALTER TABLE fee_help_with_fees
  ADD FOREIGN KEY (application_fee_id)
  REFERENCES application_fee (id);

CREATE INDEX fee_help_with_fees_application_fee_id_idx
  ON fee_help_with_fees (application_fee_id);

ALTER TABLE flag_ref_data
  ADD FOREIGN KEY (case_flag_id)
  REFERENCES case_flag (id);

CREATE INDEX flag_ref_data_case_flag_id_idx
  ON flag_ref_data (case_flag_id);

ALTER TABLE hardship_consideration
  ADD FOREIGN KEY (claim_id)
  REFERENCES claim (id);

CREATE INDEX hardship_consideration_claim_id_idx
  ON hardship_consideration (claim_id);

ALTER TABLE hearing_decision
  ADD FOREIGN KEY (case_hearing_id)
  REFERENCES case_hearing (id);

CREATE INDEX hearing_decision_case_hearing_id_idx
  ON hearing_decision (case_hearing_id);

ALTER TABLE hearing_inspection
  ADD FOREIGN KEY (case_hearing_id)
  REFERENCES case_hearing (id);

CREATE INDEX hearing_inspection_case_hearing_id_idx
  ON hearing_inspection (case_hearing_id);

ALTER TABLE market_rent_case
  ADD FOREIGN KEY (case_application_id)
  REFERENCES case_application (id);

CREATE INDEX market_rent_case_case_application_id_idx
  ON market_rent_case (case_application_id);

ALTER TABLE market_rent_case
  ADD FOREIGN KEY (pt_case_id)
  REFERENCES pt_case (id);

CREATE INDEX market_rent_case_pt_case_id_idx
  ON market_rent_case (pt_case_id);

ALTER TABLE non_rent_case
  ADD FOREIGN KEY (case_application_id)
    REFERENCES case_application (id);

CREATE INDEX non_rent_case_case_application_id_idx
  ON non_rent_case (case_application_id);

ALTER TABLE non_rent_case
  ADD FOREIGN KEY (pt_case_id)
  REFERENCES pt_case (id);

CREATE INDEX non_rent_case_pt_case_id_idx
  ON non_rent_case (pt_case_id);

ALTER TABLE notice_of_rent_change
  ADD FOREIGN KEY (document_id)
  REFERENCES document (id);

CREATE INDEX notice_of_rent_change_document_id_idx
  ON notice_of_rent_change (document_id);

ALTER TABLE notice_of_rent_change
  ADD FOREIGN KEY (notice_of_increase_validity_challenge_document_id)
  REFERENCES document (id);

CREATE INDEX notice_of_rent_change_validity_challenge_doc_id_idx
  ON notice_of_rent_change (notice_of_increase_validity_challenge_document_id);

ALTER TABLE notice_of_rent_change
  ADD FOREIGN KEY (pt_case_id)
  REFERENCES pt_case (id);

CREATE INDEX notice_of_rent_change_pt_case_id_idx
  ON notice_of_rent_change (pt_case_id);

ALTER TABLE pt_case
  ADD FOREIGN KEY (case_type_id)
  REFERENCES case_type (id);

CREATE INDEX pt_case_case_type_id_idx
  ON pt_case (case_type_id);

ALTER TABLE representative
  ADD FOREIGN KEY (case_party_representative_id)
  REFERENCES case_party_representative (id);

CREATE INDEX representative_case_party_representative_id_idx
  ON representative (case_party_representative_id);

ALTER TABLE representative
  ADD FOREIGN KEY (representative_type_id)
  REFERENCES representative_type (id);

CREATE INDEX representative_representative_type_id_idx
  ON representative (representative_type_id);

ALTER TABLE tenancy_details
  ADD FOREIGN KEY (document_id)
  REFERENCES document (id);

CREATE INDEX tenancy_details_document_id_idx
  ON tenancy_details (document_id);

ALTER TABLE tenancy_details
  ADD FOREIGN KEY (pt_case_id)
  REFERENCES pt_case (id);

CREATE INDEX tenancy_details_pt_case_id_idx
  ON tenancy_details (pt_case_id);

CREATE INDEX case_party_access_idam_id_idx
  ON case_party_access(idam_id);
