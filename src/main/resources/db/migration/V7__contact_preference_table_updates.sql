ALTER TABLE case_party_contact_preference
  DROP COLUMN enabled,
  DROP COLUMN preference_type;

ALTER TABLE case_party_contact_preference
  ADD COLUMN contact_by_text YES_NO,
  ADD COLUMN contact_by_phone YES_NO;
