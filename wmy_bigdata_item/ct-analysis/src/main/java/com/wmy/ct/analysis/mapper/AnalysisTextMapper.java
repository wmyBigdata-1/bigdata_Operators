package com.wmy.ct.analysis.mapper;

import org.apache.hadoop.hbase.client.Result;
import org.apache.hadoop.hbase.io.ImmutableBytesWritable;
import org.apache.hadoop.hbase.mapreduce.TableMapper;
import org.apache.hadoop.hbase.util.Bytes;
import org.apache.hadoop.io.Text;

import java.io.IOException;

/**
 * 分析数据Mapper
 * @author WUMINGYANG
 * @version 1.0
 * @date 2021/1/13
 */
public class AnalysisTextMapper extends TableMapper<Text, Text> {
    @Override
    protected void map(ImmutableBytesWritable key, Result value, Context context) throws IOException, InterruptedException {
        // 5_19154926260_20180802160747_16574556259_0054_1
        String rowkey = Bytes.toString(key.get());
        String[] fields = rowkey.split("_");
        String call_1 = fields[1];
        String call_2 = fields[3];
        String callTime = fields[2];
        String duration = fields[4];

        String year = callTime.substring(0,4);
        String month = callTime.substring(0, 6);
        String date = callTime.substring(0,8);

        // 主叫用户一年
        context.write(new Text(call_1 + "_" + year),new Text(duration));
        // 主叫用户已月
        context.write(new Text(call_1 + "_" + month),new Text(duration));
        // 主叫用户一日
        context.write(new Text(call_1 + "_" + date),new Text(duration));

        // 被叫用户一年
        context.write(new Text(call_2 + "_" + year),new Text(duration));
        // 主叫用户已月
        context.write(new Text(call_2 + "_" + month),new Text(duration));
        // 主叫用户一日
        context.write(new Text(call_2 + "_" + date),new Text(duration));

    }
}