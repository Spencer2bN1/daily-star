package com.dailystar.mapper;

import com.dailystar.enums.AuthAccountStatusEnum;
import java.sql.JDBCType;
import java.time.LocalDateTime;
import org.mybatis.dynamic.sql.AliasableSqlTable;
import org.mybatis.dynamic.sql.SqlColumn;

public final class AuthAccountDynamicSqlSupport {

    public static final AuthAccountTable authAccount = new AuthAccountTable();
    public static final SqlColumn<Long> id = authAccount.id;
    public static final SqlColumn<String> mobile = authAccount.mobile;
    public static final SqlColumn<String> passwordHash = authAccount.passwordHash;
    public static final SqlColumn<AuthAccountStatusEnum> status = authAccount.status;
    public static final SqlColumn<LocalDateTime> createdAt = authAccount.createdAt;
    public static final SqlColumn<LocalDateTime> updatedAt = authAccount.updatedAt;
    public static final SqlColumn<LocalDateTime> lastLoginAt = authAccount.lastLoginAt;

    private AuthAccountDynamicSqlSupport() {
    }

    public static final class AuthAccountTable extends AliasableSqlTable<AuthAccountTable> {

        public final SqlColumn<Long> id = column("id", JDBCType.BIGINT);
        public final SqlColumn<String> mobile = column("mobile", JDBCType.VARCHAR);
        public final SqlColumn<String> passwordHash = column("password_hash", JDBCType.VARCHAR);
        public final SqlColumn<AuthAccountStatusEnum> status = column("status", JDBCType.VARCHAR);
        public final SqlColumn<LocalDateTime> createdAt = column("created_at", JDBCType.TIMESTAMP);
        public final SqlColumn<LocalDateTime> updatedAt = column("updated_at", JDBCType.TIMESTAMP);
        public final SqlColumn<LocalDateTime> lastLoginAt = column("last_login_at", JDBCType.TIMESTAMP);

        public AuthAccountTable() {
            super("t_auth_account", AuthAccountTable::new);
        }
    }
}
