package com.example.concierge;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.widget.NestedScrollView;
import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.HorizontalScrollView;
import android.widget.ImageButton; // Добавлен импорт для кнопки назад
import android.widget.TextView;
import android.widget.Toast; // Добавлен импорт для всплывающих уведомлений

import java.util.HashMap;
import java.util.Map;

public class FoodDrinksActivity extends AppCompatActivity {
    private NestedScrollView menuScrollView;
    private HorizontalScrollView categoriesScrollView;

    // Кнопка "Назад"
    private ImageButton btnBackCatalog; // ID: btn_back_catalog

    // Кнопки категорий (существующие)
    private Button btnBreakfast;
    private Button btnMainCourses;
    private Button btnDesserts;
    private Button btnHotDrinks;
    private Button btnJuiceWater;
    private Button btnAlcohol;

    // Кнопки элементов меню (новые для кликабельности)
    private Button btnContinentalBreakfast; // Завтраки
    private Button btnEnglishBreakfast;     // Завтраки
    private Button btnHealthyBreakfast;     // Завтраки
    private Button btnRusBreakfast;         // Завтраки

    private Button btnRibeyeSteak;          // Основные блюда
    private Button btnSalmonPotatoes;       // Основные блюда
    private Button btnKarbonara;            // Основные блюда
    private Button btnPotatoesWithChiken;   // Основные блюда

    private Button btnTiramicy;             // Десерты
    private Button btnChocolateFondan;      // Десерты
    private Button btnCheesecake;           // Десерты

    private Button btnLatte; //Горячие напитки
    private Button btnCappuccino;  //Горячие напитки
    private Button btnEspresso; //Горячие напитки
    private Button btnRaf; //Горячие напитки
    private Button btnBlTea; //Горячие напитки
    private Button btnGrTea; //Горячие напитки
    private Button btnEgTea; //Горячие напитки

    private Button btnOrJuice; //Соки и вода
    private Button btnApJuice; //Соки и вода
    private Button btnNoncarWater; //Соки и вода
    private Button btnCarWater; //Соки и вода

    private Button btnChampBrut; //Алкоголь
    private Button btnWinePros; //Алкоголь
    private Button btnWinePingr; //Алкоголь
    private Button btnWineCabsauv; //Алкоголь
    private Button btnWinePnoir; //Алкоголь
    private Button btnAperolspr; //Алкоголь
    private Button btnMojito; //Алкоголь
    private Button btnFrench; //Алкоголь



    // Заголовки секций (существующие)
    private TextView sectionBreakfast;
    private TextView sectionMainCourses;
    private TextView sectionDesserts;
    private TextView sectionHotDrinks;
    private TextView sectionJuiceWater;
    private TextView sectionAlcohol;

    private Map<Button, TextView> categoryMap;
    private Button currentActiveButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_fooddrink);

        initViews();
        initCategoryMap();
        setupCategoryClickListeners();
        setupMenuScrollListener();

        // Настройка новых клик-слушателей
        setupBackNavigation();
        setupMenuItemClickListeners();
    }

    private void initViews() {
        // Инициализация существующих элементов
        menuScrollView = findViewById(R.id.menu_scroll_view);
        categoriesScrollView = findViewById(R.id.categories_scroll);

        btnBreakfast = findViewById(R.id.btn_breakfast);
        btnMainCourses = findViewById(R.id.btn_main_courses);
        btnDesserts = findViewById(R.id.btn_desserts);
        btnHotDrinks = findViewById(R.id.btn_hot_drinks);
        btnJuiceWater = findViewById(R.id.btn_juice_water);
        btnAlcohol = findViewById(R.id.btn_alcohol);

        sectionBreakfast = findViewById(R.id.section_breakfast);
        sectionMainCourses = findViewById(R.id.section_main_courses);
        sectionDesserts = findViewById(R.id.section_desserts);
        sectionHotDrinks = findViewById(R.id.section_hot_drinks);
        sectionJuiceWater = findViewById(R.id.section_juice_water);
        sectionAlcohol = findViewById(R.id.section_alcohol);

        // Инициализация кнопки "Назад"
        btnBackCatalog = findViewById(R.id.btn_back_catalog);

        // Инициализация кнопок элементов меню
        btnContinentalBreakfast = findViewById(R.id.btn_continental_breakfast);
        btnEnglishBreakfast = findViewById(R.id.btn_english_breakfast);
        btnHealthyBreakfast = findViewById(R.id.btn_healthy_breakfast);
        btnRusBreakfast = findViewById(R.id.btn_rus_breakfast);

        btnRibeyeSteak = findViewById(R.id.btn_ribeye_steak);
        btnSalmonPotatoes = findViewById(R.id.btn_salmon_potatoes);
        btnKarbonara = findViewById(R.id.btn_karbonara);
        btnPotatoesWithChiken = findViewById(R.id.btn_potatoes_with_chiken);

        btnTiramicy = findViewById(R.id.btn_tiramicy);
        btnChocolateFondan = findViewById(R.id.btn_chocolate_fondan);
        btnCheesecake = findViewById(R.id.btn_cheesecake);

        btnLatte = findViewById(R.id.btn_latte);
        btnCappuccino = findViewById(R.id.btn_cappuccino);
        btnEspresso = findViewById(R.id.btn_espresso);
        btnRaf = findViewById(R.id.btn_raf);
        btnBlTea = findViewById(R.id.btn_bl_tea);
        btnGrTea = findViewById(R.id.btn_gr_tea);
        btnEgTea = findViewById(R.id.btn_eg_tea);

        btnOrJuice = findViewById(R.id.btn_or_juice);
        btnApJuice = findViewById(R.id.btn_ap_juice);
        btnNoncarWater = findViewById(R.id.btn_noncar_water);
        btnCarWater = findViewById(R.id.btn_car_water);

        btnChampBrut = findViewById(R.id.btn_champ_brut);
        btnWinePros = findViewById(R.id.btn_wine_pros);
        btnWinePingr = findViewById(R.id.btn_wine_pingr);
        btnWineCabsauv = findViewById(R.id.btn_wine_cabsauv);
        btnWinePnoir = findViewById(R.id.btn_wine_pnoir);
        btnAperolspr = findViewById(R.id.btn_aperolspr);
        btnMojito = findViewById(R.id.btn_mojito);
        btnFrench = findViewById(R.id.btn_french);
    }

    private void setupBackNavigation() {
        btnBackCatalog.setOnClickListener(v -> {
            finish(); // Закрывает текущую Activity и возвращается к предыдущей (CatalogActivity)
        });
    }

    private void setupMenuItemClickListeners() {
        View.OnClickListener menuItemClickListener = v -> {
            Button button = (Button) v;
            String itemName = button.getText().toString();
            Toast.makeText(this, "Выбран заказ: " + itemName, Toast.LENGTH_SHORT).show();
            // Здесь можно добавить логику для добавления товара в корзину
        };

        btnContinentalBreakfast.setOnClickListener(menuItemClickListener);
        btnEnglishBreakfast.setOnClickListener(menuItemClickListener);
        btnHealthyBreakfast.setOnClickListener(menuItemClickListener);
        btnRusBreakfast.setOnClickListener(menuItemClickListener);

        btnRibeyeSteak.setOnClickListener(menuItemClickListener);
        btnSalmonPotatoes.setOnClickListener(menuItemClickListener);
        btnKarbonara.setOnClickListener(menuItemClickListener);
        btnPotatoesWithChiken.setOnClickListener(menuItemClickListener);

        btnTiramicy.setOnClickListener(menuItemClickListener);
        btnChocolateFondan.setOnClickListener(menuItemClickListener);
        btnCheesecake.setOnClickListener(menuItemClickListener);

        btnLatte.setOnClickListener(menuItemClickListener);
        btnCappuccino.setOnClickListener(menuItemClickListener);
        btnEspresso.setOnClickListener(menuItemClickListener);
        btnRaf.setOnClickListener(menuItemClickListener);
        btnBlTea.setOnClickListener(menuItemClickListener);
        btnGrTea.setOnClickListener(menuItemClickListener);
        btnEgTea.setOnClickListener(menuItemClickListener);

        btnOrJuice.setOnClickListener(menuItemClickListener);
        btnApJuice.setOnClickListener(menuItemClickListener);
        btnNoncarWater.setOnClickListener(menuItemClickListener);
        btnCarWater.setOnClickListener(menuItemClickListener);

        btnChampBrut.setOnClickListener(menuItemClickListener);
        btnWinePros.setOnClickListener(menuItemClickListener);
        btnWinePingr.setOnClickListener(menuItemClickListener);
        btnWineCabsauv.setOnClickListener(menuItemClickListener);
        btnWinePnoir.setOnClickListener(menuItemClickListener);
        btnAperolspr.setOnClickListener(menuItemClickListener);
        btnMojito.setOnClickListener(menuItemClickListener);
        btnFrench.setOnClickListener(menuItemClickListener);
    }

    private void initCategoryMap() {
        categoryMap = new HashMap<>();
        categoryMap.put(btnBreakfast, sectionBreakfast);
        categoryMap.put(btnMainCourses, sectionMainCourses);
        categoryMap.put(btnDesserts, sectionDesserts);
        categoryMap.put(btnHotDrinks, sectionHotDrinks);
        categoryMap.put(btnJuiceWater, sectionJuiceWater);
        categoryMap.put(btnAlcohol, sectionAlcohol);

        // Устанавливаем активной первую кнопку при старте
        currentActiveButton = btnBreakfast;
        setActiveButton(btnBreakfast);
    }

    private void setupCategoryClickListeners() {
        for (Map.Entry<Button, TextView> entry : categoryMap.entrySet()) {
            Button button = entry.getKey();
            TextView section = entry.getValue();

            button.setOnClickListener(v -> {
                setActiveButton(button);
                // Прокрутка к началу соответствующей секции с небольшой поправкой
                menuScrollView.post(() -> menuScrollView.smoothScrollTo(0, section.getTop()));
            });
        }
    }

    private void setupMenuScrollListener() {
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
        newActiveButton.setTextColor(Color.WHITE);
        currentActiveButton = newActiveButton;

        categoriesScrollView.post(() -> {
            // Центрирование активной кнопки в HorizontalScrollView
            categoriesScrollView.smoothScrollTo(newActiveButton.getLeft() - (categoriesScrollView.getWidth() / 2) + (newActiveButton.getWidth() / 2), 0);
        });
    }

    private void updateActiveCategoryButton(int scrollY) {
        Button newlyActiveButton = null;
        int smallestDistance = Integer.MAX_VALUE;

        for (Map.Entry<Button, TextView> entry : categoryMap.entrySet()) {
            TextView section = entry.getValue();
            int sectionTop = section.getTop();
            int sectionBottom = section.getBottom();

            // Если прокрутка находится внутри секции
            if (scrollY >= sectionTop && scrollY < sectionBottom) {
                newlyActiveButton = entry.getKey();
                break;
            } else if (scrollY < sectionTop) {
                // Если прокрутка еще не дошла до секции, выбираем ближайшую сверху
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

    // Обработка системной кнопки "Назад"
    @Override
    public void onBackPressed() {
        super.onBackPressed();
    }
}