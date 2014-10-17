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

import android.app.AlertDialog;
import android.app.Fragment;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;

import com.devnoobs.bmr.Interfejsy.IReklamy;
import com.devnoobs.bmr.Powiadomienia;
import com.devnoobs.bmr.R;

import java.util.ArrayList;

public class FragmentUstawienia extends Fragment implements OnClickListener {

	private CheckBox czekbox;
	private CheckBox checkReklamy;
	private Button typ_jednostek;
	private SharedPreferences sharedPref;
	private int fragVal;
	private static ArrayList<IReklamy> listaReklam = new ArrayList<IReklamy>() ;
	
	public void addListenerReklamy(IReklamy ir)
	{
		listaReklam.add(ir);
	}
	
	private void notifyReklamy(boolean stan)
	{
		for(IReklamy ir:listaReklam)
		{
			ir.zmienReklamy(stan);
		}
	}
	
	
	public FragmentUstawienia() {
	}
	
	   public static FragmentUstawienia init(int val) {
		   FragmentUstawienia truitonFrag = new FragmentUstawienia();
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
 
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		View rootView = inflater.inflate(R.layout.fragment_ustawienia,
				container, false);

		sharedPref = getActivity().getSharedPreferences(
				 getString(R.string.appPreferences), getActivity().MODE_PRIVATE);
		
		
		typ_jednostek= (Button) rootView.findViewById(R.id.buttonZmianaTypuJednostek);
		typ_jednostek.setOnClickListener(this);
		
		czekbox = (CheckBox) rootView.findViewById(R.id.checkBox1);
		checkReklamy = (CheckBox) rootView.findViewById(R.id.checkBoxReklamy);
		checkReklamy.setChecked(sharedPref.getBoolean("stanreklam", false));
		checkReklamy.setOnClickListener(this);
		
		czekbox.setChecked(sharedPref.getBoolean("stanboksa", false));
		czekbox.setOnClickListener(this);
		
		return rootView;
	}


	@Override
	public void onClick(View v) {
		Powiadomienia p = new Powiadomienia(getActivity());
		SharedPreferences.Editor editor = sharedPref.edit();

	
		if(v.getId()==R.id.checkBox1)
		{
			if(czekbox.isChecked()==true)
			{
				p.setAlarm(12,30);
				editor.putBoolean("stanboksa", true);
				editor.apply();
			}
			else
			{
				p.cancelAlarm();
				editor.putBoolean("stanboksa",false);	
				editor.apply();
			}
		}
		else if(v.getId()==R.id.checkBoxReklamy)
		{
			if(checkReklamy.isChecked()==true)
			{
				notifyReklamy(false);
				editor.putBoolean("stanreklam", true);
				editor.apply();
			}
			else
			{
				notifyReklamy(true);
				editor.putBoolean("stanreklam",false);	
				editor.apply();
			}
		}
		else if(v.getId()==R.id.buttonZmianaTypuJednostek)
		{
			showDialogPotwiedzenieZmianyJednostek();
		}	
	}	//onclick listener
	
	
    private void showDialogPotwiedzenieZmianyJednostek()
    {
    	AlertDialog.Builder alertDialog2 = new AlertDialog.Builder(getActivity());
    	// Setting Dialog Title
    	alertDialog2.setTitle("Zmiana jednostek miary...");
		final SharedPreferences.Editor editor = sharedPref.edit();
    	// Setting Dialog Message
    	alertDialog2.setMessage(getString(R.string.dialog_zmiana_jednostek));  	 
    	 
    	// Setting Positive "Yes" Btn
    	alertDialog2.setPositiveButton("Imperialne",
    	        new DialogInterface.OnClickListener() {
    	            public void onClick(DialogInterface dialog, int which) 
    	            {
    	            	editor.putBoolean(getString(R.string.imperial),true);
    					editor.apply();
    	            }
    	        });
    	// Setting Negative "NO" Btn
    	alertDialog2.setNegativeButton("Metryczne",
    	        new DialogInterface.OnClickListener() {
    	            public void onClick(DialogInterface dialog, int which) 
    	            { 
    	            	editor.putBoolean(getString(R.string.imperial),false);
    					editor.apply();
    	            }
    	        });
    	 
    	// Showing Alert Dialog
    	alertDialog2.show();	    	
    }
	
}
