package com.example.concierge.data.local.entity;

import androidx.room.Entity;
import androidx.room.PrimaryKey;
import androidx.room.ForeignKey;
import java.util.Date;

@Entity(tableName = "order",
        foreignKeys = {
                @ForeignKey(entity = Guest.class,
                        parentColumns = "id_guest",
                        childColumns = "id_guest",
                        onDelete = ForeignKey.CASCADE),
                @ForeignKey(entity = Service.class,
                        parentColumns = "id_service",
                        childColumns = "id_service",
                        onDelete = ForeignKey.CASCADE)
        })
public class Order {
    @PrimaryKey(autoGenerate = true)
    public int id_order;

    public int id_guest;
    public int id_service;
    public String status; // pending, confirmed, in_progress, delivered, cancelled
    public double total_price;
    public Date created_at;
    public Date completed_at;

    public Order(int id_guest, int id_service, String status,
                 double total_price, Date created_at, Date completed_at) {
        this.id_guest = id_guest;
        this.id_service = id_service;
        this.status = status;
        this.total_price = total_price;
        this.created_at = created_at;
        this.completed_at = completed_at;
    }
}
