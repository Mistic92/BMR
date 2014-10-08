/*
 * Copyright (c) 2014 Łukasz Byjoś
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy of this software and associated documentation files (the "Software"), to deal in the Software without restriction, including without limitation the rights to use, copy, modify, merge, publish, distribute, sublicense, and/or sell copies of the Software, and to permit persons to whom the Software is furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.
 */e com.devnoobs.bmr;

import java.util.ArrayList;
import java.util.HashMap;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.NavUtils;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;

import com.devnoobs.bmr.Baza.WynikiDataSource;
import com.devnoobs.bmr.Interfejsy.IRefreshTabeli;
import com.google.analytics.tracking.android.EasyTracker;
import com.google.analytics.tracking.android.Fields;
import com.google.analytics.tracking.android.GoogleAnalytics;
import com.google.analytics.tracking.android.Tracker;

public class SzczegolyWynikuActivity extends Activity implements TextWatcher, OnClickListener {

	private WynikiDataSource wds;
	private EditText notatka;
	private EditText bmi;
	private EditText waga;
	//zmienna jesli edytowano notatke to zmieni sie stan i pojdzie zapis
	private boolean edytowano=false;
	private Wynik wynik;
	private Button przycisk;
	private Context c ;
	private static ArrayList<IRefreshTabeli> lista = new ArrayList<IRefreshTabeli>();
	
	public void dodajListener(IRefreshTabeli irt)
	{
		lista.add(irt);
	}
	
	private void powiadom()
	{
		for(IRefreshTabeli irt:lista)
			irt.refreshTabela();
	}
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {	
		super.onCreate(savedInstanceState);	
		
		setContentView(R.layout.activity_szczegoly_wyniku);
		// Show the Up button in the action bar.
		setupActionBar();
		wds= new WynikiDataSource(this);
		Intent intent = getIntent();
		int id = intent.getIntExtra("id",0);
		if(id!=0)
		{
		wynik = wds.getIdData(id);
		}
		przycisk = (Button) this.findViewById(R.id.buttonUsunWynik);
		przycisk.setOnClickListener(this);
		notatka = (EditText) this.findViewById(R.id.EditTextNotatka);
		notatka.setText(wynik.getNotatka());
		notatka.addTextChangedListener(this);
		bmi=(EditText) this.findViewById(R.id.EditTextBMI);
		bmi.setText(Double.toString(wynik.getBmi()));
		bmi.addTextChangedListener(this);
		waga = (EditText) this.findViewById(R.id.EditTextWaga);
		waga.setText(Double.toString(wynik.getWaga()));
		waga.addTextChangedListener(this);

		c= this;
		
	    Tracker tracker = GoogleAnalytics.getInstance(this).getTracker("UA-18780283-6");
	    HashMap<String, String> hitParameters = new HashMap<String, String>();
	    hitParameters.put(Fields.HIT_TYPE, "appview");
	    hitParameters.put(Fields.SCREEN_NAME, "com.devnoobs.bmr.SzczegolyWynikuActivity");
	    tracker.send(hitParameters);
		
	}//oncreate
	
	  @Override
	  public void onStart() {
	    super.onStart();	   
	   // GoogleAnalytics.getInstance(this).getTracker("UA-18780283-6");
	     EasyTracker.getInstance(this).activityStart(this);  // Add this method.
	   /* tracker.(MapBuilder.createAppView().set(Fields.SCREEN_NAME, "Home Screen").build());
	    

	    
	    */
	   //  com.devnoobs.bmr.SzczegolyWynikuActivity

	  }

	  @Override
	  public void onStop() {
	    super.onStop();	   
	    EasyTracker.getInstance(this).activityStop(this);  // Add this method.
	  }
	
	
	@Override
	public void onBackPressed() {
	    super.onBackPressed();
	    if(edytowano==true)  
	    { 
	    	zrobUpdate();
	    	powiadom();
	    //	NavUtils.navigateUpFromSameTask(this);
		}
		
	}

	/**
	 * Set up the {@link android.app.ActionBar}.
	 */
	private void setupActionBar() {
		getActionBar().setDisplayHomeAsUpEnabled(true);
	}
	
	/**
	 * 
	 */
	private void zrobUpdate()
	{
		//Log.d("TEST", "TEST"); 
		double b=Double.parseDouble(bmi.getText().toString());
		double w=Double.parseDouble(waga.getText().toString());;
		String n=notatka.getText().toString();
		wynik.setBmi(b);
		wynik.setNotatka(n);
		wynik.setWaga(w);
		wds.updateWynik(wynik);

	}

	private void usunWynik()
	{
		wds.deleteWynik(wynik.getWynik_id());
		powiadom();
		//NavUtils.navigateUpFromSameTask(this);
		onBackPressed();
	}
	
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.szczegoly_wyniku, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		case android.R.id.home:
		    	//if(edytowano==true)  
			   // zrobUpdate();
				//NavUtils.navigateUpFromSameTask(this);
				onBackPressed();
				return true;
		}
		return super.onOptionsItemSelected(item);
	}

	
	
	@Override
	public void afterTextChanged(Editable arg0) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void beforeTextChanged(CharSequence arg0, int arg1, int arg2,
			int arg3) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void onTextChanged(CharSequence arg0, int arg1, int arg2, int arg3) {
		edytowano=true;
		
	}


	@Override
	public void onClick(View arg0) {
		showDialogUsuwanie();
	}
	
	
    private void showDialogUsuwanie()
    {
    	AlertDialog.Builder alertDialog2 = new AlertDialog.Builder(c);
    	
    	// Setting Dialog Title
    	//alertDialog2.setTitle("Potwierdz usuniecie...");
    	 
    	// Setting Dialog Message
    	alertDialog2.setMessage("Czy chcesz usunac wynik?");
    	 
    	// Setting Icon to Dialog
    	//alertDialog2.setIcon(R.drawable.delete);
    	 
    	// Setting Positive "Yes" Btn
    	alertDialog2.setPositiveButton("Tak",
    	        new DialogInterface.OnClickListener() {
    	            public void onClick(DialogInterface dialog, int which) 
    	            {
    					usunWynik();
    	            }
    	        });
    	// Setting Negative "NO" Btn
    	alertDialog2.setNegativeButton("Nie",
    	        new DialogInterface.OnClickListener() {
    	            public void onClick(DialogInterface dialog, int which) 
    	            { 
    	                dialog.cancel();
    	            }
    	        });
    	 
    	// Showing Alert Dialog
    	alertDialog2.show();
    	//return stan;    	    	
    }
	
	

}//class
