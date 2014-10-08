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

public class Dane {
	public Dane()
	{}
	private static double waga;
	private static double wzrost;
	private static double bmi;
	private static double wiek;
	
	
	public static double getWaga() {
		return waga;
	}
	public static void setWaga(double waga) {
		Dane.waga = waga;
	}
	public static double getWzrost() {
		return wzrost;
	}
	public static void setWzrost(double wzrost) {
		Dane.wzrost = wzrost;
	}
	public static double getBmi() {
		return bmi;
	}
	public static void setBmi(double bmi) {
		Dane.bmi = bmi;
	}
	public static double getWiek() {
		return wiek;
	}
	public static void setWiek(double wiek) {
		Dane.wiek = wiek;
	}
	
	
	


}
