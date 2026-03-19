package com.dailystar.dao;

import static com.dailystar.mapper.CommunityPostLikeDynamicSqlSupport.accountId;
import static com.dailystar.mapper.CommunityPostLikeDynamicSqlSupport.communityPostLike;
import static com.dailystar.mapper.CommunityPostLikeDynamicSqlSupport.createdAt;
import static com.dailystar.mapper.CommunityPostLikeDynamicSqlSupport.id;
import static com.dailystar.mapper.CommunityPostLikeDynamicSqlSupport.postId;
import static org.mybatis.dynamic.sql.SqlBuilder.isEqualTo;

import com.dailystar.entity.CommunityPostLikeEntity;
import com.dailystar.model.AccountMetricCountModel;
import com.dailystar.model.PostMetricCountModel;
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

@Mapper
public interface CommunityPostLikeDao {

    BasicColumn[] selectList = BasicColumn.columnList(id, postId, accountId, createdAt);

    @InsertProvider(type = SqlProviderAdapter.class, method = "insert")
    @Options(useGeneratedKeys = true, keyProperty = "row.id")
    int insert(InsertStatementProvider<CommunityPostLikeEntity> insertStatement);

    @SelectProvider(type = SqlProviderAdapter.class, method = "select")
    @Results(id = "CommunityPostLikeResult", value = {
        @Result(column = "id", property = "id"),
        @Result(column = "post_id", property = "postId"),
        @Result(column = "account_id", property = "accountId"),
        @Result(column = "created_at", property = "createdAt")
    })
    List<CommunityPostLikeEntity> selectMany(SelectStatementProvider selectStatement);

    @SelectProvider(type = SqlProviderAdapter.class, method = "select")
    @ResultMap("CommunityPostLikeResult")
    Optional<CommunityPostLikeEntity> selectOne(SelectStatementProvider selectStatement);

    default int insertSelective(CommunityPostLikeEntity row) {
        return MyBatis3Utils.insert(this::insert, row, communityPostLike, c ->
            c.map(postId).toProperty("postId")
                .map(accountId).toProperty("accountId")
                .map(createdAt).toProperty("createdAt")
        );
    }

    default Optional<CommunityPostLikeEntity> selectByPostAndAccount(Long targetPostId, Long targetAccountId) {
        return MyBatis3Utils.selectOne(this::selectOne, selectList, communityPostLike,
            c -> c.where(postId, isEqualTo(targetPostId))
                .and(accountId, isEqualTo(targetAccountId)));
    }

    @Delete("delete from t_community_post_like where post_id = #{postId} and account_id = #{accountId}")
    int deleteByPostAndAccount(Long postId, Long accountId);

    @Select("select count(1) from t_community_post_like where post_id = #{postId}")
    long countByPostId(Long postId);

    @Select({
        "<script>",
        "select post_id, count(1) as count_value",
        "from t_community_post_like",
        "where post_id in",
        "<foreach collection='postIds' item='postId' open='(' separator=',' close=')'>",
        "#{postId}",
        "</foreach>",
        "group by post_id",
        "</script>"
    })
    @Results({
        @Result(column = "post_id", property = "postId"),
        @Result(column = "count_value", property = "countValue")
    })
    List<PostMetricCountModel> countByPostIds(@Param("postIds") List<Long> postIds);

    @Select({
        "<script>",
        "select post_id",
        "from t_community_post_like",
        "where account_id = #{accountId}",
        "and post_id in",
        "<foreach collection='postIds' item='postId' open='(' separator=',' close=')'>",
        "#{postId}",
        "</foreach>",
        "</script>"
    })
    List<Long> selectLikedPostIdsByAccount(@Param("accountId") Long accountId, @Param("postIds") List<Long> postIds);

    @Select({
        "<script>",
        "select p.account_id as account_id, count(l.id) as count_value",
        "from t_community_post_like l",
        "join t_community_post p on p.id = l.post_id",
        "where p.account_id in",
        "<foreach collection='accountIds' item='accountId' open='(' separator=',' close=')'>",
        "#{accountId}",
        "</foreach>",
        "group by p.account_id",
        "</script>"
    })
    @Results({
        @Result(column = "account_id", property = "accountId"),
        @Result(column = "count_value", property = "countValue")
    })
    List<AccountMetricCountModel> countReceivedLikesByAccountIds(@Param("accountIds") List<Long> accountIds);

    @Select({
        "select count(l.id)",
        "from t_community_post_like l",
        "join t_community_post p on p.id = l.post_id",
        "where p.account_id = #{accountId}"
    })
    long countReceivedLikesByAccountId(Long accountId);
}
