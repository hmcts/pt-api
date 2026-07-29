CREATE TYPE PROPERTY_TYPE AS ENUM (
  'ROOM',
  'FLAT',
  'TERRACED_HOUSE',
  'SEMI_DETACHED_HOUSE',
  'FULLY_DETATCHED_HOUSE',
  'OTHER'
);

ALTER TABLE market_rent_case
  ADD COLUMN property_floor_plan_available YES_NO,
  ADD COLUMN type_of_property_renting PROPERTY_TYPE,
  ADD COLUMN renting_flat_details VARCHAR(500),
  ADD COLUMN renting_rooms_details VARCHAR(500),
  ADD COLUMN other_method_of_rent_details VARCHAR(500),
  ADD COLUMN floorplan_manual_details VARCHAR(5000),
  ADD COLUMN tenancy_include_facilities YES_NO,
  ADD COLUMN property_indoor_features VARCHAR(5000),
  ADD COLUMN other_facilities_details VARCHAR(5000),
  ADD COLUMN share_property_with_landlord YES_NO,
  ADD COLUMN share_property_with_landlord_details VARCHAR(5000);

ALTER TABLE market_rent_case
  ALTER COLUMN property_floor_plan_available SET NOT NULL,
  ALTER COLUMN type_of_property_renting SET NOT NULL,
  ALTER COLUMN tenancy_include_facilities SET NOT NULL,
  ALTER COLUMN share_property_with_landlord SET NOT NULL;
