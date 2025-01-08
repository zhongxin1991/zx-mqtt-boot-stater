package com.z.pool;

import com.z.utils.IdUtils;
import org.apache.commons.pool2.BasePooledObjectFactory;
import org.apache.commons.pool2.PooledObject;
import org.apache.commons.pool2.impl.DefaultPooledObject;
import org.eclipse.paho.client.mqttv3.MqttClient;
import org.eclipse.paho.client.mqttv3.MqttConnectOptions;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.atomic.AtomicInteger;

public class MqttConnectionFactory extends BasePooledObjectFactory<MqttConnection> {

    private static final Logger LOGGER = LoggerFactory.getLogger(MqttConnectionFactory.class);

    private final AtomicInteger counter = new AtomicInteger();

    private final String serverURI;

    private final MqttConnectOptions mqttConnectOptions;


    public MqttConnectionFactory(String serverURI, MqttConnectOptions mqttConnectOptions) {
        this.serverURI = serverURI;
        this.mqttConnectOptions = mqttConnectOptions;
    }

    @Override
    public MqttConnection create() throws Exception {
        int count = this.counter.addAndGet(1);
        String clientId = IdUtils.nextId() + "_" + count;

        // 创建MQTT连接对象
        MqttClient mqttClient = new MqttClient(serverURI, clientId);

        // 建立连接
        mqttClient.connect(mqttConnectOptions);

        // 构建mqttConnection对象
        return new MqttConnection(mqttClient);
    }

    @Override
    public PooledObject<MqttConnection> wrap(MqttConnection mqttConnection) {
        return new DefaultPooledObject<>(mqttConnection);
    }

    @Override
    public void destroyObject(PooledObject<MqttConnection> p) throws Exception {
        if (p == null) {
            return;
        }
        MqttConnection mqttConnection = p.getObject();
        mqttConnection.destroy();
    }

    @Override
    public boolean validateObject(PooledObject<MqttConnection> p) {
        MqttConnection mqttConnection = p.getObject();
        return mqttConnection.getMqttClient().isConnected();
    }



}
