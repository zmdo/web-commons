package cn.zmdo.web.common.response;

import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * 页标准返回
 * @param <T> 类
 */
@Data
@NoArgsConstructor
public class Page<T> {

    /**
     * 当前是第几页
     * <p>
     *     <font style="color:red;">
     *     <b>注意：</b> 页数是从0开始计数的
     *     </font>
     * </p>
     */
    private Long currentPage;

    /**
     * 一页有多少条数据
     */
    private Long pageSize;

    /**
     * 一共有多少条数据
     */
    private Long total;

    /**
     * 当前页的数据
     */
    private List<T> records;

    public Page(long current,long size,long total,List<T> records) {
        this.currentPage = current;
        this.pageSize = size;
        this.total = total;
        this.records = records;
    }

    /**
     * 创建一个单页的记录
     * <p>
     *     一页中的只包含 records 列表大小的数据
     * </p>
     * @param records 页中需要存放的数据
     * @return {@link Page 一页数据}
     * @param <T> 返回的数据类型
     */
    public static <T> Page<T> singlePage(List<T> records) {
        return new Page<>(0L, records.size(), records.size(), records);
    }

}
