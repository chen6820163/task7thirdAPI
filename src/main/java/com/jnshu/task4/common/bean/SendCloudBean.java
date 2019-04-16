package com.jnshu.task4.common.bean;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.PropertySource;
import org.springframework.context.support.ClassPathXmlApplicationContext;
import org.springframework.context.support.PropertySourcesPlaceholderConfigurer;
import org.springframework.stereotype.Component;

/**
 * @program: task4
 * @description:
 * @author: Mr.Chen
 * @create: 2019-04-08 01:48
 * @contact:938738637@qq.com
 **/
@Component
@PropertySource(value = "classpath:/application.properties", ignoreResourceNotFound = true)
public class SendCloudBean {
    @Value("${sendcloud.url}")
    private String url;

    @Value("${sendcloud.apiUser}")
    private String apiUser;

    @Value("${sendcloud.apiKey}")
    private String apiKey;

    @Value("${sendcloud.from}")
    private String from;

    @Bean
    public static PropertySourcesPlaceholderConfigurer propertySourcesPlaceholderConfigurer() {
        return new PropertySourcesPlaceholderConfigurer();
    }
    public String getUrl() {
        return url;
    }
    public void setUrl(String url) {
        this.url = url;
    }
    public String getApiUser() {
        return apiUser;
    }
    public void setApiUser(String apiUser) {
        this.apiUser = apiUser;
    }
    public String getApiKey() {
        return apiKey;
    }
    public void setApiKey(String apiKey) {
        this.apiKey = apiKey;
    }
    public String getFrom() {
        return from;
    }
    public void setFrom(String from) {
        this.from = from;
    }

    @Override
    public String toString() {
        return "SendCloudBean{" +
                "url='" + url + '\'' +
                ", apiUser='" + apiUser + '\'' +
                ", apiKey='" + apiKey + '\'' +
                ", from='" + from + '\'' +
                '}';
    }

    public static void main(String[] args) {
        ApplicationContext ctx = new ClassPathXmlApplicationContext("spring-mybatis.xml");


        SendCloudBean sendCloudBean = (SendCloudBean) ctx.getBean("SendCloudBean");
        System.out.println(sendCloudBean.toString());
    }

}
