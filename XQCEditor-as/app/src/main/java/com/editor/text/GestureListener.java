
package com.editor.text;

import android.view.*;

public class GestureListener extends GestureDetector.SimpleOnGestureListener
implements GestureDetector.OnGestureListener {

	
	private CodeEditor codeEditor;
	
	public GestureListener(CodeEditor codeEditor){
		this.codeEditor = codeEditor;
	}
	
	
	@Override
	public boolean onDown(MotionEvent e) {
		// TODO: Implement this method
		return true;
	}

	@Override
	public void onShowPress(MotionEvent e) {
		// TODO: Implement this method

	}

	
	@Override
	public boolean onSingleTapUp(MotionEvent e) {
		// TODO: Implement this method
		codeEditor.showIME(true);
		return true;
	}

	@Override
	public boolean onScroll(MotionEvent e1, MotionEvent e2, float distanceX, float distanceY) {
		// TODO: Implement this method

		return true;
	}

	@Override
	public void onLongPress(MotionEvent e) {
		// TODO: Implement this method
	}

	@Override
	public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX, float velocityY) {
		// TODO: Implement this method
		return true;
	}


}
