package cn.coderstudy.prop;

import org.springframework.core.Ordered;

import java.lang.annotation.*;

/**
 * @author liujun
 * @version 1.0
 * @description: BizPropretySource
 * @date 2023/2/13 9:09 下午
 */
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface BizPropertySource {

    /**
     * Indicate the resource location(s) of the properties file to be loaded.
     * for example, {@code "classpath:/com/example/app.yml"}
     *
     * @return location(s)
     */
    String value();

    /**
     * load app-{activeProfile}.yml
     *
     * @return {boolean}
     */
    boolean loadActiveProfile() default true;

    /**
     * Get the order value of this resource.
     *
     * @return order
     */
    int order() default Ordered.LOWEST_PRECEDENCE;
}
