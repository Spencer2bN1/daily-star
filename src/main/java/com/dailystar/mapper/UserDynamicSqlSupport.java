package com.dailystar.mapper;

import com.dailystar.enums.UserStatusEnum;
import java.sql.JDBCType;
import java.time.LocalDateTime;
import org.mybatis.dynamic.sql.AliasableSqlTable;
import org.mybatis.dynamic.sql.SqlColumn;

public final class UserDynamicSqlSupport {

    public static final UserTable user = new UserTable();
    public static final SqlColumn<Long> id = user.id;
    public static final SqlColumn<String> username = user.username;
    public static final SqlColumn<String> nickname = user.nickname;
    public static final SqlColumn<UserStatusEnum> status = user.status;
    public static final SqlColumn<LocalDateTime> createdAt = user.createdAt;
    public static final SqlColumn<LocalDateTime> updatedAt = user.updatedAt;

    private UserDynamicSqlSupport() {
    }

    public static final class UserTable extends AliasableSqlTable<UserTable> {

        public final SqlColumn<Long> id = column("id", JDBCType.BIGINT);
        public final SqlColumn<String> username = column("username", JDBCType.VARCHAR);
        public final SqlColumn<String> nickname = column("nickname", JDBCType.VARCHAR);
        public final SqlColumn<UserStatusEnum> status = column("status", JDBCType.VARCHAR);
        public final SqlColumn<LocalDateTime> createdAt = column("created_at", JDBCType.TIMESTAMP);
        public final SqlColumn<LocalDateTime> updatedAt = column("updated_at", JDBCType.TIMESTAMP);

        public UserTable() {
            super("t_user", UserTable::new);
        }
    }
}
