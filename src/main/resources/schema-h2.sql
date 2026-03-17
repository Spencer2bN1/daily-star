CREATE TABLE IF NOT EXISTS t_user (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    username VARCHAR(64) NOT NULL,
    nickname VARCHAR(64) NOT NULL,
    status VARCHAR(32) NOT NULL,
    created_at TIMESTAMP NOT NULL,
    updated_at TIMESTAMP NOT NULL
);

CREATE TABLE IF NOT EXISTS t_auth_account (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    mobile VARCHAR(20) NOT NULL,
    password_hash VARCHAR(255) NOT NULL,
    status VARCHAR(32) NOT NULL,
    created_at TIMESTAMP NOT NULL,
    updated_at TIMESTAMP NOT NULL,
    last_login_at TIMESTAMP NULL,
    CONSTRAINT uk_auth_account_mobile UNIQUE (mobile)
);

CREATE TABLE IF NOT EXISTS t_account_profile (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    account_id BIGINT NOT NULL,
    nickname VARCHAR(64),
    avatar VARCHAR(64),
    gender VARCHAR(32),
    created_at TIMESTAMP NOT NULL,
    updated_at TIMESTAMP NOT NULL,
    CONSTRAINT uk_account_profile_account UNIQUE (account_id)
);

CREATE TABLE IF NOT EXISTS t_account_snapshot (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    account_id BIGINT NOT NULL,
    profile_id VARCHAR(64) NOT NULL,
    snapshot_json CLOB NOT NULL,
    snapshot_updated_at BIGINT NOT NULL,
    snapshot_hash VARCHAR(64) NOT NULL,
    sync_revision BIGINT NOT NULL,
    last_device_id VARCHAR(64),
    created_at TIMESTAMP NOT NULL,
    updated_at TIMESTAMP NOT NULL,
    CONSTRAINT uk_account_snapshot_account UNIQUE (account_id)
);

CREATE TABLE IF NOT EXISTS t_sync_profile_state (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    account_id BIGINT NOT NULL,
    profile_id VARCHAR(64) NOT NULL,
    current_cursor BIGINT NOT NULL,
    created_at TIMESTAMP NOT NULL,
    updated_at TIMESTAMP NOT NULL,
    CONSTRAINT uk_sync_profile_state_account UNIQUE (account_id)
);

CREATE TABLE IF NOT EXISTS t_sync_record (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    account_id BIGINT NOT NULL,
    profile_id VARCHAR(64) NOT NULL,
    entity_type VARCHAR(32) NOT NULL,
    entity_sync_id VARCHAR(64) NOT NULL,
    operation_type VARCHAR(16) NOT NULL,
    payload_json CLOB,
    payload_hash VARCHAR(64),
    server_version BIGINT NOT NULL,
    client_updated_at BIGINT NOT NULL,
    updated_at TIMESTAMP NOT NULL,
    CONSTRAINT uk_sync_record_entity UNIQUE (account_id, entity_type, entity_sync_id)
);

CREATE INDEX idx_sync_record_account_version ON t_sync_record(account_id, server_version);
