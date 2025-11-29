package com.example.concierge;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.widget.NestedScrollView;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.HorizontalScrollView;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;
import com.example.concierge.data.local.entity.Guest;
import com.example.concierge.data.local.repository.ChatMessageRepository;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class SpaActivity extends AppCompatActivity {

    private NestedScrollView menuScrollView;
    private HorizontalScrollView categoriesScrollView;
    private ImageButton btnBackCatalog;
    private ChatMessageRepository repository;
    private Guest currentGuest;

    // Кнопки категорий
    private Button btnMassages;
    private Button btnSpaProcedures;

    // Кнопки элементов меню (по названиям из simulateConciergeResponse)
    private Button btnRelaxMassage;
    private Button btnSportsMassage;
    private Button btnAntiStressMassage;
    private Button btnThaiMassage;
    private Button btnBodyWrap;
    private Button btnFaceCare;
    private Button btnSpaForTwo;

    // Списки и карты для логики
    private List<String> selectedItems = new ArrayList<>();
    private Map<Button, TextView> categoryMap = new HashMap<>();
    private Map<String, Button> itemButtonMap = new HashMap<>();
    private Button currentActiveButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_spa);

        currentGuest = (Guest) getIntent().getSerializableExtra("guest");

        initViews();
        setupClickListeners();
        setupMenuItemClickListeners();
        setupScrollViewListeners();

        loadInitialOrder(getIntent().getStringExtra("initial_order_text"));

        setActiveButton(btnMassages);
    }

    private void initViews() {
        btnBackCatalog = findViewById(R.id.leftbut);
        menuScrollView = findViewById(R.id.menu_scroll_view);
        categoriesScrollView = findViewById(R.id.categories_scroll);

        // Кнопки категорий
        btnMassages = findViewById(R.id.btn_massages);
        btnSpaProcedures = findViewById(R.id.btn_spaprocedure);

        categoryMap.put(btnMassages, findViewById(R.id.section_relaxmas));
        categoryMap.put(btnSpaProcedures, findViewById(R.id.section_main_courses));

        initMenuItemButtonsAndMap();
    }

    private void initMenuItemButtonsAndMap() {
        // Массажи
        btnRelaxMassage = findViewById(R.id.btn_relax_massage);
        itemButtonMap.put(btnRelaxMassage.getText().toString().trim(), btnRelaxMassage);
        btnSportsMassage = findViewById(R.id.btn_sportmas);
        itemButtonMap.put(btnSportsMassage.getText().toString().trim(), btnSportsMassage);
        btnAntiStressMassage = findViewById(R.id.btn_antistrmas);
        itemButtonMap.put(btnAntiStressMassage.getText().toString().trim(), btnAntiStressMassage);
        btnThaiMassage = findViewById(R.id.btn_taysmas);
        itemButtonMap.put(btnThaiMassage.getText().toString().trim(), btnThaiMassage);

        // SPA-Процедуры
        btnBodyWrap = findViewById(R.id.btn_vodoros);
        itemButtonMap.put(btnBodyWrap.getText().toString().trim(), btnBodyWrap);
        btnFaceCare = findViewById(R.id.btn_fyoung);
        itemButtonMap.put(btnFaceCare.getText().toString().trim(), btnFaceCare);
        btnSpaForTwo = findViewById(R.id.btn_spatwo);
        itemButtonMap.put(btnSpaForTwo.getText().toString().trim(), btnSpaForTwo);
    }

    private void loadInitialOrder(String initialText) {
        if (initialText == null || initialText.isEmpty()) return;

        String prefix = "Заказ СПА: ";
        if (initialText.startsWith(prefix)) {
            String itemsString = initialText.substring(prefix.length()).trim();
            if (itemsString.isEmpty()) return;

            List<String> parsedItems = Arrays.asList(itemsString.split(",\\s*"));

            for (String item : parsedItems) {
                String itemName = item.trim();
                if (!itemName.isEmpty()) {
                    selectedItems.add(itemName);
                    Button button = itemButtonMap.get(itemName);
                    if (button != null) setButtonSelected(button, true);
                }
            }
        }
    }

    private void setButtonSelected(Button button, boolean isSelected) {
        if (isSelected) {
            button.setBackgroundResource(R.drawable.rounded_dark_button);
            button.setTextColor(Color.BLACK);
        } else {
            button.setBackgroundResource(R.drawable.rounded_light_button);
            button.setTextColor(Color.BLACK);
        }
    }

    private void setupClickListeners() {
        btnBackCatalog.setOnClickListener(v -> onBackPressed());
        btnMassages.setOnClickListener(v -> scrollToSection(btnMassages));
        btnSpaProcedures.setOnClickListener(v -> scrollToSection(btnSpaProcedures));
    }

    private void setupMenuItemClickListeners() {
        View.OnClickListener menuItemClickListener = v -> {
            Button button = (Button) v;
            String itemName = button.getText().toString().trim();

            if (selectedItems.contains(itemName)) {
                selectedItems.remove(itemName);
                setButtonSelected(button, false);
                Toast.makeText(this, itemName + " удален из заказа СПА", Toast.LENGTH_SHORT).show();
            } else {
                selectedItems.add(itemName);
                setButtonSelected(button, true);
                Toast.makeText(this, itemName + " добавлен в заказ СПА", Toast.LENGTH_SHORT).show();
            }
        };

        Button[] menuButtons = {
                btnRelaxMassage, btnSportsMassage, btnAntiStressMassage, btnThaiMassage,
                btnBodyWrap, btnFaceCare, btnSpaForTwo
        };

        for (Button button : menuButtons) {
            if (button != null) button.setOnClickListener(menuItemClickListener);
        }
    }

    @Override
    public void onBackPressed() {
        if (!selectedItems.isEmpty()) {
            String order = "Заказ СПА: " + String.join(", ", selectedItems);
            Intent resultIntent = new Intent();
            resultIntent.putExtra("final_message", order);
            setResult(RESULT_OK, resultIntent);
            Toast.makeText(this, "Заказ СПА готов к редактированию в чате", Toast.LENGTH_SHORT).show();
        } else {
            setResult(RESULT_CANCELED);
        }
        super.onBackPressed();
    }

    private void setupScrollViewListeners() {
        menuScrollView.setOnScrollChangeListener((NestedScrollView.OnScrollChangeListener) (v, scrollX, scrollY, oldScrollX, oldScrollY) -> {
            updateActiveCategoryButton(scrollY);
        });
    }

    private void setActiveButton(Button newActiveButton) {
        if (currentActiveButton != null && currentActiveButton != newActiveButton) {
            currentActiveButton.setBackgroundResource(R.drawable.rounded_light_button);
            currentActiveButton.setTextColor(Color.BLACK);
        }

        newActiveButton.setBackgroundResource(R.drawable.rounded_dark_button);
        newActiveButton.setTextColor(Color.BLACK); // Установите нужный цвет
        currentActiveButton = newActiveButton;

        categoriesScrollView.post(() -> {
            categoriesScrollView.smoothScrollTo(newActiveButton.getLeft() - (categoriesScrollView.getWidth() / 2) + (newActiveButton.getWidth() / 2), 0);
        });
    }

    private void scrollToSection(Button targetButton) {
        TextView targetSection = categoryMap.get(targetButton);
        if (targetSection != null) {
            menuScrollView.post(() -> menuScrollView.smoothScrollTo(0, targetSection.getTop()));
            setActiveButton(targetButton);
        }
    }

    private void updateActiveCategoryButton(int scrollY) {
        // Логика остается такой же, как в FoodDrinksActivity
        Button newlyActiveButton = null;
        int smallestDistance = Integer.MAX_VALUE;

        for (Map.Entry<Button, TextView> entry : categoryMap.entrySet()) {
            TextView section = entry.getValue();
            int sectionTop = section.getTop();
            int sectionBottom = section.getBottom();

            if (scrollY >= sectionTop && scrollY < sectionBottom) {
                newlyActiveButton = entry.getKey();
                break;
            } else if (scrollY < sectionTop) {
                int distance = sectionTop - scrollY;
                if (distance < smallestDistance) {
                    smallestDistance = distance;
                    newlyActiveButton = entry.getKey();
                }
            }
        }

        if (newlyActiveButton != null && newlyActiveButton != currentActiveButton) {
            setActiveButton(newlyActiveButton);
        }
    }
}