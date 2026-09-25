-- Updates to the tables for adding MRD tables

CREATE TYPE FLAG_VISIBILITY AS ENUM ('INTERNAL', 'EXTERNAL');

-- Document table
ALTER TABLE document
  ALTER COLUMN url TYPE TEXT,
  ALTER COLUMN binary_url TYPE TEXT;

-- Application fee table
ALTER TABLE application_fee
  ADD COLUMN need_help_with_application_fee YES_NO;

ALTER TABLE application_fee
  ALTER COLUMN need_help_with_application_fee SET NOT NULL;

-- Case application table
ALTER TABLE case_application
  ADD COLUMN delete_application YES_NO;

-- Case hearing table
ALTER TABLE case_hearing
  ADD COLUMN hearing_channel_key VARCHAR(64),
  ADD COLUMN hearing_subchannel_key VARCHAR(64),
  ADD COLUMN hearing_priority_key VARCHAR(64),
  ADD COLUMN hearing_type_key VARCHAR(64),
  ADD COLUMN case_management_cancellation_reason_key VARCHAR(64),
  ADD COLUMN actual_cancellation_reason_key VARCHAR(64),
  ADD COLUMN actual_part_heard_reason_key VARCHAR(64),
  ADD COLUMN hearing_judge_id BIGINT,
  ADD COLUMN change_reasons_key VARCHAR(64),
  ADD COLUMN listing_status_key VARCHAR(64),
  ADD COLUMN auto_list_change_reasons_key VARCHAR(64),
  ADD COLUMN hearing_venue_id BIGINT;

ALTER TABLE case_hearing
  ALTER COLUMN hearing_channel_key SET NOT NULL,
  ALTER COLUMN hearing_subchannel_key SET NOT NULL,
  ALTER COLUMN hearing_priority_key SET NOT NULL,
  ALTER COLUMN hearing_type_key SET NOT NULL,
  ALTER COLUMN case_management_cancellation_reason_key SET NOT NULL,
  ALTER COLUMN actual_cancellation_reason_key SET NOT NULL,
  ALTER COLUMN actual_part_heard_reason_key SET NOT NULL,
  ALTER COLUMN hearing_judge_id SET NOT NULL,
  ALTER COLUMN change_reasons_key SET NOT NULL,
  ALTER COLUMN listing_status_key SET NOT NULL,
  ALTER COLUMN auto_list_change_reasons_key SET NOT NULL,
  ALTER COLUMN hearing_venue_id SET NOT NULL;

-- Case party table
ALTER TABLE case_party
  ADD COLUMN entity_role_code_key TEXT;

-- Case party flag table
ALTER TABLE flag_ref_data
  DROP CONSTRAINT IF EXISTS flag_ref_data_case_flag_id_fkey;

ALTER TABLE case_party_flag
  ALTER COLUMN id DROP IDENTITY IF EXISTS;

ALTER TABLE case_party_flag
  ALTER COLUMN id TYPE UUID USING gen_random_uuid();

ALTER TABLE flag_ref_data
  ALTER COLUMN case_flag_id TYPE UUID USING gen_random_uuid();

ALTER TABLE case_party_flag
  ADD COLUMN sub_type_key TEXT,
  ADD COLUMN sub_type_value TEXT,
  ADD COLUMN sub_type_value_cy TEXT,
  ADD COLUMN other_description TEXT,
  ADD COLUMN other_description_cy TEXT,
  ADD COLUMN flag_comment TEXT,
  ADD COLUMN flag_comment_cy TEXT,
  ADD COLUMN flag_update_comment TEXT,
  ADD COLUMN status TEXT,
  ADD COLUMN paths TEXT;

ALTER TABLE case_party_flag
  ALTER COLUMN id DROP IDENTITY IF EXISTS;

ALTER TABLE case_party_flag
  ALTER COLUMN id TYPE UUID USING gen_random_uuid();

ALTER TABLE case_party_flag
  ALTER COLUMN status SET NOT NULL,
  ALTER COLUMN paths SET NOT NULL;

-- Case flag table
ALTER TABLE case_flag
  RENAME COLUMN flag_path TO path;

ALTER TABLE case_flag
  ALTER COLUMN id DROP IDENTITY IF EXISTS;

ALTER TABLE case_flag
  ALTER COLUMN id TYPE UUID USING gen_random_uuid(),
  ALTER COLUMN flag_code TYPE VARCHAR(16),
  ALTER COLUMN sub_type_key TYPE TEXT,
  ALTER COLUMN sub_type_value TYPE TEXT,
  ALTER COLUMN sub_type_value_cy TYPE TEXT,
  ALTER COLUMN other_description TYPE TEXT,
  ALTER COLUMN other_description_cy TYPE TEXT,
  ALTER COLUMN flag_comment TYPE TEXT,
  ALTER COLUMN flag_comment_cy TYPE TEXT,
  ALTER COLUMN flag_update_comment TYPE TEXT,
  ALTER COLUMN flag_update_comment_cy TYPE TEXT,
  ALTER COLUMN path TYPE TEXT;

ALTER TABLE case_flag
  ADD COLUMN status TEXT;

-- Flag ref data table
ALTER TABLE flag_ref_data
  RENAME TO flag_details;

ALTER TABLE flag_details
  ALTER COLUMN visibility TYPE FLAG_VISIBILITY USING NULL,
  ALTER COLUMN flag_code TYPE VARCHAR(16);

ALTER TABLE flag_details
  DROP COLUMN name,
  DROP COLUMN name_cy,
  DROP COLUMN id,
  DROP COLUMN case_flag_id;

ALTER TABLE flag_details
  ADD COLUMN case_flag_id UUID,
  ADD COLUMN case_party_flag_id UUID,
  ADD COLUMN value_en TEXT,
  ADD COLUMN value_cy TEXT;

ALTER TABLE flag_details
  ALTER COLUMN flag_code SET NOT NULL,
  ALTER COLUMN case_flag_id SET NOT NULL,
  ALTER COLUMN case_party_flag_id SET NOT NULL;

ALTER TABLE flag_details
  ADD CONSTRAINT flag_details_pk PRIMARY KEY (flag_code);

-- Case type table
ALTER TABLE case_application
  DROP CONSTRAINT IF EXISTS case_application_case_type_id_fkey;

ALTER TABLE case_type
  DROP COLUMN last_modified_by;

ALTER TABLE case_type
  RENAME COLUMN id TO "key";

ALTER TABLE pt_case
  DROP CONSTRAINT pt_case_case_type_id_fkey;

ALTER TABLE case_type
  ALTER COLUMN "key" DROP IDENTITY IF EXISTS;

ALTER TABLE case_type
  ALTER COLUMN "key" TYPE VARCHAR(64),
  ALTER COLUMN application_type_name TYPE VARCHAR(128);

ALTER TABLE case_type
  RENAME COLUMN application_type_name TO value_en;

ALTER TABLE case_type
  ADD COLUMN active YES_NO;

-- PT case table
ALTER TABLE pt_case
  RENAME COLUMN case_type_id TO case_type_key;

ALTER TABLE pt_case
  ALTER COLUMN case_type_key TYPE VARCHAR(64);

ALTER TABLE pt_case
  ADD COLUMN case_subtype_key VARCHAR(64);

-- New tables
CREATE TABLE actual_cancellation_reason (
  "key" VARCHAR(64) PRIMARY KEY,
  value_en VARCHAR(128),
  active YES_NO,
  created_date TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
  last_modified_date TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP
);

CREATE TABLE actual_part_heard_reason (
  "key" VARCHAR(64) PRIMARY KEY,
  value_en VARCHAR(128),
  active YES_NO,
  created_date TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
  last_modified_date TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP
);

CREATE TABLE auto_list_change_reasons (
  "key" VARCHAR(64) PRIMARY KEY,
  value_en VARCHAR(128),
  active YES_NO
);

CREATE TABLE case_management_cancellation_reason (
  "key" VARCHAR(64) PRIMARY KEY,
  value_en VARCHAR(128),
  active YES_NO,
  created_date TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
  last_modified_date TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP
);

CREATE TABLE case_subtype (
  "key" VARCHAR(64) PRIMARY KEY,
  value_en VARCHAR(128),
  parentcategory VARCHAR(64),
  parentkey VARCHAR(64),
  active YES_NO,
  created_date TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
  last_modified_date TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP
);

CREATE TABLE change_reasons (
  "key" VARCHAR(64) PRIMARY KEY,
  value_en VARCHAR(128),
  active YES_NO,
  created_date TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
  last_modified_date TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP
);

CREATE TABLE custody_status (
  "key" VARCHAR(64) PRIMARY KEY,
  value_en VARCHAR(128),
  active YES_NO,
  created_date TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
  last_modified_date TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP
);

CREATE TABLE entity_role_code (
  "key" VARCHAR(64) PRIMARY KEY,
  value_en VARCHAR(128),
  value_cy VARCHAR(128),
  parentkey VARCHAR(64),
  parentcategory VARCHAR(64),
  active YES_NO,
  created_date TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
  last_modified_date TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
  last_modified_by VARCHAR(100)
);

CREATE TABLE facilities (
  "key" VARCHAR(64) PRIMARY KEY,
  value_en VARCHAR(128),
  value_cy VARCHAR(128),
  active YES_NO,
  created_date TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
  last_modified_date TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
  hearing_venue_id BIGINT NOT NULL
);

CREATE TABLE hearing_channel (
  "key" VARCHAR(64) PRIMARY KEY,
  value_en VARCHAR(128),
  value_cy VARCHAR(128),
  active YES_NO,
  created_date TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
  last_modified_date TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP
);

CREATE TABLE hearing_judge (
  id BIGINT PRIMARY KEY,
  case_hearing_id BIGINT NOT NULL,
  judge_type_key VARCHAR(64) NOT NULL
);

CREATE TABLE hearing_party (
  id BIGINT PRIMARY KEY,
  case_hearing_id BIGINT NOT NULL,
  party_relationship_type_key VARCHAR(64) NOT NULL,
  custody_status_key VARCHAR(64) NOT NULL,
  interpreter_language_key VARCHAR(64) NOT NULL,
  sign_language_key VARCHAR(64) NOT NULL,
  unavailable_type_key VARCHAR(64) NOT NULL
);

CREATE TABLE hearing_priority (
  "key" VARCHAR(64) PRIMARY KEY,
  value_en VARCHAR(128),
  value_cy VARCHAR(128),
  active YES_NO,
  created_date TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
  last_modified_date TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP
);

CREATE TABLE hearing_subchannel (
  "key" VARCHAR(64) PRIMARY KEY,
  value_en VARCHAR(128),
  value_cy VARCHAR(128),
  parentcategory VARCHAR(64),
  parentkey VARCHAR(64),
  active YES_NO,
  created_date TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
  last_modified_date TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP
);

CREATE TABLE hearing_type (
  "key" VARCHAR(64) PRIMARY KEY,
  value_en VARCHAR(128),
  value_cy VARCHAR(128),
  lov_order BIGINT,
  active YES_NO,
  created_date TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
  last_modified_date TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP
);

CREATE TABLE hearing_venue (
  id BIGINT PRIMARY KEY,
  case_hearing_id BIGINT NOT NULL
);

CREATE TABLE interpreter_language (
  "key" VARCHAR(64) PRIMARY KEY,
  value_en VARCHAR(128),
  value_cy VARCHAR(128),
  active YES_NO
);

CREATE TABLE judge_type (
  "key" VARCHAR(64) PRIMARY KEY,
  value_en VARCHAR(128),
  value_cy VARCHAR(128),
  lov_order BIGINT,
  active YES_NO,
  created_date TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
  last_modified_date TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP
);

CREATE TABLE listing_status (
  "key" VARCHAR(64) PRIMARY KEY,
  value_en VARCHAR(128),
  active YES_NO
);

CREATE TABLE party_relationship_type (
  "key" VARCHAR(64) PRIMARY KEY,
  value_en VARCHAR(128),
  value_cy VARCHAR(128),
  active YES_NO,
  created_date TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
  last_modified_date TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP
);

CREATE TABLE sign_language (
  "key" VARCHAR(64) PRIMARY KEY,
  value_en VARCHAR(128),
  value_cy VARCHAR(128),
  active YES_NO
);

CREATE TABLE unavailable_type (
  "key" VARCHAR(64) PRIMARY KEY,
  value_en VARCHAR(128),
  value_cy VARCHAR(128),
  active YES_NO
);

-- Foreign keys
ALTER TABLE case_hearing
  ADD FOREIGN KEY (actual_cancellation_reason_key)
  REFERENCES actual_cancellation_reason ("key");

CREATE INDEX case_hearing_actual_cancellation_reason_key_idx
  ON case_hearing (actual_cancellation_reason_key);

ALTER TABLE case_hearing
  ADD FOREIGN KEY (actual_part_heard_reason_key)
  REFERENCES actual_part_heard_reason ("key");

CREATE INDEX case_hearing_actual_part_heard_reason_key_idx
  ON case_hearing (actual_part_heard_reason_key);

ALTER TABLE case_hearing
  ADD FOREIGN KEY (auto_list_change_reasons_key)
  REFERENCES auto_list_change_reasons("key");

CREATE INDEX case_hearing_auto_list_change_reasons_key_idx
  ON case_hearing (auto_list_change_reasons_key);

ALTER TABLE case_hearing
  ADD FOREIGN KEY (case_management_cancellation_reason_key)
  REFERENCES case_management_cancellation_reason("key");

CREATE INDEX case_hearing_case_management_cancellation_reason_key_idx
  ON case_hearing (case_management_cancellation_reason_key);

ALTER TABLE case_hearing
  ADD FOREIGN KEY (change_reasons_key)
  REFERENCES change_reasons("key");

CREATE INDEX case_hearing_change_reasons_key_idx
  ON case_hearing (change_reasons_key);

ALTER TABLE case_hearing
  ADD FOREIGN KEY (hearing_channel_key)
  REFERENCES hearing_channel("key");

CREATE INDEX case_hearing_hearing_channel_key_idx
  ON case_hearing (hearing_channel_key);

ALTER TABLE case_hearing
  ADD FOREIGN KEY (hearing_judge_id)
  REFERENCES hearing_judge(id);

CREATE INDEX case_hearing_hearing_judge_id_idx
  ON case_hearing (hearing_judge_id);

ALTER TABLE case_hearing
  ADD FOREIGN KEY (hearing_priority_key)
  REFERENCES hearing_priority("key");

CREATE INDEX case_hearing_hearing_priority_key_idx
  ON case_hearing (hearing_priority_key);

ALTER TABLE case_hearing
  ADD FOREIGN KEY (hearing_subchannel_key)
  REFERENCES hearing_subchannel("key");

CREATE INDEX case_hearing_hearing_subchannel_key_idx
  ON case_hearing (hearing_subchannel_key);

ALTER TABLE case_hearing
  ADD FOREIGN KEY (hearing_type_key)
  REFERENCES hearing_type("key");

CREATE INDEX case_hearing_hearing_type_key_idx
  ON case_hearing (hearing_type_key);

ALTER TABLE case_hearing
  ADD FOREIGN KEY (listing_status_key)
  REFERENCES listing_status("key");

CREATE INDEX case_hearing_listing_status_key_idx
  ON case_hearing (listing_status_key);

ALTER TABLE case_hearing
  ADD FOREIGN KEY (hearing_venue_id)
  REFERENCES hearing_venue(id);

CREATE INDEX case_hearing_hearing_venue_id_idx
  ON case_hearing (hearing_venue_id);

ALTER TABLE case_party
  ADD FOREIGN KEY (entity_role_code_key)
  REFERENCES entity_role_code("key");

CREATE INDEX case_party_entity_role_code_key_idx
  ON case_party (entity_role_code_key);

ALTER TABLE facilities
  ADD FOREIGN KEY (hearing_venue_id)
  REFERENCES hearing_venue(id);

CREATE INDEX facilities_hearing_venue_id_idx
  ON facilities (hearing_venue_id);

ALTER TABLE flag_details
  ADD FOREIGN KEY (case_party_flag_id)
  REFERENCES case_party_flag(id);

CREATE INDEX flag_details_case_party_flag_id_idx
  ON flag_details(case_party_flag_id);

ALTER TABLE flag_details
  ADD FOREIGN KEY (case_flag_id)
  REFERENCES case_flag(id);

CREATE INDEX flag_details_case_flag_id_idx
  ON flag_details(case_flag_id);

ALTER TABLE hearing_judge
  ADD FOREIGN KEY (judge_type_key)
  REFERENCES judge_type("key");

CREATE INDEX hearing_judge_judge_type_key_idx
  ON hearing_judge(judge_type_key);

ALTER TABLE hearing_party
  ADD FOREIGN KEY (custody_status_key)
  REFERENCES custody_status("key");

CREATE INDEX hearing_party_custody_status_key_idx
  ON hearing_party(custody_status_key);

ALTER TABLE hearing_party
  ADD FOREIGN KEY (interpreter_language_key)
  REFERENCES interpreter_language("key");

CREATE INDEX hearing_party_interpreter_language_key_idx
  ON hearing_party(interpreter_language_key);

ALTER TABLE hearing_party
  ADD FOREIGN KEY (party_relationship_type_key)
  REFERENCES party_relationship_type("key");

CREATE INDEX hearing_party_party_relationship_type_key_idx
  ON hearing_party(party_relationship_type_key);

ALTER TABLE hearing_party
  ADD FOREIGN KEY (sign_language_key)
  REFERENCES sign_language("key");

CREATE INDEX hearing_party_sign_language_key_idx
  ON hearing_party(sign_language_key);

ALTER TABLE hearing_party
  ADD FOREIGN KEY (unavailable_type_key)
  REFERENCES unavailable_type("key");

CREATE INDEX hearing_party_unavailable_type_key_idx
  ON hearing_party(unavailable_type_key);

ALTER TABLE pt_case
  ADD FOREIGN KEY (case_subtype_key)
  REFERENCES case_subtype("key");

CREATE INDEX pt_case_case_subtype_key_idx
  ON pt_case(case_subtype_key);

ALTER TABLE pt_case
  ADD FOREIGN KEY (case_type_key)
  REFERENCES case_type("key");

CREATE INDEX pt_case_case_type_key_idx
  ON pt_case(case_type_key);

ALTER TABLE case_application
  ADD FOREIGN KEY (case_type_id)
  REFERENCES case_type ("key");
