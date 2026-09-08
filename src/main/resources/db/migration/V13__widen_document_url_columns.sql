-- Widen the document columns to fit CDAM URLs.
--
-- url and binary_url were VARCHAR(100). A real CDAM URL is longer than that, so the first upload
-- in a deployed environment would have failed on insert:
--
--   http://ccd-case-document-am-api-aat.service.core-compute-aat.internal/cases/documents/<uuid>
--
-- That is 122 characters, and the /binary variant 129. Local development points at
-- http://localhost:4455, giving 74 and 81, which is why nothing caught this sooner.
--
-- file_name and content_type go to 255 to match dm-store, which holds the file itself:
-- documentcontentversion.originaldocumentname and .mimetype are both varchar(255). A filename
-- dm-store accepts now always fits here, and one it rejects never reaches us.
ALTER TABLE document
  ALTER COLUMN url TYPE VARCHAR(1024),
  ALTER COLUMN binary_url TYPE VARCHAR(1024),
  ALTER COLUMN file_name TYPE VARCHAR(255),
  ALTER COLUMN content_type TYPE VARCHAR(255);
