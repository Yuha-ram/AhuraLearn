package com.ahuralearn.learning.service.impl;

import com.ahuralearn.course.domain.po.Category;
import com.ahuralearn.course.domain.po.Course;
import com.ahuralearn.course.mapper.CategoryMapper;
import com.ahuralearn.course.mapper.CourseMapper;
import com.ahuralearn.learning.domain.po.AssessmentRecordPO;
import com.ahuralearn.learning.domain.vo.MyExamVO;
import com.ahuralearn.learning.mapper.AssessmentRecordMapper;
import com.ahuralearn.learning.service.ExamService;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Collectors;

@Service
public class ExamServiceImpl implements ExamService {

    private static final int PASS_SCORE = 80;
    private static final int TOTAL_SCORE = 100;
    private static final ObjectMapper OBJECT_MAPPER = new ObjectMapper();
    private static final TypeReference<List<Map<String, Object>>> WRONG_QUESTION_TYPE = new TypeReference<>() {
    };

    private final AssessmentRecordMapper assessmentRecordMapper;
    private final CourseMapper courseMapper;
    private final CategoryMapper categoryMapper;

    public ExamServiceImpl(AssessmentRecordMapper assessmentRecordMapper,
                           CourseMapper courseMapper,
                           CategoryMapper categoryMapper) {
        this.assessmentRecordMapper = assessmentRecordMapper;
        this.courseMapper = courseMapper;
        this.categoryMapper = categoryMapper;
    }

    @Override
    public MyExamVO getMyExam(Long userId) {
        List<AssessmentRecordPO> records = selectAssessmentRecords(userId);
        Map<Long, AssessmentRecordPO> bestRecordByCourse = selectBestRecordByCourse(records);
        Map<Long, Course> courseMap = selectCourseMap(bestRecordByCourse.keySet());
        Map<Long, Category> categoryMap = selectCategoryMap(courseMap.values());

        MyExamVO vo = new MyExamVO();

        vo.setResult(buildBestResult(bestRecordByCourse, courseMap));

        vo.setSubjects(buildSubjects(bestRecordByCourse));
        vo.setRecentExams(buildRecentExams(bestRecordByCourse, courseMap, categoryMap));

        return vo;
    }

    private List<AssessmentRecordPO> selectAssessmentRecords(Long userId) {
        LambdaQueryWrapper<AssessmentRecordPO> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(AssessmentRecordPO::getUserId, userId)
                .isNotNull(AssessmentRecordPO::getCourseId);

        return assessmentRecordMapper.selectList(wrapper);
    }

    private Map<Long, AssessmentRecordPO> selectBestRecordByCourse(List<AssessmentRecordPO> records) {
        return records.stream()
                .filter(item -> item.getCourseId() != null)
                .collect(Collectors.toMap(
                        AssessmentRecordPO::getCourseId,
                        Function.identity(),
                        (left, right) -> scoreOf(left) >= scoreOf(right) ? left : right
                ));
    }

    private Map<Long, Course> selectCourseMap(Set<Long> courseIds) {
        if (courseIds.isEmpty()) {
            return Map.of();
        }
        return courseMapper.selectBatchIds(courseIds).stream()
                .collect(Collectors.toMap(Course::getId, Function.identity()));
    }

    private Map<Long, Category> selectCategoryMap(Iterable<Course> courses) {
        Set<Long> categoryIds = new java.util.HashSet<>();
        for (Course course : courses) {
            if (course.getCategoryId() != null) {
                categoryIds.add(course.getCategoryId());
            }
        }
        if (categoryIds.isEmpty()) {
            return Map.of();
        }
        return categoryMapper.selectBatchIds(categoryIds).stream()
                .collect(Collectors.toMap(Category::getId, Function.identity()));
    }

    private MyExamVO.ResultVO buildBestResult(Map<Long, AssessmentRecordPO> recordMap,
                                              Map<Long, Course> courseMap) {
        return recordMap.values().stream()
                .filter(item -> courseMap.containsKey(item.getCourseId()))
                .max(Comparator.comparingInt(this::scoreOf)
                        .thenComparing(AssessmentRecordPO::getCourseId))
                .map(item -> {
                    Course course = courseMap.get(item.getCourseId());
                    MyExamVO.ResultVO resultVO = new MyExamVO.ResultVO();
                    resultVO.setStatus(statusOf(item).toUpperCase());
                    resultVO.setTitle(course.getName());
                    resultVO.setDescription(
                            "Congratulations! You've successfully demonstrated proficiency in this exam."
                    );
                    resultVO.setScore(scoreOf(item));
                    resultVO.setTotalScore(TOTAL_SCORE);
                    return resultVO;
                })
                .orElse(null);
    }

    private List<MyExamVO.SubjectVO> buildSubjects(Map<Long, AssessmentRecordPO> recordMap) {
        Map<Long, MyExamVO.SubjectVO> subjectMap = new HashMap<>();

        for (AssessmentRecordPO record : recordMap.values()) {
            extractWrongQuestionTopics(record).forEach(topic -> {
                Long id = Integer.toUnsignedLong(topic.hashCode());
                MyExamVO.SubjectVO subject = subjectMap.computeIfAbsent(id, key -> {
                    MyExamVO.SubjectVO vo = new MyExamVO.SubjectVO();
                    vo.setId(key);
                    vo.setName(topic);
                    vo.setScore(0);
                    return vo;
                });
                subject.setScore(Math.max(subject.getScore(), scoreOf(record)));
            });
        }

        return subjectMap.values().stream()
                .sorted(Comparator.comparing(MyExamVO.SubjectVO::getName))
                .toList();
    }

    private Set<String> extractWrongQuestionTopics(AssessmentRecordPO record) {
        if (record.getWrongQuestionContent() == null || record.getWrongQuestionContent().isBlank()) {
            return Set.of();
        }

        try {
            List<Map<String, Object>> wrongQuestions = OBJECT_MAPPER.readValue(
                    record.getWrongQuestionContent(),
                    WRONG_QUESTION_TYPE
            );

            Set<String> topics = new LinkedHashSet<>();
            for (Map<String, Object> wrongQuestion : wrongQuestions) {
                Object topic = wrongQuestion.get("topic");
                if (topic instanceof String topicName && !topicName.isBlank()) {
                    topics.add(topicName);
                }
            }
            return topics;
        } catch (JsonProcessingException e) {
            return Set.of();
        }
    }

    private List<MyExamVO.RecentExamVO> buildRecentExams(Map<Long, AssessmentRecordPO> recordMap,
                                                         Map<Long, Course> courseMap,
                                                         Map<Long, Category> categoryMap) {
        List<MyExamVO.RecentExamVO> recentExams = new ArrayList<>();
        recordMap.values().stream()
                .filter(item -> courseMap.containsKey(item.getCourseId()))
                .sorted(Comparator.comparing(AssessmentRecordPO::getCourseId, Comparator.reverseOrder()))
                .forEach(item -> {
                    Course course = courseMap.get(item.getCourseId());
                    MyExamVO.RecentExamVO recentExamVO = new MyExamVO.RecentExamVO();
                    recentExamVO.setId(item.getId());
                    recentExamVO.setCourseName(course.getName());
                    recentExamVO.setScore(scoreOf(item));
                    recentExamVO.setStatus(statusOf(item));
                    recentExamVO.setIcon(iconOf(course, categoryMap));
                    recentExams.add(recentExamVO);
                });
        return recentExams;
    }

    private String iconOf(Course course, Map<Long, Category> categoryMap) {
        Category category = course.getCategoryId() == null ? null : categoryMap.get(course.getCategoryId());
        return category == null ? null : category.getIcon();
    }

    private String statusOf(AssessmentRecordPO record) {
        return scoreOf(record) >= PASS_SCORE ? "passed" : "failed";
    }

    private int scoreOf(AssessmentRecordPO record) {
        return Objects.requireNonNullElse(record.getAccuracy(), 0);
    }
}
