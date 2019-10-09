package person.wangchen11.editor.edittext;

import java.util.Iterator;
import java.util.List;

import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;

public class ScrollBar {
	float mAll=100;
	float mStart=8;
	float mEnd=9;
	float mWidth;
	float mHeight;
	final static float MINI_SCALE=0.1f; 
	Paint mPaint=new Paint();
	Paint mWarnAndErrorPaint=new Paint();
	public ScrollBar() {
		mPaint.setColor(Color.GRAY);
	}
	
	public void draw(Canvas canvas){
		float x1=mWidth*(mStart/mAll);
		float x2=mWidth*(mEnd/mAll);
		canvas.drawRect(x1, 0, x2, mHeight, mPaint);
	}
	
	public void drawWarnAndError(Canvas canvas,List<WarnAndError> warnAndErrors,int count){
		if(count<=0)
			return;
		Iterator<WarnAndError> iterator = warnAndErrors.iterator();
		while(iterator.hasNext()){
			WarnAndError warnAndError = iterator.next();
			float x1=mWidth*(warnAndError.mLine/(float)count);
			float x2=mWidth*(warnAndError.mLine/(float)count)+12;
			mWarnAndErrorPaint.setColor(warnAndError.mColor);
			canvas.drawRect(x1, 0, x2, mHeight, mWarnAndErrorPaint);
		}
	}
	
	public void setSize(float width,float height)
	{
		mWidth=width;
		mHeight=height;
	}
	
	public void setPosition(float all,float start,float end){
		mAll=all;
		if(mAll<=0)
			mAll=1;
		mStart=start;
		mEnd=end;
		float scale=(mEnd-mStart)/mAll;
		if(scale<MINI_SCALE)
		{
			mStart-=mAll*(MINI_SCALE-scale)/2;
			mEnd+=mAll*(MINI_SCALE-scale)/2;
		}
	}
}
