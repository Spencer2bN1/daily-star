package com.dailystar.dao;

import static com.dailystar.mapper.SyncProfileStateDynamicSqlSupport.accountId;
import static com.dailystar.mapper.SyncProfileStateDynamicSqlSupport.createdAt;
import static com.dailystar.mapper.SyncProfileStateDynamicSqlSupport.currentCursor;
import static com.dailystar.mapper.SyncProfileStateDynamicSqlSupport.id;
import static com.dailystar.mapper.SyncProfileStateDynamicSqlSupport.profileId;
import static com.dailystar.mapper.SyncProfileStateDynamicSqlSupport.syncProfileState;
import static com.dailystar.mapper.SyncProfileStateDynamicSqlSupport.updatedAt;
import static org.mybatis.dynamic.sql.SqlBuilder.isEqualTo;

import com.dailystar.entity.SyncProfileStateEntity;
import java.util.List;
import java.util.Optional;
import org.apache.ibatis.annotations.InsertProvider;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Options;
import org.apache.ibatis.annotations.Result;
import org.apache.ibatis.annotations.ResultMap;
import org.apache.ibatis.annotations.Results;
import org.apache.ibatis.annotations.SelectProvider;
import org.apache.ibatis.annotations.DeleteProvider;
import org.apache.ibatis.annotations.UpdateProvider;
import org.apache.ibatis.type.JdbcType;
import org.mybatis.dynamic.sql.BasicColumn;
import org.mybatis.dynamic.sql.insert.render.InsertStatementProvider;
import org.mybatis.dynamic.sql.delete.render.DeleteStatementProvider;
import org.mybatis.dynamic.sql.select.render.SelectStatementProvider;
import org.mybatis.dynamic.sql.update.render.UpdateStatementProvider;
import org.mybatis.dynamic.sql.util.SqlProviderAdapter;
import org.mybatis.dynamic.sql.util.mybatis3.MyBatis3Utils;

@Mapper
public interface SyncProfileStateDao {

    BasicColumn[] selectList = BasicColumn.columnList(id, accountId, profileId, currentCursor, createdAt, updatedAt);

    @InsertProvider(type = SqlProviderAdapter.class, method = "insert")
    @Options(useGeneratedKeys = true, keyProperty = "row.id")
    int insert(InsertStatementProvider<SyncProfileStateEntity> insertStatement);

    @SelectProvider(type = SqlProviderAdapter.class, method = "select")
    @Results(id = "SyncProfileStateResult", value = {
        @Result(column = "id", property = "id", jdbcType = JdbcType.BIGINT),
        @Result(column = "account_id", property = "accountId", jdbcType = JdbcType.BIGINT),
        @Result(column = "profile_id", property = "profileId", jdbcType = JdbcType.VARCHAR),
        @Result(column = "current_cursor", property = "currentCursor", jdbcType = JdbcType.BIGINT),
        @Result(column = "created_at", property = "createdAt", jdbcType = JdbcType.TIMESTAMP),
        @Result(column = "updated_at", property = "updatedAt", jdbcType = JdbcType.TIMESTAMP)
    })
    List<SyncProfileStateEntity> selectMany(SelectStatementProvider selectStatement);

    @SelectProvider(type = SqlProviderAdapter.class, method = "select")
    @ResultMap("SyncProfileStateResult")
    Optional<SyncProfileStateEntity> selectOne(SelectStatementProvider selectStatement);

    @UpdateProvider(type = SqlProviderAdapter.class, method = "update")
    int update(UpdateStatementProvider updateStatement);

    @DeleteProvider(type = SqlProviderAdapter.class, method = "delete")
    int delete(DeleteStatementProvider deleteStatement);

    default int insertSelective(SyncProfileStateEntity row) {
        return MyBatis3Utils.insert(this::insert, row, syncProfileState, c ->
            c.map(accountId).toProperty("accountId")
                .map(profileId).toProperty("profileId")
                .map(currentCursor).toProperty("currentCursor")
                .map(createdAt).toProperty("createdAt")
                .map(updatedAt).toProperty("updatedAt")
        );
    }

    default Optional<SyncProfileStateEntity> selectByAccountId(Long currentAccountId) {
        return MyBatis3Utils.selectOne(this::selectOne, selectList, syncProfileState,
            c -> c.where(accountId, isEqualTo(currentAccountId)));
    }

    default int updateById(SyncProfileStateEntity row) {
        return MyBatis3Utils.update(this::update, syncProfileState, c ->
            c.set(accountId).equalTo(row::getAccountId)
                .set(profileId).equalTo(row::getProfileId)
                .set(currentCursor).equalTo(row::getCurrentCursor)
                .set(createdAt).equalTo(row::getCreatedAt)
                .set(updatedAt).equalTo(row::getUpdatedAt)
                .where(id, isEqualTo(row::getId))
        );
    }

    default int deleteByAccountId(Long currentAccountId) {
        return MyBatis3Utils.deleteFrom(this::delete, syncProfileState,
            c -> c.where(accountId, isEqualTo(currentAccountId)));
    }
}
