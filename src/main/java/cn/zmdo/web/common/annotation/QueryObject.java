package cn.zmdo.web.common.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * 查询对象注解
 */
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
public @interface QueryObject {

    /**
     * 设置查询对象对应的API
     * @return API地址数组
     */
    String[] value();

    /**
     * 默认使用第几个API
     * @return 记录API的索引
     */
    int defaultAPI() default 0;

}
