ALTER TABLE case_property_address
  ADD COLUMN address_line_3 VARCHAR(100),
  ADD COLUMN country VARCHAR(100),
  ALTER COLUMN post_code TYPE VARCHAR(16);

ALTER TABLE case_party_address
  ADD COLUMN address_line_3 VARCHAR(100),
  ADD COLUMN country VARCHAR(100),
  ALTER COLUMN post_code TYPE VARCHAR(16);

ALTER TABLE case_party
  ADD COLUMN contact_by_phone YES_NO,
  ADD COLUMN contact_by_email YES_NO,
  ADD COLUMN contact_by_post YES_NO,
  ADD COLUMN access_code INTEGER;
