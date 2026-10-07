ALTER TABLE pt_case
  DROP COLUMN hearing_requested;

ALTER TABLE market_rent_case
  ADD COLUMN hearing_requested YES_NO,
  ADD COLUMN reason_hearing_requested VARCHAR(500);
