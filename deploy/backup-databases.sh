#!/usr/bin/env bash
set -euo pipefail
umask 077
cd "$(dirname "$0")/.."
task_backup_root="${BACKUP_DIR:-./backups}"
task_backup_dir="$task_backup_root/$(date -u +%Y%m%dT%H%M%SZ)-$$"
mkdir -p "$task_backup_dir"
# Optional service arguments allow a subset; the default backs up every database.
if [ "$#" -eq 0 ]; then
  set -- auth-db user-db catalog-db booking-db notification-db payment-db review-db admin-db
fi
for task_db_service in "$@"; do
  case "$task_db_service" in
    auth-db|user-db|catalog-db|booking-db|notification-db|payment-db|review-db|admin-db) ;;
    *) echo "Unknown database service: $task_db_service" >&2; exit 2 ;;
  esac
  # COMPOSE_FILE / COMPOSE_PROJECT_NAME / --env-file through COMPOSE_ENV_FILES are supported.
  docker compose exec -T "$task_db_service" sh -ec \
    'pg_dump -U "$POSTGRES_USER" -d "$POSTGRES_DB" --format=custom --no-owner --no-acl' \
    > "$task_backup_dir/$task_db_service.dump.partial"
  docker compose exec -T "$task_db_service" pg_restore --list \
    < "$task_backup_dir/$task_db_service.dump.partial" > /dev/null
  mv "$task_backup_dir/$task_db_service.dump.partial" "$task_backup_dir/$task_db_service.dump"
done
if command -v sha256sum > /dev/null 2>&1; then
  (cd "$task_backup_dir" && sha256sum ./*.dump > SHA256SUMS)
else
  (cd "$task_backup_dir" && shasum -a 256 ./*.dump > SHA256SUMS)
fi
echo "Backup saved: $task_backup_dir"
# No automatic deletion: keep retention/offsite copies under operator control.
