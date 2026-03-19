package com.dailystar.mapper;

import org.mybatis.dynamic.sql.SqlColumn;
import org.mybatis.dynamic.sql.SqlTable;
import java.sql.JDBCType;

public final class CommunityPostLikeDynamicSqlSupport {

    public static final CommunityPostLike communityPostLike = new CommunityPostLike();
    public static final SqlColumn<Long> id = communityPostLike.id;
    public static final SqlColumn<Long> postId = communityPostLike.postId;
    public static final SqlColumn<Long> accountId = communityPostLike.accountId;
    public static final SqlColumn<java.time.LocalDateTime> createdAt = communityPostLike.createdAt;

    public static final class CommunityPostLike extends SqlTable {
        public final SqlColumn<Long> id = column("id", JDBCType.BIGINT);
        public final SqlColumn<Long> postId = column("post_id", JDBCType.BIGINT);
        public final SqlColumn<Long> accountId = column("account_id", JDBCType.BIGINT);
        public final SqlColumn<java.time.LocalDateTime> createdAt = column("created_at", JDBCType.TIMESTAMP);

        public CommunityPostLike() {
            super("t_community_post_like");
        }
    }
}
