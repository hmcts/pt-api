ALTER TABLE pt_case
  ADD COLUMN applicant_idam_user_id UUID,
  ADD COLUMN application_type VARCHAR(100),
  ADD COLUMN applicant_last_name VARCHAR(100),
  ADD COLUMN email VARCHAR(255),
  ADD COLUMN postcode VARCHAR(20);

ALTER TABLE pt_case RENAME COLUMN applicant_forename TO applicant_first_name;
