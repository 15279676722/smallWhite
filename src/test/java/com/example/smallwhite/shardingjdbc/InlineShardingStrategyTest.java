package com.example.smallwhite.shardingjdbc;

import lombok.extern.slf4j.Slf4j;
import org.apache.shardingsphere.api.config.sharding.ShardingRuleConfiguration;
import org.apache.shardingsphere.api.config.sharding.TableRuleConfiguration;
import org.apache.shardingsphere.api.config.sharding.strategy.InlineShardingStrategyConfiguration;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.Random;

import static com.example.smallwhite.shardingjdbc.DataSourceUtils.executeSql;

@Slf4j
@SpringBootTest
public class InlineShardingStrategyTest {


    private static DataSource dataSource;

    @BeforeAll
    public static void init() throws SQLException {
        dataSource = DataSourceUtils.init(InlineShardingStrategyTest::initShardingRuleConfiguration, DataSourceUtils.DATA_BASE_1, DataSourceUtils.DATA_BASE_2);
    }

    public static ShardingRuleConfiguration initShardingRuleConfiguration() {
        /**
         * 2、配置t_user分片规则
         */
        TableRuleConfiguration userRuleConfiguration =
                new TableRuleConfiguration("t_user", "sharding-jdbc-$->{1..2}.t_user_$->{0..1}");

        InlineShardingStrategyConfiguration userTableShardingStrategy = new InlineShardingStrategyConfiguration("id", "t_user_$->{id%2}");

        userRuleConfiguration.setTableShardingStrategyConfig(userTableShardingStrategy);

        InlineShardingStrategyConfiguration dataBaseShardingStrategy = new InlineShardingStrategyConfiguration("sex", "sharding-jdbc-$->{sex%2+1}");

        userRuleConfiguration.setDatabaseShardingStrategyConfig(dataBaseShardingStrategy);
        ShardingRuleConfiguration shardingRuleConfig = new ShardingRuleConfiguration();
        shardingRuleConfig.getTableRuleConfigs().add(userRuleConfiguration);

        return shardingRuleConfig;
    }


    @Test
    public void test1() throws SQLException {
        String sql = "insert t_user (id,name,sex) value (?,?,?)";
        try (Connection connection = dataSource.getConnection();
             PreparedStatement ps = connection.prepareStatement(sql);) {
            for (long id = 1; id <= 4; id++) {
                int parameterIndex = 1;
                ps.setLong(parameterIndex++, id);
                ps.setString(parameterIndex++, "yangqiang-" + id);
                ps.setInt(parameterIndex++, new Random().nextInt(2));
                ps.executeUpdate();
            }
        }
    }


    @Test
    public void test2() throws SQLException {
        String sql = "insert t_user (id,name,sex) value (?,?,?), (?,?,?), (?,?,?), (?,?,?)";
        try (Connection connection = dataSource.getConnection();
             PreparedStatement ps = connection.prepareStatement(sql);) {
            int parameterIndex = 1;
            for (long id = 5; id <= 8; id++) {
                ps.setLong(parameterIndex++, id);
                ps.setString(parameterIndex++, "yangqiang-" + id);
                ps.setInt(parameterIndex++, new Random().nextInt(2));
            }
            System.out.println("count:" + ps.executeUpdate());
        }
    }

    @Test
    public void test3() throws SQLException {
        String sql = "select id,name,sex from t_user";
        executeSql(sql);
    }


    /**
     * 因为 id字段是用来去做分表的操作
     * id =1 索引到对应的表是t_user_1 但是并不能知道属于哪个库 索引会对所有库下的t_user_1 表进行查询
     * Logic SQL: select id,name,sex from t_user where id = 1
     * Actual SQL: sharding-jdbc-1 ::: select id,name,sex from t_user_1 where id = 1
     * Actual SQL: sharding-jdbc-2 ::: select id,name,sex from t_user_1 where id = 1
     */
    @Test
    public void test4() throws SQLException {
        String sql = "select id,name,sex from t_user where id = 1";
        executeSql(sql);
    }

    /**
     * 因为 id字段是用来去做分表的操作
     * id =1 索引到对应的表是t_user_1
     * sex 字段是用来作为分库子弹
     * sex =0 可以找到对应的库为sharding-jdbc-1
     * 所以只会查询sharding-jdbc-1 的t_user_1 表
     * Logic SQL: select id,name,sex from t_user where id = 1 and sex = 0
     * Actual SQL: sharding-jdbc-1 ::: select id,name,sex from t_user_1 where id = 1 and sex = 0
     */
    @Test
    public void test5() throws SQLException {
        String sql = "select id,name,sex from t_user where id = 1 and sex = 0";
        executeSql(sql);
    }

    /**
     * 我们已经确定了sex=0 ->sharding-jdbc-1
     * id in (1,2) 分别索引到 t_user_0 t_user_1表
     * Logic SQL: select id,name,sex from t_user where id in (1,2) and sex = 0
     * Actual SQL: sharding-jdbc-1 ::: select id,name,sex from t_user_0 where id in (1,2) and sex = 0
     * Actual SQL: sharding-jdbc-1 ::: select id,name,sex from t_user_1 where id in (1,2) and sex = 0
     */
    @Test
    public void test6() throws SQLException {
        String sql = "select id,name,sex from t_user where id in (?,?) and sex = ?";
        executeSql(sql, 1L, 2L, 32);
    }

    /**
     * !=,not in,<> 这种类型的查询没办法确定 具体索引到哪张表哪个库的话
     * 就会路由到所有库所有表
     */
    @Test
    public void test7() throws SQLException {
        String sql = "select id,name,sex from t_user where id != ?";
        executeSql(sql, 1L);
    }

    /**
     * InlineShardingStrategy策略不支持对分片字段采用>=、<=、>、<、BETWEEN查询
     * java.lang.IllegalStateException:
     * Inline strategy cannot support this type sharding:RangeRouteValue(columnName=id, tableName=t_user, valueRange=[1..10])
     */
    @Test
    public void test8() throws SQLException {
        String sql = "select id,name,sex from t_user where id between 1 and 10";
        executeSql(sql);
    }
}
