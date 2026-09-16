#!/bin/sh
set -eu
task_last_success="$(cat /backups/.last-success)"
task_now="$(date +%s)"
task_max_age="$((${BACKUP_INTERVAL_SECONDS:-86400} + 600))"
[ "$((task_now - task_last_success))" -lt "$task_max_age" ]
