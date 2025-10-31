package com.example.concierge.data.local.entity;

import androidx.room.Entity;
import androidx.room.PrimaryKey;
import androidx.room.ForeignKey;
import java.util.Date;

@Entity(tableName = "push_notifications",
        foreignKeys = @ForeignKey(entity = Guest.class,
                parentColumns = "id_guest",
                childColumns = "id_guest",
                onDelete = ForeignKey.CASCADE))
public class PushNotification {
    @PrimaryKey(autoGenerate = true)
    public int id_notification;

    public int id_guest;
    public String title;
    public String message;
    public String type_notif; // order_status, promotion, hotel_event
    public Date sent_at;
    public boolean is_read;

    public PushNotification(int id_guest, String title, String message,
                            String type_notif, Date sent_at, boolean is_read) {
        this.id_guest = id_guest;
        this.title = title;
        this.message = message;
        this.type_notif = type_notif;
        this.sent_at = sent_at;
        this.is_read = is_read;
    }
}
