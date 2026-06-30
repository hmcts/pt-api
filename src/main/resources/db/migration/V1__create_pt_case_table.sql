create table pt_case (
    id                  uuid primary key,
    version             integer,
    case_reference      bigint unique,
    applicant_forename  varchar(100)
);
