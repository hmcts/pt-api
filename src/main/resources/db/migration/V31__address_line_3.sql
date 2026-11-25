ALTER TABLE case_property_address
  ADD COLUMN address_line_3 VARCHAR(100),
  ADD COLUMN country VARCHAR(100),
  ALTER COLUMN post_code TYPE VARCHAR(16);

ALTER TABLE case_party_address
  ADD COLUMN address_line_3 VARCHAR(100),
  ADD COLUMN country VARCHAR(100),
  ALTER COLUMN post_code TYPE VARCHAR(16);
