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
import android.widget.Button;

public class MainFragment extends Fragment {

    // Добавлям кнопки
    Button StartButton;
    Button ExitButton;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_main, container, false);

        // Соединяем "воображаемые" кнопки с "настоящими"
        StartButton = view.findViewById(R.id.PlayButton);
        ExitButton = view.findViewById(R.id.ExitButton);

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

        return view;
    }

    private void AlmostTheBeginning (){
        FragmentManager fragmentManager = getFragmentManager();
        fragmentManager.beginTransaction()
                .replace(R.id.frame, new AlmostTheBeginningFragment(), "AlmostTheBeginningFragment")
                .addToBackStack(null)
                .commit();
    }

    private void AreYouSure (){
        FragmentManager fragmentManager = getFragmentManager();
        fragmentManager.beginTransaction()
                .replace(R.id.frame, new AreYouSureFragment(), "AreYouSureFragment")
                .addToBackStack(null)
                .commit();
    }
}