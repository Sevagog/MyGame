package ru.sindeev.mygame;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.view.View;
import android.widget.Button;

public class MainActivity extends AppCompatActivity {

    // Добавлям кнопки
    Button StartButton;
    Button ExitButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Соединяем "воображаемые" кнопки с "настоящими"
        StartButton = findViewById(R.id.PlayButton);
        ExitButton = findViewById(R.id.ExitButton);

        // Обработка нажатия (Выход)
        ExitButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AreYouSure();
            }
        });

        // Обработка нажатия (Старт)
        StartButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlmostTheBeginning();
            }
        });
    }

    // Переход к следующей активности
    private void AlmostTheBeginning (){
        Intent intent = new Intent(this, AlmostTheBeginning.class);
        startActivity(intent);
    }

    // Запуск диалога о выходе (Ожидаем ответа)
    private void AreYouSure (){
        Intent intent = new Intent(this, AreYouSure.class);
        startActivityForResult(intent, 0);
    }

    // Проверяем, есть ли ответ
    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK && data != null){
            if (requestCode == 0) {
                finish();
            }
        }
    }
}

// Список вопросов:
//
// Как масштабировать Bitmap отрисовку?
// Как добавлять неограниченное кол-во врагов?
// Как сделать джойстик проще?
// Кнопки тоже через спрайты делать?