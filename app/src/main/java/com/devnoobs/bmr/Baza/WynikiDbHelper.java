/*
 * Copyright (c) 2014 Łukasz Byjoś
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy of this software and associated documentation files (the "Software"), to deal in the Software without restriction, including without limitation the rights to use, copy, modify, merge, publish, distribute, sublicense, and/or sell copies of the Software, and to permit persons to whom the Software is furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.
 */

package com.devnoobs.bmr.Baza;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import com.devnoobs.bmr.Wynik;

import java.util.ArrayList;

public class WynikiDbHelper extends SQLiteOpenHelper {

    public static final int DATABASE_VERSION = 6;//na nexus 4
    public static final String DATABASE_NAME = "wyniki.db";
    public static final String TABLE_NAME = "wyniki";
    public static final String COLUMN_ID = "wynik_id";
    public static final String COLUMN_WAGA = "waga";
    public static final String COLUMN_DATA = "data";
    public static final String COLUMN_NOTATKA = "notatka";
    private static ArrayList<Wynik> wynikList;

    // Database creation sql statement
    private static final String DATABASE_CREATE = "create table "
            + TABLE_NAME
            + "( wynik_id integer primary key autoincrement, "
            + " waga REAL not null,"
            + " data INTEGER, "
            + " notatka TEXT"
            + ");";

    public WynikiDbHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(DATABASE_CREATE);

    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        // Log.w(WynikiDbHelper.class.getName(),
        //        "Update bazy z wersji " + oldVersion + " do wersji "
        //            + newVersion + ", co niszczy wszystkie dane");
        //	boolean s = kopiuj();
        //if(s==true)
        // {
        db.execSQL("DROP TABLE " + TABLE_NAME);
        onCreate(db);
        //	wczytaj();
        //  }

    }

    private void wczytaj() {
        Log.i("wdh wczytaj", "Wczytywanie starej bazy");
        try {
            ContentValues contentValues;
            SQLiteDatabase db;
            db = this.getWritableDatabase();
            for (Wynik w : wynikList) {
                contentValues = new ContentValues();
                contentValues.put("waga", w.getWaga());
                contentValues.put("data", w.getData());

                // Calendar calendar = Calendar.getInstance();
                // java.util.Date currentDate = calendar.getTime();
                //Date data = new java.sql.Date(currentDate.getTime());
                // String sdata = data.toString();
                // contentValues.put("data", sdata);
                // Insert into DB
                db.insert("wyniki", null, contentValues);
            }
            Log.i("wdh wczytaj2", "Wczytywanie zako�czone");
        } catch (Exception e) {
            Log.w("wdh wczytaj2", "Wczytywanie wystapil blad!!");
        }


    }

    /**
     * Przy kazdym upgrade bazy trzeba poprawic metode
     */
    private boolean kopiuj() {
        try {
            Log.i("wdh kopia", "Kopiowanie bazy");
            wynikList = new ArrayList<Wynik>();
            SQLiteDatabase db;
            db = this.getWritableDatabase();
            // Name of the columns we want to select
            String[] tableColumns = new String[]{"wynik_id", "bmi", "waga", "data"};

            //Jesli int jest rowny 0 to znaczy ze zwrocic wszystkie wyniki
            Cursor cursor = null;

            cursor = db.query("wyniki", tableColumns, null, null, null, null, "wynik_id DESC",
                    null);//, "LIMIT "+limit);

            cursor.moveToFirst();

            // Iterate the results
            while (!cursor.isAfterLast()) {
                Wynik wynik = new Wynik();
                // Take values from the DB
                wynik.setWynik_id(cursor.getInt(0));
                wynik.setWaga(cursor.getDouble(1));
                wynik.setData(cursor.getLong(2));

                wynikList.add(wynik);
                cursor.moveToNext();
            }
            cursor.close();
            Log.i("wdh kopia2", "Kopiowanie bazy zako�czone");
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    @Override
    public void onDowngrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        onUpgrade(db, oldVersion, newVersion);
    }

}
