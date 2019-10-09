package com.editor.text;

import android.content.*;
import android.util.*;
import android.widget.*;
import android.view.*;

public class HorScrollViewText extends HorizontalScrollView {

	
	private CodeEditor codeEditor;

	public HorScrollViewText(Context context) {
		super(context);
		
	}


	public HorScrollViewText(Context context, AttributeSet attrs) {
		super(context, attrs);
	}

	
	@Override
	protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
		// TODO: Implement this method
		super.onMeasure(widthMeasureSpec, heightMeasureSpec);
		
		codeEditor = (CodeEditor) getChildAt(0);
		
		
	}



	@Override
	public void computeScroll() {
		// TODO: Implement this method
		super.computeScroll();
		
		codeEditor.scrollX(getScrollX());
		codeEditor.getViewWidth(getWidth());
	}
	
	
	

	@Override
	public boolean onTouchEvent(MotionEvent ev) {
		// TODO: Implement this method
		
		return super.onTouchEvent(ev);
	}

	@Override
	public boolean executeKeyEvent(KeyEvent event) {
		// TODO: Implement this method
		if(event.getKeyCode()==KeyEvent.KEYCODE_ENTER){
			scrollTo(0,getScrollY());
		}
		
		
		return super.executeKeyEvent(event);
	}

	
	
	public void setContainerView(CodeEditor codeEditor) {
		this.codeEditor = codeEditor;
	}

	public CodeEditor getContainerView() {
		return codeEditor;
	}
	
	
	
	public int getRowHeight() {
		return codeEditor.getRowHeight();
	}

	public int getTotalRows() {
		return codeEditor.getTotalRows();
	}

	public int getCurrRow() {
		return codeEditor.getCurrRow();
	}
	
}
