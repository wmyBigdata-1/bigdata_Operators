package com.wmy.ct.consumer.coprocessor;

import com.wmy.ct.common.bean.BaseDao;
import com.wmy.ct.common.constant.Names;
import org.apache.hadoop.hbase.TableName;
import org.apache.hadoop.hbase.client.Durability;
import org.apache.hadoop.hbase.client.Put;
import org.apache.hadoop.hbase.client.Table;
import org.apache.hadoop.hbase.coprocessor.BaseRegionObserver;
import org.apache.hadoop.hbase.coprocessor.ObserverContext;
import org.apache.hadoop.hbase.coprocessor.RegionCoprocessorEnvironment;
import org.apache.hadoop.hbase.regionserver.wal.WALEdit;
import org.apache.hadoop.hbase.util.Bytes;

import java.io.IOException;

/**
 * 协处理处理被叫用户插入数据
 *
 * @author WUMINGYANG
 * @version 1.0
 * @date 2021/1/12
 */

/**
 * 协处理器的使用：
 *  1、创建类
 *  2、让我们的表知道协处理类 ---> 和表有关联
 *          if (coprocessorClass == null && !"".equals(coprocessorClass)) {
 *             hTableDescriptor.addCoprocessor(coprocessorClass); // 这个的话不能写死
 *          }
 *  3、打包，运行jar，依赖jar
 *      打包关联jar分发到每个节点。
 */
public class InsertCalleeCoprocessor extends BaseRegionObserver {

    // 方法的命名规则
    // login
    // logout
    // prePut
    // doPut：模板方法设计模式
    //      存在父子类：
    //      父类搭建算法的骨架
    //          do1、tel取用户代码；do2、时间取年月；do3、异或运算；do4、hash散列
    //      子类重写算法的细节
    //          do1、tel取后四位；do2、202010；do3、^；do4、% &
    // postPut

    /**
     * 保存主叫用户数据之后，由hbase自动保存被叫用户数据
     *
     * @param e
     * @param put
     * @param edit
     * @param durability
     * @throws IOException
     */
    @Override
    public void postPut(ObserverContext<RegionCoprocessorEnvironment> e, Put put,
                        WALEdit edit, Durability durability) throws IOException {
        // 获取表
        Table table = e.getEnvironment().getTable(TableName.valueOf(Names.TABLE.getValue()));

        // 准备数据
        // 5_15781588029_20201118044327_14171709460_2587_1
        String rowkey = Bytes.toString(put.getRow());
        String[] fields = rowkey.split("_");

        CoprocessorDao dao = new CoprocessorDao(); // 使用内部类
        String call_1 = fields[1];
        String call_2 = fields[3];
        String callTime = fields[2];
        String duration = fields[4];
        String flg = fields[5];
        //String calleeRowkey = dao.getRegionNum(call_1, callTime) + "_" + call_2 + "_" + callTime + "_" + call_1 + "_" + duration + "_0"; // 计算，如果这样做的话，hbase就会崩溃，出现栈溢出
        if ("1".equals(flg)) { // 只有主叫数据保存之后才保存被叫用户数据
            String calleeRowkey = "";

            // 保存数据
            Put calleePut = new Put(Bytes.toBytes(calleeRowkey));
            byte[] calleeFamily = Bytes.toBytes(Names.CF_CALLEE.getValue());
            calleePut.addColumn(calleeFamily, Bytes.toBytes("call_1"), Bytes.toBytes(call_2));
            calleePut.addColumn(calleeFamily, Bytes.toBytes("call_2"), Bytes.toBytes(call_1));
            calleePut.addColumn(calleeFamily, Bytes.toBytes("calltime"), Bytes.toBytes(callTime));
            calleePut.addColumn(calleeFamily, Bytes.toBytes("duration"), Bytes.toBytes(duration));
            calleePut.addColumn(calleeFamily, Bytes.toBytes("flg"), Bytes.toBytes("0"));
            table.put(calleePut);

            // 关闭表
            table.close();
        }

    }

    protected class CoprocessorDao extends BaseDao{
        public int getRegionNum(String tel, String time) {
            return genRegionNum(tel, time);
        }
    }
}