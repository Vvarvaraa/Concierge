package com.example.concierge.data.local.dao;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;
import com.example.concierge.data.local.entity.ConciergeSession;
import java.util.List;
import java.util.Date;

@Dao
public interface ConciergeSessionDao {
    @Insert
    void insert(ConciergeSession session);

    @Query("SELECT COUNT(*) > 0 FROM concierge_sessions WHERE guest_id = :guestId AND (status = 'pending' OR status = 'active')")
    boolean hasActiveSession(Long guestId);

    @Query("UPDATE concierge_sessions SET status = 'active', concierge_id = :conciergeId, start_time = :startTime WHERE id_session = :sessionId")
    void startSession(Long sessionId, Long conciergeId, Date startTime);

    @Query("UPDATE concierge_sessions SET status = 'ended', end_time = :endTime WHERE guest_id = :guestId AND (status = 'pending' OR status = 'active')")
    void endSession(Long guestId, Date endTime);

    @Query("SELECT * FROM concierge_sessions WHERE status = 'pending' ORDER BY start_time DESC")
    LiveData<List<ConciergeSession>> getPendingRequests();

    @Query("SELECT * FROM concierge_sessions WHERE status = 'active' ORDER BY start_time DESC")
    LiveData<List<ConciergeSession>> getActiveSessions();

}