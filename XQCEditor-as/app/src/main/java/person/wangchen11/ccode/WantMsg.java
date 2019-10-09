package person.wangchen11.ccode;

import android.util.Log;

public class WantMsg {
	public String mReplace;
	public String mTip;
	public WantMsg(String replace) {
		this(replace,"");
	}
	
	public WantMsg(String replace,String tip) {
		mReplace = replace;
		mTip = tip;
	}
	
	@Override
	public boolean equals(Object o) {
		if( o instanceof String ){
			Log.i("equals:", ""+o+":"+mReplace);
			return ((String)o).equals(mReplace);
		}
		if( !(o instanceof WantMsg) )
			return false;
		return mReplace.equals(((WantMsg)o).mReplace);
	}
	
	@Override
	public String toString() {
		return mReplace;
	}
}
