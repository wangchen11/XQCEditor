package person.wangchen11.waps;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.LinearLayout;

public class AdLinearLayout extends LinearLayout{
	
	public AdLinearLayout(Context context) {
		super(context);
	}

	public AdLinearLayout(Context context, AttributeSet attrs) {
		super(context, attrs);
	}
	
    @Override
    protected void onLayout(boolean changed, int l, int t, int r, int b) {
    	super.onLayout(changed, l, t, r, b);
    	setMinimumHeight(getHeight());
    }
}
