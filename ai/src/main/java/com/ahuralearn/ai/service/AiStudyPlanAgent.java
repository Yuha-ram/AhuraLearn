package com.ahuralearn.ai.service;

import dev.langchain4j.service.SystemMessage;
import dev.langchain4j.service.TokenStream;
import dev.langchain4j.service.UserMessage;

public interface AiStudyPlanAgent {

    @SystemMessage(fromResource = "study-plan-system-prompt.txt")
    String generate(@UserMessage String userMessage);

    @SystemMessage(fromResource = "learning-assistant-system-prompt.txt")
    String chat(@UserMessage String userMessage);

    @SystemMessage(fromResource = "study-plan-form-system-prompt.txt")
    String generatePlanForm(@UserMessage String userMessage);

    @SystemMessage(fromResource = "study-plan-system-prompt.txt")
    TokenStream generateStream(@UserMessage String userMessage);

    @SystemMessage(fromResource = "learning-assistant-system-prompt.txt")
    TokenStream chatStream(@UserMessage String userMessage);
}
