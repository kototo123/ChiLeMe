package com.chileme.service;

import com.chileme.entity.Notification;
import com.baomidou.mybatisplus.core.metadata.IPage;

public interface NotificationService {
    void send(Notification notification);
    IPage<Notification> getNotifications(Long userId, int page, int pageSize);
    int getUnreadCount(Long userId);
    void markAllRead(Long userId);
}
