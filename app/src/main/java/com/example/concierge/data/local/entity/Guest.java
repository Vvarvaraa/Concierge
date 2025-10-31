package com.example.concierge.data.local.entity;

import androidx.room.Entity;
import androidx.room.PrimaryKey;
import androidx.room.ColumnInfo;

import java.io.Serializable;
import java.util.Date;

@Entity(tableName = "guests")
public class Guest implements Serializable {

    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "id_guest")
    public int id_guest;

    @ColumnInfo(name = "room_number")
    public String room_number;

    @ColumnInfo(name = "first_name")
    public String first_name;

    @ColumnInfo(name = "last_name")
    public String last_name;

    @ColumnInfo(name = "phone_number")
    public String phone_number;

    @ColumnInfo(name = "language")
    public String language;

    @ColumnInfo(name = "created_at")
    public Date created_at;

    public Guest(String room_number, String first_name, String last_name,
                 String phone_number, String language, Date created_at) {
        this.room_number = room_number;
        this.first_name = first_name;
        this.last_name = last_name;
        this.phone_number = phone_number;
        this.language = language;
        this.created_at = created_at;
    }

    public Guest() {
    }
}