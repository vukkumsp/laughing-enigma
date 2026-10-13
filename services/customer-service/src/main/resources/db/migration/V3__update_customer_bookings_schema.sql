ALTER TABLE public.customer_bookings
    ADD COLUMN currency VARCHAR(50),
    ADD COLUMN amount numeric(19,2) NOT NULL,
    ADD CONSTRAINT customer_bookings_currency_check
        CHECK (currency IN ('INR'));