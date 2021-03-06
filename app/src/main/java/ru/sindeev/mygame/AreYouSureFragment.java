package ru.sindeev.mygame;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

public class AreYouSureFragment extends Fragment {

    Button YesButton;
    Button NoButton;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_are_you_sure, container, false);

        YesButton = view.findViewById(R.id.YesButton);
        NoButton = view.findViewById(R.id.NoButton);

        YesButton.setOnClickListener((View v) -> getActivity().finish());

        NoButton.setOnClickListener((View v) -> {
            FragmentManager fragmentManager = getFragmentManager();
            fragmentManager.popBackStack();
        });

        return view;
    }
}