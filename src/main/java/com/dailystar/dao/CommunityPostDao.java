package com.dailystar.dao;

import static com.dailystar.mapper.CommunityPostDynamicSqlSupport.accountId;
import static com.dailystar.mapper.CommunityPostDynamicSqlSupport.avatarSnapshot;
import static com.dailystar.mapper.CommunityPostDynamicSqlSupport.communityPost;
import static com.dailystar.mapper.CommunityPostDynamicSqlSupport.completionStatus;
import static com.dailystar.mapper.CommunityPostDynamicSqlSupport.createdAt;
import static com.dailystar.mapper.CommunityPostDynamicSqlSupport.genderSnapshot;
import static com.dailystar.mapper.CommunityPostDynamicSqlSupport.goalCategory;
import static com.dailystar.mapper.CommunityPostDynamicSqlSupport.goalTitle;
import static com.dailystar.mapper.CommunityPostDynamicSqlSupport.id;
import static com.dailystar.mapper.CommunityPostDynamicSqlSupport.nicknameSnapshot;
import static com.dailystar.mapper.CommunityPostDynamicSqlSupport.rewardText;
import static com.dailystar.mapper.CommunityPostDynamicSqlSupport.sharedDate;
import static com.dailystar.mapper.CommunityPostDynamicSqlSupport.updatedAt;
import static org.mybatis.dynamic.sql.SqlBuilder.isEqualTo;

import com.dailystar.entity.CommunityPostEntity;
import com.dailystar.enums.GenderEnum;
import com.dailystar.model.AccountMetricCountModel;
import java.util.List;
import java.util.Optional;
import org.apache.ibatis.annotations.InsertProvider;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Options;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Result;
import org.apache.ibatis.annotations.ResultMap;
import org.apache.ibatis.annotations.Results;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.SelectProvider;
import org.apache.ibatis.annotations.UpdateProvider;
import org.apache.ibatis.type.EnumTypeHandler;
import org.apache.ibatis.type.JdbcType;
import org.mybatis.dynamic.sql.BasicColumn;
import org.mybatis.dynamic.sql.insert.render.InsertStatementProvider;
import org.mybatis.dynamic.sql.select.render.SelectStatementProvider;
import org.mybatis.dynamic.sql.update.render.UpdateStatementProvider;
import org.mybatis.dynamic.sql.util.SqlProviderAdapter;
import org.mybatis.dynamic.sql.util.mybatis3.MyBatis3Utils;

@Mapper
public interface CommunityPostDao {

    BasicColumn[] selectList = BasicColumn.columnList(
        id,
        accountId,
        nicknameSnapshot,
        avatarSnapshot,
        genderSnapshot,
        sharedDate,
        goalTitle,
        goalCategory,
        completionStatus,
        rewardText,
        createdAt,
        updatedAt
    );

    @InsertProvider(type = SqlProviderAdapter.class, method = "insert")
    @Options(useGeneratedKeys = true, keyProperty = "row.id")
    int insert(InsertStatementProvider<CommunityPostEntity> insertStatement);

    @SelectProvider(type = SqlProviderAdapter.class, method = "select")
    @Results(id = "CommunityPostResult", value = {
        @Result(column = "id", property = "id", jdbcType = JdbcType.BIGINT),
        @Result(column = "account_id", property = "accountId", jdbcType = JdbcType.BIGINT),
        @Result(column = "nickname_snapshot", property = "nicknameSnapshot", jdbcType = JdbcType.VARCHAR),
        @Result(column = "avatar_snapshot", property = "avatarSnapshot", jdbcType = JdbcType.VARCHAR),
        @Result(column = "gender_snapshot", property = "genderSnapshot", javaType = GenderEnum.class, typeHandler = EnumTypeHandler.class, jdbcType = JdbcType.VARCHAR),
        @Result(column = "shared_date", property = "sharedDate", jdbcType = JdbcType.VARCHAR),
        @Result(column = "goal_title", property = "goalTitle", jdbcType = JdbcType.VARCHAR),
        @Result(column = "goal_category", property = "goalCategory", jdbcType = JdbcType.VARCHAR),
        @Result(column = "completion_status", property = "completionStatus", jdbcType = JdbcType.VARCHAR),
        @Result(column = "reward_text", property = "rewardText", jdbcType = JdbcType.VARCHAR),
        @Result(column = "created_at", property = "createdAt", jdbcType = JdbcType.TIMESTAMP),
        @Result(column = "updated_at", property = "updatedAt", jdbcType = JdbcType.TIMESTAMP)
    })
    List<CommunityPostEntity> selectMany(SelectStatementProvider selectStatement);

    @SelectProvider(type = SqlProviderAdapter.class, method = "select")
    @ResultMap("CommunityPostResult")
    Optional<CommunityPostEntity> selectOne(SelectStatementProvider selectStatement);

    @UpdateProvider(type = SqlProviderAdapter.class, method = "update")
    int update(UpdateStatementProvider updateStatement);

    default int insertSelective(CommunityPostEntity row) {
        return MyBatis3Utils.insert(this::insert, row, communityPost, c ->
            c.map(accountId).toProperty("accountId")
                .map(nicknameSnapshot).toProperty("nicknameSnapshot")
                .map(avatarSnapshot).toProperty("avatarSnapshot")
                .map(genderSnapshot).toPropertyWhenPresent("genderSnapshot", row::getGenderSnapshot)
                .map(sharedDate).toProperty("sharedDate")
                .map(goalTitle).toProperty("goalTitle")
                .map(goalCategory).toProperty("goalCategory")
                .map(completionStatus).toProperty("completionStatus")
                .map(rewardText).toPropertyWhenPresent("rewardText", row::getRewardText)
                .map(createdAt).toProperty("createdAt")
                .map(updatedAt).toProperty("updatedAt")
        );
    }

    default Optional<CommunityPostEntity> selectById(Long postId) {
        return MyBatis3Utils.selectOne(this::selectOne, selectList, communityPost,
            c -> c.where(id, isEqualTo(postId)));
    }

    default List<CommunityPostEntity> selectFeedPage(long offset, long limit) {
        return MyBatis3Utils.selectList(this::selectMany, selectList, communityPost,
            c -> c.orderBy(createdAt.descending(), id.descending()).limit(limit).offset(offset));
    }

    default Optional<CommunityPostEntity> selectLatestByAccountAndDateAndGoal(Long currentAccountId, String currentSharedDate, String currentGoalTitle) {
        List<CommunityPostEntity> list = MyBatis3Utils.selectList(this::selectMany, selectList, communityPost,
            c -> c.where(accountId, isEqualTo(currentAccountId))
                .and(sharedDate, isEqualTo(currentSharedDate))
                .and(goalTitle, isEqualTo(currentGoalTitle))
                .orderBy(updatedAt.descending(), id.descending())
                .limit(1));
        return list.isEmpty() ? Optional.empty() : Optional.of(list.get(0));
    }

    default int updateById(CommunityPostEntity row) {
        return MyBatis3Utils.update(this::update, communityPost, c ->
            c.set(accountId).equalTo(row::getAccountId)
                .set(nicknameSnapshot).equalTo(row::getNicknameSnapshot)
                .set(avatarSnapshot).equalTo(row::getAvatarSnapshot)
                .set(genderSnapshot).equalToWhenPresent(row::getGenderSnapshot)
                .set(sharedDate).equalTo(row::getSharedDate)
                .set(goalTitle).equalTo(row::getGoalTitle)
                .set(goalCategory).equalTo(row::getGoalCategory)
                .set(completionStatus).equalTo(row::getCompletionStatus)
                .set(rewardText).equalToWhenPresent(row::getRewardText)
                .set(updatedAt).equalTo(row::getUpdatedAt)
                .where(id, isEqualTo(row::getId)));
    }

    @Select("select count(1) from t_community_post where account_id = #{accountId}")
    long countByAccountId(Long accountId);

    @Select({
        "<script>",
        "select account_id, count(1) as count_value",
        "from t_community_post",
        "where account_id in",
        "<foreach collection='accountIds' item='accountId' open='(' separator=',' close=')'>",
        "#{accountId}",
        "</foreach>",
        "group by account_id",
        "</script>"
    })
    @Results({
        @Result(column = "account_id", property = "accountId"),
        @Result(column = "count_value", property = "countValue")
    })
    List<AccountMetricCountModel> countByAccountIds(@Param("accountIds") List<Long> accountIds);
}
