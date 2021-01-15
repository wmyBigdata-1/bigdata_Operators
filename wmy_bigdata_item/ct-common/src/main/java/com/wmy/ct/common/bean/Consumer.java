package com.wmy.ct.common.bean;

import java.io.Closeable;

/**
 * 消费者接口
 * @author WUMINGYANG
 * @version 1.0
 * @date 2021/1/11
 */
public interface Consumer extends Closeable {
    /**
     * 消费数据
     */
    public void consume();
}