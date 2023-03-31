package com.example.smallwhite.shardingjdbc;

import com.zaxxer.hikari.HikariDataSource;
import org.apache.shardingsphere.api.config.sharding.ShardingRuleConfiguration;
import org.apache.shardingsphere.api.config.sharding.TableRuleConfiguration;
import org.apache.shardingsphere.shardingjdbc.api.ShardingDataSourceFactory;
import org.apache.shardingsphere.underlying.common.config.properties.ConfigurationPropertyKey;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.*;
import java.util.function.Supplier;

public class DataSourceUtils {

    private static DataSource dataSource = null;
    public static final String DATA_BASE_1 = "sharding-jdbc-1";
    public static final String DATA_BASE_2 = "sharding-jdbc-2";

    public static final String DATA_BASE_MASTER_0 = "ds_master_0";
    public static final String DATA_BASE_MASTER_1 = "ds_master_1";

    public static final String DATA_BASE_SLAVE_0 = "ds_slave_0";
    public static final String DATA_BASE_SLAVE_1 = "ds_slave_1";

    public static DataSource init(Supplier<ShardingRuleConfiguration> supplier,  String... dataBases) throws SQLException {
        Map<String, DataSource> dataSourceMap = new LinkedHashMap<>();
        /**
         * 1.配置真实数据源
         */
        for (String dataBase : dataBases) {
            dataSourceMap.put(dataBase, getDataSource(dataBase));
        }


        ShardingRuleConfiguration shardingRuleConfiguration = supplier.get();

        /**
         * 3、加入表的分片规则
         */
//        ShardingRuleConfiguration shardingRuleConfig = new ShardingRuleConfiguration();
//        if (userRuleConfigurations != null) {
//            shardingRuleConfig.getTableRuleConfigs().addAll(userRuleConfigurations);
//        }
//        if (broadcast) {
//            shardingRuleConfig.setBroadcastTables(Arrays.asList("t_dict"));
//        }
//        if(bindTable){
//            shardingRuleConfig.setBindingTableGroups(bindTableNames);
//        }
        /**
         * 4、配置一些属性
         */
        Properties props = new Properties();
        //输出sql
        props.put(ConfigurationPropertyKey.SQL_SHOW.getKey(), true);

        /**
         * 5、创建数据源
         */
        dataSource = ShardingDataSourceFactory.createDataSource(dataSourceMap, shardingRuleConfiguration, props);
        return dataSource;
    }



    public static DataSource getDataSource(String dataBaseName) {
        HikariDataSource ds = new HikariDataSource();
        ds.setDriverClassName("com.mysql.jdbc.Driver");
        ds.setJdbcUrl("jdbc:mysql://localhost:3306/" + dataBaseName + "?characterEncoding=UTF-8");
        ds.setUsername("root");
        ds.setPassword("root");
        return ds;
    }

    public static void executeSql(String sql, Object... params) throws SQLException {


        Connection connection = dataSource.getConnection();
        PreparedStatement ps = connection.prepareStatement(sql);
        try {
            int paramIndex = 1;

            for (Object param : params) {
                ps.setObject(paramIndex++, param);
            }
            ResultSet rs = ps.executeQuery();

            while (rs.next()) {
                final long id = rs.getLong("id");
                final String name = rs.getString("name");
                int sex = rs.getInt("sex");
                System.out.println(String.format("id:%s,name:%s,sex:%s", id, name, sex));
            }
        } finally {
            connection.close();
            ps.close();
        }
    }


    public static void executeSql2(String sql, Object... params) throws SQLException {


        Connection connection = dataSource.getConnection();
        PreparedStatement ps = connection.prepareStatement(sql);
        try {
            int paramIndex = 1;

            for (Object param : params) {
                ps.setObject(paramIndex++, param);
            }
            ResultSet rs = ps.executeQuery();

            while (rs.next()) {
                final long id = rs.getLong("id");
                final String name = rs.getString("name");
                int sex = rs.getInt("storage_type");
                System.out.println(String.format("id:%s,name:%s,sex:%s", id, name, sex));
            }
        } finally {
            connection.close();
            ps.close();
        }
    }


    public static void executeSql3(String sql, Object... params) throws SQLException {


        Connection connection = dataSource.getConnection();
        PreparedStatement ps = connection.prepareStatement(sql);
        try {
            int paramIndex = 1;

            for (Object param : params) {
                ps.setObject(paramIndex++, param);
            }
            ResultSet rs = ps.executeQuery();

            while (rs.next()) {
                Long order_id = rs.getLong("order_id");
                Long order_item_id = rs.getLong("order_item_id");
                Integer price = rs.getInt("price");
                System.out.println(String.format("order_id：%s，order_item_id：%s, price：%s", order_id, order_item_id, price));
            }
        } finally {
            connection.close();
            ps.close();
        }
    }
}
