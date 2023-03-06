package cn.zmdo.web.common.util.exception;

import cn.zmdo.web.common.exception.ServerExceptionCode;
import cn.zmdo.web.common.exception.ServerRuntimeException;

public class StringFormatSignException extends ServerRuntimeException {

    public static final int CODE = ServerExceptionCode.LOCAL_TOOL_CLASS_EXCEPTION | 0x00A2;

    public StringFormatSignException(String message) {
        super(CODE, message);
    }

}
