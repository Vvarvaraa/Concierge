package com.example.concierge.data.local.entity;

import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity(tableName = "concierge_staff")
public class ConciergeStaff {
    @PrimaryKey(autoGenerate = true)
    public int id_staff;

    public String name;
    public String email;
    public String password_hash;
    public boolean is_available;
    public Integer id_current_chat; // Может быть null

    public ConciergeStaff(String name, String email, String password_hash,
                          boolean is_available, Integer id_current_chat) {
        this.name = name;
        this.email = email;
        this.password_hash = password_hash;
        this.is_available = is_available;
        this.id_current_chat = id_current_chat;
    }
}
