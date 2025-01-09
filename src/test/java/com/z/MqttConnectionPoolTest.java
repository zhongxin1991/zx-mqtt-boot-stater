package com.z;

import com.z.confg.MqttConfigProperties;
import com.z.confg.MqttPoolConfig;
import com.z.pool.MqttConnection;
import com.z.pool.MqttConnectionFactory;
import com.z.pool.MqttConnectionPool;
import org.apache.commons.pool2.impl.GenericObjectPoolConfig;
import org.eclipse.paho.client.mqttv3.MqttConnectOptions;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import java.util.concurrent.CountDownLatch;

/**
 * @description:
 * @version: 1.0
 * @author: xin.zhong
 * @date: 2025/1/9 14:38
 */
public class MqttConnectionPoolTest {

    private MqttConnectionPool pool;

    @Before
    public void init() {
        MqttConfigProperties mqttConfig = new MqttConfigProperties();
        mqttConfig.setUrl("ssl://xxxx");
        mqttConfig.setUserName("admin");
        mqttConfig.setPassword("123456");
        mqttConfig.setDataDir("/tmp/zx");

        MqttPoolConfig poolConfig = new MqttPoolConfig();
        poolConfig.setMaxTotal(50);
        poolConfig.setMaxIdle(10);
        poolConfig.setMinIdle(0);
        poolConfig.setMaxWaitMillis(1000);
        poolConfig.setBlockWhenExhausted(false);
        mqttConfig.setPool(poolConfig);


        // mqtt连接配置
        MqttConnectOptions connOpts = new MqttConnectOptions();
        connOpts.setUserName(mqttConfig.getUserName());
        if (mqttConfig.getPassword() != null && !mqttConfig.getPassword().isEmpty()) {
            connOpts.setPassword(mqttConfig.getPassword().toCharArray());
        }

        MqttConnectionFactory connectionFactory = new MqttConnectionFactory(mqttConfig.getUrl(), connOpts,mqttConfig.getDataDir());

        // 连接池配置
        GenericObjectPoolConfig<MqttConnection> objectPoolConfig = MqttAutoConfiguration.initPoolConfig(mqttConfig.getPool());
        objectPoolConfig.setJmxEnabled(true);

        pool = new MqttConnectionPool(connectionFactory, objectPoolConfig);
    }


    @Test
    public void testMqttConnectionPool() throws Exception {
        CountDownLatch countDownLatch = new CountDownLatch(50);

        for (int i = 0; i < 50; i++) {
            MqttConnection connection = null;

            connection = pool.borrowObject();
            connection.publish("test", "test" + i);
            if (i % 2 == 0) {
                pool.returnObject(connection);
            }
            countDownLatch.countDown();

        }

        countDownLatch.await();

        Assert.assertEquals(25, pool.getNumActive());

        pool.close();

    }

}
