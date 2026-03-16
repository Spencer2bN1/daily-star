package com.dailystar.dao;

import static com.dailystar.mapper.AuthAccountDynamicSqlSupport.authAccount;
import static com.dailystar.mapper.AuthAccountDynamicSqlSupport.createdAt;
import static com.dailystar.mapper.AuthAccountDynamicSqlSupport.id;
import static com.dailystar.mapper.AuthAccountDynamicSqlSupport.lastLoginAt;
import static com.dailystar.mapper.AuthAccountDynamicSqlSupport.mobile;
import static com.dailystar.mapper.AuthAccountDynamicSqlSupport.passwordHash;
import static com.dailystar.mapper.AuthAccountDynamicSqlSupport.status;
import static com.dailystar.mapper.AuthAccountDynamicSqlSupport.updatedAt;
import static org.mybatis.dynamic.sql.SqlBuilder.isEqualTo;

import com.dailystar.entity.AuthAccountEntity;
import com.dailystar.enums.AuthAccountStatusEnum;
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
import org.apache.ibatis.type.EnumTypeHandler;
import org.apache.ibatis.type.JdbcType;
import org.mybatis.dynamic.sql.BasicColumn;
import org.mybatis.dynamic.sql.delete.render.DeleteStatementProvider;
import org.mybatis.dynamic.sql.insert.render.InsertStatementProvider;
import org.mybatis.dynamic.sql.select.render.SelectStatementProvider;
import org.mybatis.dynamic.sql.update.render.UpdateStatementProvider;
import org.mybatis.dynamic.sql.util.SqlProviderAdapter;
import org.mybatis.dynamic.sql.util.mybatis3.MyBatis3Utils;

@Mapper
public interface AuthAccountDao {

    BasicColumn[] selectList = BasicColumn.columnList(id, mobile, passwordHash, status, createdAt, updatedAt, lastLoginAt);

    @InsertProvider(type = SqlProviderAdapter.class, method = "insert")
    @Options(useGeneratedKeys = true, keyProperty = "row.id")
    int insert(InsertStatementProvider<AuthAccountEntity> insertStatement);

    @SelectProvider(type = SqlProviderAdapter.class, method = "select")
    @Results(id = "AuthAccountResult", value = {
        @Result(column = "id", property = "id", jdbcType = JdbcType.BIGINT),
        @Result(column = "mobile", property = "mobile", jdbcType = JdbcType.VARCHAR),
        @Result(column = "password_hash", property = "passwordHash", jdbcType = JdbcType.VARCHAR),
        @Result(column = "status", property = "status", javaType = AuthAccountStatusEnum.class, typeHandler = EnumTypeHandler.class, jdbcType = JdbcType.VARCHAR),
        @Result(column = "created_at", property = "createdAt", jdbcType = JdbcType.TIMESTAMP),
        @Result(column = "updated_at", property = "updatedAt", jdbcType = JdbcType.TIMESTAMP),
        @Result(column = "last_login_at", property = "lastLoginAt", jdbcType = JdbcType.TIMESTAMP)
    })
    List<AuthAccountEntity> selectMany(SelectStatementProvider selectStatement);

    @SelectProvider(type = SqlProviderAdapter.class, method = "select")
    @ResultMap("AuthAccountResult")
    Optional<AuthAccountEntity> selectOne(SelectStatementProvider selectStatement);

    @UpdateProvider(type = SqlProviderAdapter.class, method = "update")
    int update(UpdateStatementProvider updateStatement);

    @DeleteProvider(type = SqlProviderAdapter.class, method = "delete")
    int delete(DeleteStatementProvider deleteStatement);

    default int insertSelective(AuthAccountEntity row) {
        return MyBatis3Utils.insert(this::insert, row, authAccount, c ->
            c.map(mobile).toProperty("mobile")
                .map(passwordHash).toProperty("passwordHash")
                .map(status).toProperty("status")
                .map(createdAt).toProperty("createdAt")
                .map(updatedAt).toProperty("updatedAt")
                .map(lastLoginAt).toPropertyWhenPresent("lastLoginAt", row::getLastLoginAt)
        );
    }

    default Optional<AuthAccountEntity> selectById(Long accountId) {
        return MyBatis3Utils.selectOne(this::selectOne, selectList, authAccount, c -> c.where(id, isEqualTo(accountId)));
    }

    default Optional<AuthAccountEntity> selectByMobile(String accountMobile) {
        return MyBatis3Utils.selectOne(this::selectOne, selectList, authAccount, c -> c.where(mobile, isEqualTo(accountMobile)));
    }

    default int updateById(AuthAccountEntity row) {
        return MyBatis3Utils.update(this::update, authAccount, c ->
            c.set(mobile).equalTo(row::getMobile)
                .set(passwordHash).equalTo(row::getPasswordHash)
                .set(status).equalTo(row::getStatus)
                .set(createdAt).equalTo(row::getCreatedAt)
                .set(updatedAt).equalTo(row::getUpdatedAt)
                .set(lastLoginAt).equalToWhenPresent(row::getLastLoginAt)
                .where(id, isEqualTo(row::getId))
        );
    }
}
