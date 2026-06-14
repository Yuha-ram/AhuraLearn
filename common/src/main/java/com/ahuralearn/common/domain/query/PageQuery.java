package com.ahuralearn.common.domain.query;

import com.ahuralearn.common.utils.CollUtils;
import com.ahuralearn.common.utils.StringUtils;
import com.baomidou.mybatisplus.core.metadata.OrderItem;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Min;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import lombok.experimental.Accessors;

@Data
@Accessors(chain = true)
@Schema(description = "Pagination Parameters")
public class PageQuery {
    public static final Integer DEFAULT_PAGE_SIZE = 12;
    public static final Integer DEFAULT_PAGE_NUM = 1;

    @Schema(description = "page number", example = "1")
    @Min(value = 1, message = "page number can't less than 1")
    private Integer pageNo = DEFAULT_PAGE_NUM;

    @Schema(description = "page size", example = "6")
    @Min(value = 1, message = "page size can't less than 1")
    private Integer pageSize = DEFAULT_PAGE_SIZE;

    @Schema(description = "should be ascending order", example = "true")
    private Boolean isAsc = true;

    @Schema(description = "sort field", example = "Most Relevant")
    private String sortBy;

    public <T> Page<T> toMpPage(OrderItem... orderItems) {
        Page<T> page = new Page<>(pageNo, pageSize);

        if (orderItems != null && orderItems.length > 0) { //declare order field
            for (OrderItem orderItem : orderItems) {
                page.addOrder(orderItems);
            }
            return page;
        }

        if (StringUtils.isNotBlank(sortBy)) {
            OrderItem orderItem = new OrderItem();
            orderItem.setAsc(this.isAsc);
            orderItem.setColumn(sortBy);
            page.addOrder(orderItems);
        }
        return page;
    }

    public <T> Page<T> toMpPage(String sortBy, boolean isAsc) {
        if (StringUtils.isBlank(this.sortBy)) {
            this.sortBy = sortBy;
            this.isAsc = isAsc;
        }

        Page<T> page = new Page<>(pageNo, pageSize);
        OrderItem orderItem = new OrderItem();
        orderItem.setColumn(this.sortBy);
        orderItem.setAsc(this.isAsc);
        page.addOrder(orderItem);
        return page;
    }
}
