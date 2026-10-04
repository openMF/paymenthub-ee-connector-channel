package org.mifos.connector.channel.interceptor.config;

import org.mifos.connector.channel.config.ChannelRedisProperties;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.connection.jedis.JedisConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;

@Configuration
public class RedisRouteConfig {

    private JedisConnectionFactory jedisConnectionFactory;
    public RedisTemplate<String, String> redisTemplate;

    private final String redisHost;
    private final int redisPort;
    private final String redisPassword;

    public RedisRouteConfig(ChannelRedisProperties redisProperties) {
        this.redisHost = redisProperties.host();
        this.redisPort = redisProperties.port();
        this.redisPassword = redisProperties.password();
    }

    public JedisConnectionFactory setupConnector() {
        JedisConnectionFactory jedisConnectionFactory = new JedisConnectionFactory();
        jedisConnectionFactory.setHostName(redisHost);
        jedisConnectionFactory.setPort(redisPort);
        jedisConnectionFactory.setPassword(redisPassword);
        return jedisConnectionFactory;
    }
}
