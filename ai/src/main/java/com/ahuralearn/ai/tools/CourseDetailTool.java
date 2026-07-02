package com.ahuralearn.ai.tools;

import com.ahuralearn.common.utils.StringUtils;
import com.ahuralearn.course.domain.vo.ChapterVO;
import com.ahuralearn.course.domain.vo.CourseFullInfoVO;
import com.ahuralearn.course.domain.vo.CourseSyllabusVO;
import com.ahuralearn.course.domain.vo.InstructorVO;
import com.ahuralearn.course.domain.vo.SectionVO;
import com.ahuralearn.course.service.ICourseService;
import dev.langchain4j.agent.tool.Tool;
import dev.langchain4j.agent.tool.ToolMemoryId;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.List;

@Slf4j
@Component
@RequiredArgsConstructor
public class CourseDetailTool {

    private final ICourseService courseService;

    /*
     * 【追问详情工具】
     * 这个工具专门解决“用户追问刚才推荐课程的详细内容”这一类问题。
     * 不走 SSE 卡片推送，不写前端历史，只把数据库里的权威课程详情返回给大模型，
     * 让模型基于真实课程信息回答，避免只靠 chat memory 猜测课程大纲。
     */
    @Tool("Get authoritative course details for follow-up questions about a previously recommended course. Use this when the user asks about course content, syllabus, difficulty, duration, instructor, outcomes, prerequisites, or learning path. You must provide the courseId from the previous recommendation context.")
    public String getCourseDetail(@ToolMemoryId Object memoryId, Long courseId) {
        log.info("getCourseDetail tool invoked with memoryId={}, courseId={}", memoryId, courseId);

        if (courseId == null) {
            return "Course detail lookup failed: courseId is missing. Ask the user which recommended course they mean.";
        }

        try {
            CourseFullInfoVO detail = courseService.getCourseDetail(courseId);
            CourseSyllabusVO syllabus = courseService.getSyllabus(courseId);
            return formatCourseDetail(detail, syllabus);
        } catch (Exception e) {
            log.warn("Failed to get course detail. courseId={}", courseId, e);
            return "Course detail lookup failed for courseId=" + courseId +
                    ". Tell the user that the course details are currently unavailable and do not invent course content.";
        }
    }

    private String formatCourseDetail(CourseFullInfoVO detail, CourseSyllabusVO syllabus) {
        StringBuilder sb = new StringBuilder();
        sb.append("[Authoritative Course Details]\n");
        appendLine(sb, "Course ID", detail.getId());
        appendLine(sb, "Course Name", detail.getName());
        appendLine(sb, "Subtitle", detail.getSubtitle());
        appendLine(sb, "Category", detail.getCategoryName());
        if (detail.getDifficultyLevel() != null) {
            appendLine(sb, "Difficulty Level", detail.getDifficultyLevel().getDesc());
        }
        appendLine(sb, "Estimated Hours", detail.getHoursRequired());
        appendLine(sb, "Rating", detail.getRating());
        appendLine(sb, "Review Count", detail.getReviewCount());
        appendLine(sb, "Enrolled Count", detail.getEnrolledCount());

        InstructorVO instructor = detail.getInstructor();
        if (instructor != null) {
            sb.append("\n[Instructor]\n");
            appendLine(sb, "Name", instructor.getName());
            appendLine(sb, "Bio", instructor.getBio());
            appendLine(sb, "Rating", instructor.getRating());
            appendLine(sb, "Student Count", instructor.getStudentCount());
        }

        appendSection(sb, "Description", detail.getDescription());
        appendSection(sb, "Learning Outcomes", normalizeOutcomes(detail.getOutcomes()));
        appendSyllabus(sb, syllabus);
        sb.append("\nAnswer the user's follow-up question using only the details above.");
        return sb.toString();
    }

    private void appendSyllabus(StringBuilder sb, CourseSyllabusVO syllabus) {
        List<ChapterVO> chapters = syllabus == null ? null : syllabus.getChapters();
        if (chapters == null || chapters.isEmpty()) {
            return;
        }

        sb.append("\n[Syllabus]\n");
        for (ChapterVO chapter : chapters) {
            if (chapter == null) {
                continue;
            }
            sb.append("- Chapter");
            if (chapter.getSortOrder() != null) {
                sb.append(" ").append(chapter.getSortOrder());
            }
            if (StringUtils.isNotBlank(chapter.getTitle())) {
                sb.append(": ").append(chapter.getTitle());
            }
            if (StringUtils.isNotBlank(chapter.getDescription())) {
                sb.append(" - ").append(chapter.getDescription());
            }
            sb.append("\n");

            List<SectionVO> sections = chapter.getSections();
            if (sections == null || sections.isEmpty()) {
                continue;
            }
            for (SectionVO section : sections) {
                if (section == null || StringUtils.isBlank(section.getTitle())) {
                    continue;
                }
                sb.append("  - Section");
                if (section.getSortOrder() != null) {
                    sb.append(" ").append(section.getSortOrder());
                }
                sb.append(": ").append(section.getTitle());
                if (StringUtils.isNotBlank(section.getDurationFormat())) {
                    sb.append(" (").append(section.getDurationFormat()).append(")");
                }
                sb.append("\n");
            }
        }
    }

    private void appendSection(StringBuilder sb, String title, String value) {
        if (StringUtils.isBlank(value)) {
            return;
        }
        sb.append("\n[").append(title).append("]\n").append(value).append("\n");
    }

    private void appendLine(StringBuilder sb, String label, Object value) {
        if (value == null) {
            return;
        }
        String text = value.toString();
        if (StringUtils.isBlank(text)) {
            return;
        }
        sb.append(label).append(": ").append(text).append("\n");
    }

    private String normalizeOutcomes(String outcomes) {
        if (StringUtils.isBlank(outcomes)) {
            return null;
        }
        return outcomes.replace("[", "")
                .replace("]", "")
                .replace("\"", "")
                .replace(",", "\n");
    }
}
