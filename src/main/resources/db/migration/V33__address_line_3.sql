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
  ADD COLUMN access_code INTEGER,
  ALTER COLUMN reference_number_for_communications TYPE VARCHAR(50);

ALTER TABLE document
  ALTER COLUMN url TYPE TEXT,
  ALTER COLUMN binary_url TYPE TEXT;

ALTER TABLE notice_of_rent_change
  ALTER COLUMN rent_increase_to_cause_hardship_details TYPE VARCHAR(500);

ALTER TABLE pt_case
  ADD COLUMN method VARCHAR(100),
  ADD COLUMN channel VARCHAR(100),
  ADD COLUMN version VARCHAR(100);

ALTER TABLE tenancy_details
  ADD COLUMN repairs_responsible_by_tenant_details VARCHAR(5000);

ALTER TABLE notice_of_rent_change
  RENAME COLUMN notice_not_legally_valid_details TO notice_not_legally_valid_reason;
