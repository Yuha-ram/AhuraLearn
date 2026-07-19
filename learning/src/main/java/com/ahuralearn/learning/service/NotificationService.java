package com.ahuralearn.learning.service;

import com.ahuralearn.learning.domain.dto.NotificationQueryDTO;
import com.ahuralearn.learning.domain.po.NotificationPO;
import com.ahuralearn.learning.domain.vo.NotificationPageVO;
import com.ahuralearn.learning.domain.vo.NotificationVO;
import com.baomidou.mybatisplus.extension.service.IService;

public interface NotificationService extends IService<NotificationPO> {
    NotificationPageVO getNotificationPage(NotificationQueryDTO queryDTO);

    NotificationVO acknowledgeNotification(Long id);

    boolean deleteNotification(Long id);
}
