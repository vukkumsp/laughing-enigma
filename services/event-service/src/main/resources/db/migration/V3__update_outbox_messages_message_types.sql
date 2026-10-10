-- Step 1: Drop the old constraint
ALTER TABLE outbox_messages DROP CONSTRAINT outbox_messages_message_type_check;

-- Step 2: Add the updated constraint
ALTER TABLE outbox_messages ADD CONSTRAINT outbox_messages_message_type_check
    CHECK (message_type IN ('SEAT_RESERVATION_RESPONSE', 'SEAT_UNRESERVE_RESPONSE'));