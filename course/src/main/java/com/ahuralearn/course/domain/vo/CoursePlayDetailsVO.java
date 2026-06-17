package com.ahuralearn.course.domain.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.util.List;

@Data
@Schema(description = "Video Play specific Info")
public class CoursePlayDetailsVO {
    // chapters/sections info
    private List<ChapterVO> chapters;

    // instructor info
    private InstructorVO instructor;

    // current sec basic info
    private SectionBasicVO currentSection;
}
