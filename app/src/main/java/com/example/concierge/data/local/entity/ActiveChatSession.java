package com.example.concierge.data.local.entity;

import androidx.room.Entity;
import androidx.room.PrimaryKey;
import androidx.room.ForeignKey;
import java.util.Date;

@Entity(tableName = "active_chat_sessions",
        foreignKeys = {
                @ForeignKey(entity = Guest.class,
                        parentColumns = "id_guest",
                        childColumns = "id_guest",
                        onDelete = ForeignKey.CASCADE),
                @ForeignKey(entity = ConciergeStaff.class,
                        parentColumns = "id_staff",
                        childColumns = "id_staff",
                        onDelete = ForeignKey.CASCADE)
        })
public class ActiveChatSession {
    @PrimaryKey(autoGenerate = true)
    public int id_chat_sessions;

    public int id_guest;
    public Integer id_staff; // Может быть null (ожидание консьержа)
    public String status; // awaiting, assigned, resolved
    public Date created_at;
    public Date resolved_at; // Может быть null

    public ActiveChatSession(int id_guest, Integer id_staff, String status,
                             Date created_at, Date resolved_at) {
        this.id_guest = id_guest;
        this.id_staff = id_staff;
        this.status = status;
        this.created_at = created_at;
        this.resolved_at = resolved_at;
    }
}
