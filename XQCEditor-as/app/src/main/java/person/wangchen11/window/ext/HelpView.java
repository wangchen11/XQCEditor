package person.wangchen11.window.ext;

import java.util.ArrayList;
import java.util.Iterator;
import person.wangchen11.drawable.SmashDrawable;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;

public class HelpView extends View{
	protected ArrayList<SmashDrawable> mSmashDrawables=new ArrayList<SmashDrawable>();
	
	public HelpView(Context context) {
		super(context);
		init();
	}
	
	public HelpView(Context context, AttributeSet attrs) {
		super(context, attrs);
		init();
	}
	
	public void init(){
		this.post(new Runnable() {
			@Override
			public void run() {
				HelpView.this.postDelayed(this, 25);
				HelpView.this.run(10);
				HelpView.this.invalidate();
			}
		});
	}
	
	
	@Override
	public void draw(Canvas canvas) {
		Iterator<SmashDrawable> smashIterator = mSmashDrawables.iterator();
		int w=getWidth();
		int h=getHeight();
		while(smashIterator.hasNext()){
			smashIterator.next().draw(canvas,w,h);
		}
		/*
		for(int i=0;i<mSmashDrawables.size();i++){
			mSmashDrawables.get(i).draw(canvas, getWidth(), getHeight());
		}*/
		super.draw(canvas);
	}
	
	int number=0;
	public void run(int time){

		Iterator< SmashDrawable> smashIterator=mSmashDrawables.iterator();
		while(smashIterator.hasNext())
		{
			smashIterator.next().run(time);
		}
		/*
		for(int i=0;i<mSmashDrawables.size();i++){
			mSmashDrawables.get(i).run(time);
		}*/
		for(int i=0;i<mSmashDrawables.size();i++){
			if(!mSmashDrawables.get(i).isAlive())
			{
				mSmashDrawables.remove(i);
				i--;
			}
		}
		if(Math.random()<0.04)
			mSmashDrawables.add(new SmashDrawable( (float)Math.random(), (float)Math.random(), 1, 1, 0.2f, 0x48ffffff&getColorEx((int)(Math.random()*1024)  ) ));
	}
	
	@SuppressLint("ClickableViewAccessibility")
	@Override
	public boolean onTouchEvent(MotionEvent event) {
		mSmashDrawables.add(new SmashDrawable( event.getX()/getWidth() , event.getY()/getHeight(), 1, 1, 0.2f, 0x88ffffff&getColorEx((int)(Math.random()*1024)  ) ));
		return false;
	}

	public static int getColorEx(int data){
		int tdata=(data^0x55)&0xff;
		int r=tdata|0x80,   g=(tdata<<4)|(tdata>>4)|0x80,   b=g^0xAA|0x80;
		return Color.rgb(r,g,b);
	}
}
