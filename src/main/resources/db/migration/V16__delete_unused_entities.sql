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
