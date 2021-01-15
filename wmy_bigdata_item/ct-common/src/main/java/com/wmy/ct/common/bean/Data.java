package com.wmy.ct.common.bean;

/**
 * @author WUMINGYANG
 * @version 1.0
 * @date 2021/1/9 15:10
 */
public  abstract class Data implements Value{
    public String content;

    @Override
    public void setValue(Object val) {
        content = (String) val;
    }

    @Override
    public String getValue() {
        return content;
    }
}
