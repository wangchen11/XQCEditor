package person.wangchen11.add;

import java.util.Iterator;
import java.util.LinkedList;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Point;
import android.graphics.PointF;
import android.graphics.RectF;
import android.graphics.Typeface;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;

public class GameView extends View{
	protected static final String TAG="GameView";
	Game mGame=new Game(8, 8);
	public GameView(Context context) {
		super(context);
		init();
	}
	
	public GameView(Context context, AttributeSet attrs) {
		super(context, attrs);
		init();
	}
	
	@Override
	protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
		super.onMeasure(widthMeasureSpec, widthMeasureSpec);
	}
	
	private void init(){
		this.post(new Runnable() {
			@Override
			public void run() {
				GameView.this.postDelayed(this, 25);
				mGame.run(25);
				GameView.this.invalidate();
			}
		});
	}
	
	@SuppressLint("DrawAllocation")
	@Override
	protected void onDraw(Canvas canvas) {
		Paint paint=new Paint();
		paint.setColor(Color.argb(255, 100, 150, 200));
		Paint textPaint=new Paint();
		textPaint.setColor(Color.BLACK);
		textPaint.setTypeface(Typeface.MONOSPACE);
		Paint linkPaint=new Paint();
		linkPaint.setColor(Color.argb(0x77, 100, 100, 100));
		float spaceX=getWidth()/mGame.getWidth();
		float spaceY=getHeight()/mGame.getHeight();
		float rX=spaceX/16;
		float rY=spaceY/16;
		int gameW=mGame.getWidth();
		int gameH=mGame.getHeight();
		for(int y=0;y<gameH;y++){
			for(int x=0;x<gameW;x++){
				if(mGame.hasData(x, y))
				{
				int data=mGame.getData(x, y);
				String value=String.valueOf(data);
				paint.setColor(mGame.getColor(x, y));
				textPaint.setColor(mGame.getTextColor(x, y));
				PointF point=mGame.getPointF(x, y);
				RectF rect=new RectF(point.x*spaceX+rX,point.y*spaceY+rY,(point.x+1)*spaceX-rX,(point.y+1)*spaceY-rY);
				canvas.drawRoundRect(rect, rY, rY, paint);
				int bit=value.length();
				if(bit<=1)
					bit=2;
				float fontSize=rect.width()/bit;
				textPaint.setTextSize(fontSize);
				float textW=textPaint.measureText(value);
				canvas.drawText(value, (rect.left+rect.right-textW)/2, (rect.bottom+rect.top+fontSize)/2, textPaint);
				}
			}
		}
		if(mGame.getLinkSize()>0)
		{
			PointF point1=null;
			PointF point2=null;
			float strokenWidth=rX+rY;
			linkPaint.setStrokeWidth(strokenWidth);
			for(int i=1;i<mGame.getLinkSize();i++)
			{
				point1=mGame.getLinkPoint(i-1);
				point1.x=(point1.x+0.5f)*spaceX;
				point1.y=(point1.y+0.5f)*spaceY;
				point2=mGame.getLinkPoint(i);
				point2.x=(point2.x+0.5f)*spaceX;
				point2.y=(point2.y+0.5f)*spaceY;
				canvas.drawLine(point1.x, point1.y, point2.x, point2.y, linkPaint);
				canvas.drawCircle(point1.x, point1.y, strokenWidth/2, linkPaint);
			}
			point1=mGame.getLinkPoint(mGame.getLinkSize()-1);
			point1.x=(point1.x+0.5f)*spaceX;
			point1.y=(point1.y+0.5f)*spaceY;
			point2=mRealPoint;
			canvas.drawLine(point1.x, point1.y, point2.x, point2.y, linkPaint);
			canvas.drawCircle(point1.x, point1.y, strokenWidth/2, linkPaint);
			canvas.drawCircle(point2.x, point2.y, strokenWidth/2, linkPaint);
		}
		
		LinkedList<SmashDrawable> drawables=mGame.getDrawable();
		Iterator<SmashDrawable> smashIterator = drawables.iterator();
		int w=getWidth();
		int h=getHeight();
		while(smashIterator.hasNext()){
			smashIterator.next().draw(canvas,w,h);
		}
		/*
		for(int i=0;i<drawables.size();i++){
			drawables.get(i).draw(canvas,getWidth(),getHeight());
		}*/
		super.onDraw(canvas);
	}
	
	Point mProPoint=new Point(-1, -1);
	PointF mRealPoint=new PointF();
	@SuppressLint("ClickableViewAccessibility")
	@Override
	public boolean onTouchEvent(MotionEvent event) {
		float spaceX=getWidth()/(float)mGame.getWidth();
		float spaceY=getHeight()/(float)mGame.getHeight();
		mRealPoint.x=event.getX();
		mRealPoint.y=event.getY();
		int rtx=(int) (event.getX()/spaceX);
		int rty=(int) (event.getY()/spaceY);
		if(rtx>=0&&rty>=0&&rtx<mGame.getWidth()&&rty<mGame.getHeight())
		if( Math.sqrt( Math.pow(event.getX()-(rtx+0.5f)*spaceX, 2) + Math.pow(event.getY()-(rty+0.5f)*spaceY, 2) )  <= (spaceX<spaceY?spaceX:spaceY)/2.0f ){
			Point point=new Point(rtx, rty);
			if(!point.equals(mProPoint))
			{
				mProPoint=point;
				mGame.linkTo(rtx, rty);
			}
		}
		if(event.getAction()==MotionEvent.ACTION_UP || event.getAction()==MotionEvent.ACTION_CANCEL || event.getAction()==MotionEvent.ACTION_OUTSIDE){
			mGame.stopLink();
			mProPoint.x=-1;
			mProPoint.y=-1;
		}
		return true;
	}
	
	public Game getGame(){
		return mGame;
	}
	
	public void setGameListener(GameListener gameListener){
		mGame.setGameListener(gameListener);
	}
}
