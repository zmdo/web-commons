package cn.zmdo.web.common.converter;

import org.springframework.core.convert.converter.Converter;

import java.util.ArrayList;
import java.util.List;

public interface AdvancedConverter<S,T> extends Converter<S,T> {

    /**
     * 转换列表
     * @param sList 输入列表
     * @return 转换后的列表
     */
    default List<T> convert(List<S> sList) {
        List<T> tList = new ArrayList<>();
        for (S s : sList) {
            tList.add(convert(s));
        }
        return tList;
    }

}
