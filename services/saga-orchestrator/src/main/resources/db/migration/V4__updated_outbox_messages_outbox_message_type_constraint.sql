-- Step 1: Drop the old constraint
ALTER TABLE outbox_messages DROP CONSTRAINT outbox_messages_message_type_check;

-- Step 2: Add the updated constraint
ALTER TABLE outbox_messages ADD CONSTRAINT outbox_messages_message_type_check
    CHECK (message_type IN ('CUSTOMER_VALIDATION_REQUEST', 'SEAT_RESERVATION_REQUEST',
                            'SEAT_UNRESERVE_REQUEST','PAYMENT_ORDER_REQUEST', 'CUSTOMER_BOOKING_REQUEST',
                            'REGISTRATION_COMPLETED', 'REGISTRATION_FAILED'));