/*
 * Copyright (c) 2014 Łukasz Byjoś
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy of this software and associated documentation files (the "Software"), to deal in the Software without restriction, including without limitation the rights to use, copy, modify, merge, publish, distribute, sublicense, and/or sell copies of the Software, and to permit persons to whom the Software is furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.
 */

package com.devnoobs.bmr.Fragments;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.Fragment;
import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.graphics.Color;
import android.graphics.Typeface;
import android.os.Bundle;
import android.support.v7.widget.CardView;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RadioGroup;
import android.widget.RadioGroup.OnCheckedChangeListener;
import android.widget.TextView;
import android.widget.Toast;

import com.devnoobs.bmr.Baza.WynikiDataSource;
import com.devnoobs.bmr.Dane;
import com.devnoobs.bmr.Kalkulator;
import com.devnoobs.bmr.R;

public class FragmentBMR extends Fragment implements TextWatcher,
        OnCheckedChangeListener, OnClickListener {

    private Fragment mFragment;
    WynikiDataSource wds;
    EditText pole_wiek;// = (EditText) findViewById(R.id.EditTextWiek);
    EditText pole_waga;// = (EditText) findViewById(R.id.EditTextWaga);
    EditText pole_wzrost;// = (EditText) findViewById(R.id.EditTextWzrost);
    RadioGroup radioGroupPlec;
    RadioGroup radioGroupAktywnosc;
    // dodawanie wyniku przycisk
    private Button dodaj_wynik;
    private EditText bmi;
    private EditText waga;
    private EditText notatka;
    public boolean imperial = false;
    private final double mnoznik_imperial = 703.06957964;

    // Obiekty pol w tabeli pokazywania wynikow. Odpowiednio kcal i gramy
    TextView text_wynik_bmi;
    TextView text_wynik_bmr;
    TextView text_kalorie;
    TextView text_bialko;
    TextView text_tluszcz;
    TextView text_weglowodany;
    TextView text_bialko_gram;
    TextView text_tluszcz_gram;
    TextView text_weglowodany_gram;
    // ////////////////////////////////////////
    // Wybor czy ustawiac layout z dopiskiem kcal i gram czy nie w zaleznosci od
    // szerokosci ekranu urzadzenia.
    private Configuration config;// = getResources().getConfiguration();
    // private Point size = new Point();
    private int szerokosc;

    private SharedPreferences sharedPref;
    Kalkulator k = new Kalkulator();
    Dane d = new Dane();
    private int indeksAktywnosc = -1;
    private int indeksPlci = -1;
    private int fragVal;

    // stale jako minimalne i maksymalne wartosci dla jakich program wykonuje
    // obliczenia i zapisuje dane
    private static final int maxWiek = 200;
    private static final int maxWaga = 400;
    private static final int maxWzrost = 250;
    private static final int minWiek = 0;
    private static final int minWaga = 20;
    private static final int minWzrost = 20;

    private static final int[] tablicaIndeksowPolBMI = {
            R.id.textBMIZnaczenie1, R.id.textBMIZnaczenie2,
            R.id.textBMIZnaczenie3, R.id.textBMIZnaczenie4,
            R.id.textBMIZnaczenie5, R.id.textBMIZnaczenie6,
            R.id.textBMIZnaczenie7, R.id.textBMIZnaczenie8};

    private TextView[] tablicaPolBMI = new TextView[8];

    private LinearLayout linearBrakDanych;// = (LinearLayout) getActivity().findViewById(R.id
    // .linearLayoutBrakuDanychWynikow);
    private LinearLayout linearWynikow;// = (LinearLayout) getActivity().findViewById(R.id
    // .linearLayoutWynikow);

    public static FragmentBMR init(int val) {
        FragmentBMR truitonFrag = new FragmentBMR();
        // Supply val input as an argument.
        Bundle args = new Bundle();
        args.putInt("val", val);
        truitonFrag.setArguments(args);
        return truitonFrag;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        fragVal = getArguments() != null ? getArguments().getInt("val") : 1;
    }

	/*
     * @Override public void onResume() { super.onResume(); Tracker tracker =
	 * GoogleAnalytics.getInstance(getActivity()).getTracker("UA-18780283-6");
	 * tracker.send(MapBuilder.createAppView().set(Fields.SCREEN_NAME,
	 * this.getClass().getSimpleName()).build()); }
	 */

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        config = getResources().getConfiguration();
        View rootView;
        szerokosc = config.smallestScreenWidthDp;
        rootView = inflater.inflate(R.layout.fragment_bmr, container, false);

        wds = new WynikiDataSource(getActivity());

        try {
            sharedPref = getActivity().getSharedPreferences(
                    getString(R.string.appPreferences),
                    getActivity().MODE_PRIVATE);
        } catch (Exception e) {
            Log.e("com.devnoobs.bmr", "", e.fillInStackTrace());
        }

        this.linearBrakDanych = (LinearLayout) rootView.findViewById(R.id.linearLayoutBrakuDanychWynikow);
        this.linearWynikow = (LinearLayout) rootView.findViewById(R.id.linearLayoutWynikow);

        pole_wiek = (EditText) rootView.findViewById(R.id.EditTextWiek);
        pole_waga = (EditText) rootView.findViewById(R.id.EditTextWaga);
        pole_wzrost = (EditText) rootView.findViewById(R.id.EditTextWzrost);
        radioGroupPlec = (RadioGroup) rootView
                .findViewById(R.id.radioGroupPlec);
        radioGroupAktywnosc = (RadioGroup) rootView
                .findViewById(R.id.radioGroupAktywnosci);
        text_wynik_bmi = (TextView) rootView.findViewById(R.id.textWynikBMI);
        text_wynik_bmr = (TextView) rootView.findViewById(R.id.textWynikBMR);
        text_kalorie = (TextView) rootView.findViewById(R.id.textKalorie);
        text_bialko = (TextView) rootView.findViewById(R.id.textBialko);
        text_tluszcz = (TextView) rootView.findViewById(R.id.textTluszcz);
        text_weglowodany = (TextView) rootView
                .findViewById(R.id.textWeglowodany);
        text_bialko_gram = (TextView) rootView
                .findViewById(R.id.textBialkoGram);
        text_tluszcz_gram = (TextView) rootView
                .findViewById(R.id.textTluszczGram);
        text_weglowodany_gram = (TextView) rootView
                .findViewById(R.id.textWeglowodanyGram);
        dodaj_wynik = (Button) rootView.findViewById(R.id.buttonDodajWynikWBMR);

        // Pobieranie wartosci z przed wylaczenia programu i ich ustawianie w
        // polach
        try {
            imperial = sharedPref.getBoolean(getString(R.string.imperial),
                    false);
            // ustawienie hintow przez metode
            ustawienieHint(imperial);

            double waga = sharedPref.getFloat(getString(R.string.shared_waga),
                    0);
            waga = Math.round(waga * 100.0) / 100.0;
            int wiek = sharedPref.getInt(getString(R.string.text_wiek), 0);
            double wzrost = sharedPref.getFloat(
                    getString(R.string.text_wzrost), 0);
            this.indeksPlci = sharedPref.getInt(getString(R.string.text_plec),
                    0);
            this.indeksAktywnosc = sharedPref.getInt(
                    getString(R.string.text_aktywnosc), 0);

            // jezeli waga wieksza od 0 to wartosc jest ustawiana w tym polu
            if ((waga > minWaga) && (waga < maxWaga)) {
                pole_waga.setText(Double.toString(waga));
            } else {
                // jesli nic nie zostanie ustawione to jest ustawiana szerokosc
                // XXX nie wiem nawet czy to dziala :D
                if (szerokosc < 320)
                    pole_waga.getLayoutParams().width = 30;
            }
            if ((wzrost > minWzrost) && (wzrost < maxWzrost)) {
                pole_wzrost.setText(Double.toString(wzrost));
            } else {
                if (szerokosc < 320)
                    pole_wzrost.getLayoutParams().width = 30;
            }
            if ((wiek > minWiek) && (wiek < maxWiek)) {
                pole_wiek.setText(Integer.toString(wiek));
            }
            // switch do ustawiania plci
            switch (indeksPlci) {
                case 0:
                    radioGroupPlec.check(R.id.radio_plec0);
                    break;
                case 1:
                    radioGroupPlec.check(R.id.radio_plec1);
                    break;
                default:
                    radioGroupPlec.check(R.id.radio_plec0);
                    break;
            }

            switch (indeksAktywnosc) {
                case 0:
                    radioGroupAktywnosc.check(R.id.radio0);
                    break;
                case 1:
                    radioGroupAktywnosc.check(R.id.radio1);
                    break;
                case 2:
                    radioGroupAktywnosc.check(R.id.radio2);
                    break;
                case 3:
                    radioGroupAktywnosc.check(R.id.radio3);
                    break;
                case 4:
                    radioGroupAktywnosc.check(R.id.radio4);
                    break;
                default:
                    radioGroupAktywnosc.check(R.id.radio0);
                    break;
            }
            // XXX
            for (int i = 0; i < tablicaIndeksowPolBMI.length; i++) {
                tablicaPolBMI[i] = (TextView) rootView
                        .findViewById(tablicaIndeksowPolBMI[i]);
            }

            if (sprawdzeniePolTekstowych()) {
                oblicz(this.indeksPlci, this.indeksAktywnosc);
            }

        } catch (Exception e) {
            Log.e("Ustawianie starych danych", "t", e.fillInStackTrace());
        }

        // dodanie listenerow. Dodawane po ustawianiu wartosci w polach bo sie
        // niepotrzebnie aktywowaly i sypaly bledami :P
        pole_wiek.addTextChangedListener(this);
        pole_waga.addTextChangedListener(this);
        pole_wzrost.addTextChangedListener(this);
        radioGroupPlec.setOnCheckedChangeListener(this);
        radioGroupAktywnosc.setOnCheckedChangeListener(this);
        dodaj_wynik.setOnClickListener(this);

        // adView = (AdView) rootView.findViewById(R.id.adView);
        // adView.loadAd(new AdRequest());

        return rootView;
    }

    /**
     * Sluzy do ustawiania hintow w polach tekstowych w zaleznosci od ustawien
     * programu
     *
     * @param imperial
     */
    private void ustawienieHint(boolean imperial) {
        // jezeli imperial false to podpowiedz tekstu jest oznaczana jako
        // kilogramy
        if (imperial == false) {
            pole_waga.setHint(R.string.text_kilogramy);
            pole_wzrost.setHint(R.string.text_centymetry);
        } else {
            pole_waga.setHint(R.string.text_kilogramy_funty);
            pole_wzrost.setHint(R.string.text_cale);
        }
    }

    @Override
    public void afterTextChanged(Editable s) {
        if (sprawdzeniePolTekstowych() == true) {
            oblicz(this.indeksPlci, this.indeksAktywnosc);
        }

    }// aftertextchanged

    /**
     * Sprawdzenie warunkow dla obu listenerow. Sprazdzam czy dane sa realne i w
     * zaleznosci od tego zwraca boolean.
     *
     * @return
     */
    private boolean sprawdzeniePolTekstowych() {
        // TODO jesli brak danych to zamiast wynikow pojawia sie tekst o braku
        // wynikow
        boolean wynik = false;
        try {
            SharedPreferences.Editor editor = sharedPref.edit();
            int wiek = 0;
            try {
                wiek = Integer.parseInt(pole_wiek.getText().toString());
            } catch (NumberFormatException e) {
                /*
				 * editor.putInt(getString(R.string.text_wiek), 0);
				 * editor.apply();
				 */
                wynik = false;
            }

            double waga = 0;
            try {
                waga = Double.parseDouble(pole_waga.getText().toString());
            } catch (NumberFormatException e) {
				/*
				 * editor.putFloat(getString(R.string.shared_waga), 0);
				 * editor.apply();
				 */
                wynik = false;
            }

            double wzrost = 0;
            try {
                wzrost = Double.parseDouble(pole_wzrost.getText().toString());
            } catch (NumberFormatException e) {
				/*
				 * editor.putFloat(getString(R.string.text_wzrost), 0);
				 * editor.apply();
				 */
                wynik = false;
            }

			/*
			 * private static final int maxWiek = 200; private static final int
			 * maxWaga = 400; private static final int maxWzrost = 250; private
			 * static final int minWiek = 0; private static final int minWaga =
			 * 20; private static final int minWzrost = 20;
			 */
            int radio_buttonPlec = radioGroupPlec.getCheckedRadioButtonId();
            View rbPlec = radioGroupPlec.findViewById(radio_buttonPlec);
            this.indeksPlci = radioGroupPlec.indexOfChild(rbPlec);

            int radioButtonAktywnosc = radioGroupAktywnosc
                    .getCheckedRadioButtonId();
            View rbAktywnosc = radioGroupAktywnosc
                    .findViewById(radioButtonAktywnosc);
            this.indeksAktywnosc = radioGroupAktywnosc
                    .indexOfChild(rbAktywnosc);

            // this.plec =
            // radio_plec.indexOfChild(getActivity().findViewById(radio_plec
            // .getCheckedRadioButtonId()));
            // this.aktywnosc =
            // radioGroupAktywnosc.indexOfChild(getActivity().findViewById(radioGroupAktywnosc
            // .getCheckedRadioButtonId()));

            if ((wiek < maxWiek && wiek > minWiek)
                    && (waga < maxWaga && waga > minWaga)
                    && (wzrost < maxWzrost && wzrost > minWzrost)
                    && this.indeksPlci >= 0 && this.indeksAktywnosc >= 0) {
                wynik = true;
                // editor.putInt(getString(R.string.text_wiek), 0);
                // editor.putFloat(getString(R.string.shared_waga), 0);
                // editor.putFloat(getString(R.string.text_wzrost), 0);
                // editor.apply();
            } else
                wynik = false;

			/*
			 * if (wiek > maxWiek || waga > maxWaga || wzrost > maxWzrost ||
			 * wiek <= minWiek || waga <= minWaga || wzrost <= minWzrost ||
			 * this.plec < 0 || this.aktywnosc < 0 ) { wynik = false; } else //
			 * jedyne ktore zwroci true wynik = true;
			 */
        } catch (NullPointerException e) {
        } catch (NumberFormatException e) {
        } catch (Exception e) {
            Log.e("com.devnoobs.bmr.fragmentBMR", "Exception",
                    e.fillInStackTrace());
        }

        if (wynik == false) {
            linearBrakDanych.setVisibility(LinearLayout.VISIBLE);
            linearWynikow.setVisibility(LinearLayout.GONE);
        } else {
            linearBrakDanych.setVisibility(LinearLayout.GONE);
            linearWynikow.setVisibility(LinearLayout.VISIBLE);
        }

        return wynik;

    }

    @Override
    public void beforeTextChanged(CharSequence s, int start, int count,
                                  int after) {
    }

    @Override
    public void onTextChanged(CharSequence s, int start, int before, int count) {
    }// ontextchanged

    @Override
    public void onCheckedChanged(RadioGroup arg0, int arg1) {
        if (sprawdzeniePolTekstowych() == true) {
            oblicz(this.indeksPlci, this.indeksAktywnosc);
        }

    }// oncheckedchanged

    /**
     * @param id_plec
     * @param id_aktywnosc
     * @return
     */

    @SuppressLint("CommitPrefEdits")
    private void oblicz(int id_plec, int id_aktywnosc) {
        try {
            double wiek = Integer.parseInt(pole_wiek.getText().toString());
            double waga = Double.parseDouble(pole_waga.getText().toString());
            double wzrost = Double
                    .parseDouble(pole_wzrost.getText().toString());

            SharedPreferences.Editor editor = sharedPref.edit();
            try {
                editor.putFloat(getString(R.string.shared_waga), (float) waga);
                editor.putInt(getString(R.string.text_wiek), (int) wiek);
                editor.putFloat(getString(R.string.text_wzrost), (int) wzrost);
                editor.putInt(getString(R.string.text_plec), id_plec);
                editor.putInt(getString(R.string.text_aktywnosc), id_aktywnosc);
                editor.apply();
            } catch (Exception e) {
                Log.e("com.devnoobs.bmr", "", e.fillInStackTrace());
            }

            double bmr = k.liczBmr(waga, wzrost, wiek, id_plec);
            double kalorie = k.liczKalorie(bmr, id_aktywnosc);
            double weglowodany = k.liczWeglodowany(kalorie);
            double bialko = k.liczBialko(kalorie);
            double tluszcz = k.liczTluszcz(kalorie);
            double weglowodany_gram = k.cal2bialko(weglowodany);
            double bialko_gram = k.cal2bialko(bialko);
            double tluszcz_gram = k.cal2bialko(tluszcz);

            wzrost = wzrost / 100;
            double bmi = (waga / (wzrost * wzrost));
            // w przypadku jednostek imperialnych wynik mnozy sie jeszcze razy
            // 703
            if (imperial == true) {
                bmi *= mnoznik_imperial;
            }
            bmi = Math.round(bmi * 100.0) / 100.0;
            editor.putFloat(getString(R.string.text_bmi), (float) bmi);
            editor.apply();

            zresetujTextBMI();
            TextView twojebmi = null;
			/*
			 * // Reset wygl�du p�l od BMI TextView twojebmi = null;// =
			 * (TextView) // getActivity().findViewById(R.id.radio2); try {
			 * twojebmi = (TextView) getActivity().findViewById(
			 * R.id.textBMIZnaczenie1); twojebmi = zresetujText(twojebmi);
			 * twojebmi = (TextView) getActivity().findViewById(
			 * R.id.textBMIZnaczenie2); twojebmi = zresetujText(twojebmi);
			 * twojebmi = (TextView) getActivity().findViewById(
			 * R.id.textBMIZnaczenie3); twojebmi = zresetujText(twojebmi);
			 * twojebmi = (TextView) getActivity().findViewById(
			 * R.id.textBMIZnaczenie4); twojebmi = zresetujText(twojebmi);
			 * twojebmi = (TextView) getActivity().findViewById(
			 * R.id.textBMIZnaczenie5); twojebmi = zresetujText(twojebmi);
			 * twojebmi = (TextView) getActivity().findViewById(
			 * R.id.textBMIZnaczenie6); twojebmi = zresetujText(twojebmi);
			 * 
			 * twojebmi = (TextView) getActivity().findViewById(
			 * R.id.textBMIZnaczenie7); twojebmi = zresetujText(twojebmi);
			 * twojebmi = (TextView) getActivity().findViewById(
			 * R.id.textBMIZnaczenie8); twojebmi = zresetujText(twojebmi); }
			 * catch (NullPointerException e) { // Log.w("Brak ustawionego bmi",
			 * "com.devnoobs.bmr", // e.fillInStackTrace()); }
			 */
			/*
			 * try { // Ustawienie wygl�du wybranego pola BMI. // Zolty jest w
			 * komentarzu bo byl nieczytelny if (bmi < 16)// < 16,0 �
			 * wyg�odzenie { twojebmi = (TextView) getActivity().findViewById(
			 * R.id.textBMIZnaczenie1);
			 * twojebmi.setTextColor(Color.parseColor("#CC0000")); } else if
			 * (bmi >= 16 && bmi <= 16.99)// 16,0�16,99 � wychudzenie { twojebmi
			 * = (TextView) getActivity().findViewById( R.id.textBMIZnaczenie2);
			 * twojebmi.setTextColor(Color.parseColor("#CC0000")); } else if
			 * (bmi >= 17 && bmi <= 18.49)// 17,0�18,49 � niedowaga { twojebmi =
			 * (TextView) getActivity().findViewById( R.id.textBMIZnaczenie3);
			 * twojebmi.setTextColor(Color.parseColor("#FF8800")); } else if
			 * (bmi >= 18.5 && bmi <= 24.99)// 18,5�24,99 � idealna waga cia�a {
			 * twojebmi = (TextView) getActivity().findViewById(
			 * R.id.textBMIZnaczenie4);
			 * twojebmi.setTextColor(Color.parseColor("#669900")); } else if
			 * (bmi >= 25 && bmi <= 29.99)// 25,0�29,99 � nadwaga { twojebmi =
			 * (TextView) getActivity().findViewById( R.id.textBMIZnaczenie5);
			 * twojebmi.setTextColor(Color.parseColor("#FF8800")); } else if
			 * (bmi >= 30 && bmi <= 34.99)// 30,0�34,99 � I stopie� // oty�o�ci
			 * { twojebmi = (TextView) getActivity().findViewById(
			 * R.id.textBMIZnaczenie6);
			 * twojebmi.setTextColor(Color.parseColor("#FF8800")); } else if
			 * (bmi >= 35 && bmi <= 39.99)// 35,0�39,99 � II stopie� // oty�o�ci
			 * { twojebmi = (TextView) getActivity().findViewById(
			 * R.id.textBMIZnaczenie7);
			 * twojebmi.setTextColor(Color.parseColor("#CC0000")); } else if
			 * (bmi > 40)// > 40,0 � III stopie� oty�o�ci { twojebmi =
			 * (TextView) getActivity().findViewById( R.id.textBMIZnaczenie8);
			 * twojebmi.setTextColor(Color.parseColor("#CC0000")); }
			 */

            ustawieniePolaBMI(bmi);
			/*
			 * try { // Ustawienie wygl�du wybranego pola BMI. // Zolty jest w
			 * komentarzu bo byl nieczytelny if (bmi < 16)// < 16,0 �
			 * wyg�odzenie { twojebmi = tablicaPolBMI[0];
			 * twojebmi.setTextColor(Color.parseColor("#CC0000")); } else if
			 * (bmi >= 16 && bmi <= 16.99)// 16,0�16,99 � wychudzenie { twojebmi
			 * = tablicaPolBMI[1];
			 * twojebmi.setTextColor(Color.parseColor("#CC0000")); } else if
			 * (bmi >= 17 && bmi <= 18.49)// 17,0�18,49 � niedowaga { twojebmi =
			 * tablicaPolBMI[2];
			 * twojebmi.setTextColor(Color.parseColor("#FF8800")); } else if
			 * (bmi >= 18.5 && bmi <= 24.99)// 18,5�24,99 � idealna waga cia�a {
			 * twojebmi = tablicaPolBMI[3];
			 * twojebmi.setTextColor(Color.parseColor("#669900")); } else if
			 * (bmi >= 25 && bmi <= 29.99)// 25,0�29,99 � nadwaga { twojebmi =
			 * tablicaPolBMI[4];
			 * twojebmi.setTextColor(Color.parseColor("#FF8800")); } else if
			 * (bmi >= 30 && bmi <= 34.99)// 30,0�34,99 � I stopie� oty�o�ci {
			 * twojebmi = tablicaPolBMI[5];
			 * twojebmi.setTextColor(Color.parseColor("#FF8800")); } else if
			 * (bmi >= 35 && bmi <= 39.99)// 35,0�39,99 � II stopie� oty�o�ci {
			 * twojebmi = tablicaPolBMI[6];
			 * twojebmi.setTextColor(Color.parseColor("#CC0000")); } else if
			 * (bmi > 40)// > 40,0 � III stopie� oty�o�ci { twojebmi =
			 * tablicaPolBMI[7];
			 * twojebmi.setTextColor(Color.parseColor("#CC0000")); }
			 * 
			 * twojebmi.setTypeface(Typeface.DEFAULT_BOLD); if (szerokosc < 380)
			 * { twojebmi.setTextSize(17); } else twojebmi.setTextSize(19); }
			 * catch (NullPointerException e) { Log.w("com.devnoobs.bmr.bmr",
			 * "com.devnoobs.bmr",e); }
			 * 
			 * 
			 * // Ustawianie wynikow w polach tabeli
			 * text_wynik_bmi.setText(Double.toString(bmi));
			 * text_wynik_bmr.setText(Double.toString(bmr));
			 * text_weglowodany.setText(Double.toString(weglowodany));
			 * text_kalorie.setText(Double.toString(kalorie));
			 * text_bialko.setText(Double.toString(bialko));
			 * text_tluszcz.setText(Double.toString(tluszcz));
			 * text_bialko_gram.setText(Double.toString(bialko_gram));
			 * text_tluszcz_gram.setText(Double.toString(tluszcz_gram));
			 * text_weglowodany_gram.setText(Double.toString(weglowodany_gram));
			 */
            // Ustawianie wynikow w polach tabeli
            text_wynik_bmi.setText(Double.toString(bmi));
            text_wynik_bmr.setText(Double.toString(bmr));
            text_weglowodany.setText(Double.toString(weglowodany) + " "
                    + getString(R.string.kcal));
            text_kalorie.setText(Double.toString(kalorie) + " "
                    + getString(R.string.kcal));
            text_bialko.setText(Double.toString(bialko) + " "
                    + getString(R.string.kcal));
            text_tluszcz.setText(Double.toString(tluszcz) + " "
                    + getString(R.string.kcal));
            text_bialko_gram.setText(Double.toString(bialko_gram) + " "
                    + getString(R.string.gram));
            text_tluszcz_gram.setText(Double.toString(tluszcz_gram) + " "
                    + getString(R.string.gram));
            text_weglowodany_gram.setText(Double.toString(weglowodany_gram)
                    + " " + getString(R.string.gram));

        } catch (Exception e) {
            Log.e("com.devnoobs.bmr.bmr", "", e.fillInStackTrace());
        }
    }// oblicz

    /**
     * @param t
     * @return
     */
    private TextView zresetujText(TextView t) {
        t.setTypeface(Typeface.DEFAULT);
        t.setTextColor(Color.BLACK);
        if (szerokosc < 380) {
            t.setTextSize(15);
        } else
            t.setTextSize(17);
        return t;
    }

    private void ustawieniePolaBMI(double bmi) {
        TextView twojebmi = null;
        try {
            // Ustawienie wygl�du wybranego pola BMI.
            // Zolty jest w komentarzu bo byl nieczytelny
            if (bmi < 16)// < 16,0 � wyg�odzenie
            {
                twojebmi = tablicaPolBMI[0];
                twojebmi.setTextColor(Color.parseColor("#CC0000"));
            } else if (bmi >= 16 && bmi <= 16.99)// 16,0�16,99 � wychudzenie
            {
                twojebmi = tablicaPolBMI[1];
                twojebmi.setTextColor(Color.parseColor("#CC0000"));
            } else if (bmi >= 17 && bmi <= 18.49)// 17,0�18,49 � niedowaga
            {
                twojebmi = tablicaPolBMI[2];
                twojebmi.setTextColor(Color.parseColor("#FF8800"));
            } else if (bmi >= 18.5 && bmi <= 24.99)// 18,5�24,99 � idealna waga
            // cia�a
            {
                twojebmi = tablicaPolBMI[3];
                twojebmi.setTextColor(Color.parseColor("#669900"));
            } else if (bmi >= 25 && bmi <= 29.99)// 25,0�29,99 � nadwaga
            {
                twojebmi = tablicaPolBMI[4];
                twojebmi.setTextColor(Color.parseColor("#FF8800"));
            } else if (bmi >= 30 && bmi <= 34.99)// 30,0�34,99 � I stopie�
            // oty�o�ci
            {
                twojebmi = tablicaPolBMI[5];
                twojebmi.setTextColor(Color.parseColor("#FF8800"));
            } else if (bmi >= 35 && bmi <= 39.99)// 35,0�39,99 � II stopie�
            // oty�o�ci
            {
                twojebmi = tablicaPolBMI[6];
                twojebmi.setTextColor(Color.parseColor("#CC0000"));
            } else if (bmi > 40)// > 40,0 � III stopie� oty�o�ci
            {
                twojebmi = tablicaPolBMI[7];
                twojebmi.setTextColor(Color.parseColor("#CC0000"));
            }

            twojebmi.setTypeface(Typeface.DEFAULT_BOLD);
            if (szerokosc < 320) {
                twojebmi.setTextSize(17);
            } else
                twojebmi.setTextSize(19);
        } catch (NullPointerException e) {
            Log.w("com.devnoobs.bmr.bmr", "com.devnoobs.bmr", e);
        }
    }

    private void zresetujTextBMI() {
        try {
            TextView textBMI = null;
            for (int i = 0; i < tablicaPolBMI.length; i++) {
                textBMI = tablicaPolBMI[i];
                textBMI.setTypeface(Typeface.DEFAULT);
                textBMI.setTextColor(Color.BLACK);
                if (szerokosc < 380) {
                    textBMI.setTextSize(15);
                } else
                    textBMI.setTextSize(17);
            }
        } catch (NullPointerException ex) {
            Log.d("com.devnoobs.bmr.bmr", "", ex);
        }

    }

    private void showToast(String tekst) {
        Context context = getActivity().getApplicationContext();
        CharSequence text = tekst;
        int duration = Toast.LENGTH_SHORT;

        Toast toast = Toast.makeText(context, text, duration);
        toast.show();
    }// showtoast

    @Override
    public void onClick(View v) {
        if (v.getId() == dodaj_wynik.getId()) {
            showDialogDodawanie();
        }

    }// onclick

    public void showDialogDodawanie() {

        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        // Get the layout inflater
        builder.setTitle("Wprowad� dane do dodania.");
        LayoutInflater inflater = getActivity().getLayoutInflater();
        // Inflate and set the layout for the dialog
        // Pass null as the parent view because its going in the dialog layout
        final View widok = inflater.inflate(R.layout.popup_dodawanie, null);
        bmi = (EditText) widok.findViewById(R.id.popup_bmi);
        waga = (EditText) widok.findViewById(R.id.popup_waga);
        notatka = (EditText) widok.findViewById(R.id.popup_dodatkowy_tekst);
        double iw = sharedPref.getFloat(getString(R.string.shared_waga), 0);
        double ib = sharedPref.getFloat(getString(R.string.text_bmi), 0);
        ib = Math.round(ib * 100.0) / 100.0;
        iw = Math.round(iw * 100.0) / 100.0;

        if (iw != 0 && ib != 0) {
            bmi.setText(Double.toString(ib));
            waga.setText(Double.toString(iw));
        }
        builder.setView(widok)
                // Add action buttons

                .setPositiveButton("Dodaj",
                        new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int id) {
                                try {

                                    double b = Double.parseDouble(bmi.getText()
                                            .toString());
                                    double w = Double.parseDouble(waga
                                            .getText().toString());
                                    try {
                                        String n = notatka.getText().toString();
                                        wds.addWynik(b, w, n);
                                    } catch (Exception e) {
                                        wds.addWynik(b, w);
                                    }
                                    // wds.addWynik(b, w);
                                    // TODO zrobic refresh po dodaniu
                                    // FragmentTabele ft = new FragmentTabele();
                                    // ft.refresh=true;
                                    // ft.spinnerWyborWczytania(7);
                                    Toast.makeText(widok.getContext(),
                                            "Wynik dodano.", Toast.LENGTH_SHORT)
                                            .show();
                                } catch (NullPointerException e) {
                                    Toast.makeText(widok.getContext(),
                                            "Pola nie zosta� wype�nione.",
                                            Toast.LENGTH_SHORT).show();
                                    e.printStackTrace();
                                }

                            }
                        })
                .setNegativeButton("Anuluj",
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {

                                dialog.cancel();
                            }
                        });
        builder.show();
    }// showdialogdodawanie

}
