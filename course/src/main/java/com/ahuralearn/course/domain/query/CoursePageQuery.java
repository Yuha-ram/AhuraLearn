package com.ahuralearn.course.domain.query;

import com.ahuralearn.common.domain.query.PageQuery;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;

@EqualsAndHashCode(callSuper = true)
@Data
@Schema(description = "Course Search and Pagination Request")
public class CoursePageQuery extends PageQuery {

    @Schema(description = "search keyword", example = "Java")
    private String keyword;

    @Schema(description = "course rating" ,example = "4.5")
    private Double minRating;

    @Schema(description = "course difficulty" ,example = "intermediate")
    private String difficulty;
}
