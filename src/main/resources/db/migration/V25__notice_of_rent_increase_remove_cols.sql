ALTER TABLE notice_of_rent_change
  DROP COLUMN document_uploaded,
  DROP COLUMN reason_document_not_uploaded,
  DROP COLUMN has_notice_of_increase_validity_challenge,
  DROP COLUMN notice_of_increase_validity_challenge_document_uploaded,
  DROP COLUMN reason_notice_of_increase_validity_document_not_uploaded;

ALTER TABLE notice_of_rent_change
  RENAME COLUMN hardship_details TO rent_increase_to_cause_hardship_details;
