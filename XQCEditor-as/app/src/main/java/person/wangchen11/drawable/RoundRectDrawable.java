package person.wangchen11.drawable;

import android.graphics.Canvas;
import android.graphics.ColorFilter;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.RectF;
import android.graphics.drawable.Drawable;

public class RoundRectDrawable extends Drawable{
	float mRxScale;
	float mRyScale;
	Paint mPaint;
	int mColor;
	RectF mRect=new RectF();
	public RoundRectDrawable(float rxScale,float ryScale,int color) {
		mRxScale=rxScale;
		mRyScale=ryScale;
		mColor=color;
		mPaint=new Paint();
		mPaint.setColor(color);
	}
	
	@Override
	public void draw(Canvas canvas) {
		Rect rect=getBounds();
		int w=rect.width();//canvas.getWidth();
		int h=rect.height();//canvas.getHeight();
		float rx=w*mRxScale;
		float ry=h*mRyScale;
		mRect.left=0;
		mRect.top=0;
		mRect.right=w;
		mRect.bottom=h;
		canvas.drawRoundRect(mRect, rx, ry, mPaint);
	}

	@Override
	public void setAlpha(int alpha) {
	}

	@Override
	public void setColorFilter(ColorFilter colorFilter) {
	}

	@Override
	public int getOpacity() {
		return 0;
	}
}
