package com.dailystar.mapper;

import com.dailystar.enums.GenderEnum;
import java.sql.JDBCType;
import java.time.LocalDateTime;
import org.mybatis.dynamic.sql.AliasableSqlTable;
import org.mybatis.dynamic.sql.SqlColumn;

public final class AccountProfileDynamicSqlSupport {

    public static final AccountProfileTable accountProfile = new AccountProfileTable();
    public static final SqlColumn<Long> id = accountProfile.id;
    public static final SqlColumn<Long> accountId = accountProfile.accountId;
    public static final SqlColumn<String> nickname = accountProfile.nickname;
    public static final SqlColumn<String> avatar = accountProfile.avatar;
    public static final SqlColumn<GenderEnum> gender = accountProfile.gender;
    public static final SqlColumn<LocalDateTime> createdAt = accountProfile.createdAt;
    public static final SqlColumn<LocalDateTime> updatedAt = accountProfile.updatedAt;

    private AccountProfileDynamicSqlSupport() {
    }

    public static final class AccountProfileTable extends AliasableSqlTable<AccountProfileTable> {

        public final SqlColumn<Long> id = column("id", JDBCType.BIGINT);
        public final SqlColumn<Long> accountId = column("account_id", JDBCType.BIGINT);
        public final SqlColumn<String> nickname = column("nickname", JDBCType.VARCHAR);
        public final SqlColumn<String> avatar = column("avatar", JDBCType.VARCHAR);
        public final SqlColumn<GenderEnum> gender = column("gender", JDBCType.VARCHAR);
        public final SqlColumn<LocalDateTime> createdAt = column("created_at", JDBCType.TIMESTAMP);
        public final SqlColumn<LocalDateTime> updatedAt = column("updated_at", JDBCType.TIMESTAMP);

        public AccountProfileTable() {
            super("t_account_profile", AccountProfileTable::new);
        }
    }
}
