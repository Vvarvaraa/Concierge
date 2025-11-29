package com.example.concierge;

import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.concierge.data.local.DatabaseClient;
import com.example.concierge.data.local.adapters.MessageAdapter;
import com.example.concierge.data.local.dao.ChatMessageDao;
import com.example.concierge.data.local.entity.ChatMessage;
import com.example.concierge.data.local.repository.ChatMessageRepository;
import com.example.concierge.data.local.repository.ConciergeSessionRepository;
import java.util.Date;
import java.util.List;

public class ConciergeChatActivity extends AppCompatActivity {

    private TextView guestNameTextView;
    private RecyclerView chatRecyclerView;
    private EditText messageEditText;
    private ImageButton sendButton;
    private Button endSessionButton;
    private MessageAdapter messageAdapter;
    private ChatMessageRepository chatMessageRepository;
    private ConciergeSessionRepository sessionRepository;

    private long guestId;
    private final Handler handler = new Handler(Looper.getMainLooper());
    private final long POLL_INTERVAL = 2000;
    private long lastMessageId = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_concierge_chat);

        guestId = getIntent().getLongExtra("guest_id", -1);
        String guestName = getIntent().getStringExtra("guest_name");

        if (guestId == -1 || guestName == null) {
            Toast.makeText(this, "Ошибка загрузки чата", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        chatMessageRepository = new ChatMessageRepository(this);
        sessionRepository = new ConciergeSessionRepository(this);

        initViews();
        loadHistoryAndStartPolling();
    }

    private void initViews() {
        guestNameTextView = findViewById(R.id.guest_name_textview);
        if (guestNameTextView == null) guestNameTextView = findViewById(R.id.hotelTitle);
        guestNameTextView.setText("Чат с: " + getIntent().getStringExtra("guest_name"));

        chatRecyclerView = findViewById(R.id.chat_recycler_view);
        messageEditText = findViewById(R.id.messageEditText);
        sendButton = findViewById(R.id.sendbutt);
        endSessionButton = findViewById(R.id.end_session_button);

        messageAdapter = new MessageAdapter();
        chatRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        chatRecyclerView.setAdapter(messageAdapter);

        sendButton.setOnClickListener(v -> sendMessage());
        endSessionButton.setOnClickListener(v -> endSession());
    }

    private void loadHistoryAndStartPolling() {
        new Thread(() -> {
            List<ChatMessage> history = chatMessageRepository.getMessagesByGuest(guestId);
            if (!history.isEmpty()) {
                lastMessageId = history.get(history.size() - 1).id_message;
            }
            runOnUiThread(() -> {
                messageAdapter.setMessages(history);
                scrollToBottom();
            });
            startPolling();
        }).start();
    }

    private void startPolling() {
        handler.postDelayed(pollingRunnable, POLL_INTERVAL);
    }

    private final Runnable pollingRunnable = new Runnable() {
        @Override
        public void run() {
            new Thread(() -> {
                List<ChatMessage> newMessages = chatMessageRepository.getMessagesAfterId(lastMessageId, guestId);
                if (!newMessages.isEmpty()) {
                    lastMessageId = newMessages.get(newMessages.size() - 1).id_message;
                    runOnUiThread(() -> {
                        for (ChatMessage msg : newMessages) {
                            messageAdapter.addMessage(msg);
                        }
                        scrollToBottom();
                    });
                }
                handler.postDelayed(this, POLL_INTERVAL);
            }).start();
        }
    };

    private void scrollToBottom() {
        chatRecyclerView.scrollToPosition(messageAdapter.getItemCount() - 1);
    }

    private void sendMessage() {
        String text = messageEditText.getText().toString().trim();
        if (text.isEmpty()) return;

        ChatMessage msg = new ChatMessage(guestId, "concierge_live", text, new Date(), null);

        new Thread(() -> {
            long newId = chatMessageRepository.chatMessageDao.insert(msg);
            msg.id_message = (int) newId;

            runOnUiThread(() -> {
                messageAdapter.addMessage(msg);
                scrollToBottom();
                messageEditText.setText("");
            });

            // Убираем дубли — обновляем lastMessageId
            lastMessageId = newId;
        }).start();
    }

    private void endSession() {
        new Thread(() -> {
            sessionRepository.endSession(guestId);
            chatMessageRepository.sendConciergeMessage(guestId, "Консьерж завершил сессию. Спасибо за обращение!", "concierge_live");
            runOnUiThread(() -> {
                Toast.makeText(this, "Сессия завершена", Toast.LENGTH_SHORT).show();
                finish();
            });
        }).start();
    }

    @Override
    protected void onDestroy() {
        handler.removeCallbacks(pollingRunnable);
        super.onDestroy();
    }
}