ALTER TABLE case_party
  ADD COLUMN extra_support_required YES_NO,
  ADD COLUMN extra_support_required_details VARCHAR(500);
