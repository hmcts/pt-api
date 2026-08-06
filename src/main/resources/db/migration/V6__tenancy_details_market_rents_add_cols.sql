CREATE TYPE YES_NO_NOT_SURE AS ENUM ('YES', 'NO', 'NOT_SURE');
CREATE TYPE FREQUENCY AS ENUM ('WEEKLY', 'FORTNIGHTLY', 'MONTHLY', 'YEARLY');

ALTER TABLE tenancy_details
  ADD COLUMN additional_services_provided_in_tenancy YES_NO,
  ADD COLUMN additional_services_provided_in_tenancy_details VARCHAR (5000),
  ADD COLUMN landlord_repairs_details VARCHAR (5000),
  ADD COLUMN tenant_repairs_details VARCHAR (5000),
  ADD COLUMN any_tenants_made_property_repairs YES_NO_NOT_SURE,
  ADD COLUMN tenants_property_repairs_details VARCHAR(5000),
  ADD COLUMN tenancy_include_facilities YES_NO,
  ADD COLUMN other_facilities_details VARCHAR(5000),
  ADD COLUMN furniture_provided_in_tenancy YES_NO,
  ADD COLUMN furniture_provided_in_tenancy_details VARCHAR (5000),
  ADD COLUMN tenancy_end_date TIMESTAMP,
  ADD COLUMN current_tenancy_replace_original_tenancy YES_NO_NOT_SURE,
  ADD COLUMN original_tenancy_start_date TIMESTAMP,
  ADD COLUMN current_tenancy_start_date TIMESTAMP,
  ADD COLUMN tribunal_previously_determined_tenancy_rent YES_NO,
  ADD COLUMN previous_tribunal_case_reference VARCHAR (30);

ALTER TABLE market_rent_case
  ADD COLUMN rent_payment_frequency FREQUENCY,
  ADD COLUMN rent_cost_weekly NUMERIC(18,2),
  ADD COLUMN rent_cost_fortnightly NUMERIC(18,2),
  ADD COLUMN rent_cost_monthly NUMERIC(18,2),
  ADD COLUMN rent_cost_yearly NUMERIC(18,2),
  ADD COLUMN rent_includes_council_tax YES_NO,
  ADD COLUMN council_tax_frequency FREQUENCY,
  ADD COLUMN council_tax_cost_weekly NUMERIC(18,2),
  ADD COLUMN council_tax_cost_fortnightly NUMERIC(18,2),
  ADD COLUMN council_tax_cost_monthly NUMERIC(18,2),
  ADD COLUMN council_tax_cost_yearly NUMERIC(18,2),
  ADD COLUMN council_tax_frequency_and_cost_details VARCHAR(500),
  ADD COLUMN utilities_paid_frequency FREQUENCY,
  ADD COLUMN utilities_cost_weekly NUMERIC(18,2),
  ADD COLUMN utilities_cost_fortnightly NUMERIC(18,2),
  ADD COLUMN utilities_cost_monthly NUMERIC(18,2),
  ADD COLUMN utilities_cost_yearly NUMERIC(18,2),
  ADD COLUMN utilities_frequency_and_cost_details VARCHAR(500),
  ADD COLUMN other_household_management_charges YES_NO,
  ADD COLUMN rent_inclusive_of_utility_charges YES_NO,
  ADD COLUMN applicant_suggested_monthly_market_rent NUMERIC(18,2),
  ADD COLUMN additional_rental_service_charges_vary YES_NO,
  ADD COLUMN additional_rental_varying_service_charges_details VARCHAR(500);

ALTER TABLE market_rent_case
  RENAME COLUMN renting_rooms_details TO renting_room_details;

ALTER TABLE market_rent_case
  DROP COLUMN tenancy_include_facilities,
  DROP COLUMN other_facilities_details;
