package com.dailystar.mapper;

import java.sql.JDBCType;
import java.time.LocalDateTime;
import org.mybatis.dynamic.sql.AliasableSqlTable;
import org.mybatis.dynamic.sql.SqlColumn;

public final class AccountFollowDynamicSqlSupport {

    public static final AccountFollowTable accountFollow = new AccountFollowTable();
    public static final SqlColumn<Long> id = accountFollow.id;
    public static final SqlColumn<Long> followerAccountId = accountFollow.followerAccountId;
    public static final SqlColumn<Long> followeeAccountId = accountFollow.followeeAccountId;
    public static final SqlColumn<LocalDateTime> createdAt = accountFollow.createdAt;

    private AccountFollowDynamicSqlSupport() {
    }

    public static final class AccountFollowTable extends AliasableSqlTable<AccountFollowTable> {

        public final SqlColumn<Long> id = column("id", JDBCType.BIGINT);
        public final SqlColumn<Long> followerAccountId = column("follower_account_id", JDBCType.BIGINT);
        public final SqlColumn<Long> followeeAccountId = column("followee_account_id", JDBCType.BIGINT);
        public final SqlColumn<LocalDateTime> createdAt = column("created_at", JDBCType.TIMESTAMP);

        public AccountFollowTable() {
            super("t_account_follow", AccountFollowTable::new);
        }
    }
}
