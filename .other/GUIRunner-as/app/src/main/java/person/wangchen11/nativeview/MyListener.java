package person.wangchen11.nativeview;

import android.annotation.SuppressLint;
import android.graphics.Canvas;
import android.media.MediaPlayer;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;

public class MyListener implements View.OnClickListener , 
									View.OnLongClickListener ,
									View.OnKeyListener ,
									View.OnTouchListener ,
									MyView.OnDrawListener ,
									MediaPlayer.OnCompletionListener
{
	private int mFunAddr = 0;
	private int mDataAddr = 0;
	public MyListener(int fun,int data) {
		mFunAddr  = fun;
		mDataAddr = data;
	}
	
	@Override
	public void onClick(View view) {
		onClick(mFunAddr,mDataAddr,view);
	}

	@Override
	public boolean onLongClick(View view) {
		return onLongClick(mFunAddr,mDataAddr,view);
	}

	@Override
	public boolean onKey(View view, int key, KeyEvent keyEvent) {
		return onKey(mFunAddr,mDataAddr,view,key,keyEvent);
	}

	@SuppressLint("ClickableViewAccessibility") 
	@Override
	public boolean onTouch(View view, MotionEvent event) {
		return onTouch(mFunAddr,mDataAddr,view,event.getAction(),event.getX(),event.getY(),event);
	}

	@Override
	public void draw(View view, Canvas canvas) {
		draw(mFunAddr,mDataAddr,view,canvas);
	}
	
	@Override
	public void onCompletion(MediaPlayer mp) {
		onCompletion(mFunAddr,mDataAddr,mp);
	}

	public native static void onClick(int fun,int addr,View view);
	public native static boolean onLongClick(int fun,int addr,View view);
	public native static boolean onKey  (int fun,int addr,View view,int key, KeyEvent keyEvent);
	public native static boolean onTouch(int fun,int addr,View view,int action,float x,float y,MotionEvent event);
	public native static void draw(int fun,int addr,View view,Canvas canvas);
	public native static void onCompletion(int fun,int addr,MediaPlayer mediaPlayer);

}
