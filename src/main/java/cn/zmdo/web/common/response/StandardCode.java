package cn.zmdo.web.common.response;

import cn.zmdo.web.common.exception.ServerExceptionCode;

/**
 * 标准返回码列表
 */
public interface StandardCode {

    /** 通用成功码 */
    int SUCCESS = 0x000000;

    /** 通用成功响应消息 **/
    String SUCCESS_MESSAGE = "success";

    /** 通用失败码 */
    int FAILURE = 0x000001;

    /** 通用失败响应消息 */
    String FAILURE_MESSAGE = "failure";

    /** 服务器错误 */
    int SERVER_ERROR = ServerExceptionCode.SERVER_EXCEPTION_CODE;

    /** 服务器错误 */
    String SERVER_ERROR_MESSAGE = "server error";

}
