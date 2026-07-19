package com.ahuralearn.learning.domain.po;

import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

@Data
@TableName("assessment_record")
public class AssessmentRecordPO {

    private Long id;

    private Long userId;

    private Long courseId;

    private Integer accuracy;

    private Integer totalQuestions;

    private Integer correctCount;

    private String wrongQuestionContent;
}
