package com.dailystar.mapper;

import java.sql.JDBCType;
import org.mybatis.dynamic.sql.SqlColumn;
import org.mybatis.dynamic.sql.SqlTable;

public final class SyncProfileStateDynamicSqlSupport {

    public static final SyncProfileState syncProfileState = new SyncProfileState();
    public static final SqlColumn<Long> id = syncProfileState.id;
    public static final SqlColumn<Long> accountId = syncProfileState.accountId;
    public static final SqlColumn<String> profileId = syncProfileState.profileId;
    public static final SqlColumn<Long> currentCursor = syncProfileState.currentCursor;
    public static final SqlColumn<java.time.LocalDateTime> createdAt = syncProfileState.createdAt;
    public static final SqlColumn<java.time.LocalDateTime> updatedAt = syncProfileState.updatedAt;

    private SyncProfileStateDynamicSqlSupport() {
    }

    public static final class SyncProfileState extends SqlTable {
        public final SqlColumn<Long> id = column("id", JDBCType.BIGINT);
        public final SqlColumn<Long> accountId = column("account_id", JDBCType.BIGINT);
        public final SqlColumn<String> profileId = column("profile_id", JDBCType.VARCHAR);
        public final SqlColumn<Long> currentCursor = column("current_cursor", JDBCType.BIGINT);
        public final SqlColumn<java.time.LocalDateTime> createdAt = column("created_at", JDBCType.TIMESTAMP);
        public final SqlColumn<java.time.LocalDateTime> updatedAt = column("updated_at", JDBCType.TIMESTAMP);

        public SyncProfileState() {
            super("t_sync_profile_state");
        }
    }
}
