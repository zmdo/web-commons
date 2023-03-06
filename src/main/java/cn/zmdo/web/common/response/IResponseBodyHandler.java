package cn.zmdo.web.common.response;

import java.util.Comparator;

/**
 * 响应体处理器
 */
public interface IResponseBodyHandler {

    /**
     * 拦截优先级顺序
     * <p>值越小优先级越高</p>
     * @return 优先级值
     */
    int order();

    /**
     * 预处理方法
     * @param response 源结果
     * @return 处理后的结果
     * @param <T> 原结果中数据类型
     */
    <T> R<T> handle(R<T> response);

    /**
     * 优先级顺序比较器
     */
    Comparator<IResponseBodyHandler> ORDER_COMPARATOR =
            (o1, o2) -> o1.order() - o2.order();

}
