package com.jnshu.task4.common.utils;

import java.util.Random;

/**
 * @program: task4
 * @description:给邮箱和手机号提供全数字的验证码
 * @author: Mr.Chen
 * @create: 2019-04-01 00:00
 * @contact:938738637@qq.com
 **/
public class NumberUtil {
    /**
     * 生成编号
     * @param number 生成的编号位数
     * @return 返回生成的随机数
     */
    public static String randomNo(int number){
        double rate1 = Math.pow(10,number-1);
        double rate2 = rate1*9;
        long rate3 = (long)rate1*10;
        Random random = new Random();
        double tmp= random.nextDouble()*rate2+rate1;
        long no = Math.round(tmp)%rate3;
        return String.valueOf(no);
    }

    public static void main(String[] args) {
        String s = randomNo(6);
        System.out.println(s);
    }
}
