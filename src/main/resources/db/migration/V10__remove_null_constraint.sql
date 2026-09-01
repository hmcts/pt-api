ALTER TABLE notice_of_rent_change
  ALTER COLUMN received_landlord_notice_proposing_new_rent DROP NOT NULL,
  ALTER COLUMN rent_increase_to_cause_hardship DROP NOT NULL,
  ALTER COLUMN notice_legally_valid DROP NOT NULL;
