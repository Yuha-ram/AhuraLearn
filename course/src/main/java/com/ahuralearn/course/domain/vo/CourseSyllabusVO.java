package com.ahuralearn.course.domain.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.List;

@Data
@Schema(description = "Course Syllabus")
@AllArgsConstructor
public class CourseSyllabusVO {
    private List<ChapterVO> chapters;
}
