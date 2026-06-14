package com.ahuralearn.common.domain.vo;

import com.ahuralearn.common.utils.CollUtils;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "Pagination Results")
public class PageVO<T> {

    @Schema(description = " Total records")
    private Long total;

    @Schema(description = " Total pages")
    private Long pages;

    @Schema(description = "The returned data")
    private List<T> list;

    // generate the empty pagination result, which is used for the stage before page query
    public static <T> PageVO<T> empty(Long total, Long pages) {
        return new PageVO<>(total, pages, CollUtils.emptyList());
    }

    // generate the empty pagination result, which is used for the stage after page query
    public static <T> PageVO<T> empty(Page<?> page) {
        return new PageVO<>(page.getTotal(), page.getPages(), CollUtils.emptyList());
    }

    // build the pagination result automatically
    public static <T> PageVO<T> of(Page<?> page, List<T> list) {
        return new PageVO<>(page.getTotal(), page.getPages(), list);
    }
}
