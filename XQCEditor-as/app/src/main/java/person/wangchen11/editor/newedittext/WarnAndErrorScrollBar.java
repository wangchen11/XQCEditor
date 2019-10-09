package person.wangchen11.editor.newedittext;

import java.util.List;

import person.wangchen11.editor.edittext.WarnAndError;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.view.View;

public class WarnAndErrorScrollBar extends View {
	private List<WarnAndError> mWarnAndErrors = null;
	private int mLineCount = 0;
	private Paint mPaint = new Paint(); 
	public WarnAndErrorScrollBar(Context context) {
		super(context);
	}
	
	public WarnAndErrorScrollBar(Context context, AttributeSet attrs ) {
		super(context, attrs);
	}
	
	public WarnAndErrorScrollBar(Context context, AttributeSet attrs,
			int defStyleAttr) {
		super(context, attrs, defStyleAttr);
	}
	
	public void setWarnAndErrors(List<WarnAndError> warnAndErrors,int lineCount){
		mWarnAndErrors = warnAndErrors;
		mLineCount = lineCount;
		invalidate();
	}
	
	@Override
	protected void onDraw(Canvas canvas) {
		if(mLineCount==0){
			//canvas.drawColor(Color.BLACK);
		}
		else if(mWarnAndErrors!=null)
		{
			float space = getHeight()/64;
			for(WarnAndError warnAndError:mWarnAndErrors){
				float startY = warnAndError.mLine/(float)mLineCount*getHeight();
				mPaint.setColor(warnAndError.mColor);
				canvas.drawRect(0, startY,getWidth(),startY+space,mPaint);
			}
		}
	}
}
