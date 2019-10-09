package person.wangchen11.waps;

import person.wangchen11.xqceditor.R;
import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

public class AdRelativeLayoutWithClose extends RelativeLayout {
	public ImageView mColseAdButton = null;
	public TextView  mAdSuggestInfo = null;

	private void init(Context context){
		addSuggestInfo();
	}
	
	public AdRelativeLayoutWithClose(Context context) {
		super(context);
		init(context);
	}

	public AdRelativeLayoutWithClose(Context context, AttributeSet attrs) {
		super(context, attrs);
		init(context);
	}
	
	@Override
	protected void onLayout(boolean changed, int l, int t, int r, int b) {
		super.onLayout(changed, l, t, r, b);
		/*
		if(mColseAdButton!=null)
		if(getHeight()>0 && mColseAdButton.getVisibility()!=View.VISIBLE){
	    	mColseAdButton.setVisibility(View.VISIBLE);
		}*/
	}

	public void addCloseButton(){
    	mColseAdButton = new ImageView(getContext());
    	mColseAdButton.setImageResource(R.drawable.cancel);
    	LayoutParams layoutParams = new LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
    	layoutParams.addRule(CENTER_VERTICAL);
    	mColseAdButton.setVisibility(View.GONE);
    	this.addView(mColseAdButton,layoutParams);
	}

	public void addSuggestInfo(){
		mAdSuggestInfo = new TextView(getContext());
		mAdSuggestInfo.setText(R.string.ad_suggest_info);
    	LayoutParams layoutParams = new LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
    	layoutParams.addRule(ALIGN_PARENT_RIGHT);
    	layoutParams.addRule(CENTER_VERTICAL);
    	this.addView(mAdSuggestInfo,layoutParams);
	}
}
