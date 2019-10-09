package person.wangchen11.editor.codeedittext;

import android.annotation.SuppressLint;
import android.util.Log;

import java.util.Iterator;
import java.util.LinkedList;

import person.wangchen11.codeanalysis.phpcode.PHPCodeParser;
import person.wangchen11.codeanalysis.phpcode.PHPCodeSpan;

public class PHPIndexer implements Runnable{
	private final static String TAG = "PHPIndexer";
	private PHPCodeParser mCodeParser;
	private int mPos;
	private PHPCodeSpan mNow = null;
	private LinkedList<String> mWants = new LinkedList<String>();
	public PHPIndexer(PHPCodeParser parser,int pos) {
		mCodeParser = parser;
		mPos = pos;
	}
	
	@SuppressLint("DefaultLocale") 
	@Override
	public void run() {
		Log.i(TAG, "run");
		LinkedList<PHPCodeSpan> codeSpans = mCodeParser.getCodeSpans();
		Iterator<PHPCodeSpan> iterator = codeSpans.iterator();
		while(iterator.hasNext())
		{
			PHPCodeSpan codeSpan = iterator.next();
			if(codeSpan.hasPosition(mPos-1))
			{
				mNow = codeSpan;
				break;
			}
		}
		if(mNow == null)
			return;
		
		iterator = codeSpans.iterator();
		while(iterator.hasNext())
		{
			PHPCodeSpan codeSpan = iterator.next();
			if( codeSpan.mContent.length()>mNow.mContent.length() )
			{
				if( codeSpan.mContent.startsWith(mNow.mContent) )
				{
					addWant(codeSpan);
				}
				else
				if( codeSpan.mContent.toLowerCase().startsWith(mNow.mContent.toLowerCase()) )
				{
					addWant(codeSpan);
				}
			}
		}
	}
	
	public void addWant(PHPCodeSpan codeSpan){
		if(!mWants.contains(codeSpan.mContent))
		{
			mWants.addLast(codeSpan.mContent);
		}
	}

	public LinkedList<String> getWants() {
		if(mNow==null)
			return null;
		return mWants;
	}

	public int getWantChangeStart() {
		if(mNow==null)
			return 0;
		return mNow.mStart;
	}

	public int getWantChangeEnd() {
		if(mNow==null)
			return 0;
		return mNow.mEnd;
	}
}
