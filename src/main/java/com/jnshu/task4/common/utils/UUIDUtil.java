package com.jnshu.task4.common.utils;

import java.util.UUID;

/**
 * @program: task4
 * @description: 生成uuid
 * @author: Mr.Chen
 * @create: 2019-03-31 13:53
 * @contact:938738637@qq.com
 **/
public class UUIDUtil {
    public UUIDUtil() {
    }

    public static String getUUID() {
        UUID uuid = UUID.randomUUID();
        String str = uuid.toString();
        //去掉“-”符号
        String temp =str.substring(0,8)+str.substring(9,13)+str.substring(14,18)
                +str.substring(19,23)+str.substring(24);
        return temp;
//        return UUID.randomUUID().toString().replace("-","");
    }

    public static void main(String[] args) {
        String s = UUIDUtil.getUUID();
        System.out.println(s);
    }
}
