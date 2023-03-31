package com.example.smallwhite.shardingjdbc;

import lombok.extern.slf4j.Slf4j;
import org.apache.shardingsphere.api.config.sharding.ShardingRuleConfiguration;
import org.apache.shardingsphere.api.config.sharding.TableRuleConfiguration;
import org.apache.shardingsphere.api.config.sharding.strategy.InlineShardingStrategyConfiguration;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import javax.sql.DataSource;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static com.example.smallwhite.shardingjdbc.DataSourceUtils.executeSql3;

@Slf4j
@SpringBootTest
public class BindingTableGroupsTest {
    private static DataSource dataSource;

    @BeforeAll
    public static void init() throws SQLException {
        dataSource = DataSourceUtils.init(BindingTableGroupsTest::initShardingRuleConfiguration, DataSourceUtils.DATA_BASE_1, DataSourceUtils.DATA_BASE_2);
    }

    public static ShardingRuleConfiguration initShardingRuleConfiguration() {

        List<TableRuleConfiguration> tableRuleConfigurations = new ArrayList<>();

        TableRuleConfiguration tableRuleConfiguration = new TableRuleConfiguration("t_order", "sharding-jdbc-${1..2}.t_order_${0..1}");
        InlineShardingStrategyConfiguration orderTableShardingStrategy =
                new InlineShardingStrategyConfiguration("order_id", "t_order_$->{(order_id + 1) % 2}");
        tableRuleConfiguration.setTableShardingStrategyConfig(orderTableShardingStrategy);


        //t_order_item分片规则
        TableRuleConfiguration orderItemRuleConfiguration =
                new TableRuleConfiguration("t_order_item", "sharding-jdbc-${1..2}.t_order_item_$->{0..1}");
        InlineShardingStrategyConfiguration orderItemTableShardingStrategy =
                new InlineShardingStrategyConfiguration("order_id", "t_order_item_$->{(order_id + 1) % 2}");
        orderItemRuleConfiguration.setTableShardingStrategyConfig(orderItemTableShardingStrategy);

        tableRuleConfigurations.add(tableRuleConfiguration);
        tableRuleConfigurations.add(orderItemRuleConfiguration);


        ShardingRuleConfiguration shardingRuleConfig = new ShardingRuleConfiguration();
        shardingRuleConfig.getTableRuleConfigs().add(tableRuleConfiguration);
        shardingRuleConfig.setBindingTableGroups(Arrays.asList("t_order", "t_order_item"));

        return shardingRuleConfig;
    }

    /**
     * 还没有加对应的表关联 所以会对 t_order_0 和t_order_item_0 t_order_item_1 分别关联
     * Logic SQL: select a.order_id,b.id as order_item_id,b.price from t_order a,t_order_item b where a.order_id = b.order_id and a.order_id = 1
     * Actual SQL: sharding-jdbc-2 ::: select a.order_id,b.id as order_item_id,b.price from t_order_0 a,t_order_item_1 b where a.order_id = b.order_id and a.order_id = 1
     * Actual SQL: sharding-jdbc-2 ::: select a.order_id,b.id as order_item_id,b.price from t_order_0 a,t_order_item_0 b where a.order_id = b.order_id and a.order_id = 1
     * Actual SQL: sharding-jdbc-1 ::: select a.order_id,b.id as order_item_id,b.price from t_order_0 a,t_order_item_1 b where a.order_id = b.order_id and a.order_id = 1
     * Actual SQL: sharding-jdbc-1 ::: select a.order_id,b.id as order_item_id,b.price from t_order_0 a,t_order_item_0 b where a.order_id = b.order_id and a.order_id = 1
     * order_id：1，order_item_id：1, price：5
     * order_id：1，order_item_id：2, price：15
     * order_id：1，order_item_id：1, price：5
     * order_id：1，order_item_id：2, price：15
     * <p>
     * 进行手动表绑定后就会路由到t_order_0表继续路由到和t_order_item_0表
     * Logic SQL: select a.order_id,b.id as order_item_id,b.price from t_order a,t_order_item b where a.order_id = b.order_id and a.order_id = 1
     * SQLStatement: SelectStatementContext(super=CommonSQLStatementContext(sqlStatement=org.apache.shardingsphere.sql.parser.sql.statement.dml.SelectStatement@73973e77, tablesContext=org.apache.shardingsphere.sql.parser.binder.segment.table.TablesContext@54a5eff), tablesContext=org.apache.shardingsphere.sql.parser.binder.segment.table.TablesContext@54a5eff, projectionsContext=ProjectionsContext(startIndex=7, stopIndex=46, distinctRow=false, projections=[ColumnProjection(owner=a, name=order_id, alias=Optional.empty), ColumnProjection(owner=b, name=id, alias=Optional[order_item_id]), ColumnProjection(owner=b, name=price, alias=Optional.empty)]), groupByContext=org.apache.shardingsphere.sql.parser.binder.segment.select.groupby.GroupByContext@29612ee2, orderByContext=org.apache.shardingsphere.sql.parser.binder.segment.select.orderby.OrderByContext@20524816, paginationContext=org.apache.shardingsphere.sql.parser.binder.segment.select.pagination.PaginationContext@6a7cbeed, containsSubquery=false)
     * Actual SQL: sharding-jdbc-2 ::: select a.order_id,b.id as order_item_id,b.price from t_order_0 a,t_order_item_0 b where a.order_id = b.order_id and a.order_id = 1
     * Actual SQL: sharding-jdbc-1 ::: select a.order_id,b.id as order_item_id,b.price from t_order_0 a,t_order_item_0 b where a.order_id = b.order_id and a.order_id = 1
     * order_id：1，order_item_id：1, price：5
     * order_id：1，order_item_id：2, price：15
     * order_id：1，order_item_id：1, price：5
     * order_id：1，order_item_id：2, price：15
     */
    @Test
    public void test1() throws SQLException {
        String sql = "select a.order_id,b.id as order_item_id,b.price " +
                "from t_order a,t_order_item b " +
                "where a.order_id = b.order_id and a.order_id = 1";

        executeSql3(sql);
    }


}
