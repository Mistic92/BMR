package com.devnoobs.bmr.Fragments;

import android.app.Fragment;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.devnoobs.bmr.Baza.WynikiDataSource;
import com.devnoobs.bmr.R;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;

/**
 * Created by Lukasz on 2015-04-15.
 */
public class FragmentWyniki extends Fragment {

    private int fragVal;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        fragVal = getArguments() != null ? getArguments().getInt("val") : 2;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        View rootView;

        rootView = inflater.inflate(R.layout.fragment_wyniki, container, false);


        return rootView;

       // return super.onCreateView(inflater, container, savedInstanceState);
    }
}
