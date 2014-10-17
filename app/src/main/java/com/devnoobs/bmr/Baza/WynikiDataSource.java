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
import android.database.sqlite.SQLiteException;
import android.util.Log;

import com.devnoobs.bmr.Wynik;

import java.util.ArrayList;
import java.util.Calendar;

public class WynikiDataSource {

	 private static SQLiteDatabase db;
	 private static WynikiDbHelper dbHelper;
 
 
	 public WynikiDataSource(Context context) 
 	 {
		 dbHelper = new WynikiDbHelper(context);
	       db = dbHelper.getWritableDatabase();
	 }

	 public WynikiDataSource()
	 {
		 
	 }
	 
  	 public void close() 
  	 {
  		dbHelper.close();
  	 }
	 
	/**
	 * 
	 * @param bmi
	 * @param waga
	 * @param notatka
	 */
  	public void addWynik(double bmi, double waga, String notatka) {
        ContentValues contentValues = new ContentValues();
        contentValues.put("bmi", bmi);
        contentValues.put("waga", waga);
        contentValues.put("notatka", notatka);
        Calendar c = Calendar.getInstance();
        long ical = c.getTimeInMillis();
        Log.d("TEST DATY", Long.toString(ical));
        contentValues.put("data", ical);

        db.insert("wyniki", null, contentValues);
    }//addwynik
  	
  	/**
  	 * 
  	 * @param bmi
  	 * @param waga
  	 */
  	public void addWynik(double bmi, double waga) {
        ContentValues contentValues = new ContentValues();
        contentValues.put("bmi", bmi);
        contentValues.put("waga", waga);
        Calendar c = Calendar.getInstance();
        long ical =  c.getTimeInMillis();
        Log.d("TEST DATY", Long.toString(ical));
        contentValues.put("data", ical);
        db.insert("wyniki", null, contentValues);
    }//addwynik  	
  	
  	
	/**
	 * 
	 * @param w
	 */
  	public void updateWynik(Wynik w)
    {
     ContentValues cv=new ContentValues();
     //cv.put("wynik_id", w.getWynik_id());
     cv.put("bmi", w.getBmi());
     cv.put("waga", w.getWaga());
    // cv.put("data", w.getData());
     cv.put("notatka", w.getNotatka());
      db.update("wyniki", cv, "wynik_id=?", new String []{Integer.toString(w.getWynik_id())});   
    }
  	
  	/**
  	 * 
  	 * @param wynik_id
  	 */
  	public void deleteWynik(int wynik_id) {
        // Delete from DB where id match
        db.delete("wyniki", "wynik_id = " + wynik_id, null);
    }
 
  	/**
  	 * 
  	 * @param id
  	 * @return
  	 */
  	public Wynik getIdData(int id)
  	{
  		Wynik wynik = new Wynik();
  	try{

  	   Cursor cursor=db.rawQuery("SELECT * FROM wyniki where wynik_id=?", new String [] {Integer.toString(id)});
  	   cursor.moveToFirst();
  	   wynik.setWynik_id(cursor.getInt(0));
       wynik.setBmi(cursor.getDouble(1));
       wynik.setWaga(cursor.getDouble(2));
      // Log.d("test", Long.toString(cursor.getLong(3)));
       wynik.setData(cursor.getLong(3));
       wynik.setNotatka(cursor.getString(4));
  	}
  	catch(Exception e)
  	{
  		 Log.e("com.devnoobs.bmr", "getIdDataException", e.fillInStackTrace());	  
  	}
  		return wynik;
  	}


    /**
     * Sortowanie albo DESC albo ASC
     * @param poczatek
     * @param koniec
     * @param sortowanie
     * @return
     */
  	public ArrayList<Wynik> getData(long poczatek, long koniec, String sortowanie) {
     
  		ArrayList<Wynik> wynikList = new ArrayList<Wynik>();
  	try
      {
        // Name of the columns we want to select
        String[] tableColumns = new String[] {"wynik_id","bmi","waga","data","notatka"};
  
        //Jesli int jest rowny 0 to znaczy ze zwrocic wszystkie wyniki
        Cursor cursor = null;
         cursor=db.rawQuery("SELECT * FROM wyniki where data>"+poczatek+
        						" and data<"+koniec+" order by wynik_id "+sortowanie+";", new String [] {});
        // cursor = db.query("wyniki", tableColumns, null, null, null, null,"wynik_id "+sortowanie,null);//, "LIMIT "+limit);

         cursor.moveToFirst();
  
        // Iterate the results
        while (!cursor.isAfterLast()) {
            Wynik wynik = new Wynik();
            // Take values from the DB
            wynik.setWynik_id(cursor.getInt(0));
            wynik.setBmi(cursor.getDouble(1));
            wynik.setWaga(cursor.getDouble(2));
         //  Log.d("test", Long.toString(cursor.getLong(3)));
            wynik.setData(cursor.getLong(3));
            wynik.setNotatka(cursor.getString(4));
  
            wynikList.add(wynik);
            cursor.moveToNext();
        }
        cursor.close();
      }
      catch(SQLiteException e)
      {
    	  Log.e("com.devnoobs.bmr", "getDataSQL", e.fillInStackTrace());
      }
      catch(Exception e)
      {
    	  Log.e("com.devnoobs.bmr", "getDataException", e.fillInStackTrace());	  
      }
  
        return wynikList;
    }//pobieranie wynikow
  	
  	/**
  	 * 
  	 * @param poczatek
  	 * @param koniec
  	 * @param sortowanie
  	 * @return
  	
  	public ArrayList<Wynik> getData(Calendar poczatek, Calendar koniec, String sortowanie) {
  	     
  		ArrayList<Wynik> wynikList = new ArrayList<Wynik>();
  	try
      {
        String[] tableColumns = new String[] {"wynik_id","bmi","waga","datetime(data, 'localtime')","notatka"};
        Cursor cursor = null;

         cursor = db.query("wyniki", tableColumns, null, null, null, null,"wynik_id "+sortowanie,null);//, "LIMIT "+limit);
        cursor.moveToFirst();
  
        // Iterate the results
        while (!cursor.isAfterLast()) {
            Wynik wynik = new Wynik();
            // Take values from the DB
            wynik.setWynik_id(cursor.getInt(0));
            wynik.setBmi(cursor.getDouble(1));
            wynik.setWaga(cursor.getDouble(2));
            wynik.setData(cursor.getLong(3));
            wynik.setNotatka(cursor.getString(4));
  
            wynikList.add(wynik);
            cursor.moveToNext();
        }
        cursor.close();
      }
      catch(SQLiteException e)
      {
    	  Log.e("com.devnoobs.bmr", "getDataSQL", e.fillInStackTrace());
      }
      catch(Exception e)
      {
    	  Log.e("com.devnoobs.bmr", "getDataException", e.fillInStackTrace());	  
      }
  
        return wynikList;
    }//pobieranie wynikow
		 */  
	 
}//class
