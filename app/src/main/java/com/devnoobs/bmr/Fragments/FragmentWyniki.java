package com.devnoobs.bmr.Fragments;

import android.app.AlertDialog;
import android.app.Fragment;
import android.app.FragmentTransaction;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityOptionsCompat;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.devnoobs.bmr.Baza.WynikiDataSource;
import com.devnoobs.bmr.CustomSpinner;
import com.devnoobs.bmr.Interfejsy.IRefreshTabeli;
import com.devnoobs.bmr.Interfejsy.WyborDadyDialogFragmentListener;
import com.devnoobs.bmr.R;
import com.devnoobs.bmr.SzczegolyWynikuActivity;
import com.devnoobs.bmr.WyborDatyDialogFragment;
import com.devnoobs.bmr.Wynik;
import com.devnoobs.bmr.WynikAdapter;
import com.echo.holographlibrary.Line;
import com.echo.holographlibrary.LineGraph;
import com.echo.holographlibrary.LinePoint;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;

import jp.wasabeef.recyclerview.animators.SlideInUpAnimator;

/**
 * Created by Lukasz on 2015-04-15.
 */
public class FragmentWyniki extends Fragment implements AdapterView.OnItemSelectedListener,
        View.OnClickListener, WyborDadyDialogFragmentListener, IRefreshTabeli,
        AdapterView.OnItemClickListener,
        AdapterView.OnItemLongClickListener {

    private WynikiDataSource wds;
    private ArrayList<Wynik> lista;

    private static Spinner spinner;
    private static ArrayAdapter<CharSequence> adapter;
    private static TextView tekstZakresu;

    private double minwaga = 1000;
    private double maxwaga = 0;
//    private ImageView refresh;

    private LineGraph wykres_waga;

    private static Calendar _poczatek;
    private static Calendar _koniec;

    //--------------------------------------------//
    // TABELA
    private EditText bmi;
    private EditText waga;
    private EditText notatka;
    private SharedPreferences sharedPref;
    //private static ListView listaWynikow;
    private BaseAdapter fAdapter;
    private WynikAdapter wAdapter;
    private Context contextFragmentTabele;
    //	private ImageView refresh_button;
    public static boolean refresh = false;

    //recycler itp
    private static RecyclerView mRecyclerView;


    //maksymalna ilosc punktow z podpisami do wyswietlania
    private final int maksymalnaIloscPunktow = 11;

    private int fragVal;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        fragVal = getArguments() != null ? getArguments().getInt("val") : 1;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView;
        rootView = inflater.inflate(R.layout.fragment_wyniki, container, false);
        wds = new WynikiDataSource(getActivity());

        //USTAWIENIE PIER... SPINERA
        spinner = (Spinner) rootView.findViewById(R.id.spinner_zakres_dat);
        CustomSpinner cspin = new CustomSpinner(rootView.getContext());
        //	spinner = cspin;
        // Create an ArrayAdapter using the string array and a default spinner layout
        adapter = ArrayAdapter.createFromResource(rootView.getContext(),
                R.array.tabela_zakres, R.layout.spinner_item);
        //Specify the layout to use when the list of choices appears
        adapter.setDropDownViewResource(R.layout.spinner_item);
        // Apply the adapter to the spinner
        spinner.setAdapter(adapter);
        spinner.setOnItemSelectedListener(this);


        tekstZakresu = (TextView) rootView.findViewById(R.id.textzakreswykresu);

        //refresh = (ImageView) rootView.findViewById(R.id.button_wykresy_refresh);
        //refresh.setOnClickListener(this);

        wykres_waga = (LineGraph) rootView.findViewById(R.id.graph_waga);

        //============================================
        //TABELA
        mRecyclerView = (RecyclerView) rootView.findViewById(R.id.recyclerWyniki);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(contextFragmentTabele));
        mRecyclerView.setItemAnimator(new SlideInUpAnimator());
        sharedPref = getActivity().getSharedPreferences(
                getString(R.string.appPreferences), getActivity().MODE_PRIVATE);

        contextFragmentTabele = rootView.getContext();

        wczytajTydzien();


        return rootView;

        // return super.onCreateView(inflater, container, savedInstanceState);
    }

    /**
     * Metoda wczytujaca liste dla ldni oparta na ListView. Ponizej na tabeli
     *
     * @param poczatek
     * @param koniec
     */
    public void wczytajTabele(long poczatek, long koniec) {

        ArrayList<Wynik> lista = wds.getData(poczatek, koniec, "DESC");
        //  fAdapter = new AdapterWynikow(lista, contextFragmentTabele);
        wAdapter = new WynikAdapter(lista, R.layout.listview_wynik, contextFragmentTabele);

        // listaWynikow.setAdapter(fAdapter);
        mRecyclerView.setAdapter(wAdapter);
        Log.i("d", "costam");
    }//wczytajtabele


    public void showDialogDodawanie() {

        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        // Get the layout inflater
        builder.setTitle(contextFragmentTabele.getString(R.string.title_add_result));
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

                .setPositiveButton("Dodaj", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int id) {
                        try {

                            double b = Double.parseDouble(bmi.getText().toString());
                            double w = Double.parseDouble(waga.getText().toString());
                            try {
                                String n = notatka.getText().toString();
                                wds.addWynik(b, w, n);
                            } catch (Exception e) {
                                wds.addWynik(b, w);
                            }
                            // wds.addWynik(b, w);
                            Toast.makeText(widok.getContext(),
                                    "Wynik dodano.", Toast.LENGTH_SHORT)
                                    .show();
                            wczytajTydzien();
                        } catch (NullPointerException e) {
                            Toast.makeText(widok.getContext(),
                                    "Pola nie zosta? wype?nione.", Toast.LENGTH_SHORT)
                                    .show();
                        }

                    }
                })
                .setNegativeButton("Anuluj", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {

                        dialog.cancel();
                    }
                });
        builder.show();
        //  return builder.create();


    }//showAddResultDialog

    private void showDialogUsuwanie(final int id) {
        AlertDialog.Builder alertDialog2 = new AlertDialog.Builder(getActivity());

        // Setting Dialog Title
        //alertDialog2.setTitle("Potwierdz usuniecie...");

        // Setting Dialog Message
        alertDialog2.setMessage("Czy chcesz usunac wynik?");

        // Setting Positive "Yes" Btn
        alertDialog2.setPositiveButton("Tak",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        Toast.makeText(getActivity(),
                                "Usuni?to", Toast.LENGTH_SHORT)
                                .show();
                        wds.deleteWynik(id);
                        wczytajTydzien();
                    }
                });
        // Setting Negative "NO" Btn
        alertDialog2.setNegativeButton("Nie",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.cancel();
                    }
                });

        // Showing Alert Dialog
        alertDialog2.show();
        //return stan;
    }


    /**
     * Nie odpala sie tego bezposrednio tylko przez spinnerwyborwczytania
     *
     * @param poczatek
     * @param koniec
     */
    private void wczytajWykresy(long poczatek, long koniec) {
        try {
            lista = wds.getData(poczatek, koniec, "ASC");
            wykres_waga.removeAllLines();

            Line waga = new Line();
            LinePoint wagap;// = new LinePoint();

            int rozmiar = lista.size();
            if (rozmiar > 1) {
                wykres_waga.setVisibility(View.VISIBLE);
                Wynik w;
                for (int i = 0; i < rozmiar; i++) {
                    w = lista.get(i);
                    String sdata = Long.toString(w.getData());
                    char[] buf = sdata.toCharArray();
                    sdata = sdata.copyValueOf(buf, 0, 10);

                    wagap = new LinePoint();
                    wagap.setX(i);
                    wagap.setyValue(sdata);
                    double dwaga = w.getWaga();
                    if (dwaga > maxwaga)
                        maxwaga = dwaga;
                    if (dwaga < minwaga)
                        minwaga = dwaga;
                    wagap.setY((float) dwaga);
                    waga.addPoint(wagap);

                }

                waga.setColor(Color.parseColor("#AA66CC"));

                wykres_waga.setGridColor(Color.BLACK);
                wykres_waga.showHorizontalGrid(true);
                wykres_waga.showMinAndMaxValues(true);
                wykres_waga.setTextColor(Color.BLACK);
                if (rozmiar < maksymalnaIloscPunktow) {
                    wykres_waga.showPointValues(true);
                    wykres_waga.setValuesTextSize(25);
                    wykres_waga.setValuesColor(Color.BLACK);
                    //wykres_waga.showPointYVal(true);
                    wykres_waga.setYValColor(Color.BLACK);
                } else {
                    wykres_waga.showPointValues(false);
                }
                wykres_waga.addLine(waga);

                //okresla zakres wykresow w zaleznosci od min i max

                if (maxwaga - minwaga < 4)
                    wykres_waga.setRangeY((float) minwaga - 1, (float) maxwaga + 1);
                else if (maxwaga - minwaga >= 4 && maxwaga - minwaga < 15)
                    wykres_waga.setRangeY((float) minwaga - 4, (float) maxwaga + 6);
                else
                    wykres_waga.setRangeY((float) minwaga - 4, (float) maxwaga + 10);
                wykres_waga.setLineToFill(0);
            }//if
            else {
                showToast(getString(R.string.error_wykres_brak_danych));
                wykres_waga.setVisibility(View.GONE);
            }
        }//try
        catch (IndexOutOfBoundsException e) {
            showToast(getString(R.string.error_wykres_brak_danych));
        } catch (Exception e) {
            showToast(getString(R.string.error_wczytywanie_wykresow));
            Log.e("error", "wykres_wczytanie", e.fillInStackTrace());
        }
    }

    private void wyborDaty() {

        Integer apiLvl = Integer.valueOf(Build.VERSION.SDK_INT);


        if (apiLvl < 21) {
            //	Fragment wybor = new WyborDatyDialogFragment();
            // ((WyborDatyDialogFragment) wybor).addListener(this);
            // FragmentManager fragmentManager = getFragmentManager();
            // fragmentManager.beginTransaction().replace(R.id.content_frame, wybor).commit();
            //  fragmentManager.beginTransaction().show(wybor).commit();

            // Create and show the dialog.
            WyborDatyDialogFragment dialog = new WyborDatyDialogFragment();
            dialog.addListener(this);
//            dialog.show(ft, "dialog");
            showDialog(dialog);
        } else {

            //z parametrem true poniewaz jest api lolipopa i sie nie miesci kalendarz ;d
            WyborDatyDialogFragment dialog = new WyborDatyDialogFragment("p");
            dialog.addListener(this);
            showDialog(dialog);
//            dialog.show(ft, "dialog");

            //todo zrobic w osobnym watku widocznie
//            dialog = new WyborDatyDialogFragment("k");
//            dialog.show(ft, "dialog");

        }
    }


    @Override
    public void onYesButton(Calendar poczatek, Calendar koniec) {
        wczytajWykresy(poczatek.getTimeInMillis(), koniec.getTimeInMillis());
        ustawTekstZakresu(poczatek, koniec);
    }

    @Override
    public void onYesButtonStart(Calendar poczatek) {
        _poczatek = poczatek;
        WyborDatyDialogFragment dialog = new WyborDatyDialogFragment("k");
        dialog.addListener(this);
        showDialog(dialog);

    }

    @Override
    public void onYesButtonEnd(Calendar koniec) {
        _koniec = koniec;
        wczytajWykresy(_poczatek.getTimeInMillis(), koniec.getTimeInMillis());
        ustawTekstZakresu(_poczatek, koniec);
    }

    /**
     * @param dialog
     */
    private void showDialog(WyborDatyDialogFragment dialog) {
        FragmentTransaction ft = getFragmentManager().beginTransaction();
        Fragment prev = getFragmentManager().findFragmentByTag("DatyDialog");
        if (prev != null) {
            ft.remove(prev);
        }
        ft.addToBackStack(null);
        dialog.addListener(this);
        dialog.show(ft, "dialog");
    }

    /**
     * @param poczatek
     * @param koniec
     */
    private void ustawTekstZakresu(Calendar poczatek, Calendar koniec) {
        Calendar minusrok = Calendar.getInstance();
        minusrok.add(Calendar.YEAR, -1);
        SimpleDateFormat sdf;
        if (!poczatek.after(minusrok)) {
            sdf = new SimpleDateFormat("dd MMMM yyyy");
            tekstZakresu.setTextSize(17f);
        } else {
            sdf = new SimpleDateFormat("dd MMMM");
            tekstZakresu.setTextSize(20f);
        }
        String tekst = sdf.format(poczatek.getTime()) + " - " + sdf.format(koniec.getTime());
        ;
        tekstZakresu.setText(tekst);

    }//ustawtekstzakresu

    @Override
    public void onClick(View v) {

    }

    /**
     * Spinner do wybierania zakresu
     *
     * @param parent
     * @param view
     * @param pos
     * @param id
     */
    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int pos,
                               long id) {
        Calendar koniec = Calendar.getInstance();
        Calendar poczatek = Calendar.getInstance();
        if (pos == 0) {
            poczatek.add(Calendar.DAY_OF_MONTH, -7);
            wczytajTabele(poczatek.getTimeInMillis(), koniec.getTimeInMillis());
            ustawTekstZakresu(poczatek, koniec);
        } else if (pos == 1) {
            poczatek.add(Calendar.DAY_OF_MONTH, -14);
            wczytajTabele(poczatek.getTimeInMillis(), koniec.getTimeInMillis());
            ustawTekstZakresu(poczatek, koniec);
        } else if (pos == 2) {
            poczatek.add(Calendar.MONTH, -1);
            wczytajTabele(poczatek.getTimeInMillis(), koniec.getTimeInMillis());
            ustawTekstZakresu(poczatek, koniec);
        } else if (pos == 3) {
            wyborDaty();
        }
    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {

    }


    @Override
    /**
     * TYM JEST URUCHAMIANY INTENT SZCZEGOLOW
     */
    public void onItemClick(AdapterView<?> arg0, View view, int position,
                            long id) {
        Wynik w = (Wynik) arg0.getItemAtPosition(position);
        SzczegolyWynikuActivity swa = new SzczegolyWynikuActivity();
        Intent intent = new Intent(getActivity(), swa.getClass());
        swa.dodajListener(this);
        intent.putExtra("id", w.getWynik_id());
        startActivity(intent, ActivityOptionsCompat.makeSceneTransitionAnimation(getActivity())
                .toBundle());

    }

    @Override
    public boolean onItemLongClick(AdapterView<?> arg0, View arg1, int position, long arg3) {
        Wynik w = (Wynik) arg0.getItemAtPosition(position);
        showDialogUsuwanie(w.getWynik_id());
        // wczytajTydzien();
        return false;
    }

    /**
     * Metoda wczytuje ostatni tydzien.
     */
    private void wczytajTydzien() {
        Calendar koniec = Calendar.getInstance();
        Calendar poczatek = Calendar.getInstance();
        poczatek.add(Calendar.DAY_OF_MONTH, -7);
        wczytajTabele(poczatek.getTimeInMillis(), koniec.getTimeInMillis());
        wczytajWykresy(poczatek.getTimeInMillis(), koniec.getTimeInMillis());
    }


    @Override
    public void refreshTabela() {
        wczytajTydzien();

    }

    /**
     * Od pokazywania tostow :P
     *
     * @param tekst
     */
    private void showToast(String tekst) {
        Context context = getActivity().getApplicationContext();
        CharSequence text = tekst;
        int duration = Toast.LENGTH_SHORT;

        Toast toast = Toast.makeText(context, text, duration);
        toast.show();
    }
}
