CREATE TABLE pt_case (
  id BIGINT GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
  case_reference BIGINT NOT NULL,
  legislative_country VARCHAR(100),
  created_date TIMESTAMP DEFAULT CURRENT_TIMESTAMP NOT NULL,
  last_modified_date TIMESTAMP DEFAULT CURRENT_TIMESTAMP NOT NULL,
  last_modified_by VARCHAR(100),
  CONSTRAINT uq_pt_case_reference UNIQUE (
    case_reference
  )
);

CREATE TABLE case_application (
  id BIGINT GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
  pt_case_id BIGINT NOT NULL,
  submitted_date TIMESTAMP,
  issued_date TIMESTAMP,
  language VARCHAR(100),
  dx_number VARCHAR(100),
  created_date TIMESTAMP DEFAULT CURRENT_TIMESTAMP NOT NULL,
  last_modified_date TIMESTAMP DEFAULT CURRENT_TIMESTAMP NOT NULL,
  last_modified_by VARCHAR(100),
  CONSTRAINT fk_case_application_pt_case FOREIGN KEY (pt_case_id) REFERENCES pt_case (
    id
  ) ON DELETE CASCADE
);

CREATE INDEX idx_case_application_pt_case ON case_application(pt_case_id);
