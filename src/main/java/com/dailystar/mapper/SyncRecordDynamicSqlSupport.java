package com.dailystar.mapper;

import java.sql.JDBCType;
import org.mybatis.dynamic.sql.SqlColumn;
import org.mybatis.dynamic.sql.SqlTable;

public final class SyncRecordDynamicSqlSupport {

    public static final SyncRecord syncRecord = new SyncRecord();
    public static final SqlColumn<Long> id = syncRecord.id;
    public static final SqlColumn<Long> accountId = syncRecord.accountId;
    public static final SqlColumn<String> profileId = syncRecord.profileId;
    public static final SqlColumn<String> entityType = syncRecord.entityType;
    public static final SqlColumn<String> entitySyncId = syncRecord.entitySyncId;
    public static final SqlColumn<String> operationType = syncRecord.operationType;
    public static final SqlColumn<String> payloadJson = syncRecord.payloadJson;
    public static final SqlColumn<String> payloadHash = syncRecord.payloadHash;
    public static final SqlColumn<Long> serverVersion = syncRecord.serverVersion;
    public static final SqlColumn<Long> clientUpdatedAt = syncRecord.clientUpdatedAt;
    public static final SqlColumn<java.time.LocalDateTime> updatedAt = syncRecord.updatedAt;

    private SyncRecordDynamicSqlSupport() {
    }

    public static final class SyncRecord extends SqlTable {
        public final SqlColumn<Long> id = column("id", JDBCType.BIGINT);
        public final SqlColumn<Long> accountId = column("account_id", JDBCType.BIGINT);
        public final SqlColumn<String> profileId = column("profile_id", JDBCType.VARCHAR);
        public final SqlColumn<String> entityType = column("entity_type", JDBCType.VARCHAR);
        public final SqlColumn<String> entitySyncId = column("entity_sync_id", JDBCType.VARCHAR);
        public final SqlColumn<String> operationType = column("operation_type", JDBCType.VARCHAR);
        public final SqlColumn<String> payloadJson = column("payload_json", JDBCType.LONGVARCHAR);
        public final SqlColumn<String> payloadHash = column("payload_hash", JDBCType.VARCHAR);
        public final SqlColumn<Long> serverVersion = column("server_version", JDBCType.BIGINT);
        public final SqlColumn<Long> clientUpdatedAt = column("client_updated_at", JDBCType.BIGINT);
        public final SqlColumn<java.time.LocalDateTime> updatedAt = column("updated_at", JDBCType.TIMESTAMP);

        public SyncRecord() {
            super("t_sync_record");
        }
    }
}
