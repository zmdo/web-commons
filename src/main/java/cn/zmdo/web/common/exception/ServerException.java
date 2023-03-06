package cn.zmdo.web.common.exception;

/**
 * 服务器异常
 */
public class ServerException extends Throwable implements IStatusCodeException{

    /**
     * 错误码
     */
    private final int code;

    public ServerException(int code,String message) {
        super(String.format("0x%X",code) + ":" + message);
        this.code = code;
    }

    @Override
    public int getCode() {
        return code;
    }

}
