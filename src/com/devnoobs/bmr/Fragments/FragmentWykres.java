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

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;

import android.app.Fragment;
import android.app.FragmentTransaction;
import android.content.Context;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.devnoobs.bmr.CustomSpinner;
import com.devnoobs.bmr.R;
import com.devnoobs.bmr.WyborDatyDialogFragment;
import com.devnoobs.bmr.Wynik;
import com.devnoobs.bmr.Baza.WynikiDataSource;
import com.devnoobs.bmr.Interfejsy.WyborDadyDialogFragmentListener;
import com.echo.holographlibrary.Line;
import com.echo.holographlibrary.LineGraph;
import com.echo.holographlibrary.LinePoint;

public class FragmentWykres extends Fragment implements OnItemSelectedListener, 
														OnClickListener, 
														WyborDadyDialogFragmentListener {

	private Fragment mFragment;
	private WynikiDataSource wds;
	private ArrayList<Wynik> lista;
	
	private static Spinner spinner;
	private static ArrayAdapter<CharSequence> adapter;
	private static TextView tekstZakresu;
	
	private double minwaga=1000;
	private double minbmi=1000;
	private double maxwaga=0;
	private double maxbmi=0;
	private ImageView refresh;
	
	private LineGraph wykres_bmi;
	private LineGraph wykres_waga;
	//maksymalna ilosc punktow z podpisami do wyswietlania
	private final int maksymalnaIloscPunktow = 11;
	
	private int fragVal;
	//TODO zrobic ladowanie wykresow w async i krecace kolko jesli za malo danych do utworzenia wykresu
	public FragmentWykres() {
	}

	   public static FragmentWykres init(int val) {
		   FragmentWykres truitonFrag = new FragmentWykres();
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
	    	tracker.send(MapBuilder.createAppView().set(Fields.SCREEN_NAME, this.getClass().getSimpleName()).build());
	    }
	 */
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		View rootView = inflater.inflate(R.layout.fragment_wykres,container, false);
		wds =  new WynikiDataSource(getActivity());

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
		
				
		tekstZakresu = (TextView) rootView.findViewById(R.id.textzakreswykresu);
		
		//refresh = (ImageView) rootView.findViewById(R.id.button_wykresy_refresh);
		//refresh.setOnClickListener(this);

		wykres_bmi = (LineGraph)rootView.findViewById(R.id.graph_bmi);
		wykres_waga = (LineGraph)rootView.findViewById(R.id.graph_waga);
		
		//pinnerWyborWczytania(spinner.getSelectedItemPosition());
		
		return rootView;
	}

	/**
	 * Nie odpala sie tego bezposrednio tylko przez spinnerwyborwczytania
	 * @param ldni
	 */
	private void wczytajWykresy(long poczatek, long koniec)
	{
		try
		{
		lista = wds.getData(poczatek, koniec,"ASC");
		wykres_bmi.removeAllLines();
		wykres_waga.removeAllLines();

		Line bmi = new Line();
		LinePoint bmip ;//= new LinePoint();
		Line waga = new Line();
		LinePoint wagap;// = new LinePoint();
		
		int rozmiar = lista.size();
		if(rozmiar>1)
		{
			wykres_bmi.setVisibility(View.VISIBLE);
			wykres_waga.setVisibility(View.VISIBLE);
			Wynik w;
			for(int i=0;i<rozmiar;i++)
			{
			    w = lista.get(i);
				String sdata=Long.toString(w.getData());
				char[] buf = sdata.toCharArray();
				sdata=sdata.copyValueOf(buf, 0, 10);
				bmip = new LinePoint();
				bmip.setX(i);
				bmip.setyValue(sdata);
				double dbmi=w.getBmi();
				if(dbmi>maxbmi)
					maxbmi=dbmi;
				if(dbmi<minbmi)
					minbmi=dbmi;
				bmip.setY((float) dbmi);	
				bmi.addPoint(bmip);
				
				wagap=new LinePoint();
				wagap.setX(i);
				wagap.setyValue(sdata);
				double dwaga=w.getWaga();
				if(dwaga>maxwaga)
					maxwaga=dwaga;
				if(dwaga<minwaga)
					minwaga=dwaga;
				wagap.setY((float) dwaga);
				waga.addPoint(wagap);

			}
			
			bmi.setColor(Color.parseColor("#AA66CC"));
			waga.setColor(Color.parseColor("#AA66CC"));
	
			wykres_bmi.setGridColor(Color.BLACK);
			wykres_bmi.showHorizontalGrid(true);
			wykres_bmi.showMinAndMaxValues(true);
			wykres_bmi.setTextColor(Color.BLACK);
			
			
			//jesli wyswietla wiecej niz 11 wartosci to sie zlewaja podpisy punktow
			if(rozmiar<maksymalnaIloscPunktow)
			{
				wykres_bmi.showPointValues(true);
				wykres_bmi.setValuesTextSize(25);
				wykres_bmi.setValuesColor(Color.BLACK);
				//wykres_bmi.showPointYVal(true);
				wykres_bmi.setYValColor(Color.BLACK);
			}
			else
			{
				wykres_bmi.showPointValues(false);	
			}
			
			wykres_waga.setGridColor(Color.BLACK);
			wykres_waga.showHorizontalGrid(true);
			wykres_waga.showMinAndMaxValues(true);
			wykres_waga.setTextColor(Color.BLACK);
			if(rozmiar<maksymalnaIloscPunktow)
			{
				wykres_waga.showPointValues(true);
				wykres_waga.setValuesTextSize(25);
				wykres_waga.setValuesColor(Color.BLACK);
				//wykres_waga.showPointYVal(true);
				wykres_waga.setYValColor(Color.BLACK);
			}
			else
			{
				wykres_waga.showPointValues(false);
			}
			
			wykres_bmi.addLine(bmi);
			wykres_waga.addLine(waga);
			
			//okresla zakres wykresow w zaleznosci od min i max
			if(maxbmi-minbmi<4)
				wykres_bmi.setRangeY((float) (minbmi-1), (float) (maxbmi+1));
			else
				wykres_bmi.setRangeY((float) (minbmi-4), (float) (maxbmi+6));
			wykres_bmi.setLineToFill(0);
			
			if(maxwaga-minwaga<4)
				wykres_waga.setRangeY((float)minwaga-1,(float) maxwaga+1);
			else if(maxwaga-minwaga>=4 && maxwaga-minwaga<15)
				wykres_waga.setRangeY((float)minwaga-4,(float) maxwaga+6);
			else
				wykres_waga.setRangeY((float)minwaga-4,(float) maxwaga+10);
			wykres_waga.setLineToFill(0);	
		}//if
		else
		{
			showToast(getString(R.string.error_wykres_brak_danych));
			wykres_bmi.setVisibility(View.GONE);
			wykres_waga.setVisibility(View.GONE);
		}
		}//try
		catch(IndexOutOfBoundsException e)
		{
			showToast(getString(R.string.error_wykres_brak_danych));
		}
		catch(Exception e)
		{
			showToast(getString(R.string.error_wczytywanie_wykresow));
			Log.e("com.devnoobs.bmr wykresy", "wykres_wczytanie", e.fillInStackTrace());
		}
	}
	
	
	@Override
	public void onItemSelected(AdapterView<?> parent, View view, int pos,
			long id) 
	{	
		Calendar koniec = Calendar.getInstance();
		Calendar poczatek = Calendar.getInstance();
		if(pos==0)
		{
			poczatek.add(Calendar.DAY_OF_MONTH, -7);
			wczytajWykresy(poczatek.getTimeInMillis(), koniec.getTimeInMillis());
			ustawTekstZakresu(poczatek, koniec);
		}
		else if(pos==1)
		{
			poczatek.add(Calendar.DAY_OF_MONTH, -14);
			wczytajWykresy(poczatek.getTimeInMillis(), koniec.getTimeInMillis());	
			ustawTekstZakresu(poczatek, koniec);
		}
		else if (pos==2)
		{
			poczatek.add(Calendar.MONTH, -1);
			wczytajWykresy(poczatek.getTimeInMillis(), koniec.getTimeInMillis());	
			ustawTekstZakresu(poczatek, koniec);
		}
		else if(pos==3)
		{
			wyborDaty();
		}
	}

	@Override
	public void onNothingSelected(AdapterView<?> arg0) {
		
	}
	
	/**
	 * Od pokazywania tostow :P
	 * @param tekst
	 */
	private void showToast(String tekst)
	{
		Context context = getActivity().getApplicationContext();
		CharSequence text = tekst;
		int duration = Toast.LENGTH_SHORT;

		Toast toast = Toast.makeText(context, text, duration);
		toast.show();
	}

	@Override
	public void onClick(View v) {

		

	}//onclick
	
	private void wyborDaty()
	{

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
	public void onYesButton(Calendar poczatek, Calendar koniec) {
		wczytajWykresy(poczatek.getTimeInMillis(), koniec.getTimeInMillis());	
		ustawTekstZakresu(poczatek, koniec);
	}
	
	/**
	 * 
	 * @param poczatek
	 * @param koniec
	 */
	private void ustawTekstZakresu(Calendar poczatek, Calendar koniec)
	{
		Calendar minusrok = Calendar.getInstance();
		minusrok.add(Calendar.YEAR, -1);
		SimpleDateFormat sdf;
		if(!poczatek.after(minusrok))
			{
			sdf = new SimpleDateFormat("dd MMMM yyyy");
			tekstZakresu.setTextSize(17f);
			}
		else
			{
			sdf = new SimpleDateFormat("dd MMMM");
			tekstZakresu.setTextSize(20f);
			}
		String tekst = sdf.format(poczatek.getTime())+" - "+sdf.format(koniec.getTime());;
		tekstZakresu.setText(tekst);
		
	}//ustawtekstzakresu


	
	
}
