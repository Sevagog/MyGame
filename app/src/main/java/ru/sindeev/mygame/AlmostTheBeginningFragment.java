package ru.sindeev.mygame;

import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.List;

public class AlmostTheBeginningFragment extends Fragment {

    // Добавлям кнопки
    Button StartButton;
    Button Player;
    Button Difficulty;
    Button ExitButton;

    TextView PlayerName;
    ImageView PlayerImage;

    boolean isPlayable1 = true;
    boolean isPlayable2 = true;
    int player = 0;
    int difficulty = 4;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_almost_the_beginning, container, false);

        // Соединяем "воображаемые" кнопки с "настоящими"
        StartButton = view.findViewById(R.id.PlayButton);
        Player = view.findViewById(R.id.Character);
        Difficulty = view.findViewById(R.id.Difficulty);
        ExitButton = view.findViewById(R.id.ExitButton);
        PlayerName = view.findViewById(R.id.PlayerName);
        PlayerImage = view.findViewById(R.id.PlayerImage);

        // Обработка нажатия (Старт)
        StartButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (isPlayable1 && isPlayable2) {
                    Intent intent = new Intent(getActivity(), Game.class);
                    startActivity(intent);
                }
            }
        });

        Difficulty.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                difficulty += 1;

                switch (difficulty % 5){
                    case 0:
                        Difficulty.setText("Сложность: Минимальная");
                        isPlayable2 = false;
                        StartButton.getBackground().setAlpha(100);
                        break;
                    case 1:
                        Difficulty.setText("Сложность: Легкая");
                        isPlayable2 = false;
                        StartButton.getBackground().setAlpha(100);
                        break;
                    case 2:
                        Difficulty.setText("Сложность: Средняя");
                        isPlayable2 = false;
                        StartButton.getBackground().setAlpha(100);
                        break;
                    case 3:
                        Difficulty.setText("Сложность: Сложная");
                        isPlayable2 = false;
                        StartButton.getBackground().setAlpha(100);
                        break;
                    case 4:
                        Difficulty.setText("Сложность: Максимальная");
                        isPlayable2 = true;
                        if (isPlayable1) {
                            StartButton.getBackground().setAlpha(255);
                        }
                        break;
                }
            }
        });

        Player.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                player += 1;

                switch (player % 2){
                    case 0:
                        Player.setText("Персонаж: Фур");
                        PlayerName.setText("Персонаж: Фур");
                        PlayerImage.setImageResource(R.drawable.fur);
                        isPlayable1 = true;
                        if (isPlayable2) {
                            StartButton.getBackground().setAlpha(255);
                        }
                        break;
                    case 1:
                        Player.setText("Персонаж: Аман");
                        PlayerName.setText("Персонаж: Аман");
                        PlayerImage.setImageResource(R.drawable.amans);
                        isPlayable1 = false;
                        StartButton.getBackground().setAlpha(100);
                        break;
                }
            }
        });

        // Обработка нажатия (Выход)
        ExitButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FragmentManager fragmentManager = getFragmentManager();
                fragmentManager.popBackStack();
            }
        });
        return view;
    }
}