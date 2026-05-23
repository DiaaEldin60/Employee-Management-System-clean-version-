#!/bin/sh

# MySQL Database Backup Script
# This script runs daily via cron to backup the MySQL database

# Environment variables
MYSQL_HOST="mysql"
MYSQL_PORT="3306"
MYSQL_DATABASE="${MYSQL_DATABASE}"
MYSQL_USER="${MYSQL_USER}"
MYSQL_PASSWORD="${MYSQL_PASSWORD}"
MYSQL_ROOT_PASSWORD="${MYSQL_ROOT_PASSWORD}"
BACKUP_DIR="/backups"
RETENTION_DAYS="${BACKUP_RETENTION_DAYS:-7}"

# Create backup directory if it doesn't exist
mkdir -p ${BACKUP_DIR}

# Generate backup filename with timestamp
BACKUP_FILE="${BACKUP_DIR}/${MYSQL_DATABASE}_backup_$(date +%Y%m%d_%H%M%S).sql"

echo "Starting backup for ${MYSQL_DATABASE} at $(date)"

# Perform backup using mysqldump
mysqldump -h ${MYSQL_HOST} -P ${MYSQL_PORT} -u ${MYSQL_USER} -p${MYSQL_PASSWORD} \
  --single-transaction --routines --triggers --events \
  ${MYSQL_DATABASE} > ${BACKUP_FILE}

# Check if backup was successful
if [ $? -eq 0 ]; then
  echo "Backup completed successfully: ${BACKUP_FILE}"
  
  # Compress the backup
  gzip ${BACKUP_FILE}
  echo "Backup compressed: ${BACKUP_FILE}.gz"
  
  # Remove old backups based on retention policy
  echo "Removing backups older than ${RETENTION_DAYS} days..."
  find ${BACKUP_DIR} -name "${MYSQL_DATABASE}_backup_*.sql.gz" -type f -mtime +${RETENTION_DAYS} -delete
  
  # List remaining backups
  echo "Current backups:"
  ls -lh ${BACKUP_DIR}/${MYSQL_DATABASE}_backup_*.sql.gz 2>/dev/null || echo "No backups found"
else
  echo "ERROR: Backup failed!"
  exit 1
fi

echo "Backup process completed at $(date)"
