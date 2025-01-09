package com.z.utils;

import com.z.pool.MqttConnection;
import com.z.pool.MqttConnectionPool;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;


@Service
public class MqttUtils {

    private final Logger LOGGER = LoggerFactory.getLogger(this.getClass());

    @Autowired
    private MqttConnectionPool mqttConnectionPool;

    public void publish(String clientId, String message) {
        MqttConnection connection = null;
        try {
            connection = mqttConnectionPool.borrowObject();
            connection.publish(clientId, message);
        } catch (Exception e) {
            if(LOGGER.isErrorEnabled()){
                LOGGER.error("publish ERROR ; clientId={},message={}", clientId, message, e);
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
