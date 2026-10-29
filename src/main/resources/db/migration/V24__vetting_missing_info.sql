-- Vetting tables associated with the missing information flow
CREATE TABLE case_application_vetting (
  id BIGINT GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
  vetting_status VARCHAR(100),
  status_reason TEXT,
  market_rent_case_id BIGINT NOT NULL REFERENCES market_rent_case (id),

  created_date TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
  last_modified_date TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
  last_modified_by VARCHAR(100)
);

CREATE TABLE missing_information_request (
  id BIGINT GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
  missing_information_types VARCHAR(250),
  other_documents_information_description TEXT,
  missing_information_deadline TIMESTAMP,
  case_application_vetting_id BIGINT NOT NULL REFERENCES case_application_vetting (id),

  created_date TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
  last_modified_date TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
  last_modified_by VARCHAR(100)
);
