package com.example.concierge.data.local.dao;

import androidx.room.*;
import com.example.concierge.data.local.entity.ChatMessage;
import java.util.List;

@Dao
public interface ChatMessageDao {

    @Insert
    long insert(ChatMessage message);

    @Query("SELECT * FROM chat_message WHERE id_guest = :guestId ORDER BY timestamp ASC")
    List<ChatMessage> getMessagesByGuest(long guestId);

    @Query("SELECT * FROM chat_message WHERE id_message > :lastId AND id_guest = :guestId ORDER BY timestamp ASC")
    List<ChatMessage> getMessagesAfterId(long lastId, long guestId);
}