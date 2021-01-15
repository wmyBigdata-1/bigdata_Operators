package com.wmy.ct.common.bean;

import java.io.Closeable;
import java.io.IOException;

/**
 * 面向对象接口编程：生产者接口
 * @author WUMINGYANG
 * @version 1.0
 * @date 2021/1/9 15:06
 */
public interface Producer extends Closeable {

    public void setIn(DataIn in);
    public void setOut(DataOut out);

    /**
     * 生产数据
     */
    public void produce();
}
