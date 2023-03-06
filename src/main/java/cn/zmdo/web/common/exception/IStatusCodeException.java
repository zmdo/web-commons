package cn.zmdo.web.common.exception;

public interface IStatusCodeException {

    int getCode();

    default int code() {
        return getCode();
    }

}
