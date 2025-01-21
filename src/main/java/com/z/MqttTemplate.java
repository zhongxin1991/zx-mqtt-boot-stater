package com.z;

import com.z.constant.Qos;
import com.z.pool.MqttConnection;
import com.z.pool.MqttConnectionPool;
import com.z.pool.PoolMqttCallback;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;


@Service
public class MqttTemplate {

    private final Logger LOGGER = LoggerFactory.getLogger(this.getClass());

    @Autowired
    private MqttConnectionPool mqttConnectionPool;

    public void publish(String topic, String message) {
        MqttConnection connection = null;
        try {
            connection = mqttConnectionPool.borrowObject();
            connection.publish(topic, message);
        } catch (Exception e) {
            if (LOGGER.isErrorEnabled()) {
                LOGGER.error("publish ERROR ; topic={},message={}", topic, message, e);
            }
        } finally {
            if (null != connection) {
                connection.close();
            }
        }
    }

    public void subscribeWithCallback(String topicFilter, Qos qos, PoolMqttCallback callback) {
        MqttConnection connection = null;
        try {
            connection = mqttConnectionPool.borrowObject();
            connection.setCallback(callback);
            connection.subscribe(new String[]{topicFilter}, new int[]{qos.getValue()});
        } catch (Exception e) {
            if (LOGGER.isErrorEnabled()) {
                LOGGER.error("subscribeWithCallback failed", e);
            }
        } finally {
            if (null != connection) {
                connection.close();
            }
        }
    }

    public MqttConnection getConnection() throws Exception {
        return mqttConnectionPool.borrowObject();
    }

    public void returnConnection(MqttConnection connection) throws Exception {
        mqttConnectionPool.returnObject(connection);
    }


}
