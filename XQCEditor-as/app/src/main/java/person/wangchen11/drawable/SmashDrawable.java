package person.wangchen11.drawable;

import java.util.Iterator;
import java.util.LinkedList;

import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.PointF;

public class SmashDrawable {
	protected final static String TAG="SmashDrawable";
	private int mRow;
	private int mCol;
	private float mScale;
	private int mColor;
	private int mNumberToRun;
	LinkedList<Body> mBodies=new LinkedList<Body>();
	public SmashDrawable(float x,float y,int row,int col,float scale,int color) {
		mRow=row;
		mCol=col;
		mScale=scale;
		mColor=color;
		mNumberToRun=60;
		for(int i=0;i<10;i++){
			mBodies.add(new Body(x, y, (float)(Math.random()-0.5f)*2f,((float)( -Math.random()))*2f) );
		}
	}

	public void draw(Canvas canvas,int w,int h) {
		Paint paint=new Paint();
		float spaceX=w/(float)mRow;
		float spaceY=h/(float)mCol;
		paint.setColor(mColor);
		Iterator<Body> bodyIterator = mBodies.iterator();
		while(bodyIterator.hasNext()){
			Body body=bodyIterator.next();
			canvas.drawCircle((body.mPosition.x)*spaceX,(body.mPosition.y)*spaceY, mScale*(spaceX+spaceY)/40f,paint );
		}
		/*
		for (int i = 0; i < mBodies.size(); i++) {
			Body body=mBodies.get(i);
			canvas.drawCircle((body.mPosition.x)*spaceX,(body.mPosition.y)*spaceY, mScale*(spaceX+spaceY)/40f,paint );
		}
		*/
	}
	
	public void run(int time){

		Iterator<Body> bodyIterator = mBodies.iterator();
		while(bodyIterator.hasNext()){
			Body body=bodyIterator.next();
			body.run(time/1000f, 10f);
		}
		/*
		for(int i=0;i<mBodies.size();i++)
		{
			mBodies.get(i).run(time/1000f, 10f);
		}*/
		mNumberToRun--;
	}
	
	public boolean isAlive(){
		if(mNumberToRun>0)
			return true;
		return false;
	}
}

class Body{
	PointF mPosition;
	float mVx;//水平速度
	float mVy;//垂直速度
	public Body(float x,float y,float vx,float vy) {
		mPosition=new PointF(x, y);
		mVx=vx;
		mVy=vy;
	}
	public void run(float time,float g){
		float mVn=mVy+g*time;
		float mVp=(mVn+mVy)/2;
		mVy=mVn;
		mPosition.x+=mVx*time;
		mPosition.y+=mVp*time;
	}
}
