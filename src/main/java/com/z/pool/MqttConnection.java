package com.z.pool;

import org.apache.commons.pool2.impl.GenericObjectPool;
import org.eclipse.paho.client.mqttv3.MqttClient;
import org.eclipse.paho.client.mqttv3.MqttMessage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class MqttConnection {

    private final Logger LOGGER = LoggerFactory.getLogger(this.getClass());

    private MqttClient mqttClient;

    public MqttConnection(MqttClient mqttClient) {
        this.mqttClient = mqttClient;
    }

    /**
     * 隶属于的连接池
     */
    private MqttConnectionPool pool;


    /**
     * 推送方法消息
     */
    public void publish(String topic, String message) throws Exception {
        MqttMessage mqttMessage = new MqttMessage();
        mqttMessage.setPayload(message.getBytes());
        mqttClient.publish(topic, mqttMessage);
    }


    /**
     * 销毁连接
     */
    public void destroy() {
        try {
            if (this.mqttClient.isConnected()) {
                this.mqttClient.disconnect();
            }
            this.mqttClient.close();
        } catch (Exception e) {
            LOGGER.error("MqttConnection destroy ERROR ; errorMsg={}", e.getMessage(), e);
        }
    }

    /**
     * 换回连接池
     */
    public void close() {
        if (pool != null) {
            MqttConnectionPool dataSource = this.pool;
            this.pool = null;
            dataSource.returnObject(this);
        }
    }


    public MqttClient getMqttClient() {
        return mqttClient;
    }

    public void setMqttClient(MqttClient mqttClient) {
        this.mqttClient = mqttClient;
    }

    public GenericObjectPool<MqttConnection> getPool() {
        return pool;
    }

    public void setPool(MqttConnectionPool pool) {
        this.pool = pool;
    }
}
