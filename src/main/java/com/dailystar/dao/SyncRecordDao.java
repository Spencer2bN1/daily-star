package com.dailystar.dao;

import static com.dailystar.mapper.SyncRecordDynamicSqlSupport.accountId;
import static com.dailystar.mapper.SyncRecordDynamicSqlSupport.clientUpdatedAt;
import static com.dailystar.mapper.SyncRecordDynamicSqlSupport.entitySyncId;
import static com.dailystar.mapper.SyncRecordDynamicSqlSupport.entityType;
import static com.dailystar.mapper.SyncRecordDynamicSqlSupport.id;
import static com.dailystar.mapper.SyncRecordDynamicSqlSupport.operationType;
import static com.dailystar.mapper.SyncRecordDynamicSqlSupport.payloadHash;
import static com.dailystar.mapper.SyncRecordDynamicSqlSupport.payloadJson;
import static com.dailystar.mapper.SyncRecordDynamicSqlSupport.profileId;
import static com.dailystar.mapper.SyncRecordDynamicSqlSupport.serverVersion;
import static com.dailystar.mapper.SyncRecordDynamicSqlSupport.syncRecord;
import static com.dailystar.mapper.SyncRecordDynamicSqlSupport.updatedAt;
import static org.mybatis.dynamic.sql.SqlBuilder.isEqualTo;
import static org.mybatis.dynamic.sql.SqlBuilder.isGreaterThan;

import com.dailystar.entity.SyncRecordEntity;
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
public interface SyncRecordDao {

    BasicColumn[] selectList = BasicColumn.columnList(
        id, accountId, profileId, entityType, entitySyncId, operationType, payloadJson, payloadHash, serverVersion, clientUpdatedAt, updatedAt
    );

    @InsertProvider(type = SqlProviderAdapter.class, method = "insert")
    @Options(useGeneratedKeys = true, keyProperty = "row.id")
    int insert(InsertStatementProvider<SyncRecordEntity> insertStatement);

    @SelectProvider(type = SqlProviderAdapter.class, method = "select")
    @Results(id = "SyncRecordResult", value = {
        @Result(column = "id", property = "id", jdbcType = JdbcType.BIGINT),
        @Result(column = "account_id", property = "accountId", jdbcType = JdbcType.BIGINT),
        @Result(column = "profile_id", property = "profileId", jdbcType = JdbcType.VARCHAR),
        @Result(column = "entity_type", property = "entityType", jdbcType = JdbcType.VARCHAR),
        @Result(column = "entity_sync_id", property = "entitySyncId", jdbcType = JdbcType.VARCHAR),
        @Result(column = "operation_type", property = "operationType", jdbcType = JdbcType.VARCHAR),
        @Result(column = "payload_json", property = "payloadJson", jdbcType = JdbcType.LONGVARCHAR),
        @Result(column = "payload_hash", property = "payloadHash", jdbcType = JdbcType.VARCHAR),
        @Result(column = "server_version", property = "serverVersion", jdbcType = JdbcType.BIGINT),
        @Result(column = "client_updated_at", property = "clientUpdatedAt", jdbcType = JdbcType.BIGINT),
        @Result(column = "updated_at", property = "updatedAt", jdbcType = JdbcType.TIMESTAMP)
    })
    List<SyncRecordEntity> selectMany(SelectStatementProvider selectStatement);

    @SelectProvider(type = SqlProviderAdapter.class, method = "select")
    @ResultMap("SyncRecordResult")
    Optional<SyncRecordEntity> selectOne(SelectStatementProvider selectStatement);

    @UpdateProvider(type = SqlProviderAdapter.class, method = "update")
    int update(UpdateStatementProvider updateStatement);

    @DeleteProvider(type = SqlProviderAdapter.class, method = "delete")
    int delete(DeleteStatementProvider deleteStatement);

    default int insertSelective(SyncRecordEntity row) {
        return MyBatis3Utils.insert(this::insert, row, syncRecord, c ->
            c.map(accountId).toProperty("accountId")
                .map(profileId).toProperty("profileId")
                .map(entityType).toProperty("entityType")
                .map(entitySyncId).toProperty("entitySyncId")
                .map(operationType).toProperty("operationType")
                .map(payloadJson).toProperty("payloadJson")
                .map(payloadHash).toProperty("payloadHash")
                .map(serverVersion).toProperty("serverVersion")
                .map(clientUpdatedAt).toProperty("clientUpdatedAt")
                .map(updatedAt).toProperty("updatedAt")
        );
    }

    default Optional<SyncRecordEntity> selectByAccountAndEntity(Long currentAccountId, String currentEntityType, String currentSyncId) {
        return MyBatis3Utils.selectOne(this::selectOne, selectList, syncRecord, c ->
            c.where(accountId, isEqualTo(currentAccountId))
                .and(entityType, isEqualTo(currentEntityType))
                .and(entitySyncId, isEqualTo(currentSyncId))
        );
    }

    default List<SyncRecordEntity> selectByAccountAndVersionAfter(Long currentAccountId, Long cursor) {
        return MyBatis3Utils.selectList(this::selectMany, selectList, syncRecord, c ->
            c.where(accountId, isEqualTo(currentAccountId))
                .and(serverVersion, isGreaterThan(cursor))
                .orderBy(serverVersion)
        );
    }

    default int updateById(SyncRecordEntity row) {
        return MyBatis3Utils.update(this::update, syncRecord, c ->
            c.set(accountId).equalTo(row::getAccountId)
                .set(profileId).equalTo(row::getProfileId)
                .set(entityType).equalTo(row::getEntityType)
                .set(entitySyncId).equalTo(row::getEntitySyncId)
                .set(operationType).equalTo(row::getOperationType)
                .set(payloadJson).equalTo(row::getPayloadJson)
                .set(payloadHash).equalTo(row::getPayloadHash)
                .set(serverVersion).equalTo(row::getServerVersion)
                .set(clientUpdatedAt).equalTo(row::getClientUpdatedAt)
                .set(updatedAt).equalTo(row::getUpdatedAt)
                .where(id, isEqualTo(row::getId))
        );
    }

    default int deleteByAccountId(Long currentAccountId) {
        return MyBatis3Utils.deleteFrom(this::delete, syncRecord,
            c -> c.where(accountId, isEqualTo(currentAccountId)));
    }
}
