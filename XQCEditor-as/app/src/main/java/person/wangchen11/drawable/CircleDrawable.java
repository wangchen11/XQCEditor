package person.wangchen11.drawable;

import android.graphics.Canvas;
import android.graphics.ColorFilter;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;

public class CircleDrawable extends Drawable{
	private int mColor;
	private Paint mPaint;
	private Rect mRect=new Rect();
	public CircleDrawable(int color) {
		mColor=color;
		mPaint=new Paint();
		mPaint.setColor(mColor);
	}
	@Override
	public void draw(Canvas canvas) {
		canvas.getClipBounds(mRect);
		canvas.drawCircle(mRect.centerX(), mRect.centerY(), (mRect.height()<mRect.width()?mRect.height():mRect.width())/2, mPaint);
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
