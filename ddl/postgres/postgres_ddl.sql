/* Create enum type "yes_no" */
CREATE TYPE "yes_no" AS
ENUM('YES', 'NO');

/* Create enum type "property_type" */
CREATE TYPE "property_type" AS
ENUM('ROOM', 'FLAT', 'TERRACED_HOUSE', 'SEMI_DETACHED_HOUSE', 'FULLY_DETACHED_HOUSE', 'OTHER');

/* Create enum type "yes_no_not_sure" */
CREATE TYPE "yes_no_not_sure" AS
ENUM('YES', 'NO', 'NOT_SURE');

/* Create enum type "frequency" */
CREATE TYPE "frequency" AS
ENUM('WEEKLY', 'FORTNIGHTLY', 'MONTHLY', 'YEARLY', 'OTHER');

/* Create enum type "document_type" */
CREATE TYPE "document_type" AS
ENUM('HARDSHIP_EVIDENCE', 'TENANCY_AGREEMENT', 'NEW_RENT_INCREASE_NOTICE', 'NOTICE_NOT_LEGALLY_VALID_EVIDENCE', 'PROPERTY_FLOOR_PLAN', 'OUTSIDE_PROPERTY', 'PROPERTY_ROOMS', 'TENANT_REPAIRS_EVIDENCE', 'TENANT_PROPOSED_MARKET_RENT_EVIDENCE');

/* Create "pt_case" table */
CREATE TABLE "pt_case" (
  "id" BIGINT NOT NULL GENERATED ALWAYS AS IDENTITY,
  "case_reference" BIGINT NOT NULL,
  "landlord_type" VARCHAR(100) NULL,
  "created_date" TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
  "last_modified_date" TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
  "last_modified_by" VARCHAR(100) NULL,
  "application_submitted_date" TIMESTAMP NULL,
  "case_issued_date" TIMESTAMP NULL,
  PRIMARY KEY ("id"),
  CONSTRAINT "pt_case_case_reference_key" UNIQUE (
    "case_reference"
  )
);

/* Create "scheduled_tasks" table */
CREATE TABLE "scheduled_tasks" (
  "task_name" TEXT NOT NULL,
  "task_instance" TEXT NOT NULL,
  "task_data" BYTEA NULL,
  "execution_time" TIMESTAMPTZ NOT NULL,
  "picked" BOOLEAN NOT NULL,
  "picked_by" TEXT NULL,
  "last_success" TIMESTAMPTZ NULL,
  "last_failure" TIMESTAMPTZ NULL,
  "consecutive_failures" INTEGER NULL,
  "last_heartbeat" TIMESTAMPTZ NULL,
  "version" BIGINT NOT NULL,
  "priority" SMALLINT NULL,
  PRIMARY KEY ("task_name", "task_instance")
);

/* Create index "execution_time_idx" to table: "scheduled_tasks" */
CREATE INDEX "execution_time_idx" ON "scheduled_tasks"("execution_time");

/* Create index "last_heartbeat_idx" to table: "scheduled_tasks" */
CREATE INDEX "last_heartbeat_idx" ON "scheduled_tasks"("last_heartbeat");

/* Create index "priority_execution_time_idx" to table: "scheduled_tasks" */
CREATE INDEX "priority_execution_time_idx" ON "scheduled_tasks"("priority" DESC, "execution_time");

/* Create "market_rent_case" table */
CREATE TABLE "market_rent_case" (
  "id" BIGINT NOT NULL GENERATED ALWAYS AS IDENTITY,
  "method" VARCHAR(100) NULL,
  "channel" VARCHAR(100) NULL,
  "version" INTEGER NULL,
  "status" VARCHAR(100) NULL,
  "pt_case_id" BIGINT NULL,
  "created_date" TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
  "last_modified_date" TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
  "last_modified_by" VARCHAR(100) NULL,
  "property_floor_plan_available" yes_no NULL,
  "type_of_property_renting" property_type NULL,
  "renting_flat_details" VARCHAR(500) NULL,
  "renting_room_details" VARCHAR(500) NULL,
  "other_method_of_rent_details" VARCHAR(500) NULL,
  "floorplan_manual_details" VARCHAR(5000) NULL,
  "property_indoor_features" VARCHAR(5000) NULL,
  "share_property_with_landlord" yes_no NULL,
  "share_property_with_landlord_details" VARCHAR(5000) NULL,
  "rent_payment_frequency" frequency NULL,
  "rent_cost_weekly" DECIMAL(18, 2) NULL,
  "rent_cost_fortnightly" DECIMAL(18, 2) NULL,
  "rent_cost_monthly" DECIMAL(18, 2) NULL,
  "rent_cost_yearly" DECIMAL(18, 2) NULL,
  "rent_includes_council_tax" yes_no NULL,
  "council_tax_frequency" frequency NULL,
  "council_tax_cost_weekly" DECIMAL(18, 2) NULL,
  "council_tax_cost_fortnightly" DECIMAL(18, 2) NULL,
  "council_tax_cost_monthly" DECIMAL(18, 2) NULL,
  "council_tax_cost_yearly" DECIMAL(18, 2) NULL,
  "council_tax_frequency_and_cost_details" VARCHAR(500) NULL,
  "utilities_paid_frequency" frequency NULL,
  "utilities_cost_weekly" DECIMAL(18, 2) NULL,
  "utilities_cost_fortnightly" DECIMAL(18, 2) NULL,
  "utilities_cost_monthly" DECIMAL(18, 2) NULL,
  "utilities_cost_yearly" DECIMAL(18, 2) NULL,
  "utilities_frequency_and_cost_details" VARCHAR(500) NULL,
  "other_household_management_charges" yes_no NULL,
  "rent_inclusive_of_utility_charges" yes_no NULL,
  "applicant_suggested_market_rent" DECIMAL(18, 2) NULL,
  "additional_rental_service_charges_vary" yes_no NULL,
  "varying_additional_rental_service_charges_details" VARCHAR(500) NULL,
  "applicant_suggested_market_rent_reasons" VARCHAR(5000) NULL,
  "additional_prop_info_to_consider_when_determining_rent" yes_no NULL,
  "additional_prop_info_to_consider_when_determining_rent_details" VARCHAR(500) NULL,
  "other_household_management_charges_details" VARCHAR(500) NULL,
  "hearing_requested" yes_no NULL,
  "reason_hearing_requested" VARCHAR(500) NULL,
  "agree_to_decision_without_inspection" yes_no NULL,
  "no_decision_without_inspection_reason" VARCHAR(500) NULL,
  "market_rent_case_type" VARCHAR(100) NULL,
  "language" VARCHAR(100) NULL,
  "applicant_suggested_market_rent_frequency" frequency NULL,
  "applicant_suggested_market_rent_frequency_other" VARCHAR(500) NULL,
  PRIMARY KEY ("id"),
  CONSTRAINT "market_rent_case_pt_case_id_key" UNIQUE (
    "pt_case_id"
  ),
  CONSTRAINT "market_rent_case_pt_case_id_fkey" FOREIGN KEY ("pt_case_id") REFERENCES "pt_case" (
    "id"
  ) ON UPDATE NO ACTION ON DELETE NO ACTION
);

/* Create index "market_rent_case_pt_case_id_idx" to table: "market_rent_case" */
CREATE INDEX "market_rent_case_pt_case_id_idx" ON "market_rent_case"("pt_case_id");

/* Create "application_fee" table */
CREATE TABLE "application_fee" (
  "id" BIGINT NOT NULL GENERATED ALWAYS AS IDENTITY,
  "request_reference" VARCHAR(100) NULL,
  "request_date" TIMESTAMP NULL,
  "amount" DECIMAL NULL,
  "status" VARCHAR(100) NULL,
  "external_reference" VARCHAR(100) NULL,
  "created_date" TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
  "last_modified_date" TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
  "last_modified_by" VARCHAR(100) NULL,
  "market_rent_case_id" BIGINT NULL,
  PRIMARY KEY ("id"),
  CONSTRAINT "application_fee_market_rent_case_id_key" UNIQUE (
    "market_rent_case_id"
  ),
  CONSTRAINT "application_fee_market_rent_case_id_fkey" FOREIGN KEY ("market_rent_case_id") REFERENCES "market_rent_case" (
    "id"
  ) ON UPDATE NO ACTION ON DELETE NO ACTION
);

/* Create index "application_fee_market_rent_case_id_idx" to table: "application_fee" */
CREATE INDEX "application_fee_market_rent_case_id_idx" ON "application_fee"("market_rent_case_id");

/* Create "application_statement_of_truth" table */
CREATE TABLE "application_statement_of_truth" (
  "id" BIGINT NOT NULL GENERATED ALWAYS AS IDENTITY,
  "completed_date" TIMESTAMP NULL,
  "completed_by" VARCHAR(100) NULL,
  "accepted" yes_no NULL,
  "full_name" VARCHAR(100) NULL,
  "firm_name" VARCHAR(100) NULL,
  "position_held" VARCHAR(100) NULL,
  "created_date" TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
  "last_modified_date" TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
  "last_modified_by" VARCHAR(100) NULL,
  "market_rent_case_id" BIGINT NULL,
  PRIMARY KEY ("id"),
  CONSTRAINT "application_statement_of_truth_market_rent_case_id_key" UNIQUE (
    "market_rent_case_id"
  ),
  CONSTRAINT "application_statement_of_truth_market_rent_case_id_fkey" FOREIGN KEY ("market_rent_case_id") REFERENCES "market_rent_case" (
    "id"
  ) ON UPDATE NO ACTION ON DELETE NO ACTION
);

/* Create index "application_statement_of_truth_market_rent_case_id_idx" to table: "application_statement_of_truth" */
CREATE INDEX "application_statement_of_truth_market_rent_case_id_idx" ON "application_statement_of_truth"("market_rent_case_id");

/* Create "case_notification" table */
CREATE TABLE "case_notification" (
  "id" BIGINT NOT NULL GENERATED ALWAYS AS IDENTITY,
  "pt_case_id" BIGINT NULL,
  "type" VARCHAR(100) NULL,
  "scheduled_date" TIMESTAMP NULL,
  "status" VARCHAR(100) NULL,
  "created_date" TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
  "last_modified_date" TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
  "last_modified_by" VARCHAR(100) NULL,
  "provider_notification_id" UUID NULL,
  "recipient" VARCHAR(100) NOT NULL,
  "submitted_date" TIMESTAMP NULL,
  PRIMARY KEY ("id"),
  CONSTRAINT "case_notification_pt_case_id_fkey" FOREIGN KEY ("pt_case_id") REFERENCES "pt_case" (
    "id"
  ) ON UPDATE NO ACTION ON DELETE NO ACTION
);

/* Create index "case_notification_pt_case_id_idx" to table: "case_notification" */
CREATE INDEX "case_notification_pt_case_id_idx" ON "case_notification"("pt_case_id");

/* Create "case_party_type" table */
CREATE TABLE "case_party_type" (
  "id" BIGINT NOT NULL GENERATED ALWAYS AS IDENTITY,
  "type_name" VARCHAR(100) NULL,
  "created_date" TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
  "last_modified_date" TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
  "last_modified_by" VARCHAR(100) NULL,
  PRIMARY KEY ("id")
);

/* Create "case_party" table */
CREATE TABLE "case_party" (
  "id" BIGINT NOT NULL GENERATED ALWAYS AS IDENTITY,
  "pt_case_id" BIGINT NULL,
  "first_name" VARCHAR(100) NULL,
  "last_name" VARCHAR(100) NULL,
  "organisation_name" VARCHAR(100) NULL,
  "phone_number" VARCHAR(20) NULL,
  "mobile_phone_number" VARCHAR(20) NULL,
  "email_address" VARCHAR(100) NULL,
  "date_of_birth" TIMESTAMP NULL,
  "reference_number_for_communications" VARCHAR(5) NULL,
  "case_party_type_id" BIGINT NULL,
  "created_date" TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
  "last_modified_date" TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
  "last_modified_by" VARCHAR(100) NULL,
  "party_role" VARCHAR(100) NULL,
  "idam_id" UUID NULL,
  "contact_by_text" yes_no NULL,
  "pcq_id" UUID NULL,
  PRIMARY KEY ("id"),
  CONSTRAINT "case_party_case_party_type_id_fkey" FOREIGN KEY ("case_party_type_id") REFERENCES "case_party_type" (
    "id"
  ) ON UPDATE NO ACTION ON DELETE NO ACTION,
  CONSTRAINT "case_party_pt_case_id_fkey" FOREIGN KEY ("pt_case_id") REFERENCES "pt_case" (
    "id"
  ) ON UPDATE NO ACTION ON DELETE NO ACTION
);

/* Create index "case_party_case_party_type_id_idx" to table: "case_party" */
CREATE INDEX "case_party_case_party_type_id_idx" ON "case_party"("case_party_type_id");

/* Create index "case_party_idam_id_idx" to table: "case_party" */
CREATE INDEX "case_party_idam_id_idx" ON "case_party"("idam_id");

/* Create index "case_party_pt_case_id_idx" to table: "case_party" */
CREATE INDEX "case_party_pt_case_id_idx" ON "case_party"("pt_case_id");

/* Create "case_party_access_code" table */
CREATE TABLE "case_party_access_code" (
  "id" BIGINT NOT NULL GENERATED ALWAYS AS IDENTITY,
  "pt_case_id" BIGINT NOT NULL,
  "case_party_id" BIGINT NOT NULL,
  "code" VARCHAR(100) NOT NULL,
  "expires_at" TIMESTAMP NOT NULL,
  "used_at" TIMESTAMP NULL,
  "created_date" TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
  "last_modified_date" TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
  "last_modified_by" VARCHAR(100) NULL,
  PRIMARY KEY ("id"),
  CONSTRAINT "uq_case_party_access_code_case_party" UNIQUE (
    "case_party_id"
  ),
  CONSTRAINT "case_party_access_code_case_party_id_fkey" FOREIGN KEY ("case_party_id") REFERENCES "case_party" (
    "id"
  ) ON UPDATE NO ACTION ON DELETE NO ACTION,
  CONSTRAINT "case_party_access_code_pt_case_id_fkey" FOREIGN KEY ("pt_case_id") REFERENCES "pt_case" (
    "id"
  ) ON UPDATE NO ACTION ON DELETE NO ACTION
);

/* Create "case_party_address" table */
CREATE TABLE "case_party_address" (
  "id" BIGINT NOT NULL GENERATED ALWAYS AS IDENTITY,
  "case_party_id" BIGINT NOT NULL,
  "address_line_1" VARCHAR(100) NULL,
  "address_line_2" VARCHAR(100) NULL,
  "town_city" VARCHAR(100) NULL,
  "county" VARCHAR(100) NULL,
  "post_code" VARCHAR(10) NULL,
  "created_date" TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
  "last_modified_date" TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
  "last_modified_by" VARCHAR(100) NULL,
  PRIMARY KEY ("id"),
  CONSTRAINT "case_party_address_case_party_id_key" UNIQUE (
    "case_party_id"
  ),
  CONSTRAINT "case_party_address_case_party_id_fkey" FOREIGN KEY ("case_party_id") REFERENCES "case_party" (
    "id"
  ) ON UPDATE NO ACTION ON DELETE NO ACTION
);

/* Create "case_party_representative" table */
CREATE TABLE "case_party_representative" (
  "id" BIGINT NOT NULL GENERATED ALWAYS AS IDENTITY,
  "case_party_id" BIGINT NULL,
  "start_date" TIMESTAMP NULL,
  "end_date" TIMESTAMP NULL,
  "is_primary" yes_no NULL,
  "active" yes_no NULL,
  "created_date" TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
  "last_modified_date" TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
  "last_modified_by" VARCHAR(100) NULL,
  PRIMARY KEY ("id"),
  CONSTRAINT "case_party_representative_case_party_id_fkey" FOREIGN KEY ("case_party_id") REFERENCES "case_party" (
    "id"
  ) ON UPDATE NO ACTION ON DELETE NO ACTION
);

/* Create index "case_party_representative_case_party_id_idx" to table: "case_party_representative" */
CREATE INDEX "case_party_representative_case_party_id_idx" ON "case_party_representative"("case_party_id");

/* Create "case_property_address" table */
CREATE TABLE "case_property_address" (
  "id" BIGINT NOT NULL GENERATED ALWAYS AS IDENTITY,
  "pt_case_id" BIGINT NOT NULL,
  "address_line_1" VARCHAR(100) NULL,
  "address_line_2" VARCHAR(100) NULL,
  "town_city" VARCHAR(100) NULL,
  "county" VARCHAR(100) NULL,
  "post_code" VARCHAR(10) NULL,
  "created_date" TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
  "last_modified_date" TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
  "last_modified_by" VARCHAR(100) NULL,
  PRIMARY KEY ("id"),
  CONSTRAINT "case_property_address_pt_case_id_key" UNIQUE (
    "pt_case_id"
  ),
  CONSTRAINT "case_property_address_pt_case_id_fkey" FOREIGN KEY ("pt_case_id") REFERENCES "pt_case" (
    "id"
  ) ON UPDATE NO ACTION ON DELETE NO ACTION
);

/* Create "document" table */
CREATE TABLE "document" (
  "id" BIGINT NOT NULL GENERATED ALWAYS AS IDENTITY,
  "category_id" VARCHAR(50) NULL,
  "url" VARCHAR(1024) NULL,
  "file_name" VARCHAR(255) NULL,
  "binary_url" VARCHAR(1024) NULL,
  "content_type" VARCHAR(255) NULL,
  "description" VARCHAR(100) NULL,
  "size" BIGINT NULL,
  "pt_case_id" BIGINT NULL,
  "created_date" TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
  "last_modified_date" TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
  "last_modified_by" VARCHAR(100) NULL,
  "document_type" document_type NOT NULL,
  PRIMARY KEY ("id"),
  CONSTRAINT "document_pt_case_id_fkey" FOREIGN KEY ("pt_case_id") REFERENCES "pt_case" (
    "id"
  ) ON UPDATE NO ACTION ON DELETE NO ACTION
);

/* Create index "document_pt_case_id_idx" to table: "document" */
CREATE INDEX "document_pt_case_id_idx" ON "document"("pt_case_id");

/* Create "fee_help_with_fees" table */
CREATE TABLE "fee_help_with_fees" (
  "id" BIGINT NOT NULL GENERATED ALWAYS AS IDENTITY,
  "application_fee_id" BIGINT NULL,
  "reference_number" INTEGER NULL,
  "created_date" TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
  "last_modified_date" TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
  "last_modified_by" VARCHAR(100) NULL,
  PRIMARY KEY ("id"),
  CONSTRAINT "fee_help_with_fees_application_fee_id_fkey" FOREIGN KEY ("application_fee_id") REFERENCES "application_fee" (
    "id"
  ) ON UPDATE NO ACTION ON DELETE NO ACTION
);

/* Create index "fee_help_with_fees_application_fee_id_idx" to table: "fee_help_with_fees" */
CREATE INDEX "fee_help_with_fees_application_fee_id_idx" ON "fee_help_with_fees"("application_fee_id");

/* Create "notice_of_rent_change" table */
CREATE TABLE "notice_of_rent_change" (
  "id" BIGINT NOT NULL GENERATED ALWAYS AS IDENTITY,
  "document_uploaded" yes_no NULL,
  "reason_document_not_uploaded" VARCHAR(100) NULL,
  "has_notice_of_increase_validity_challenge" yes_no NULL,
  "notice_of_increase_validity_challenge_document_uploaded" yes_no NULL,
  "reason_notice_of_increase_validity_document_not_uploaded" VARCHAR(100) NULL,
  "created_date" TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
  "last_modified_date" TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
  "last_modified_by" VARCHAR(100) NULL,
  "received_landlord_notice_proposing_new_rent" yes_no NULL,
  "no_upload_of_notice_proposing_new_rent_reason" VARCHAR(500) NULL,
  "rent_increase_to_cause_hardship" yes_no NULL,
  "notice_legally_valid" yes_no NULL,
  "notice_not_legally_valid_details" VARCHAR(500) NULL,
  "market_rent_case_id" BIGINT NULL,
  "hardship_details" VARCHAR(100) NULL,
  PRIMARY KEY ("id"),
  CONSTRAINT "notice_of_rent_change_market_rent_case_id_key" UNIQUE (
    "market_rent_case_id"
  ),
  CONSTRAINT "notice_of_rent_change_market_rent_case_id_fkey" FOREIGN KEY ("market_rent_case_id") REFERENCES "market_rent_case" (
    "id"
  ) ON UPDATE NO ACTION ON DELETE NO ACTION
);

/* Create index "notice_of_rent_change_market_rent_case_id_idx" to table: "notice_of_rent_change" */
CREATE INDEX "notice_of_rent_change_market_rent_case_id_idx" ON "notice_of_rent_change"("market_rent_case_id");

/* Create "representative_type" table */
CREATE TABLE "representative_type" (
  "id" BIGINT NOT NULL GENERATED ALWAYS AS IDENTITY,
  "type_name" VARCHAR(100) NULL,
  "created_date" TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
  "last_modified_date" TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
  "last_modified_by" VARCHAR(100) NULL,
  PRIMARY KEY ("id")
);

/* Create "representative" table */
CREATE TABLE "representative" (
  "id" BIGINT NOT NULL GENERATED ALWAYS AS IDENTITY,
  "case_party_representative_id" BIGINT NULL,
  "representative_type_id" BIGINT NULL,
  "first_name" VARCHAR(100) NULL,
  "last_name" VARCHAR(100) NULL,
  "organisation_name" VARCHAR(100) NULL,
  "email_address" VARCHAR(100) NULL,
  "phone_number" VARCHAR(20) NULL,
  "created_date" TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
  "last_modified_date" TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
  "last_modified_by" VARCHAR(100) NULL,
  PRIMARY KEY ("id"),
  CONSTRAINT "representative_case_party_representative_id_fkey" FOREIGN KEY ("case_party_representative_id") REFERENCES "case_party_representative" (
    "id"
  ) ON UPDATE NO ACTION ON DELETE NO ACTION,
  CONSTRAINT "representative_representative_type_id_fkey" FOREIGN KEY ("representative_type_id") REFERENCES "representative_type" (
    "id"
  ) ON UPDATE NO ACTION ON DELETE NO ACTION
);

/* Create index "representative_case_party_representative_id_idx" to table: "representative" */
CREATE INDEX "representative_case_party_representative_id_idx" ON "representative"("case_party_representative_id");

/* Create index "representative_representative_type_id_idx" to table: "representative" */
CREATE INDEX "representative_representative_type_id_idx" ON "representative"("representative_type_id");

/* Create "tenancy_details" table */
CREATE TABLE "tenancy_details" (
  "id" BIGINT NOT NULL GENERATED ALWAYS AS IDENTITY,
  "document_uploaded" yes_no NULL,
  "created_date" TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
  "last_modified_date" TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
  "last_modified_by" VARCHAR(100) NULL,
  "tenancy_type" VARCHAR(100) NULL,
  "additional_services_provided_in_tenancy" yes_no NULL,
  "additional_services_provided_in_tenancy_details" VARCHAR(5000) NULL,
  "landlord_repairs_details" VARCHAR(5000) NULL,
  "tenant_repairs_details" VARCHAR(5000) NULL,
  "any_tenants_made_property_repairs" yes_no_not_sure NULL,
  "tenants_property_repairs_details" VARCHAR(5000) NULL,
  "tenancy_include_facilities" yes_no NULL,
  "other_facilities_details" VARCHAR(5000) NULL,
  "furniture_provided_in_tenancy" yes_no NULL,
  "furniture_provided_in_tenancy_details" VARCHAR(5000) NULL,
  "tenancy_end_date" TIMESTAMP NULL,
  "current_tenancy_replace_original_tenancy" yes_no_not_sure NULL,
  "original_tenancy_start_date" TIMESTAMP NULL,
  "current_tenancy_start_date" TIMESTAMP NULL,
  "tribunal_previously_determined_tenancy_rent" yes_no NULL,
  "previous_tribunal_case_reference" VARCHAR(30) NULL,
  "copy_of_tenancy_agreement" yes_no NULL,
  "no_tenancy_agreement_reason" VARCHAR(500) NULL,
  "market_rent_case_id" BIGINT NULL,
  PRIMARY KEY ("id"),
  CONSTRAINT "tenancy_details_market_rent_case_id_key" UNIQUE (
    "market_rent_case_id"
  ),
  CONSTRAINT "tenancy_details_market_rent_case_id_fkey" FOREIGN KEY ("market_rent_case_id") REFERENCES "market_rent_case" (
    "id"
  ) ON UPDATE NO ACTION ON DELETE NO ACTION
);

/* Create index "tenancy_details_market_rent_case_id_idx" to table: "tenancy_details" */
CREATE INDEX "tenancy_details_market_rent_case_id_idx" ON "tenancy_details"("market_rent_case_id");
