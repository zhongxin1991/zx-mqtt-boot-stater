package com.z.constant;

public enum Qos {

    AT_MOST_ONCE(0),
    AT_LEAST_ONCE(1),
    EXACTLY_ONCE(2);

    private int value;

    Qos(int value) {
        this.value = value;
    }

    public int getValue() {
        return value;
    }

}
