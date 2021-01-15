package com.wmy.ct.common.constant;

import java.util.Enumeration;
import java.util.HashMap;
import java.util.Map;
import java.util.ResourceBundle;

/**
 * 配置常量
 * @author WUMINGYANG
 * @version 1.0
 * @date 2021/1/11
 */
public class ConfigConstant {
    private static Map<String, String> valueMap = new HashMap<String, String>();
    static {
        // 如何读取配置文件：国际化，当时的网站希望一个网站有多门语言，在不同的环境下显示不同的文字，在什么环境下显示什么语言
        // 页面布局就显得很难看，用户的友好度不好，不同的语言对应不同的页面
        ResourceBundle resourceBundle = ResourceBundle.getBundle("wmy_ct");
        Enumeration<String> enumeration = resourceBundle.getKeys();
        while (enumeration.hasMoreElements()) {
            String elementKey = enumeration.nextElement();
            String elementValue = resourceBundle.getString(elementKey);
            valueMap.put(elementKey, elementValue);
        }
    }

    public static String getValue(String key) {
        return valueMap.get(key);
    }

    public static void main(String[] args) {
        String value = ConfigConstant.getValue("wmy_ct.namespace");
        System.out.println(value);
    }
}