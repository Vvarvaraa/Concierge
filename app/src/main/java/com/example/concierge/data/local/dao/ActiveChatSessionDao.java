package com.example.concierge.data.local.dao;

import androidx.room.*;
import com.example.concierge.data.local.entity.ActiveChatSession;
import java.util.List;

@Dao
public interface ActiveChatSessionDao {
    @Insert
    long insert(ActiveChatSession session);

    @Update
    void update(ActiveChatSession session);

    @Delete
    void delete(ActiveChatSession session);

    @Query("SELECT * FROM active_chat_sessions WHERE id_chat_sessions = :id")
    ActiveChatSession getById(int id);

    @Query("SELECT * FROM active_chat_sessions WHERE status = 'awaiting' ORDER BY created_at ASC")
    List<ActiveChatSession> getAwaitingSessions();

    @Query("SELECT * FROM active_chat_sessions WHERE id_staff = :staffId AND status = 'assigned'")
    List<ActiveChatSession> getAssignedSessions(int staffId);

    @Query("SELECT * FROM active_chat_sessions WHERE id_guest = :guestId AND status != 'resolved'")
    ActiveChatSession getActiveSessionByGuest(int guestId);

    @Query("UPDATE active_chat_sessions SET status = :status, id_staff = :staffId WHERE id_chat_sessions = :sessionId")
    void assignToStaff(int sessionId, int staffId, String status);

    @Query("UPDATE active_chat_sessions SET status = 'resolved', resolved_at = :resolvedAt WHERE id_chat_sessions = :sessionId")
    void resolveSession(int sessionId, java.util.Date resolvedAt);
}
