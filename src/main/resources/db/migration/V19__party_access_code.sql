-- Table for storing respondent access codes
CREATE TABLE party_access_code (
    id BIGINT GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
    pt_case_id BIGINT NOT NULL REFERENCES pt_case (id),
    case_party_id BIGINT NOT NULL REFERENCES case_party (id),
    code VARCHAR(100) NOT NULL, -- code will not be stored in raw form, it will be hashed
    expires_at TIMESTAMP NOT NULL,

    created_date TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    last_modified_date TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    last_modified_by VARCHAR(100),

    CONSTRAINT uq_party_access_code_case_party UNIQUE (case_party_id)
);
