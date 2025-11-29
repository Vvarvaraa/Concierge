package com.example.concierge.data.local.entity;

import androidx.room.Entity;
import androidx.room.PrimaryKey;
import java.util.Date;

@Entity(tableName = "concierge_sessions")
public class ConciergeSession {
    @PrimaryKey(autoGenerate = true)
    public Long id_session;
    public Long guest_id;
    public String guest_name;
    public String initial_request;
    public Date start_time;
    public Date end_time;
    public String status;
    public Long concierge_id;

    public ConciergeSession() {}

    public ConciergeSession(Long guest_id, String guest_name, String initial_request) {
        this.guest_id = guest_id;
        this.guest_name = guest_name;
        this.initial_request = initial_request;
        this.start_time = new Date();
        this.status = "pending";
    }
}