package com.ahuralearn.learning.controller;

import com.ahuralearn.common.domain.Result;
import com.ahuralearn.common.enums.ResultCode;
import com.ahuralearn.learning.domain.dto.NotificationQueryDTO;
import com.ahuralearn.learning.domain.vo.NotificationPageVO;
import com.ahuralearn.learning.domain.vo.NotificationVO;
import com.ahuralearn.learning.service.NotificationService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/notifications")
@Tag(name = "notificationController")
@RequiredArgsConstructor
public class NotificationController {
    private final NotificationService notificationService;

    @Operation(summary = "Get notifications")
    @GetMapping
    public Result<NotificationPageVO> getNotifications(NotificationQueryDTO queryDTO) {
        return Result.success(notificationService.getNotificationPage(queryDTO));
    }

    @Operation(summary = "Acknowledge a notification")
    @PatchMapping("/{id}/acknowledge")
    public Result<NotificationVO> acknowledgeNotification(@PathVariable("id") Long id) {
        NotificationVO notification = notificationService.acknowledgeNotification(id);
        if (notification == null) {
            return Result.error(ResultCode.NOT_FOUND);
        }
        return Result.success(notification);
    }

    @Operation(summary = "Delete a notification")
    @DeleteMapping("/{id}")
    public Result<Void> deleteNotification(@PathVariable("id") Long id) {
        boolean deleted = notificationService.deleteNotification(id);
        if (!deleted) {
            return Result.error(ResultCode.NOT_FOUND);
        }
        return Result.success();
    }
}
