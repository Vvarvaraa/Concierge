package com.example.concierge.data.local.repository;

import android.content.Context;
import com.example.concierge.data.local.DatabaseClient;
import com.example.concierge.data.local.entity.PushNotification;
import java.util.List;

public class PushNotificationRepository {
    private final DatabaseClient databaseClient;

    public PushNotificationRepository(Context context) {
        this.databaseClient = DatabaseClient.getInstance(context);
    }

    public long insertNotification(PushNotification notification) {
        return databaseClient.getAppDatabase().pushNotificationDao().insert(notification);
    }

    public void updateNotification(PushNotification notification) {
        databaseClient.getAppDatabase().pushNotificationDao().update(notification);
    }

    public void deleteNotification(PushNotification notification) {
        databaseClient.getAppDatabase().pushNotificationDao().delete(notification);
    }

    public List<PushNotification> getNotificationsByGuest(int guestId) {
        return databaseClient.getAppDatabase().pushNotificationDao().getByGuest(guestId);
    }

    public List<PushNotification> getUnreadNotificationsByGuest(int guestId) {
        return databaseClient.getAppDatabase().pushNotificationDao().getUnreadByGuest(guestId);
    }

    public void markNotificationAsRead(int notificationId) {
        databaseClient.getAppDatabase().pushNotificationDao().markAsRead(notificationId);
    }

    public int getUnreadNotificationsCount(int guestId) {
        return databaseClient.getAppDatabase().pushNotificationDao().getUnreadCount(guestId);
    }

    // Бизнес-логика: создание уведомления о статусе заказа
    public long createOrderStatusNotification(int guestId, String orderTitle, String status) {
        String title = "Статус заказа";
        String message = "Ваш заказ \"" + orderTitle + "\" имеет статус: " + status;

        PushNotification notification = new PushNotification(guestId, title, message,
                "order_status", new java.util.Date(), false);
        return insertNotification(notification);
    }

    // Бизнес-логика: создание промо-уведомления
    public long createPromotionNotification(int guestId, String title, String message) {
        PushNotification notification = new PushNotification(guestId, title, message,
                "promotion", new java.util.Date(), false);
        return insertNotification(notification);
    }

    // Бизнес-логика: пометить все уведомления как прочитанные
    public void markAllNotificationsAsRead(int guestId) {
        List<PushNotification> unreadNotifications = getUnreadNotificationsByGuest(guestId);
        for (PushNotification notification : unreadNotifications) {
            markNotificationAsRead(notification.id_notification);
        }
    }
}
