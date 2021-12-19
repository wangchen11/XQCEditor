package person.wangchen11.editor.edittext;

import java.util.Iterator;
import java.util.List;

import person.wangchen11.waps.Waps;
import person.wangchen11.xqceditor.R;
import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.Typeface;
import android.support.v4.widget.EdgeEffectCompat;
import android.support.v7.widget.PopupMenu;
import android.support.v7.widget.PopupMenu.OnMenuItemClickListener;
import android.text.ClipboardManager;
import android.text.Editable;
import android.text.Selection;
import android.text.TextPaint;
import android.text.TextWatcher;
import android.util.AttributeSet;
import android.util.Log;
import android.view.GestureDetector;
import android.view.GestureDetector.OnDoubleTapListener;
import android.view.GestureDetector.OnGestureListener;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.ScaleGestureDetector;
import android.view.ViewParent;
import android.view.ScaleGestureDetector.OnScaleGestureListener;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputConnection;
import android.view.inputmethod.InputMethodManager;
import android.widget.Scroller;
import android.widget.TextView;

@SuppressWarnings("deprecation")
public class MyEditText extends View implements OnGestureListener,TextWatcher, OnScaleGestureListener, OnDoubleTapListener {
	protected static final String TAG="MyEditText";

	public static float mFontScale = 1.0f;
	public static float mLineScale = 1.0f;
	
	private GestureDetector mGestureDetector;
	private ScaleGestureDetector mScaleGestureDetector;
	private Scroller mScroller;
	private EdgeEffectCompat mEdgeEffectTop;
	private EdgeEffectCompat mEdgeEffectBottom;
	private ScrollBar mScrollBar;
	private Bitmap mBitmapSelectLeft=null;
	private Bitmap mBitmapSelectRight=null;
	protected MyInputConnection mBaseInputConnection;
	MyLayout mLayout;
	Paint mCursorPaint=new Paint();
	Paint mSelectionPaint=new Paint();
	Paint mSelectionBackgroundPaint=new Paint();
	TextPaint mTextPaint=new TextPaint();
	TextPaint mLineNumberPaint=new TextPaint();
	public MyEditText(Context context) {
		super(context);
		init();
	}
	

	public MyEditText(Context context, AttributeSet attrs) {
		super(context, attrs);
		init();
	}

	public void insertText(CharSequence charSequence){
		mBaseInputConnection.commitText(charSequence, getCursor());
	}
	
	public void setText(CharSequence charSequence){
		if(mBaseInputConnection==null)
			mBaseInputConnection=new MyInputConnection(this)
		{
		    public boolean clearMetaKeyStates(int states) {
		    	mDownState=0;
		    	return super.clearMetaKeyStates(states);
		    }
		    
			@Override
		    public boolean performContextMenuAction(int id) {
				MyEditText.this.performContextMenuAction(id);
		        return super.performContextMenuAction(id);
		    }
			
			@Override
			public boolean setSelection(int start, int end) {
				
				if(start==end){
					post(new Runnable() {
						@Override
						public void run() {
							bringPosToVisible(getCursor());
						}
					});
				}
				else
				if(isMoveSelectionStart()){
					postDelayed(new Runnable() {
						@Override
						public void run() {
							bringPosToVisible(getSelectionStart());
						}
					},100);
				}else
				if(isMoveSelectionEnd()){
					postDelayed(new Runnable() {
						@Override
						public void run() {
							bringPosToVisible(getSelectionEnd());
						}
					},100);
				}else{
					postDelayed(new Runnable() {
						@Override
						public void run() {
							bringPosToVisible(getSelectionEnd());
						}
					},100);
				}
				return super.setSelection(start, end);
			}
		};
		
		mBaseInputConnection.getEditable().clear();
		if(charSequence!=null){
			mBaseInputConnection.getEditable().insert(0, charSequence);
		}
		else{
		}

		if(getCursor()>mBaseInputConnection.getEditable().length())
			setCursor(mBaseInputConnection.getEditable().length());
		
		cleanRedo();
		cleanUndo();
		makeLayout();
	}
	
	private float mSize = 1f;
	public float getTextSize()
	{
		return mSize;
	}
	private float mSpaceWidth = 0;
	public void setTextSize(float size){
		mSize=size;
		mTextPaint.setTextSize(size*mFontScale);
		getLayout().setPaint(mTextPaint);
		mLineNumberPaint=new TextPaint(mTextPaint);
		mLineNumberPaint.setColor(Color.GRAY);
		mLineNumberPaint.setTypeface(Typeface.MONOSPACE);
		mSpaceWidth = mLineNumberPaint.measureText("0");
		float strokeWidth=mSpaceWidth/4;
		if(strokeWidth<1)
			strokeWidth=1;
		mCursorPaint.setStrokeWidth(strokeWidth);
		getLayout().setLineHeight(size*mFontScale*1.2f*mLineScale);
	}
	
	public Editable getText(){
		return mBaseInputConnection.getEditable();
	}
	
	public MyLayout getLayout(){
		if(mBaseInputConnection==null)
			return null;
		return mBaseInputConnection.getLayout();
	}
	
	private void init()
	{
		mBitmapSelectLeft=BitmapFactory.decodeResource(getResources(), R.drawable.select_left);
		mBitmapSelectRight=BitmapFactory.decodeResource(getResources(), R.drawable.select_right);
		setWillNotCacheDrawing(true);
		setText("");
		
		mScroller=new Scroller(getContext());
		mGestureDetector=new GestureDetector(getContext(), this);
		mGestureDetector.setOnDoubleTapListener(this);
		mScaleGestureDetector=new ScaleGestureDetector(getContext(), this);
		mEdgeEffectTop=new EdgeEffectCompat(getContext());
		mEdgeEffectTop.setSize(getWidth(), getHeight());
		mEdgeEffectBottom=new EdgeEffectCompat(getContext());
		mEdgeEffectBottom.setSize(getWidth(), getHeight());
		mScrollBar=new ScrollBar();
		float density=getContext().getResources().getDisplayMetrics().density;
		mScrollBar.setSize(getHeight(),density*4);
		float size=density*12f;
		setTextSize(size);
		mSelectionBackgroundPaint.setColor(Color.argb(0x60, 0x80, 0xf8, 0x80));
		mSelectionPaint.setColor(Color.argb(0xff, 0x80, 0x88, 0xff));
		mLineNumberPaint.setTypeface(Typeface.MONOSPACE);
		setScrollContainer(true);
		this.post(new Runnable(){
			int colors[]={
					Color.BLACK,Color.argb(0xff, 0xff, 0xff, 0xff),
					};
			int num=0;
			@Override
			public void run() {
				if(num>=colors.length)
					num=0;
				mCursorPaint.setColor(colors[num]);
				MyEditText.this.postDelayed(this, 360);
				postInvalidateSpan(getSelectionStart(),getSelectionStart());
				postInvalidateSpan(getSelectionEnd(),getSelectionEnd());
				num++;
			}
		});
		setScrollbarFadingEnabled(true);
	}

    public void postInvalidateSpan(int start,int end)
    {
    	int left,top,right,bottom;
    	if(mLayout!=null){
    		int line=mLayout.getLineForOffset(start);
    		mLayout.getLineBounds(line, mBoundsOfCursor); 
    		left=mBoundsOfCursor.left;
    		top=mBoundsOfCursor.top;
    		
    		line=mLayout.getLineForOffset(end);
    		mLayout.getLineBounds(line, mBoundsOfCursor); 
    		right=mBoundsOfCursor.right;
    		bottom=mBoundsOfCursor.bottom;
    		postInvalidate(left, top, right, bottom);
    	}
    }
    
	@Override
	public InputConnection onCreateInputConnection(EditorInfo outAttrs) {
		outAttrs.imeOptions=EditorInfo.IME_FLAG_NO_ENTER_ACTION | EditorInfo.IME_ACTION_DONE | EditorInfo.IME_FLAG_NO_EXTRACT_UI;
		outAttrs.inputType=EditorInfo.TYPE_CLASS_TEXT ;//1310738;
		return mBaseInputConnection;
	}
	
	@Override
	protected void onSizeChanged(int w, int h, int oldw, int oldh) {
		post(new Runnable() {
			@Override
			public void run() {
				setCursor(getCursor());
				bringPosToVisible(getCursor());
			}
		});
		mEdgeEffectTop.setSize(getWidth(), getHeight());
		mEdgeEffectBottom.setSize(getWidth(), getHeight());
		float density=getContext().getResources().getDisplayMetrics().density;
		mScrollBar.setSize(getHeight(),density*4);
		//mEdgeEffectLeft.setSize(getHeight(),getWidth());
		super.onSizeChanged(w, h, oldw, oldh);
	}
	
	@SuppressLint("ClickableViewAccessibility")
	@Override
	public boolean onTouchEvent(MotionEvent event) {
		if(event.getAction()==MotionEvent.ACTION_DOWN)
		{
			mScroller.abortAnimation();
		}
		mGestureDetector.onTouchEvent(event);
		mScaleGestureDetector.onTouchEvent(event);
		return true;
	}

	int i=0;
	private Rect mBoundsOfCursor=new Rect();
	private Rect mBounds=new Rect();
	@Override
	protected void onDraw(Canvas canvas) {
		if(mLayout!=null)
		{
			//计算选择条
			int selectLineStart=0;
			int selectLineEnd=0;
			float selectStartX=0;
			float selectEndX=0;
			if(isSelection())
			{
				int start=getSelectionStart();
				int end=getSelectionEnd();
				selectLineStart=mLayout.getLineForOffset(start);
				selectLineEnd=mLayout.getLineForOffset(end);
				selectStartX=mLayout.getPrimaryHorizontal(start);
				selectEndX=mLayout.getPrimaryHorizontal(end);
			}
			
			int cursor=getCursor();
			float x=mLayout.getPrimaryHorizontal(cursor);
			int line=mLayout.getLineForOffset(cursor);
			mLayout.getLineBounds(line, mBoundsOfCursor); 
			canvas.drawLine(x, mBoundsOfCursor.top, x, mBoundsOfCursor.bottom, mCursorPaint);
			
			//////////////////////////////////////////
			canvas.getClipBounds(mBounds);
			int lineStart=mLayout.getLineForVertical(mBounds.top);
			int lineEnd=mLayout.getLineForVertical(mBounds.bottom);
			float descent=mLayout.getPaint().getFontMetrics().descent;
			for(int i=lineStart;i<=lineEnd;i++){
				
				mLayout.getLineBounds(i, mBounds);
				if(mBounds.left>=0)
				{
					String value=String.valueOf(i+1);
					canvas.drawText(value, mBounds.left-(mSpaceWidth*(value.length()+1)), mBounds.bottom - descent , mLineNumberPaint);
				}
				if(isSelection())
				{//画选择框 
					if(i>selectLineStart && i<selectLineEnd){
						canvas.drawRect(mBounds, mSelectionBackgroundPaint);
					}
					else
					if(i==selectLineStart )
					{
						if(i==selectLineEnd){
							canvas.drawRect(selectStartX, mBounds.top, selectEndX, mBounds.bottom, mSelectionBackgroundPaint);
						}
						else{
							canvas.drawRect(selectStartX, mBounds.top, mBounds.right, mBounds.bottom, mSelectionBackgroundPaint);
						}
					}
					else
					if(i==selectLineEnd )
					{
						canvas.drawRect(0, mBounds.top, selectEndX, mBounds.bottom, mSelectionBackgroundPaint);
					}
				}
				//mLineNumberPaint
			}
			
			{
				float lineStartY = 0;
				float lineEndY = 0;
				mLayout.getLineBounds(lineStart, mBounds);
				lineStartY = mBounds.top;
				mLayout.getLineBounds(lineEnd, mBounds);
				lineEndY = mBounds.bottom;
				float oldWidth = mLineNumberPaint.getStrokeWidth();
				mLineNumberPaint.setStrokeWidth(mSpaceWidth/8);
				canvas.drawLine(mBounds.left - mSpaceWidth/4, lineStartY, mBounds.left - mSpaceWidth/4, lineEndY, mLineNumberPaint);
				mLineNumberPaint.setStrokeWidth(oldWidth);
			}
			
			try {
				mLayout.draw(canvas);
			} catch (OutOfMemoryError e) {
				e.printStackTrace();
			}

			if(isSelection()){
				//int r=(int) mSelectBarRadius;
				//canvas.drawCircle(x, mBoundsOfCursor.bottom + r, r, mSelectionPaint);
				canvas.drawBitmap(mBitmapSelectRight, x-(mBitmapSelectLeft.getWidth()*10f/72), mBoundsOfCursor.bottom, mSelectionPaint);
				
				cursor=getSelectionStart();
				x=mLayout.getPrimaryHorizontal(cursor);
				line=mLayout.getLineForOffset(cursor);
				mLayout.getLineBounds(line, mBoundsOfCursor); 
				canvas.drawLine(x, mBoundsOfCursor.top, x, mBoundsOfCursor.bottom, mCursorPaint);

				//canvas.drawCircle(x, mBoundsOfCursor.bottom + r, r, mSelectionPaint);
				canvas.drawBitmap(mBitmapSelectLeft, x-(mBitmapSelectLeft.getWidth()*62f/72), mBoundsOfCursor.bottom, mSelectionPaint);
			}
			
		}
		canvas.save();
		canvas.rotate(90);
		canvas.translate( getScrollY(),-(getScrollX()+getWidth()) );
		mScrollBar.draw(canvas);
		if(mLayout instanceof EditableWithLayout) {
			EditableWithLayout editableWithLayout = (EditableWithLayout) mLayout;
			mScrollBar.drawWarnAndError(canvas,editableWithLayout.getWarnAndErrors(),mLayout.getLineCount());
		}
		canvas.save();
		/*
		canvas.restore();
		canvas.save();
		canvas.translate(getScrollX(), getScrollY());
		mEdgeEffectTop.draw(canvas);
		canvas.restore();
		canvas.save();
		canvas.rotate(180);
		canvas.translate( -(getScrollX()+getWidth()), -(getScrollY()+getHeight()) );
		mEdgeEffectBottom.draw(canvas);
		canvas.restore();
		canvas.rotate(-90);
		canvas.translate( -(getScrollY()+getHeight()) ,getScrollX() );
		//mEdgeEffectLeft.draw(canvas);
		canvas.restore();
		canvas.save();
		*/
	}
	
	protected void makeLayout(){
		if(getText()!=null)
		{
			mLayout=mBaseInputConnection.getLayout();
			//mLayout=new StaticLayout(mEditable, mTextPaint, Integer.MAX_VALUE,Layout.Alignment.ALIGN_NORMAL,1f,0f,false);
			//mLayout=new DynamicLayout(getText(),mTextPaint,Integer.MAX_VALUE,Layout.Alignment.ALIGN_NORMAL,1f,0f,false);
		}
	}

	public TextPaint getPaint(){
		return mTextPaint;
	}
	
	private float getWidthOfBit(int bit)
	{
		/*
		String str="";
		for(int i=0;i<bit;i++)
		{
			str+='0';
		}
		return getPaint().measureText(str);*/
		return bit * mSpaceWidth;
	}

	public int getBoundOfLeft(){
		int left=(int) getWidthOfBit(1+getBitOfNum(getLineCount()));
		return -(left);
	}

	public int getBoundOfRight(){
		if(mLayout==null)
			return getWidth();
		return Integer.MAX_VALUE/2;
	}
	
	public int getBoundOfTop(){
		return 0;//(int) -mTextPaint.getTextSize();
	}
	
	public int getBoundOfBottom(){
		if(mLayout==null)
			return getHeight();
		int h1=mLayout.getHeight();
		int h2=getHeight();
		return h1>h2?h1:h2;
	}
	
	@Override
	public boolean canScrollHorizontally(int direction) {
		int newX=getScrollX()+direction;
		if( newX >= getBoundOfLeft()  && newX<=getBoundOfRight()  )
			return true;
		return false;
	}
	
	@Override
	public boolean canScrollVertically(int direction) {
		int newY=getScrollY()+direction;
		if( newY >=getBoundOfTop() && newY <= getBoundOfBottom() )
			return true;
		return false;
	}
	
	@Override
	public void scrollTo(int x, int y) {
		int left=getBoundOfLeft();
		int right=getBoundOfRight();
		int top=getBoundOfTop();
		int bottom=getBoundOfBottom();
		if(x<left)
		{
			//mEdgeEffectLeft.onAbsorb(left-x);
			x=left;
		}
		if(x>right)
			x=right;
		if(y<top)
		{
			//mEdgeEffectTop.onAbsorb(top-y);
			y=top;
		}
		if(y>bottom)
		{
			//mEdgeEffectBottom.onAbsorb(y-bottom);
			y=bottom;
		}
		mScrollBar.setPosition(bottom-top+getHeight(), y-top, y-top+getHeight());
		//awakenScrollBars();
		super.scrollTo(x, y);
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

    public int getLineCount() {
        return mLayout != null ? mLayout.getLineCount() : 0;
    }

    public boolean isSelection(){
    	return getSelectionStart()!=getSelectionEnd();
    }

    public int getSelectionStart(){
    	return Selection.getSelectionStart(mBaseInputConnection.getEditable());
    }
    
    public int getSelectionEnd(){
    	return Selection.getSelectionEnd(mBaseInputConnection.getEditable());
    }
    
    public int getCursor(){
    	return getSelectionEnd();
    }
    
    public void bringPosToVisible(int pos){
    	if(mLayout!=null){
    		if(pos>=0 && pos<=getText().length()){
    			float x=mLayout.getPrimaryHorizontal(pos);
    			int line=mLayout.getLineForOffset(pos);
    			mLayout.getLineBounds(line, mBoundsOfCursor); 
    			int scrollX=getScrollX();
    			int scrollY=getScrollY();
    			if(mBoundsOfCursor.top<scrollY){
    				scrollY=mBoundsOfCursor.top;
    			}
    			if(mBoundsOfCursor.bottom>scrollY+getHeight())
    				scrollY=mBoundsOfCursor.bottom-getHeight();
    			
    			if(x<scrollX+mTextPaint.getTextSize())
    				scrollX= (int)( x-mTextPaint.getTextSize());
    			
    			if(x>scrollX+getWidth()-mTextPaint.getTextSize())
    				scrollX=(int) (x-getWidth()+mTextPaint.getTextSize());
    			
    			scrollTo(scrollX, scrollY);
    		}
    	}
    	postInvalidate();
    }
    
    public boolean setCursor(int cursor){
    	return setSelection(cursor, cursor);
    }
    
	public void showSoftKeyboard(){
		setFocusableInTouchMode(true);
		requestFocus();
		InputMethodManager inputMethodManager=(InputMethodManager) getContext().getSystemService(Context.INPUT_METHOD_SERVICE);
		inputMethodManager.showSoftInput(this,InputMethodManager.SHOW_FORCED);
	}
	
	protected int mDownState=0;
	public boolean isMoveSelectionStart(){
		return mDownState==1;
	}
	
	public boolean isMoveSelectionEnd(){
		return mDownState==2;
	}
	
	private boolean isMoveSelection(){
		return mDownState!=0;
	}
	
	@Override
	public boolean onDown(MotionEvent e) {
		mDownState=0;
		if(isSelection())
		{	
			int cursor=getCursor();
			float x=mLayout.getPrimaryHorizontal(cursor);
			int line=mLayout.getLineForOffset(cursor);
			mLayout.getLineBounds(line, mBoundsOfCursor); 
			int r=mBitmapSelectLeft.getWidth()/2;//(int) mSelectBarRadius;
			int y=mBoundsOfCursor.bottom + r;
			if( Math.sqrt( Math.pow(getScrollX()+e.getX()-(x+r), 2f) + Math.pow(getScrollY()+e.getY()-y, 2f)  ) <= r){
				mDownState=2;
			}

			cursor=getSelectionStart();
			x=mLayout.getPrimaryHorizontal(cursor);
			line=mLayout.getLineForOffset(cursor);
			mLayout.getLineBounds(line, mBoundsOfCursor); 
			r=mBitmapSelectRight.getWidth()/2;//(int) mSelectBarRadius;
			y=mBoundsOfCursor.bottom + r;
			if( Math.sqrt( Math.pow(getScrollX()+e.getX()-(x-r), 2f) + Math.pow(getScrollY()+e.getY()-y, 2f)  ) <= r){
				mDownState=1;
			}
		}
		return true;
	}

	

	@Override
	public boolean onSingleTapConfirmed(MotionEvent e) {
		Log.i(TAG, "onSingleTapConfirmed");
		return false;
	}


	@Override
	public boolean onDoubleTap(MotionEvent e) {
		Log.i(TAG, "onDoubleTap");
		//showMenu();
		if(mLayout!=null){
			int line=mLayout.getLineForVertical(getScrollY()+(int) e.getY());
			int offset=mLayout.getOffsetForHorizontal(line, getScrollX()+(int)e.getX());
			if(!showWarnAndError(line,offset))
				showMenu();
		}else{
			showMenu();
		}
		return false;
	}
	
	public boolean showWarnAndError(int line,int offset){
		if( !(getLayout() instanceof EditableWithLayout))
		{
			return false;
		}
		List<WarnAndError> warnAndErrors = ((EditableWithLayout)getLayout()).getWarnAndErrors();
		if(warnAndErrors==null)
			return false;
		
		Iterator<WarnAndError> iterator = warnAndErrors.iterator();
		WarnAndError warnAndError = null;
		while(iterator.hasNext()){
			WarnAndError temp = iterator.next();
			if(temp.include(line, offset))
			{
				warnAndError = temp;
				break;
			}
		}
		if(warnAndError==null)
			return false;
		
		AlertDialog alertDialog = null;
		AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
		builder.setTitle(warnAndError.getTitle());
		builder.setCancelable(true);
		TextView textView = new TextView(getContext());
		String msg = ""+warnAndError.mMsg+"\n";
		String trasMsg = WarnAndError.translateMsg(warnAndError.mMsg);
		if(!Waps.isGoogle())
		if(!warnAndError.mMsg.equals(trasMsg))
			msg+=""+trasMsg+"\n";
		textView.setText(msg);
		textView.setPadding(12, 12, 12, 12);
		textView.setTextIsSelectable(true);
		builder.setView(textView);
		alertDialog = builder.create();
		alertDialog.show();
		return true;
	}

	@Override
	public boolean onDoubleTapEvent(MotionEvent e) {
		Log.i(TAG, "onDoubleTapEvent");
		return false;
	}
	
	@Override
	public void onShowPress(MotionEvent e) {
	}

	@Override
	public boolean onSingleTapUp(MotionEvent e) {
		Log.i(TAG, "onSingleTapUp");
		if(mLayout!=null){
			int line=mLayout.getLineForVertical(getScrollY()+(int) e.getY());
			int offset=mLayout.getOffsetForHorizontal(line, getScrollX()+(int)e.getX());
			setCursor(offset);
		}
		showSoftKeyboard();
		return false;
	}
	
	@Override
	public boolean onScroll(MotionEvent e1, MotionEvent e2, float distanceX,
			float distanceY) {
		if(!isMoveSelection())
		{
			scrollBy((int)distanceX, (int )distanceY);
			postInvalidate();  
		}
		else{
			int line=mLayout.getLineForVertical(getScrollY()+(int) e2.getY()-(int)(1.5f*mLayout.getLineHeight()) );
			int offset=mLayout.getOffsetForHorizontal(line, getScrollX()+(int)e2.getX());
			int start=getSelectionStart();
			int end=getSelectionEnd();
			if(isMoveSelectionEnd()){
				if(offset<=start)
					offset=start+1;
				if(offset<=getText().length())
				{
					setSelection(start, offset);
				}
			}else
			if(isMoveSelectionStart()){
				if(offset>=end)
					offset=end-1;
				if(offset>=0){
					setSelection(offset, end);
				}
			}
		}
		return true;
	}
	
	
	private View getViewToShowPopupMenu(View view)
	{
		if(view instanceof ViewToShowPopupMenu )
		{
			return view;
		}
		if(view instanceof ViewGroup)
		{
			ViewGroup viewGroup=(ViewGroup) view;
			int count=viewGroup.getChildCount();
			for(int i=0;i<count;i++)
			{
				View ret = getViewToShowPopupMenu(viewGroup.getChildAt(i));
				if(ret!=null)
					return ret;
			}
		}
		return null;
	}
	
	private View chooseViewToPopMenu(View view)
	{
		ViewParent parent= view.getParent();
		if(parent!=null&&parent instanceof ViewGroup)
		{
			ViewGroup viewGroup=(ViewGroup) parent;
			View ret = getViewToShowPopupMenu( viewGroup );
			if(ret!=null)
				return ret;
		}
		return this;
	}

	public PopupMenu createMenu() {
		PopupMenu popupMenu=new PopupMenu(this.getContext(), chooseViewToPopMenu(this) );
		Menu menu=popupMenu.getMenu();
		menu.add(0, android.R.id.copy, 0, android.R.string.copy);
		menu.add(0, android.R.id.cut, 0, android.R.string.cut);
		menu.add(0, android.R.id.selectAll, 0, android.R.string.selectAll);
		menu.add(0, android.R.id.paste, 0, android.R.string.paste);
		popupMenu.setOnMenuItemClickListener(new OnMenuItemClickListener() {
			@Override
			public boolean onMenuItemClick(MenuItem arg0) {
				int id=arg0.getItemId();
				//if(id==android.R.id.paste)
				//	setCursor(getCursor());
				return performContextMenuAction(id);
			}
		});
		return popupMenu;
	}

	public void showMenu()
	{
		createMenu().show();
	}

	@Override
	public void onLongPress(MotionEvent e) {
		//Log.i(TAG, "onLongPress");
		if(isSelection()||getText().length()==0){
			showMenu();
		}else
		if(mLayout!=null){
			int line=mLayout.getLineForVertical(getScrollY()+(int) e.getY());
			int offset=mLayout.getOffsetForHorizontal(line, getScrollX()+(int)e.getX());
			if(!selectAtPosition(offset)){
				int start=offset-1;
				if(start<0)
					start=0;
				setSelection(start, offset);
			}
		}
	}

	private boolean selectAtPosition(int position){
		int start=position;
		int end=position;
		Editable editable=getText();
		if(position<0 || position>=editable.length())
			return false;
		char ch=editable.charAt(position);
		if( !(Character.isLetterOrDigit(ch) || ch=='_') )
			return false;
		for(start=position;start>0;start--){
			char indexCh=editable.charAt(start);
			if(!(Character.isLetterOrDigit(indexCh) || indexCh=='_') ){
				start++;
				break;
			}
		}
		
		for(end=position;end<editable.length()-1;end++){
			char indexCh=editable.charAt(end);
			if(!(Character.isLetterOrDigit(indexCh) || indexCh=='_') ){
				break;
			}
		}
		if(start>=end)
			return false;
		setSelection(start, end);
		return true;
	}
	
	@Override
	public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX,
			float velocityY) {
		if(!isMoveSelection())
		{
			//Log.i(TAG, "onFling");
			mScroller.fling(getScrollX(), getScrollY(), -(int)velocityX , -(int)velocityY, Integer.MIN_VALUE, Integer.MAX_VALUE, Integer.MIN_VALUE,  Integer.MAX_VALUE);
    		postInvalidate();  
		}
		return true;
	}
	
    @Override  
    public void computeScroll() {  
    	//先判断mScroller滚动是否完成  
    	if (mScroller.computeScrollOffset()) 
    	{
    		//这里调用View的scrollTo()完成实际的滚动  
    		scrollTo(mScroller.getCurrX(), mScroller.getCurrY());  
    		//必须调用该方法，否则不一定能看到滚动效果  
    		//postInvalidate();  
    	}  
    }
    
	public boolean setSelection(int start, int end) {
		//Log.i(TAG, "setSelection:"+start+" "+end);
		mScroller.abortAnimation();
    	boolean ret = mBaseInputConnection.setSelection(start, end);
    	postInvalidateSpan(start,end);
    	return ret;
	}
	
	@Override
	public boolean onKeyUp(int keyCode, KeyEvent event) {
		//Log.i(TAG, "onKeyUp:"+event);
		switch(event.getKeyCode()){
		case KeyEvent.KEYCODE_SHIFT_LEFT:
		case KeyEvent.KEYCODE_SHIFT_RIGHT:
			mDownState=0;
			break;
		case KeyEvent.KEYCODE_ENTER:
			break;
		default:
			return false;	
		}
		return true;
	}
	
	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		//event.getDevice().isVirtual();
		//Log.i(TAG, "onKeyDown:"+event);
		Editable editable=getText();
		int start=Selection.getSelectionStart(editable);
		int end=Selection.getSelectionEnd(editable);

		if(!event.isCtrlPressed())
		switch(event.getKeyCode()){
		case KeyEvent.KEYCODE_SHIFT_LEFT:
		case KeyEvent.KEYCODE_SHIFT_RIGHT:
			if(mDownState==0)
				mDownState=3;
			break;
		case KeyEvent.KEYCODE_DEL:
			if(start>=end)
				start=end-1;
			if(start>=0)
			editable.delete(start, end);
			break;
		case KeyEvent.KEYCODE_ENTER:
			editable.replace(start, end, "\n");
			break;
		case KeyEvent.KEYCODE_TAB:
			editable.replace(start, end, "\t");
			setSelection(start+1, start+1);
			break;
		case KeyEvent.KEYCODE_DPAD_LEFT:
			if( mDownState ==3 )
				mDownState=1;
			if(isMoveSelectionStart()){
				start--;
				if(start>=0)
					setSelection(start, end);
			}else if(isMoveSelectionEnd()){
				end--;
				if(end>start)
					setSelection(start, end);
			}else {
				end--;
				if(end>=0)
					setSelection(end, end);
			}
			break;
		case KeyEvent.KEYCODE_DPAD_RIGHT:
			if( mDownState==3 )
				mDownState=2;
			if(isMoveSelectionStart()){
				start++;
				if(start<end)
					setSelection(start, end);
			}else if(isMoveSelectionEnd()){
				end++;
				if(end<=editable.length())
					setSelection(start, end);
			}else {
				end++;
				if(end<=editable.length())
					setSelection(end, end);
			}
			break;
		case KeyEvent.KEYCODE_DPAD_UP :
			if( mDownState ==3 )
				mDownState=1;
			if(isMoveSelectionStart()){
				int line=mLayout.getLineForOffset(start);
				float x=mLayout.getPrimaryHorizontal(start);
				if(line>0)
				{
					int position=mLayout.getOffsetForHorizontal(line-1, x);
					if(position>=0 && position < end)
						setSelection(position, end);
				}
			}
			else
			{
				int line=mLayout.getLineForOffset(end);
				if(line>0){
					float x=mLayout.getPrimaryHorizontal(end);
					int position=mLayout.getOffsetForHorizontal(line-1, x);
					if(isMoveSelectionEnd()){
						if(position>start && position<=editable.length())
							setSelection(start, position);
					}
					else{
						setCursor(position);
					}
				}
			}
			break;
		case KeyEvent.KEYCODE_DPAD_DOWN:
			if( mDownState==3 )
				mDownState=2;
			if(isMoveSelectionStart()){
				int line=mLayout.getLineForOffset(start);
				if(line<mLayout.getLineCount()-1){
					float x=mLayout.getPrimaryHorizontal(start);
					int position=mLayout.getOffsetForHorizontal(line+1, x);
					if(position>=0 && position < end)
						setSelection(position, end);
				}
			}else{
				int line=mLayout.getLineForOffset(end);
				if(line<mLayout.getLineCount()-1){
					float x=mLayout.getPrimaryHorizontal(end);
					int position=mLayout.getOffsetForHorizontal(line+1, x);
					if(isMoveSelectionEnd()){
						if(position>start && position<=editable.length())
							setSelection(start, position);
					}
					else
						setCursor(position);
				}
			}
			break;
		default:
			return false;
		}
		
		/////////////////////////////////////
		if(event.isCtrlPressed())
		switch(event.getKeyCode()){
		case KeyEvent.KEYCODE_A:
			this.performContextMenuAction(android.R.id.selectAll);
			break;
		case KeyEvent.KEYCODE_Z:
			Log.i(TAG, "Ctrl Z");
			this.undo();
			break;
		case KeyEvent.KEYCODE_Y:
			Log.i(TAG, "Ctrl Y");
			this.redo();
			break;
		case KeyEvent.KEYCODE_X:
			this.performContextMenuAction(android.R.id.cut);
			break;
		case KeyEvent.KEYCODE_C:
			this.performContextMenuAction(android.R.id.copy);
			break;
		case KeyEvent.KEYCODE_V:
			this.performContextMenuAction(android.R.id.paste);
			break;
		default:
			return false;	
		}
		
		return true;
	}

	public boolean performContextMenuAction(int id) {
		//Log.i(TAG, "performContextMenuAction:"+id);
		Editable editable=getText();
		int start=Selection.getSelectionStart(editable);
		int end=Selection.getSelectionEnd(editable);
		switch (id) {
		case android.R.id.selectAll:
			setSelection(0, editable.length());
			break;
		case android.R.id.cut:
			try {
				ClipboardManager clipboardManager= (ClipboardManager) getContext().getSystemService(Context.CLIPBOARD_SERVICE);
				clipboardManager.setText(editable.subSequence(start, end));
				editable.delete(start, end);
				setSelection(start, start);
			} catch (Exception e) {
				e.printStackTrace();
			}
			break;
		case android.R.id.copy:
			try {
				ClipboardManager clipboardManager= (ClipboardManager) getContext().getSystemService(Context.CLIPBOARD_SERVICE);
				clipboardManager.setText(editable.subSequence(start, end));
				setSelection(end, end);
			} catch (Exception e) {
				e.printStackTrace();
			}
			break;
		case android.R.id.paste:
			try {
				ClipboardManager clipboardManager= (ClipboardManager) getContext().getSystemService(Context.CLIPBOARD_SERVICE);
				CharSequence charSequence=clipboardManager.getText();
				editable.replace(start, end, charSequence);
				int t=start+charSequence.length();
				setSelection(t,t);
			} catch (Exception e) {
				e.printStackTrace();
			}
			break;
		default:
			return false;
		}
		return true;
	}


	@Override
	public void beforeTextChanged(CharSequence s, int start, int count,int after) {
	}


	@Override
	public void onTextChanged(CharSequence s, int start, int before, int count) {
	}


	@Override
	public void afterTextChanged(Editable s) {
		//Log.i(TAG, "afterTextChanged");
		post(new Runnable() {
			@Override
			public void run() {
				bringPosToVisible(getCursor());
			}
		});
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

	public void setMaxSaveHistory(int maxSaveHistory){
		if(getLayout() instanceof EditableWithLayout)
		{
			((EditableWithLayout)getLayout()).setMaxSaveHistory(maxSaveHistory);
		}
	}

	public void cleanUndo(){
		if(getLayout() instanceof EditableWithLayout)
		{
			((EditableWithLayout)getLayout()).cleanUndo();
		}
	}
	
	public boolean canUndo(){
		if(getLayout() instanceof EditableWithLayout)
		{
			return ((EditableWithLayout)getLayout()).canUndo();
		}
		return false;
	}
	
	public boolean undo(){
		if(getLayout() instanceof EditableWithLayout)
		{
			return ((EditableWithLayout)getLayout()).undo();
		}
		return false;
	}
	
	public void cleanRedo(){
		if(getLayout() instanceof EditableWithLayout)
		{
			((EditableWithLayout)getLayout()).cleanRedo();
		}
	}
	
	public boolean canRedo(){
		if(getLayout() instanceof EditableWithLayout)
		{
			return ((EditableWithLayout)getLayout()).canRedo();
		}
		return false;
		
	}
	
	public boolean redo(){
		if(getLayout() instanceof EditableWithLayout)
		{
			return ((EditableWithLayout)getLayout()).redo();
		}
		return false;
	}
	
	public void setWarnAndError(List<WarnAndError> warnAndErrors){
		if(getLayout() instanceof EditableWithLayout)
		{
			((EditableWithLayout)getLayout()).setWarnAndErrors(warnAndErrors);
			this.postInvalidate();
		}
	}
	
	public boolean findString(String str){
		int index=getText().toString().indexOf(str, getCursor());
		if(index == -1)
			return false;
		return setSelection(index, index+str.length());
	}
	
	public boolean insertText(String str){
		getText().replace(getSelectionStart(), getSelectionEnd(), str);
		return true;
	}
	
	public boolean replaceString(String str){
		if(getSelectionStart()==getSelectionEnd())
			return false;
		getText().replace(getSelectionStart(), getSelectionEnd(), str);
		return true;
	}
	
	public boolean replaceFindString(String find,String replace){
		if(find.length() <= 0)
			return false;
		setCursor(getSelectionStart());
		if(!findString(find))
			return false;
		replaceString(replace);
		return true;
	}
	
	public boolean replaceAll(String find,String replace){
		int start=getSelectionStart();
		int end = getSelectionEnd();
		boolean finded=false;
		setCursor(0);
		while(replaceFindString(find,replace))
		{
			finded=true;
		}
		if(!finded)
			setSelection(start, end);
		return finded;
	}


	@Override
	public boolean onScale(ScaleGestureDetector detector) {
		float size=mTextPaint.getTextSize()/mFontScale;
		float scale=detector.getScaleFactor();
		size*=scale;
		if(size<8)
			return true;
		setTextSize(size);
		scrollTo((int)(getScrollX()*scale),(int)( getScrollY()*scale) );
		postInvalidate();
		return true;
	}


	@Override
	public boolean onScaleBegin(ScaleGestureDetector detector) {
		return true;
	}


	@Override
	public void onScaleEnd(ScaleGestureDetector detector) {
	}

}
