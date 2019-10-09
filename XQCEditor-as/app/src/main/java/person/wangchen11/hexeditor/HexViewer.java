package person.wangchen11.hexeditor;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Typeface;
import android.graphics.Paint.FontMetrics;
import android.util.AttributeSet;
import android.view.View;

public class HexViewer extends View {
	private HexHelper mHexHelper = null;
	private int mCountOfEachLine = 16;
	private Paint mLineNumberPaint = null;
	private Paint mHexTextPaint = null;
	private float mLineScale = 1.2f;
	private float mFontSize = 32;

	public HexViewer(Context context) {
		super(context);
		init();
	}

	public HexViewer(Context context, AttributeSet attrs) {
		super(context, attrs);
		init();
	}

	public HexViewer(Context context, AttributeSet attrs, int defStyleAttr) {
		super(context, attrs, defStyleAttr);
		init();
	}

	public void setHexHelper(HexHelper helper){
		mHexHelper = helper;
		invalidate();
	}
	
	private void init(){
		mLineNumberPaint = new Paint();
		mHexTextPaint = new Paint();
		mLineNumberPaint.setTypeface(Typeface.MONOSPACE);
		mHexTextPaint.setTypeface(Typeface.MONOSPACE);
		float fontSize = mFontSize;
		mLineNumberPaint.setTextSize(fontSize);
		mHexTextPaint.setTextSize(fontSize);
	}
	
	@Override
	protected void onDraw(Canvas canvas) {
		FontMetrics fm = mHexTextPaint.getFontMetrics();
		float textCenterVerticalBaselineY = mHexTextPaint.getTextSize()/2 + (fm.descent - fm.ascent) / 2- fm.descent ;
		
		float diryX = 0; 
		diryX = mLineNumberPaint.measureText(String.format("%08X", 0)); 
		float eachByteWidth = mHexTextPaint.measureText(String.format("000")); 
		float lineSpace = mHexTextPaint.getTextSize()*mLineScale;
		for(int i=0;i<mCountOfEachLine;i++){
			String str = String.format("%02x", i);
			canvas.drawText(str, diryX+(i*eachByteWidth), (lineSpace-mHexTextPaint.getTextSize())+textCenterVerticalBaselineY, mLineNumberPaint);
		}
	}
	
}
