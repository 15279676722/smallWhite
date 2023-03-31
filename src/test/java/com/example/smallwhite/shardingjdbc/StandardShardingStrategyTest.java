package com.example.smallwhite.shardingjdbc;

import com.google.common.collect.Range;
import lombok.extern.slf4j.Slf4j;
import org.apache.shardingsphere.api.config.sharding.ShardingRuleConfiguration;
import org.apache.shardingsphere.api.config.sharding.TableRuleConfiguration;
import org.apache.shardingsphere.api.config.sharding.strategy.StandardShardingStrategyConfiguration;
import org.apache.shardingsphere.api.sharding.standard.PreciseShardingAlgorithm;
import org.apache.shardingsphere.api.sharding.standard.PreciseShardingValue;
import org.apache.shardingsphere.api.sharding.standard.RangeShardingAlgorithm;
import org.apache.shardingsphere.api.sharding.standard.RangeShardingValue;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.*;

import static com.example.smallwhite.shardingjdbc.DataSourceUtils.executeSql;

@Slf4j
@SpringBootTest
public class StandardShardingStrategyTest {
    private static DataSource dataSource;

    @BeforeAll
    public static void init() throws SQLException {
        dataSource = DataSourceUtils.init(StandardShardingStrategyTest::initShardingRuleConfiguration, DataSourceUtils.DATA_BASE_1, DataSourceUtils.DATA_BASE_2);
    }

    public static ShardingRuleConfiguration initShardingRuleConfiguration() {
        Map<Range<Comparable>, String> idRangeTableNameMap = new HashMap<>();
        idRangeTableNameMap.put(Range.closed(1L, 3L), "t_user_0");
        idRangeTableNameMap.put(Range.atLeast(4L), "t_user_1");
        System.out.println(idRangeTableNameMap);

        /**
         * 利用sex 去进行分库 1-50放在库 1 50-100放在库2
         * */
        Map<Range<Comparable>, String> sexRangeDataBaseMap = new HashMap<>();
        sexRangeDataBaseMap.put(Range.atMost(50), DataSourceUtils.DATA_BASE_1);
        sexRangeDataBaseMap.put(Range.atLeast(51), DataSourceUtils.DATA_BASE_2);
        System.out.println(idRangeTableNameMap);

        /**
         * 2、配置t_user分片规则
         */
        TableRuleConfiguration tableRuleConfiguration = new TableRuleConfiguration("t_user", "sharding-jdbc-$->{1..2}.t_user_$->{0..1}");


        //设置 =,in 的算法策略
        PreciseShardingAlgorithm preciseShardingAlgorithm = new PreciseShardingAlgorithm() {
            @Override
            public String doSharding(Collection collection, PreciseShardingValue preciseShardingValue) {
                for (Map.Entry<Range<Comparable>, String> rangeStringEntry : idRangeTableNameMap.entrySet()) {
                    Range<Comparable> idRange = rangeStringEntry.getKey();
                    String tableName = rangeStringEntry.getValue();
                    Comparable id = preciseShardingValue.getValue();
                    if (idRange.contains(id)) {
                        System.out.println(String.format("准确路由,id：%s, tableName：%s", id, tableName));
                        return tableName;
                    }
                }
                return null;
            }
        };

        //设置dataBase的=,in 的算法策略
        PreciseShardingAlgorithm databasePreciseShardingAlgorithm = new PreciseShardingAlgorithm() {
            @Override
            public String doSharding(Collection collection, PreciseShardingValue preciseShardingValue) {
                for (Map.Entry<Range<Comparable>, String> rangeStringEntry : sexRangeDataBaseMap.entrySet()) {
                    Range<Comparable> idRange = rangeStringEntry.getKey();
                    String databaseName = rangeStringEntry.getValue();
                    Comparable sex = preciseShardingValue.getValue();
                    if (idRange.contains(sex)) {
                        System.out.println(String.format("准确路由,sex：%s, databaseName：%s", sex, databaseName));
                        return databaseName;
                    }
                }
                return null;
            }
        };


        //设置 BETWEEN AND, >, <, >=, <= 范围算法策略
        RangeShardingAlgorithm rangeShardingAlgorithm = new RangeShardingAlgorithm() {
            @Override
            public Collection<String> doSharding(Collection collection, RangeShardingValue rangeShardingValue) {
                List<String> tableNameList = new ArrayList<>();
                for (Map.Entry<Range<Comparable>, String> rangeStringEntry : idRangeTableNameMap.entrySet()) {
                    Range<Comparable> idRange = rangeStringEntry.getKey();
                    String tableName = rangeStringEntry.getValue();
                    Range valueRange = rangeShardingValue.getValueRange();
                    if (idRange.isConnected(valueRange)) {
                        tableNameList.add(tableName);
                    }
                }
                System.out.println(String.format("范围路由,id：%s, tableNameList：%s", rangeShardingValue, tableNameList));

                return tableNameList;
            }
        };

        //设置database的 BETWEEN AND, >, <, >=, <= 范围算法策略
        RangeShardingAlgorithm databaseRangeShardingAlgorithm = new RangeShardingAlgorithm() {
            @Override
            public Collection<String> doSharding(Collection collection, RangeShardingValue rangeShardingValue) {
                List<String> databaseNameList = new ArrayList<>();
                for (Map.Entry<Range<Comparable>, String> rangeStringEntry : sexRangeDataBaseMap.entrySet()) {
                    Range<Comparable> idRange = rangeStringEntry.getKey();
                    String databaseName = rangeStringEntry.getValue();
                    Range valueRange = rangeShardingValue.getValueRange();
                    if (idRange.isConnected(valueRange)) {
                        databaseNameList.add(databaseName);
                    }
                }
                System.out.println(String.format("范围路由,sex：%s, databaseNameList：%s", rangeShardingValue, databaseNameList));

                return databaseNameList;
            }
        };

        StandardShardingStrategyConfiguration shardingStrategyConfiguration =
                new StandardShardingStrategyConfiguration("id", preciseShardingAlgorithm, rangeShardingAlgorithm);

        StandardShardingStrategyConfiguration databaseShardingStrategyConfiguration =
                new StandardShardingStrategyConfiguration("sex", databasePreciseShardingAlgorithm, databaseRangeShardingAlgorithm);


        tableRuleConfiguration.setTableShardingStrategyConfig(shardingStrategyConfiguration);
        tableRuleConfiguration.setDatabaseShardingStrategyConfig(databaseShardingStrategyConfiguration);

        ShardingRuleConfiguration shardingRuleConfig = new ShardingRuleConfiguration();
        shardingRuleConfig.getTableRuleConfigs().add(tableRuleConfiguration);


        return shardingRuleConfig;
    }


    /**
     * 先进行库的路由再进行 表的路由
     * <p>
     * 准确路由,sex：67, databaseName：sharding-jdbc-2
     * 准确路由,id：1, tableName：t_user_0
     * Logic SQL: insert t_user (id,name,sex) value (?,?,?)
     * Actual SQL: sharding-jdbc-2 ::: insert t_user_0 (id,name,sex) value (?, ?, ?) ::: [1, yangqiang-1, 67]
     * 准确路由,sex：46, databaseName：sharding-jdbc-1
     * 准确路由,id：2, tableName：t_user_0
     * Logic SQL: insert t_user (id,name,sex) value (?,?,?)
     * Actual SQL: sharding-jdbc-1 ::: insert t_user_0 (id,name,sex) value (?, ?, ?) ::: [2, yangqiang-2, 46]
     * 准确路由,sex：19, databaseName：sharding-jdbc-1
     * 准确路由,id：3, tableName：t_user_0
     * Logic SQL: insert t_user (id,name,sex) value (?,?,?)
     * Actual SQL: sharding-jdbc-1 ::: insert t_user_0 (id,name,sex) value (?, ?, ?) ::: [3, yangqiang-3, 19]
     */
    @Test
    public void test1() throws SQLException {
        String sql = "insert t_user (id,name,sex) value (?,?,?)";
        try (Connection connection = dataSource.getConnection();
             PreparedStatement ps = connection.prepareStatement(sql);) {
            for (long id = 1; id <= 50; id++) {
                int parameterIndex = 1;
                ps.setLong(parameterIndex++, id);
                ps.setString(parameterIndex++, "yangqiang-" + id);
                ps.setInt(parameterIndex++, new Random().nextInt(100));
                ps.executeUpdate();
            }
        }
    }

    /**
     * 准确路由,sex：85, databaseName：sharding-jdbc-2
     * 准确路由,id：1, tableName：t_user_0
     * 准确路由,sex：32, databaseName：sharding-jdbc-1
     * 准确路由,id：2, tableName：t_user_0
     * 准确路由,sex：13, databaseName：sharding-jdbc-1
     * 准确路由,id：3, tableName：t_user_0
     * 准确路由,sex：23, databaseName：sharding-jdbc-1
     * 准确路由,id：4, tableName：t_user_1
     * Logic SQL: insert t_user (id,name,sex) value (?,?,?), (?,?,?), (?,?,?), (?,?,?)
     * Actual SQL: sharding-jdbc-2 ::: insert t_user_0 (id,name,sex) value (?, ?, ?) ::: [1, yangqiang-1, 85]
     * Actual SQL: sharding-jdbc-1 ::: insert t_user_0 (id,name,sex) value (?, ?, ?), (?, ?, ?) ::: [2, yangqiang-2, 32, 3, yangqiang-3, 13]
     * Actual SQL: sharding-jdbc-1 ::: insert t_user_1 (id,name,sex) value (?, ?, ?) ::: [4, yangqiang-4, 23]
     * count:4
     */
    @Test
    public void test2() throws SQLException {
        String sql = "insert t_user (id,name,sex) value (?,?,?), (?,?,?), (?,?,?), (?,?,?)";
        try (Connection connection = dataSource.getConnection();
             PreparedStatement ps = connection.prepareStatement(sql);) {
            int parameterIndex = 1;
            for (long id = 1; id <= 4; id++) {
                ps.setLong(parameterIndex++, id);
                ps.setString(parameterIndex++, "yangqiang-" + id);
                ps.setInt(parameterIndex++, new Random().nextInt(100));
            }
            System.out.println("count:" + ps.executeUpdate());
        }
    }

    /**
     * 没有具体的条件可以路由到 对应的库或者表 所以会全库 全表查询
     * Logic SQL: select id,name,sex from t_user
     * SQLStatement: SelectStatementContext(super=CommonSQLStatementContext(sqlStatement=org.apache.shardingsphere.sql.parser.sql.statement.dml.SelectStatement@28d1b2f, tablesContext=org.apache.shardingsphere.sql.parser.binder.segment.table.TablesContext@5f32de90), tablesContext=org.apache.shardingsphere.sql.parser.binder.segment.table.TablesContext@5f32de90, projectionsContext=ProjectionsContext(startIndex=7, stopIndex=17, distinctRow=false, projections=[ColumnProjection(owner=null, name=id, alias=Optional.empty), ColumnProjection(owner=null, name=name, alias=Optional.empty), ColumnProjection(owner=null, name=sex, alias=Optional.empty)]), groupByContext=org.apache.shardingsphere.sql.parser.binder.segment.select.groupby.GroupByContext@1181526e, orderByContext=org.apache.shardingsphere.sql.parser.binder.segment.select.orderby.OrderByContext@5134a7d6, paginationContext=org.apache.shardingsphere.sql.parser.binder.segment.select.pagination.PaginationContext@3b8a8abc, containsSubquery=false)
     * Actual SQL: sharding-jdbc-1 ::: select id,name,sex from t_user_0
     * Actual SQL: sharding-jdbc-1 ::: select id,name,sex from t_user_1
     * Actual SQL: sharding-jdbc-2 ::: select id,name,sex from t_user_0
     * Actual SQL: sharding-jdbc-2 ::: select id,name,sex from t_user_1
     * id:2,name:yangqiang-2,sex:32
     * id:3,name:yangqiang-3,sex:13
     * id:4,name:yangqiang-4,sex:23
     * id:1,name:yangqiang-1,sex:85
     */
    @Test
    public void test3() throws SQLException {
        String sql = "select id,name,sex from t_user";
        executeSql(sql);
    }

    @Test
    public void test4() throws SQLException {
        String sql = "select id,name,sex from t_user where id = ?";
        executeSql(sql, 1L);
    }

    /**
     * 准确路由,id：1, tableName：t_user_0
     * 准确路由,id：2, tableName：t_user_0
     * 准确路由,id：4, tableName：t_user_1
     * 准确路由,id：1, tableName：t_user_0
     * 准确路由,id：2, tableName：t_user_0
     * 准确路由,id：4, tableName：t_user_1
     * Logic SQL: select id,name,sex from t_user where id in (?,?,?)
     * Actual SQL: sharding-jdbc-1 ::: select id,name,sex from t_user_0 where id in (?,?,?) ::: [1, 2, 4]
     * Actual SQL: sharding-jdbc-1 ::: select id,name,sex from t_user_1 where id in (?,?,?) ::: [1, 2, 4]
     * Actual SQL: sharding-jdbc-2 ::: select id,name,sex from t_user_0 where id in (?,?,?) ::: [1, 2, 4]
     * Actual SQL: sharding-jdbc-2 ::: select id,name,sex from t_user_1 where id in (?,?,?) ::: [1, 2, 4]
     * id:2,name:yangqiang-2,sex:32
     * id:4,name:yangqiang-4,sex:23
     * id:1,name:yangqiang-1,sex:85
     */
    @Test
    public void test5() throws SQLException {
        String sql = "select id,name,sex from t_user where id in (?,?,?)";
        executeSql(sql, 1L, 2L, 4L);
    }

    /**
     * 触发全库全表的路由
     * Logic SQL: select id,name,sex from t_user where id != ?
     * SQLStatement: SelectStatementContext(super=CommonSQLStatementContext(sqlStatement=org.apac
     * Actual SQL: sharding-jdbc-1 ::: select id,name,sex from t_user_0 where id != ? ::: [1]
     * Actual SQL: sharding-jdbc-1 ::: select id,name,sex from t_user_1 where id != ? ::: [1]
     * Actual SQL: sharding-jdbc-2 ::: select id,name,sex from t_user_0 where id != ? ::: [1]
     * Actual SQL: sharding-jdbc-2 ::: select id,name,sex from t_user_1 where id != ? ::: [1]
     * <p>
     * id:2,name:yangqiang-2,sex:32
     * id:3,name:yangqiang-3,sex:13
     * id:4,name:yangqiang-4,sex:23
     */
    @Test
    public void test6() throws SQLException {
        String sql = "select id,name,sex from t_user where id != ?";
        executeSql(sql, 1L);
    }


    /**
     * 范围路由,id：RangeShardingValue(logicTableName=t_user, columnName=id, valueRange=[1..2]), tableNameList：[t_user_0]
     * 范围路由,id：RangeShardingValue(logicTableName=t_user, columnName=id, valueRange=[1..2]), tableNameList：[t_user_0]
     * Logic SQL: select id,name,sex from t_user where id between ? and ?
     * Actual SQL: sharding-jdbc-1 ::: select id,name,sex from t_user_0 where id between ? and ? ::: [1, 2]
     * Actual SQL: sharding-jdbc-2 ::: select id,name,sex from t_user_0 where id between ? and ? ::: [1, 2]
     * id:2,name:yangqiang-2,sex:32
     * id:1,name:yangqiang-1,sex:85
     */
    @Test
    public void test7() throws SQLException {
        String sql = "select id,name,sex from t_user where id between ? and ?";
        executeSql(sql, 1L, 2L);
    }

    /**
     * 范围路由,id：RangeShardingValue(logicTableName=t_user, columnName=id, valueRange=[1..2]), tableNameList：[t_user_0]
     * 范围路由,id：RangeShardingValue(logicTableName=t_user, columnName=id, valueRange=[1..2]), tableNameList：[t_user_0]
     * 范围路由,id：RangeShardingValue(logicTableName=t_user, columnName=id, valueRange=[4..+∞)), tableNameList：[t_user_1]
     * 范围路由,id：RangeShardingValue(logicTableName=t_user, columnName=id, valueRange=[4..+∞)), tableNameList：[t_user_1]
     * Logic SQL: select id,name,sex from t_user where id between ? and ? or id>=?
     * Actual SQL: sharding-jdbc-1 ::: select id,name,sex from t_user_0 where id between ? and ? or id>=? ::: [1, 2, 4]
     * Actual SQL: sharding-jdbc-2 ::: select id,name,sex from t_user_0 where id between ? and ? or id>=? ::: [1, 2, 4]
     * Actual SQL: sharding-jdbc-1 ::: select id,name,sex from t_user_1 where id between ? and ? or id>=? ::: [1, 2, 4]
     * Actual SQL: sharding-jdbc-2 ::: select id,name,sex from t_user_1 where id between ? and ? or id>=? ::: [1, 2, 4]
     * id:2,name:yangqiang-2,sex:32
     * id:4,name:yangqiang-4,sex:23
     * id:1,name:yangqiang-1,sex:85
     */
    @Test
    public void test8() throws SQLException {
        String sql = "select id,name,sex from t_user where id between ? and ? or id>=?";
        executeSql(sql, 1L, 2L, 4L);
    }


}
