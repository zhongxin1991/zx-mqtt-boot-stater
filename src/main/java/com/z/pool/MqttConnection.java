package com.z.pool;

import org.apache.commons.pool2.impl.GenericObjectPool;

import org.eclipse.paho.client.mqttv3.IMqttMessageListener;
import org.eclipse.paho.client.mqttv3.IMqttToken;
import org.eclipse.paho.client.mqttv3.MqttClient;
import org.eclipse.paho.client.mqttv3.MqttException;
import org.eclipse.paho.client.mqttv3.MqttMessage;
import org.eclipse.paho.client.mqttv3.MqttPersistenceException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Arrays;

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
        this.publish(topic, mqttMessage);
    }

    public void publish(String topic, byte[] payload, int qos, boolean retained) throws MqttException, MqttPersistenceException {
        MqttMessage message = new MqttMessage(payload);
        message.setQos(qos);
        message.setRetained(retained);
        this.publish(topic, message);
    }

    public void publish(String topic, MqttMessage message) throws MqttException, MqttPersistenceException {
        mqttClient.publish(topic, message);
    }

    public void setCallback(PoolMqttCallback callback) {
        mqttClient.setCallback(callback);
    }

    public void subscribe(String topicFilter) throws MqttException {
        this.subscribe(new String[]{topicFilter}, new int[]{1});
    }

    public void subscribe(String[] topicFilters) throws MqttException {
        int[] qos = new int[topicFilters.length];
        Arrays.fill(qos, 1);

        this.subscribe(topicFilters, qos);
    }

    public void subscribe(String topicFilter, int qos) throws MqttException {
        this.subscribe(new String[]{topicFilter}, new int[]{qos});
    }

    public void subscribe(String[] topicFilters, int[] qos) throws MqttException {
        this.subscribe(topicFilters, qos, (IMqttMessageListener[]) null);
    }

    public void subscribe(String topicFilter, IMqttMessageListener messageListener) throws MqttException {
        this.subscribe(new String[]{topicFilter}, new int[]{1}, new IMqttMessageListener[]{messageListener});
    }

    public void subscribe(String[] topicFilters, IMqttMessageListener[] messageListeners) throws MqttException {
        int[] qos = new int[topicFilters.length];
        Arrays.fill(qos, 1);
        this.subscribe(topicFilters, qos, messageListeners);
    }

    public void subscribe(String topicFilter, int qos, IMqttMessageListener messageListener) throws MqttException {
        this.subscribe(new String[]{topicFilter}, new int[]{qos}, new IMqttMessageListener[]{messageListener});
    }

    public void subscribe(String[] topicFilters, int[] qos, IMqttMessageListener[] messageListeners) throws MqttException {
        mqttClient.subscribe(topicFilters, qos, messageListeners);
    }

    public IMqttToken subscribeWithResponse(String topicFilter) throws MqttException {
        return this.subscribeWithResponse(new String[]{topicFilter}, new int[]{1});
    }

    public IMqttToken subscribeWithResponse(String topicFilter, IMqttMessageListener messageListener) throws MqttException {
        return this.subscribeWithResponse(new String[]{topicFilter}, new int[]{1}, new IMqttMessageListener[]{messageListener});
    }

    public IMqttToken subscribeWithResponse(String topicFilter, int qos) throws MqttException {
        return this.subscribeWithResponse(new String[]{topicFilter}, new int[]{qos});
    }

    public IMqttToken subscribeWithResponse(String topicFilter, int qos, IMqttMessageListener messageListener) throws MqttException {
        return this.subscribeWithResponse(new String[]{topicFilter}, new int[]{qos}, new IMqttMessageListener[]{messageListener});
    }

    public IMqttToken subscribeWithResponse(String[] topicFilters) throws MqttException {
        int[] qos = new int[topicFilters.length];

        Arrays.fill(qos, 1);

        return this.subscribeWithResponse(topicFilters, qos);
    }

    public IMqttToken subscribeWithResponse(String[] topicFilters, IMqttMessageListener[] messageListeners) throws MqttException {
        int[] qos = new int[topicFilters.length];

        Arrays.fill(qos, 1);

        return this.subscribeWithResponse(topicFilters, qos, messageListeners);
    }

    public IMqttToken subscribeWithResponse(String[] topicFilters, int[] qos) throws MqttException {
        return this.subscribeWithResponse(topicFilters, qos, null);
    }

    public IMqttToken subscribeWithResponse(String[] topicFilters, int[] qos, IMqttMessageListener[] messageListeners) throws MqttException {
        return mqttClient.subscribeWithResponse(topicFilters, qos, messageListeners);
    }

    public void unsubscribe(String topicFilter) throws MqttException {
        this.unsubscribe(new String[]{topicFilter});
    }

    public void unsubscribe(String[] topicFilters) throws MqttException {
        mqttClient.unsubscribe(topicFilters);
    }

    public boolean isConnected() {
        return mqttClient.isConnected();
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


    public GenericObjectPool<MqttConnection> getPool() {
        return pool;
    }

    public void setPool(MqttConnectionPool pool) {
        this.pool = pool;
    }
}
