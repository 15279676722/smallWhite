package com.example.smallwhite.shardingjdbc;

import lombok.extern.slf4j.Slf4j;
import org.apache.shardingsphere.api.config.sharding.ShardingRuleConfiguration;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Arrays;

@Slf4j
@SpringBootTest
public class BroadcastTest {
    private static DataSource dataSource;

    @BeforeAll
    public static void init() throws SQLException {
        dataSource = DataSourceUtils.init(BroadcastTest::initShardingRuleConfiguration, DataSourceUtils.DATA_BASE_1, DataSourceUtils.DATA_BASE_2);
    }

    public static ShardingRuleConfiguration initShardingRuleConfiguration() {
        /**
         * 2、无分片规则
         */

        ShardingRuleConfiguration shardingRuleConfig = new ShardingRuleConfiguration();
        shardingRuleConfig.setBroadcastTables(Arrays.asList("t_dict"));
        return shardingRuleConfig;
    }

    /**
     * 测试插入数据：
     * 2023-03-24 11:44:41.132  INFO 21034 --- [           main] ShardingSphere-SQL                       : Logic SQL: insert into t_dict (code,k,v) values ('gender','0','1'),('gender','1','2')
     * 2023-03-24 11:44:41.133  INFO 21034 --- [           main] ShardingSphere-SQL                       : SQLStatement: InsertStatementContext(super=CommonSQLStatementContext(sqlStatement=org.apache.shardingsphere.sql.parser.sql.statement.dml.InsertStatement@63d14dbf, tablesContext=org.apache.shardingsphere.sql.parser.binder.segment.table.TablesContext@67521a79), tablesContext=org.apache.shardingsphere.sql.parser.binder.segment.table.TablesContext@67521a79, columnNames=[code, k, v], insertValueContexts=[InsertValueContext(parametersCount=0, valueExpressions=[LiteralExpressionSegment(startIndex=38, stopIndex=45, literals=gender), LiteralExpressionSegment(startIndex=47, stopIndex=49, literals=0), LiteralExpressionSegment(startIndex=51, stopIndex=53, literals=1)], parameters=[]), InsertValueContext(parametersCount=0, valueExpressions=[LiteralExpressionSegment(startIndex=57, stopIndex=64, literals=gender), LiteralExpressionSegment(startIndex=66, stopIndex=68, literals=1), LiteralExpressionSegment(startIndex=70, stopIndex=72, literals=2)], parameters=[])], generatedKeyContext=Optional.empty)
     * 2023-03-24 11:44:41.133  INFO 21034 --- [           main] ShardingSphere-SQL                       : Actual SQL: sharding-jdbc-1 ::: insert into t_dict (code,k,v) values ('gender', '0', '1'), ('gender', '1', '2')
     * 2023-03-24 11:44:41.133  INFO 21034 --- [           main] ShardingSphere-SQL                       : Actual SQL: sharding-jdbc-2 ::: insert into t_dict (code,k,v) values ('gender', '0', '1'), ('gender', '1', '2')
     * 插入记录数：2
     * */
    @Test
    public void test1() throws SQLException {
        Connection connection = dataSource.getConnection();
        System.out.println("测试插入数据：");
        String sql = "insert into t_dict (code,k,v) values ('gender','0','1'),('gender','1','2')";
        PreparedStatement ps = connection.prepareStatement(sql);
        System.out.println("插入记录数：" + ps.executeUpdate());

        ps.close();
        connection.close();
    }
    /**
     * 10次查询发现每次查询都是随机进行路由的
     * */
    @Test
    public void test2() throws SQLException {
        for (int i = 0; i < 10; i++) {
            Connection connection = dataSource.getConnection();
            System.out.println("测试查询数据：");
            PreparedStatement ps = connection.prepareStatement("select count(*) from t_dict");
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                System.out.println("count:" + rs.getInt(1));
            }
            ps.close();
            connection.close();
        }

    }
}
