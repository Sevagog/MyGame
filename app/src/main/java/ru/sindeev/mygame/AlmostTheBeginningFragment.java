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

import java.util.List;

public class AlmostTheBeginningFragment extends Fragment {

    // Добавлям кнопки
    Button StartButton;
    Button ExitButton;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_almost_the_beginning, container, false);

        // Соединяем "воображаемые" кнопки с "настоящими"
        StartButton = view.findViewById(R.id.PlayButton);
        ExitButton = view.findViewById(R.id.ExitButton);

        // Обработка нажатия (Выход)
        ExitButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FragmentManager fragmentManager = getFragmentManager();
                fragmentManager.popBackStack();
            }
        });

        // Обработка нажатия (Старт)
        StartButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Game();
            }
        });
        return view;
    }

    // Переход к следующей активности
    private void Game() {
        Intent intent = new Intent(getActivity(), Game.class);
        startActivity(intent);
    }
}