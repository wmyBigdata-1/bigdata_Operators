package com.wmy.ct.consumer.bean;

import com.wmy.ct.common.api.Column;
import com.wmy.ct.common.api.Rowkey;
import com.wmy.ct.common.api.TableRef;

/**
 * 通话日志
 * @author WUMINGYANG
 * @version 1.0
 * @date 2021/1/12
 */
@TableRef("wmyct:calllog")
public class Calllog {
    @Rowkey
    private String rowkey;
    @Column(familiy = "caller")
    private String call_1;
    @Column(familiy = "caller")
    private String call_2;
    @Column(familiy = "caller")
    private String callTime;
    @Column(familiy = "caller")
    private String duration;
    @Column(familiy = "caller")
    private String flg = "1";
    private String name;


    public Calllog() {
    }

    public Calllog(String data) {
        String[] fields = data.split("\t");
        call_1 = fields[0];
        call_2 = fields[1];
        callTime = fields[2];
        duration = fields[3];
    }

    public String getFlg() {
        return flg;
    }

    public void setFlg(String flg) {
        this.flg = flg;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getRowkey() {
        return rowkey;
    }

    public void setRowkey(String rowkey) {
        this.rowkey = rowkey;
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
}