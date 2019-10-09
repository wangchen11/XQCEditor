package person.wangchen11.questions;

import person.wangchen11.xqceditor.R;
import android.annotation.SuppressLint;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Rect;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.View;
import android.view.GestureDetector.OnGestureListener;

public class QuestionFloatBall extends View implements OnGestureListener {
	private GestureDetector mGestureDetector = null;
	private float mDensity = 0;
	private Bitmap mBitmap = null;
	private Rect   mFromRect = new Rect();
	private Rect   mToRect = new Rect();
	private Paint mPaint = null;
	
	public QuestionFloatBall(Context context) {
		super(context);
		mGestureDetector = new GestureDetector(context,this);
		mDensity = context.getResources().getDisplayMetrics().density;
		mBitmap = BitmapFactory.decodeResource(context.getResources(), R.drawable.question);
		mFromRect.set(0, 0, mBitmap.getWidth(), mBitmap.getHeight());
		mPaint = new Paint();
	}
	
	@Override
	protected void onSizeChanged(int w, int h, int oldw, int oldh) {
		super.onSizeChanged(w, h, oldw, oldh);
        getHandler().post(new Runnable() {
			@Override
			public void run() {
				checkViewInParent();
			}
		});
	}
	
	public void savePosition(){
		SharedPreferences sharedPreferences = null;
		if(mIsPortrait()){
			sharedPreferences = getContext().getSharedPreferences("float_ball_portrait", Context.MODE_PRIVATE);
		}else{
			sharedPreferences = getContext().getSharedPreferences("float_ball_landscape", Context.MODE_PRIVATE);
		}
		Editor editor = sharedPreferences.edit();
		editor.putFloat("position_x", getX());
		editor.putFloat("position_y", getY());
		editor.commit();
		
	}
	
	private void resumePosition(){
		SharedPreferences sharedPreferences = null;
		if(mIsPortrait()){
			sharedPreferences = getContext().getSharedPreferences("float_ball_portrait", Context.MODE_PRIVATE);
		}else{
			sharedPreferences = getContext().getSharedPreferences("float_ball_landscape", Context.MODE_PRIVATE);
		}
		setX(sharedPreferences.getFloat("position_x", 0));
		setY(sharedPreferences.getFloat("position_y", mDensity*62));
	}
	
	
	private boolean mIsPortrait(){
		/*
		View parent = (View) getParent();
		if(parent!=null){
			if(parent.getHeight()>parent.getWidth())
				return true;
			return false;
		}
		return false;*/
		return true;
	}
	
	@SuppressLint("DrawAllocation") 
	@Override
	protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        setMeasuredDimension((int)(42*mDensity), (int)(42*mDensity));
        getHandler().post(new Runnable() {
			@Override
			public void run() {
				resumePosition();
			}
		});
	}
	
	@Override
	protected void onDraw(Canvas canvas) {
		//canvas.drawColor(Color.RED);
		//super.onDraw(canvas);
		mToRect.set(0, 0, getWidth(), getHeight());
		canvas.drawBitmap(mBitmap,mFromRect,mToRect, mPaint);
	}
	
	@SuppressLint("ClickableViewAccessibility") 
	@Override
	public boolean onTouchEvent(MotionEvent event) {
		MotionEvent copyEvent = MotionEvent.obtain(event);
		copyEvent.setLocation(event.getRawX(), event.getRawY());
		mGestureDetector.onTouchEvent(copyEvent);
		copyEvent.recycle();
		return true;
	}

	@Override
	public boolean onDown(MotionEvent e) {
		return true;
	}

	@Override
	public void onShowPress(MotionEvent e) {
	}

	@Override
	public boolean onSingleTapUp(MotionEvent e) {
		return true;
	}
	
	@Override
	public void setX(float x) {
		if(x<=0){
			super.setX(0);
			return;
		}
		
		View parent = (View) getParent();
		if(parent!=null)
		if(x>parent.getWidth()-getWidth()){
			super.setX(parent.getWidth()-getWidth());
			return;
		}
		super.setX(x);
	}
	
	@Override
	public void setY(float y) {
		if(y<=0){
			super.setY(0);
			return;
		}
		
		View parent = (View) getParent();
		if(parent!=null)
		if(y>parent.getHeight()-getHeight()){
			super.setY(parent.getHeight()-getHeight());
			return;
		}
		super.setY(y);
	}
	
	public void checkViewInParent(){
		if(getX()<=0){
			super.setX(0);
		}
		if(getY()<=0){
			super.setY(0);
		}

		View parent = (View) getParent();
		if(parent!=null){
			if(getY()>parent.getHeight()-getHeight()){
				super.setY(parent.getHeight()-getHeight());
			}
			if(getX()>parent.getWidth()-getWidth()){
				super.setX(parent.getWidth()-getWidth());
			}
		}
	}
	
	@Override
	public boolean onScroll(MotionEvent e1, MotionEvent e2, float distanceX,
			float distanceY) {
		setX(getX()-distanceX);
		setY(getY()-distanceY);
		savePosition();
		return true;
	}

	@Override
	public void onLongPress(MotionEvent e) {
		
	}

	@Override
	public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX,
			float velocityY) {
		return true;
	}
}
