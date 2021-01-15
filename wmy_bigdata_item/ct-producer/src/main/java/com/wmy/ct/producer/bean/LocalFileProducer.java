package com.wmy.ct.producer.bean;


import com.wmy.ct.common.bean.DataIn;
import com.wmy.ct.common.bean.DataOut;
import com.wmy.ct.common.bean.Producer;
import com.wmy.ct.common.util.DateUtil;
import com.wmy.ct.common.util.NumberUtil;

import java.io.IOException;
import java.util.Date;
import java.util.List;
import java.util.Random;

/**
 * 本地数据文件的生产者
 * @author WUMINGYANG
 * @version 1.0
 * @date 2021/1/9 15:19
 */
public class LocalFileProducer implements Producer {
    private DataIn in;
    private DataOut out;
    private volatile boolean flg = true;

    public void setIn(DataIn in) {
        this.in = in;
    }

    public void setOut(DataOut out) {
        this.out = out;
    }



    /**
     * 生产数据
     */
    @Override
    public void produce()  {
        try {
            // 读取通讯录的数据
            List<Contact> contacts = in.read(Contact.class);

            while (flg) {
                // 从通讯录中随机查找2个点号码（主叫，被叫）
                // Math.random()这个的话是没有规律的,在工作当中的话尽量使用这个
                int call_1_Index = new Random().nextInt(contacts.size()); // 这个可能是有规律的话
                int call_2_Index;
                while (true) {
                    call_2_Index = new Random().nextInt(contacts.size());
                    if (call_2_Index != call_1_Index) {
                        break;
                    }
                }

                Contact call_1 = contacts.get(call_1_Index);
                Contact call_2 = contacts.get(call_2_Index);

                // 生成随机的通话时间 2020全年的时间点
                String startDate = "20200101000000"; // 小时分钟和秒
                String endDate = "20210101000000";

                // 将字符串转换为日期格式
                long startTime = DateUtil.parse(startDate,"yyyyMMddhhmmss").getTime(); // 按照大小来开始
                long endTime = DateUtil.parse(endDate,"yyyyMMddhhmmss").getTime();

                // 通话时间：保证通话时间在范围内
                long callTime = startTime + (long)((endTime - startTime) * Math.random()); // 0.0 - 1.0 double -> long

                // 通话时间字符串 ---> 将通话时间转换为字符串
                String callTimeString = DateUtil.format(new Date(callTime),"yyyyMMddhhmmss");

                // 生成随机的通话时长
                String duration = NumberUtil.format(new Random().nextInt(3000), 4);

                // 生成通话记录给刷写到数据文件中
                Calllog calllog = new Calllog(call_1.getTel(),call_2.getTel(),callTimeString,duration);
                out.write(calllog);
                System.out.println(calllog);

                // 休眠
                Thread.sleep(500);
            }
        } catch (IOException e) {
            e.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 关闭生产者
     * @throws IOException
     */
    @Override
    public void close() throws IOException {
        if (in != null) {
            in.close();
        }
        if (out != null) {
            out.close();
        }
    }
}
