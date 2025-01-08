package com.z.confg;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Component
@ConfigurationProperties(prefix = "mqtt")
public class MqttConfigProperties {
    /**
     * MQTT host 地址
     */
    private String url;

    /**
     * 登录用户
     */
    private String userName;

    /**
     * 登录密码
     */
    private String password;


    /**
     * Mqtt Pool Config
     */
    private MqttPoolConfig pool;

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public MqttPoolConfig getPool() {
        return pool;
    }

    public void setPool(MqttPoolConfig pool) {
        this.pool = pool;
    }
}
