ALTER TABLE tenancy_details DROP COLUMN document_id;

ALTER TABLE notice_of_rent_change
  DROP COLUMN document_id,
  DROP COLUMN notice_of_increase_validity_challenge_document_id;
