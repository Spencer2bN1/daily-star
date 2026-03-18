package com.dailystar.dao;

import static com.dailystar.mapper.AccountProfileDynamicSqlSupport.accountId;
import static com.dailystar.mapper.AccountProfileDynamicSqlSupport.accountProfile;
import static com.dailystar.mapper.AccountProfileDynamicSqlSupport.avatar;
import static com.dailystar.mapper.AccountProfileDynamicSqlSupport.createdAt;
import static com.dailystar.mapper.AccountProfileDynamicSqlSupport.gender;
import static com.dailystar.mapper.AccountProfileDynamicSqlSupport.id;
import static com.dailystar.mapper.AccountProfileDynamicSqlSupport.nickname;
import static com.dailystar.mapper.AccountProfileDynamicSqlSupport.updatedAt;
import static org.mybatis.dynamic.sql.SqlBuilder.isEqualTo;
import static org.mybatis.dynamic.sql.SqlBuilder.isIn;

import com.dailystar.entity.AccountProfileEntity;
import com.dailystar.enums.GenderEnum;
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
public interface AccountProfileDao {

    BasicColumn[] selectList = BasicColumn.columnList(id, accountId, nickname, avatar, gender, createdAt, updatedAt);

    @InsertProvider(type = SqlProviderAdapter.class, method = "insert")
    @Options(useGeneratedKeys = true, keyProperty = "row.id")
    int insert(InsertStatementProvider<AccountProfileEntity> insertStatement);

    @SelectProvider(type = SqlProviderAdapter.class, method = "select")
    @Results(id = "AccountProfileResult", value = {
        @Result(column = "id", property = "id", jdbcType = JdbcType.BIGINT),
        @Result(column = "account_id", property = "accountId", jdbcType = JdbcType.BIGINT),
        @Result(column = "nickname", property = "nickname", jdbcType = JdbcType.VARCHAR),
        @Result(column = "avatar", property = "avatar", jdbcType = JdbcType.VARCHAR),
        @Result(column = "gender", property = "gender", javaType = GenderEnum.class, typeHandler = EnumTypeHandler.class, jdbcType = JdbcType.VARCHAR),
        @Result(column = "created_at", property = "createdAt", jdbcType = JdbcType.TIMESTAMP),
        @Result(column = "updated_at", property = "updatedAt", jdbcType = JdbcType.TIMESTAMP)
    })
    List<AccountProfileEntity> selectMany(SelectStatementProvider selectStatement);

    @SelectProvider(type = SqlProviderAdapter.class, method = "select")
    @ResultMap("AccountProfileResult")
    Optional<AccountProfileEntity> selectOne(SelectStatementProvider selectStatement);

    @UpdateProvider(type = SqlProviderAdapter.class, method = "update")
    int update(UpdateStatementProvider updateStatement);

    @DeleteProvider(type = SqlProviderAdapter.class, method = "delete")
    int delete(DeleteStatementProvider deleteStatement);

    default int insertSelective(AccountProfileEntity row) {
        return MyBatis3Utils.insert(this::insert, row, accountProfile, c ->
            c.map(accountId).toProperty("accountId")
                .map(nickname).toPropertyWhenPresent("nickname", row::getNickname)
                .map(avatar).toPropertyWhenPresent("avatar", row::getAvatar)
                .map(gender).toPropertyWhenPresent("gender", row::getGender)
                .map(createdAt).toProperty("createdAt")
                .map(updatedAt).toProperty("updatedAt")
        );
    }

    default Optional<AccountProfileEntity> selectByAccountId(Long currentAccountId) {
        return MyBatis3Utils.selectOne(this::selectOne, selectList, accountProfile,
            c -> c.where(accountId, isEqualTo(currentAccountId)));
    }

    default List<AccountProfileEntity> selectByAccountIds(List<Long> currentAccountIds) {
        if (currentAccountIds == null || currentAccountIds.isEmpty()) {
            return java.util.Collections.emptyList();
        }
        return MyBatis3Utils.selectList(this::selectMany, selectList, accountProfile,
            c -> c.where(accountId, isIn(currentAccountIds)));
    }

    default int updateById(AccountProfileEntity row) {
        return MyBatis3Utils.update(this::update, accountProfile, c ->
            c.set(accountId).equalTo(row::getAccountId)
                .set(nickname).equalToWhenPresent(row::getNickname)
                .set(avatar).equalToWhenPresent(row::getAvatar)
                .set(gender).equalToWhenPresent(row::getGender)
                .set(createdAt).equalTo(row::getCreatedAt)
                .set(updatedAt).equalTo(row::getUpdatedAt)
                .where(id, isEqualTo(row::getId))
        );
    }
}
