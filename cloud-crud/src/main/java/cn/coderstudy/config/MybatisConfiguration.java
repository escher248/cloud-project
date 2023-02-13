package cn.coderstudy.config;

import cn.coderstudy.prop.BizPropertySource;
import org.springframework.context.annotation.Configuration;

/**
 * @author liujun
 * @version 1.0
 * @description: MybatisConfiguration
 * @date 2023/2/13 10:14 下午
 */
@Configuration(proxyBeanMethods = false)
@BizPropertySource(loadActiveProfile = false,value = "classpath:/mybatis.yml")
public class MybatisConfiguration {
}
