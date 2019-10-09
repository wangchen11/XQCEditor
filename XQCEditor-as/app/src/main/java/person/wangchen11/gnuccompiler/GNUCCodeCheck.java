package person.wangchen11.gnuccompiler;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.LinkedList;

import person.wangchen11.plugins.PluginsManager;

import android.content.Context;
import android.util.Log;

public class GNUCCodeCheck {
	final static String TAG="GNUCCodeCheck";
	private String mCmd ="";
	private boolean mIsChecked = false;
	private LinkedList<CheckInfo> mCheckInfos = new LinkedList<CheckInfo>();
	private boolean mStopFlag = false;
	
	public GNUCCodeCheck(Context context,File file) {
		mCmd=PluginsManager.getInstance().getSourceCmd();
		mCmd+="cd \""+context.getFilesDir().getAbsolutePath()+"\"\n";
		File tempFile = new File(file.getAbsolutePath()+".o");
		try {
			tempFile = File.createTempFile("qeditor", null);
		} catch (IOException e) {
			e.printStackTrace();
		} catch (Error e) {
			e.printStackTrace();
		}
		tempFile.deleteOnExit();
		mCmd+=GNUCCompiler2.getCompilerOnlyCmd(file,tempFile,null);
		mCmd+="\nexit\n";
	}
	
	public void stop(){
		mStopFlag = true;
	}
	
	public boolean isChencked()
	{
		return mIsChecked;
	}
	
	public LinkedList<CheckInfo> start()
	{
		mIsChecked = false;
		mCheckInfos.clear();
		Log.i(TAG, "start");
		try {
			startEx();
		} catch (Exception e) {
			e.printStackTrace();
		} catch (Error e) {
			e.printStackTrace();
		}
		Log.i(TAG, "mCheckInfos:"+mCheckInfos);
		mIsChecked = true;
		return mCheckInfos;
	}
	
	private void startEx() 
	{
		try {
			StringBuilder stringBuilder = new StringBuilder();
			Process process = Runtime.getRuntime().exec("sh");
			InputStream inputStream = process.getErrorStream();
			OutputStream outputStream = process.getOutputStream();
			outputStream.write(mCmd.getBytes());
			outputStream.flush();
			int proLength=0;
			byte[] buffer=new byte[100];
			while(!mStopFlag){
				try {
					Thread.sleep(10);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
				int readLen = inputStream.read(buffer,proLength,buffer.length-proLength);
				if(readLen>0)
				{
					int nowLength=proLength+readLen;
					int utf8len=getUtf8Length(buffer,nowLength);
					if(nowLength==utf8len)
					{
						stringBuilder.append(new String(buffer,0,utf8len));
						proLength=0;
					}
					else
					if(nowLength>utf8len)
					{
						stringBuilder.append(new String(buffer,0,utf8len));
						proLength=nowLength-utf8len;
						System.arraycopy(buffer, utf8len, buffer, 0, proLength);
					}
					else
					{
						proLength=0;
					}
				}
				else
					break;
			}
			if(!mStopFlag){
				mCheckInfos = dexErrorPutMsg(stringBuilder.toString());
			}
		} catch (IOException e) {
			e.printStackTrace();
		}

	}
	
	public static LinkedList<CheckInfo> dexErrorPutMsg(String msg)
	{
		LinkedList<CheckInfo> checkInfos = new LinkedList<CheckInfo>();
		String []lines = msg.split("\n");
		for (int i = 0; i < lines.length; i++) {
			String line = lines[i];
			String[] items = line.split(":");
			if(items == null||items.length<5)
				continue;
			else
			{
				try {
					int lineAt = Integer.valueOf(items[1]);
					int charAt = Integer.valueOf(items[2]);
					int type = CheckInfo.TYPE_WARN;
					if(items[3].contains("error"))
					{
						type = CheckInfo.TYPE_ERROR;
					}
					else
					if(items[3].contains("warning"))
					{
						type = CheckInfo.TYPE_WARN;
					}
					else
						continue;
					String wmsg = items[4];
					for(int j=5;j<items.length;j++)
					{
						wmsg+=":"+items[j];
					}
					checkInfos.add(new CheckInfo(items[0], wmsg, type, lineAt, charAt));
					
				}catch (Exception e) {
				}
			}
		}
		return checkInfos;
	}

	private static int getUtf8Length(byte data[],int length)
	{
		int offset=0;
		while(true)
		{
			if(offset>=length)
				break;
			int bytes=getUtf8ByteLength(data[offset]);
			int nextOffset=offset+bytes;
			if(nextOffset==length)
			{
				offset=length;
				break;
			}
			if(nextOffset>length)
			{
				break;
			}
			offset=nextOffset;
		}
		return offset;
	}
	
	private static int getUtf8ByteLength(byte b)
	{
		int data=((int) b)&0xff;
		if( (data&0b10000000) == 0b00000000)
			return 1;
		if( (data&0b11100000) == 0b11000000)
			return 2;
		if( (data&0b11110000) == 0b11100000)
			return 3;
		if( (data&0b11111000) == 0b11110000)
			return 4;
		if( (data&0b11111100) == 0b11111000)
			return 5;
		if( (data&0b11111110) == 0b11111100)
			return 6;
		return 1;
	}
}
