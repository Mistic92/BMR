/*
 * Copyright (c) 2014 Łukasz Byjoś
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy of this software and associated documentation files (the "Software"), to deal in the Software without restriction, including without limitation the rights to use, copy, modify, merge, publish, distribute, sublicense, and/or sell copies of the Software, and to permit persons to whom the Software is furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.
 */

package com.echo.holographlibrary;

import java.util.ArrayList;

public class Line {

    private ArrayList<LinePoint> points = new ArrayList<LinePoint>();
    private int color;
    private boolean showPoints = true;


    public int getColor() {
        return color;
    }

    public void setColor(int color) {
        this.color = color;
    }

    public ArrayList<LinePoint> getPoints() {
        return points;
    }

    public void setPoints(ArrayList<LinePoint> points) {
        this.points = points;
    }

    public void addPoint(LinePoint point) {
        points.add(point);
    }

    public LinePoint getPoint(int index) {
        return points.get(index);
    }

    public int getSize() {
        return points.size();
    }

    public boolean isShowingPoints() {
        return showPoints;
    }

    public void setShowingPoints(boolean showPoints) {
        this.showPoints = showPoints;
    }

}
