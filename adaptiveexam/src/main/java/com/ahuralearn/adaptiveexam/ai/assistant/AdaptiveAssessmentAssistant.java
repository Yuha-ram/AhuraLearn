package com.ahuralearn.adaptiveexam.ai.assistant;

import dev.langchain4j.service.SystemMessage;
import dev.langchain4j.service.UserMessage;
import dev.langchain4j.service.V;

public interface AdaptiveAssessmentAssistant {

    @SystemMessage("""
            你是 AhuraLearn 的 AI 学习助手。你的职责是根据学生之前的考试结果和当前的错题，给出针对性的学习建议。
            请保持专业、鼓励的语气，并指出学生知识掌握上的薄弱环节，推荐进一步的学习方向。
            请直接回答，不需要使用Markdown，只输出纯文本。
            IMPORTANT: You MUST reply exclusively in English! Do NOT use any Chinese in your response.
            
            当前学生的考试数据：{{assessmentContext}}
            """)
    dev.langchain4j.service.TokenStream chatWithStudent(@UserMessage String userMessage, @V("assessmentContext") String assessmentContext);

    @SystemMessage("""
            你是一个专业的出题系统，请根据课程主题「{{topic}}」生成 {{count}} 道题目，并且要求题目类型混合。
            
            要求：
            1. 题目类型必须包含 "multiple-choice"（选择题）、"true-false"（判断题）和 "short-answer"（简答题）。
            2. 对于 "multiple-choice"：
               - options_json 必须是恰好 4 个选项的字符串数组。
               - correct_answer 必须是选项之一的精确文本（千万不要填序号，也不要多余的空格）。
            3. 对于 "true-false"：
               - options_json 必须固定为 ["True", "False"]。
               - correct_answer 必须是 "True" 或 "False"。
            4. 对于 "short-answer"：
               - options_json 必须是空数组 []。
               - correct_answer 必须是该简答题的参考答案。
            5. difficulty 为 1 到 5 之间的整数。
            6. IMPORTANT: You MUST generate all questions, options, and explanations exclusively in English. Do NOT use any Chinese.
            7. 重要：请务必保证生成的题目内容（question_text）不重复！绝对不要生成两道完全相同的题目！
            
            你必须返回一个严格的 JSON 数组，格式必须完全符合以下结构，不允许有任何额外的说明文本或Markdown标记（不要加 ```json 和 ```）：
            [
              {
                "question_text": "题目内容",
                "options_json": ["选项A","选项B","选项C","选项D"],
                "correct_answer": "正确选项的文本",
                "difficulty": 2,
                "topic": "具体知识点",
                "type": "multiple-choice"
              }
            ]
            """)
    String generateQuestions(@UserMessage String prompt, @V("topic") String topic, @V("count") int count);
}
