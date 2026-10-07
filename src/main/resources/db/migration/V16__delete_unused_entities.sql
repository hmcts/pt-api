DROP TABLE IF EXISTS application_status;
DROP TABLE IF EXISTS application_retention;
DROP TABLE IF EXISTS non_rent_case;
DROP TABLE IF EXISTS case_event;
DROP TABLE IF EXISTS case_state;
DROP TABLE IF EXISTS case_task;
DROP TABLE IF EXISTS case_note;
DROP TABLE IF EXISTS flag_ref_data;
DROP TABLE IF EXISTS case_flag;

ALTER TABLE case_party ADD COLUMN party_role VARCHAR(100);
UPDATE case_party cp
  SET party_role = cpr.role_name
  FROM case_party_role cpr
  WHERE cp.case_party_role_id = cpr.id;
ALTER TABLE case_party DROP COLUMN case_party_role_id;
DROP TABLE IF EXISTS case_party_role;

DROP TABLE IF EXISTS decision_appeal;
DROP TABLE IF EXISTS hearing_decision;
DROP TABLE IF EXISTS hearing_inspection;
DROP TABLE IF EXISTS case_hearing;
DROP TABLE IF EXISTS case_mediation;
DROP TABLE IF EXISTS case_order;

ALTER TABLE market_rent_case
  ADD COLUMN agree_to_decision_without_inspection YES_NO,
  ADD COLUMN no_decision_without_inspection_reason VARCHAR(500);
UPDATE market_rent_case mrc
  SET agree_to_decision_without_inspection = pi.agree_to_decision_without_inspection,
      no_decision_without_inspection_reason = pi.no_decision_without_inspection_reason
  FROM property_inspection pi
  WHERE mrc.pt_case_id = pi.pt_case_id;
INSERT INTO market_rent_case (pt_case_id, agree_to_decision_without_inspection, no_decision_without_inspection_reason)
  SELECT DISTINCT ON (pi.pt_case_id)
    pi.pt_case_id, pi.agree_to_decision_without_inspection, pi.no_decision_without_inspection_reason
  FROM property_inspection pi
  WHERE NOT EXISTS (SELECT 1 FROM market_rent_case mrc WHERE mrc.pt_case_id = pi.pt_case_id)
  ORDER BY pi.pt_case_id, pi.id;
DROP TABLE IF EXISTS property_inspection;

ALTER TABLE case_party ADD COLUMN idam_id UUID;
UPDATE case_party cp
  SET idam_id = cpa.idam_id
  FROM (
    SELECT DISTINCT ON (case_party_id) case_party_id, idam_id
    FROM case_party_access
    WHERE idam_id IS NOT NULL
    ORDER BY case_party_id, id
  ) cpa
  WHERE cp.id = cpa.case_party_id;
CREATE INDEX case_party_idam_id_idx ON case_party (idam_id);
DROP TABLE IF EXISTS case_party_access;

ALTER TABLE application_statement_of_truth DROP COLUMN IF EXISTS pt_case_id;

ALTER TABLE case_party ADD COLUMN contact_by_text YES_NO;
UPDATE case_party cp
  SET contact_by_text = cpcp.contact_by_text
  FROM (
    SELECT DISTINCT ON (case_party_id) case_party_id, contact_by_text
    FROM case_party_contact_preference
    WHERE case_party_id IS NOT NULL
    ORDER BY case_party_id, id
  ) cpcp
  WHERE cp.id = cpcp.case_party_id;
DROP TABLE IF EXISTS case_party_contact_preference;
