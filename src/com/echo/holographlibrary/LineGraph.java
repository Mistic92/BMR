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

import android.content.Context;
import android.graphics.*;
import android.graphics.Bitmap.Config;
import android.graphics.Path.Direction;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;

import java.util.ArrayList;

public class LineGraph extends View {
	
	private ArrayList<Line> lines = new ArrayList<Line>();
	private Paint paint = new Paint();
	private Paint txtPaint = new Paint();
	private float minY = 0, minX = 0;
	private float maxY = 0, maxX = 0;
	private boolean isMaxYUserSet = false;
	private int lineToFill = -1;
	private int indexSelected = -1;
	private OnPointClickedListener listener;
	private Bitmap fullImage;
	private boolean shouldUpdate = false;
    private boolean showMinAndMax = false;
    private boolean showHorizontalGrid = false;
    
    //moje dodane pola
    private boolean showPointValues=false;
    private float valuesTextSize=30;
    private int valuesColor=0xffffffff;
  
    //pokazuje wartosci osi Y pod punktami
    private boolean showPointYVal=false;
    private float yValueTextSize=10;
    private int yValueColor=0xffffffff;    
    
	private int gridColor = 0xffffffff;
	public LineGraph(Context context){
		this(context,null);
	}
	public LineGraph(Context context, AttributeSet attrs) {
		super(context, attrs);
		txtPaint.setColor(0xffffffff);
		txtPaint.setTextSize(20);
		txtPaint.setAntiAlias(true);
	}
	
	//moje metody
	/**
	 * In "dp"
	 * @param size
	 */
	public void setValuesTextSize(float size)
	{
		valuesTextSize=size;
	}
	public void showPointValues(boolean show)
	{
		showPointValues=show;
	}	
	public void showPointYVal(boolean show)
	{
		showPointYVal=show;
	}	
	/**
	 * Example: 0xffffffff = black
	 * Color.BLACK
	 * @param color
	 */
	public void setValuesColor(int color)
	{
		valuesColor=color;
	}
	
	/**
	 * Example: 0xffffffff = black
	 * Color.BLACK
	 * @param color
	 */
	public void setYValColor(int color)
	{
		yValueColor=color;
	}
	public void setYValSize(float size)
	{
		yValueTextSize=size;
	}
	
	
	
	
	//koniec moich metod
	public void setGridColor(int color)
	{
		gridColor = color;
	}
	public void showHorizontalGrid(boolean show)
	{
		showHorizontalGrid = show;
	}
	public void showMinAndMaxValues(boolean show)
	{
        showMinAndMax = show;
    }
	public void setTextColor(int color)
	{
		txtPaint.setColor(color);
	}
	public void setTextSize(float s)
	{
		txtPaint.setTextSize(s);
	}
	public void setMinY(float minY){
		this.minY = minY;
	}
	
	public void update()
	{
		shouldUpdate = true;
		postInvalidate();
	}
	public void removeAllLines(){
		while (lines.size() > 0){
			lines.remove(0);
		}
		shouldUpdate = true;
		postInvalidate();
	}
	
	public void addLine(Line line) {
		lines.add(line);
		shouldUpdate = true;
		postInvalidate();
	}
	public ArrayList<Line> getLines() {
		return lines;
	}
	public void setLineToFill(int indexOfLine) {
		this.lineToFill = indexOfLine;
		shouldUpdate = true;
		postInvalidate();
	}
	public int getLineToFill(){
		return lineToFill;
	}
	public void setLines(ArrayList<Line> lines) {
		this.lines = lines;
	}
	public Line getLine(int index) {
		return lines.get(index);
	}
	public int getSize(){
		return lines.size();
	}
	
	public void setRangeY(float min, float max) {
		minY = min;
		maxY = max;
		isMaxYUserSet = true;
	}
	public float getMaxY(){
		if (isMaxYUserSet){
			return maxY;
		} else {
			maxY = lines.get(0).getPoint(0).getY();
			for (Line line : lines){
				for (LinePoint point : line.getPoints()){
					if (point.getY() > maxY){
						maxY = point.getY();
					}
				}
			}
			return maxY;
		}
		
	}
	public float getMinY(){
		if (isMaxYUserSet){
			return minY;
		} else {
			float min = lines.get(0).getPoint(0).getY();
			for (Line line : lines){
				for (LinePoint point : line.getPoints()){
					if (point.getY() < min) min = point.getY();
				}
			}
			minY = min;
			return minY;
		}
	}
	public float getMaxX(){
		float max = lines.get(0).getPoint(0).getX();
		for (Line line : lines){
			for (LinePoint point : line.getPoints()){
				if (point.getX() > max) max = point.getX();
			}
		}
		maxX = max;
		return maxX;
		
	}
	public float getMinX(){
		float max = lines.get(0).getPoint(0).getX();
		for (Line line : lines){
			for (LinePoint point : line.getPoints()){
				if (point.getX() < max) max = point.getX();
			}
		}
		maxX = max;
		return maxX;
	}
	
	public void onDraw(Canvas ca) {
		if (fullImage == null || shouldUpdate) {
			fullImage = Bitmap.createBitmap(getWidth(), getHeight(), Config.ARGB_8888);
			Canvas canvas = new Canvas(fullImage);
			String max = (int)maxY+"";// used to display max
			String min = (int)minY+"";// used to display min
			paint.reset();
			Path path = new Path();
			
			float bottomPadding = 1, topPadding = 0;
			float sidePadding = 10;
            if (this.showMinAndMax)
                sidePadding = txtPaint.measureText(max);
			
			float usableHeight = getHeight() - bottomPadding - topPadding;
			float usableWidth = getWidth() - sidePadding;
			float lineSpace = usableHeight/10;
			
			int lineCount = 0;
			for (Line line : lines){
				int count = 0;
                float lastXPixels = 0, newYPixels;
                float lastYPixels = 0, newXPixels;
                float maxY = getMaxY();
				float minY = getMinY();
				float maxX = getMaxX();
				float minX = getMinX();
				
				if (lineCount == lineToFill){
					paint.setColor(Color.BLACK);
					paint.setAlpha(30);
					paint.setStrokeWidth(2);
					for (int i = 10; i-getWidth() < getHeight(); i = i+20){
						canvas.drawLine(i, getHeight()-bottomPadding, 0, getHeight()-bottomPadding-i, paint);
					}
					
					paint.reset();
					
					paint.setXfermode(new PorterDuffXfermode(android.graphics.PorterDuff.Mode.CLEAR));
					for (LinePoint p : line.getPoints()){
						float yPercent = (p.getY()-minY)/(maxY - minY);
						float xPercent = (p.getX()-minX)/(maxX - minX);
						if (count == 0){
							lastXPixels = sidePadding + (xPercent*usableWidth);
							lastYPixels = getHeight() - bottomPadding - (usableHeight*yPercent);
							path.moveTo(lastXPixels, lastYPixels);
						} else {
							newXPixels = sidePadding + (xPercent*usableWidth);
							newYPixels = getHeight() - bottomPadding - (usableHeight*yPercent);
							path.lineTo(newXPixels, newYPixels);
							Path pa = new Path();
							pa.moveTo(lastXPixels, lastYPixels);
							pa.lineTo(newXPixels, newYPixels);
							pa.lineTo(newXPixels, 0);
							pa.lineTo(lastXPixels, 0);
							pa.close();
							canvas.drawPath(pa, paint);
							lastXPixels = newXPixels;
							lastYPixels = newYPixels;
						}
						count++;
					}
					
					path.reset();
					
					path.moveTo(0, getHeight()-bottomPadding);
					path.lineTo(sidePadding, getHeight()-bottomPadding);
					path.lineTo(sidePadding, 0);
					path.lineTo(0, 0);
					path.close();
					canvas.drawPath(path, paint);
					
					path.reset();
					
					path.moveTo(getWidth(), getHeight()-bottomPadding);
					path.lineTo(getWidth()-sidePadding, getHeight()-bottomPadding);
					path.lineTo(getWidth()-sidePadding, 0);
					path.lineTo(getWidth(), 0);
					path.close();
					
					canvas.drawPath(path, paint);
					
				}
				
				lineCount++;
			}
			
			paint.reset();
			
			paint.setColor(this.gridColor);
			paint.setAlpha(50);
			paint.setAntiAlias(true);
			canvas.drawLine(sidePadding, getHeight() - bottomPadding, getWidth(), getHeight()-bottomPadding, paint);
			if(this.showHorizontalGrid)
				for(int i=1;i<=10;i++)
				{
					canvas.drawLine(sidePadding, getHeight() - bottomPadding-(i*lineSpace), getWidth(), getHeight()-bottomPadding-(i*lineSpace), paint);
				}
			paint.setAlpha(255);
			
			
			for (Line line : lines){
				int count = 0;
                float lastXPixels = 0, newYPixels;
                float lastYPixels = 0, newXPixels;
                float maxY = getMaxY();
				float minY = getMinY();
				float maxX = getMaxX();
				float minX = getMinX();
				
				paint.setColor(line.getColor());
				paint.setStrokeWidth(6);
				
				for (LinePoint p : line.getPoints()){
					float yPercent = (p.getY()-minY)/(maxY - minY);
					float xPercent = (p.getX()-minX)/(maxX - minX);
					if (count == 0){
						lastXPixels = sidePadding + (xPercent*usableWidth);
						lastYPixels = getHeight() - bottomPadding - (usableHeight*yPercent);
					} else {
						newXPixels = sidePadding + (xPercent*usableWidth);
						newYPixels = getHeight() - bottomPadding - (usableHeight*yPercent);
						canvas.drawLine(lastXPixels, lastYPixels, newXPixels, newYPixels, paint);
						lastXPixels = newXPixels;
						lastYPixels = newYPixels;
					}
					count++;
				}
			}
			
			
			int pointCount = 0;
			
			for (Line line : lines){
				float maxY = getMaxY();
				float minY = getMinY();
				float maxX = getMaxX();
				float minX = getMinX();
				
				paint.setColor(line.getColor());
				paint.setStrokeWidth(6);
				paint.setStrokeCap(Paint.Cap.ROUND);

				if (line.isShowingPoints()){
						int liczbaPunktow=line.getSize();
						
						//i plus jeden, tak jakby kolejny punkt, sprawdzanie czy x jest rowne liczbie punktow
						//przy sprawdzaniu czy ostatni punkt
						int x=1;
						float lastY=0;
						int ilastY=0;
						
						LinePoint p;
						LinePoint nextPoint;
					for (int i=0;i<liczbaPunktow;i++)
					{
						p=line.getPoint(i);
						float yPercent = (p.getY()-minY)/(maxY - minY);
						float xPercent = (p.getX()-minX)/(maxX - minX);
						float xPixels = sidePadding + (xPercent*usableWidth);
						float yPixels = getHeight() - bottomPadding - (usableHeight*yPercent);
						
						// TODO chyba tutaj
						if(showPointValues==true)
						{	
							paint.setColor(valuesColor);
							paint.setTextSize(valuesTextSize);
							paint.setAntiAlias(true);
							
							float roznicaPunktowZNastepnym = 0 ;
							float roznicaPunktowZPoprzednim = lastY-p.getY(); 
							
							try{
							nextPoint=line.getPoint(x);
							roznicaPunktowZNastepnym= nextPoint.getY()-p.getY();
							}
							catch(IndexOutOfBoundsException e)
							{}
							//jesli roznica jest za duza to punkt ogolny jest przesuniety w lewo i podniesiony
							if(x!=liczbaPunktow && roznicaPunktowZNastepnym>=0.5 && x!=1)
							{canvas.drawText(Float.toString(p.getY()), xPixels-34, yPixels-22, paint);}
							
							//jesli roznica to 0.2 to mniejsze przesuniecie	punktu ogolnego
							else if(x!=liczbaPunktow && roznicaPunktowZNastepnym>=0.2 && x!=1)
							{canvas.drawText(Float.toString(p.getY()), xPixels-24, yPixels-22, paint);}
								
							//jesli ostatni y jest rozny od poprzedniego wtedy sa na tym samym poziomie nad punktem
							//i roznica pomiedzy nim a poprzednim jest mniejsza niz 0.5
							//x wskazuje na ostatni punkt
							if(x==liczbaPunktow && ilastY!=(int)p.getY() && roznicaPunktowZPoprzednim<0.5)
								canvas.drawText(Float.toString(p.getY()), xPixels-60, yPixels-20, paint);
							
							//jesli roznica jest wieksza to punkt jest podniesiony
							else if(x==liczbaPunktow && ilastY!=(int)p.getY() && roznicaPunktowZPoprzednim>=0.5)
								canvas.drawText(Float.toString(p.getY()), xPixels-60, yPixels-40, paint);
							
							//jesli sa takie same rzutowane na inta to ostatni opis punktu idzie wyzej
							else if(x==liczbaPunktow && ilastY==(int)p.getY() && liczbaPunktow>2)
								canvas.drawText(Float.toString(p.getY()), xPixels-65, yPixels-55, paint);
							
							//ostatni wyjatek po prostu rysuje punkt :P 
							else if(x==1 || roznicaPunktowZNastepnym<0.2)
								canvas.drawText(Float.toString(p.getY()), xPixels, yPixels-18, paint);
							
							lastY=p.getY();
							ilastY=(int)p.getY();
						}
						if(showPointYVal==true)
						{
							paint.setColor(valuesColor);
							paint.setTextSize(valuesTextSize);
							paint.setAntiAlias(true);
							if(x==liczbaPunktow)
							{
								canvas.drawText(p.getYValue(), xPixels-125, yPixels+40, paint);
							}
							else
							{
								canvas.drawText(p.getYValue(), xPixels, yPixels+40, paint);
							}
								
						}
						x++;
						
						paint.setColor(Color.GRAY);
						canvas.drawCircle(xPixels, yPixels, 10, paint);
						
						paint.setColor(Color.WHITE);
						canvas.drawCircle(xPixels, yPixels, 5, paint);			
						Path path2 = new Path();
						path2.addCircle(xPixels, yPixels, 30, Direction.CW);
						p.setPath(path2);
						p.setRegion(new Region((int)(xPixels-30), (int)(yPixels-30), (int)(xPixels+30), (int)(yPixels+30)));
						
						if (indexSelected == pointCount && listener != null){
							paint.setColor(Color.parseColor("#33B5E5"));
							paint.setAlpha(100);
							canvas.drawPath(p.getPath(), paint);
							paint.setAlpha(255);
						}
						
						pointCount++;
					}
				}
			}
			
			shouldUpdate = false;
            if (this.showMinAndMax) {
            	
				ca.drawText(max, 0, txtPaint.getTextSize(), txtPaint);
				ca.drawText(min,0,this.getHeight(),txtPaint);
			}
		}
		
		ca.drawBitmap(fullImage, 0, 0, null);
		
		
	}
	
	@Override
	public boolean onTouchEvent(MotionEvent event) {

	    Point point = new Point();
	    point.x = (int) event.getX();
	    point.y = (int) event.getY();
	    
	    int count = 0;
	    int lineCount = 0;
        int pointCount;

        Region r = new Region();
	    for (Line line : lines){
	    	pointCount = 0;
	    	for (LinePoint p : line.getPoints()){
	    		
	    		if (p.getPath() != null && p.getRegion() != null){
	    			r.setPath(p.getPath(), p.getRegion());
                    if (r.contains(point.x, point.y) && event.getAction() == MotionEvent.ACTION_DOWN) {
                        indexSelected = count;
			    	} else if (event.getAction() == MotionEvent.ACTION_UP){
                        if (r.contains(point.x, point.y) && listener != null) {
                            listener.onClick(lineCount, pointCount);
			    		}
			    		indexSelected = -1;
			    	}
	    		}
		    	
		    	pointCount++;
			    count++;
	    	}
	    	lineCount++;
	    	
	    }
	    
	    if (event.getAction() == MotionEvent.ACTION_DOWN || event.getAction() == MotionEvent.ACTION_UP){
	    	shouldUpdate = true;
	    	postInvalidate();
	    }

	    return true;
	}
	
	public void setOnPointClickedListener(OnPointClickedListener listener) {
		this.listener = listener;
	}
	
	public interface OnPointClickedListener {
		abstract void onClick(int lineIndex, int pointIndex);
	}
}
