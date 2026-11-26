ALTER TABLE case_property_address
  ADD COLUMN address_line_3 VARCHAR(100),
  ADD COLUMN country VARCHAR(100),
  ALTER COLUMN post_code TYPE VARCHAR(16),
  ALTER COLUMN address_line_1 SET NOT NULL,
  ALTER COLUMN town_city SET NOT NULL,
  ALTER COLUMN post_code SET NOT NULL;

ALTER TABLE case_party_address
  ADD COLUMN address_line_3 VARCHAR(100),
  ADD COLUMN country VARCHAR(100),
  ALTER COLUMN post_code TYPE VARCHAR(16),
  ALTER COLUMN address_line_1 SET NOT NULL,
  ALTER COLUMN town_city SET NOT NULL,
  ALTER COLUMN post_code SET NOT NULL;

ALTER TABLE case_party
  ADD COLUMN contact_by_phone YES_NO,
  ADD COLUMN contact_by_email YES_NO,
  ADD COLUMN contact_by_post YES_NO,
  ADD COLUMN access_code INTEGER,
  ALTER COLUMN reference_number_for_communications TYPE VARCHAR(50),
  ALTER COLUMN phone_number TYPE VARCHAR(16),
  ALTER COLUMN mobile_phone_number TYPE VARCHAR(16);

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
  ADD COLUMN repairs_responsible_by_tenant_details VARCHAR(5000),
  ALTER COLUMN tenancy_type TYPE VARCHAR(32),
  ALTER COLUMN additional_services_provided_in_tenancy_details TYPE VARCHAR(500);

ALTER TABLE notice_of_rent_change
  RENAME COLUMN notice_not_legally_valid_details TO notice_not_legally_valid_reason;

ALTER TABLE notice_of_rent_change
  ALTER COLUMN received_landlord_notice_proposing_new_rent SET NOT NULL;

ALTER TABLE fee_help_with_fees
  ADD COLUMN applied_for_help_with_fees YES_NO NOT NULL,
  ALTER COLUMN reference_number TYPE VARCHAR(32);

ALTER TABLE case_property_details
  DROP COLUMN hearing_requested,
  DROP COLUMN reason_hearing_requested,
  ALTER COLUMN floorplan_manual_details TYPE VARCHAR(200),
  ALTER COLUMN property_indoor_features TYPE VARCHAR(200),
  ALTER COLUMN share_property_with_landlord_details TYPE VARCHAR(32),
  ALTER COLUMN applicant_suggested_market_rent_reasons TYPE VARCHAR(200);
