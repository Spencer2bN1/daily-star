package com.dailystar.mapper;

import java.sql.JDBCType;
import org.mybatis.dynamic.sql.SqlColumn;
import org.mybatis.dynamic.sql.SqlTable;

public final class AccountSnapshotDynamicSqlSupport {

    public static final AccountSnapshot accountSnapshot = new AccountSnapshot();
    public static final SqlColumn<Long> id = accountSnapshot.id;
    public static final SqlColumn<Long> accountId = accountSnapshot.accountId;
    public static final SqlColumn<String> profileId = accountSnapshot.profileId;
    public static final SqlColumn<String> snapshotJson = accountSnapshot.snapshotJson;
    public static final SqlColumn<Long> snapshotUpdatedAt = accountSnapshot.snapshotUpdatedAt;
    public static final SqlColumn<String> snapshotHash = accountSnapshot.snapshotHash;
    public static final SqlColumn<Long> syncRevision = accountSnapshot.syncRevision;
    public static final SqlColumn<String> lastDeviceId = accountSnapshot.lastDeviceId;
    public static final SqlColumn<java.time.LocalDateTime> createdAt = accountSnapshot.createdAt;
    public static final SqlColumn<java.time.LocalDateTime> updatedAt = accountSnapshot.updatedAt;

    private AccountSnapshotDynamicSqlSupport() {
    }

    public static final class AccountSnapshot extends SqlTable {

        public final SqlColumn<Long> id = column("id", JDBCType.BIGINT);
        public final SqlColumn<Long> accountId = column("account_id", JDBCType.BIGINT);
        public final SqlColumn<String> profileId = column("profile_id", JDBCType.VARCHAR);
        public final SqlColumn<String> snapshotJson = column("snapshot_json", JDBCType.LONGVARCHAR);
        public final SqlColumn<Long> snapshotUpdatedAt = column("snapshot_updated_at", JDBCType.BIGINT);
        public final SqlColumn<String> snapshotHash = column("snapshot_hash", JDBCType.VARCHAR);
        public final SqlColumn<Long> syncRevision = column("sync_revision", JDBCType.BIGINT);
        public final SqlColumn<String> lastDeviceId = column("last_device_id", JDBCType.VARCHAR);
        public final SqlColumn<java.time.LocalDateTime> createdAt = column("created_at", JDBCType.TIMESTAMP);
        public final SqlColumn<java.time.LocalDateTime> updatedAt = column("updated_at", JDBCType.TIMESTAMP);

        public AccountSnapshot() {
            super("t_account_snapshot");
        }
    }
}
