-- new field added as part of HDPD-1017
ALTER TABLE tenancy_details
  ADD COLUMN tenancy_type VARCHAR(100);
