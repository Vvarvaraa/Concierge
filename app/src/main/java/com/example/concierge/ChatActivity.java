package com.example.concierge;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.concierge.data.local.adapters.MessageAdapter;
import com.example.concierge.data.local.dao.GuestDao;
import com.example.concierge.data.local.entity.ChatMessage;
import com.example.concierge.data.local.entity.Guest;
import com.example.concierge.data.local.repository.ChatMessageRepository;
import com.example.concierge.data.local.repository.ConciergeSessionRepository;

import java.util.Date;
import java.util.List;
import java.util.Locale;

public class ChatActivity extends AppCompatActivity {

    private static final String TAG = "ChatActivity";

    private TextView conciergeStatus;
    private EditText messageEditText;
    private ImageButton sendButton;
    private Button openCatalogButton;
    private RecyclerView chatRecyclerView;
    private CardView catalogLayout;
    private Button btnFoodDrinks, btnSpa, btnTransfer, btnHousehold;

    private MessageAdapter messageAdapter;
    private Guest currentGuest;
    private ChatMessageRepository chatMessageRepository;
    private ConciergeSessionRepository sessionRepository;

    private boolean isCatalogOpen = false;
    private boolean isConciergeActive = false;
    private final Handler handler = new Handler(Looper.getMainLooper());
    private Runnable statusChecker;

    private final ActivityResultLauncher<Intent> catalogResultLauncher = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(),
            result -> {
                if (result.getResultCode() == Activity.RESULT_OK && result.getData() != null) {
                    String finalMessage = result.getData().getStringExtra("final_message");
                    if (finalMessage != null) {
                        messageEditText.setText(finalMessage);
                        messageEditText.setSelection(finalMessage.length());
                        if (isCatalogOpen) toggleCatalog();
                    }
                }
            });

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);

        currentGuest = (Guest) getIntent().getSerializableExtra("guest");
        if (currentGuest == null) {
            Toast.makeText(this, "Ошибка: данные гостя не получены", Toast.LENGTH_LONG).show();
            finish();
            return;
        }

        chatMessageRepository = new ChatMessageRepository(getApplicationContext());
        sessionRepository = new ConciergeSessionRepository(getApplicationContext());

        initViews();
        startStatusChecker();
        loadChatHistory();
    }

    private void initViews() {
        conciergeStatus = findViewById(R.id.conciergeStatus);
        messageEditText = findViewById(R.id.messageEditText);
        sendButton = findViewById(R.id.sendButton);
        openCatalogButton = findViewById(R.id.openCatalogButton);
        chatRecyclerView = findViewById(R.id.chat_recycler_view);
        catalogLayout = findViewById(R.id.catalog_card_view);
        btnFoodDrinks = findViewById(R.id.btn_food_drinks);
        btnSpa = findViewById(R.id.btn_spa);
        btnTransfer = findViewById(R.id.btn_transfer);
        btnHousehold = findViewById(R.id.btn_household);

        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        layoutManager.setStackFromEnd(true);
        chatRecyclerView.setLayoutManager(layoutManager);

        messageAdapter = new MessageAdapter();
        chatRecyclerView.setAdapter(messageAdapter);

        sendButton.setOnClickListener(v -> {
            String text = messageEditText.getText().toString().trim();
            if (!text.isEmpty()) {
                sendMessage(text);
                messageEditText.setText("");
            }
        });

        openCatalogButton.setOnClickListener(v -> toggleCatalog());
        setupCategoryButtons();
        updateUIForConciergeStatus();
    }

    private void loadChatHistory() {
        new Thread(() -> {
            List<ChatMessage> messages = chatMessageRepository.getMessagesByGuest(currentGuest.id_guest);
            runOnUiThread(() -> {
                messageAdapter.setMessages(messages);

                if (messages.isEmpty()) {
                    String welcome = "Добро пожаловать в отель Cortez! Чем я могу вам помочь сегодня?";
                    ChatMessage welcomeMsg = new ChatMessage(
                            currentGuest.id_guest, "concierge", welcome, new Date(), null);
                    chatMessageRepository.sendConciergeMessage(currentGuest.id_guest, welcome, "concierge");
                    messageAdapter.addMessage(welcomeMsg);
                }
                chatRecyclerView.scrollToPosition(messageAdapter.getItemCount() - 1);
            });
        }).start();
    }

    private void sendMessage(String text) {
        if (text.trim().isEmpty()) return;
        long tempGuestId = currentGuest.id_guest;
        ChatMessage guestMsg = new ChatMessage(tempGuestId, "guest", text, new Date(), null);
        messageAdapter.addMessage(guestMsg);
        chatRecyclerView.scrollToPosition(messageAdapter.getItemCount() - 1);
        messageEditText.setText("");
        new Thread(() -> {
            try {
                GuestDao guestDao = com.example.concierge.data.local.DatabaseClient.getInstance(getApplicationContext()).getAppDatabase().guestDao();
                Guest dbGuest = guestDao.getById(currentGuest.id_guest);

                if (dbGuest == null) {
                    runOnUiThread(() -> Toast.makeText(ChatActivity.this, "Исправляю базу данных...", Toast.LENGTH_SHORT).show());
                    long newId = guestDao.insert(currentGuest);
                    currentGuest.id_guest = (int) newId; // Обновляем ID
                }
                chatMessageRepository.sendGuestMessage(currentGuest.id_guest, text, null);

                String lower = text.toLowerCase(Locale.getDefault());
                boolean wantsConcierge = lower.contains("консьерж") || lower.contains("оператор") ||
                        lower.contains("позови") || lower.contains("человек");

                if (wantsConcierge) {
                    sessionRepository.createConciergeRequest(currentGuest.id_guest,
                            currentGuest.first_name, text);

                    String waitText = "Соединяю с консьержем...";
                    chatMessageRepository.sendConciergeMessage(currentGuest.id_guest, waitText, "concierge");

                    runOnUiThread(() -> {
                        messageAdapter.addMessage(new ChatMessage(currentGuest.id_guest, "concierge", waitText, new Date(), null));
                        chatRecyclerView.scrollToPosition(messageAdapter.getItemCount() - 1);
                    });
                    return;
                }
                Thread.sleep(500);

                String reply = generateHumanLikeResponse(text);

                chatMessageRepository.sendConciergeMessage(currentGuest.id_guest, reply, "concierge");

                runOnUiThread(() -> {
                    messageAdapter.addMessage(new ChatMessage(currentGuest.id_guest, "concierge", reply, new Date(), null));
                    chatRecyclerView.scrollToPosition(messageAdapter.getItemCount() - 1);
                });

            } catch (Exception e) {
                e.printStackTrace();
                String errorText = e.getMessage();
                runOnUiThread(() -> Toast.makeText(ChatActivity.this, "ОШИБКА: " + errorText, Toast.LENGTH_LONG).show());
            }
        }).start();
    }

    private String generateHumanLikeResponse(String userMessage) {
        String lower = userMessage.toLowerCase(Locale.getDefault());
        if (lower.contains("стейк") || lower.contains("рибай") || lower.contains("лосось") ||
                lower.contains("паста") || lower.contains("картошка") || lower.contains("крылья") ||
                lower.contains("тирамису") || lower.contains("фондант") || lower.contains("чизкейк") ||
                lower.contains("латте") || lower.contains("капучино") || lower.contains("эспрессо") ||
                lower.contains("раф") || lower.contains("чай") || lower.contains("сок") ||
                lower.contains("вода") || lower.contains("шампанск") || lower.contains("просекко") ||
                lower.contains("вино") || lower.contains("апероль") || lower.contains("мохито") ||
                lower.contains("завтрак") || lower.contains("закажу") || lower.contains("хочу") ||
                lower.contains("принесите") || lower.contains("заказ")) {

            String[] foodReplies = {
                    "Отличный выбор! Заказ принят — всё будет в номере через 10–15 минут. Приятного аппетита!",
                    "Готово! Ваш заказ уже в работе. Ждите доставку через 12–15 минут",
                    "Принято! Скоро постучимся с вашим заказом. Приятного отдыха!",
                    "Заказ подтверждён! Доставим в номер через 10–15 минут. Спасибо за заказ!"
            };
            return foodReplies[(int) (Math.random() * foodReplies.length)];
        }

        // 2. СПА — ЗАПИСЬ НА ПРОЦЕДУРУ
        if (lower.contains("массаж") || lower.contains("спа") || lower.contains("обертывание") ||
                lower.contains("уход за лицом") || lower.contains("ритуал") || lower.contains("запишите") ||
                lower.contains("хочу на массаж")) {

            String[] spaReplies = {
                    "Отлично! Вас записали. Ждём в СПА-зоне на 1 этаже через 30 минут — мастер уже готовится",
                    "Готово! Сеанс подтверждён. Спуститесь через полчаса — всё будет идеально",
                    "Записал! Приходите в СПА через 30–40 минут. Расслабьтесь и наслаждайтесь!",
                    "Прекрасно! Вас ждут в СПА на 1 этаже через 30 минут. До встречи!"
            };
            return spaReplies[(int) (Math.random() * spaReplies.length)];
        }

        // 3. ТРАНСФЕР
        if (lower.contains("трансфер") || lower.contains("машина") || lower.contains("такси") ||
                lower.contains("аэропорт") || lower.contains("едем") || lower.contains("нужна машина")) {

            String[] transferReplies = {
                    "Всё организовано! Машина будет у входа через 15–20 минут. Водитель позвонит за 5 минут",
                    "Готово! Автомобиль прибудет через 20 минут. Хорошей дороги!",
                    "Заказал! Белый Mercedes уже в пути — будет через 15 минут у главного входа",
                    "Трансфер подтверждён! Машина приедет через 20 минут. Если что — пишите"
            };
            return transferReplies[(int) (Math.random() * transferReplies.length)];
        }

        // 4. БЫТОВЫЕ УСЛУГИ
        if (lower.contains("уборка") || lower.contains("стирка") || lower.contains("гладить") ||
                lower.contains("кроватка") || lower.contains("стульчик") || lower.contains("починить") ||
                lower.contains("сломалось")) {

            String[] householdReplies = {
                    "Сейчас всё сделаем! Горничная поднимается в номер через 10 минут",
                    "Готово! Стирка принята — вещи будут через 2 часа чистые и выглаженные",
                    "Кроватка и стульчик уже везут! Через 5 минут будут у вас",
                    "Ремонтник уже в пути! Будет через 10–15 минут — всё починим бесплатно",
                    "Принято! Уборка назначена на ближайшее время. Спасибо, что сообщили!"
            };
            return householdReplies[(int) (Math.random() * householdReplies.length)];
        }

        // 5. ЕСЛИ ПРОСЯТ МЕНЮ — ДАЁМ КРАТКОЕ МЕНЮ КАТЕГОРИЙ
        if (lower.contains("меню") || lower.contains("покажи") || lower.contains("что у вас") ||
                lower.contains("еда") || lower.contains("спа") || lower.contains("трансфер") ||
                lower.contains("бытовые") || lower.contains("услуги")) {

            return "Чем могу помочь?\n\n" +
                    "• Еда и напитки\n" +
                    "• Завтраки\n" +
                    "• СПА и массаж\n" +
                    "• Трансфер\n" +
                    "• Бытовые услуги (уборка, стирка и т.д.)\n\n" +
                    "Напишите, что вас интересует — и я всё организую!";
        }

        // 6. ПРИВЕТСТВИЕ
        if (lower.contains("привет") || lower.contains("здравствуйте") || lower.contains("добрый")) {
            return "Добрый день! Чем могу помочь сегодня?";
        }

        if (lower.contains("спасибо") || lower.contains("благодарю")) {
            return "Всегда пожалуйста! Обращайтесь в любое время";
        }

        // 7. НЕ ПОНЯЛ — УТОЧНЯЕТ
        String[] fallback = {
                "Понял! Скажите: еда, СПА, трансфер или бытовые услуги?",
                "Конечно, помогу! Что вам нужно прямо сейчас?",
                "Напишите подробнее — моментально всё организую",
                "Готов помочь! Еда, массаж, машина или уборка?"
        };
        return fallback[(int) (Math.random() * fallback.length)];
    }

    private void startStatusChecker() {
        statusChecker = () -> {
            checkActiveConciergeSession();
            handler.postDelayed(statusChecker, 3000);
        };
        handler.post(statusChecker);
    }

    private void stopStatusChecker() {
        handler.removeCallbacks(statusChecker);
    }

    private void checkActiveConciergeSession() {
        new Thread(() -> {
            try {
                boolean active = sessionRepository.hasActiveSession(currentGuest.id_guest + 0L);
                Log.d(TAG, "hasActiveSession for guest " + currentGuest.id_guest + " = " + active);
                runOnUiThread(() -> {
                    if (isConciergeActive != active) {
                        isConciergeActive = active;
                        Log.d(TAG, "Concierge status changed to: " + active);
                        updateUIForConciergeStatus();
                        if (!active) {
                            Toast.makeText(this, "Консьерж отключился", Toast.LENGTH_SHORT).show();
                        }
                    }
                });
            } catch (Exception e) {
                e.printStackTrace();
                Log.e(TAG, "Error checking session", e);
            }
        }).start();
    }

    private void updateUIForConciergeStatus() {
        if (isConciergeActive) {
            conciergeStatus.setText("Консьерж в чате • " + currentGuest.first_name);
            conciergeStatus.setBackgroundColor(getResources().getColor(android.R.color.darker_gray));
            messageEditText.setHint("Напишите живому консьержу...");
        } else {
            conciergeStatus.setText("Бот-помощник • " + currentGuest.first_name);
            conciergeStatus.setBackgroundColor(getResources().getColor(R.color.bordo));
            messageEditText.setHint("Введите сообщение...");
        }
    }

    private void toggleCatalog() {
        if (isCatalogOpen) {
            Animation slideDown = AnimationUtils.loadAnimation(this, R.anim.slide_down);
            Animation fadeOut = AnimationUtils.loadAnimation(this, R.anim.fade_out);
            openCatalogButton.startAnimation(slideDown);
            catalogLayout.startAnimation(fadeOut);
            catalogLayout.setVisibility(View.GONE);
            openCatalogButton.setText("Открыть каталог");
            isCatalogOpen = false;
        } else {
            Animation slideUp = AnimationUtils.loadAnimation(this, R.anim.slide_up);
            Animation fadeIn = AnimationUtils.loadAnimation(this, R.anim.fade_in);
            openCatalogButton.startAnimation(slideUp);
            catalogLayout.setVisibility(View.VISIBLE);
            catalogLayout.startAnimation(fadeIn);
            openCatalogButton.setText("Закрыть каталог");
            isCatalogOpen = true;
        }
    }

    private void setupCategoryButtons() {
        View.OnClickListener catalogButtonListener = v -> {
            Intent intent;
            Class<?> targetActivity;

            int id = v.getId();
            if (id == R.id.btn_food_drinks) {
                targetActivity = FoodDrinksActivity.class;
            } else if (id == R.id.btn_spa) {
                targetActivity = SpaActivity.class;
            } else if (id == R.id.btn_transfer) {
                targetActivity = TransferActivity.class;
            } else if (id == R.id.btn_household) {
                targetActivity = HouseholdActivity.class;
            } else {
                return;
            }

            intent = new Intent(ChatActivity.this, targetActivity);
            intent.putExtra("guest", currentGuest);

            String currentText = messageEditText.getText().toString().trim();
            if (!currentText.isEmpty()) {
                intent.putExtra("initial_order_text", currentText);
            }

            catalogResultLauncher.launch(intent);
        };

        btnFoodDrinks.setOnClickListener(catalogButtonListener);
        btnSpa.setOnClickListener(catalogButtonListener);
        btnTransfer.setOnClickListener(catalogButtonListener);
        btnHousehold.setOnClickListener(catalogButtonListener);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        stopStatusChecker();
    }
}