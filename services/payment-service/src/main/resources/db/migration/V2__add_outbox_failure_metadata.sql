ALTER TABLE outbox_messages
    ADD COLUMN failure_type VARCHAR(50),
    ADD COLUMN failure_reason TEXT,
    ADD COLUMN failed_at TIMESTAMPTZ;