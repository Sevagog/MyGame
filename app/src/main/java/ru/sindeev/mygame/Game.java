package ru.sindeev.mygame;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;

public class Game extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(new GameView(this));
    }
}