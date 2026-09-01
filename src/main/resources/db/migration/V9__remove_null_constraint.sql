-- Remove not null constraint
ALTER TABLE property_inspection
  ALTER COLUMN agree_to_decision_without_inspection DROP NOT NULL;
