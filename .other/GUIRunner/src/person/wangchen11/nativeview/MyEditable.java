package person.wangchen11.nativeview;

import android.text.Editable;
import android.text.InputFilter;

public class MyEditable implements Editable {

	@Override
	public char charAt(int index) {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public int length() {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public CharSequence subSequence(int start, int end) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void getChars(int start, int end, char[] dest, int destoff) {
		// TODO Auto-generated method stub

	}

	@Override
	public void removeSpan(Object what) {
		// TODO Auto-generated method stub

	}

	@Override
	public void setSpan(Object what, int start, int end, int flags) {
		// TODO Auto-generated method stub

	}

	@Override
	public int getSpanEnd(Object tag) {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public int getSpanFlags(Object tag) {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public int getSpanStart(Object tag) {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public <T> T[] getSpans(int arg0, int arg1, Class<T> arg2) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public int nextSpanTransition(int start, int limit, Class type) {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public Editable append(CharSequence text) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Editable append(char text) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Editable append(CharSequence text, int start, int end) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void clear() {
		// TODO Auto-generated method stub

	}

	@Override
	public void clearSpans() {
		// TODO Auto-generated method stub

	}

	@Override
	public Editable delete(int st, int en) {
		return replace(st, en, null, 0, 0);
	}

	@Override
	public InputFilter[] getFilters() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Editable insert(int where, CharSequence text) {
		return insert(where,text,0,text.length());
	}

	@Override
	public Editable insert(int where, CharSequence text, int start, int end) {
		int len = end-start;
		if(where<0 || where>length() || len<=0)
			return this;
		return replace(where,where, text, start, end);
	}

	@Override
	public Editable replace(int st, int en, CharSequence text) {
		return replace(st, en, text, 0, text.length());
	}

	@Override
	public Editable replace(int st, int en, CharSequence source, int start,
			int end) {
		return null;
	}

	@Override
	public void setFilters(InputFilter[] filters) {
	}

}
