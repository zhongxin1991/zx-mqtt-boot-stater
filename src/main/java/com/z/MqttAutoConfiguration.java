package com.z;

import com.z.confg.MqttConfigProperties;
import com.z.confg.MqttPoolConfig;
import com.z.pool.MqttConnection;
import com.z.pool.MqttConnectionFactory;
import com.z.pool.MqttConnectionPool;

import org.apache.commons.pool2.impl.GenericObjectPoolConfig;
import org.eclipse.paho.client.mqttv3.MqttConnectOptions;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class MqttAutoConfiguration {

    @Autowired
    private MqttConfigProperties mqttConfig;

    @Bean
    public MqttConnectionPool mqttConnectionPool() {
        // 连接池配置
        GenericObjectPoolConfig<MqttConnection> poolConfig = this.initPoolConfig();

        // mqtt连接配置
        MqttConnectOptions connOpts = new MqttConnectOptions();
        connOpts.setUserName(this.mqttConfig.getUserName());
        if (mqttConfig.getPassword() != null && !mqttConfig.getPassword().isEmpty()) {
            connOpts.setPassword(this.mqttConfig.getPassword().toCharArray());
        }

        // 创建工厂对象
        MqttConnectionFactory connectionFactory = new MqttConnectionFactory(mqttConfig.getUrl(), connOpts);

        // 创建连接池
        return new MqttConnectionPool(connectionFactory, poolConfig);

    }

    private GenericObjectPoolConfig<MqttConnection> initPoolConfig() {
        GenericObjectPoolConfig<MqttConnection> poolConfig = new GenericObjectPoolConfig<>();
        MqttPoolConfig mqttPoolConfig = this.mqttConfig.getPool();

        poolConfig.setMinIdle(mqttPoolConfig.getMinIdle());
        poolConfig.setMaxIdle(mqttPoolConfig.getMaxIdle());
        poolConfig.setMaxTotal(mqttPoolConfig.getMaxTotal());

        poolConfig.setTestOnCreate(mqttPoolConfig.getTestOnCreate());

        poolConfig.setTestOnBorrow(mqttPoolConfig.getTestOnBorrow());
        poolConfig.setTestOnReturn(mqttPoolConfig.getTestOnReturn());
        poolConfig.setTestWhileIdle(mqttPoolConfig.getTestWhileIdle());

        poolConfig.setBlockWhenExhausted(mqttPoolConfig.getBlockWhenExhausted());
        poolConfig.setMaxWaitMillis(mqttPoolConfig.getMaxWaitMillis());

        if (mqttPoolConfig.getEvictionPolicyClassName() != null && !mqttPoolConfig.getEvictionPolicyClassName().isEmpty()) {
            poolConfig.setEvictionPolicyClassName(mqttPoolConfig.getEvictionPolicyClassName());
        }
        poolConfig.setMinEvictableIdleTimeMillis(mqttPoolConfig.getMinEvictableIdleTimeMillis());
        poolConfig.setTimeBetweenEvictionRunsMillis(mqttPoolConfig.getTimeBetweenEvictionRunsMillis());
        poolConfig.setNumTestsPerEvictionRun(mqttPoolConfig.getNumTestsPerEvictionRun());

        poolConfig.setLifo(mqttPoolConfig.getLifo());
        poolConfig.setFairness(mqttPoolConfig.getFairness());


        return poolConfig;
    }


}
