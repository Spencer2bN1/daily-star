package com.dailystar.mapper;

import com.dailystar.enums.GenderEnum;
import java.sql.JDBCType;
import java.time.LocalDateTime;
import org.mybatis.dynamic.sql.AliasableSqlTable;
import org.mybatis.dynamic.sql.SqlColumn;

public final class CommunityPostDynamicSqlSupport {

    public static final CommunityPostTable communityPost = new CommunityPostTable();
    public static final SqlColumn<Long> id = communityPost.id;
    public static final SqlColumn<Long> accountId = communityPost.accountId;
    public static final SqlColumn<String> nicknameSnapshot = communityPost.nicknameSnapshot;
    public static final SqlColumn<String> avatarSnapshot = communityPost.avatarSnapshot;
    public static final SqlColumn<GenderEnum> genderSnapshot = communityPost.genderSnapshot;
    public static final SqlColumn<String> sharedDate = communityPost.sharedDate;
    public static final SqlColumn<String> goalTitle = communityPost.goalTitle;
    public static final SqlColumn<String> goalCategory = communityPost.goalCategory;
    public static final SqlColumn<String> completionStatus = communityPost.completionStatus;
    public static final SqlColumn<String> rewardText = communityPost.rewardText;
    public static final SqlColumn<LocalDateTime> createdAt = communityPost.createdAt;
    public static final SqlColumn<LocalDateTime> updatedAt = communityPost.updatedAt;

    private CommunityPostDynamicSqlSupport() {
    }

    public static final class CommunityPostTable extends AliasableSqlTable<CommunityPostTable> {

        public final SqlColumn<Long> id = column("id", JDBCType.BIGINT);
        public final SqlColumn<Long> accountId = column("account_id", JDBCType.BIGINT);
        public final SqlColumn<String> nicknameSnapshot = column("nickname_snapshot", JDBCType.VARCHAR);
        public final SqlColumn<String> avatarSnapshot = column("avatar_snapshot", JDBCType.VARCHAR);
        public final SqlColumn<GenderEnum> genderSnapshot = column("gender_snapshot", JDBCType.VARCHAR);
        public final SqlColumn<String> sharedDate = column("shared_date", JDBCType.VARCHAR);
        public final SqlColumn<String> goalTitle = column("goal_title", JDBCType.VARCHAR);
        public final SqlColumn<String> goalCategory = column("goal_category", JDBCType.VARCHAR);
        public final SqlColumn<String> completionStatus = column("completion_status", JDBCType.VARCHAR);
        public final SqlColumn<String> rewardText = column("reward_text", JDBCType.VARCHAR);
        public final SqlColumn<LocalDateTime> createdAt = column("created_at", JDBCType.TIMESTAMP);
        public final SqlColumn<LocalDateTime> updatedAt = column("updated_at", JDBCType.TIMESTAMP);

        public CommunityPostTable() {
            super("t_community_post", CommunityPostTable::new);
        }
    }
}
