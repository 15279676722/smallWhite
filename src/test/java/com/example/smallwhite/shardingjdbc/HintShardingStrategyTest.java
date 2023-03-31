package com.example.smallwhite.shardingjdbc;

import lombok.extern.slf4j.Slf4j;
import org.apache.shardingsphere.api.config.sharding.ShardingRuleConfiguration;
import org.apache.shardingsphere.api.config.sharding.TableRuleConfiguration;
import org.apache.shardingsphere.api.config.sharding.strategy.HintShardingStrategyConfiguration;
import org.apache.shardingsphere.api.hint.HintManager;
import org.apache.shardingsphere.api.sharding.hint.HintShardingAlgorithm;
import org.apache.shardingsphere.api.sharding.hint.HintShardingValue;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Random;

@Slf4j
@SpringBootTest
public class HintShardingStrategyTest {
    private static DataSource dataSource;

    @BeforeAll
    public static void init() throws SQLException {
        dataSource = DataSourceUtils.init(HintShardingStrategyTest::initShardingRuleConfiguration, DataSourceUtils.DATA_BASE_1, DataSourceUtils.DATA_BASE_2);
    }

    public static ShardingRuleConfiguration initShardingRuleConfiguration() {
        /**
         * 2、配置 t_file分片规则
         */
        //逻辑表名
        final String logicTable = "t_user";
        //对应的实际表（4张）
        final String actualDataNodes = DataSourceUtils.DATA_BASE_1+".t_user_0,"+DataSourceUtils.DATA_BASE_1+".t_user_1,"+DataSourceUtils.DATA_BASE_2+".t_user_0,"+DataSourceUtils.DATA_BASE_2+".t_user_1" ;

        TableRuleConfiguration tableRuleConfiguration = new TableRuleConfiguration(logicTable, actualDataNodes);
        //混合分片策略配置
        HintShardingStrategyConfiguration hintShardingStrategyConfiguration = new HintShardingStrategyConfiguration(
                new HintShardingAlgorithm<Integer>() {
                    @Override
                    public Collection<String> doSharding(Collection<String> availableTargetNames, HintShardingValue<Integer> shardingValue) {
                        final Object[] tables = availableTargetNames.toArray();
                        List<String> result = new ArrayList<>();
                        //HintManager.getInstance().addTableShardingValue放入的值都在shardingValue里面
                        final Collection<Integer> tableIndexList = shardingValue.getValues();
                        for (Integer tableIndex : tableIndexList) {
                            result.add((String) tables[tableIndex]);
                        }
                        return result;
                    }
                });
        tableRuleConfiguration.setTableShardingStrategyConfig(hintShardingStrategyConfiguration);
        //混合分片策略配置
        HintShardingStrategyConfiguration databaseConfiguration = new HintShardingStrategyConfiguration(
                new HintShardingAlgorithm<Integer>() {
                    @Override
                    public Collection<String> doSharding(Collection<String> availableTargetNames, HintShardingValue<Integer> shardingValue) {
                        final Object[] databases = availableTargetNames.toArray();
                        List<String> result = new ArrayList<>();
                        //HintManager.getInstance().addTableShardingValue放入的值都在shardingValue里面
                        final Collection<Integer> databaseIndexList = shardingValue.getValues();
                        for (Integer tableIndex : databaseIndexList) {
                            result.add((String) databases[tableIndex]);
                        }
                        return result;
                    }
                });

        tableRuleConfiguration.setDatabaseShardingStrategyConfig(databaseConfiguration);

        ShardingRuleConfiguration shardingRuleConfig = new ShardingRuleConfiguration();
        shardingRuleConfig.getTableRuleConfigs().add(tableRuleConfiguration);
        return shardingRuleConfig;
    }


    /**
     * Logic SQL: select id,name,sex from t_user
     * Actual SQL: sharding-jdbc-1 ::: select id,name,sex from t_user_0
     * Actual SQL: sharding-jdbc-2 ::: select id,name,sex from t_user_0
     * */
    @Test
    public void test1() throws SQLException {
        String sql = "select id,name,sex from t_user";
        try (HintManager instance = HintManager.getInstance();) {
            //设置查询表的索引，addTableShardingValue(逻辑表名,值)
            instance.addTableShardingValue("t_user", 0);
            instance.addDatabaseShardingValue("t_user",1);
            try (
                    Connection connection = dataSource.getConnection();
                    PreparedStatement ps = connection.prepareStatement(sql);
                    ResultSet rs = ps.executeQuery();) {
                while (rs.next()) {
                    final long id = rs.getLong("id");
                    final String name = rs.getString("name");
                    final Integer sex = rs.getInt("sex");

                    System.out.println(String.format("id:%s,name:%s,sex:%s", id, name,sex));
                }
            }
        }
    }

    /**
     * Logic SQL: insert t_user (id,name,sex) value (?,?,?), (?,?,?), (?,?,?), (?,?,?)
     * Actual SQL: sharding-jdbc-2 ::: insert t_user_0 (id,name,sex) value (?, ?, ?), (?, ?, ?), (?, ?, ?), (?, ?, ?) ::: [1, yangqiang-1, 51, 2, yangqiang-2, 69, 3, yangqiang-3, 17, 4, yangqiang-4, 28]
     * Actual SQL: sharding-jdbc-2 ::: insert t_user_1 (id,name,sex) value (?, ?, ?), (?, ?, ?), (?, ?, ?), (?, ?, ?) ::: [1, yangqiang-1, 51, 2, yangqiang-2, 69, 3, yangqiang-3, 17, 4, yangqiang-4, 28]
     * count:8
     *
     * */
    @Test
    public void test2() throws SQLException {
        String sql = "insert t_user (id,name,sex) value (?,?,?), (?,?,?), (?,?,?), (?,?,?)";
        try (Connection connection = dataSource.getConnection();
             PreparedStatement ps = connection.prepareStatement(sql);
             HintManager instance = HintManager.getInstance();

             ) {
            instance.addTableShardingValue("t_user", 0);
            instance.addTableShardingValue("t_user", 1);

            instance.addDatabaseShardingValue("t_user",1);
            int parameterIndex = 1;
            for (long id = 1; id <= 4; id++) {
                ps.setLong(parameterIndex++, id);
                ps.setString(parameterIndex++, "yangqiang-" + id);
                ps.setInt(parameterIndex++, new Random().nextInt(100));
            }
            System.out.println("count:" + ps.executeUpdate());
        }
    }
}
