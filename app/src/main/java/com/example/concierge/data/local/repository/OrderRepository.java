package com.example.concierge.data.local.repository;

import android.content.Context;
import com.example.concierge.data.local.DatabaseClient;
import com.example.concierge.data.local.entity.Order;
import java.util.List;

public class OrderRepository {
    private final DatabaseClient databaseClient;

    public OrderRepository(Context context) {
        this.databaseClient = DatabaseClient.getInstance(context);
    }

    public long insertOrder(Order order) {
        return databaseClient.getAppDatabase().orderDao().insert(order);
    }

    public void updateOrder(Order order) {
        databaseClient.getAppDatabase().orderDao().update(order);
    }

    public void deleteOrder(Order order) {
        databaseClient.getAppDatabase().orderDao().delete(order);
    }

    public Order getOrderById(int id) {
        return databaseClient.getAppDatabase().orderDao().getById(id);
    }

    public List<Order> getOrdersByGuest(int guestId) {
        return databaseClient.getAppDatabase().orderDao().getByGuest(guestId);
    }

    public List<Order> getOrdersByStatus(String status) {
        return databaseClient.getAppDatabase().orderDao().getByStatus(status);
    }

    public List<Order> getOrdersByRoomNumber(String roomNumber) {
        return databaseClient.getAppDatabase().orderDao().getByRoomNumber(roomNumber);
    }

    // Бизнес-логика: создание нового заказа
    public long createOrder(int guestId, int serviceId, double totalPrice) {
        Order order = new Order(guestId, serviceId, "pending", totalPrice,
                new java.util.Date(), null);
        return insertOrder(order);
    }

    // Бизнес-логика: обновление статуса заказа
    public void updateOrderStatus(int orderId, String status) {
        databaseClient.getAppDatabase().orderDao().updateStatus(orderId, status);

        if (status.equals("delivered") || status.equals("cancelled")) {
            // Если заказ завершен, устанавливаем время завершения
            Order order = getOrderById(orderId);
            if (order != null) {
                order.completed_at = new java.util.Date();
                updateOrder(order);
            }
        }
    }

    // Бизнес-логика: получение активных заказов гостя
    public List<Order> getActiveOrdersByGuest(int guestId) {
        List<Order> orders = getOrdersByGuest(guestId);
        // Фильтруем только активные заказы (не завершенные)
        return orders.stream()
                .filter(order -> !order.status.equals("delivered") && !order.status.equals("cancelled"))
                .collect(java.util.stream.Collectors.toList());
    }
}
