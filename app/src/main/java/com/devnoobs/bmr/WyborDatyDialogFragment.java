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

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.DatePicker;

import com.devnoobs.bmr.Interfejsy.WyborDadyDialogFragmentListener;

import java.util.ArrayList;
import java.util.Calendar;


@SuppressLint("ValidFragment")
public class WyborDatyDialogFragment extends DialogFragment {

    private DatePicker wybor_poczatek;
    private DatePicker wybor_koniec;


    //interfejs itp do wysylania kalendarzy w swiat ;p
    private static ArrayList<WyborDadyDialogFragmentListener> listeners = new
            ArrayList<WyborDadyDialogFragmentListener>();

    public void addListener(WyborDadyDialogFragmentListener toAdd) {
        listeners.add(toAdd);
    }

    public void notifyYesButton(Calendar poczatek, Calendar koniec) {
        for (WyborDadyDialogFragmentListener hl : listeners)
            hl.onYesButton(poczatek, koniec);
    }

    //wymagane przez dialogfragment
    public WyborDatyDialogFragment() {
    }

    //interfejs w klasie
    public interface WyborDatyDialogFragmentListener {
        void onFinishEditDialog(String inputText);
    }

    public static WyborDatyDialogFragment newInstance(int num) {
        WyborDatyDialogFragment f = new WyborDatyDialogFragment();

        // Supply num input as an argument.
        Bundle args = new Bundle();
        args.putInt("num", num);
        f.setArguments(args);

        return f;
    }


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }


    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {

        LayoutInflater factory = LayoutInflater.from(getActivity());
        final View v = factory.inflate(R.layout.wybor_daty_dialog_fragment, null);

        wybor_poczatek = (DatePicker) v.findViewById(R.id.datePickerPoczatek);
        wybor_poczatek.setCalendarViewShown(false);

        wybor_koniec = (DatePicker) v.findViewById(R.id.datePickerKoniec);
        wybor_koniec.setCalendarViewShown(false);


        return new AlertDialog.Builder(getActivity())
                .setTitle(getString(R.string.wybierz_zakres_dat))
                .setView(v)
                .setPositiveButton(R.string.wybierz,
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int whichButton) {
                                Calendar kalendarz_poczatek = przygotujKalendarzPoczatek();
                                Calendar kalendarz_koniec = przygotujKalendarzKoniec();
                                notifyYesButton(kalendarz_poczatek, kalendarz_koniec);

                            }
                        }
                )
                .setNegativeButton(R.string.anuluj,
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int whichButton) {

                            }
                        }
                )
                .create();
    }//oncreatedialog

    private Calendar przygotujKalendarzPoczatek() {
        Calendar kalendarz_poczatek = Calendar.getInstance();
        int rok_poczatek;
        int miesiac_poczatek;
        int dzien_poczatek;

        rok_poczatek = wybor_poczatek.getYear();
        miesiac_poczatek = wybor_poczatek.getMonth();
        dzien_poczatek = wybor_poczatek.getDayOfMonth();

        kalendarz_poczatek.set(rok_poczatek, miesiac_poczatek, dzien_poczatek);

        return kalendarz_poczatek;

    }//kalendarzpoczatek

    private Calendar przygotujKalendarzKoniec() {
        Calendar kalendarz_koniec = Calendar.getInstance();
        int rok_koniec;
        int miesiac_koniec;
        int dzien_koniec;

        rok_koniec = wybor_koniec.getYear();
        miesiac_koniec = wybor_koniec.getMonth();
        dzien_koniec = wybor_koniec.getDayOfMonth();

        kalendarz_koniec.set(rok_koniec, miesiac_koniec, dzien_koniec);
        kalendarz_koniec.add(Calendar.HOUR_OF_DAY, 23);
        kalendarz_koniec.add(Calendar.MINUTE, 55);
        return kalendarz_koniec;

    }//kalendarzkoniec


}
