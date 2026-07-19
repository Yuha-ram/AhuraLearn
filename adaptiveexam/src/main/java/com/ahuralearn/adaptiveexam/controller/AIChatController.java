package com.ahuralearn.adaptiveexam.controller;

import com.ahuralearn.adaptiveexam.ai.service.AIChatService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.Map;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import jakarta.servlet.http.HttpServletResponse;

import dev.langchain4j.service.TokenStream;

@RestController
@RequestMapping("/api/ai")
public class AIChatController {

    @Autowired
    private AIChatService aiChatService;

    /**
     * 发送聊天消息给 AI 学习助手 (流式返回)
     */
    @PostMapping("/chat")
    public void chat(@RequestBody Map<String, String> request, HttpServletResponse response) {
        String message = request.get("message");
        String recordId = request.get("recordId");
        
        Long currentUserId = 1L; 
        
        response.setContentType("text/plain;charset=UTF-8");
        
        // 调用 Service 获取 TokenStream
        TokenStream stream = aiChatService.chat(currentUserId, message, recordId);
        
        // We use a latch or just block until stream finishes, because the Servlet thread will exit otherwise.
        // Wait, TokenStream in langchain4j is asynchronous! If the method returns, the response is committed!
        // So we MUST block the Servlet thread, or use AsyncContext.
        // Langchain4j's TokenStream does NOT run on the caller thread if using DashScope (it uses OkHttp async).
        
        java.util.concurrent.CountDownLatch latch = new java.util.concurrent.CountDownLatch(1);
        
        stream.onPartialResponse(token -> {
            try {
                response.getOutputStream().write(token.getBytes(StandardCharsets.UTF_8));
                response.getOutputStream().flush();
            } catch (IOException e) {
                // connection closed by client
            }
        })
        .onCompleteResponse(res -> latch.countDown())
        .onError(err -> latch.countDown())
        .start();
        
        try {
            latch.await(); // wait for the stream to complete before returning
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }
}
