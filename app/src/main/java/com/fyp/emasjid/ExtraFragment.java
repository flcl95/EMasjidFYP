package com.fyp.emasjid;

import android.content.Intent;
import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.cardview.widget.CardView;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.github.ksoichiro.android.observablescrollview.ObservableScrollView;

public class ExtraFragment extends Fragment {//implements ObservableScrollViewCallbacks {

    private View view;
    private CardView adzanCard, qiblaCard, quranCard;

    @Nullable
    @Override
    public View onCreateView (@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        view = view = inflater.inflate(R.layout.fragment_extra,container,false);
        adzanCard = (CardView) view.findViewById(R.id.card_adzan);
        qiblaCard = (CardView) view.findViewById(R.id.card_qibla);
        quranCard = (CardView) view.findViewById(R.id.card_quran);


        adzanCard.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getActivity(), AdzanActivity.class);
                startActivity(intent);
            }
        });

        qiblaCard.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getActivity(), QiblaActivity.class);
                startActivity(intent);
            }
        });

        quranCard.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getActivity(), QuranActivity.class);
                startActivity(intent);
            }
        });

        return view;
    }
}

