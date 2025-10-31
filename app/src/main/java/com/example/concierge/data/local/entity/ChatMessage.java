package com.example.concierge.data.local.entity;

import androidx.room.Entity;
import androidx.room.PrimaryKey;
import androidx.room.ForeignKey;
import java.util.Date;

@Entity(tableName = "chat_message",
        foreignKeys = {
                @ForeignKey(entity = Guest.class,
                        parentColumns = "id_guest",
                        childColumns = "id_guest",
                        onDelete = ForeignKey.CASCADE),
                @ForeignKey(entity = Order.class,
                        parentColumns = "id_order",
                        childColumns = "related_order_id",
                        onDelete = ForeignKey.CASCADE)
        })
public class ChatMessage {
    @PrimaryKey(autoGenerate = true)
    public int id_message;

    public int id_guest;
    public String sender_type; // guest, bot, concierge
    public String message_text;
    public Date timestamp;
    public Integer related_order_id; // Может быть null

    public ChatMessage(int id_guest, String sender_type, String message_text,
                       Date timestamp, Integer related_order_id) {
        this.id_guest = id_guest;
        this.sender_type = sender_type;
        this.message_text = message_text;
        this.timestamp = timestamp;
        this.related_order_id = related_order_id;
    }
}
