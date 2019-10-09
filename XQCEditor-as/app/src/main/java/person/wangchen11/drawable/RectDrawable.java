package person.wangchen11.drawable;

import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.ColorFilter;
import android.graphics.Paint;
import android.graphics.drawable.Drawable;

public class RectDrawable extends Drawable{
	protected final static String TAG="RectDrawable";
	private int mWidth=0;
	private int mHeight=0;
	private int mCol=Color.TRANSPARENT;
	private String mText="";
	private int mTextCol=Color.BLACK;
	
	public RectDrawable(int w,int h,int col) {
		mWidth=w;
		mHeight=h;
		mCol=col;
	}
	
	public RectDrawable(int w,int h,int col,String text,int textCol) {
		mWidth=w;
		mHeight=h;
		mCol=col;
		setText(text,textCol);
	}

	public void setText(String text,int textCol)
	{

		mText=text;
		mTextCol=textCol;
	}

	public void setText(String text)
	{
		mText=text;
	}
	
	@Override
	public void draw(Canvas canvas) {
		float scaleX;
		float scaleY;
		float scale;
		scaleX=canvas.getWidth()/(float)mWidth;
		scaleY=canvas.getHeight()/(float)mHeight;
		scale=scaleX<scaleY?scaleX:scaleY;
		Paint paint=new Paint();
		paint.setColor(mCol);
		float left=(canvas.getWidth()-mWidth*scale)/2;
		float right=canvas.getWidth()-(canvas.getWidth()-mWidth*scale)/2;
		
		float top=(canvas.getHeight()-mHeight*scale)/2;
		float bottom=canvas.getHeight()-(canvas.getHeight()-mHeight*scale)/2;
		//Log.i(TAG, " left:"+left+" right:"+right+" top:"+top+" bottom"+bottom);
		
		canvas.drawRect(left, top, right, bottom, paint);
		if(mText!=null)
		{
			paint.setColor(mTextCol);
			paint.setTextSize(32);
			float w=paint.measureText(mText);
			canvas.drawText(mText, (canvas.getWidth()-w)/2, (canvas.getHeight()+paint.getTextSize())/2, paint);
		}
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
	
	@Override
	public int getIntrinsicWidth() {
		return mWidth;
	}
	
	@Override
	public int getIntrinsicHeight() {
		return mHeight;
	}
}
