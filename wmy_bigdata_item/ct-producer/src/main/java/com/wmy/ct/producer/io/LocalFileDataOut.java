package com.wmy.ct.producer.io;

import com.wmy.ct.common.bean.DataOut;

import java.io.*;

/**
 * 本地文件的数据输出
 * @author WUMINGYANG
 * @version 1.0
 * @date 2021/1/9 15:24
 */
public class LocalFileDataOut implements DataOut {
    private PrintWriter printWriter = null;

    public LocalFileDataOut(String path) {
        setPath(path);
    }

    @Override
    public void setPath(String path) {
        try {
            printWriter = new PrintWriter(new OutputStreamWriter(new FileOutputStream(path), "UTF-8"));
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void write(Object data) throws Exception {
        write(data.toString());
    }

    /**
     * 将数据字符串生成到文件中
     * @param data
     * @throws Exception
     */
    @Override
    public void write(String data) throws Exception {
        printWriter.println(data);
        printWriter.flush(); // 将流中的数据放到文件中去，来一条刷新一条
    }

    @Override
    public void close() throws IOException {
        if (printWriter != null) {
            printWriter.close();
        }
    }
}
