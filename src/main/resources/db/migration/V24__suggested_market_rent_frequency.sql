ALTER TABLE market_rent_case
  ADD COLUMN applicant_suggested_market_rent_frequency FREQUENCY,
  ADD COLUMN applicant_suggested_market_rent_frequency_other VARCHAR(500);
