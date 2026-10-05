-- Table for db scheduler
CREATE TABLE scheduled_tasks (
  task_name TEXT NOT NULL,
  task_instance TEXT NOT NULL,
  task_data BYTEA,
  execution_time TIMESTAMP WITH TIME ZONE NOT NULL,
  picked BOOLEAN NOT NULL,
  picked_by TEXT,
  last_success TIMESTAMP WITH TIME ZONE,
  last_failure TIMESTAMP WITH TIME ZONE,
  consecutive_failures INT,
  last_heartbeat TIMESTAMP WITH TIME ZONE,
  version BIGINT NOT NULL,
  priority SMALLINT,
  PRIMARY KEY (task_name, task_instance)
);

CREATE INDEX execution_time_idx ON scheduled_tasks (execution_time);
CREATE INDEX last_heartbeat_idx ON scheduled_tasks (last_heartbeat);
CREATE INDEX priority_execution_time_idx ON scheduled_tasks (priority DESC, execution_time ASC);

-- Case notification table
ALTER TABLE case_notification
  ADD COLUMN provider_notification_id UUID,
  ADD COLUMN recipient VARCHAR(100),
  ADD COLUMN submitted_date TIMESTAMP;

ALTER TABLE case_notification
  ALTER COLUMN recipient_email SET NOT NULL;
