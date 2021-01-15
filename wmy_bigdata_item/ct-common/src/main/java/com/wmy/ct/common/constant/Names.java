package com.wmy.ct.common.constant;

import com.wmy.ct.common.bean.Value;

/**
 * 表名、字段名、列族名
 * 枚举是可以实现接口
 *
 * 名称常量枚举类
 *
 * @author WUMINGYANG
 * @version 1.0
 * @date 2021/1/9 15:13
 */
public enum  Names implements Value {

    NAMESPACE("wmyct"),
    TOPIC("calllog"),
    CF_CALLER("caller"), // 主叫列族
    CF_CALLEE("callee"), // 被叫列族
    CF_INFO("info"),
    TABLE("wmyct:calllog");

    private String name;

    private Names(String name) {
        this.name = name;
    }

    @Override
    public void setValue(Object val) {
        this.name = (String) val;
    }

    @Override
    public String getValue() {
        return name;
    }


}
