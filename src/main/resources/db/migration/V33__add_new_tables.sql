CREATE TABLE duplicate_check (
  id BIGINT GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
  case_application_vetting_id BIGINT NOT NULL REFERENCES case_application_vetting(id),

  created_date TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
  last_modified_date TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
  last_modified_by VARCHAR(100)
);

CREATE TABLE legal_officer_referral (
  id BIGINT GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
  urgency VARCHAR(100),
  referral_reasons VARCHAR(500),
  referral_details VARCHAR(500),
  recommended_instructions VARCHAR (500),
  case_application_vetting_id BIGINT NOT NULL REFERENCES case_application_vetting(id),

  created_date TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
  last_modified_date TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
  last_modified_by VARCHAR(100)
);

ALTER TABLE respondent_property_details
  ADD COLUMN property_address_correct YES_NO,
  ADD COLUMN served_notice_proposing_new_rent_provided_in_tenant_application YES_NO,
  ADD COLUMN agree_with_rent_proposed_by_tenant YES_NO,
  ADD COLUMN agree_to_decision_without_inspection YES_NO,
  ADD COLUMN respond_to_tribunal_about_tenant_experiencing_hardship_answers YES_NO,
  ADD COLUMN agree_to_decision_without_hearing YES_NO,
  ADD COLUMN no_decision_without_hearing_reason VARCHAR(500),
  ADD COLUMN no_decision_without_inspection_reason VARCHAR(500),
  ADD COLUMN landlord_proposed_rent_amount_and_reasons VARCHAR(500),
  ADD COLUMN tenants_answers_experiencing_hardship_response VARCHAR(500),
  ADD COLUMN statement_of_truth_id BIGINT REFERENCES statement_of_truth (id),
  ADD COLUMN case_party_id BIGINT REFERENCES case_party (id);

ALTER TYPE DOCUMENT_TYPE
  ADD VALUE 'EVIDENCE_SUPPORTING_RESPONDENT_PROPOSED_RENT_AND_REASONS';
ALTER TYPE DOCUMENT_TYPE
  ADD VALUE 'EVIDENCE_SUPPORTING_RESPONSE_TO_TENANT_EXPERIENCING_HARDSHIP';
ALTER TYPE DOCUMENT_TYPE
  ADD VALUE 'LANDLORD_PROPOSED_MARKET_RENT_EVIDENCE';
ALTER TYPE DOCUMENT_TYPE
  ADD VALUE 'LANDLORD_REPAIRS_EVIDENCE';

CREATE INDEX duplicate_check_case_application_vetting_id_idx
  ON duplicate_check (case_application_vetting_id);
CREATE INDEX legal_officer_referral_case_application_vetting_id_idx
  ON legal_officer_referral (case_application_vetting_id);
CREATE INDEX respondent_property_details_statement_of_truth_id_idx
  ON respondent_property_details (statement_of_truth_id);
CREATE INDEX respondent_property_details_case_party_id_idx
  ON respondent_property_details (case_party_id);
