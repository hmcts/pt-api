DROP TABLE IF EXISTS case_party_attribute_assertion;
DROP TABLE IF EXISTS case_party_event;
DROP TABLE IF EXISTS case_party_flag;
DROP TABLE IF EXISTS application_event;

ALTER TABLE market_rent_case ADD COLUMN market_rent_case_type VARCHAR(100);
ALTER TABLE case_application DROP COLUMN case_type_id;
ALTER TABLE pt_case DROP COLUMN case_type_id;
DROP TABLE IF EXISTS case_type;
