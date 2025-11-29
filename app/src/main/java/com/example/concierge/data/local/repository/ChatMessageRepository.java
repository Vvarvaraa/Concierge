package com.example.concierge.data.local.repository;

import android.content.Context;
import com.example.concierge.data.local.DatabaseClient;
import com.example.concierge.data.local.entity.ChatMessage;
import java.util.Date;
import java.util.List;

public class ChatMessageRepository {
    public final com.example.concierge.data.local.dao.ChatMessageDao chatMessageDao;

    public ChatMessageRepository(Context context) {
        this.chatMessageDao = DatabaseClient.getInstance(context).getAppDatabase().chatMessageDao();
    }

    public void sendGuestMessage(long guestId, String text, String relatedOrderId) {
        ChatMessage msg = new ChatMessage(guestId, "guest", text, new Date(), relatedOrderId);
        new Thread(() -> chatMessageDao.insert(msg)).start();
    }

    public void sendConciergeMessage(long guestId, String text, String senderType) {
        ChatMessage msg = new ChatMessage(guestId, senderType, text, new Date(), null);
        new Thread(() -> chatMessageDao.insert(msg)).start();
    }

    public List<ChatMessage> getMessagesByGuest(long guestId) {
        return chatMessageDao.getMessagesByGuest(guestId);
    }

    public List<ChatMessage> getMessagesAfterId(long lastId, long guestId) {
        return chatMessageDao.getMessagesAfterId(lastId, guestId);
    }
}