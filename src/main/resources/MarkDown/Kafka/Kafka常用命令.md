## Topic脚本命令

![image-20220303143848826](/Users/yangqiang/Library/Application Support/typora-user-images/image-20220303143848826.png)

![image-20220303144049723](/Users/yangqiang/Library/Application Support/typora-user-images/image-20220303144049723.png)



| 参数                 | 描述                                                         |
| -------------------- | ------------------------------------------------------------ |
| --alter              | 改变分区的数量，副本分配，和/或.主题的配置。不能减少分区不能修改副本数 |
| --bootstrap-server   | 连接kafka服务端                                              |
| --create             | 创建一个新的topic                                            |
| --delete             | 删除topic                                                    |
| --describe           | 指定topic的详细信息                                          |
| --list               | 列出全部topic                                                |
| --topic              | 对topic进行创建 删除 修改操作时的命令                        |
| --replica-assignment | 设置topic的分区数                                            |
| --replication-factor | 设置topic的副本数                                            |



创建topic：

```livescript
./kafka-topics.sh  --bootstrap-server localhost:9092 --create --topic first --partitions 5 --replication-factor 1
```

创建topic(集群)：

```livescript
./kafka-topics.sh  --bootstrap-server localhost:9092,localhost:9093,localhost:9094 --create --topic first --partitions 5 --replication-factor 3
```



查看topic：

```livescript
./kafka-topics.sh  --bootstrap-server localhost:9092 --list
```

查看topic(集群)：

```livescript
./kafka-topics.sh  --bootstrap-server localhost:9092,localhost:9093,localhost:9094 --list
```



## 生产者脚本命令

![image-20220303153339858](/Users/yangqiang/Library/Application Support/typora-user-images/image-20220303153339858.png)

![image-20220303153404002](/Users/yangqiang/Library/Application Support/typora-user-images/image-20220303153404002.png)

| 参数               | 描述                                  |
| :----------------- | ------------------------------------- |
| --bootstrap-server | 连接kafka服务端                       |
| --topic            | 对topic进行创建 删除 修改操作时的命令 |
|                    |                                       |
|                    |                                       |

给Topic发送消息

```livescript
./kafka-console-producer.sh --bootstrap-server localhost:9092 --topic first
```

给Topic发送消息(集群)

```livescript
./kafka-console-producer.sh --bootstrap-server localhost:9092,localhost:9093,localhost:9094 --topic first
```

 

## 消费者脚本命令

![image-20220303153639014](/Users/yangqiang/Library/Application Support/typora-user-images/image-20220303153639014.png)

![image-20220303153649342](/Users/yangqiang/Library/Application Support/typora-user-images/image-20220303153649342.png)

| 参数               | 描述                                  |
| :----------------- | ------------------------------------- |
| --bootstrap-server | 连接kafka服务端                       |
| --topic            | 对topic进行创建 删除 修改操作时的命令 |
| --from-beginning   | 接受历史消息                          |
|                    |                                       |

接受Topic消息

```livescript
./kafka-console-consumer.sh --bootstrap-server localhost:9092 --topic first
```

接受Topic消息(集群)

```livescript
./kafka-console-consumer.sh --bootstrap-server localhost:9092,localhost:9093,localhost:9094 --topic first --group test
```



接受Topic消息(历史消息)

```livescript
./kafka-console-consumer.sh --bootstrap-server localhost:9092 --topic first --from-beginning
```

消费__customer_offset主题

```livescript
./kafka-console-consumer.sh --topic __consumer_offsets --bootstrap-server localhost:9092,localhost:9093,localhost:9094 -- consumer.config config/consumer.properties --formatter "kafka.coordinator.group.GroupMetadataManager\$OffsetsMessageForm atter" --from-beginning
```

