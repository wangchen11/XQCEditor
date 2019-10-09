package com.editor.text;
import android.content.*;
import android.widget.*;
import android.util.*;
import android.view.*;

public class ScrollViewText extends ScrollView {

	private int rowHeight;
	private int totalRows;
	private HorScrollViewText HorScrollText;

	public ScrollViewText(Context context) {
		super(context);
	}


	public ScrollViewText(Context context, AttributeSet attrs) {
		super(context, attrs);

	}

	
	@Override
	protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
		// TODO: Implement this method
		super.onMeasure(widthMeasureSpec, heightMeasureSpec);
		HorScrollText = (HorScrollViewText) getChildAt(0);

		rowHeight = HorScrollText.getRowHeight();
		HorScrollText.getCurrRow();
		totalRows = HorScrollText.getCurrRow();


	}


	@Override
	public boolean executeKeyEvent(KeyEvent event) {
		// TODO: Implement this method
		if (event.getKeyCode() == KeyEvent.KEYCODE_DEL) {
			if (totalRows * rowHeight <= getHeight() / 2) {
				scrollTo(getScrollX(), 0);
			}
		}
		return super.executeKeyEvent(event);
	}



	@Override
	public void computeScroll() {
		// TODO: Implement this method
		super.computeScroll();
		HorScrollText.getContainerView().scrollY(getScrollY());

		HorScrollText.getContainerView().getViewHeight(getHeight());
	}

}
