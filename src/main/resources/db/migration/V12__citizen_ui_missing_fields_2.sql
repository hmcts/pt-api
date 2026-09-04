-- Market rent case table updates
ALTER TABLE market_rent_case
  ADD COLUMN applicant_suggested_monthly_market_rent_reasons VARCHAR(5000),
  ADD COLUMN additional_prop_info_to_consider_when_determining_rent YES_NO,
  ADD COLUMN additional_prop_info_to_consider_when_determining_rent_details VARCHAR(500),
  ADD COLUMN other_household_management_charges_details VARCHAR(500);

-- Document type updates
ALTER TYPE DOCUMENT_TYPE
  RENAME VALUE 'FLOOR_PLAN' TO 'PROPERTY_FLOOR_PLAN';

ALTER TYPE DOCUMENT_TYPE
  RENAME VALUE 'REPAIRS_EVIDENCE' TO 'TENANT_REPAIRS_EVIDENCE';

ALTER TYPE DOCUMENT_TYPE
  ADD VALUE 'TENANT_PROPOSED_MARKET_RENT_EVIDENCE';

-- Tenancy details table updates
ALTER TABLE tenancy_details
  ADD COLUMN copy_of_tenancy_agreement YES_NO,
  ADD COLUMN no_tenancy_agreement_reason VARCHAR(500);
