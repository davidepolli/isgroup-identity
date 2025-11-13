#!/usr/bin/env bash
set -Eeuo pipefail
export PGPASSWORD="${POSTGRES_PASSWORD}"

# ---- helpers for safe SQL quoting ----
sql_escape_literal() { sed "s/'/''/g"; }   # for 'single-quoted' SQL literals
sql_escape_ident()  { sed 's/"/""/g'; }    # for "double-quoted" identifiers

APP_USER_LIT=$(printf "%s" "$APP_USER"     | sql_escape_literal)
APP_PASS_LIT=$(printf "%s" "$APP_PASSWORD" | sql_escape_literal)
APP_USER_ID=$(printf  "%s" "$APP_USER"     | sql_escape_ident)

echo ">> Ensuring application role exists (with LOGIN + password)..."
ROLE_EXISTS=$(psql -v ON_ERROR_STOP=1 -U "$POSTGRES_USER" -d "$POSTGRES_DB" -tAc \
  "SELECT 1 FROM pg_roles WHERE rolname='${APP_USER_LIT}'" || true)

if [[ "$ROLE_EXISTS" != "1" ]]; then
  echo "   -> creating role ${APP_USER}"
  psql -v ON_ERROR_STOP=1 -U "$POSTGRES_USER" -d "$POSTGRES_DB" -c \
    "CREATE ROLE \"${APP_USER_ID}\" LOGIN PASSWORD '${APP_PASS_LIT}';"
else
  echo "   -> role exists; ensuring LOGIN and password"
  psql -v ON_ERROR_STOP=1 -U "$POSTGRES_USER" -d "$POSTGRES_DB" -c \
    "ALTER ROLE \"${APP_USER_ID}\" LOGIN PASSWORD '${APP_PASS_LIT}';"
fi

echo ">> Ensuring application database exists..."
DB_EXISTS=$(psql -v ON_ERROR_STOP=1 -U "$POSTGRES_USER" -d "$POSTGRES_DB" -tAc \
  "SELECT 1 FROM pg_database WHERE datname='${APP_DB}'" || true)
if [[ "$DB_EXISTS" != "1" ]]; then
  echo "   -> creating database ${APP_DB} owned by ${APP_USER}"
  createdb -U "$POSTGRES_USER" -O "$APP_USER" "$APP_DB"
else
  echo "   -> database ${APP_DB} already exists; skipping"
fi

echo ">> Granting privileges on ${APP_DB}.public..."
psql -v ON_ERROR_STOP=1 --username "$POSTGRES_USER" --dbname "$APP_DB" <<-EOSQL
  ALTER SCHEMA public OWNER TO "${APP_USER}";
  GRANT ALL ON SCHEMA public TO "${APP_USER}";
EOSQL

echo ">> Initialization complete."