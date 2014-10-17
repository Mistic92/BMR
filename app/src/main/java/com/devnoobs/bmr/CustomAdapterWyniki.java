/*
 * Copyright (c) 2014 Łukasz Byjoś
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy of this software and associated documentation files (the "Software"), to deal in the Software without restriction, including without limitation the rights to use, copy, modify, merge, publish, distribute, sublicense, and/or sell copies of the Software, and to permit persons to whom the Software is furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.
 */

package com.devnoobs.bmr;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;

public class CustomAdapterWyniki extends BaseAdapter{

	private ArrayList<Wynik> _data;
    Context _c;
	
    public CustomAdapterWyniki (ArrayList<Wynik> data, Context c){
        _data = data;
        _c = c;
    }
    CustomAdapterWyniki(Context c)
    {
    	_c=c;
    }
    public void setLista(ArrayList<Wynik> data)
    {
    	_data=data;
    }
	
	@Override
	public int getCount() {
		
		return _data.size();
	}

	@Override
	public Object getItem(int position) {
		return _data.get(position);
	}

	@Override
	public long getItemId(int position) {
		return position;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) 
	{
        View v = convertView;
        if (v == null)
        {
           LayoutInflater vi = (LayoutInflater)_c.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
           v = vi.inflate(R.layout.listview_wynik, null);
        }

        
          ImageView image = (ImageView) v.findViewById(R.id.imgview_notatka);
          TextView data = (TextView)v.findViewById(R.id.listview_text_data);
          TextView bmi = (TextView)v.findViewById(R.id.listview_text_bmi);
          TextView waga = (TextView)v.findViewById(R.id.listview_text_waga);

          Wynik w = _data.get(position);
          
	        // URL url = new URL(msg.getUrlLogo());
          if(w.getNotatka().length()>0)
        	  image.setVisibility(View.VISIBLE);
          else
        	  image.setVisibility(View.INVISIBLE);
          
          data.setText(getDate(w.getData(),"dd/MM/yyyy HH:mm:ss"));
          bmi.setText(Double.toString(w.getBmi()));
          waga.setText(Double.toString(w.getWaga()));
                           
                       
       return v;
	}//getview

	private String getDate(long milliSeconds, String dateFormat)
	{
	    // Create a DateFormatter object for displaying date in specified format.
	    DateFormat formatter = new SimpleDateFormat(dateFormat);

	    // Create a calendar object that will convert the date and time value in milliseconds to date. 
	     Calendar calendar = Calendar.getInstance();
	     calendar.setTimeInMillis(milliSeconds);
	     return formatter.format(calendar.getTime());
	}
	
}//clss
