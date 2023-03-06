package cn.zmdo.web.common.config;

import cn.zmdo.web.common.i18n.MessageBundle;
import cn.zmdo.web.common.i18n.ResponseMessageHandler;
import cn.zmdo.web.common.response.ResponseBodyHandlerManager;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;

@Configuration
@ComponentScan("cn.zmdo.web.common.response")
public class WebCommonConfiguration {

    @Autowired
    private MessageSource messageSource;

    /**
     * 响应内容处理器管理器
     * <p>
     *     里面自动注入了国际化的包
     * </p>
     * @return {@link ResponseBodyHandlerManager 响应内容处理器管理器实体对象}
     */
    @Bean
    public ResponseBodyHandlerManager responseBodyHandlerManager() {
        ResponseBodyHandlerManager responseBodyHandlerManager = new ResponseBodyHandlerManager();
        responseBodyHandlerManager.add(
                new ResponseMessageHandler(
                        new MessageBundle(messageSource)
                )
        );
        return responseBodyHandlerManager;
    }

}
