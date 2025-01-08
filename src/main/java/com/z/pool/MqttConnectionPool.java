package com.z.pool;

import org.apache.commons.pool2.impl.GenericObjectPool;
import org.apache.commons.pool2.impl.GenericObjectPoolConfig;

public class MqttConnectionPool extends GenericObjectPool<MqttConnection> {

    public MqttConnectionPool(MqttConnectionFactory factory,
                              GenericObjectPoolConfig<MqttConnection> config) {
        super(factory, config);
    }

    @Override
    public MqttConnection borrowObject() throws Exception {
        MqttConnection conn = super.borrowObject();
        // 设置所属连接池
        if (conn.getPool() == null) {
            conn.setPool(this);
        }
        return conn;
    }

    @Override
    public void returnObject(MqttConnection conn) {
        if (conn != null) {
            super.returnObject(conn);
        }
    }
}
