package com.ahuralearn.ai.tools;

import com.ahuralearn.ai.domain.vo.CourseCardPayloadVO;
import com.ahuralearn.ai.sse.ChatStreamContext;
import com.ahuralearn.ai.sse.SseEventPublisher;
import com.ahuralearn.ai.utils.SseEmitterContext;
import com.ahuralearn.course.domain.po.Course;
import com.ahuralearn.course.service.ICourseService;
import dev.langchain4j.agent.tool.Tool;
import dev.langchain4j.agent.tool.ToolMemoryId;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class CourseRecommendationTool {

    private final ICourseService courseService;
    private final SseEventPublisher sseEventPublisher;

    @Tool("Recommend a course to the user by rendering a course card UI on their screen. You must provide the correct courseId.")
    public String recommendCourse(@ToolMemoryId Object memoryId, Long courseId) {
        // 修改：工具成功推送课程卡片后，会把卡片同时写入当前流上下文，供历史持久化复用。
        Long sessionId = Long.valueOf(memoryId.toString());
        log.info("============== TOOL CALLED ==============");
        log.info("recommendCourse tool invoked with sessionId={}, courseId={}", sessionId, courseId);

        Course course = courseService.getById(courseId);
        if (course == null) {
            return "The course ID is invalid. Please apologize to the user and do not continue with recommendation reasons.";
        }

        ChatStreamContext streamContext = SseEmitterContext.get(sessionId);
        if (streamContext == null) {
            throw new IllegalStateException("SSE stream context is missing for the current session.");
        }

        CourseCardPayloadVO payload = new CourseCardPayloadVO();
        payload.setId(course.getId());
        payload.setName(course.getName());
        payload.setCoverUrl(course.getCoverUrl());
        payload.setDifficultyLevel(course.getDifficultyLevel() == null ? null : course.getDifficultyLevel().getDesc());

        try {
            sseEventPublisher.publishCourseCard(streamContext.getEmitter(), payload);
            streamContext.addCourseCard(payload);
            log.info("Successfully pushed course card to SSE stream.");
        } catch (Exception e) {
            // 修改：课程卡片发送失败时直接抛错，避免模型误以为前端已成功渲染。
            log.error("Failed to push course card to SSE stream", e);
            throw new IllegalStateException("Failed to render course card on the client.", e);
        }

        /*
         * 【Memory 锚点】
         * 这段文本不会作为 SSE 文本直接展示给前端，而是作为 LangChain4j 的工具结果进入 chat_memory_message。
         * 后续用户说“这门课/刚才推荐的课”时，模型可以从最近 memory 中拿到 courseId，再调用 CourseDetailTool。
         */
        return "The course card has been rendered successfully. " +
                "Recommended course memory anchor: courseId=" + course.getId() +
                ", courseName=\"" + course.getName() + "\"" +
                ", difficultyLevel=\"" + (course.getDifficultyLevel() == null ? "unknown" : course.getDifficultyLevel().getDesc()) + "\". " +
                "Now provide recommendation reasons using a Markdown bullet list. " +
                "Each reason must follow the format `- **[Subtitle]**: [Explanation]`.";
    }
}
