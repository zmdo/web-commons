package cn.zmdo.web.common.enumeration;

import com.fasterxml.jackson.annotation.JsonValue;

/**
 * 标准枚举接口
 * @param <T> 枚举类
 */
public interface StandardEnumerator<T extends Enum<T>> {

    int code();

    @JsonValue
    String value();

}
