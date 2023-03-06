package cn.zmdo.web.common.i18n;

import org.springframework.context.MessageSource;
import org.springframework.context.i18n.LocaleContextHolder;

/**
 * 通用国际化包
 */
public class MessageBundle {

    private final MessageSource messageSource;

    public MessageBundle(MessageSource messageSource) {
        this.messageSource = messageSource;
    }

    /**
     * 获取一条国际化信息
     * @param key 键
     * @param params 参数
     * @return 国际化信息
     */
    public String get(String key,Object...params) {
        return messageSource.getMessage(key,params, LocaleContextHolder.getLocale());
    }

    public MessageSource getMessageSource() {
        return messageSource;
    }

}
