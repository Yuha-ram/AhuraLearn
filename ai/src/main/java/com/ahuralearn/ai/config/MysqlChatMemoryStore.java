package com.ahuralearn.ai.config;

import com.ahuralearn.ai.domain.po.ChatMemoryMessage;
import com.ahuralearn.ai.mapper.ChatMemoryMessageMapper;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import dev.langchain4j.agent.tool.ToolExecutionRequest;
import dev.langchain4j.data.message.AiMessage;
import dev.langchain4j.data.message.ChatMessage;
import dev.langchain4j.data.message.SystemMessage;
import dev.langchain4j.data.message.ToolExecutionResultMessage;
import dev.langchain4j.data.message.UserMessage;
import dev.langchain4j.store.memory.chat.ChatMemoryStore;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Component
@RequiredArgsConstructor
public class MysqlChatMemoryStore implements ChatMemoryStore {

    private final ChatMemoryMessageMapper chatMemoryMessageMapper;
    private final ObjectMapper objectMapper;

    @Override
    public List<ChatMessage> getMessages(Object memoryId) {
        // 修改：memory 改为从独立表读取，避免与前端历史消息混用。
        Long sessionId = Long.valueOf(memoryId.toString());
        List<ChatMemoryMessage> records = chatMemoryMessageMapper.selectList(
                new LambdaQueryWrapper<ChatMemoryMessage>()
                        .eq(ChatMemoryMessage::getSessionId, sessionId)
                        .orderByAsc(ChatMemoryMessage::getSequence)
        );

        List<ChatMessage> messages = new ArrayList<>(records.size());
        for (ChatMemoryMessage record : records) {
            messages.add(toLangChainMessage(record));
        }
        return messages;
    }

    @Override
    public void updateMessages(Object memoryId, List<ChatMessage> messages) {
        // 修改：窗口型 memory 每次全量覆盖，保证顺序与 LangChain4j 视图一致。
        Long sessionId = Long.valueOf(memoryId.toString());
        deleteMessages(memoryId);

        int sequence = 0;
        for (ChatMessage message : messages) {
            ChatMemoryMessage record = new ChatMemoryMessage();
            record.setSessionId(sessionId);
            record.setSequence(sequence++);
            record.setMessageType(message.type().name());
            record.setContent(resolveContent(message));
            record.setAttributesJson(resolveAttributes(message));
            chatMemoryMessageMapper.insert(record);
        }
    }

    @Override
    public void deleteMessages(Object memoryId) {
        // 修改：清理会话 memory 时直接按 session 维度删除。
        Long sessionId = Long.valueOf(memoryId.toString());
        chatMemoryMessageMapper.delete(
                new LambdaQueryWrapper<ChatMemoryMessage>()
                        .eq(ChatMemoryMessage::getSessionId, sessionId)
        );
    }

    private ChatMessage toLangChainMessage(ChatMemoryMessage record) {
        try {
            switch (record.getMessageType()) {
                case "SYSTEM":
                    return SystemMessage.from(record.getContent());
                case "USER":
                    return UserMessage.from(record.getContent());
                case "AI":
                    return restoreAiMessage(record);
                case "TOOL_EXECUTION_RESULT":
                    return restoreToolExecutionResult(record);
                default:
                    throw new IllegalStateException("Unsupported chat memory message type: " + record.getMessageType());
            }
        } catch (Exception e) {
            throw new IllegalStateException("Failed to restore chat memory message", e);
        }
    }

    private ChatMessage restoreAiMessage(ChatMemoryMessage record) throws Exception {
        JsonNode root = readAttributes(record.getAttributesJson());
        List<ToolExecutionRequest> toolRequests = new ArrayList<>();
        if (root != null && root.has("toolExecutionRequests")) {
            JsonNode requestNodes = root.get("toolExecutionRequests");
            if (requestNodes.isArray()) {
                for (JsonNode requestNode : requestNodes) {
                    ToolExecutionRequest toolRequest = ToolExecutionRequest.builder()
                            .id(textValue(requestNode, "id"))
                            .name(textValue(requestNode, "name"))
                            .arguments(textValue(requestNode, "arguments"))
                            .build();
                    toolRequests.add(toolRequest);
                }
            }
        }
        if (toolRequests.isEmpty()) {
            return AiMessage.from(record.getContent());
        }
        return AiMessage.from(record.getContent(), toolRequests);
    }

    private ChatMessage restoreToolExecutionResult(ChatMemoryMessage record) throws Exception {
        JsonNode root = readAttributes(record.getAttributesJson());
        String toolExecutionId = root == null ? null : textValue(root, "id");
        String toolName = root == null ? null : textValue(root, "toolName");
        return ToolExecutionResultMessage.from(toolExecutionId, toolName, record.getContent());
    }

    private String resolveContent(ChatMessage message) {
        if (message instanceof SystemMessage systemMessage) {
            return systemMessage.text();
        }
        if (message instanceof UserMessage userMessage) {
            return userMessage.singleText();
        }
        if (message instanceof AiMessage aiMessage) {
            return aiMessage.text();
        }
        if (message instanceof ToolExecutionResultMessage toolResult) {
            return toolResult.text();
        }
        return null;
    }

    private String resolveAttributes(ChatMessage message) {
        try {
            if (message instanceof AiMessage aiMessage && aiMessage.hasToolExecutionRequests()) {
                Map<String, Object> payload = new HashMap<>();
                List<Map<String, String>> toolRequestPayloads = new ArrayList<>();
                for (ToolExecutionRequest request : aiMessage.toolExecutionRequests()) {
                    Map<String, String> requestPayload = new HashMap<>();
                    requestPayload.put("id", request.id());
                    requestPayload.put("name", request.name());
                    requestPayload.put("arguments", request.arguments());
                    toolRequestPayloads.add(requestPayload);
                }
                payload.put("toolExecutionRequests", toolRequestPayloads);
                return objectMapper.writeValueAsString(payload);
            }
            if (message instanceof ToolExecutionResultMessage toolResult) {
                Map<String, Object> payload = new HashMap<>();
                payload.put("id", toolResult.id());
                payload.put("toolName", toolResult.toolName());
                payload.put("isError", toolResult.isError());
                return objectMapper.writeValueAsString(payload);
            }
            return null;
        } catch (Exception e) {
            throw new IllegalStateException("Failed to serialize chat memory message attributes", e);
        }
    }

    private JsonNode readAttributes(String attributesJson) throws Exception {
        if (attributesJson == null || attributesJson.isBlank()) {
            return null;
        }
        return objectMapper.readTree(attributesJson);
    }

    private String textValue(JsonNode root, String fieldName) {
        JsonNode value = root.get(fieldName);
        return value == null || value.isNull() ? null : value.asText();
    }
}
