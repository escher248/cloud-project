package cn.coderstudy;

import com.alibaba.cloud.nacos.NacosConfigProperties;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.context.ConfigurableApplicationContext;

/**
 * @author liujun
 * @version 1.0
 * @description: 启动类
 * @date 2023/2/6 10:57
 */
@SpringBootApplication
@EnableDiscoveryClient
public class ConsumerApplication {
    public static void main(String[] args) {
        ConfigurableApplicationContext applicationContext = SpringApplication.run(ConsumerApplication.class, args);
        NacosConfigProperties nacosConfigProperties = applicationContext.getBean(NacosConfigProperties.class);
        System.err.println(nacosConfigProperties);

        String userName = applicationContext.getEnvironment().getProperty("spring.datasource.dev.url");
        String userAge = applicationContext.getEnvironment().getProperty("user.age");
        System.err.println("user name :"+userName+"; age: "+userAge);


    }
}
