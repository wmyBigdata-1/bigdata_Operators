package com.wmy.ct.common.util;

import java.text.DecimalFormat;

/**
 * 数字工具类
 *
 * @author WUMINGYANG
 * @version 1.0
 * @date 2021/1/10 15:56
 */
public class NumberUtil {
    /**
     * 将数字格式化为字符串
     * @param number
     * @param length
     * @return
     */
    public static String format(int number, int length) {
        StringBuilder stringBuilder = new StringBuilder();
        for (int i = 0; i < length; i++) {
            stringBuilder.append("0");
        }
        DecimalFormat df = new DecimalFormat(stringBuilder.toString());
        return df.format(number);
    }
}
