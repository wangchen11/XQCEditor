package person.wangchen11.editor.codeedittext;

import java.util.LinkedList;
import java.util.List;

import android.graphics.Color;
import android.os.Handler;
import android.text.style.ForegroundColorSpan;

import person.wangchen11.ccode.WantMsg;
import person.wangchen11.editor.edittext.SpanBody;

public abstract class CodeStyleAdapter implements Runnable{
	Handler mHandler = null;
	int mLength = 0;
	CodeStypeAdapterListener mAdapterListener = null;
	public CodeStyleAdapter(Handler handler,int length,CodeStypeAdapterListener adapterListener) {
		mHandler = handler;
		mLength = length;
		mAdapterListener = adapterListener;
	}
	
	/**
	 * 返回当前的文本长度
	 * 用于提前终止任务，节省开销
	 * @return
	 */
	public abstract void parser();
	public abstract List<SpanBody> getStyles();
	public abstract LinkedList<WantMsg> getWants();
	public abstract int getWantChangeStart();
	public abstract int getWantChangeEnd();
	
	
	public static int mCommentsColor=Color.rgb( 0x60, 0xa0, 0x60);
	public static int mConstantColor=Color.rgb( 0xff, 0x80, 0x80);
	public static int mKeywordsColor=Color.rgb( 0x80, 0x80, 0xff);
	public static int mProKeywordsColor =Color.rgb( 0x80, 0x80, 0xff);
	public static int mWordsColor =Color.rgb( 0x80, 0x80, 0x80);

	public static ForegroundColorSpan mCommentsColorSpan=new ForegroundColorSpan(mCommentsColor);
	public static ForegroundColorSpan mConstantColorSpan=new ForegroundColorSpan(mConstantColor);
	public static ForegroundColorSpan mKeywordsColorSpan=new ForegroundColorSpan(mKeywordsColor);
	public static ForegroundColorSpan mProKeywordsColorSpan=new ForegroundColorSpan(mProKeywordsColor);
	public static ForegroundColorSpan mWordsColorSpan=new ForegroundColorSpan(mWordsColor);
	
	public static void refColorSpan(){
		mCommentsColorSpan=new ForegroundColorSpan(mCommentsColor);
		mConstantColorSpan=new ForegroundColorSpan(mConstantColor);
		mKeywordsColorSpan=new ForegroundColorSpan(mKeywordsColor);
		mProKeywordsColorSpan=new ForegroundColorSpan(mProKeywordsColor);
		mWordsColorSpan=new ForegroundColorSpan(mWordsColor);
	}

	public int length()
	{
		return mLength;
	}
	
	@Override
	public void run() {
		try {
			if(length()==checkLength())
				Thread.sleep(200);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}

		try {
			try {
				if(length()==checkLength())
				{
					parser();
					if(length()==checkLength())
					{
						final List<SpanBody> styles=getStyles();
						mHandler.post(new Runnable() {
							@Override
							public void run() {
								if(mAdapterListener!=null)
									mAdapterListener.parserComplete(CodeStyleAdapter.this,styles);
							}
						});
					}
					
					final LinkedList<WantMsg> linkedList=getWants();
					mHandler.post(new Runnable() {
						@Override
						public void run() {
							if(mAdapterListener!=null)
								mAdapterListener.getWantComplete(CodeStyleAdapter.this,getWantChangeStart(),getWantChangeEnd(),linkedList);
						}
					});
				}
			} catch (Error e) {
				e.printStackTrace();
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	private int checkLength()
	{
		if(mAdapterListener == null)
			return 0;
		return mAdapterListener.checkLength();
	}
	
	public interface CodeStypeAdapterListener{
		public abstract int checkLength();
		public abstract void parserComplete(CodeStyleAdapter parser,List<SpanBody> spanBodies);
		public abstract void getWantComplete(CodeStyleAdapter parser,int wantChangeStart,int wantChangeEnd,List<WantMsg> wants);
	}
}
