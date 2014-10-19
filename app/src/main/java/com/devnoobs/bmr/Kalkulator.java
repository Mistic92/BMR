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

public class Kalkulator {
    private double[] tablica_aktywnosci = {1.2, 1.375, 1.55, 1.725, 1.9};

    public double liczBmr(double masa, double wzrost, double wiek, int plec) {
        double bmr = 0;
        if (plec == 0) {
            bmr = 13.7516 * masa + 5.0033 * wzrost - 6.755 * wiek + 66.473;
        } else {
            bmr = 9.5634 * masa + 1.8496 * wzrost - 4.6756 * wiek + 655.0955;
        }
        bmr = Math.round(bmr * 100.0) / 100.0;
        return bmr;
    }//liczbmr

    public double liczKalorie(double bmr, int aktywnosc) {
        double kalorie = bmr * tablica_aktywnosci[aktywnosc];
        kalorie = Math.round(kalorie * 100.0) / 100.0;
        return kalorie;
    }

    public double liczBialko(double kalorie) {
        double bialko = kalorie * 0.15;
        return Math.round(bialko * 100.0) / 100.0;
    }

    public double liczWeglodowany(double kalorie) {
        double weglo = kalorie * 0.55;
        weglo = Math.round(weglo * 100.0) / 100.0;
        return weglo;
    }

    public double liczTluszcz(double kalorie) {
        return Math.round((kalorie * 0.3) * 100.0) / 100.0;
    }

    public double cal2bialko(double wartosc) {
        wartosc = wartosc / 4;
        wartosc = Math.round(wartosc * 100.0) / 100.0;
        return wartosc;

    }


}
