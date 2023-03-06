package cn.zmdo.web.common.exception;

/**
 * 服务器运行时异常（不进行检查，直接抛出）
 */
public class ServerRuntimeException extends RuntimeException implements IStatusCodeException{

    /**
     * 错误码
     */
    private final int code;

    public ServerRuntimeException(int code,String message) {
        super(String.format("0x%X",code) + ":" + message);
        this.code = code;
    }

    @Override
    public int getCode() {
        return code;
    }

}
