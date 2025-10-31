package com.example.concierge.data.local.repository;

import android.app.Application;

import com.example.concierge.data.local.DatabaseClient;
import com.example.concierge.data.local.dao.ChatMessageDao;
import com.example.concierge.data.local.entity.ChatMessage;
import java.util.Date;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class ChatMessageRepository {
    private ChatMessageDao chatMessageDao;
    private ExecutorService executor;

    public ChatMessageRepository(Application application) {
        this.chatMessageDao = DatabaseClient.getInstance(application).getAppDatabase().chatMessageDao();
        this.executor = Executors.newSingleThreadExecutor();
    }

    // Основные методы для работы с сообщениями
    public long sendGuestMessage(int guestId, String messageText, Integer relatedOrderId) {
        ChatMessage message = new ChatMessage(
                guestId,
                "guest",
                messageText,
                new Date(),
                relatedOrderId
        );

        return chatMessageDao.insert(message);
    }

    // Отправка сообщения от консьержа/бота
    public long sendConciergeMessage(int guestId, String messageText, Integer relatedOrderId) {
        ChatMessage message = new ChatMessage(
                guestId,
                "concierge", // или "bot"
                messageText,
                new Date(),
                relatedOrderId
        );

        return chatMessageDao.insert(message);
    }

    public void createWelcomeMessage(int guestId) {
        ChatMessage welcomeMessage = new ChatMessage(
                guestId,
                "bot",
                "Добро пожаловать в отель Cortez! Чем я могу вам помочь сегодня?",
                new Date(),
                null
        );

        chatMessageDao.insert(welcomeMessage);
    }

    // Получение истории сообщений гостя
    public List<ChatMessage> getMessagesByGuest(int guestId) {
        return chatMessageDao.getByGuest(guestId);
    }

    // Получение количества сообщений
    public int getMessageCount(int guestId) {
        return chatMessageDao.getMessageCount(guestId);
    }

    // Получение последнего сообщения
    public ChatMessage getLastMessage(int guestId) {
        return chatMessageDao.getLastMessage(guestId);
    }

    // Получение недавних сообщений
    public List<ChatMessage> getRecentMessages(int guestId, int limit) {
        return chatMessageDao.getRecentByGuest(guestId, limit);
    }

    // Асинхронное получение количества сообщений
    public void getMessageCountAsync(int guestId, MessageCountCallback callback) {
        executor.execute(() -> {
            int count = chatMessageDao.getMessageCount(guestId);
            if (callback != null) {
                callback.onCountLoaded(count);
            }
        });
    }

    // Асинхронное получение последнего сообщения
    public void getLastMessageAsync(int guestId, LastMessageCallback callback) {
        executor.execute(() -> {
            ChatMessage lastMessage = chatMessageDao.getLastMessage(guestId);
            if (callback != null) {
                callback.onLastMessageLoaded(lastMessage);
            }
        });
    }

    // Асинхронное получение всех сообщений
    public void getMessagesByGuestAsync(int guestId, ChatMessagesCallback callback) {
        executor.execute(() -> {
            List<ChatMessage> messages = chatMessageDao.getByGuest(guestId);
            if (callback != null) {
                callback.onMessagesLoaded(messages);
            }
        });
    }

    // Интерфейсы колбэков
    public interface MessageCountCallback {
        void onCountLoaded(int count);
    }

    public interface LastMessageCallback {
        void onLastMessageLoaded(ChatMessage lastMessage);
    }

    public interface ChatMessagesCallback {
        void onMessagesLoaded(List<ChatMessage> messages);
    }
}