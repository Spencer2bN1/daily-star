package com.dailystar.dao;

import static com.dailystar.mapper.AccountFollowDynamicSqlSupport.accountFollow;
import static com.dailystar.mapper.AccountFollowDynamicSqlSupport.createdAt;
import static com.dailystar.mapper.AccountFollowDynamicSqlSupport.followeeAccountId;
import static com.dailystar.mapper.AccountFollowDynamicSqlSupport.followerAccountId;
import static com.dailystar.mapper.AccountFollowDynamicSqlSupport.id;
import static org.mybatis.dynamic.sql.SqlBuilder.isEqualTo;

import com.dailystar.entity.AccountFollowEntity;
import com.dailystar.model.AccountMetricCountModel;
import java.util.List;
import java.util.Optional;
import org.apache.ibatis.annotations.Delete;
import org.apache.ibatis.annotations.InsertProvider;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Options;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Result;
import org.apache.ibatis.annotations.ResultMap;
import org.apache.ibatis.annotations.Results;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.SelectProvider;
import org.mybatis.dynamic.sql.BasicColumn;
import org.mybatis.dynamic.sql.insert.render.InsertStatementProvider;
import org.mybatis.dynamic.sql.select.render.SelectStatementProvider;
import org.mybatis.dynamic.sql.util.SqlProviderAdapter;
import org.mybatis.dynamic.sql.util.mybatis3.MyBatis3Utils;
import org.apache.ibatis.annotations.UpdateProvider;
import org.mybatis.dynamic.sql.update.render.UpdateStatementProvider;

@Mapper
public interface AccountFollowDao {

    BasicColumn[] selectList = BasicColumn.columnList(id, followerAccountId, followeeAccountId, createdAt);

    @InsertProvider(type = SqlProviderAdapter.class, method = "insert")
    @Options(useGeneratedKeys = true, keyProperty = "row.id")
    int insert(InsertStatementProvider<AccountFollowEntity> insertStatement);

    @SelectProvider(type = SqlProviderAdapter.class, method = "select")
    @Results(id = "AccountFollowResult", value = {
        @Result(column = "id", property = "id"),
        @Result(column = "follower_account_id", property = "followerAccountId"),
        @Result(column = "followee_account_id", property = "followeeAccountId"),
        @Result(column = "created_at", property = "createdAt")
    })
    List<AccountFollowEntity> selectMany(SelectStatementProvider selectStatement);

    @SelectProvider(type = SqlProviderAdapter.class, method = "select")
    @ResultMap("AccountFollowResult")
    Optional<AccountFollowEntity> selectOne(SelectStatementProvider selectStatement);

    @UpdateProvider(type = SqlProviderAdapter.class, method = "update")
    int update(UpdateStatementProvider updateStatement);

    default int insertSelective(AccountFollowEntity row) {
        return MyBatis3Utils.insert(this::insert, row, accountFollow, c ->
            c.map(followerAccountId).toProperty("followerAccountId")
                .map(followeeAccountId).toProperty("followeeAccountId")
                .map(createdAt).toProperty("createdAt")
        );
    }

    default Optional<AccountFollowEntity> selectByPair(Long followerId, Long followeeIdValue) {
        return MyBatis3Utils.selectOne(this::selectOne, selectList, accountFollow,
            c -> c.where(followerAccountId, isEqualTo(followerId))
                .and(followeeAccountId, isEqualTo(followeeIdValue)));
    }

    @Select("select count(1) from t_account_follow where followee_account_id = #{accountId}")
    long countFollowers(Long accountId);

    @Select("select count(1) from t_account_follow where follower_account_id = #{accountId}")
    long countFollowing(Long accountId);

    @Select("select followee_account_id from t_account_follow where follower_account_id = #{accountId}")
    List<Long> selectFolloweeIdsByFollower(Long accountId);

    @Select({
        "<script>",
        "select followee_account_id as account_id, count(1) as count_value",
        "from t_account_follow",
        "where followee_account_id in",
        "<foreach collection='accountIds' item='accountId' open='(' separator=',' close=')'>",
        "#{accountId}",
        "</foreach>",
        "group by followee_account_id",
        "</script>"
    })
    @Results({
        @Result(column = "account_id", property = "accountId"),
        @Result(column = "count_value", property = "countValue")
    })
    List<AccountMetricCountModel> countFollowersByAccountIds(@Param("accountIds") List<Long> accountIds);

    @Select({
        "<script>",
        "select follower_account_id as account_id, count(1) as count_value",
        "from t_account_follow",
        "where follower_account_id in",
        "<foreach collection='accountIds' item='accountId' open='(' separator=',' close=')'>",
        "#{accountId}",
        "</foreach>",
        "group by follower_account_id",
        "</script>"
    })
    @Results({
        @Result(column = "account_id", property = "accountId"),
        @Result(column = "count_value", property = "countValue")
    })
    List<AccountMetricCountModel> countFollowingByAccountIds(@Param("accountIds") List<Long> accountIds);

    @Select({
        "<script>",
        "select followee_account_id",
        "from t_account_follow",
        "where follower_account_id = #{followerId}",
        "and followee_account_id in",
        "<foreach collection='accountIds' item='accountId' open='(' separator=',' close=')'>",
        "#{accountId}",
        "</foreach>",
        "</script>"
    })
    List<Long> selectFolloweeIdsByFollowerAndTargets(@Param("followerId") Long followerId, @Param("accountIds") List<Long> accountIds);

    @Select("select * from t_account_follow where followee_account_id = #{accountId} order by created_at desc, id desc limit #{limit} offset #{offset}")
    @ResultMap("AccountFollowResult")
    List<AccountFollowEntity> selectFollowersPage(Long accountId, long offset, int limit);

    @Select("select * from t_account_follow where follower_account_id = #{accountId} order by created_at desc, id desc limit #{limit} offset #{offset}")
    @ResultMap("AccountFollowResult")
    List<AccountFollowEntity> selectFollowingPage(Long accountId, long offset, int limit);

    @Delete("delete from t_account_follow where follower_account_id = #{followerId} and followee_account_id = #{followeeId}")
    int deleteByPair(Long followerId, Long followeeId);
}
