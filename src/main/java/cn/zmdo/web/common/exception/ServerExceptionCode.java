package cn.zmdo.web.common.exception;

/**
 * 服务器异常码
 * <p>
 *     服务器异常码范围为：0xA00000 ~ 0xAFFFFF，以下为服务器异常下的细分类目：
 * </p>
 * <div class="classUseContainer">
 * <table class="overviewSummary" border="0" width="600" cellpadding="3" cellspacing="0">
 * <tr>
 * <th>异常类型</th>
 * <th>范围</th>
 * <th>备注</th>
 * </tr>
 * <tr>
 * <td>数据库异常</td>
 * <td>0xA10000 ~ 0xA1FFFF</td>
 * <td>{@link ServerExceptionCode#DATABASE_EXCEPTION_CODE}</td>
 * </tr>
 * <tr>
 * <td>远程调用异常</td>
 * <td>0xA20000 ~ 0xA2FFFF</td>
 * <td>{@link ServerExceptionCode#RPC_EXCEPTION_CODE}</td>
 * </tr>
 * <tr>
 * <td>第三方服务异常</td>
 * <td>0xA30000 ~ 0xA3FFFF</td>
 * <td>{@link ServerExceptionCode#THIRD_PART_SERVICE_EXCEPTION}</td>
 * </tr>
 * <tr>
 * <td>本地工具类异常</td>
 * <td>0xA40000 ~ 0xA4FFFF</td>
 * <td>{@link ServerExceptionCode#LOCAL_TOOL_CLASS_EXCEPTION}</td>
 * </tr>
 * </table>
 * </div>
 */
public interface ServerExceptionCode {

    /**
     * 服务器异常，范围：0xA00000 ~ 0xAFFFFF
     */
    int SERVER_EXCEPTION_CODE = 0xA00000 ;

    /**
     * 数据库异常，范围：0xA10000 ~ 0xA1FFFF
     * <p>
     *     当数据库调用出现异常时报告数据库异常
     * </p>
     */
    int DATABASE_EXCEPTION_CODE = 0xA10000;

    /**
     * 远程调用异常，范围：0xA20000 ~ 0xA2FFFF
     * <p>
     *     远程调用注册中心的服务出现异常时，报告远程调用异常
     * </p>
     * 注意，不包括第三方远程调用，请参见：{@link ServerExceptionCode#THIRD_PART_SERVICE_EXCEPTION}
     */
    int RPC_EXCEPTION_CODE = 0xA20000;

    /**
     * 第三方服务异常，范围：0xA30000 ~ 0xA3FFFF
     * <p>
     *     使用第三方服务出现异常时，报告第三方服务异常
     * </p>
     */
    int THIRD_PART_SERVICE_EXCEPTION = 0xA30000;

    /**
     * 本地工具类异常，范围：0xA40000 ~ 0xA4FFFF
     * <p>
     *     在本地的工具类使用时产生的异常
     * </p>
     */
    int LOCAL_TOOL_CLASS_EXCEPTION = 0xA40000;

}
