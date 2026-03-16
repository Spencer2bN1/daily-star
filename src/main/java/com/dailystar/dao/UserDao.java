package com.dailystar.dao;

import static com.dailystar.mapper.UserDynamicSqlSupport.createdAt;
import static com.dailystar.mapper.UserDynamicSqlSupport.id;
import static com.dailystar.mapper.UserDynamicSqlSupport.nickname;
import static com.dailystar.mapper.UserDynamicSqlSupport.status;
import static com.dailystar.mapper.UserDynamicSqlSupport.updatedAt;
import static com.dailystar.mapper.UserDynamicSqlSupport.user;
import static com.dailystar.mapper.UserDynamicSqlSupport.username;
import static org.mybatis.dynamic.sql.SqlBuilder.isEqualTo;

import com.dailystar.entity.UserEntity;
import com.dailystar.enums.UserStatusEnum;
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
import org.apache.ibatis.type.JdbcType;
import org.apache.ibatis.type.EnumTypeHandler;
import org.mybatis.dynamic.sql.BasicColumn;
import org.mybatis.dynamic.sql.delete.render.DeleteStatementProvider;
import org.mybatis.dynamic.sql.insert.render.InsertStatementProvider;
import org.mybatis.dynamic.sql.select.render.SelectStatementProvider;
import org.mybatis.dynamic.sql.util.SqlProviderAdapter;
import org.mybatis.dynamic.sql.util.mybatis3.MyBatis3Utils;

@Mapper
public interface UserDao {

    BasicColumn[] selectList = BasicColumn.columnList(id, username, nickname, status, createdAt, updatedAt);

    @InsertProvider(type = SqlProviderAdapter.class, method = "insert")
    @Options(useGeneratedKeys = true, keyProperty = "row.id")
    int insert(InsertStatementProvider<UserEntity> insertStatement);

    @SelectProvider(type = SqlProviderAdapter.class, method = "select")
    @Results(id = "UserResult", value = {
        @Result(column = "id", property = "id", jdbcType = JdbcType.BIGINT),
        @Result(column = "username", property = "username", jdbcType = JdbcType.VARCHAR),
        @Result(column = "nickname", property = "nickname", jdbcType = JdbcType.VARCHAR),
        @Result(column = "status", property = "status", javaType = UserStatusEnum.class, typeHandler = EnumTypeHandler.class, jdbcType = JdbcType.VARCHAR),
        @Result(column = "created_at", property = "createdAt", jdbcType = JdbcType.TIMESTAMP),
        @Result(column = "updated_at", property = "updatedAt", jdbcType = JdbcType.TIMESTAMP)
    })
    List<UserEntity> selectMany(SelectStatementProvider selectStatement);

    @SelectProvider(type = SqlProviderAdapter.class, method = "select")
    @ResultMap("UserResult")
    Optional<UserEntity> selectOne(SelectStatementProvider selectStatement);

    @DeleteProvider(type = SqlProviderAdapter.class, method = "delete")
    int delete(DeleteStatementProvider deleteStatement);

    default int insertSelective(UserEntity row) {
        return MyBatis3Utils.insert(this::insert, row, user, c ->
            c.map(username).toProperty("username")
                .map(nickname).toProperty("nickname")
                .map(status).toProperty("status")
                .map(createdAt).toProperty("createdAt")
                .map(updatedAt).toProperty("updatedAt")
        );
    }

    default Optional<UserEntity> selectByPrimaryKey(Long userId) {
        return MyBatis3Utils.selectOne(this::selectOne, selectList, user, c -> c.where(id, isEqualTo(userId)));
    }

    default List<UserEntity> selectAll() {
        return MyBatis3Utils.selectList(this::selectMany, selectList, user, c -> c.orderBy(id.descending()));
    }

    default int deleteByPrimaryKey(Long userId) {
        return MyBatis3Utils.deleteFrom(this::delete, user, c -> c.where(id, isEqualTo(userId)));
    }
}
