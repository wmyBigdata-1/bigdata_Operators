package com.wmy.ct.consumer.dao;

import com.wmy.ct.common.bean.BaseDao;
import com.wmy.ct.common.constant.Names;
import com.wmy.ct.common.constant.ValueConstant;
import com.wmy.ct.consumer.bean.Calllog;
import org.apache.hadoop.hbase.client.Put;
import org.apache.hadoop.hbase.util.Bytes;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * HBASE访问对象：这里面就可以获取连接对象
 * @author WUMINGYANG
 * @version 1.0
 * @date 2021/1/11
 */
public class HBaseDao extends BaseDao {
    /**
     * 初始化
     */
    public void init() throws Exception{
        start();

        /**
         * 业务逻辑：创建命名空间 和 表
         */
        createNamespaceNX(Names.NAMESPACE.getValue());
        creaTableXX(Names.TABLE.getValue(), "com.wmy.ct.consumer.coprocessor.InsertCalleeCoprocessor",ValueConstant.REGION_COUNT, Names.CF_CALLER.getValue(), Names.CF_CALLEE.getValue());

        end();
    }



    /**
     * 插入数据
     * @param value
     */
    public void inserDatas(String value) throws IOException {
        // 将童话日志保存到hbase表中
        // 1、获取通话日志的数据
        String[] fields = value.split("\t");
        String call_1 = fields[0];
        String call_2 = fields[1];
        String callTime = fields[2];
        String duration = fields[3];

        // 2、创建数据对象
        /**
         * Rowkey设计：
         *  1）长度原则
         *      最大的值64KB，推荐长度为：10 ~ 100 byte 最好是八的倍数，能短则短
         *      Rowkey：如果太长的话，会影响性能，存储空间就会变大，会大量存储Rowkey
         *
         *  2）唯一原则
         *      rowkey应该具备唯一性
         *
         *  3）散列原则
         *      3-1）盐值散列，不能使用时间戳直接作为Rowkey，导致热点数据、数据倾斜
         *          在Rowkey前增加随机数，没有规律，放到不同的分区当中
         *      3-2）字符串反转，不能使用时间戳直接作为Rowkey来进行设计，但是我们可以使用反转时间戳的方式来创建Rowkey
         *          时间戳、电话号码 ---> 都是有规律的 3 4 4
         *      3-3）计算分区号，HashMap
         */

        // 主叫用户
        // rowkey = regionNum + call_1 + time + call_2 + duration
        // regionNum如何得到，使用分区算法
        String rowkey = genRegionNum(call_1, callTime) + "_" + call_1 + "_" + callTime + "_" + call_2 + "_" + duration + "_1";// 该如何取做勒，rowkey的设计
        Put put = new Put(Bytes.toBytes(rowkey));

        byte[] family = Bytes.toBytes(Names.CF_CALLER.getValue());
        put.addColumn(family, Bytes.toBytes("call_1"), Bytes.toBytes(call_1));
        put.addColumn(family, Bytes.toBytes("call_2"), Bytes.toBytes(call_2));
        put.addColumn(family, Bytes.toBytes("callTime"), Bytes.toBytes(callTime));
        put.addColumn(family, Bytes.toBytes("duration"), Bytes.toBytes(duration));
        put.addColumn(family, Bytes.toBytes("flg"), Bytes.toBytes("1"));

        String calleeRowkey = genRegionNum(call_2, callTime) + "_" + call_2 + "_" + callTime + "_" + call_1 + "_" + duration + "_0";

        // 被叫用户
        //Put calleePut = new Put(Bytes.toBytes(calleeRowkey));
        //byte[] calleeFamily = Bytes.toBytes(Names.CF_CALLEE.getValue());
        //calleePut.addColumn(calleeFamily, Bytes.toBytes("call_1"), Bytes.toBytes(call_2));
        //calleePut.addColumn(calleeFamily, Bytes.toBytes("call_2"), Bytes.toBytes(call_1));
        //calleePut.addColumn(calleeFamily, Bytes.toBytes("calltime"), Bytes.toBytes(callTime));
        //calleePut.addColumn(calleeFamily, Bytes.toBytes("duration"), Bytes.toBytes(duration));
        //calleePut.addColumn(calleeFamily, Bytes.toBytes("flg"), Bytes.toBytes("0"));


        // 3、保存数据
        List<Put> puts = new ArrayList<Put>();
        puts.add(put);
        //puts.add(calleePut);

        putData(Names.TABLE.getValue(), puts);
    }

    /**
     * 插入对象：和数据有关系
     * @param calllog
     * @throws Exception
     */
    public void insertData(Calllog calllog) throws Exception{
        calllog.setRowkey(genRegionNum(calllog.getCall_1(), calllog.getCallTime()) + "_" + calllog.getCall_1() + "_" + calllog.getCallTime() + "_" + calllog.getCall_2() + "_" + calllog.getDuration());
        putData(calllog);
    }
}