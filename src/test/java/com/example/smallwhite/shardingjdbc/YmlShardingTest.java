package com.example.smallwhite.shardingjdbc;

import org.apache.shardingsphere.shardingjdbc.api.yaml.YamlShardingDataSourceFactory;

import javax.sql.DataSource;
import java.io.IOException;
import java.io.InputStream;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

public class YmlShardingTest {
    public static void main(String[] args) throws IOException, SQLException {
        //1.读取demo2.yml文件
        InputStream inputStream = ClassLoader.getSystemResourceAsStream("shardingsphere/test.yml");
        byte[] bytes = new byte[inputStream.available()];
        inputStream.read(bytes);
        //2.创建数据源
        DataSource dataSource = YamlShardingDataSourceFactory.createDataSource(bytes);
        /**
         * 3、获取连接，执行sql
         */
        String sql = "insert into t_order (order_id,user_id,price) values (?,?,?)";
        try (Connection connection = dataSource.getConnection();
             PreparedStatement ps = connection.prepareStatement(sql);) {
            // 插入4条数据测试,每个表会落入1条数据
            for (long user_id = 1; user_id <= 2; user_id++) {
                for (long order_id = 1; order_id <= 2; order_id++) {
                    int j = 1;
                    ps.setLong(j++, order_id);
                    ps.setLong(j++, user_id);
                    ps.setLong(j, 100);
                    System.out.println(ps.executeUpdate());
                }
            }
        }
    }
}
