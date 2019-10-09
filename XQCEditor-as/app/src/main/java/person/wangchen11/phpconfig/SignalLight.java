package person.wangchen11.phpconfig;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.RectF;
import android.util.AttributeSet;
import android.view.View;

public class SignalLight extends View{
	private Paint mPaint=new Paint();
	private int mColor=Color.YELLOW;
	public SignalLight(Context context) {
		super(context);
	}

	public SignalLight(Context context, AttributeSet attrs) {
		super(context, attrs);
	}
	
	public void setColor(int color)
	{
		mColor=color;
		invalidate();
	}
	RectF mRectF=new RectF();
	@Override
	protected void onDraw(Canvas canvas) {
		float w=getWidth();
		float h=getHeight();
		mPaint.setColor(mColor);
		mRectF.set(0f, 0f, w, h);
		canvas.drawRoundRect(mRectF, w/3f, h/3f, mPaint);
		super.onDraw(canvas);
	}

}
