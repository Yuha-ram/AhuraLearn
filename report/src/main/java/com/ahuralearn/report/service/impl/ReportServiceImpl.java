package com.ahuralearn.report.service.impl;

import com.ahuralearn.adaptiveexam.domain.po.AssessmentRecord;
import com.ahuralearn.adaptiveexam.mapper.AssessmentMapper;
import com.ahuralearn.common.utils.UserContext;
import com.ahuralearn.report.domain.po.ReportCoursePO;
import com.ahuralearn.report.domain.vo.ReportCourseVO;
import com.ahuralearn.report.domain.vo.ReportVO;
import com.ahuralearn.report.mapper.ReportCourseMapper;
import com.ahuralearn.report.service.ReportService;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class ReportServiceImpl implements ReportService {

    private static final int ASSESSMENT_COMPLETED_STATUS = 2;
    private static final int COURSE_ACTIVE_STATUS = 1;

    private final ReportCourseMapper reportCourseMapper;
    private final ObjectMapper objectMapper;
    private final AssessmentMapper assessmentMapper;

    public ReportServiceImpl(
            ReportCourseMapper reportCourseMapper,
            ObjectMapper objectMapper,
            AssessmentMapper assessmentMapper) {

        this.reportCourseMapper = reportCourseMapper;
        this.objectMapper = objectMapper;
        this.assessmentMapper = assessmentMapper;
    }

    /**
     * 获取指定课程的最新已完成考试报告。
     */
    @Override
    public ReportVO getReport(Long courseId) {

        Long userId = getCurrentUserId();

        if (courseId == null) {
            throw new IllegalArgumentException(
                    "courseId is required"
            );
        }

        /*
         * 查询条件：
         *
         * 1. 当前登录用户
         * 2. 当前课程
         * 3. status = 2，考试已经完成
         * 4. 按创建时间倒序，取最新一条
         */
        LambdaQueryWrapper<AssessmentRecord> assessmentWrapper =
                new LambdaQueryWrapper<>();

        assessmentWrapper
                .eq(
                        AssessmentRecord::getUserId,
                        userId
                )
                .eq(
                        AssessmentRecord::getCourseId,
                        courseId
                )
                .eq(
                        AssessmentRecord::getStatus,
                        ASSESSMENT_COMPLETED_STATUS
                )
                .orderByDesc(
                        AssessmentRecord::getCreateTime
                )
                .last("LIMIT 1");

        AssessmentRecord assessmentRecord =
                assessmentMapper.selectOne(
                        assessmentWrapper
                );

        System.out.println(
                "Report 当前查询用户 ID："
                        + userId
        );

        System.out.println(
                "Report 当前查询课程 ID："
                        + courseId
        );

        System.out.println(
                "Report 最新已完成考试记录："
                        + assessmentRecord
        );

        ReportVO vo = new ReportVO();

        String wrongQuestionContent =
                assessmentRecord == null
                        ? null
                        : assessmentRecord
                        .getWrongQuestionContent();

        /*
         * 1. Proficiency
         */
        setProficiency(
                vo,
                assessmentRecord
        );

        /*
         * 2. Error Distribution
         */
        vo.setErrors(
                buildErrorDistribution(
                        wrongQuestionContent
                )
        );

        /*
         * 3. Knowledge Gap
         */
        vo.setKnowledgeGap(
                buildKnowledgeGap(
                        wrongQuestionContent
                )
        );

        /*
         * 4. AI Suggestion
         */
        vo.setAiSuggestion(
                buildAiSuggestion(
                        assessmentRecord,
                        wrongQuestionContent
                )
        );

        return vo;
    }

    /**
     * 获取当前用户拥有已完成考试记录的课程。
     *
     * 只有 assessment_record 中满足以下条件的课程才会显示：
     *
     * user_id = 当前用户
     * status = 2
     * course_id 不为空
     */
    @Override
    public List<ReportCourseVO> getReportCourses() {

        Long userId = getCurrentUserId();

        LambdaQueryWrapper<AssessmentRecord> assessmentWrapper =
                new LambdaQueryWrapper<>();

        assessmentWrapper
                .eq(
                        AssessmentRecord::getUserId,
                        userId
                )
                .eq(
                        AssessmentRecord::getStatus,
                        ASSESSMENT_COMPLETED_STATUS
                )
                .isNotNull(
                        AssessmentRecord::getCourseId
                )
                .orderByDesc(
                        AssessmentRecord::getCreateTime
                );

        List<AssessmentRecord> completedRecords =
                assessmentMapper.selectList(
                        assessmentWrapper
                );

        System.out.println(
                "Report 当前登录用户 ID："
                        + userId
        );

        System.out.println(
                "Report 已完成考试记录："
                        + completedRecords
        );

        List<Long> courseIds =
                completedRecords.stream()
                        .map(
                                AssessmentRecord::getCourseId
                        )
                        .filter(id -> id != null)
                        .distinct()
                        .toList();

        System.out.println(
                "Report 已完成考试课程 ID："
                        + courseIds
        );

        if (courseIds.isEmpty()) {
            return Collections.emptyList();
        }

        /*
         * ReportCoursePO 已映射到正式 course 表。
         *
         * 根据考试记录中的 course_id 查询课程名称。
         */
        LambdaQueryWrapper<ReportCoursePO> courseWrapper =
                new LambdaQueryWrapper<>();

        courseWrapper
                .in(
                        ReportCoursePO::getId,
                        courseIds
                )
                .eq(
                        ReportCoursePO::getStatus,
                        COURSE_ACTIVE_STATUS
                )
                .orderByAsc(
                        ReportCoursePO::getId
                );

        List<ReportCoursePO> courseList =
                reportCourseMapper.selectList(
                        courseWrapper
                );

        System.out.println(
                "Report 最终课程列表："
                        + courseList
        );

        return courseList.stream()
                .map(item ->
                        new ReportCourseVO(
                                item.getId(),
                                item.getName()
                        )
                )
                .toList();
    }

    /**
     * 获取当前登录用户 ID。
     */
    private Long getCurrentUserId() {

        Long userId = UserContext.getUser();

        if (userId == null) {
            throw new RuntimeException(
                    "User is not logged in"
            );
        }

        return userId;
    }

    /**
     * 根据最新考试分数生成熟练度信息。
     */
    private void setProficiency(
            ReportVO vo,
            AssessmentRecord assessmentRecord) {

        if (assessmentRecord == null
                || assessmentRecord.getScore() == null) {

            vo.setProficiency(
                    new ReportVO.ProficiencyVO(
                            0,
                            "No Record",
                            "No completed assessment record was found for this course.",
                            "Complete an assessment to generate your report."
                    )
            );

            return;
        }

        Integer score =
                assessmentRecord.getScore();

        String level;
        String description;
        String message;

        if (score >= 80) {
            level = "Advanced";

            description =
                    "You have demonstrated strong proficiency in this course.";

            message =
                    "Great progress!";

        } else if (score >= 60) {
            level = "Intermediate";

            description =
                    "You have a good foundation, but some areas still need improvement.";

            message =
                    "Keep practicing!";

        } else {
            level = "Beginner";

            description =
                    "You need more practice to strengthen your understanding.";

            message =
                    "Review the course materials and try again.";
        }

        vo.setProficiency(
                new ReportVO.ProficiencyVO(
                        score,
                        level,
                        description,
                        message
                )
        );
    }

    /**
     * 根据错题 topic 生成错误分布。
     */
    private List<Map<String, Object>>
    buildErrorDistribution(
            String wrongQuestionContent) {

        List<Map<String, Object>> wrongQuestions =
                parseWrongQuestions(
                        wrongQuestionContent
                );

        if (wrongQuestions.isEmpty()) {
            return Collections.emptyList();
        }

        Map<String, Long> errorCountByTopic =
                countErrorsByTopic(
                        wrongQuestions
                );

        if (errorCountByTopic.isEmpty()) {
            return Collections.emptyList();
        }

        long totalErrors =
                errorCountByTopic.values()
                        .stream()
                        .mapToLong(
                                Long::longValue
                        )
                        .sum();

        return errorCountByTopic.entrySet()
                .stream()
                .map(entry -> {
                    Map<String, Object> item =
                            new LinkedHashMap<>();

                    item.put(
                            "type",
                            entry.getKey()
                    );

                    item.put(
                            "value",
                            calculatePercentage(
                                    entry.getValue(),
                                    totalErrors
                            )
                    );

                    return item;
                })
                .toList();
    }

    /**
     * 根据错题 topic 生成知识薄弱点。
     */
    private List<Map<String, Object>>
    buildKnowledgeGap(
            String wrongQuestionContent) {

        List<Map<String, Object>> wrongQuestions =
                parseWrongQuestions(
                        wrongQuestionContent
                );

        if (wrongQuestions.isEmpty()) {
            return Collections.emptyList();
        }

        Map<String, Long> errorCountByTopic =
                countErrorsByTopic(
                        wrongQuestions
                );

        if (errorCountByTopic.isEmpty()) {
            return Collections.emptyList();
        }

        long totalErrors =
                errorCountByTopic.values()
                        .stream()
                        .mapToLong(
                                Long::longValue
                        )
                        .sum();

        return errorCountByTopic.entrySet()
                .stream()
                .map(entry -> {
                    Map<String, Object> item =
                            new LinkedHashMap<>();

                    item.put(
                            "label",
                            entry.getKey()
                    );

                    item.put(
                            "value",
                            calculatePercentage(
                                    entry.getValue(),
                                    totalErrors
                            )
                    );

                    return item;
                })
                .toList();
    }

    /**
     * 按 topic 统计错题数量。
     */
    private Map<String, Long> countErrorsByTopic(
            List<Map<String, Object>> wrongQuestions) {

        return wrongQuestions.stream()
                .filter(item -> item != null)
                .collect(
                        Collectors.groupingBy(
                                item ->
                                        normalizeTopic(
                                                item.get(
                                                        "topic"
                                                )
                                        ),
                                LinkedHashMap::new,
                                Collectors.counting()
                        )
                );
    }

    /**
     * 计算百分比。
     */
    private int calculatePercentage(
            long value,
            long total) {

        if (total <= 0) {
            return 0;
        }

        return (int) Math.round(
                value * 100.0 / total
        );
    }

    /**
     * 根据分数和最薄弱知识点动态生成学习建议。
     */
    private ReportVO.AiSuggestionVO buildAiSuggestion(
            AssessmentRecord assessmentRecord,
            String wrongQuestionContent) {

        if (assessmentRecord == null
                || assessmentRecord.getScore() == null) {

            return new ReportVO.AiSuggestionVO(
                    "AI Personalized Suggestions",
                    "No Assessment Record",
                    "Complete an Assessment",
                    "Complete an assessment first so that personalized learning suggestions can be generated.",
                    "Check Learning Plan"
            );
        }

        Integer score =
                assessmentRecord.getScore();

        String weakestTopic =
                getWeakestTopic(
                        wrongQuestionContent
                );

        String keyword;
        String suggestionText;

        if (score < 40) {
            keyword =
                    "Foundation Improvement";

            suggestionText =
                    "Focus on the fundamental concepts of "
                            + weakestTopic
                            + ". Review the lesson materials, study worked examples, "
                            + "and complete basic practice questions before attempting "
                            + "another assessment.";

        } else if (score < 60) {
            keyword =
                    "Concept Reinforcement";

            suggestionText =
                    "Strengthen your understanding of "
                            + weakestTopic
                            + " through targeted practice. Review the mistakes from "
                            + "your latest assessment and revisit the related course sections.";

        } else if (score < 80) {
            keyword =
                    "Targeted Practice";

            suggestionText =
                    "Your overall foundation is good. Continue practicing "
                            + weakestTopic
                            + " and attempt more challenging questions to improve "
                            + "your accuracy and confidence.";

        } else {
            keyword =
                    "Advanced Practice";

            suggestionText =
                    "You have demonstrated strong proficiency. Continue with "
                            + "advanced exercises in "
                            + weakestTopic
                            + " and review occasional mistakes to maintain "
                            + "your performance.";
        }

        return new ReportVO.AiSuggestionVO(
                "AI Personalized Suggestions",
                keyword,
                weakestTopic,
                suggestionText,
                "Check Learning Plan"
        );
    }

    /**
     * 查找错题数量最多的知识点。
     *
     * 数量相同时保留最先出现的 topic。
     */
    private String getWeakestTopic(
            String wrongQuestionContent) {

        List<Map<String, Object>> wrongQuestions =
                parseWrongQuestions(
                        wrongQuestionContent
                );

        if (wrongQuestions.isEmpty()) {
            return "General Course Knowledge";
        }

        Map<String, Long> topicCount =
                countErrorsByTopic(
                        wrongQuestions
                );

        if (topicCount.isEmpty()) {
            return "General Course Knowledge";
        }

        String weakestTopic =
                "General Course Knowledge";

        long highestErrorCount = 0;

        for (Map.Entry<String, Long> entry
                : topicCount.entrySet()) {

            if (entry.getValue()
                    > highestErrorCount) {

                highestErrorCount =
                        entry.getValue();

                weakestTopic =
                        entry.getKey();
            }
        }

        return weakestTopic;
    }

    /**
     * 统一解析 wrong_question_content。
     */
    private List<Map<String, Object>>
    parseWrongQuestions(
            String wrongQuestionContent) {

        if (wrongQuestionContent == null
                || wrongQuestionContent.isBlank()) {

            return Collections.emptyList();
        }

        try {
            List<Map<String, Object>> wrongQuestions =
                    objectMapper.readValue(
                            wrongQuestionContent,
                            new TypeReference<
                                    List<Map<String, Object>>
                                    >() {
                            }
                    );

            if (wrongQuestions == null) {
                return Collections.emptyList();
            }

            return wrongQuestions;

        } catch (Exception e) {
            System.out.println(
                    "解析 wrong_question_content 失败："
                            + e.getMessage()
            );

            return Collections.emptyList();
        }
    }

    /**
     * 处理 topic 为空的情况。
     */
    private String normalizeTopic(
            Object topicValue) {

        if (topicValue == null) {
            return "General";
        }

        String topic =
                topicValue.toString().trim();

        if (topic.isBlank()) {
            return "General";
        }

        return topic;
    }
}