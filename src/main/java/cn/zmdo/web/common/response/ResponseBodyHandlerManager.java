package cn.zmdo.web.common.response;

import java.util.TreeSet;

/**
 * 响应处理管理器
 */
public class ResponseBodyHandlerManager extends TreeSet<IResponseBodyHandler> {

    public ResponseBodyHandlerManager() {
        super(IResponseBodyHandler.ORDER_COMPARATOR);
    }

    /**
     * 对响应进行处理
     * @param response 源响应
     * @return 处理后的响应
     * @param <T> 响应中的数据类型
     */
    public <T> R<T> handle(R<T> response) {
        R<T> result = response;
        for (IResponseBodyHandler responseBodyHandler : this) {
            result = responseBodyHandler.handle(result);
        }
        return result;
    }

}
