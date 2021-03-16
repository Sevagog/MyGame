package ru.sindeev.mygame;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;

public class Game extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // Рисовать будем не Активити. Этим будет заниматься другой класс.
        setContentView(new GameView(this));
    }
}