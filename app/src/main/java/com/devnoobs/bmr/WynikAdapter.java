/*
 * Copyright (c) 2015 Łukasz Byjoś
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy of this software and associated documentation files (the "Software"), to deal in the Software without restriction, including without limitation the rights to use, copy, modify, merge, publish, distribute, sublicense, and/or sell copies of the Software, and to permit persons to whom the Software is furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.
 */

package com.devnoobs.bmr;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.support.v4.app.ActivityOptionsCompat;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.devnoobs.bmr.Wynik;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;

import static android.support.v4.app.ActivityCompat.startActivity;
import static android.support.v7.widget.RecyclerView.ViewHolder;

/**
 Created by Lukasz on 2014-10-22.
 */
public class WynikAdapter extends RecyclerView.Adapter<WynikAdapter.ViewHolder>
{


    private ArrayList<Wynik> _data;
    int rowLayout;
    Context mContext;
    private Activity act;
    private int lastPosition = -1;


    public static class ViewHolder extends RecyclerView.ViewHolder
    {
        ImageView image;//= (ImageView) v.findViewById(R.id.imgview_notatka);
        TextView data;// = (TextView) v.findViewById(R.id.listview_text_data);
        TextView waga;// = (TextView) v.findViewById(R.id.listview_text_waga);
//        android.support.v7.widget.CardView cardView;
        LinearLayout linear;
        public static Context _context;


        public ViewHolder(View itemView)
        {
            super(itemView);
            image = (ImageView) itemView.findViewById(R.id.imgview_notatka);
            data = (TextView) itemView.findViewById(R.id.listview_text_data);
            waga = (TextView) itemView.findViewById(R.id.listview_text_waga);
//            cardView = (android.support.v7.widget.CardView) itemView.findViewById(R.id.cardViewWynik);
            linear = (LinearLayout) itemView.findViewById(R.id.cardViewWynik);
        }


    }

    public WynikAdapter(ArrayList<Wynik> _data, int rowLayout, Context mContext)
    {
        this._data = _data;
        this.rowLayout = rowLayout;
        this.mContext = mContext;
        ViewHolder._context = mContext;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup viewGroup, int viewType)
    {

        View v = LayoutInflater.from(viewGroup.getContext()).inflate(rowLayout, viewGroup, false);
        //View v = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.listview_wynik,viewGroup,false);
        return new ViewHolder(v);

    }

    @Override
    public void onBindViewHolder(ViewHolder viewHolder, int i)
    {
        Wynik wynik = _data.get(i);
        // URL url = new URL(msg.getUrlLogo());
        if (wynik.getNotatka().length() > 0)
            viewHolder.image.setVisibility(View.VISIBLE);
        else
            viewHolder.image.setVisibility(View.INVISIBLE);

        viewHolder.data.setText(getDate(wynik.getData(), "dd/MM/yyyy HH:mm:ss"));
        viewHolder.waga.setText(Double.toString(wynik.getWaga()));
//        setAnimation(viewHolder.cardView,i);
    }

    /**
     * Here is the key method to apply the animation
     */
    private void setAnimation(View viewToAnimate, int position)
    {
        // If the bound view wasn't previously displayed on screen, it's animated
        if (position > lastPosition)
        {
            Animation animation = AnimationUtils.loadAnimation(mContext, android.R.anim.fade_in);
            viewToAnimate.startAnimation(animation);
            lastPosition = position;
        }
    }



    @Override
    public int getItemCount()
    {
        return _data.size();
    }


    private String getDate(long milliSeconds, String dateFormat)
    {
        // Create a DateFormatter object for displaying date in specified format.
        DateFormat formatter = new SimpleDateFormat(dateFormat);

        // Create a calendar object that will convert the date and time value in milliseconds to
        // date.
        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(milliSeconds);
        return formatter.format(calendar.getTime());
    }
}
