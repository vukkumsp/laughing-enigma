-- Create service-specific users

CREATE USER "security-user" WITH PASSWORD 'password';
CREATE USER "customer-user" WITH PASSWORD 'password';
CREATE USER "event-user" WITH PASSWORD 'password';
CREATE USER "payment-user" WITH PASSWORD 'password';
CREATE USER "saga-user" WITH PASSWORD 'password';
CREATE USER "incentive-user" WITH PASSWORD 'password';
CREATE USER "notification-user" WITH PASSWORD 'password';


-- Grant database-level privileges

ALTER DATABASE "security-db" OWNER TO "security-user";
ALTER DATABASE "event-db" OWNER TO "event-user";
ALTER DATABASE "payment-db" OWNER TO "payment-user";
ALTER DATABASE "customer-db" OWNER TO "customer-user";
ALTER DATABASE "saga-db" OWNER TO "saga-user";
ALTER DATABASE "notification-db" OWNER TO "notification-user";
ALTER DATABASE "incentive-db" OWNER TO "incentive-user";