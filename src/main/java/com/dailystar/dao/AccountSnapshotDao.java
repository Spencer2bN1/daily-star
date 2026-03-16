package com.dailystar.dao;

import static com.dailystar.mapper.AccountSnapshotDynamicSqlSupport.accountId;
import static com.dailystar.mapper.AccountSnapshotDynamicSqlSupport.accountSnapshot;
import static com.dailystar.mapper.AccountSnapshotDynamicSqlSupport.createdAt;
import static com.dailystar.mapper.AccountSnapshotDynamicSqlSupport.id;
import static com.dailystar.mapper.AccountSnapshotDynamicSqlSupport.lastDeviceId;
import static com.dailystar.mapper.AccountSnapshotDynamicSqlSupport.profileId;
import static com.dailystar.mapper.AccountSnapshotDynamicSqlSupport.snapshotJson;
import static com.dailystar.mapper.AccountSnapshotDynamicSqlSupport.snapshotHash;
import static com.dailystar.mapper.AccountSnapshotDynamicSqlSupport.snapshotUpdatedAt;
import static com.dailystar.mapper.AccountSnapshotDynamicSqlSupport.syncRevision;
import static com.dailystar.mapper.AccountSnapshotDynamicSqlSupport.updatedAt;
import static org.mybatis.dynamic.sql.SqlBuilder.isEqualTo;

import com.dailystar.entity.AccountSnapshotEntity;
import java.util.List;
import java.util.Optional;
import org.apache.ibatis.annotations.DeleteProvider;
import org.apache.ibatis.annotations.InsertProvider;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Options;
import org.apache.ibatis.annotations.Result;
import org.apache.ibatis.annotations.ResultMap;
import org.apache.ibatis.annotations.Results;
import org.apache.ibatis.annotations.SelectProvider;
import org.apache.ibatis.annotations.UpdateProvider;
import org.apache.ibatis.type.JdbcType;
import org.mybatis.dynamic.sql.BasicColumn;
import org.mybatis.dynamic.sql.delete.render.DeleteStatementProvider;
import org.mybatis.dynamic.sql.insert.render.InsertStatementProvider;
import org.mybatis.dynamic.sql.select.render.SelectStatementProvider;
import org.mybatis.dynamic.sql.update.render.UpdateStatementProvider;
import org.mybatis.dynamic.sql.util.SqlProviderAdapter;
import org.mybatis.dynamic.sql.util.mybatis3.MyBatis3Utils;

@Mapper
public interface AccountSnapshotDao {

    BasicColumn[] selectList = BasicColumn.columnList(
        id, accountId, profileId, snapshotJson, snapshotUpdatedAt, snapshotHash, syncRevision, lastDeviceId, createdAt, updatedAt
    );

    @InsertProvider(type = SqlProviderAdapter.class, method = "insert")
    @Options(useGeneratedKeys = true, keyProperty = "row.id")
    int insert(InsertStatementProvider<AccountSnapshotEntity> insertStatement);

    @SelectProvider(type = SqlProviderAdapter.class, method = "select")
    @Results(id = "AccountSnapshotResult", value = {
        @Result(column = "id", property = "id", jdbcType = JdbcType.BIGINT),
        @Result(column = "account_id", property = "accountId", jdbcType = JdbcType.BIGINT),
        @Result(column = "profile_id", property = "profileId", jdbcType = JdbcType.VARCHAR),
        @Result(column = "snapshot_json", property = "snapshotJson", jdbcType = JdbcType.LONGVARCHAR),
        @Result(column = "snapshot_updated_at", property = "snapshotUpdatedAt", jdbcType = JdbcType.BIGINT),
        @Result(column = "snapshot_hash", property = "snapshotHash", jdbcType = JdbcType.VARCHAR),
        @Result(column = "sync_revision", property = "syncRevision", jdbcType = JdbcType.BIGINT),
        @Result(column = "last_device_id", property = "lastDeviceId", jdbcType = JdbcType.VARCHAR),
        @Result(column = "created_at", property = "createdAt", jdbcType = JdbcType.TIMESTAMP),
        @Result(column = "updated_at", property = "updatedAt", jdbcType = JdbcType.TIMESTAMP)
    })
    List<AccountSnapshotEntity> selectMany(SelectStatementProvider selectStatement);

    @SelectProvider(type = SqlProviderAdapter.class, method = "select")
    @ResultMap("AccountSnapshotResult")
    Optional<AccountSnapshotEntity> selectOne(SelectStatementProvider selectStatement);

    @UpdateProvider(type = SqlProviderAdapter.class, method = "update")
    int update(UpdateStatementProvider updateStatement);

    @DeleteProvider(type = SqlProviderAdapter.class, method = "delete")
    int delete(DeleteStatementProvider deleteStatement);

    default int insertSelective(AccountSnapshotEntity row) {
        return MyBatis3Utils.insert(this::insert, row, accountSnapshot, c ->
            c.map(accountId).toProperty("accountId")
                .map(profileId).toProperty("profileId")
                .map(snapshotJson).toProperty("snapshotJson")
                .map(snapshotUpdatedAt).toProperty("snapshotUpdatedAt")
                .map(snapshotHash).toProperty("snapshotHash")
                .map(syncRevision).toProperty("syncRevision")
                .map(lastDeviceId).toProperty("lastDeviceId")
                .map(createdAt).toProperty("createdAt")
                .map(updatedAt).toProperty("updatedAt")
        );
    }

    default Optional<AccountSnapshotEntity> selectByAccountId(Long currentAccountId) {
        return MyBatis3Utils.selectOne(this::selectOne, selectList, accountSnapshot,
            c -> c.where(accountId, isEqualTo(currentAccountId)));
    }

    default int updateById(AccountSnapshotEntity row) {
        return MyBatis3Utils.update(this::update, accountSnapshot, c ->
            c.set(accountId).equalTo(row::getAccountId)
                .set(profileId).equalTo(row::getProfileId)
                .set(snapshotJson).equalTo(row::getSnapshotJson)
                .set(snapshotUpdatedAt).equalTo(row::getSnapshotUpdatedAt)
                .set(snapshotHash).equalTo(row::getSnapshotHash)
                .set(syncRevision).equalTo(row::getSyncRevision)
                .set(lastDeviceId).equalTo(row::getLastDeviceId)
                .set(createdAt).equalTo(row::getCreatedAt)
                .set(updatedAt).equalTo(row::getUpdatedAt)
                .where(id, isEqualTo(row::getId))
        );
    }

    default int deleteByAccountId(Long currentAccountId) {
        return MyBatis3Utils.deleteFrom(this::delete, accountSnapshot,
            c -> c.where(accountId, isEqualTo(currentAccountId)));
    }
}
