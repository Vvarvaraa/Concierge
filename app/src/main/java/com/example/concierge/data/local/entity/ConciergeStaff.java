package com.example.concierge.data.local.entity;

import androidx.room.Entity;
import androidx.room.PrimaryKey;
import java.io.Serializable;

@Entity(tableName = "concierge_staff")
public class ConciergeStaff implements Serializable {

    @PrimaryKey(autoGenerate = true)
    public int id_staff;

    public String first_name;
    public String last_name;
    public String password_hash;
    public String email;
    public boolean is_available;
    public Integer current_chat_session_id;

    public ConciergeStaff() {}

    public ConciergeStaff(String first_name, String last_name, String password_hash,
                          String email, boolean is_available, Integer current_chat_session_id) {
        this.first_name = first_name;
        this.last_name = last_name;
        this.password_hash = password_hash;
        this.email = email;
        this.is_available = is_available;
        this.current_chat_session_id = current_chat_session_id;
    }

    public ConciergeStaff(String first_name, String last_name, String password_hash) {
        this.first_name = first_name;
        this.last_name = last_name;
        this.password_hash = password_hash;
        this.email = first_name.toLowerCase() + "@hotel.ru";
        this.is_available = true;
        this.current_chat_session_id = null;
    }

    @Override
    public String toString() {
        return first_name + " " + last_name + " (пароль: " + password_hash + ")";
    }
}