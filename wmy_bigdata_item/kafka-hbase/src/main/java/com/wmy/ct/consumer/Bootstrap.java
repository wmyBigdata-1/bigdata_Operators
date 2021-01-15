package com.wmy.ct.consumer;

import com.wmy.ct.common.bean.Consumer;
import com.wmy.ct.consumer.bean.CalllogConsumer;

import java.io.IOException;

/**
 * 启动消费者：使用Kafka的消费者来获取Flume采集的数据，将数据直接存储到Hbase中
 * @author WUMINGYANG
 * @version 1.0
 * @date 2021/1/11
 */
public class Bootstrap {
    public static void main(String[] args) throws IOException {
        // 创建消费者
        Consumer consumer = new CalllogConsumer();

        
        // 消费数据
        consumer.consume();

        // 关闭资源
        consumer.close();
    }
}