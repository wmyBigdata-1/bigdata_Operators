package com.wmy.ct.producer.bean;

/**
 * @author WUMINGYANG
 * @version 1.0
 * @date 2021/1/10 16:18
 */
public class Calllog {
    private String call_1;
    private String call_2;
    private String  callTime;
    private String duration;

    public Calllog(String call_1, String call_2, String callTime, String duration) {
        this.call_1 = call_1;
        this.call_2 = call_2;
        this.callTime = callTime;
        this.duration = duration;
    }

    public String getCall_1() {
        return call_1;
    }

    public void setCall_1(String call_1) {
        this.call_1 = call_1;
    }

    public String getCall_2() {
        return call_2;
    }

    public void setCall_2(String call_2) {
        this.call_2 = call_2;
    }

    public String getCallTime() {
        return callTime;
    }

    public void setCallTime(String callTime) {
        this.callTime = callTime;
    }

    public String getDuration() {
        return duration;
    }

    public void setDuration(String duration) {
        this.duration = duration;
    }

    @Override
    public String toString() {
        return call_1 + "\t" + call_2 + "\t" + callTime + "\t" + duration; // 这个方法实际上是调用Object来去实现的
    }
}
