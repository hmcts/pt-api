ALTER TABLE market_rent_case
  ALTER COLUMN property_floor_plan_available DROP NOT NULL,
  ALTER COLUMN type_of_property_renting DROP NOT NULL,
  ALTER COLUMN share_property_with_landlord DROP NOT NULL;
