package com.wmy.ct.common.bean;

import com.wmy.ct.common.api.Column;
import com.wmy.ct.common.api.Rowkey;
import com.wmy.ct.common.api.TableRef;
import com.wmy.ct.common.constant.Names;
import com.wmy.ct.common.constant.ValueConstant;
import com.wmy.ct.common.util.DateUtil;
import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.hbase.*;
import org.apache.hadoop.hbase.client.*;
import org.apache.hadoop.hbase.util.Bytes;

import java.io.IOException;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

/**
 * 基础的数据访问对象
 * @author WUMINGYANG
 * @version 1.0
 * @date 2021/1/11
 */
public abstract class BaseDao {
    private ThreadLocal<Connection> connHolder = new ThreadLocal<Connection>();
    private ThreadLocal<Admin> adminHolder = new ThreadLocal<Admin>();


    protected void start() throws Exception{
        getConnection();
        getAdmin();
    }

    protected void end() throws Exception {
        // 取Admin
        Admin admin = getAdmin();
        if (admin != null) {
            admin.close();
            adminHolder.remove();
        }

        Connection conn = getConnection();
        if (conn != null) {
            conn.close();
            connHolder.remove();
        }

    }

    /**
     * protected：只能让子类访问，不能封装成工具类，不能在任何地方都可以使用
     * 获取连接对象
     */
    protected Connection getConnection() throws IOException {
        Connection conn = null;
        if (conn == null) {
            Configuration conf = HBaseConfiguration.create();
            conn = ConnectionFactory.createConnection(conf);
            connHolder.set(conn);
        }
        return conn;
    }

    /**
     * protected：只能让子类访问，不能封装成工具类，不能在任何地方都可以使用
     * 获取连接对象
     */
    protected synchronized Admin getAdmin() throws IOException {
        Admin admin = null;
        if (admin == null) {
            admin = getConnection().getAdmin();
        }
        return admin;
    }

    /**
     * 创建命名空间：如果命名空间存在，不需要创建，否则创建新的
     * @param nameSpace
     */
    protected void createNamespaceNX(String nameSpace) throws IOException {
        Admin admin = getAdmin();
        try {
            admin.getNamespaceDescriptor(nameSpace); // NamespaceNotFoundException
        } catch (NamespaceNotFoundException e) {
            // e.printStackTrace();
            NamespaceDescriptor namespaceDescriptor = NamespaceDescriptor.create(nameSpace).build();
            admin.createNamespace(namespaceDescriptor);
        }
    }

    /**
     * 创建表：creaTableXX表示有表的话就删掉表在重新创建
     * @param name
     * @param columnFamilies
     * @throws Exception
     */
    protected void creaTableXX(String name, String... columnFamilies) throws Exception {
        creaTableXX(name, null, null, columnFamilies);
    }

    /**
     * 创建表：creaTableXX表示有表的话就删掉表在重新创建  ---> 方法的重载
     *
     * @param name
     * @param columnFamilies
     * @throws Exception
     */
    protected void creaTableXX(String name, String coprocessorClass, Integer regionCount, String... columnFamilies) throws Exception {
        TableName tableName = TableName.valueOf(name);
        Admin admin = getAdmin();
        boolean tableExists = admin.tableExists(tableName);
        if (tableExists) {
            // 表存在，删除表，写一个专门删除的方法
            deleteTable(name);
        }

        // 创建表
        createTable(name, coprocessorClass, regionCount, columnFamilies);
    }

    /**
     * 删除表
     * @param name
     * @throws IOException
     */
    protected void deleteTable(String name) throws IOException {
        TableName tableName = TableName.valueOf(name);
        Admin admin = getAdmin();
        admin.disableTable(tableName);
        admin.deleteTable(tableName);
    }

    /**
     * 创建表：创建这个表以后可能用不上：createTableXX
     * 表有多少个分区，负载均衡的问题，数据倾斜，热点数据
     * Integer：这样写的话我们可以填一个null
     */
    private void createTable(String name, String coprocessorClass, Integer regionCount, String... columnFamilies) throws Exception {
        Admin admin = getAdmin();
        TableName tableName = TableName.valueOf(name);
        // 表格表述器
        HTableDescriptor hTableDescriptor = new HTableDescriptor(tableName);
        // 增加列族
        if (columnFamilies == null || columnFamilies.length == 0) {
            columnFamilies = new String[1];
            columnFamilies[0] = Names.CF_INFO.getValue(); // 默认给一个info列族
        }
        for (String family : columnFamilies) {
            HColumnDescriptor columnDescriptor = new HColumnDescriptor(family);
            hTableDescriptor.addFamily(columnDescriptor);
        }

        if (coprocessorClass == null && !"".equals(coprocessorClass)) {
            hTableDescriptor.addCoprocessor(coprocessorClass); // 这个的话不能写死
        }


        // 增加预分区：分区键的二维数组
        if (regionCount == null || regionCount <= 1) {
            admin.createTable(hTableDescriptor);
        } else {
            byte[][] splitKeys = genSplitKeys(regionCount); // 生成分区键
            admin.createTable(hTableDescriptor, splitKeys);
        }
    }

    /**
     * 生成分区键
     *
     * @param regionCount
     * @return
     */
    private byte[][] genSplitKeys(Integer regionCount) {
        /**
         * [a,b] ---> [byte[],byte[]]
         */
        int splitKeyCount = regionCount - 1;
        byte[][] bs = new byte[splitKeyCount][];
        // 0|,1|,2|,3|,4|
        // 000111
        // 110000
        // 223222 ---> 把这些数据放到分区键
        // (负无穷,正无穷) 中间是包含的关系(负无穷,0|),[0|,1|),[1|,正无穷)
        List<byte[]> bsList = new ArrayList<byte[]>();
        for (int i = 0; i < splitKeyCount; i++) {
            String splitkey = i + "|";
            bsList.add(Bytes.toBytes(splitkey)); // 字符串转换为字节数组
        }
        bsList.toArray(bs);
        return bs;
    }

    /**
     * 计算分区号
     * @param tel
     * @param date
     * @return
     */
    public static int genRegionNum(String tel, String date) {
        // 18275245502
        String userCode = tel.substring(tel.length() - 4); // 电话号码的后四位：5502
        String yearMonth = date.substring(0,6); // 20201212000000 ---> 202012 ---> 能保证同一个月的通话记录是在一个分区里面
        int userCodeHash = userCode.hashCode();
        int yearMonthHash = yearMonth.hashCode();

        // crc校验：采用的是异或算法，相等为0，不同为1
        int crc = Math.abs(userCodeHash ^ yearMonthHash);

        // 取模
        int regionNum = crc % ValueConstant.REGION_COUNT;
        return regionNum;
    }

    /**
     * 增加单条数据
     * @param name
     * @param put
     * @throws IOException
     */
    protected void putData(String name, Put put) throws IOException {
        // 获取表对象
        Connection conn = getConnection();
        Table table = conn.getTable(TableName.valueOf(name));

        // 增加数据
        table.put(put);

        // 关闭表
        table.close();
    }

    /**
     * 增加多条数据
     * @param name
     * @param puts
     * @throws IOException
     */
    protected void putData(String name, List<Put> puts) throws IOException {
        // 获取表对象
        Connection conn = getConnection();
        Table table = conn.getTable(TableName.valueOf(name));

        // 增加数据
        table.put(puts);

        // 关闭表
        table.close();
    }

    /**
     * 增加对象：自动封装数据，将对象直接保存到HBase中取
     * @param obj
     * @throws Exception
     */
    protected void putData(Object obj) throws Exception {
        // 反射
        Class clazz = obj.getClass();
        TableRef tableRef = (TableRef) clazz.getAnnotation(TableRef.class);
        String tableName = tableRef.value(); // 取的是当前传的按个值@TableRef("wmyct:calllog")

        Field[] fs = clazz.getDeclaredFields();
        String stringRowkey = "";
        for (Field f : fs) {
            Rowkey rowkey = f.getAnnotation(Rowkey.class);
            if (rowkey != null) {
                f.setAccessible(true);
                stringRowkey = (String) f.get(obj);
                break;
            }
        }

        // 获取表对象
        Connection conn = getConnection();
        Table table = conn.getTable(TableName.valueOf(tableName));
        Put put = new Put(Bytes.toBytes(stringRowkey));

        for (Field f : fs) {
            Column column = f.getAnnotation(Column.class);
            if (column != null) {
                String familiy = column.familiy();
                String colName = column.column();
                if (colName == null || "".equals(colName)) {
                    colName = f.getName();
                }
                f.setAccessible(true); // 拿到我们想要的数据
                String value = (String) f.get(obj);

                put.addColumn(Bytes.toBytes(familiy),Bytes.toBytes(colName),Bytes.toBytes(value));
            }
        }

        // 增加数据
        table.put(put);

        // 关闭表
        table.close();
    }

    /**
     * 获取查询时，startRow,endRow集合
     * @param tel
     * @param start
     * @param end
     * @return
     */
    protected static List<String[]> getStartStorRowkeys( String tel, String start, String end ) {
        List<String[]> rowkeyss = new ArrayList<String[]>();

        String startTime = start.substring(0, 6);
        String endTime = end.substring(0, 6);

        Calendar startCal = Calendar.getInstance();
        startCal.setTime(DateUtil.parse(startTime, "yyyyMM"));

        Calendar endCal = Calendar.getInstance();
        endCal.setTime(DateUtil.parse(endTime, "yyyyMM"));

        while (startCal.getTimeInMillis() <= endCal.getTimeInMillis()) {

            // 当前时间
            String nowTime = DateUtil.format(startCal.getTime(), "yyyyMM");

            int regionNum = genRegionNum(tel, nowTime);

            String startRow = regionNum + "_" + tel + "_" + nowTime;
            String stopRow = startRow + "|";

            String[] rowkeys = {startRow, stopRow};
            rowkeyss.add(rowkeys);

            // 月份+1
            startCal.add(Calendar.MONTH, 1);
        }

        return rowkeyss;
    }

}