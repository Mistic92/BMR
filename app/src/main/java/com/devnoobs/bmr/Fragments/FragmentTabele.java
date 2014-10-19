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

import android.annotation.TargetApi;
import android.app.ActivityOptions;
import android.app.AlertDialog;
import android.app.Fragment;
import android.app.FragmentTransaction;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.ActivityOptionsCompat;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AdapterView.OnItemLongClickListener;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.devnoobs.bmr.Baza.WynikiDataSource;
import com.devnoobs.bmr.CustomAdapterWyniki;
import com.devnoobs.bmr.CustomSpinner;
import com.devnoobs.bmr.Interfejsy.IRefreshTabeli;
import com.devnoobs.bmr.Interfejsy.WyborDadyDialogFragmentListener;
import com.devnoobs.bmr.R;
import com.devnoobs.bmr.SzczegolyWynikuActivity;
import com.devnoobs.bmr.WyborDatyDialogFragment;
import com.devnoobs.bmr.Wynik;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;

public class FragmentTabele extends Fragment implements OnClickListener,
        OnItemSelectedListener,
        OnItemClickListener,
        OnItemLongClickListener,
        WyborDadyDialogFragmentListener,
        IRefreshTabeli {

    private WynikiDataSource wds;
    private EditText bmi;
    private EditText waga;
    private EditText notatka;
    private Button dodaj_wynik;
    private SharedPreferences sharedPref;

    private static ListView ListaWynikow;
    private BaseAdapter fAdapter;
    private Context contextFragmentTabele;
    //	private ImageView refresh_button;
    public static boolean refresh = false;


    private static TextView tekstZakresu;
    private static Spinner spinner;
    private static ArrayAdapter<CharSequence> adapter;


    private RecyclerView mRecyclerView;
    private RecyclerView.Adapter mAdapter;
    private RecyclerView.LayoutManager mLayoutManager;

    private int fragVal;

    public static FragmentTabele init(int val) {
        FragmentTabele truitonFrag = new FragmentTabele();
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
    @Override
    public void onResume()
    {
    	super.onResume();
    	Tracker tracker =  GoogleAnalytics.getInstance(getActivity()).getTracker("UA-18780283-6"); 
    	tracker.send(MapBuilder.createAppView().set(Fields.SCREEN_NAME,
    	this.getClass().getSimpleName()).build());
    }
    */


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_tabele, container, false);
        //dodaj_wynik = (Button) rootView.findViewById(R.id.buttonDodajWynik);
        ListaWynikow = (ListView) rootView.findViewById(R.id.listViewWyniki);
        wds = new WynikiDataSource(getActivity());
        //	refresh_button = (ImageView) rootView.findViewById(R.id.button_tabele_refresh);
        //	refresh_button.setOnClickListener(this);

        sharedPref = getActivity().getSharedPreferences(
                getString(R.string.appPreferences), getActivity().MODE_PRIVATE);

        //USTAWIENIE PIER... SPINERA
        spinner = (Spinner) rootView.findViewById(R.id.spinner_zakres_dat);
        CustomSpinner cspin = new CustomSpinner(rootView.getContext());
        //	spinner = cspin;
        // Create an ArrayAdapter using the string array and a default spinner layout
        adapter = ArrayAdapter.createFromResource(rootView.getContext(),
                R.array.tabela_zakres, android.R.layout.simple_spinner_item);
        //Specify the layout to use when the list of choices appears
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        // Apply the adapter to the spinner
        spinner.setAdapter(adapter);
        spinner.setOnItemSelectedListener(this);

        contextFragmentTabele = rootView.getContext();

        tekstZakresu = (TextView) rootView.findViewById(R.id.textzakreswykresu);


//        mRecyclerView = (RecyclerView) findViewById(R.id.recyclerWyniki);
//        mRecyclerView.setHasFixedSize(true);
//        mLayoutManager = new LinearLayoutManager(contextFragmentTabele);
//        mRecyclerView.setLayoutManager(mLayoutManager);
//        mAdapter = new AdapterTabeli(myDataset);
//        mRecyclerView.setAdapter(mAdapter);


        wczytajTydzien();

        //TODO
        //dopisac wczytanie zakresu ostatniego tygodnia
        //zrobic spinner z zakresami dat i wyborem na koncu
        //naprawic refresh
        //wczytajTabele(7);
        //dodaj_wynik.setOnClickListener(this);
        //TODO po longpress z wybranej daty przeskakuje na tydzien
        ListaWynikow.setOnItemClickListener(this);
        ListaWynikow.setOnItemLongClickListener(this);


        return rootView;
    }//oncreateview


    /**
     * Metoda wczytujaca liste dla ldni oparta na ListView. Ponizej na tabeli
     *
     * @param poczatek
     * @param koniec
     */
    public void wczytajTabele(long poczatek, long koniec) {

        ArrayList<Wynik> lista = wds.getData(poczatek, koniec, "DESC");
        fAdapter = new CustomAdapterWyniki(lista, contextFragmentTabele);
        ListaWynikow.setAdapter(fAdapter);
    }//wczytajtabele


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
                                    "Pola nie zosta� wype�nione.", Toast.LENGTH_SHORT)
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


    }//showDialogDodawanie		

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
                                "Usuni�to", Toast.LENGTH_SHORT)
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


    @TargetApi(Build.VERSION_CODES.JELLY_BEAN)
    @Override
    public void onClick(View v) {
        //showWynikDialog().show();
        // the text could tell you if its a plus button or minus button
       /*/
        switch(v.getId())
		{
		case R.id.buttonDodajWynik:
			showWynikDialog();
			break;
		}
		*/
        if (v.getId() == dodaj_wynik.getId()) {
            showDialogDodawanie();
        }
//		else if(v.getId()==refresh_button.getId())
//		{
//			//spinnerWyborWczytania();
//		}
        //else
        //{
            /*

			SzczegolyWynikuActivity swa = new SzczegolyWynikuActivity();  
			Intent intent = new Intent(getActivity(), swa.getClass());
			swa.dodajListener(this);
			  intent.putExtra("id", v.getId());
			 // if(android.os.Build.VERSION.SDK_INT<android.os.Build.VERSION_CODES.JELLY_BEAN)
				  startActivity(intent);
			//  else
			//  {
			//  PendingIntent pendingIntent =
			//	        TaskStackBuilder.create(getActivity())
				                        // add all of DetailsActivity's parents to the stack,
				                        // followed by DetailsActivity itself
			//	                        .addNextIntentWithParentStack(intent)
			//	                        .getPendingIntent(0, PendingIntent.FLAG_UPDATE_CURRENT);
			//  }
			 * 
			 */
        //}

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
    public void onNothingSelected(AdapterView<?> arg0) {
    }


    private void wyborDaty() {

        FragmentTransaction ft = getFragmentManager().beginTransaction();
        Fragment prev = getFragmentManager().findFragmentByTag("DatyDialog");
        if (prev != null) {
            ft.remove(prev);
        }
        ft.addToBackStack(null);

        //	Fragment wybor = new WyborDatyDialogFragment();
        // ((WyborDatyDialogFragment) wybor).addListener(this);
        // FragmentManager fragmentManager = getFragmentManager();
        // fragmentManager.beginTransaction().replace(R.id.content_frame, wybor).commit();
        //  fragmentManager.beginTransaction().show(wybor).commit();

        // Create and show the dialog.
        WyborDatyDialogFragment dialog = new WyborDatyDialogFragment();
        dialog.addListener(this);
        dialog.show(ft, "dialog");


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


    @Override
    public void onYesButton(Calendar poczatek, Calendar koniec) {
        wczytajTabele(poczatek.getTimeInMillis(), koniec.getTimeInMillis());
        ustawTekstZakresu(poczatek, koniec);
        // zmienAdapter(poczatek, koniec);
    }

//    /**
//     * Ustawia ostatni zapisany zakres dni
//     */
//    private void ustawOstatniZakresDni() {
//
//    }
//
//
//    private void ustawSharedPref(String tekst)
//    {
//        SharedPreferences.Editor editor = sharedPref.edit();
//        try {
//            editor.putString(getString(R.string.ostatniZakres),  tekst);
//            editor.apply();
//        } catch (Exception e) {
//            Log.e("com.devnoobs.bmr", "", e.fillInStackTrace());
//        }
//    }

    private void wczytajTydzien() {
        Calendar koniec = Calendar.getInstance();
        Calendar poczatek = Calendar.getInstance();
        poczatek.add(Calendar.DAY_OF_MONTH, -7);
        wczytajTabele(poczatek.getTimeInMillis(), koniec.getTimeInMillis());
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
    public void refreshTabela() {
        wczytajTydzien();

    }

}//class
