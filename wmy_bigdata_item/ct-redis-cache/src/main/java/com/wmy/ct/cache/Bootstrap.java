package com.wmy.ct.cache;

import com.wmy.ct.common.util.JDBCUtil;
import redis.clients.jedis.Jedis;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

/**
 * 启动缓存客户端
 * @author WUMINGYANG
 * @version 1.0
 * @date 2021/1/13
 */
public class Bootstrap {
    public static void main(String[] args) {
        // 读取Mysql中的数据
        Map<String, String> userMap = new HashMap<String, String>();

        // 向redis中存储数据
        Connection connection = null;
        PreparedStatement pstat = null;
        ResultSet rs = null;

        try {

            connection = JDBCUtil.getConnection();

            String queryUserSql = "select tel,name from ct_user";
            pstat = connection.prepareStatement(queryUserSql);
            rs = pstat.executeQuery();
            while ( rs.next() ) {
                String tel = rs.getString(1);
                String name = rs.getString(2);
                userMap.put(tel, name);
            }

            rs.close();

        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if ( rs != null ) {
                try {
                    rs.close();
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            }
            if ( pstat != null ) {
                try {
                    pstat.close();
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            }
            if ( connection != null ) {
                try {
                    connection.close();
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            }

            // 向redis中放数据
            Jedis jedis = new Jedis("bigdata111",6379);
            Iterator<String> keyIterator = userMap.keySet().iterator();
            while (keyIterator.hasNext()) {
                String key = keyIterator.next();
                String value = userMap.get(key);
                jedis.hset("ct_user", key, "" + value); // hash
            }


        }
    }
}