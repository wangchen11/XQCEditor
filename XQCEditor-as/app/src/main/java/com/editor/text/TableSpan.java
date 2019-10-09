package com.editor.text;

import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Paint.FontMetricsInt;
import android.text.style.ReplacementSpan;

public class TableSpan extends ReplacementSpan {
	private Paint mPaint = null;
	private float mSpaceWidth = 0;
	@Override
	public int getSize(Paint paint, CharSequence text, int start, int end,
			FontMetricsInt fm) {
		if(paint == mPaint && paint.getTextSize() == mPaint.getTextSize()){
		}else{
			mPaint = paint;
			mSpaceWidth = paint.measureText("0");
		}
		float tableWidth = (mSpaceWidth*4);
		return (int) (tableWidth);
	}

	@Override
	public void draw(Canvas canvas, CharSequence text, int start, int end,
			float x, int top, int y, int bottom, Paint paint) {
	}
	
}
