## Document：文档 

 document 类比为mysql表中的一条数据

- Elasticsearch是面向文档的，文档是所有可搜索数据的最小基础信息单元。

  > 日志文件中的日志项
  >
  > 一部电影的具体信息/一张唱片的详细信息
  >
  > MP3里放的一首歌/一篇PDF文档中的详细内容

- 一个Document就像数据库中的一行记录，文档会被序列化成JSON格式，保持在Elasticsearch中，多个Document存储于一个索引(Index)中。文档以JSON（Javascript Object Notation）格式来表示，而JSON是一个到处存在的互联网数据交互格式。

  > JSON对象由字段组成
  >
  > 每个字段有对应的字段类型(字符串/数值/布尔/日期/二进制/范围类型)

- 每一个文档都有一个UniqueID

  > 自己指定ID
  >
  > elasticsearch自动生成

### JSON文档

- 一篇文档包含了一系列的字段。类似数据库的一条记录

- JSON文档格式灵活 不需要预先定义格式

  > 字段的类型可以通过elasticsearch自动推算
  >
  > 支持数组/支持嵌套

| movieId | title     | genres                                          |
| ------- | --------- | ----------------------------------------------- |
| 1       | Toy Story | Adventure\|Animation\|Children\|Comeby\|Fantasy |

通过JSON转换为

```json
{
  "id":1,
  "title":"Toy Story",
  "genres":["Adventure","Animation","Children","Comeby","Fantasy"],
  "@version":1
}
```



### 文档的元数据

元数据用于标注文档的相关信息

```json
{
    "_index":"movies",
    "_type":"_doc",
    "_id":1,
    "_score":14.69302,
    "_source":{
        "id":1,
        "title":"Toy Story",
        "genres":[
            "Adventure",
            "Animation",
            "Children",
            "Comeby",
            "Fantasy"
        ],
        "@version":1
    }
}
```

- _index    - 文档所属的索引名
- _type      - 文档所属的类型名
- _id          - 文档唯一ID
- _source - 文档原始json数据
- _all         - 整合所有字段内容到该字段 7.0已被废除
- _score   - 相关性打分
- _version- 文档的版本信息