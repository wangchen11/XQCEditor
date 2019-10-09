package com.editor.text;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

import android.text.Editable;
import android.text.InputFilter;
import android.text.Selection;
import android.text.SpanWatcher;
import android.text.SpannableStringBuilder;
import android.text.Spanned;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;

public class QuicklySpannableStringBuilder implements Editable {
	protected static final String TAG="QuicklySpannableStringBuilder";
	SpannableStringBuilder s;
	private int mLength=0;
	private char []mText=new char[0];
	private List<SpanInfo> mSpanInfos = new ArrayList<SpanInfo>();
	private InputFilter[] mFilters = new InputFilter[0];
	
	public QuicklySpannableStringBuilder(CharSequence source) {
		replace(0, length(), source);
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
	
	@Override
	public void setSpan(Object what, int start, int end, int flags) {
		if(what.getClass().equals(TextWatcher.class)){
			Log.i(TAG,"getClass:TextWatcher.class");
		}
		//Log.i(TAG, "setSpan:"+mSpanBodies.size()+"  :"+what.getClass());
		SpanInfo spanInfo = getOrCreateSpanBody(what);
		spanInfo.mSpan = what;
		spanInfo.mStart = start;
		spanInfo.mEnd = end;
		spanInfo.mFlags = flags;
	}

	@Override
	public void removeSpan(Object what) {
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

	@SuppressWarnings("unchecked")
	@Override
	public <T> T[] getSpans(int start, int end, Class<T> type) {
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
	public int getSpanStart(Object tag) {
		SpanInfo spanInfo = getSpanBody(tag);
		if(spanInfo==null)
			return -1;
		return spanInfo.mStart;
	}

	@Override
	public int getSpanEnd(Object tag) {
		SpanInfo spanInfo = getSpanBody(tag);
		if(spanInfo==null)
			return -1;
		return spanInfo.mEnd;
	}

	@Override
	public int getSpanFlags(Object tag) {
		SpanInfo spanInfo = getSpanBody(tag);
		if(spanInfo==null)
			return 0;
		return spanInfo.mFlags;
	}

	@Override
	public int nextSpanTransition(int start, int limit, Class kind) {
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
	public Editable replace(int start, int end, CharSequence tb, int tbstart,
			int tbend) {
		Log.i(TAG, "replace:"+"start:"+start+" end:"+end+" tb:"+tb+" tbstart:"+tbstart+"tbend:"+tbend);
		int deleteLen=end-start;
		int insertLen=tbend-tbstart;
		int addLen=insertLen-deleteLen;
		if(deleteLen<0||insertLen<0)
			return this;
		if(deleteLen==0&&insertLen==0)
			return this;
		
		

        int filtercount = mFilters.length;
        for (int i = 0; i < filtercount; i++) {
            CharSequence repl = mFilters[i].filter(tb, tbstart, tbend, this, start, end);

            if (repl != null) {
                tb = repl;
                tbstart = 0;
                tbend = repl.length();
            }
        }

        final int origLen = end - start;
        final int newLen = tbend - tbstart;

        if (origLen == 0 && newLen == 0) {
            // This is a no-op iif there are no spans in tb that would be added (with a 0-length)
            // Early exit so that the text watchers do not get notified
            return this;
        }
		resizeTo(length()+addLen);

        TextWatcher[] textWatchers = getSpans(start, start + origLen, TextWatcher.class);
        sendBeforeTextChanged(textWatchers, start, origLen, newLen);
        Log.i(TAG, "textWatchers:"+textWatchers.length);
        
        boolean adjustSelection = origLen != 0 && newLen != 0;
        int selectionStart = 0;
        int selectionEnd = 0;
        if (adjustSelection) {
            selectionStart = Selection.getSelectionStart(this);
            selectionEnd = Selection.getSelectionEnd(this);
        }
		
		if(addLen>0)
			charsCopyEndToStart(mText, end, mText, end+addLen, length()+addLen);
		else
		if(addLen<0)
			charsCopyStartToEnd(mText, end, mText, end+addLen, length()+addLen);
		if(insertLen>0)
			TextUtils.getChars(tb, tbstart, tbend, mText, start);
		mLength+=addLen;
		

        if (adjustSelection) {
            if (selectionStart > start && selectionStart < end) {
                final int offset = (selectionStart - start) * newLen / origLen;
                selectionStart = start + offset;

                setSpan(/*false,*/ Selection.SELECTION_START, selectionStart, selectionStart,
                        Spanned.SPAN_POINT_POINT);
            }
            if (selectionEnd > start && selectionEnd < end) {
                final int offset = (selectionEnd - start) * newLen / origLen;
                selectionEnd = start + offset;

                setSpan(/*false,*/ Selection.SELECTION_END, selectionEnd, selectionEnd,
                        Spanned.SPAN_POINT_POINT);
            }
        }
        sendTextChanged(textWatchers, start, origLen, newLen);
        sendAfterTextChanged(textWatchers);

        SpanWatcher[] spanWatchers = getSpans(0,length(),SpanWatcher.class);
        for(SpanWatcher spanWatcher:spanWatchers){
        	Log.i(TAG, "spanWatcher:"+spanWatcher.getClass());
        }
        // Span watchers need to be called after text watchers, which may update the layout
        //sendToSpanWatchers(start, end, newLen - origLen);
		
		return this;
	}

    private void sendBeforeTextChanged(TextWatcher[] watchers, int start, int before, int after) {
        int n = watchers.length;

        for (int i = 0; i < n; i++) {
            watchers[i].beforeTextChanged(this, start, before, after);
        }
    }

    private void sendTextChanged(TextWatcher[] watchers, int start, int before, int after) {
        int n = watchers.length;

        for (int i = 0; i < n; i++) {
            watchers[i].onTextChanged(this, start, before, after);
        }
    }

    private void sendAfterTextChanged(TextWatcher[] watchers) {
        int n = watchers.length;

        for (int i = 0; i < n; i++) {
            watchers[i].afterTextChanged(this);
        }
    }
/*
    private void sendSpanAdded(Object what, int start, int end) {
        SpanWatcher[] recip = getSpans(start, end, SpanWatcher.class);
        int n = recip.length;

        for (int i = 0; i < n; i++) {
            recip[i].onSpanAdded(this, what, start, end);
        }
    }

    private void sendSpanRemoved(Object what, int start, int end) {
        SpanWatcher[] recip = getSpans(start, end, SpanWatcher.class);
        int n = recip.length;

        for (int i = 0; i < n; i++) {
            recip[i].onSpanRemoved(this, what, start, end);
        }
    }

    private void sendSpanChanged(Object what, int oldStart, int oldEnd, int start, int end) {
        // The bounds of a possible SpanWatcher are guaranteed to be set before this method is
        // called, so that the order of the span does not affect this broadcast.
        SpanWatcher[] spanWatchers = getSpans(Math.min(oldStart, start),
                Math.min(Math.max(oldEnd, end), length()), SpanWatcher.class);
        int n = spanWatchers.length;
        for (int i = 0; i < n; i++) {
            spanWatchers[i].onSpanChanged(this, what, oldStart, oldEnd, start, end);
        }
    }
*/
	@Override
	public Editable replace(int st, int en, CharSequence text) {
		return replace(st,en,text,0,text.length());
	}

	@Override
	public Editable insert(int where, CharSequence text, int start, int end) {
		return this;
	}

	@Override
	public Editable insert(int where, CharSequence text) {
		return this;
	}

	@Override
	public Editable delete(int st, int en) {
		return this;
	}

	@Override
	public Editable append(CharSequence text) {
		return this;
	}

	@Override
	public Editable append(CharSequence text, int start, int end) {
		return this;
	}

	@Override
	public Editable append(char text) {
		return this;
	}

	@Override
	public void clear() {
		mLength = 0;
		clearSpans();
	}

	@Override
	public void clearSpans() {
		mSpanInfos.clear();
	}
	
	@Override
	public void setFilters(InputFilter[] filters) {
		mFilters = filters;
	}

	@Override
	public InputFilter[] getFilters() {
		return mFilters;
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
	public String toString() {
		return String.copyValueOf(mText, 0, length());
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