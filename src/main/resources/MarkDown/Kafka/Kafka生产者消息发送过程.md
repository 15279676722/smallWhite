​			 	

![image-20220315161127761](/Users/yangqiang/Library/Application Support/typora-user-images/image-20220315161127761.png)

1. 生产者开启main线程。创建KafkaProducer对象调用send()方法进行消息发送
2. 执行用户自定义拦截器。
3. 对key和value进行序列化。序列化器对基本数据都已经序列化完了。在大数据场景下java序列化太重了。
4. 进入分区器来对数据进行分区操作
5. 进入到缓冲区中(32M)等待batch.size(16k)或者linger.ms(0ms无延迟)满足后开启一个sender线程来进行数据发送
6. 开始拉取sender线程发送过来的request请求数据进行缓存。为每个broker缓存5个request请求(无需应答即可继续发送请求的最大request请求数量)
7. 数据发送到kafka集群上leader进行数据确认落盘和副本的备份操作。完成应答acks。如果应答失败。则进行重试操作默认重试次数是integer的最大值无限重试。如果应答成功。则从缓冲区中删除数据。

