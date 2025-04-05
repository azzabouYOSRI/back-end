-- Ensure the Keycloak user exists
DO
$do$
BEGIN
   IF NOT EXISTS (SELECT FROM pg_roles WHERE rolname = 'keycloak') THEN
      CREATE ROLE keycloak WITH LOGIN PASSWORD 'password';
   END IF;
END
$do$;

-- Ensure the Keycloak database exists
DO
$do$
BEGIN
   IF NOT EXISTS (SELECT FROM pg_database WHERE datname = 'keycloak') THEN
      CREATE DATABASE keycloak OWNER keycloak;
   END IF;
END
$do$;

-- Ensure the Keycloak user has full access to the database
GRANT ALL PRIVILEGES ON DATABASE keycloak TO keycloak;
ALTER ROLE keycloak SET search_path TO public;
GRANT USAGE, CREATE ON SCHEMA public TO keycloak;
GRANT ALL PRIVILEGES ON ALL TABLES IN SCHEMA public TO keycloak;
GRANT ALL PRIVILEGES ON ALL SEQUENCES IN SCHEMA public TO keycloak;
GRANT ALL PRIVILEGES ON ALL FUNCTIONS IN SCHEMA public TO keycloak;
ALTER DEFAULT PRIVILEGES IN SCHEMA public GRANT ALL ON TABLES TO keycloak;
ALTER DEFAULT PRIVILEGES IN SCHEMA public GRANT ALL ON SEQUENCES TO keycloak;
ALTER DEFAULT PRIVILEGES IN SCHEMA public GRANT ALL ON FUNCTIONS TO keycloak;
