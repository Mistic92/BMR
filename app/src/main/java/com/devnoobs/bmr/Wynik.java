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


public class Wynik {
	public Wynik(){}
	
	private int wynik_id;
	private double bmi;
	private double waga;
	private long data;
	private String notatka;
	
	
	public String getNotatka() {
		return notatka;
	}
	public void setNotatka(String notatka) {
		this.notatka = notatka;
	}
	public int getWynik_id() {
		return wynik_id;
	}
	public void setWynik_id(int wynik_id) {
		this.wynik_id = wynik_id;
	}

	
	public double getBmi() {
		return bmi;
	}
	public void setBmi(double bmi) {
		this.bmi = bmi;
	}
	public double getWaga() {
		return waga;
	}
	public void setWaga(double waga) {
		this.waga = waga;
	}
	public long getData() {
		return data;
	}
	public void setData(long data) {
		this.data = data;
	}
	
	
	
}
