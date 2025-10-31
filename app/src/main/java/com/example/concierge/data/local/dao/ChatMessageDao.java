package com.example.concierge.data.local.dao;

import androidx.room.*;
import com.example.concierge.data.local.entity.ChatMessage;
import java.util.List;

@Dao public interface ChatMessageDao {
    @Insert
    long insert(ChatMessage message);

    @Update
    void update(ChatMessage message);

    @Delete
    void delete(ChatMessage message);

    @Query("SELECT * FROM chat_message WHERE id_guest = :guestId ORDER BY timestamp ASC")
    List<ChatMessage> getByGuest(int guestId);

    @Query("SELECT * FROM chat_message WHERE related_order_id = :orderId ORDER BY timestamp ASC")
    List<ChatMessage> getByOrder(int orderId);

    @Query("SELECT * FROM chat_message WHERE id_guest = :guestId AND sender_type = :senderType")
    List<ChatMessage> getByGuestAndSender(int guestId, String senderType);

    @Query("DELETE FROM chat_message WHERE id_guest = :guestId")
    void deleteByGuest(int guestId);

    // Получение количества сообщений гостя
    @Query("SELECT COUNT(*) FROM chat_message WHERE id_guest = :guestId")
    int getMessageCount(int guestId);

    // Получение последнего сообщения гостя
    @Query("SELECT * FROM chat_message WHERE id_guest = :guestId ORDER BY timestamp DESC LIMIT 1")
    ChatMessage getLastMessage(int guestId);

    // Получение недавних сообщений (если нужно)
    @Query("SELECT * FROM chat_message WHERE id_guest = :guestId ORDER BY timestamp DESC LIMIT :limit")
    List<ChatMessage> getRecentByGuest(int guestId, int limit);

}
