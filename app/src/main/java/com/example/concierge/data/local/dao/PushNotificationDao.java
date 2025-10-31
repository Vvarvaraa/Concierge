package com.example.concierge.data.local.dao;

import androidx.room.*;
import com.example.concierge.data.local.entity.PushNotification;
import java.util.List;

@Dao
public interface PushNotificationDao {
    @Insert
    long insert(PushNotification notification);

    @Update
    void update(PushNotification notification);

    @Delete
    void delete(PushNotification notification);

    @Query("SELECT * FROM push_notifications WHERE id_guest = :guestId ORDER BY sent_at DESC")
    List<PushNotification> getByGuest(int guestId);

    @Query("SELECT * FROM push_notifications WHERE is_read = 0 AND id_guest = :guestId")
    List<PushNotification> getUnreadByGuest(int guestId);

    @Query("UPDATE push_notifications SET is_read = 1 WHERE id_notification = :notificationId")
    void markAsRead(int notificationId);

    @Query("SELECT COUNT(*) FROM push_notifications WHERE is_read = 0 AND id_guest = :guestId")
    int getUnreadCount(int guestId);
}