package com.example.smallwhite.config;

import com.example.smallwhite.config.websocket.MyEndpointConfigure;
import com.example.smallwhite.spring.entity.Book;
import com.example.smallwhite.utils.JdbcUtil;
import com.example.smallwhite.utils.RedisService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.PropertySource;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.listener.RedisMessageListenerContainer;
import org.springframework.data.redis.serializer.JdkSerializationRedisSerializer;
import org.springframework.data.redis.serializer.RedisSerializer;
import org.springframework.data.redis.serializer.StringRedisSerializer;
import org.springframework.web.socket.server.standard.ServerEndpointExporter;

/**
 * @author yangqiang
 */
@Slf4j
@Configuration
@PropertySource(value = {"classpath:application.yml", "classpath:application.properties"})//制定读取配置文件的路径
@ConfigurationProperties(prefix = "spring.datasource")
public class JavaConfig {
    @Value("${url}")
    private String url;
    @Value("${driver-class-name}")
    private String driver;
    @Value("${username}")
    private String username;
    @Value("${password}")
    private String password;
//    @Autowired
//    private RedisTemplate<String,Object> redisTemplate;
    /**
     * @return redis 服务
     */
    @Bean
    public RedisService redisService(RedisTemplate redisTemplate) {
        RedisSerializer stringRedisSerializer = new StringRedisSerializer();
        redisTemplate.setKeySerializer(stringRedisSerializer);
        redisTemplate.setValueSerializer(stringRedisSerializer);
        redisTemplate.setHashKeySerializer(stringRedisSerializer);
        redisTemplate.setHashValueSerializer(stringRedisSerializer);
        return new RedisService(redisTemplate);
    }
    /**
     * redis key 过期
     * */
    @Bean
    RedisMessageListenerContainer container(RedisConnectionFactory connectionFactory) {

        RedisMessageListenerContainer container = new RedisMessageListenerContainer();
        container.setConnectionFactory(connectionFactory);
        return container;
    }

    @Bean
    public JdbcUtil jdbcUtil() {
        log.info(url + "," + driver + "," + username + "," + password);
        JdbcUtil jdbcUtil = new JdbcUtil(url, driver, username, password);
        return jdbcUtil;
    }
//    @Bean
//    public ServerEndpointExporter serverEndpointExporter() {
//        return new ServerEndpointExporter();
//    }

    @Bean
    public MyEndpointConfigure newConfigure() {
        return new MyEndpointConfigure();
    }



}
