package person.wangchen11.editor.edittext;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Stack;


import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.text.Editable;
import android.text.InputFilter;
import android.text.Selection;
import android.text.SpanWatcher;
import android.text.TextPaint;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.text.style.ForegroundColorSpan;
import android.util.Log;

public class EditableWithLayout implements Editable,MyLayout {
	protected static final String TAG="EditableWithLayout";
	public static boolean mEnableHightLight=true;
	private int mLength=0;
	private char []mText=new char[0];
	private TextPaint mTextPaint ;
	private TextPaint mSpanPaint ;
    //private int mComposingStart=0;
    //private int mComposingEnd=0;
    private Rect mRect=new Rect();
    private Rect mRectLine=new Rect();
    private float mLineHeight;
    private float mTabWidth;
    private ForegroundColorSpan mDefaultColorSpan;
	private int mMaxSaveHistory;
	private Stack<ReplaceBody> mUndoBodies = new Stack<ReplaceBody>();
	private Stack<ReplaceBody> mRedoBodies = new Stack<ReplaceBody>();
    private List<LineBody> mLineBodies=new ArrayList<LineBody>();
    private List<SpanBody> mSpanBodies=new ArrayList<SpanBody>();
	private List<SpanInfo> mSpanInfos = new ArrayList<SpanInfo>();
	
    private List<WarnAndError> mWarnAndErrors=new ArrayList<WarnAndError>();
	private InputFilter[] mFilters ;
	public EditableWithLayout() {
		setPaint(new TextPaint());
		analysisLines();
	}
	
	public void setPaint(TextPaint paint){
		mTextPaint=paint;
		mLineHeight=paint.getTextSize();
		mTabWidth=mTextPaint.measureText("0000");
		mSpanPaint=new TextPaint(paint);
		mDefaultColorSpan=new ForegroundColorSpan(mTextPaint.getColor());
		cleanLineWidthInfo();
	}
	
	public void setLineHeight(float lineHeight){
		//Log.i(TAG, "setLineHeight:"+lineHeight);
		mLineHeight=lineHeight;
	}

	@Override
	public float getLineHeight() {
		//Log.i(TAG, "getLineHeight:"+mLineHeight);
		return mLineHeight;
	}
	@Override
	public int length() {
		return mLength;
	}
	
	private void resizeTo(int size){
		if(mText.length>size)
			return ;
		char[] text=new char[size+4096];
		System.arraycopy(mText, 0, text, 0, length());
		mText=text;
	}
	
	@Override
	public char charAt(int index) {
		return mText[index];
	}

	@Override
	public CharSequence subSequence(int start, int end) {
		return String.copyValueOf(mText, start, end-start);
	}

	@Override
	public void getChars(int start, int end, char[] dest, int destoff) {
        for (int i = start; i < end; i++)
            dest[destoff++] = mText[i];
	}

	private SpanInfo getOrCreateSpanBody(Object what){
		for(SpanInfo spanInfo:mSpanInfos){
			if(spanInfo.mSpan == what){
				return spanInfo;
			}
		}
		SpanInfo spanInfo = new SpanInfo(what, 0, 0, 0);
		mSpanInfos.add(spanInfo);
		return spanInfo ;
	}

	private SpanInfo getSpanBody(Object what){
		for(SpanInfo spanInfo:mSpanInfos){
			if(spanInfo.mSpan == what){
				return spanInfo;
			}
		}
		return null ;
	}

	public void setSpanEx(Object what, int start, int end, int flags) {
		//Log.i(TAG, "setSpanEx:"+mSpanInfos.size()+"  :"+what.getClass());
		SpanInfo spanInfo = getOrCreateSpanBody(what);
		int ostart = spanInfo.mStart;
		int oend = spanInfo.mEnd;
		
		spanInfo.mSpan = what;
		spanInfo.mStart = start;
		spanInfo.mEnd = end;
		spanInfo.mFlags = flags;
		
		SpanWatcher []spanWatchers = (SpanWatcher[]) getSpans(0, length(), SpanWatcher.class);
		sendOnSpanChanged(spanWatchers,spanInfo.mSpan, ostart, oend, spanInfo.mStart, spanInfo.mEnd);
	}

	@Override
	public void setSpan(Object what, int start, int end, int flags) {
		if(what==MyInputConnection.COMPOSING){
			//Log.i(TAG, "setSpan COMPOSING:start:"+start+" end:"+end);
		}
		/*
		if( what == Selection.SELECTION_START )
		{
			Log.i(TAG, "set SELECTION_START");
			mSelectionStart=start;
		}
		else
		if( what == Selection.SELECTION_END )
		{
			Log.i(TAG, "set SELECTION_END");
			mSelectionEnd=start;
		}
		else
		if( what instanceof ForegroundColorSpan ){
		}
		else
		if( what == MyInputConnection.COMPOSING  )
		{
			Log.i(TAG, "set COMPOSING");
			mComposingStart=start;
			mComposingEnd=end;
		}else*/
		{
			setSpanEx(what,start,end,flags);
		}
		
	}

	public void sendTextBeforeChanged(TextWatcher []textWatchers,CharSequence s,int start,int count,int after){
		for(TextWatcher textWatcher:textWatchers)
			textWatcher.beforeTextChanged(s, start, count, after);
	}
	
	public void sendOnTextChanged(TextWatcher []textWatchers,CharSequence s,int start,int before,int count){
		for(int i=0;i<mSpanBodies.size();i++)
		{
			SpanBody spanBody=mSpanBodies.get(i);
			if(spanBody.mEnd>start){
				spanBody.mEnd+=count;
			}
			if(spanBody.mStart>start){
				spanBody.mStart+=count;
			}
			if(spanBody.length()<=0)
			{
				//Log.i(TAG, "remove");
				mSpanBodies.remove(i);
				i--;
			}
		}
		for(TextWatcher textWatcher:textWatchers)
			textWatcher.onTextChanged(s, start, before, count);
	}
	
	public void sendTextAfterChanged(TextWatcher []textWatchers){
		for(TextWatcher textWatcher:textWatchers)
			textWatcher.afterTextChanged(this);
	}
	
	public void sendOnSpanChanged(SpanWatcher []spanWatchers,Object what, int ostart,int oend, int nstart, int nend){
		for(SpanWatcher spanWatcher:spanWatchers){
			spanWatcher.onSpanChanged(this, what, ostart, oend, nstart, nend);
		}
	}

	public void removeSpanEx(Object what) {
		int pos = -1;
		int index = 0;
		for(SpanInfo spanInfo : mSpanInfos){
			if(spanInfo.mSpan == what){
				pos = index;
				break;
			}
			index++;
		}
		if(pos>=0)
			mSpanInfos.remove(pos);
	}

	@Override
	public void removeSpan(Object what) {
		removeSpanEx(what);
	}

	@SuppressWarnings("unchecked")
	public <T> T[] getSpansEx(int start, int end, Class<T> type) {
		if(type==null)
			return (T[]) Array.newInstance(type, 0);
		ArrayList<T> arrayList = new ArrayList<T>();
		for(SpanInfo body:mSpanInfos){
			if(type.isInstance(body.mSpan)){
				arrayList.add((T) body.mSpan);
				
				//if(type.equals(TextWatcher.class)){
				//	Log.i(TAG,""+body.mSpan.getClass());
				//}
			}
		}
		T[] ts = (T[]) Array.newInstance(type, arrayList.size());
		arrayList.toArray(ts);
		return ts;
	}
	
	@Override
	public <T> T[] getSpans(int start, int end, Class<T> type) {
		//return (T[]) Array.newInstance(type, 0);
		return getSpansEx(start,end,type);
	}
	
	private List<SpanBody> getColorSpanBodies(int start, int end){
		ArrayList<SpanBody> bodies=new ArrayList<SpanBody>();
		int startSpan=0;
		int endSpan=mSpanBodies.size()-1;
		int pos=0;
		if(endSpan>=0)
		while(true)
		{
			int center=(startSpan+endSpan)/2;
			SpanBody centerBody=mSpanBodies.get(center);
			int ret=centerBody.compileTo(start);
			if(ret==0){
				pos=center;
				break;
			}
			if(center >= endSpan){
				pos=center;
				break;
			}
			if(ret>0)
			{
				startSpan=center+1;
			}else{
				endSpan=center-1;
			}
		}
		
		int proStart=start;
		for(;pos<mSpanBodies.size();pos++){
			SpanBody body=mSpanBodies.get(pos);
			if(body.mStart>=end)
				break;
			if(body.hasSub(start, end))
			{
				if(body.mStart>proStart){
					bodies.add(new SpanBody(mDefaultColorSpan, proStart, body.mStart, 0));
				}
				int tend=body.mEnd<=end?body.mEnd:end;
				int tstart=body.mStart>=start?body.mStart:start;
				if(tend-tstart>0)
					bodies.add(new SpanBody(body.mSpan, tstart, tend, 0));
				proStart=tend;
				if(proStart>=end)
					break;
			}
		}
		if(proStart<end)
			bodies.add(new SpanBody(mDefaultColorSpan, proStart, end, 0));
		/*
		if(mSpanBodies!=null)
		{
			Iterator<SpanBody> iterator=mSpanBodies.iterator();
			int proStart=start;
			while(iterator.hasNext()){
				SpanBody body=iterator.next();
				if(body.hasSub(start, end))
				{
					if(body.mStart>proStart){
						bodies.add(new SpanBody(mDefaultColorSpan, proStart, body.mStart, 0));
					}
					int tend=body.mEnd<=end?body.mEnd:end;
					int tstart=body.mStart>=start?body.mStart:start;
					if(tend-tstart>0)
						bodies.add(new SpanBody(body.mSpan, tstart, tend, 0));
					proStart=tend;
					if(proStart>=end)
						break;
				}
			}
			if(proStart<end)
				bodies.add(new SpanBody(mDefaultColorSpan, proStart, end, 0));
				
		}
		else{
			bodies.add(new SpanBody(mDefaultColorSpan, start, end, 0));
		}
		*/
		//Log.i(TAG, "sumLoop:"+sumLoop);
		return bodies;
	}

	public int getSpanStartEx(Object tag) {
		SpanInfo spanInfo = getSpanBody(tag);
		if(spanInfo==null)
			return -1;
		return spanInfo.mStart;
	}

	public int getSpanEndEx(Object tag) {
		SpanInfo spanInfo = getSpanBody(tag);
		if(spanInfo==null)
			return -1;
		return spanInfo.mEnd;
	}

	public int getSpanFlagsEx(Object tag) {
		SpanInfo spanInfo = getSpanBody(tag);
		if(spanInfo==null)
			return 0;
		return spanInfo.mFlags;
	}

	public int nextSpanTransitionEx(int start, int limit, Class kind) {
        int count = mSpanInfos.size();
        
        if (kind == null) {
            kind = Object.class;
        }

        for (int i = 0; i < count; i++) {
        	SpanInfo spanInfo = mSpanInfos.get(i);
            int st = spanInfo.mStart;
            int en = spanInfo.mEnd;

            if (st > start && st < limit && kind.isInstance(spanInfo.mSpan))
                limit = st;
            if (en > start && en < limit && kind.isInstance(spanInfo.mSpan))
                limit = en;
        }

        return limit;
	}

	@Override
	public int getSpanStart(Object tag) {
		/*
		if(tag == Selection.SELECTION_START)
			return mSelectionStart;
		if(tag == Selection.SELECTION_END)
			return mSelectionEnd;
			
		if(tag == MyInputConnection.COMPOSING )
			return mComposingStart;*/
		return getSpanStartEx(tag);
	}

	@Override
	public int getSpanEnd(Object tag) {
		/*
		if(tag == Selection.SELECTION_END)
			return mSelectionEnd;
		if(tag == Selection.SELECTION_START)
			return mSelectionStart;
		if(tag == MyInputConnection.COMPOSING )
			return mComposingEnd;*/
		return getSpanEndEx(tag);
	}

	@Override
	public int getSpanFlags(Object tag) {
		return getSpanFlagsEx(tag);
	}

	@SuppressWarnings("rawtypes")
	@Override
	public int nextSpanTransition(int start, int limit, Class type) {
		return nextSpanTransitionEx(start,limit,type);
	}

	@Override
	public Editable replace(int start, int end, CharSequence tb, int tbstart,int tbend) {
		cleanRedo();
		return replace(start,end,tb,tbstart,tbend,true);
	}
	
	public Editable replace(int start, int end, CharSequence tb, int tbstart,int tbend,boolean saveToUndo) {		
		
		if(mFilters!=null){
	        int filtercount = mFilters.length;
	        for (int i = 0; i < filtercount; i++) {
	            CharSequence repl = mFilters[i].filter(tb, tbstart, tbend, this, start, end);

	            if (repl != null) {
	                tb = repl;
	                tbstart = 0;
	                tbend = repl.length();
	            }
	        }
		}

		int deleteLen=end-start;
		int insertLen=tbend-tbstart;
		int addLen=insertLen-deleteLen;
		resizeTo(length()+addLen);
		if(deleteLen<0||insertLen<0)
			return this;
		if(deleteLen==0&&insertLen==0)
			return this;

		int mSelectionStart = getSpanStart(Selection.SELECTION_START);
		int mSelectionEnd  =  getSpanEnd(Selection.SELECTION_END);
		
		//saved to history to be undo or redo 
		if(saveToUndo){
			if(mMaxSaveHistory>0){
				if(mUndoBodies.size()>mMaxSaveHistory)
					mUndoBodies.remove(0);
				ReplaceBody body=new ReplaceBody(start, end, subSequence(start, end),tb, tbstart, tbend, mSelectionStart, mSelectionEnd);
				if(!mUndoBodies.isEmpty() && mUndoBodies.peek().addBody(body))
				{
				}
				else
					mUndoBodies.push(body);
			}
		}

		TextWatcher []textWatchers = getSpans(tbstart, tbend, TextWatcher.class);
		
		sendOnTextChanged(textWatchers,this, start, length(), addLen);
		sendTextBeforeChanged(textWatchers,this, start,addLen ,length()+addLen);
		
		if(mSelectionStart==start && mSelectionEnd==end){
			mSelectionStart=mSelectionEnd=end+addLen;
		}
		else
		{
			if(mSelectionStart>=start){
				mSelectionStart+=addLen;
				if(mSelectionStart<start)
					mSelectionStart=start;
			}
			if(mSelectionEnd>=start){
				mSelectionEnd+=addLen;
				if(mSelectionEnd<start)
					mSelectionEnd=start;
			}
		}

		if(addLen>0)
			charsCopyEndToStart(mText, end, mText, end+addLen, length()+addLen);
		else
		if(addLen<0)
			charsCopyStartToEnd(mText, end, mText, end+addLen, length()+addLen);
		if(insertLen>0)
			TextUtils.getChars(tb, tbstart, tbend, mText, start);
		mLength+=addLen;
		if(mSelectionStart<0)
			mSelectionStart=0;
		if(mSelectionEnd<0)
			mSelectionEnd=0;
		if(mSelectionStart>mLength)
			mSelectionStart=mLength;
		if(mSelectionEnd>mLength)
			mSelectionEnd=mLength;
		setSpan(Selection.SELECTION_START, mSelectionStart, mSelectionStart, 0);
		setSpan(Selection.SELECTION_END, mSelectionEnd, mSelectionEnd, 0);
		
		analysisLines();
		sendTextAfterChanged(textWatchers);
		return this;
	}

	@Override
	public Editable replace(int st, int en, CharSequence text) {
		return replace(st, en, text, 0, text.length());
	}

	@Override
	public Editable insert(int where, CharSequence text, int start, int end) {
		int len = end-start;
		if(where<0 || where>length() || len<=0)
			return this;
		return replace(where,where, text, start, end);
	}

	static void charsCopyStartToEnd(char []src,int where,char []dst,int start,int end){
		for(;start<end;start++,where++){
			dst[start]=src[where];
		}
	}
	static void charsCopyEndToStart(char []src,int where,char []dst,int start,int end){
		int srcend=where+end-start;
		for(;end>=start;end--,srcend--){
			dst[end]=src[srcend];
		}
	}
	
	@Override
	public Editable insert(int where, CharSequence text) {
		return insert(where,text,0,text.length());
	}

	@Override
	public Editable delete(int st, int en) {
		return replace(st, en, "", 0, 0);
	}

	@Override
	public Editable append(CharSequence text) {
		return null;
	}

	@Override
	public Editable append(CharSequence text, int start, int end) {
		return null;
	}

	@Override
	public Editable append(char text) {
		return null;
	}

	@Override
	public void clear() {
		TextWatcher []textWatchers = getSpans(0, length(), TextWatcher.class);
		sendOnTextChanged(textWatchers,this, 0, length(), length());
		sendTextBeforeChanged(textWatchers,this, 0, length(), 0);
		mLength=0;
		sendTextAfterChanged(textWatchers);
	}
	
	public void clearSpansEx() {
		mSpanInfos.clear();
	}
	

	@Override
	public void clearSpans() {
		clearSpansEx();		
	}

	@Override
	public void setFilters(InputFilter[] filters) {
        mFilters  = filters;
	}

	@Override
	public InputFilter[] getFilters() {
		return mFilters;
	}

	@Override
	public int getLineForOffset(int offset) {
		if(mLineBodies.isEmpty())
			return 0;
		int startLine=0;
		int endLine=getLineCount()-1;
		if(mLineBodies instanceof ArrayList )
		{
		//array list使用二分搜索速度更快
			while(true){
				int centerLine=(startLine+endLine)/2;
				LineBody bodyCenter=mLineBodies.get(centerLine);
				int ret=bodyCenter.compileTo(offset);
				if(ret==0)
					return centerLine;
				if(centerLine>=endLine)
					break;
				if(ret<0)
					endLine=centerLine-1;
				else//if(ret>0)
					startLine=centerLine+1;
			}
		}
		else
		{
			for(int i=0;i<mLineBodies.size();i++)
			{
				LineBody body=mLineBodies.get(i);
				if(body.in(offset))
					return i;
			}
		}
		return endLine;
	}

	@Override
	public float getPrimaryHorizontal(int start) {
		int line=getLineForOffset(start);
		LineBody body= mLineBodies.get(line);
		Paint paint=getPaint();
		float horizontal=0f;
		for(int offset=body.mStart;offset<start;offset++){
			float tx=0;
			if(mText[offset]=='\t')
				tx=mTabWidth - (horizontal % mTabWidth);
			else
				tx=paint.measureText(mText, offset, 1);
			horizontal+=tx;
		}
		/*
		try {
			//horizontal=getPaint().measureText(this, body.mStart, start);
		} catch (Exception e) {
			e.printStackTrace();
		}*/
		return horizontal;
	}

	@Override
	public void getLineBounds(int line, Rect rect) {
		rect.left=0;
		rect.right=Integer.MAX_VALUE/2;
		float top=line*mLineHeight;
		rect.top=(int) top;
		rect.bottom=(int) (top+mLineHeight);
	}

	@Override
	public int getLineForVertical(int top) {
		int line=(int) (top/mLineHeight);
		if(line<0)
			line=0;
		if(line>=getLineCount())
			line=getLineCount()-1;
		return line;
	}
	
	private void cleanLineWidthInfo(){
		synchronized (mLineBodies) {
			for(LineBody lineBody:mLineBodies){
				lineBody.mSpanBodies = null;
			}
		}
	}
	
	private ArrayList<ExSpanBody> getLineBodies(int line){
		LineBody body=mLineBodies.get(line);
		ArrayList<ExSpanBody> exSpanBodies = body.mSpanBodies;
		if(exSpanBodies==null)
			exSpanBodies = new ArrayList<ExSpanBody>();
		else
			return exSpanBodies;
		
		int lastLine=getLineCount()-1;
		int proTab=body.mStart;
		int nextTab;
		float startX=0;
		int start;
		int count;
		float measureWidth = 0;
		while(true){
			nextTab=getNextTab(proTab);
			if(nextTab==-1)
				break;
			if( proTab!=nextTab ){
				start=proTab;
				count=nextTab-proTab;
				List<SpanBody> bodies=getColorSpanBodies(start, start+count);
				Iterator<SpanBody> iterator=bodies.iterator();
				while(iterator.hasNext()){
					SpanBody spanBody=iterator.next();
					//mSpanPaint.setColor( ((ForegroundColorSpan)spanBody.mSpan).getForegroundColor() );
					measureWidth = measureText(mText, spanBody.mStart, spanBody.length());
					float tstartX=startX+measureWidth;
					exSpanBodies.add(new ExSpanBody(spanBody, measureWidth));
					//if(tstartX>mRect.left)
					{
						//canvas.drawText(mText, spanBody.mStart, spanBody.length(), startX, lineY, mSpanPaint);
					}
					startX=tstartX;
					//if(startX>mRect.right)
					//	break;
				}
				//if(startX>mRect.right)
				//	break;
			}
			measureWidth = mTabWidth-(startX % mTabWidth);
			exSpanBodies.add(new ExSpanBody(null,0,0,0, measureWidth));
			startX+=mTabWidth-(startX % mTabWidth);
			proTab=nextTab+1;
		}
		
		start=proTab;
		if(lastLine==line)
			count=body.mEnd-proTab;
		else
			count=body.mEnd-proTab-1;
		
		if(count>0)
		{
			List<SpanBody> bodies=getColorSpanBodies(start, start+count);
			Iterator<SpanBody> iterator=bodies.iterator();
			while(iterator.hasNext()){
				SpanBody spanBody=iterator.next();
				//mSpanPaint.setColor( ((ForegroundColorSpan)spanBody.mSpan).getForegroundColor() );
				measureWidth = measureText(mText, spanBody.mStart, spanBody.length());
				float tstartX=startX+measureWidth;
				exSpanBodies.add(new ExSpanBody(spanBody, measureWidth));
				//if(tstartX>mRect.left)
				{
					//canvas.drawText(mText, spanBody.mStart, spanBody.length(), startX, lineY, mSpanPaint);
				}
				startX=tstartX;
				//if(startX>mRect.right)
				//	break;
			}
		}
		body.mSpanBodies = exSpanBodies;
		return exSpanBodies;
	}
	
	public void draw(Canvas canvas){
		canvas.getClipBounds(mRect);
		int startLine=getLineForVertical(mRect.top);
		int endLine=getLineForVertical(mRect.bottom);
		float descent=mTextPaint.getFontMetrics().descent;
		
		Paint warnAndErrorPaint = new Paint();
		warnAndErrorPaint.setStrokeWidth(mTextPaint.getTextSize()/12);
		
		for(int i=startLine;i<=endLine;i++)
		{
			getLineBounds(i, mRectLine);
			float lineY=mRectLine.bottom-descent;
			ArrayList<ExSpanBody> spanBodies = getLineBodies(i);
			float startX = 0;
			if(spanBodies!=null)
			for(ExSpanBody spanBody:spanBodies){
				if(spanBody.mSpan==null){
					startX+=spanBody.mWidth;
					continue;
				}
				if(startX+spanBody.mWidth<=mRect.left){
					startX+=spanBody.mWidth;
					continue;
				}
				mSpanPaint.setColor( ((ForegroundColorSpan)spanBody.mSpan).getForegroundColor() );
				canvas.drawText(mText, spanBody.mStart, spanBody.length(), startX, lineY, mSpanPaint);
				startX+=spanBody.mWidth;
				if(startX>mRect.right)
					break;
			}
			
			{
				WarnAndError fullLine = null;
				LinkedList<WarnAndError> curLines = new LinkedList<WarnAndError>();
				Iterator<WarnAndError> curLinesIterator = mWarnAndErrors.iterator();
				while(curLinesIterator.hasNext()){
					WarnAndError warnAndError = curLinesIterator.next();
					if(warnAndError.mLine==i){
						if(warnAndError.mFullLine){
							if(fullLine==null)
								fullLine = warnAndError;
							break;
						}else{
							curLines.add(warnAndError);
						}
					}
				}
				if(fullLine!=null){
					warnAndErrorPaint.setColor(fullLine.mColor);
					canvas.drawLine(0, lineY ,startX>0?startX:mRectLine.right, lineY , warnAndErrorPaint);
				}
			}
		}
	}
	
	public void drawEx(Canvas canvas) {
		canvas.getClipBounds(mRect);
		int lastLine=getLineCount()-1;
		int startLine=getLineForVertical(mRect.top);
		int endLine=getLineForVertical(mRect.bottom);
		float descent=mTextPaint.getFontMetrics().descent;
		Paint warnAndErrorPaint = new Paint();
		warnAndErrorPaint.setStrokeWidth(mTextPaint.getTextSize()/12);
		for(int i=startLine;i<=endLine;i++)
		{
			WarnAndError fullLine = null;
			LinkedList<WarnAndError> curLines = new LinkedList<WarnAndError>();
			Iterator<WarnAndError> curLinesIterator = mWarnAndErrors.iterator();
			while(curLinesIterator.hasNext()){
				WarnAndError warnAndError = curLinesIterator.next();
				if(warnAndError.mLine==i){
					if(warnAndError.mFullLine){
						if(fullLine==null)
							fullLine = warnAndError;
						break;
					}else{
						curLines.add(warnAndError);
					}
				}
			}
			getLineBounds(i, mRectLine);
			LineBody body=mLineBodies.get(i);
			int proTab=body.mStart;
			int nextTab;
			float startX=0;
			float lineY=mRectLine.bottom-descent;
			int start;
			int count;
			while(true){
				nextTab=getNextTab(proTab);
				if(nextTab==-1)
					break;
				if( proTab!=nextTab ){
					start=proTab;
					count=nextTab-proTab;
					List<SpanBody> bodies=getColorSpanBodies(start, start+count);
					Iterator<SpanBody> iterator=bodies.iterator();
					while(iterator.hasNext()){
						SpanBody spanBody=iterator.next();
						mSpanPaint.setColor( ((ForegroundColorSpan)spanBody.mSpan).getForegroundColor() );
						float tstartX=startX+measureText(mText, spanBody.mStart, spanBody.length());
						if(tstartX>mRect.left){
							canvas.drawText(mText, spanBody.mStart, spanBody.length(), startX, lineY, mSpanPaint);
						}
						startX=tstartX;
						if(startX>mRect.right)
							break;
					}
					if(startX>mRect.right)
						break;
				}
				startX+=mTabWidth-(startX % mTabWidth);
				proTab=nextTab+1;
			}
			
			start=proTab;
			if(lastLine==i)
				count=body.mEnd-proTab;
			else
				count=body.mEnd-proTab-1;
			
			if(count>0)
			{
				List<SpanBody> bodies=getColorSpanBodies(start, start+count);
				Iterator<SpanBody> iterator=bodies.iterator();
				while(iterator.hasNext()){
					SpanBody spanBody=iterator.next();
					mSpanPaint.setColor( ((ForegroundColorSpan)spanBody.mSpan).getForegroundColor() );
					//耗时大户，在measureText,drawText这里耗费了大量的时间  
					float tstartX=startX+measureText(mText, spanBody.mStart, spanBody.length());
					if(tstartX>mRect.left){
						canvas.drawText(mText, spanBody.mStart, spanBody.length(), startX, lineY, mSpanPaint);
					}
					startX=tstartX;
					if(startX>mRect.right)
						break;
				}
			}

			if(fullLine!=null){
				warnAndErrorPaint.setColor(fullLine.mColor);
				canvas.drawLine(0, lineY ,startX>0?startX:mRectLine.right, lineY , warnAndErrorPaint);
			}
		}
	}
	
	@SuppressWarnings("unused")
	private void drawTest(Canvas canvas)
	{
		float startY=0;
		for(int y=0;y<40;y++)
		{
			float startX=0;
			for(int x=0;x<18;x++)
			{
				String text=""+x;
				mSpanPaint.setColor(Color.rgb(255, 0, 0));
				canvas.drawText(text, startX, startY, mSpanPaint);
				startX+=mSpanPaint.measureText(text);
				
				text=",";
				mSpanPaint.setColor(Color.rgb(0, 0, 0));
				canvas.drawText(text, startX, startY, mSpanPaint);
				startX+=mSpanPaint.measureText(text);
				
			}
			startY+=mSpanPaint.getTextSize();
		}
	}
	
	public float measureText(char []text,int index,int count)
	{
		return mSpanPaint.measureText(text,index,count);
	}
	/**
	 * 寻找下一个TAB符的位置
	 * 遇到换行符或结尾就会停止查找
	 * @return
	 */
	private int getNextTab(int offset){
		int len=length();
		while(offset<len ){
			if(mText[offset]=='\n')
				return -1;
			if(mText[offset]=='\t')
				return offset;
			offset++;
		}
		return -1;
	}
	
	private void analysisLines(){
		synchronized (mLineBodies) {
			mLineBodies.clear();
			int start=0;
			for(int i=0;i<mLength;i++){
				if(mText[i]=='\n'){
					mLineBodies.add(new LineBody(start, i+1));
					start=i+1;
				}
			}
			mLineBodies.add(new LineBody(start, mLength));
		}
	}
	
	@Override
	public int getLineCount() {
		return mLineBodies.size();
	}

	@Override
	public Paint getPaint() {
		return mTextPaint;
	}
	
	@Override
	public int getOffsetForHorizontal(int line, float x) {
		LineBody body=mLineBodies.get(line);
		Paint paint = getPaint();
		float sumx=0;
		int pos=body.mStart;
		if(x<=0)
			return pos;
		for(;pos<body.mEnd;pos++){
			float tx=0;
			if(mText[pos]=='\t')
				tx=mTabWidth - (sumx % mTabWidth);
			else
				tx=paint.measureText(mText, pos, 1);
			if(x<=sumx+tx/2)
				return pos;
			sumx+=tx;
		}
		int lineCount=getLineCount();
		//最后一行没有包含\n  
		if(line==lineCount-1)
			return body.mEnd;
		return body.mEnd-1;
	}

	@Override
	public int getHeight() {
		return (int) (getLineCount()*mLineHeight);
	}	

	@Override
	public String toString() {
		return String.copyValueOf(mText, 0, length());
	}
	
	/**
	 * 应用颜色span  为了保证速度够快    该spans必须保证是顺序排列的</br>
	 * 并且最好使用ArrayList以便可以使用二分查找 </br>
	 * 程序不会对传入的spans做任何校验，因此你必须保证span.mSpan instanceof ForegroundColorSpan</br>
	 * 并且所有的span不能相交！！！</br>
	 * 你可以在子线程中创建完成spans然后在主线程中使用applyColorSpans</br>
	 * 以便让程序看起来不卡。</br>
	 * @param spans
	 */
	public void applyColorSpans(List<SpanBody> spans){
		//Log.i(TAG, "applyColorSpans:"+spans);
		if(mEnableHightLight)
			mSpanBodies=spans;
		cleanLineWidthInfo();
	}

	public void addColorSpan(SpanBody spanBody){
		if(mEnableHightLight)
			mSpanBodies.add(spanBody);
	}
	@Override
	public int getLineOffset(int line) {
		return mLineBodies.get(line).mStart;
	}
	
	public void setMaxSaveHistory(int maxSaveHistory){
		mMaxSaveHistory=maxSaveHistory;
	}

	public void cleanUndo(){
		mUndoBodies.clear();
	}
	
	public boolean canUndo(){
		//Log.i(TAG, "canUndo :"+!mUndoBodies.isEmpty());
		return !mUndoBodies.isEmpty();
	}
	
	private boolean mSaveToHistory = true;
	
	public boolean isSaveToHistory(){
		return mSaveToHistory;
	}
	
	public boolean undo(){
		if(!canUndo())
			return false;
		mSaveToHistory = false;
		ReplaceBody body=mUndoBodies.pop();
		ReplaceBody replaceBody=body.getUndoBody();
		if(mMaxSaveHistory>0){
			if(mRedoBodies.size()>mMaxSaveHistory)
				mRedoBodies.remove(0);
			mRedoBodies.push(body);
		}
		replace(replaceBody.mSt, replaceBody.mEn, replaceBody.mText, replaceBody.mStart, replaceBody.mEnd, false);
		

		int mSelectionStart = getSpanStart(Selection.SELECTION_START);
		int mSelectionEnd  =  getSpanEnd(Selection.SELECTION_END);
		
		
		if(replaceBody.mText==null || replaceBody.mText.length()==0 || replaceBody.mEnd-replaceBody.mStart==0 )
		{
			mSelectionStart=replaceBody.mSt;
			mSelectionEnd=mSelectionStart;
		}
		else
		{
			mSelectionStart=replaceBody.mSt;
			mSelectionEnd=replaceBody.mSt+replaceBody.mEnd-replaceBody.mStart;
		}
		setSpan(Selection.SELECTION_START, mSelectionStart, mSelectionStart, 0);
		setSpan(Selection.SELECTION_END, mSelectionEnd, mSelectionEnd, 0);
		mSaveToHistory = true;
		return true;
	}
	
	public void cleanRedo(){
		mRedoBodies.clear();
	}
	
	public boolean canRedo(){
		//Log.i(TAG, "canRedo :"+!mRedoBodies.isEmpty());
		return !mRedoBodies.isEmpty();
		
	}
	
	public boolean redo(){
		if(!canRedo())
			return false;
		mSaveToHistory = false;
		ReplaceBody body=mRedoBodies.pop();
		ReplaceBody replaceBody=body.getRedoBody();
		if(mMaxSaveHistory>0){
			if(mUndoBodies.size()>mMaxSaveHistory)
				mUndoBodies.remove(0);
			mUndoBodies.push(body);
		}
		replace(replaceBody.mSt, replaceBody.mEn, replaceBody.mText, replaceBody.mStart, replaceBody.mEnd, false);
		

		int mSelectionStart = getSpanStart(Selection.SELECTION_START);
		int mSelectionEnd  =  getSpanEnd(Selection.SELECTION_END);
		
		if(replaceBody.mText==null || replaceBody.mText.length()==0 || replaceBody.mEnd-replaceBody.mStart==0 )
		{
			mSelectionStart=replaceBody.mSt;
			mSelectionEnd=mSelectionStart;
		}
		else
		{
			mSelectionStart=replaceBody.mSt;
			mSelectionEnd=replaceBody.mSt+replaceBody.mEnd-replaceBody.mStart;
		}
		setSpan(Selection.SELECTION_START, mSelectionStart, mSelectionStart, 0);
		setSpan(Selection.SELECTION_END, mSelectionEnd, mSelectionEnd, 0);
		mSaveToHistory = true;
		return true;
	}

	public void setWarnAndErrors(List<WarnAndError> warnAndErrors){
		mWarnAndErrors = warnAndErrors;
		if(mWarnAndErrors==null)
			mWarnAndErrors = new LinkedList<WarnAndError>();
	}
	
	public List<WarnAndError> getWarnAndErrors(){
		return mWarnAndErrors;
	}

	class ExSpanBody extends SpanBody {
		public float mWidth;
		public ExSpanBody(Object span, int start, int end, int flag,float width) {
			super(span, start, end, flag);
			mWidth = width;
		}
		
		public ExSpanBody(SpanBody spanBody,float width) {
			this(spanBody.mSpan,spanBody.mStart,spanBody.mEnd,spanBody.mFlag,width);
		}
	}
	
	class LineBody {
		int mStart;
		int mEnd;
		ArrayList<ExSpanBody> mSpanBodies = null; 

		public LineBody(int start, int end) {
			mStart = start;
			mEnd = end;
		}

		public boolean in(int pos) {
			if (pos >= mStart && pos < mEnd)
				return true;
			return false;
		}

		public int compileTo(int pos) {
			if (pos < mStart)
				return -1;
			if (pos >= mEnd)
				return 1;
			return 0;
		}

		public int length() {
			return mEnd - mStart - 1;
		}
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
			//Log.i("isInsert", "false");
			return false;
		}

		public boolean addBody(ReplaceBody body) {
			if (isDelete() && body.isDelete() && mSt == body.mEn) {// 合并相连的删除
				this.mSt = body.mSt;
				this.mSubtext = body.mSubtext.toString() + this.mSubtext;
				this.mSelectionStart = body.mSelectionStart;
				return true;
			}
			//Log.i("addBody", "addBody");
			if (isInsert() && body.isInsert()
					&& mSt + mText.length() == body.mSt) {// 合并相连的插入
				//Log.i("addBody", "合并相连的插入 ");
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
}

class SpanInfo {
	public Object mSpan;
	public int mStart;
	public int mEnd;
	public int mFlags;
	public SpanInfo(Object span,int start,int end,int flags){
		mSpan = span;
		mEnd  = end;
		mFlags = flags;
	}
}