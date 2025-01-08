package com.z.utils;

public class IdUtils {
    /**
     * 起始的时间戳
     */
    private final static long START_STAMP = 1480166465631L;

    /**
     * 每一部分占用的位数
     */
    private final static long SEQUENCE_BIT = 12; //序列号占用的位数
    private final static long MACHINE_BIT = 5;   //机器标识占用的位数
    private final static long DATACENTER_BIT = 5;//数据中心占用的位数

    private final static long MAX_SEQUENCE = ~(-1L << SEQUENCE_BIT);

    /**
     * 每一部分向左的位移
     */
    private final static long MACHINE_LEFT = SEQUENCE_BIT;
    private final static long DATACENTER_LEFT = SEQUENCE_BIT + MACHINE_BIT;
    private final static long TIMESTAMP_LEFT = DATACENTER_LEFT + DATACENTER_BIT;

    private static final long DATACENTER_ID = 0L;  //数据中心
    private static final long MACHINE_ID = 1L;
    //机器标识
    private static long SEQUENCE = 0L; //序列号
    private static long LAST_TIMESTAMP = -1L;//上一次时间戳


    /**
     * 产生下一个ID
     *
     * @return
     */
    public static synchronized long nextId() {
        long currStmp = getNewTimestamp();
        if (currStmp < LAST_TIMESTAMP) {
            throw new RuntimeException("Clock moved backwards.  Refusing to generate id");
        }

        if (currStmp == LAST_TIMESTAMP) {
            //相同毫秒内，序列号自增
            SEQUENCE = (SEQUENCE + 1) & MAX_SEQUENCE;
            //同一毫秒的序列数已经达到最大
            if (SEQUENCE == 0L) {
                currStmp = getNextMill();
            }
        } else {
            //不同毫秒内，序列号置为0
            SEQUENCE = 0L;
        }

        LAST_TIMESTAMP = currStmp;

        return (currStmp - START_STAMP) << TIMESTAMP_LEFT //时间戳部分
                | DATACENTER_ID << DATACENTER_LEFT       //数据中心部分
                | MACHINE_ID << MACHINE_LEFT             //机器标识部分
                | SEQUENCE;                             //序列号部分
    }

    private static long getNextMill() {
        long mill = getNewTimestamp();
        while (mill <= LAST_TIMESTAMP) {
            mill = getNewTimestamp();
        }
        return mill;
    }


    private static long getNewTimestamp() {
        return System.currentTimeMillis();
    }


}
