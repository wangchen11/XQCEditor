package com.editor.text;
import android.content.*;
import android.graphics.*;
import android.util.*;
import android.view.*;
import android.view.inputmethod.*;

public class CodeEditor extends ViewGroup {

	private Paint backPaint;
	private GestureDetector detector;
	private int rectStartX,rectStartY;
	private int rectEndX,rectEndY;
	private int rowHeight,currRow;
	private int totalRows;

	private int scrollY;
	private InputMethodManager imm;
	private boolean isKeyEnter = false;
	private boolean isKeyDelete = false;
	private int viewHeight;
	private int mChildHeight,srcRow;
	private TextEditorView textEditor;

	private int screenWidth,screenHeight;

	private final int MARGIN_LEFT = 100;


	public CodeEditor(Context context) {
		super(context, null);
	}


	public CodeEditor(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
	}


	public CodeEditor(Context context, AttributeSet attrs) {
		super(context, attrs);
		init(context);
	}

	public void setTextEditor(TextEditorView textEditor) {
		this.textEditor = textEditor;
	}

	public TextEditorView getTextEditor() {
		return textEditor;

	}

	public void init(Context context) {
		WindowManager windowManager = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
		screenWidth = windowManager.getDefaultDisplay().getWidth();
		screenHeight = windowManager.getDefaultDisplay().getHeight();

		imm = (InputMethodManager)getContext()
			.getSystemService(Context.INPUT_METHOD_SERVICE);
		detector = new GestureDetector(new GestureListener(this));
		setBackgroundColor(Color.TRANSPARENT);
		backPaint = new Paint();
		backPaint.setColor(Color.TRANSPARENT);
		rectStartX = MARGIN_LEFT;
	}




	@Override
	protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
		super.onMeasure(widthMeasureSpec, heightMeasureSpec);
		int widthMode = MeasureSpec.getMode(widthMeasureSpec);
		int heightMode = MeasureSpec.getMode(heightMeasureSpec);
		int widthSize = MeasureSpec.getSize(widthMode);
		int heightSize = MeasureSpec.getSize(heightMode);

		calcuDimension(widthMode, heightMode, widthSize, heightSize);

		measureChildren(widthMeasureSpec, heightMeasureSpec);

	}


	public void calcuDimension(int widthMode, int heightMode
							   , int widthSize, int heightSize) {

		int width=0,height=0;
		int childWidth=0,childHeight=0;
		MarginLayoutParams mLayoutParams = null;

		for (int i=0;i < getChildCount();i++) {
			View childView = getChildAt(i);
			childWidth = getChildAt(i).getMeasuredWidth();
			childHeight = getChildAt(i).getMeasuredHeight();
			mLayoutParams = (MarginLayoutParams)childView.getLayoutParams();

			width = childWidth + mLayoutParams.leftMargin + mLayoutParams.rightMargin ;
			height = childHeight + mLayoutParams.topMargin + mLayoutParams.bottomMargin;

		}

		if (width < screenWidth) {
			width = screenWidth + screenWidth / 2 ;
		} else {
			width = width + screenWidth / 2;
		}

		if (height < screenHeight) {
			height = screenHeight + screenHeight / 2;
		} else {
			height = height + screenHeight / 2;
		}
		rectEndX = width;

		setMeasuredDimension((widthMode == MeasureSpec.EXACTLY) ? widthSize
							 : width, (heightMode == MeasureSpec.EXACTLY) ? heightSize: height);
	}

	@Override
	protected void onLayout(boolean changed, int l, int t, int r, int b) {



		int childWidth=0,childHeight=0;
		MarginLayoutParams mLayoutParams = null;
		for (int i=0;i < getChildCount();i++) {
			View childView = getChildAt(i);
			childWidth = getChildAt(i).getMeasuredWidth();
			childHeight = getChildAt(i).getMeasuredHeight();
			mLayoutParams = (MarginLayoutParams) childView.getLayoutParams();


			l = mLayoutParams.leftMargin;
			if (i == 0) {
				t = mLayoutParams.topMargin;
			} else {
				t = b + mLayoutParams.topMargin;
			}

			r = l + childWidth + rectEndX ;
			b = t + childHeight ;

			childView.layout(l, t, r, b);

		}


	}

	@Override
	public ViewGroup.LayoutParams generateLayoutParams(AttributeSet attrs) {
		return new MarginLayoutParams(getContext(), attrs);
	}

	@Override
	protected ViewGroup.LayoutParams generateDefaultLayoutParams() {
		return new MarginLayoutParams(LayoutParams.FILL_PARENT,
									  LayoutParams.FILL_PARENT);
	}

	@Override
	protected ViewGroup.LayoutParams generateLayoutParams(
		ViewGroup.LayoutParams p) {
		return new MarginLayoutParams(p);
	}



	@Override
	protected void onDraw(Canvas canvas) {
		// TODO: Implement this method
		super.onDraw(canvas);
		invalidate();
	}


	@Override
	protected void dispatchDraw(Canvas canvas) {
		// TODO: Implement this method
		super.dispatchDraw(canvas);
		textEditor = (TextEditorView) getChildAt(0);
		rowHeight = textEditor.getRowHeight();
		currRow = textEditor.getCurrRow();
		totalRows = textEditor.getTotalRows();
		if (srcRow != currRow) {
			int totalHeight = 0;
			rectEndY = currRow * rowHeight + rowHeight / 5;
			rectStartY = rectEndY - rowHeight - rowHeight / 10;
			for (int i=0;i < getChildCount() - 1;i++) {
				totalHeight += getChildAt(i).getMeasuredHeight();
			}
			mChildHeight = totalHeight;
			srcRow = currRow;

		}
	}

	@Override
	protected boolean drawChild(Canvas canvas, View child, long drawingTime) {
		// TODO: Implement this method
		textEditor = (TextEditorView) getChildAt(0);
		if (!textEditor.hasSelection()) {
			if ((currRow + 1) * rowHeight + rowHeight / 4 - scrollY >= viewHeight - mChildHeight
				&& isKeyEnter == true) {
				rectEndY = scrollY + viewHeight  - mChildHeight;
				rectStartY = rectEndY - rowHeight;
			}

			if ((currRow + 1) * rowHeight - scrollY <= rowHeight / 2 
				&& isKeyDelete == true) {
				rectEndY =  scrollY - rowHeight - rowHeight / 4;
				rectStartY = rectEndY - rowHeight;
			} 

			canvas.drawRect(rectStartX, rectStartY + mChildHeight, rectEndX, rectEndY + mChildHeight, backPaint);
		}

		return super.drawChild(canvas, child, drawingTime);
	}

	@Override
	public boolean onTouchEvent(MotionEvent event) {
		// TODO: Implement this method
		isKeyEnter = false;
		isKeyDelete = false;
		return detector.onTouchEvent(event);
	}


	@Override
	public boolean dispatchTouchEvent(MotionEvent ev) {
		// TODO: Implement this method
		isKeyEnter = false;
		isKeyDelete = false;
		return super.dispatchTouchEvent(ev);
	}


	@Override
	public boolean dispatchKeyEvent(KeyEvent event) {
		// TODO: Implement this method
		switch (event.getKeyCode()) {
			case KeyEvent.KEYCODE_ENTER:
				isKeyEnter = true;
				isKeyDelete = false;
				break;
			case KeyEvent.KEYCODE_DEL:
				isKeyEnter = false;
				isKeyDelete = true;
				break;
			default:
				isKeyEnter = false;
				isKeyDelete = false;
				break;
		}
		return super.dispatchKeyEvent(event);
	}

	public void showIME(boolean show) {
		if (show) {
			textEditor.setSelection(textEditor.getText().length());
			imm.showSoftInput(textEditor, 0);

		} else {
			imm.hideSoftInputFromWindow(textEditor.getWindowToken(), 0); 
		}
	}

	public void getViewWidth(int viewWidth) {
	}

	public void getViewHeight(int viewHeight) {
		this.viewHeight = viewHeight;
	}

	public void scrollX(int scrollX) {
	}   

	public void scrollY(int scrollY) {
		this.scrollY = scrollY;
	}

	public int getRowHeight() {
		return this.rowHeight;
	}

	public int getTotalRows() {
		return this.totalRows;
	}

	public int getCurrRow() {
		return this.currRow;
	}

}
