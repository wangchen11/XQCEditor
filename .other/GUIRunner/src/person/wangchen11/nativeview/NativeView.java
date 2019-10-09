package person.wangchen11.nativeview;

//by wangchen11

import android.content.Context;
import android.graphics.Canvas;
import android.view.MotionEvent;
import android.view.View;
import android.view.inputmethod.BaseInputConnection;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputConnection;
import android.view.inputmethod.InputMethodManager;

public class NativeView extends View{
	public NativeView(Context context){
		super(context);
		NativeInterface.initView(this);
	}
	private static final int MAX_POINTER_NUMBER=10;
	private float pointersX[]=new float[MAX_POINTER_NUMBER];
	private float pointersY[]=new float[MAX_POINTER_NUMBER];
	private int   pointersId[]=new int[MAX_POINTER_NUMBER];
	
	@Override
	public boolean onTouchEvent(MotionEvent event) {
		//MotionEvent.ACTION_DOWN 
		int count=event.getPointerCount();
		if(count>MAX_POINTER_NUMBER)
			count=MAX_POINTER_NUMBER;
		for(int i=0;i<count;i++)
		{
			pointersX[i]=event.getX(i);
			pointersY[i]=event.getY(i);
			pointersId[i]=event.getPointerId(i);
		}
		
		return NativeInterface.touchEvent(event.getAction(),event.getX(),event.getY(),event.getActionIndex(),count
				,pointersX,pointersY,pointersId);
	}
	
	@Override
	protected void onDraw(Canvas canvas) {
		super.onDraw(canvas);
		NativeInterface.startDraw(canvas);
		NativeInterface.stopDraw();
	}
	
	@Override
	protected void onSizeChanged(int w, int h, int oldw, int oldh) {
		NativeInterface.sizeChange(w, h, oldw, oldh,getContext().getResources().getDisplayMetrics().density);
		super.onSizeChanged(w, h, oldw, oldh);
	}

	@Override
	public InputConnection onCreateInputConnection(EditorInfo outAttrs) {
		outAttrs.imeOptions=NativeInterface.mImeOption;//EditorInfo.IME_FLAG_NO_ENTER_ACTION | EditorInfo.IME_ACTION_DONE | EditorInfo.IME_FLAG_NO_EXTRACT_UI;
		outAttrs.inputType=NativeInterface.mInputType;//EditorInfo.TYPE_CLASS_TEXT ;//1310738;
		return new BaseInputConnection(this, true);
	}

	public void showSoftKeyboard(){
		setFocusableInTouchMode(true);
		requestFocus();
		InputMethodManager inputMethodManager=(InputMethodManager) getContext().getSystemService(Context.INPUT_METHOD_SERVICE);
		inputMethodManager.showSoftInput(this,InputMethodManager.SHOW_FORCED);
	}
	
	public void closeInputMethod() {
		View editView=this;
	    InputMethodManager imm = (InputMethodManager) getContext().getSystemService(Context.INPUT_METHOD_SERVICE);
	    boolean isOpen = imm.isActive();
	    if (isOpen) {
	        imm.hideSoftInputFromWindow(editView.getApplicationWindowToken(), 0 );
	    }
	}
}
