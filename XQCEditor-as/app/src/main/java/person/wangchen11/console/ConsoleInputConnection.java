package person.wangchen11.console;

import java.util.ArrayList;

import android.content.Context;
import android.graphics.Color;
import android.text.Editable;
import android.text.Selection;
import android.text.style.ForegroundColorSpan;
import android.util.Log;
import android.view.View;
import person.wangchen11.busybox.Busybox;
import person.wangchen11.console.Console.ConsoleCallback;
import person.wangchen11.editor.edittext.EditableWithLayout;
import person.wangchen11.editor.edittext.MyInputConnection;
import person.wangchen11.editor.edittext.SpanBody;
import person.wangchen11.gnuccompiler.GNUCCompiler2;
import person.wangchen11.plugins.PluginsManager;

public class ConsoleInputConnection extends MyInputConnection implements ConsoleCallback{
	private Console mConsole;
	private int mProEndPosition=0;
	private int mStartPosition=0;
	private int mMaxLine=1000;
	
	public ConsoleInputConnection(View view) {
		super(view);
		mConsole=new Terminal(view.getHandler(),this,view.getContext());
		//mConsole.execute(getExportPathCmd(view.getContext()));
	}
	
	public static String getExportPathCmd(Context context)
	{
		String cmd="";
		cmd+="export APP_PATH=\""+context.getFilesDir().getAbsolutePath()+"\"\n";
		cmd+=Busybox.getCmd(context);
		cmd+=PluginsManager.getCmd(context);
		cmd+=GNUCCompiler2.getExportEnvPathCmd(context);
		return cmd;
	}
	
	public Console getConsole(){
		return mConsole;
	}
	
	private static String[] splitString(String str,String split)
	{
		String strs[];
		ArrayList<String> arrayList=new ArrayList<String>();
		int start,pos;
		for(start=0,pos=0;start<str.length();start=pos+1)
		{
			pos=str.indexOf('\n', start);
			if(pos>=0)
			{
				if(pos==start)
					arrayList.add("");
				else
					arrayList.add(str.substring(start, pos));
			}
			else
			{
				break;
			}
		}
		if(str.length()==start)
			arrayList.add("");
		else
			arrayList.add(str.substring(start));
		strs=new String[arrayList.size()];
		arrayList.toArray(strs);
		return strs;
	}

	ForegroundColorSpan errorColorSpan=new ForegroundColorSpan(Color.rgb(0xff, 0x80, 0x80));
	ForegroundColorSpan warningColorSpan=new ForegroundColorSpan(Color.rgb(0xb0, 0xa0, 0x00));
	@Override
	public synchronized void onReadData(Console console, byte[] data, int len,
			boolean isError) {
		String str=new String(data,0,len);
		if(isError){
			String []lines=splitString(str,"\n");
			if(lines!=null&&lines.length>0)
			if(mEditable instanceof EditableWithLayout){
				for(int i=0;i<lines.length;i++)
				{
					String line=lines[i];
					if(i!=lines.length-1 )
					{
						line+="\n";
					}
					mEditable.replace(mProEndPosition, mProEndPosition,line);
					if(line.contains("error:"))
						((EditableWithLayout)mEditable).addColorSpan(new SpanBody(errorColorSpan , mProEndPosition, mProEndPosition+line.length(), 0));
					else
						((EditableWithLayout)mEditable).addColorSpan(new SpanBody(warningColorSpan , mProEndPosition, mProEndPosition+line.length(), 0));
					mProEndPosition+=line.length();
					mStartPosition+=line.length();
					int max=getEditable().length();
					if(mProEndPosition>max)
						mProEndPosition=max;
					if(mStartPosition>max)
						mStartPosition=max;
				}
			}
		}
		else
		{
			mEditable.replace(mProEndPosition, mProEndPosition,str);
			mProEndPosition+=str.length();
			mStartPosition+=str.length();
			int max=getEditable().length();
			if(mProEndPosition>max)
				mProEndPosition=max;
			if(mStartPosition>max)
				mStartPosition=max;
		}
		checkLine();
	}

	@Override
	public void onConsoleClosed(Console console) {
		if(mColseListener!=null)
			mColseListener.onConsoleClose(console);
		Log.i(TAG, "onConsoleClosed");
	}
    public boolean onEnterKey(){
    	setSelection( getEditable().length(), getEditable().length());
    	int len=getEditable().length();
    	getEditable().replace(len, len, "\n");
    	String cmd= getEditable().subSequence(mStartPosition,len )+"\n";
    	Log.i(TAG, "onEnterKey:cmd:"+cmd);
    	mConsole.execute(cmd);
    	//mConsole.showPs1();
    	mStartPosition=getEditable().length();
    	mProEndPosition=mStartPosition;
    	checkLine();
		return true;
    }
    
    public void checkLine(){
    	int lineCount=getLayout().getLineCount();
    	if(lineCount>mMaxLine){
    		int deleteLen=getLayout().getLineOffset(lineCount-mMaxLine+1);
    		mEditable.replace(0, deleteLen, "",0,0);
        	mStartPosition=getEditable().length();
        	mProEndPosition=mStartPosition;
    	}
    }
    
    @Override
    public boolean commitText(CharSequence text, int newCursorPosition) {
		Editable content=getEditable();
    	int start=Selection.getSelectionStart(content);
    	if(start<mStartPosition)
    		return false;
    	return super.commitText(text, newCursorPosition);
    }
    
    public boolean onDeleteKey(){
    	if(getEditable().length()>mStartPosition)
    		return false;
    	return true;
    		
    }
    
    @Override
    public boolean setSelection(int start, int end) {
    	if(start==end && start<mStartPosition)
    		start=end=mStartPosition;
    	return super.setSelection(start, end);
    }
    
	public void destory(){
		mConsole.destory();
	}
	
	private OnConsoleColseListener mColseListener;
	public void setConsoleCloseListener( OnConsoleColseListener colseListener){
		mColseListener=colseListener;
	}
}
