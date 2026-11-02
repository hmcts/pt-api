CREATE TABLE case_property_address (
  id BIGINT GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
  pt_case_id BIGINT NOT NULL UNIQUE REFERENCES pt_case (id),
  address_line_1 VARCHAR(100),
  address_line_2 VARCHAR(100),
  town_city VARCHAR(100),
  county VARCHAR(100),
  post_code VARCHAR(10),

  created_date TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
  last_modified_date TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
  last_modified_by VARCHAR(100)
);

CREATE TABLE case_party_address (
  id BIGINT GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
  case_party_id BIGINT NOT NULL UNIQUE REFERENCES case_party (id),
  address_line_1 VARCHAR(100),
  address_line_2 VARCHAR(100),
  town_city VARCHAR(100),
  county VARCHAR(100),
  post_code VARCHAR(10),

  created_date TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
  last_modified_date TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
  last_modified_by VARCHAR(100)
);

DROP TABLE IF EXISTS address;
