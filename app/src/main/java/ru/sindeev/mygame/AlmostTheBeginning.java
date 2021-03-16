package ru.sindeev.mygame;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

public class AlmostTheBeginning extends AppCompatActivity {

    // Добавлям кнопки
    Button StartButton;
    Button ExitButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_almost_the_beginning);

        // Соединяем "воображаемые" кнопки с "настоящими"
        StartButton = findViewById(R.id.PlayButton);
        ExitButton = findViewById(R.id.ExitButton);

        // Обработка нажатия (Выход)
        ExitButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        // Обработка нажатия (Старт)
        StartButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Game();
            }
        });

    }

    // Переход к следующей активности
    private void Game() {
        Intent intent = new Intent(this, Game.class);
        startActivity(intent);
    }
}