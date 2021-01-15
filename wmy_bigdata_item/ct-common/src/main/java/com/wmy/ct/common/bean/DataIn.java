package com.wmy.ct.common.bean;

import java.io.Closeable;
import java.io.IOException;
import java.util.List;

/**
 * 数据来源
 * @author WUMINGYANG
 * @version 1.0
 * @date 2021/1/9 15:08
 */
public interface DataIn  extends Closeable {
    public void setPath(String path);

    public Object read() throws IOException;
    public <T extends Data> List<T> read(Class<T> clazz) throws IOException;
}
