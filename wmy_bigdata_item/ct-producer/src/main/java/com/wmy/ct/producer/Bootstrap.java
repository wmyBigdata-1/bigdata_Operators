package com.wmy.ct.producer;

import com.wmy.ct.common.bean.Producer;
import com.wmy.ct.producer.bean.LocalFileProducer;
import com.wmy.ct.producer.io.LocalFileDataIn;
import com.wmy.ct.producer.io.LocalFileDataOut;

import java.io.IOException;

/**
 * 启动对象：对象的的开发思路就是组合各个模块
 * @author WUMINGYANG
 * @version 1.0
 * @date 2021/1/9 15:17
 */
public class Bootstrap {
    public static void main(String[] args) throws IOException {
        if (args.length < 2) {
            System.out.println("系统参数不正确，请按照指定格式传递：java -jar Produce.java path1 path2");
            System.exit(1);
        }

        // 构建生产者对象
        Producer producer = new LocalFileProducer();

        // Windows 平台
        // producer.setIn(new LocalFileDataIn("D:\\bigdata\\大数据项目\\尚硅谷大数据技术之电信客服综合案例\\2.资料\\辅助文档\\contact.log"));
        // producer.setOut(new LocalFileDataOut("D:\\bigdata\\大数据项目\\尚硅谷大数据技术之电信客服综合案例\\2.资料\\辅助文档\\call.log"));

        // Linux 平台
        producer.setIn(new LocalFileDataIn(args[0]));
        producer.setOut(new LocalFileDataOut(args[1]));


        // 生产数据
        producer.produce();

        // 释放资源
        producer.close();

    }
}
