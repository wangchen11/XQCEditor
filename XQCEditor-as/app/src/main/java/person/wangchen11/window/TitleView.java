package person.wangchen11.window;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Paint.Style;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.widget.TextView;

public class TitleView extends TextView{
	private Paint mPaint = new Paint();
	private boolean mIsQuickColseEnable = true;
	private int mColseColor = Color.argb(0xff, 0xff, 0x6f, 0x00);
	public TitleView(Context context) {
		super(context);
		init();
	}
	
	public TitleView(Context context, AttributeSet attrs) {
		super(context, attrs);
		init();
	}

	public TitleView(Context context, AttributeSet attrs, int defStyleAttr) {
		super(context, attrs, defStyleAttr);
		init();
	}
	
	private void init()
	{
		setQuickColseColor(mColseColor);
	}
	
	public void setQuickCloseEnable(boolean enable)
	{
		mIsQuickColseEnable = enable;
		postInvalidate();
	}
	
	public void setQuickColseColor(int color)
	{
		mColseColor=color;
		mPaint.setColor(mColseColor);
		postInvalidate();
	}
	
	public boolean isInColseArea()
	{
		if(!mIsQuickColseEnable)
			return false;
		return mInColseArea;
	}
	
	private boolean mInColseArea = false;
	private float mDownX = 0;
	private float mDownY = 0;
	@SuppressLint("ClickableViewAccessibility")
	@Override
	public boolean onTouchEvent(MotionEvent event) {
		int w=getWidth();
		int h=getHeight();
		float density=getResources().getDisplayMetrics().density;
		float l=h/4;
		int action=event.getAction();
		if(action==MotionEvent.ACTION_DOWN)
		{
			float x=event.getX();
			float y=event.getY();
			if(x>=w-l*2 && x<=w && y>=0 &&y<=l*2 )
			{
				mInColseArea = true;
			} else
			{
				mInColseArea = false;
			}
			mDownX=x;
			mDownY=y;
		}
		if(action==MotionEvent.ACTION_MOVE)
		{
			float x=event.getX();
			float y=event.getY();
			float dx=x-mDownX;
			float dy=y-mDownY;
			if( dx*dx+dy*dy > (2*density)*(2*density) )
			{
				mInColseArea = false;
			}
		}
		if(action==MotionEvent.ACTION_CANCEL 
				||action==MotionEvent.ACTION_OUTSIDE
				||action==MotionEvent.ACTION_SCROLL)
		{
			mInColseArea = false;
		}
		return super.onTouchEvent(event);
	}
	
	@Override
	protected void onDraw(Canvas canvas) {
		super.onDraw(canvas);
		if(mIsQuickColseEnable)
		{
			mPaint.setStyle(Style.STROKE);
			mPaint.setStrokeWidth(2*getResources().getDisplayMetrics().density);
			int w=getWidth();
			int h=getHeight();
			float l=h/4;
			canvas.translate(-l/2, l/2);
			canvas.drawLine(w-l, 0, w, l, mPaint);
			canvas.drawLine(w-l, l, w, 0, mPaint);
		}
	}
}
