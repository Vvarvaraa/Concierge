package com.example.concierge.data.local;
import com.example.concierge.data.local.entity.*;

import java.util.List;

public class DatabaseInitializer {

    public static void initializeSampleData(AppDatabase db) {
        initializeCategories(db);
        initializeServices(db);
        initializeStaff(db);
        initializeGuests(db);
        initializeSampleMessages(db);
    }

    private static void initializeCategories(AppDatabase db) {
        if (db.categoryServiceDao().getAll().isEmpty()) {
            CategoryService[] categories = {
                    new CategoryService("Еда и напитки", 1),
                    new CategoryService("СПА и Wellness", 2),
                    new CategoryService("Трансфер", 3),
                    new CategoryService("Бытовые услуги", 4),
            };

            for (CategoryService category : categories) {
                db.categoryServiceDao().insert(category);
            }
        }
    }

    private static void initializeServices(AppDatabase db) {
        if (db.serviceDao().getAllAvailable().isEmpty()) {
            Service[] services = {
                    // Еда и напитки (category_id = 1)
                    new Service(1, "Завтрак Континентальный", "Свежая выпечка, джем, йогурт, кофе/чай", 1200.0, "", true),
                    new Service(1, "Пицца Маргарита", "Классическая пицца с томатами и моцареллой", 850.0, "", true),
                    new Service(1, "Ужин Шеф-меню", "Специальное предложение от шеф-повара", 2200.0, "", true),

                    // СПА и Wellness (category_id = 2)
                    new Service(2, "Расслабляющий массаж", "Снятие напряжения и стресса (60 мин)", 3500.0, "", true),
                    new Service(2, "SPA-процедура", "Комплексный уход за телом", 5000.0, "", true),

                    // Трансфер (category_id = 3)
                    new Service(3, "Трансфер в аэропорт", "Комфортабельный седан", 2500.0, "", true),

                    // Уборка (category_id = 4)
                    new Service(4, "Дополнительная уборка", "Глубокая уборка номера", 0.0, "", true)
            };

            for (Service service : services) {
                db.serviceDao().insert(service);
            }
        }
    }

    private static void initializeStaff(AppDatabase db) {
        if (db.conciergeStaffDao().getAvailableStaff().isEmpty()) {
            ConciergeStaff[] staff = {
                    new ConciergeStaff("Анна", "Волкова", "0501", "anna@hotel.ru", true, null),
                    new ConciergeStaff("Сергей", "Соколов", "0102", "sery@hotel.ru", true, null),
                    new ConciergeStaff("Мария", "Нагих", "1205", "maria@hotel.ru", false, null),
                    new ConciergeStaff("Елизавета", "Домашенко", "2406", "liza@hotel.ru", true, null)
            };

            for (ConciergeStaff s : staff) {
                db.conciergeStaffDao().insert(s);
            }
        }
    }

    private static void initializeGuests(AppDatabase db) {
        if (db.guestDao().getAll().isEmpty()) {
            Guest[] guests = {
                    new Guest("101", "Ронан", "Амстронг", "+79161234567", "ru", new java.util.Date()),
                    new Guest("101", "ронан", "амстронг", "+79161234567", "ru", new java.util.Date()),
                    new Guest("102", "Моника", "Бинг", "+79167654321", "ru", new java.util.Date()),
                    new Guest("102", "моника", "бинг", "+79167654321", "ru", new java.util.Date()),
                    new Guest("103", "Джереми", "Волков", "+44123456789", "ru", new java.util.Date()),
                    new Guest("103", "джереми", "волков", "+44123456789", "ru", new java.util.Date()),
                    new Guest("201", "Анна", "Сидорова", "+79165554433", "ru", new java.util.Date()),
                    new Guest("201", "анна", "сидорова", "+79165554433", "ru", new java.util.Date()),
                    new Guest("202", "Петр", "Кузнецов", "+79167778899", "ru", new java.util.Date()),
                    new Guest("202", "петр", "кузнецов", "+79167778899", "ru", new java.util.Date())
            };

            for (Guest guest : guests) {
                db.guestDao().insert(guest);
            }
        }
    }
    private static void initializeSampleMessages(AppDatabase db) {
        // Проверяем, есть ли уже сообщения у гостя с id_guest = 1
        List<ChatMessage> existing = db.chatMessageDao().getMessagesByGuest(1L);
        if (existing == null || existing.isEmpty()) {
            ChatMessage[] messages = {
                    new ChatMessage(1L, "concierge", "Добро пожаловать в отель Cortez! Чем могу помочь?",
                            new java.util.Date(System.currentTimeMillis() - 3600000), null),
                    new ChatMessage(1L, "guest", "Здравствуйте! Как заказать завтрак в номер?",
                            new java.util.Date(System.currentTimeMillis() - 1800000), null),
                    new ChatMessage(1L, "concierge", "Конечно! Откройте каталог услуг и выберите 'Еда и напитки'",
                            new java.util.Date(System.currentTimeMillis() - 1200000), null),
                    new ChatMessage(1L, "guest", "Спасибо! А как работает служба уборки?",
                            new java.util.Date(System.currentTimeMillis() - 600000), null),
                    new ChatMessage(1L, "concierge", "Уборка проводится ежедневно с 10:00 до 14:00. Нужна дополнительная уборка?",
                            new java.util.Date(System.currentTimeMillis() - 300000), null)
            };
            for (ChatMessage message : messages) {
                db.chatMessageDao().insert(message);
            }
        }
    }
}
