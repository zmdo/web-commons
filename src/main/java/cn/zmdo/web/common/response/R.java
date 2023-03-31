package cn.zmdo.web.common.response;

import com.fasterxml.jackson.annotation.JsonIgnore;
import cn.zmdo.web.common.annotation.ErrorCode;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.List;

/**
 * 通用返回
 * @param <T> 返回的数据类型
 */
@Data
@NoArgsConstructor
public class R<T> implements Cloneable {

    /** 错误码 */
    private int code;

    /** 返回消息 */
    private String message;

    /** 返回的数据 */
    private T data;

    /**
     * 是否需要国际化翻译消息
     * <p>
     *     <font style="color:red;">
     *     <b>注意：</b> 该值不会通过json序列化返回
     *     </font>
     * </p>
     */
    @JsonIgnore
    private Boolean needTranslateMessage;

    /**
     * 国际化翻译消息需要的参数
     * <p>
     *     <font style="color:red;">
     *     <b>注意：</b> 该值不会通过json序列化返回
     *     </font>
     * </p>
     */
    @JsonIgnore
    private Object[] messageParams;

    /**
     * 消息前缀
     */
    @JsonIgnore
    private String messagePrefix;

    public R(int code, String message) {
        this(code,message,null);
    }

    public R(int code, String message, T data) {
        this.code = code;
        this.message = message;
        this.data = data;
    }

    /**
     * 克隆一个返回结果
     * <p>
     *     <font style="color:red;">
     *     <b>注意：</b> 这只是一个浅复制，请在确保数据安全的情况下使用
     *     </font>
     * </p>
     * @return 当前结果的克隆
     */
    @Override
    public R<T> clone() {
        R<T> newR = new R<>(code,message,data);
        newR.setNeedTranslateMessage(needTranslateMessage);
        newR.setMessageParams(messageParams);
        return newR;
    }

    /**
     * 直接返回成功结果
     * @return 成功结果
     * @param <T> data的类型
     */
    public static <T> R<T> success() {
        return success(StandardCode.SUCCESS_MESSAGE,null);
    }

    /**
     * 直接返回成功结果
     * @param message 消息
     * @return 成功结果
     * @param <T> data的类型
     */
    public static <T> R<T> success(String message) {
        return new R<>(StandardCode.SUCCESS,message,null);
    }

    /**
     * 直接返回成功结果
     * @param message 消息
     * @param data 数据
     * @return 成功结果
     * @param <T> data的类型
     */
    public static <T> R<T> success(String message,T data) {
        return new R<>(StandardCode.SUCCESS,message,data);
    }

    /**
     * 直接返回成功结果
     * @param data 数据
     * @return 成功结果
     * @param <T> data的类型
     */
    public static <T> R<T> success(T data) {
        return R.success(StandardCode.SUCCESS_MESSAGE,data);
    }

    /**
     * 直接返回失败结果
     * @return 失败结果
     * @param <T> data的类型
     */
    public static <T> R<T> failure() {
        return failure(StandardCode.FAILURE_MESSAGE);
    }

    /**
     * 直接返回失败结果
     * @param message 失败信息
     * @return 失败结果
     * @param <T> data的类型
     */
    public static <T> R<T> failure(String message) {
        return new R<>(StandardCode.FAILURE,message);
    }

    /**
     * 自定义错误码的返回值
     * <p>
     *     <font style="color:red;">
     *     <b>注意：</b> 错误码不能等于 0 ,参见 {@link StandardCode#SUCCESS}
     *     </font>
     * </p>
     * @param code 自定义的错误码
     * @param message 错误消息
     * @return 失败结果
     * @param <T> data的类型
     */
    public static <T> R<T> failure(int code,String message) {
        if (code != StandardCode.SUCCESS) {
            return new R<>(code,message);
        }
        throw new RuntimeException("无效的错误码");
    }

    /**
     * 根据错误码枚举类返回失败结果
     * <p>
     *     错误码枚举需要被 {@link ErrorCode} 注解进行标注
     * </p>
     * @param errorCode 错误码枚举类的一个值
     * @param params 错误码参数
     * @return 失败结果
     * @param <E> 枚举类类型
     * @param <T> data的类型
     * @see ErrorCode
     */
    public static <E extends Enum<E>,T> R<T> failure(E errorCode,Object...params) {
        Class<?> errorCodeClass = errorCode.getClass();
        if(errorCodeClass.isAnnotationPresent(ErrorCode.class)) {

            int code ;
            String message;

            try {

                // 获取错误码标准值
                Method codeMethod = errorCodeClass.getMethod("code");
                code = (int) codeMethod.invoke(errorCode);

                // 获取国际化的 message 方法
                Method messageIdMethod = null;
                Method[] methods = errorCodeClass.getMethods();
                for (Method method : methods) {
                    if (method.getName().equals("message") && method.getParameterCount() == 0) {
                        messageIdMethod = errorCodeClass.getMethod("message");
                        break;
                    }
                }

                // 获取 message 消息，如果没有 message 方法，那么就将枚举名作为 message
                if (messageIdMethod != null) {
                    message = (String) messageIdMethod.invoke(errorCode);
                } else {
                    message = errorCode.name();
                }

            } catch (NoSuchMethodException | InvocationTargetException | IllegalAccessException  e) {
                throw new RuntimeException(e);
            }

            // 创建结果
            R<T> result = new R<>(code,message);

            // 判断是否需要国际化
            ErrorCode errorCodeAnno = errorCodeClass.getAnnotation(ErrorCode.class);
            if (errorCodeAnno.translate()) {
                // 设置国际化属性需要的字段
                result.setNeedTranslateMessage(true);
                result.setMessageParams(params);
            }

            // 设置前缀
            if (!errorCodeAnno.messagePrefix().isEmpty()) {
                result.setMessagePrefix(errorCodeAnno.messagePrefix());
            }

            // 返回结果
            return result;

        } else {
            throw new RuntimeException("不能使用未注解的 ErrorCode");
        }
    }

    /**
     * 返回一页对象（默认返回状态为成功）
     * @param current 当前是第几页
     * @param size 一页能存多少数据
     * @param total 一共多少页
     * @param records 当前页记录的数据
     * @return {@link Page 页标准返回对象}
     * @param <T> 数据类型
     * @see Page
     */
    public static <T> R<Page<T>> page(long current,long size,long total,List<T> records) {
        Page<T> page = new Page<>(
                current,
                size,
                total,
                records
        );
        return R.success(page);
    }

}
