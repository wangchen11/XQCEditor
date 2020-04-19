package com.editor.text;

import android.annotation.SuppressLint;
import android.content.*;
import android.graphics.*;
import android.os.*;
import android.text.*;
import android.text.Editable.Factory;
import android.text.style.SuggestionSpan;
import android.util.*;
import android.view.*;
import android.view.inputmethod.InputMethodManager;
import android.widget.*;

import java.util.Stack;

import person.wangchen11.editor.edittext.CodeInputFilter;
import person.wangchen11.window.ext.Setting;

public class TextEditorView extends EditText {
	private static final String TAG = "TextEditorView";
	private Paint mTextPaint;
	private float mSpaceWidth = 0;
	
	private boolean mSaveToHistory = true;
	private int mMaxSaveHistory = 20;
	private Stack<ReplaceBody> mUndoBodies = new Stack<ReplaceBody>();
	private Stack<ReplaceBody> mRedoBodies = new Stack<ReplaceBody>();
	private static String mReplaceTab = "    ";

	public TextEditorView(Context context) {
		super(context, null);
	}

	public TextEditorView(Context context, AttributeSet attrs) {
		super(context, attrs);
		init(context);
	}

	public void init(Context context) {
		setEditableFactory(new Factory(){
			@Override
			public Editable newEditable(CharSequence source) {
				return new SpannableStringBuilder(source){
					@Override
					public void setSpan(Object what, int start, int end,
							int flags) {
						if(what instanceof SuggestionSpan){
							Log.i(TAG, "jump setSpan:"+what.getClass());
							return ;
						}
						super.setSpan(what, start, end, flags);
					}
				};
				//return new QuicklySpannableStringBuilder(source);
				//return super.newEditable(source);
			}
		});
		setGravity(Gravity.TOP);
		getPaint().setTypeface(Typeface.MONOSPACE);
		getPaint().setColor(Setting.mConfig.mEditorConfig.mBaseFontColor);
		mTextPaint = new Paint();
		mTextPaint.setTypeface(Typeface.MONOSPACE);
		mTextPaint.setAntiAlias(true);
		mTextPaint.setColor(0xff888888);
		setHorizontallyScrolling(true);
		new Handler();
		setFilters(new InputFilter[] {inputFilter});
		addTextChangedListener(watcher);
		setTextScale(Setting.mConfig.mEditorConfig.mFontScale);
		setLineSpacing(0, 1.12f*Setting.mConfig.mEditorConfig.mLineScale);
		mScaleGestureDetector = new ScaleGestureDetector(context, new ScaleGestureDetector.OnScaleGestureListener() {
			@Override
			public void onScaleEnd(ScaleGestureDetector detector) {
				Log.i(TAG,"onScaleEnd");
				getParent().requestDisallowInterceptTouchEvent(false);
				mScaling = false;
			}
			
			@Override
			public boolean onScaleBegin(ScaleGestureDetector detector) {
				Log.i(TAG,"onScaleBegin");
				mScaling = true;
				return true;
			}
			
			@Override
			public boolean onScale(ScaleGestureDetector detector) {
				Log.i(TAG,"onScale");
				float scale = mTextScale*detector.getScaleFactor();
				setTextScale(scale);
				return true;
			}
		});
	}
	
	private boolean mScaling = false;
	private ScaleGestureDetector mScaleGestureDetector = null;
	@SuppressLint("ClickableViewAccessibility")
	@Override
	public boolean onTouchEvent(MotionEvent event) {
		Log.i(TAG, "onTouchEvent:"+event.getAction());
		if( (event.getAction()&MotionEvent.ACTION_MASK) == MotionEvent.ACTION_POINTER_DOWN && event.getPointerCount()==2 ){
			getParent().requestDisallowInterceptTouchEvent(true);
		}
		if( (event.getAction()&MotionEvent.ACTION_MASK) == MotionEvent.ACTION_UP && event.getPointerCount()<=1 ){
			getParent().requestDisallowInterceptTouchEvent(false);
		}
		mScaleGestureDetector.onTouchEvent(event);
		if(mScaling)
			return true;
		return super.onTouchEvent(event);
	}
	
	@Override
	protected void onSizeChanged(int w, int h, int oldw, int oldh) {
		//if(getSelectionStart()==getSelectionEnd() && getSelectionEnd()>=0 )
		//	bringPointIntoView(getSelectionEnd());
		super.onSizeChanged(w, h, oldw, oldh);
	}
	
	private float mTextScale = 1.0f;
	public void setTextScale(float textScale){
		mTextScale = textScale;
		float textSize = dip2px(getContext(),12)*mTextScale;
		mTextPaint.setTextSize(textSize);
		mSpaceWidth = mTextPaint.measureText("0");
		getPaint().setTextSize(textSize);
		setPadding(getBoundOfLeft(), 0, 0, 0);
	}
	
	@Override
	public void setText(CharSequence text, BufferType type) {
		super.setText(replaceAllLineStartTab(text),type);
		setPadding(getBoundOfLeft(), 0, 0, 0);
	}
	
	public String getTextEx(){
		return replaceAllLineStartSpace(getText());
	}
	
	public float dip2px(Context context, float dpValue) {
		float scale =context.getResources().getDisplayMetrics().density;
		return dpValue * scale + 0.5f;
	}

	public float px2dip(Context context, float pxValue) {
		float scale = context.getResources().getDisplayMetrics().density;
		return pxValue / scale + 0.5f;
	}
	
	private InputFilter inputFilter = new CodeInputFilter(){
		public String getTabString() {
			return mReplaceTab;
		};
		@Override
		public boolean isEnable() {
			return mSaveToHistory;
		}
	};

    private TextWatcher watcher = new TextWatcher(){
    	
    	int beforeStart = 0;
    	int beforEnd = 0;
    	CharSequence beforCharSequence = "";
        @Override
        public void beforeTextChanged(CharSequence s, int start, int count, int after) {
        	Log.i(TAG, "beforeTextChanged"+":"+start+":"+count+":"+after);
        	beforeStart = start;
        	beforEnd = start+count;
        	beforCharSequence = s.subSequence(beforeStart, beforEnd);
        }

        @Override
        public void onTextChanged(CharSequence s, int start, int before, int count) {
    		//saved to history to be undo or redo 
    		if(mSaveToHistory){
    			mRedoBodies.clear();
    			if(mMaxSaveHistory>0){
    				if(mUndoBodies.size()>mMaxSaveHistory)
    					mUndoBodies.remove(0);
    				ReplaceBody body=new ReplaceBody(beforeStart, beforEnd, beforCharSequence,s.subSequence(start, start+count), 0, count, getSelectionStart(), getSelectionEnd());
    				if(!mUndoBodies.isEmpty() && mUndoBodies.peek().addBody(body))
    				{
    				}
    				else
    					mUndoBodies.push(body);
    			}
    		}else{
    			setSelection(start, start+count);
    		}
    		TextEditorView.this.onTextChanged(s,start,before,count);
        	Log.i(TAG, "onTextChanged"+":"+start+":"+before+":"+count);
        }

        @Override
        public void afterTextChanged(Editable edit) {
        	Log.i(TAG, "afterTextChanged");
			//setPadding(getBoundOfLeft(), 0, 0, 0);
			TextEditorView.this.afterTextChanged(edit);
        }
	};
	

	public void afterTextChanged(Editable edit) {
	}
	
	public void onTextChanged(CharSequence s, int start, int before, int count){
	}

	public boolean gotoLine(int line) {
		--line;
		
		if (line > getLineCount()){
			setSelection(getText().toString().length());
			return false;
		}
			

		Layout layout = getLayout();
		setSelection(layout.getLineStart(line), layout.getLineEnd(line));
		return true;
	}


	@Override
	protected void onDraw(Canvas canvas) {
		super.onDraw(canvas);
		canvas.save();
		drawText(canvas);
		canvas.restore();
	}
	
	public void drawText(Canvas canvas) {
		Layout layout = getLayout();
		if (layout!=null) {
			for (int i=0;i < getLineCount();i++) {
				layout.getLineDescent(i);
				float textX = getPaddingLeft() - getWidthOfBit(getBitOfNum(i+1)+1);
				float textY = layout.getLineBaseline(i);//+(i) * getLineHeight();
				canvas.drawText(String.valueOf(i + 1), textX, textY, mTextPaint);
			}
		}
		
		float oldPaintWidth = mTextPaint.getStrokeWidth();
		float newPaintWidth = mSpaceWidth/8;
		mTextPaint.setStrokeWidth(newPaintWidth);
		canvas.drawLine(getPaddingLeft()-mSpaceWidth/4, 0, getPaddingLeft()-mSpaceWidth/4, (getLineCount()) * getLineHeight(), mTextPaint);
		mTextPaint.setStrokeWidth(oldPaintWidth);
	}

	public int getRowHeight() {
		return getLineHeight();
	}

	public int getTotalRows() {
		return getLineCount();
	}

	public int getCurrRow() {
		return getLayout().getLineForOffset(getSelectionStart()) + 1;
	}


	public interface OnTextChangedListener {
		public void onTextChanged(String text);
	}

	@Override
	protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
		//long timeStart = System.currentTimeMillis();
		Layout layout = getLayout();
		if(layout==null){
			super.onMeasure(widthMeasureSpec, heightMeasureSpec);
			setPadding(getBoundOfLeft(), 0, 0, 0);
		}
		else
			setMeasuredDimension(layout.getWidth(), layout.getHeight());
		//Log.i(TAG, "onMeasure used:"+(System.currentTimeMillis()-timeStart));
		
	}

	private float getWidthOfBit(int bit)
	{
		return bit * mSpaceWidth ;
	}

	public int getBoundOfLeft(){
		int left=(int) getWidthOfBit(1+getBitOfNum(getLineCount()));
		return (left);
	}

	private int getBitOfNum(int num)
	{
		if(num<10)
			return 1;
		if(num<100)
			return 2;
		if(num<1000)
			return 3;
		if(num<10000)
			return 4;
		if(num<100000)
			return 5;
		if(num<1000000)
			return 6;
		if(num<10000000)
			return 7;
		if(num<100000000)
			return 8;
		return 0;
	}

	public void closeInputMethod() {
		Log.i(TAG, "closeInputMethod");
		View editView=this;
	    InputMethodManager imm = (InputMethodManager) getContext().getSystemService(Context.INPUT_METHOD_SERVICE);
	    boolean isOpen = imm.isActive();
	    if (isOpen) {
	        imm.hideSoftInputFromWindow(editView.getApplicationWindowToken(), 0 );
	    }
	}
	
	public void cleanRedo() {
		mRedoBodies.clear();
	}

	public void cleanUndo() {
		mUndoBodies.clear();
	}

	public void setMaxSaveHistory(int i) {
		mMaxSaveHistory = i;
	}

	public boolean canRedo() {
		return mRedoBodies.size()>0;
	}

	public boolean canUndo() {
		return mUndoBodies.size()>0;
	}

	public boolean redo() {
		if(!canRedo())
			return false;
		ReplaceBody body=mRedoBodies.pop();
		ReplaceBody replaceBody=body.getRedoBody();
		if(mMaxSaveHistory>0){
			if(mUndoBodies.size()>mMaxSaveHistory)
				mUndoBodies.remove(0);
			mUndoBodies.push(body);
		}
		mSaveToHistory = false;
		getEditableText().replace(replaceBody.mSt, replaceBody.mEn, replaceBody.mText, replaceBody.mStart, replaceBody.mEnd);
		mSaveToHistory = true;
		return true;
	}
	
	public boolean undo() {
		if(!canUndo())
			return false;
		ReplaceBody body=mUndoBodies.pop();
		ReplaceBody replaceBody=body.getUndoBody();
		if(mMaxSaveHistory>0){
			if(mRedoBodies.size()>mMaxSaveHistory)
				mRedoBodies.remove(0);
			mRedoBodies.push(body);
		}
		mSaveToHistory = false;
		getEditableText().replace(replaceBody.mSt, replaceBody.mEn, replaceBody.mText, replaceBody.mStart, replaceBody.mEnd);
		mSaveToHistory = true;
		return true;
	}

	class ReplaceBody {
		int mSt;
		int mEn;
		CharSequence mSubtext;
		CharSequence mText;
		int mStart;
		int mEnd;
		int mSelectionStart;
		int mSelectionEnd;

		public ReplaceBody(int st, int en, CharSequence subtext,
				CharSequence text, int start, int end, int selectionStart,
				int selectionEnd) {
			mSt = st;
			mEn = en;
			mSubtext = subtext;
			mText = text;
			mStart = start;
			mEnd = end;
			mSelectionStart = selectionStart;
			mSelectionEnd = selectionEnd;
		}

		public ReplaceBody getUndoBody() {
			return new ReplaceBody(mSt, mSt + mEnd - mStart, mText, mSubtext,
					0, mSubtext.length(), mSelectionStart, mSelectionEnd);
		}

		public ReplaceBody getRedoBody() {
			return this;
		}

		public boolean isDelete() {
			if (mEn - mSt > 0 && mStart == 0 && mEnd == 0)
				return true;
			return false;
		}

		public boolean isInsert() {
			if (mSt == mEn && mText != null && mText.length() != 0
					&& mEnd - mStart > 0)
				return true;
			Log.i("isInsert", "false");
			return false;
		}

		public boolean addBody(ReplaceBody body) {
			if (isDelete() && body.isDelete() && mSt == body.mEn) {// 合并相连的删除
				this.mSt = body.mSt;
				this.mSubtext = body.mSubtext.toString() + this.mSubtext;
				this.mSelectionStart = body.mSelectionStart;
				return true;
			}
			Log.i("addBody", "addBody");
			if (isInsert() && body.isInsert()
					&& mSt + mText.length() == body.mSt) {// 合并相连的插入
				Log.i("addBody", "合并相连的插入 ");
				if (body.mText.toString().contains("\n")) {
					return false;
				}
				this.mText = this.mText
						+ body.mText.subSequence(body.mStart, body.mEnd)
								.toString();
				this.mEnd += body.mEnd - body.mStart;
				this.mSelectionEnd = body.mSelectionEnd;
				return true;
			}
			return false;
		}
	}
	
	public boolean findString(String string) {
		int index=getText().toString().indexOf(string, getSelectionEnd());
		if(index == -1)
			return false;
		setSelection(index, index+string.length());
		return true;
	}

	public boolean insertText(String str){
		str = str.replaceAll("\t", mReplaceTab );
		getText().replace(getSelectionStart(), getSelectionEnd(), str);
		return true;
	}
	
	public boolean replaceString(String str){
		if(getSelectionStart()==getSelectionEnd())
			return false;
		insertText(str);
		return true;
	}
	
	public boolean replaceFindString(String find,String replace){
		if(find.length() <= 0)
			return false;
		setSelection(getSelectionStart());
		if(!findString(find))
			return false;
		replaceString(replace);
		return true;
	}
	
	public boolean replaceAll(String find,String replace){
		int start=getSelectionStart();
		int end = getSelectionEnd();
		boolean finded=false;
		setSelection(0);
		while(replaceFindString(find,replace))
		{
			finded=true;
		}
		if(!finded)
			setSelection(start, end);
		return finded;
	}

	private static String getNStr(int n,String str){
		StringBuilder builder = new StringBuilder();
		for(;n>0;n--)
			builder.append(str);
		return builder.toString();
	}
	
	private static String replaceAllLineStartTab(CharSequence str){
		StringBuilder stringBuilder = new StringBuilder();
		boolean newLine = true;
		int spaceNumber = 0;
		for(int i=0;i<str.length();i++){
			char ch = str.charAt(i);
			if(ch=='\n'){
				if(spaceNumber!=0){
					stringBuilder.append(getNStr(spaceNumber," "));
					spaceNumber = 0;
				}
				
				newLine = true;
				stringBuilder.append(ch);
			}else{
				if(newLine){
					if(ch == '\t'){
						spaceNumber+=mReplaceTab.length();
					}else if(ch == ' '){
						spaceNumber++;
					}else{
						if(spaceNumber!=0){
							stringBuilder.append(getNStr(spaceNumber," "));
							spaceNumber = 0;
						}
						
						newLine = false;
						stringBuilder.append(ch);
					}
				}else{
					stringBuilder.append(ch);
				}
			}
		}
		if(spaceNumber!=0){
			stringBuilder.append(getNStr(spaceNumber," "));
			spaceNumber = 0;
		}
		
		return stringBuilder.toString();
	}
	
	private static String replaceAllLineStartSpace(CharSequence str){
		StringBuilder stringBuilder = new StringBuilder();
		boolean newLine = true;
		int spaceNumber = 0;
		for(int i=0;i<str.length();i++){
			char ch = str.charAt(i);
			if(ch=='\n'){
				if(spaceNumber!=0){
					stringBuilder.append(getNStr(spaceNumber/mReplaceTab.length(),"\t"));
					stringBuilder.append(getNStr(spaceNumber%mReplaceTab.length()," "));
					spaceNumber = 0;
				}
				
				newLine = true;
				stringBuilder.append(ch);
			}else{
				if(newLine){
					if(ch == '\t'){
						spaceNumber+=mReplaceTab.length();
					}else if(ch == ' '){
						spaceNumber++;
					}else{
						if(spaceNumber!=0){
							stringBuilder.append(getNStr(spaceNumber/mReplaceTab.length(),"\t"));
							stringBuilder.append(getNStr(spaceNumber%mReplaceTab.length()," "));
							spaceNumber = 0;
						}
						
						newLine = false;
						stringBuilder.append(ch);
					}
				}else{
					stringBuilder.append(ch);
				}
			}
		}
		if(spaceNumber!=0){
			stringBuilder.append(getNStr(spaceNumber/mReplaceTab.length(),"\t"));
			stringBuilder.append(getNStr(spaceNumber%mReplaceTab.length()," "));
			spaceNumber = 0;
		}
		
		return stringBuilder.toString();
	}
}
