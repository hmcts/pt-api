ALTER TABLE pt_case
  DROP COLUMN landlord_type;

ALTER TABLE case_party
  ADD COLUMN has_representative YES_NO_NOT_SURE;

DROP TABLE representative;

ALTER TABLE case_party_representative
  ADD COLUMN first_name VARCHAR(100),
  ADD COLUMN last_name VARCHAR(100),
  ADD COLUMN organisation_name VARCHAR(100),
  ADD COLUMN email_address VARCHAR(100),
  ADD COLUMN phone_number VARCHAR(16),
  ADD COLUMN representative_type_id BIGINT REFERENCES representative_type (id),
  ADD COLUMN address_line_1 VARCHAR(100),
  ADD COLUMN address_line_2 VARCHAR(100),
  ADD COLUMN address_line_3 VARCHAR(100),
  ADD COLUMN town_city VARCHAR(100),
  ADD COLUMN county VARCHAR(100),
  ADD COLUMN post_code VARCHAR(16),
  ADD COLUMN country VARCHAR(100);

ALTER TABLE case_party_representative
  ALTER COLUMN representative_type_id SET NOT NULL;

ALTER TABLE case_property_address
  ADD COLUMN country VARCHAR(100);

ALTER TABLE case_party_address
  ADD COLUMN country VARCHAR(100);
