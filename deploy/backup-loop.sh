#!/bin/sh
set -eu
umask 077
task_interval="${BACKUP_INTERVAL_SECONDS:-86400}"
case "$task_interval" in ''|*[!0-9]*) echo "Invalid backup interval" >&2; exit 2;; esac
if [ "$task_interval" -lt 60 ]; then echo "Backup interval must be at least 60 seconds" >&2; exit 2; fi
while :; do
  task_directory="/backups/$(date -u +%Y%m%dT%H%M%SZ)-$$"
  mkdir -p "$task_directory"
  task_success=true
  for task_pair in auth-db:auth_db user-db:users_db catalog-db:catalogs_db booking-db:booking_db \
    notification-db:notification_db payment-db:payment_db review-db:review_db admin-db:admin_db; do
    task_host="${task_pair%%:*}"
    task_database="${task_pair#*:}"
    if pg_dump -h "$task_host" -U "$PGUSER" -d "$task_database" --format=custom --no-owner --no-acl \
       > "$task_directory/$task_host.dump.partial" \
       && pg_restore --list "$task_directory/$task_host.dump.partial" > /dev/null; then
      mv "$task_directory/$task_host.dump.partial" "$task_directory/$task_host.dump"
    else
      task_success=false
      echo "Backup failed: $task_host" >&2
    fi
  done
  if [ "$task_success" = true ]; then
    (cd "$task_directory" && sha256sum ./*.dump > SHA256SUMS)
    touch "$task_directory/COMPLETE"
    date +%s > /backups/.last-success.partial
    mv /backups/.last-success.partial /backups/.last-success
    echo "All database backups complete: $task_directory"
  else
    echo "Incomplete backup batch: $task_directory" >&2
  fi
  # Never automatically delete backups. Configure retention and off-site transfer separately.
  sleep "$task_interval"
done
