### Index:索引

```json
{
  "movies":{
    "settings":{
      "index":{
        "creation_date":"1552737458543",
        "number_of_shards":"2",
        "number_of_replicas":"0",
        "uuid": "Qnd7lMsisdkwqq",
        "version":{
          "created":"6060299"
        },
        "provided_name":"movies"
      }
    }
  }
}
```

- 索引是文档的容器,是一类文档的结合

  > index体现了逻辑空间的概念:每个索引都有自己的Mapping定义，用于订单包含的文档的字段名喝字段类型
  >
  > Shard体现了物理空间的概念:索引中的数据分散在Shard上

- 索引的Mapping与Setting

  > Mapping定义文档字段的类型
  >
  > Setting定义不同的数据分布 

#### 索引的不同语意



![image-20230107145946645](/Users/yangqiang/Library/Application Support/typora-user-images/image-20230107145946645.png)

- 名词：一个Elasticsearch集群中，可以创建很多个不同的索引
- 动词：保存一个文档到Elasticsearch的过程也叫索引(indexing) ES中创建一个倒排索引的过程
- 名词：一个B树索引，一个倒排索引

### Type

- 在7.0之前，一个index可以设置多个Types
- 6.0开始，Type已经被Deorecated。7.0开始一个索引只能创建一个 Type-"doc"



```json
{
   "goods": {
      "mappings": {
         "electronic_goods": {
            "properties": {
               "name": {
                  "type": "string",
               },
               "price": {
                  "type": "double"
               },
               "service_period": {
                  "type": "string"
                   }            
                }
         },
         "fresh_goods": {
            "properties": {
               "name": {
                  "type": "string",
               },
               "price": {
                  "type": "double"
               },
               "eat_period": {
                    "type": "string"
               }
                }
         }
      }
   }
}
```



### 抽象和类比 es和mysql

![image-20230107151243670](/Users/yangqiang/Library/Application Support/typora-user-images/image-20230107151243670.png)