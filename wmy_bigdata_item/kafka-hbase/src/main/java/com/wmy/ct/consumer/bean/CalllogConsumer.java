package com.wmy.ct.consumer.bean;

import com.wmy.ct.common.bean.Consumer;
import com.wmy.ct.common.constant.Names;
import com.wmy.ct.consumer.dao.HBaseDao;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.clients.consumer.ConsumerRecords;
import org.apache.kafka.clients.consumer.KafkaConsumer;

import java.io.IOException;
import java.util.Arrays;
import java.util.Properties;

/**
 * 通话日志消费者对象
 * @author WUMINGYANG
 * @version 1.0
 * @date 2021/1/11
 */
public class CalllogConsumer implements Consumer {
    /**
     * 消费数据
     */
    @Override
    public void consume() {
        try {
            // 创建配置对象
            Properties props = new Properties(); // 高级API，我们使用配置文件的方式来进行写入
            props.load(Thread.currentThread().getContextClassLoader().getResourceAsStream("consumer.properties"));

            // 获取Flume采集的数据
            KafkaConsumer<String, String> consumer = new KafkaConsumer<String, String>(props);

            // 关注主题
            // consumer.subscribe(Arrays.asList("ct"));
            consumer.subscribe(Arrays.asList(Names.TOPIC.getValue()));

            // 把HBase放到Consumer当中
            HBaseDao hBaseDao = new HBaseDao();
            // 需要初始化，命名空间、表……
            hBaseDao.init();

            // 消费数据
            while (true) {
                ConsumerRecords<String, String> consumerRecords = consumer.poll(100);
                for (ConsumerRecord<String, String> consumerRecord : consumerRecords) {

                    // 需要把数据保存到HBASE当中
                    // 常用的方式
                    hBaseDao.inserDatas(consumerRecord.value());

                    // 下面这种方式是使用对象的方式
                    //Calllog log = new Calllog(consumerRecord.value()); // 如何把一行数据变成一个对象
                    //hBaseDao.insertData(log); // 这个是升级版的java
                    //Thread.sleep(500);
                    System.out.println(consumerRecord.value());
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 关闭资源
     * @throws IOException
     */
    @Override
    public void close() throws IOException {
    }
}