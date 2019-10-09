package person.wangchen11.add;

import java.util.Iterator;
import java.util.LinkedList;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Canvas;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;

public class HelpView extends View{
	protected LinkedList<SmashDrawable> mSmashDrawables=new LinkedList<SmashDrawable>();
	
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
		if(Math.random()<0.02)
			mSmashDrawables.add(new SmashDrawable( (float)Math.random(), (float)Math.random(), 1, 1, 0.2f, 0x48ffffff&Game.getColorEx((int)(Math.random()*1024)  ) ));
	}
	
	@SuppressLint("ClickableViewAccessibility")
	@Override
	public boolean onTouchEvent(MotionEvent event) {
		mSmashDrawables.add(new SmashDrawable( event.getX()/getWidth() , event.getY()/getHeight(), 1, 1, 0.2f, 0x88ffffff&Game.getColorEx((int)(Math.random()*1024)  ) ));
		return false;
	}
}
