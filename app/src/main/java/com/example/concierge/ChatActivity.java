package com.example.concierge;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.ImageButton;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import com.example.concierge.data.local.entity.Guest;
import com.example.concierge.data.local.entity.ChatMessage;
import com.example.concierge.data.local.repository.ChatMessageRepository;
import java.util.List;

public class ChatActivity extends AppCompatActivity {

    private static final String TAG = "ChatActivity";

    private TextView conciergeStatus;
    private TextView chatBubble;
    private EditText messageEditText;
    private ImageButton sendButton;
    private Button openCatalogButton;
    private Guest currentGuest;
    private ChatMessageRepository chatMessageRepository;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);

        // Получаем данные гостя
        currentGuest = (Guest) getIntent().getSerializableExtra("guest");
        if (currentGuest == null) {
            Toast.makeText(this, "Ошибка: данные гостя не получены", Toast.LENGTH_LONG).show();
            finish();
            return;
        }

        chatMessageRepository = new ChatMessageRepository(getApplication());

        initViews();
        setupWelcomeMessage();
        loadChatHistory();
        setupCatalogButton(); // Добавлен вызов метода настройки кнопки каталога

        // Тестируем работу базы данных
        testDatabaseOperations();
    }

    private void initViews() {
        conciergeStatus = findViewById(R.id.conciergeStatus);
        chatBubble = findViewById(R.id.chatBubble);
        messageEditText = findViewById(R.id.messageEditText);
        sendButton = findViewById(R.id.sendButton);
        openCatalogButton = findViewById(R.id.openCatalogButton);

        sendButton.setOnClickListener(v -> {
            String message = messageEditText.getText().toString().trim();
            if (!message.isEmpty()) {
                sendMessage(message);
            } else {
                Toast.makeText(ChatActivity.this, "Введите сообщение", Toast.LENGTH_SHORT).show();
            }
        });

        // Убрана временная заглушка, теперь используется setupCatalogButton()

        // Обработка нажатия Enter в поле ввода
        messageEditText.setOnEditorActionListener((v, actionId, event) -> {
            if (actionId == android.view.inputmethod.EditorInfo.IME_ACTION_SEND) {
                sendButton.performClick();
                return true;
            }
            return false;
        });
    }

    private void sendMessage(String messageText) {
        if (currentGuest != null) {
            new Thread(() -> {
                try {
                    // Сохраняем сообщение в базу данных
                    long messageId = chatMessageRepository.sendGuestMessage(
                            currentGuest.id_guest,
                            messageText,
                            null // relatedOrderId может быть null
                    );

                    runOnUiThread(() -> {
                        // Обновляем отображение
                        updateChatBubble(messageText, "guest");
                        messageEditText.setText("");
                        Toast.makeText(ChatActivity.this, "Сообщение отправлено", Toast.LENGTH_SHORT).show();
                    });

                    // Имитируем ответ консьержа
                    simulateConciergeResponse();

                } catch (Exception e) {
                    Log.e(TAG, "Ошибка отправки сообщения", e);
                    runOnUiThread(() -> {
                        Toast.makeText(ChatActivity.this, "Ошибка отправки: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                    });
                }
            }).start();
        }
    }

    private void updateChatBubble(String message, String sender) {
        String currentText = chatBubble.getText().toString();
        String senderPrefix = getSenderPrefix(sender);
        String newMessage = senderPrefix + message;

        // Если в chatBubble уже есть сообщения, добавляем новое с новой строки
        if (!currentText.isEmpty()) {
            chatBubble.setText(currentText + "\n\n" + newMessage);
        } else {
            chatBubble.setText(newMessage);
        }

        // Прокручиваем вниз к новому сообщению
        scrollToBottom();
    }

    private String getSenderPrefix(String sender) {
        switch (sender) {
            case "guest":
                return "Вы: ";
            case "concierge":
                return "Консьерж: ";
            case "bot":
                return "Бот: ";
            default:
                return "Система: ";
        }
    }

    private void scrollToBottom() {
        // Прокручиваем TextView вниз к последнему сообщению
        chatBubble.post(() -> {
            int scrollAmount = chatBubble.getLayout().getLineTop(chatBubble.getLineCount()) - chatBubble.getHeight();
            if (scrollAmount > 0) {
                chatBubble.scrollTo(0, scrollAmount);
            } else {
                chatBubble.scrollTo(0, 0);
            }
        });
    }

    private void simulateConciergeResponse() {
        // Имитация ответа консьержа через 2 секунды
        new Thread(() -> {
            try {
                Thread.sleep(2000);

                String[] responses = {
                        "Спасибо за ваше сообщение! Чем еще могу помочь?",
                        "Понял вас! Нужна помощь с чем-то еще?",
                        "Хорошо, я учту эту информацию. Что-то еще?",
                        "Благодарю за обращение! Есть ли другие вопросы?",
                        "Отличный вопрос! Давайте разберемся вместе."
                };

                String response = responses[(int) (Math.random() * responses.length)];

                // Сохраняем ответ консьержа в базу
                if (currentGuest != null) {
                    chatMessageRepository.sendConciergeMessage(currentGuest.id_guest, response, null);
                }

                runOnUiThread(() -> {
                    updateChatBubble(response, "concierge");
                });

            } catch (InterruptedException e) {
                Log.e(TAG, "Имитация ответа прервана", e);
                Thread.currentThread().interrupt();
            } catch (Exception e) {
                Log.e(TAG, "Ошибка при имитации ответа", e);
            }
        }).start();
    }

    private void loadChatHistory() {
        if (currentGuest != null) {
            new Thread(() -> {
                try {
                    List<ChatMessage> messages = chatMessageRepository.getMessagesByGuest(currentGuest.id_guest);

                    runOnUiThread(() -> {
                        if (messages.isEmpty()) {
                            // Если сообщений нет, создаем приветственное
                            createWelcomeMessage();
                        } else {
                            // Отображаем историю сообщений
                            displayChatHistory(messages);
                        }
                    });

                } catch (Exception e) {
                    Log.e(TAG, "Ошибка загрузки истории чата", e);
                    runOnUiThread(() -> {
                        createWelcomeMessage(); // Создаем приветственное сообщение при ошибке
                        Toast.makeText(ChatActivity.this, "Ошибка загрузки истории", Toast.LENGTH_SHORT).show();
                    });
                }
            }).start();
        }
    }

    private void displayChatHistory(List<ChatMessage> messages) {
        StringBuilder chatHistory = new StringBuilder();
        for (ChatMessage message : messages) {
            String senderPrefix = getSenderPrefix(message.sender_type);
            chatHistory.append(senderPrefix).append(message.message_text).append("\n\n");
        }
        chatBubble.setText(chatHistory.toString().trim());

        // Прокручиваем к последнему сообщению
        scrollToBottom();
    }

    private void createWelcomeMessage() {
        if (currentGuest != null) {
            new Thread(() -> {
                try {
                    chatMessageRepository.createWelcomeMessage(currentGuest.id_guest);

                    runOnUiThread(() -> {
                        chatBubble.setText("Бот: Добро пожаловать в отель Cortez! Чем я могу вам помочь сегодня?");
                    });

                } catch (Exception e) {
                    Log.e(TAG, "Ошибка создания приветственного сообщения", e);
                    runOnUiThread(() -> {
                        chatBubble.setText("Бот: Добро пожаловать в отель Cortez! Чем я могу вам помочь сегодня?");
                    });
                }
            }).start();
        }
    }

    private void setupWelcomeMessage() {
        if (currentGuest != null) {
            conciergeStatus.setText("Консьерж онлайн • " + currentGuest.first_name);
        } else {
            conciergeStatus.setText("Консьерж онлайн");
        }
    }

    private void testDatabaseOperations() {
        new Thread(() -> {
            try {
                // Проверяем количество сообщений
                int messageCount = chatMessageRepository.getMessageCount(currentGuest.id_guest);
                Log.d(TAG, "Количество сообщений для гостя " + currentGuest.id_guest + ": " + messageCount);

                // Проверяем последнее сообщение
                ChatMessage lastMessage = chatMessageRepository.getLastMessage(currentGuest.id_guest);
                if (lastMessage != null) {
                    Log.d(TAG, "Последнее сообщение: " + lastMessage.message_text + " от " + lastMessage.sender_type);
                } else {
                    Log.d(TAG, "Сообщений нет");
                }

            } catch (Exception e) {
                Log.e(TAG, "Ошибка теста БД: " + e.getMessage(), e);
            }
        }).start();
    }

    private void setupCatalogButton() {
        openCatalogButton.setOnClickListener(v -> {
            Intent intent = new Intent(ChatActivity.this, CatalogActivity.class);
            // Передаем данные гостя в CatalogActivity если нужно
            intent.putExtra("guest", currentGuest);
            startActivity(intent);
            // Анимация появления каталога (поднятие снизу)
            overridePendingTransition(R.anim.slide_in_down, R.anim.slide_out_down);
        });
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        // Очистка ресурсов
    }
}