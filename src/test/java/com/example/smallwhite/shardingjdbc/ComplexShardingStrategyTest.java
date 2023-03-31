package com.example.smallwhite.shardingjdbc;

import org.apache.shardingsphere.api.config.sharding.ShardingRuleConfiguration;
import org.apache.shardingsphere.api.config.sharding.TableRuleConfiguration;
import org.apache.shardingsphere.api.config.sharding.strategy.ComplexShardingStrategyConfiguration;
import org.apache.shardingsphere.api.sharding.complex.ComplexKeysShardingAlgorithm;
import org.apache.shardingsphere.api.sharding.complex.ComplexKeysShardingValue;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;

import static com.example.smallwhite.shardingjdbc.DataSourceUtils.executeSql2;

public class ComplexShardingStrategyTest {
    private static DataSource dataSource;

    @BeforeAll
    public static void init() throws SQLException {
        dataSource = DataSourceUtils.init(ComplexShardingStrategyTest::initShardingRuleConfiguration, DataSourceUtils.DATA_BASE_1, DataSourceUtils.DATA_BASE_2);
    }

    public static ShardingRuleConfiguration initShardingRuleConfiguration() {
        /**
         * 2、配置 t_file分片规则
         */
        //逻辑表名
        final String logicTable = "t_file";
        //对应的实际表（3张）
        final String actualDataNodes =
                DataSourceUtils.DATA_BASE_1 + ".t_file_0," + DataSourceUtils.DATA_BASE_1 + ".t_file_1," + DataSourceUtils.DATA_BASE_1 + ".t_file_2";

        TableRuleConfiguration tableRuleConfiguration = new TableRuleConfiguration(logicTable, actualDataNodes);

        ComplexKeysShardingAlgorithm complexKeysShardingAlgorithm = new ComplexKeysShardingAlgorithm() {
            @Override
            public Collection<String> doSharding(Collection collection, ComplexKeysShardingValue complexKeysShardingValue) {
                Map columnNameAndShardingValuesMap = complexKeysShardingValue.getColumnNameAndShardingValuesMap();
                Collection<Integer> storageTypes = (Collection<Integer>) columnNameAndShardingValuesMap.get("storage_type");
                Collection<Long> idList = (Collection<Long>) columnNameAndShardingValuesMap.get("id");
                List<String> tableNames = new ArrayList<>();

                if (storageTypes != null) {
                    for (Integer storageType : storageTypes) {
                        if (storageType == 0) {
                            if (idList != null) {
                                for (Long id : idList) {
                                    if (id % 2 == 0) {
                                        tableNames.add("t_file_0");
                                    } else {
                                        tableNames.add("t_file_1");
                                    }
                                }
                            }else {
                                tableNames.add("t_file_0");
                                tableNames.add("t_file_1");
                            }
                        }else if(storageType==1){
                            tableNames.add("t_file_2");
                        }
                    }
                }
                System.out.println(String.format("路由信息,tableNames：%s, id值：%s, storage_type值：%s", tableNames, idList, storageTypes));
                return tableNames.isEmpty() ? collection : tableNames;
            }
        };

        ComplexShardingStrategyConfiguration complexShardingStrategyConfiguration =
                new ComplexShardingStrategyConfiguration("id,storage_type", complexKeysShardingAlgorithm);

        tableRuleConfiguration.setTableShardingStrategyConfig(complexShardingStrategyConfiguration);


        ShardingRuleConfiguration shardingRuleConfig = new ShardingRuleConfiguration();
        shardingRuleConfig.getTableRuleConfigs().add(tableRuleConfiguration);
        return shardingRuleConfig;
    }

    /**
     * 路由信息,tableNames：[t_file_1], id值：[1], storage_type值：[0]
     * Logic SQL: insert t_file (id,storage_type,name) value (?,?,?)
     * Actual SQL: sharding-jdbc-1 ::: insert t_file_1 (id,storage_type,name) value (?, ?, ?) ::: [1, 0, ShardingSphere-1]
     * 路由信息,tableNames：[t_file_0], id值：[2], storage_type值：[0]
     * Logic SQL: insert t_file (id,storage_type,name) value (?,?,?)
     * Actual SQL: sharding-jdbc-1 ::: insert t_file_0 (id,storage_type,name) value (?, ?, ?) ::: [2, 0, ShardingSphere-2]
     * 路由信息,tableNames：[t_file_2], id值：[3], storage_type值：[1]
     * Logic SQL: insert t_file (id,storage_type,name) value (?,?,?)
     * Actual SQL: sharding-jdbc-1 ::: insert t_file_2 (id,storage_type,name) value (?, ?, ?) ::: [3, 1, ShardingSphere-3]
     * 路由信息,tableNames：[t_file_2], id值：[4], storage_type值：[1]
     * Logic SQL: insert t_file (id,storage_type,name) value (?,?,?)
     * Actual SQL: sharding-jdbc-1 ::: insert t_file_2 (id,storage_type,name) value (?, ?, ?) ::: [4, 1, ShardingSphere-4]
     *
     * */
    @Test
    public void test1() throws SQLException {
        String sql = "insert t_file (id,storage_type,name) value (?,?,?)";
        try (Connection connection = dataSource.getConnection();
             PreparedStatement ps = connection.prepareStatement(sql);) {
            long id = 1;
            for (int storage_type = 0; storage_type <= 1; storage_type++) {
                for (; id <= storage_type * 2 + 2; id++) {
                    int parameterIndex = 1;
                    ps.setLong(parameterIndex++, id);
                    ps.setInt(parameterIndex++, storage_type);
                    ps.setString(parameterIndex++, "ShardingSphere-" + id);
                    ps.executeUpdate();
                }
            }
        }
    }

    /**
     * 路由信息,tableNames：[t_file_2], id值：[1], storage_type值：[1]
     * 路由信息,tableNames：[t_file_0], id值：[2], storage_type值：[0]
     * 路由信息,tableNames：[t_file_2], id值：[3], storage_type值：[1]
     * 路由信息,tableNames：[t_file_0], id值：[4], storage_type值：[0]
     * Logic SQL: insert t_file (id,storage_type,name) value (?,?,?), (?,?,?), (?,?,?), (?,?,?)
     * Actual SQL: sharding-jdbc-1 ::: insert t_file_2 (id,storage_type,name) value (?, ?, ?), (?, ?, ?) ::: [1, 1, ShardingSphere-1, 3, 1, ShardingSphere-3]
     * Actual SQL: sharding-jdbc-1 ::: insert t_file_0 (id,storage_type,name) value (?, ?, ?), (?, ?, ?) ::: [2, 0, ShardingSphere-2, 4, 0, ShardingSphere-4]
     * */
    @Test
    public void test2() throws SQLException {
        String sql = "insert t_file (id,storage_type,name) value (?,?,?), (?,?,?), (?,?,?), (?,?,?)";
        try (Connection connection = dataSource.getConnection();
             PreparedStatement ps = connection.prepareStatement(sql);) {
            int parameterIndex = 1;
            for (long id = 1; id <= 4; id++) {
                ps.setLong(parameterIndex++, id);
                ps.setInt(parameterIndex++, (int) (id % 2));
                ps.setString(parameterIndex++, "ShardingSphere-" + id);
            }
            System.out.println("count:" + ps.executeUpdate());
        }
    }
    /**
     * 路由信息,tableNames：[t_file_2], id值：[1], storage_type值：[1]
     * Logic SQL: select id,storage_type,name from t_file where id =? and storage_type = ?
     * Actual SQL: sharding-jdbc-1 ::: select id,storage_type,name from t_file_2 where id =? and storage_type = ? ::: [1, 1]
     *
     * */
    @Test
    public void test3() throws SQLException {
        String sql = "select id,storage_type,name from t_file where id =? and storage_type = ?";
        executeSql2(sql,1L,1);

    }

    /**
     * 路由信息,tableNames：[t_file_1, t_file_2], id值：[1], storage_type值：[0, 1]
     * Logic SQL: select id,storage_type,name from t_file where storage_type in(?,?) and id =?
     * SQLStatement: SelectStatementContext(super=CommonSQLStatementContext(sqlStatement=org.apache.shardingsphere.sql.parser.sql.statement.dml.SelectStatement@2216effc, tablesContext=org.apache.shardingsphere.sql.parser.binder.segment.table.TablesContext@745c2004), tablesContext=org.apache.shardingsphere.sql.parser.binder.segment.table.TablesContext@745c2004, projectionsContext=ProjectionsContext(startIndex=7, stopIndex=26, distinctRow=false, projections=[ColumnProjection(owner=null, name=id, alias=Optional.empty), ColumnProjection(owner=null, name=storage_type, alias=Optional.empty), ColumnProjection(owner=null, name=name, alias=Optional.empty)]), groupByContext=org.apache.shardingsphere.sql.parser.binder.segment.select.groupby.GroupByContext@6da9dc6, orderByContext=org.apache.shardingsphere.sql.parser.binder.segment.select.orderby.OrderByContext@7fd69dd, paginationContext=org.apache.shardingsphere.sql.parser.binder.segment.select.pagination.PaginationContext@12010fd1, containsSubquery=false)
     * Actual SQL: sharding-jdbc-1 ::: select id,storage_type,name from t_file_1 where storage_type in(?,?) and id =? ::: [0, 1, 1]
     * Actual SQL: sharding-jdbc-1 ::: select id,storage_type,name from t_file_2 where storage_type in(?,?) and id =? ::: [0, 1, 1]
     * id:1,name:ShardingSphere-1,sex:1
     * */
    @Test
    public void test4() throws SQLException {
        String sql = "select id,storage_type,name from t_file where storage_type in(?,?) and id =?";
        executeSql2(sql,0,1,1L);

    }


}
