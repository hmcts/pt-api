-- Remove testing db
DROP TABLE IF EXISTS pt_case;
DROP INDEX IF EXISTS pt_case_applicant_idam_user_id_idx;

-- Create new db
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
  accepted BOOLEAN,
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
  enabled BOOLEAN,

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
  is_primary BOOLEAN,
  active BOOLEAN,

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
  type_name VARCHAR(100),

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
  evidence_document_uploaded BOOLEAN,
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
  document_uploaded BOOLEAN,
  reason_document_not_uploaded VARCHAR(100),
  has_notice_of_increase_validity_challenge BOOLEAN,
  notice_of_increase_validity_challenge_document_id BIGINT,
  notice_of_increase_validity_challenge_document_uploaded BOOLEAN,
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
  document_uploaded BOOLEAN,

  created_date TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
  last_modified_date TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
  last_modified_by VARCHAR(100)
);

ALTER TABLE application_event
  ADD FOREIGN KEY (case_application_id)
  REFERENCES case_application (id);

ALTER TABLE application_fee
  ADD FOREIGN KEY (case_application_id)
  REFERENCES case_application (id);

ALTER TABLE application_retention
  ADD FOREIGN KEY (case_application_id)
  REFERENCES case_application (id);

ALTER TABLE application_statement_of_truth
  ADD FOREIGN KEY (case_application_id)
  REFERENCES case_application (id);

ALTER TABLE application_statement_of_truth
  ADD FOREIGN KEY (pt_case_id)
  REFERENCES pt_case (id);

ALTER TABLE application_status
  ADD FOREIGN KEY (case_application_id)
  REFERENCES case_application (id);

ALTER TABLE case_application
  ADD FOREIGN KEY (case_party_id)
  REFERENCES case_party (id);

ALTER TABLE case_application
  ADD FOREIGN KEY (case_type_id)
  REFERENCES case_type (id);

ALTER TABLE case_event
  ADD FOREIGN KEY (pt_case_id)
  REFERENCES pt_case (id);

ALTER TABLE case_evidence
  ADD FOREIGN KEY (case_application_id)
  REFERENCES case_application (id);

ALTER TABLE case_evidence
  ADD FOREIGN KEY (pt_case_id)
  REFERENCES pt_case (id);

ALTER TABLE case_flag
  ADD FOREIGN KEY (pt_case_id)
  REFERENCES pt_case (id);

ALTER TABLE case_hearing
  ADD FOREIGN KEY (case_application_id)
  REFERENCES case_application (id);

ALTER TABLE case_hearing
  ADD FOREIGN KEY (pt_case_id)
  REFERENCES pt_case (id);

ALTER TABLE case_mediation
  ADD FOREIGN KEY (case_application_id)
  REFERENCES case_application (id);

ALTER TABLE case_mediation
  ADD FOREIGN KEY (pt_case_id)
  REFERENCES pt_case (id);

ALTER TABLE case_note
  ADD FOREIGN KEY (pt_case_id)
  REFERENCES pt_case (id);

ALTER TABLE case_notification
  ADD FOREIGN KEY (pt_case_id)
  REFERENCES pt_case (id);

ALTER TABLE case_order
  ADD FOREIGN KEY (case_application_id)
  REFERENCES case_application (id);

ALTER TABLE case_order
  ADD FOREIGN KEY (pt_case_id)
  REFERENCES pt_case (id);

ALTER TABLE case_party
  ADD FOREIGN KEY (case_party_role_id)
  REFERENCES case_party_role (id);

ALTER TABLE case_party
  ADD FOREIGN KEY (case_party_type_id)
  REFERENCES case_party_type (id);

ALTER TABLE case_party
  ADD FOREIGN KEY (pt_case_id)
  REFERENCES pt_case (id);

ALTER TABLE case_party_access
  ADD FOREIGN KEY (case_party_id)
  REFERENCES case_party (id);

ALTER TABLE case_party_address
  ADD FOREIGN KEY (case_party_id)
  REFERENCES case_party (id);

ALTER TABLE case_party_attribute_assertion
  ADD FOREIGN KEY (case_party_id)
  REFERENCES case_party (id);

ALTER TABLE case_party_contact_preference
  ADD FOREIGN KEY (case_party_id)
  REFERENCES case_party (id);

ALTER TABLE case_party_event
  ADD FOREIGN KEY (case_party_id)
  REFERENCES case_party (id);

ALTER TABLE case_party_flag
  ADD FOREIGN KEY (case_party_id)
  REFERENCES case_party (id);

ALTER TABLE case_party_representative
  ADD FOREIGN KEY (case_party_id)
  REFERENCES case_party (id);

ALTER TABLE case_property
  ADD FOREIGN KEY (pt_case_id)
  REFERENCES pt_case (id);

ALTER TABLE case_state
  ADD FOREIGN KEY (pt_case_id)
  REFERENCES pt_case (id);

ALTER TABLE case_task
  ADD FOREIGN KEY (pt_case_id)
  REFERENCES pt_case (id);

ALTER TABLE claim
  ADD FOREIGN KEY (pt_case_id)
  REFERENCES pt_case (id);

ALTER TABLE claim
  ADD FOREIGN KEY (claim_type_id)
  REFERENCES claim_type (id);

ALTER TABLE decision_appeal
  ADD FOREIGN KEY (hearing_decision_id)
  REFERENCES hearing_decision (id);

ALTER TABLE document
  ADD FOREIGN KEY (case_application_id)
  REFERENCES case_application (id);

ALTER TABLE document
  ADD FOREIGN KEY (pt_case_id)
  REFERENCES pt_case (id);

ALTER TABLE fee_help_with_fees
  ADD FOREIGN KEY (application_fee_id)
  REFERENCES application_fee (id);

ALTER TABLE flag_ref_data
  ADD FOREIGN KEY (case_flag_id)
  REFERENCES case_flag (id);

ALTER TABLE hardship_consideration
  ADD FOREIGN KEY (claim_id)
  REFERENCES claim (id);

ALTER TABLE hearing_decision
  ADD FOREIGN KEY (case_hearing_id)
  REFERENCES case_hearing (id);

ALTER TABLE hearing_inspection
  ADD FOREIGN KEY (case_hearing_id)
  REFERENCES case_hearing (id);

ALTER TABLE market_rent_case
  ADD FOREIGN KEY (case_application_id)
  REFERENCES case_application (id);

ALTER TABLE market_rent_case
  ADD FOREIGN KEY (pt_case_id)
  REFERENCES pt_case (id);

ALTER TABLE non_rent_case
  ADD FOREIGN KEY (case_application_id)
  REFERENCES case_application (id);

ALTER TABLE non_rent_case
  ADD FOREIGN KEY (pt_case_id)
  REFERENCES pt_case (id);

ALTER TABLE notice_of_rent_change
  ADD FOREIGN KEY (document_id)
  REFERENCES document (id);

ALTER TABLE notice_of_rent_change
  ADD FOREIGN KEY (notice_of_increase_validity_challenge_document_id)
  REFERENCES document (id);

ALTER TABLE notice_of_rent_change
  ADD FOREIGN KEY (pt_case_id)
  REFERENCES pt_case (id);

ALTER TABLE pt_case
  ADD FOREIGN KEY (case_type_id)
  REFERENCES case_type (id);

ALTER TABLE representative
  ADD FOREIGN KEY (case_party_representative_id)
  REFERENCES case_party_representative (id);

ALTER TABLE representative
  ADD FOREIGN KEY (representative_type_id)
  REFERENCES representative_type (id);

ALTER TABLE tenancy_details
  ADD FOREIGN KEY (document_id)
  REFERENCES document (id);

ALTER TABLE tenancy_details
  ADD FOREIGN KEY (pt_case_id)
  REFERENCES pt_case (id);

CREATE INDEX case_party_access_idam_id_idx
  ON case_party_access(idam_id);
