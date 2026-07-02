package com.ahuralearn.ai.config;

import com.ahuralearn.common.utils.StringUtils;
import dev.langchain4j.model.chat.ChatModel;
import dev.langchain4j.rag.query.Query;
import dev.langchain4j.rag.query.transformer.QueryTransformer;
import com.ahuralearn.common.exceptions.BusinessException;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.Collection;
import java.util.List;

@Component
@Slf4j
public class CourseIntentQueryTransformer implements QueryTransformer {

    public static final String OUT_OF_SCOPE_REPLY =
            "Sorry, I can only help with course recommendations and learning-related questions.";

    @Resource
    private ChatModel qwenChatModel;

    // translate the user words to professional terms
    @Override
    public Collection<Query> transform(Query query) {
        String prompt = "You are a course intent identifier. " +
                "If the user is just greeting or making unrelated small talk (e.g., 'hello', 'who are you', 'tell me a joke'), output exactly [BLOCK]. " +
                "If the user is looking for courses, extract the core technical keywords separated by spaces (e.g., 'Frontend Web HTML Vue'). " +
                "If the user is asking a follow-up question about a previously discussed course (e.g., 'what does it teach?', 'how long is it?'), output exactly [FOLLOW_UP]. " +
                "Output ONLY the keywords or the exact tag, no prefix, no explanation, no other text.\n" +
                "User input: " + query.text();

        String result = qwenChatModel.chat(prompt);

        if (StringUtils.isNotBlank(result) && result.contains("[BLOCK]")) {
            throw new BusinessException(OUT_OF_SCOPE_REPLY);
        }

        if (StringUtils.isNotBlank(result) && result.contains("[FOLLOW_UP]")) {
            log.info("用户正在追问，跳过检索");
            return java.util.Collections.emptyList();
        }

        log.info("转换后的用户提问: {}", result);
        return List.of(Query.from(result));
    }
}
