package com.example.smallwhite.shardingjdbc;

import lombok.extern.slf4j.Slf4j;
import org.apache.shardingsphere.api.config.masterslave.MasterSlaveRuleConfiguration;
import org.apache.shardingsphere.api.config.sharding.ShardingRuleConfiguration;
import org.apache.shardingsphere.api.config.sharding.TableRuleConfiguration;
import org.apache.shardingsphere.api.config.sharding.strategy.InlineShardingStrategyConfiguration;
import org.apache.shardingsphere.api.hint.HintManager;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@Slf4j
@SpringBootTest
public class MasterSlaveTest {
    private static DataSource dataSource;

    @BeforeAll
    public static void init() throws SQLException {
        dataSource = DataSourceUtils.init(MasterSlaveTest::initShardingRuleConfiguration,
                DataSourceUtils.DATA_BASE_MASTER_1, DataSourceUtils.DATA_BASE_MASTER_0, DataSourceUtils.DATA_BASE_SLAVE_0, DataSourceUtils.DATA_BASE_SLAVE_1);
    }

    public static ShardingRuleConfiguration initShardingRuleConfiguration() {
        // 主从规则配置，就是配置主从关系，让系统知道哪个库是主库、他的从库列表是哪些？
        MasterSlaveRuleConfiguration master0SlaveRuleConfig =
                new MasterSlaveRuleConfiguration(
                        "ds0",
                        "ds_master_0",  //dataSourceMap中主库的key
                        Arrays.asList("ds_slave_0")); // dataSourceMap中ds_master_0从库的key
        // 配置读写分离规则
        MasterSlaveRuleConfiguration master1SlaveRuleConfig =
                new MasterSlaveRuleConfiguration(
                        "ds1",
                        "ds_master_1",  //dataSourceMap中主库的key
                        Arrays.asList("ds_slave_1")); // dataSourceMap中ds_master_1从库的key
        /**
         * 2、配置t_user分片规则
         */
        TableRuleConfiguration userTableRuleConfiguration =
                new TableRuleConfiguration("t_user", "ds$->{0..1}.t_user");
        //设置t_user表的分库规则
        InlineShardingStrategyConfiguration userTableShardingStrategy =
                new InlineShardingStrategyConfiguration("id", "ds$->{(id+1) % 2}");
        userTableRuleConfiguration.setDatabaseShardingStrategyConfig(userTableShardingStrategy);

        ShardingRuleConfiguration shardingRuleConfig = new ShardingRuleConfiguration();
        shardingRuleConfig.getTableRuleConfigs().add(userTableRuleConfiguration);
        shardingRuleConfig.setMasterSlaveRuleConfigs(Arrays.asList(master0SlaveRuleConfig, master1SlaveRuleConfig));


        return shardingRuleConfig;
    }

    /**
     * 无事务查询
     * 无事务读取落入从库
     */
    @Test
    public void test1() throws SQLException {
        String sql = "select id,name from t_user where id = 1";
        try (Connection connection = dataSource.getConnection();
             PreparedStatement ps = connection.prepareStatement(sql);
             ResultSet rs = ps.executeQuery();) {
            while (rs.next()) {
                final long id = rs.getLong("id");
                final String name = rs.getString("name");
                System.out.println(String.format("id:%s,name:%s", id, name));
            }
        }
    }

    /**
     * 事务中直接读取落入从库
     */
    @Test
    public void test2() throws SQLException {
        try (Connection connection = dataSource.getConnection();) {
            //手动开启事务
            connection.setAutoCommit(false);
            String sql = "select id,name from t_user where id = 2";
            PreparedStatement ps = connection.prepareStatement(sql);
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                final long id = rs.getLong("id");
                final String name = rs.getString("name");
                System.out.println(String.format("id:%s,name:%s", id, name));
            }
            connection.commit();
        }
    }


    /**
     * -----------插入id为3数据-----------
     * Logic SQL: insert into t_user values (3,'张三')
     * Actual SQL: ds_master_0 ::: insert into t_user values (3, '张三')
     * -----------查询刚插入的数据-----------
     * Logic SQL: select id,name from t_user where id = 3
     * Actual SQL: ds_master_0 ::: select id,name from t_user where id = 3
     * id:3,name:张三
     * 上面id为3的在t_master_0,下面来看看读取id为2的，看看会读取主库还是从库？
     * -----------查询id为2的数据-----------
     * Logic SQL: select id,name from t_user where id = 2
     * Actual SQL: ds_master_1 ::: select id,name from t_user where id = 2
     * id:2,name:我是ds_master_1
     * <p>
     * 结论：只要开启了手动事务，且第一个sql为insert，后面的不管路由到哪个库，都会落入主库
     */
    @Test
    public void test3() throws SQLException {
        try (Connection connection = dataSource.getConnection();) {
            connection.setAutoCommit(false);
            System.out.println("-----------插入id为3数据-----------");
            String sql = "insert into t_user values (3,'zhangsan')";
            PreparedStatement ps = connection.prepareStatement(sql);
            ps.executeUpdate();
            System.out.println("-----------查询刚插入的数据-----------");
            sql = "select id,name from t_user where id = 3";
            ps = connection.prepareStatement(sql);
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                final long id = rs.getLong("id");
                final String name = rs.getString("name");
                System.out.println(String.format("id:%s,name:%s", id, name));
            }
            System.out.println("上面id为3的在t_master_0,下面来看看读取id为2的，看看会读取主库还是从库？");
            System.out.println("-----------查询id为2的数据-----------");
            sql = "select id,name from t_user where id = 2";
            ps = connection.prepareStatement(sql);
            rs = ps.executeQuery();
            while (rs.next()) {
                final long id = rs.getLong("id");
                final String name = rs.getString("name");
                System.out.println(String.format("id:%s,name:%s", id, name));
            }
            connection.commit();
        }
    }


    /**
     * 可以通过hintManager.setMasterRouteOnly()强制走主库
     * */
    @Test
    public void test4() throws SQLException {
        String sql = "select id,name from t_user where id = 1";
        try (Connection connection = dataSource.getConnection();
             PreparedStatement ps = connection.prepareStatement(sql);) {
            HintManager hintManager = null;
            try {
                //通过HintManager.setMasterRouteOnly()强制走主库，注意在finally中释放HintManager.close();
                hintManager = HintManager.getInstance();
                hintManager.setMasterRouteOnly();
                ResultSet rs = ps.executeQuery();
                while (rs.next()) {
                    final long id = rs.getLong("id");
                    final String name = rs.getString("name");
                    System.out.println(String.format("id:%s,name:%s", id, name));
                }
            } finally {
                if (hintManager != null) {
                    hintManager.close();
                }
            }
        }
    }
}
