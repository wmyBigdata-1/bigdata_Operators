package com.wmy.ct.common.bean;

import java.io.Closeable;

/**
 * 数据输出
 * @author WUMINGYANG
 * @version 1.0
 * @date 2021/1/9 15:08
 */
public interface DataOut extends Closeable {
    public void setPath(String path);

    public void write( Object data ) throws Exception;
    public void write( String data ) throws Exception;
}
