package com.example.concierge;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.widget.NestedScrollView;

import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.HorizontalScrollView;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;
import android.content.Intent;

import com.example.concierge.data.local.entity.Guest;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class TransferActivity extends AppCompatActivity {
    private NestedScrollView menuScrollView;
    private HorizontalScrollView categoriesScrollView;

    private ImageButton btnBackCatalog;
    private Guest currentGuest;

    // Кнопки категорий
    private Button btnEkonom;
    private Button btnKomfort;
    private Button btnBicness;

    // Кнопки элементов меню
    private Button btnStandtrans;
    private Button btnGrouptrans;
    private Button btnKomfsedan;
    private Button btnMinivan;
    private Button btnBissedan;
    private Button btnVipsedan;

    private List<String> selectedItems = new ArrayList<>();
    private Map<Button, TextView> categoryMap = new HashMap<>();
    private Map<String, Button> itemButtonMap = new HashMap<>();
    private Button currentActiveButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_transfer);

        currentGuest = (Guest) getIntent().getSerializableExtra("guest");

        initViews();
        setupClickListeners();
        setupMenuItemClickListeners();
        setupScrollViewListeners();

        loadInitialOrder(getIntent().getStringExtra("initial_order_text"));
        setActiveButton(btnEkonom);
    }

    private void initViews() {
        btnBackCatalog = findViewById(R.id.sendButton); // Исправлен ID
        menuScrollView = findViewById(R.id.menu_scroll_view);
        categoriesScrollView = findViewById(R.id.categories_scroll);

        // Кнопки категорий
        btnEkonom = findViewById(R.id.btn_ekonom);
        btnKomfort = findViewById(R.id.btn_komfort);
        btnBicness = findViewById(R.id.btn_bicness);

        // Привязка кнопок категорий к их заголовкам (TextView)
        categoryMap.put(btnEkonom, findViewById(R.id.ekklass));
        categoryMap.put(btnKomfort, findViewById(R.id.business));
        categoryMap.put(btnBicness, findViewById(R.id.vipbisn));

        initMenuItemButtonsAndMap();
    }

    private void initMenuItemButtonsAndMap() {
        // Эконом-класс
        btnStandtrans = findViewById(R.id.btn_standtrans);
        btnGrouptrans = findViewById(R.id.btn_grouptrans);

        // Комфорт-класс
        btnKomfsedan = findViewById(R.id.btn_komfsedan);
        btnMinivan = findViewById(R.id.btn_minivan);

        // Бизнес/VIP-класс
        btnBissedan = findViewById(R.id.btn_bissedan);
        btnVipsedan = findViewById(R.id.btn_vipsedan);

        // Заполняем карту для поиска кнопок по тексту
        if (btnStandtrans != null) itemButtonMap.put(btnStandtrans.getText().toString(), btnStandtrans);
        if (btnGrouptrans != null) itemButtonMap.put(btnGrouptrans.getText().toString(), btnGrouptrans);
        if (btnKomfsedan != null) itemButtonMap.put(btnKomfsedan.getText().toString(), btnKomfsedan);
        if (btnMinivan != null) itemButtonMap.put(btnMinivan.getText().toString(), btnMinivan);
        if (btnBissedan != null) itemButtonMap.put(btnBissedan.getText().toString(), btnBissedan);
        if (btnVipsedan != null) itemButtonMap.put(btnVipsedan.getText().toString(), btnVipsedan);
    }

    private void loadInitialOrder(String initialText) {
        if (initialText == null || initialText.isEmpty()) {
            return;
        }

        String prefix = "Заказ трансфера: ";
        if (initialText.startsWith(prefix)) {
            String itemsString = initialText.substring(prefix.length()).trim();

            if (itemsString.isEmpty()) {
                return;
            }

            List<String> parsedItems = Arrays.asList(itemsString.split(",\\s*"));

            for (String item : parsedItems) {
                String itemName = item.trim();
                if (!itemName.isEmpty()) {
                    selectedItems.add(itemName);
                    Button button = itemButtonMap.get(itemName);
                    if (button != null) {
                        setButtonSelected(button, true);
                    }
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

        btnEkonom.setOnClickListener(v -> scrollToSection(btnEkonom));
        btnKomfort.setOnClickListener(v -> scrollToSection(btnKomfort));
        btnBicness.setOnClickListener(v -> scrollToSection(btnBicness));
    }

    private void setupMenuItemClickListeners() {
        View.OnClickListener menuItemClickListener = v -> {
            Button button = (Button) v;
            String itemName = button.getText().toString().trim();

            if (selectedItems.contains(itemName)) {
                selectedItems.remove(itemName);
                setButtonSelected(button, false);
                Toast.makeText(this, itemName + " удален из заказа", Toast.LENGTH_SHORT).show();
            } else {
                selectedItems.add(itemName);
                setButtonSelected(button, true);
                Toast.makeText(this, itemName + " добавлен в заказ", Toast.LENGTH_SHORT).show();
            }
        };

        Button[] menuButtons = {
                btnStandtrans, btnGrouptrans,
                btnKomfsedan, btnMinivan,
                btnBissedan, btnVipsedan
        };

        for (Button button : menuButtons) {
            if (button != null) {
                button.setOnClickListener(menuItemClickListener);
            }
        }
    }

    @Override
    public void onBackPressed() {
        if (!selectedItems.isEmpty()) {
            String order = "Заказ трансфера: " + String.join(", ", selectedItems);
            Intent resultIntent = new Intent();
            resultIntent.putExtra("final_message", order);
            setResult(RESULT_OK, resultIntent);
            Toast.makeText(this, "Заказ готов к редактированию в чате", Toast.LENGTH_SHORT).show();
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
        newActiveButton.setTextColor(Color.BLACK);
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