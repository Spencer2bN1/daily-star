CREATE TABLE IF NOT EXISTS t_user (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    username VARCHAR(64) NOT NULL,
    nickname VARCHAR(64) NOT NULL,
    status VARCHAR(32) NOT NULL,
    created_at DATETIME NOT NULL,
    updated_at DATETIME NOT NULL
);

CREATE TABLE IF NOT EXISTS t_auth_account (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    mobile VARCHAR(20) NOT NULL,
    password_hash VARCHAR(255) NOT NULL,
    status VARCHAR(32) NOT NULL,
    created_at DATETIME NOT NULL,
    updated_at DATETIME NOT NULL,
    last_login_at DATETIME NULL,
    CONSTRAINT uk_auth_account_mobile UNIQUE (mobile)
);

CREATE TABLE IF NOT EXISTS t_account_snapshot (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    account_id BIGINT NOT NULL,
    profile_id VARCHAR(64) NOT NULL,
    snapshot_json LONGTEXT NOT NULL,
    snapshot_updated_at BIGINT NOT NULL,
    snapshot_hash VARCHAR(64) NOT NULL,
    sync_revision BIGINT NOT NULL,
    last_device_id VARCHAR(64) NULL,
    created_at DATETIME NOT NULL,
    updated_at DATETIME NOT NULL,
    CONSTRAINT uk_account_snapshot_account UNIQUE (account_id)
);

SET @ddl = (
    SELECT IF(
        EXISTS(
            SELECT 1 FROM information_schema.COLUMNS
            WHERE TABLE_SCHEMA = DATABASE()
              AND TABLE_NAME = 't_account_snapshot'
              AND COLUMN_NAME = 'snapshot_hash'
        ),
        'SELECT 1',
        CONCAT(
            'ALTER TABLE t_account_snapshot ADD COLUMN snapshot_hash VARCHAR(64) NOT NULL DEFAULT ',
            CHAR(39), CHAR(39)
        )
    )
);
PREPARE stmt FROM @ddl;
EXECUTE stmt;
DEALLOCATE PREPARE stmt;

SET @ddl = (
    SELECT IF(
        EXISTS(
            SELECT 1 FROM information_schema.COLUMNS
            WHERE TABLE_SCHEMA = DATABASE()
              AND TABLE_NAME = 't_account_snapshot'
              AND COLUMN_NAME = 'sync_revision'
        ),
        'SELECT 1',
        'ALTER TABLE t_account_snapshot ADD COLUMN sync_revision BIGINT NOT NULL DEFAULT 0'
    )
);
PREPARE stmt FROM @ddl;
EXECUTE stmt;
DEALLOCATE PREPARE stmt;

SET @ddl = (
    SELECT IF(
        EXISTS(
            SELECT 1 FROM information_schema.COLUMNS
            WHERE TABLE_SCHEMA = DATABASE()
              AND TABLE_NAME = 't_account_snapshot'
              AND COLUMN_NAME = 'last_device_id'
        ),
        'SELECT 1',
        'ALTER TABLE t_account_snapshot ADD COLUMN last_device_id VARCHAR(64) NULL'
    )
);
PREPARE stmt FROM @ddl;
EXECUTE stmt;
DEALLOCATE PREPARE stmt;

CREATE TABLE IF NOT EXISTS t_sync_profile_state (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    account_id BIGINT NOT NULL,
    profile_id VARCHAR(64) NOT NULL,
    current_cursor BIGINT NOT NULL,
    created_at DATETIME NOT NULL,
    updated_at DATETIME NOT NULL,
    CONSTRAINT uk_sync_profile_state_account UNIQUE (account_id)
);

CREATE TABLE IF NOT EXISTS t_sync_record (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    account_id BIGINT NOT NULL,
    profile_id VARCHAR(64) NOT NULL,
    entity_type VARCHAR(32) NOT NULL,
    entity_sync_id VARCHAR(64) NOT NULL,
    operation_type VARCHAR(16) NOT NULL,
    payload_json LONGTEXT NULL,
    payload_hash VARCHAR(64) NULL,
    server_version BIGINT NOT NULL,
    client_updated_at BIGINT NOT NULL,
    updated_at DATETIME NOT NULL,
    CONSTRAINT uk_sync_record_entity UNIQUE (account_id, entity_type, entity_sync_id)
);

SET @ddl = (
    SELECT IF(
        EXISTS(
            SELECT 1 FROM information_schema.STATISTICS
            WHERE TABLE_SCHEMA = DATABASE()
              AND TABLE_NAME = 't_sync_record'
              AND INDEX_NAME = 'idx_sync_record_account_version'
        ),
        'SELECT 1',
        'CREATE INDEX idx_sync_record_account_version ON t_sync_record(account_id, server_version)'
    )
);
PREPARE stmt FROM @ddl;
EXECUTE stmt;
DEALLOCATE PREPARE stmt;
