package cn.zmdo.web.common.i18n;

import cn.zmdo.web.common.response.IResponseBodyHandler;
import cn.zmdo.web.common.response.R;
import lombok.Data;

/**
 * 返回消息拦截器
 */
@Data
public class ResponseMessageHandler implements IResponseBodyHandler {

    /**
     * 默认拦截优先级顺序为 0
     */
    public static final int DEFAULT_ORDER = 0;

    /**
     * 拦截优先级顺序
     */

    private int order;

    /**
     * 国际化处理包
     */
    private MessageBundle messageBundle;

    public ResponseMessageHandler(MessageBundle messageBundle) {
        this.messageBundle = messageBundle;
        this.order = order();
    }

    @Override
    public int order() {
        return order;
    }

    public <T> R<T> handle(R<T> response) {
        R<T> newResponse;

        if(Boolean.TRUE == response.getNeedTranslateMessage()) {

            // 初始化响应结果
            newResponse = new R<>(
                    response.getCode(),
                    null,
                    response.getData()
            );

            // 将消息进行国际化翻译
            String message = messageBundle.get(
                    response.getMessage(),
                    response.getMessageParams());
            newResponse.setMessage(message);

        } else {
            newResponse = response.clone();
        }

        return newResponse;
    }

}
