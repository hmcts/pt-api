-- Market rent case table updates
ALTER TABLE market_rent_case
  ADD COLUMN applicant_suggested_monthly_market_rent_reasons VARCHAR(5000),
  ADD COLUMN additional_property_info_to_consider_when_determining YES_NO,
  ADD COLUMN additional_property_info_to_consider_when_determining_details VARCHAR(500);
