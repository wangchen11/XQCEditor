package person.wangchen11.questions;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.concurrent.TimeoutException;

import android.content.Context;
import android.graphics.Color;
import android.util.Log;

public class QuestionTask implements Runnable  {
	private int mTimeOut = 1000;
	private int mMarks = 0;
	private OnTaskCompliteListener mListener = null;
	public boolean  mTesting  = false;
	private boolean mComplite = false;
	private String mName = "";
	private boolean mStopFlag = false;
	public static int TRANSPARENT = Color.TRANSPARENT;
	public static int RED = Color.RED;
	public static int GREEN =  Color.GREEN;
	public static int YELLOW = Color.rgb(0xf0,0x80,0x80);
	
	public QuestionTask(Context context,String name,OnTaskCompliteListener compliteListener,int timeOut) {
		mListener = compliteListener;
		mTimeOut = timeOut;
		mName = name;
	}
	
	public String getName(){
		return mName;
	}
	
	@Override
	public void run() {
		mTesting = true;
		try {
			Thread.sleep(500);
		} catch (InterruptedException e1) {
			e1.printStackTrace();
		}
		try {
			inRun();
		} catch (Exception e) {
			e.printStackTrace();
		}
		mComplite = true;
		mTesting = false;
		mListener.onComplite(this);
	}
	
	public boolean isComplite(){
		return mComplite;
	}
	
	public void inRun(){
	}
	
	public int getMarks(){
		return mMarks;
	}
	
	public String getCompliteMsg(Context context){
		return "Done";
	}
	
	public int getColor(){
		return TRANSPARENT;
	}
	
	public boolean canContinue(){
		return true;
	}
	
	public int getTimeOut(){
		return mTimeOut;
	}
	

	public String runCmd(String cmd,int timeOut,boolean useErrorInput) throws IOException, TimeoutException{
		cmd+="\nexit\n";
		StringBuilder stringBuilder = new StringBuilder();
		Process process = null;
		InputStream inputStream = null;
		try { 
			process = Runtime.getRuntime().exec("sh");
			inputStream = useErrorInput?process.getErrorStream():process.getInputStream();
			 
			OutputStream outputStream = process.getOutputStream();
			outputStream.write(cmd.getBytes());
			outputStream.flush();
			int proLength=0;
			byte[] buffer=new byte[100];
			int timeUsed = 0;
			while(!mStopFlag){
				int available = inputStream.available();
				Log.i("Task", "available:"+inputStream.available());
				if(available<=0){
					try {
						//test process exited.
						process.exitValue();
						break;
					} catch (IllegalThreadStateException e) {
						//process does not exited! 
					}
					
					try {
						Thread.sleep(10);
						timeUsed+=10;
					} catch (InterruptedException e) {
						e.printStackTrace();
					}
					if(timeUsed>timeOut){
						throw new TimeoutException("Time out!");
					}
					continue;
				}
				int readLen = inputStream.read(buffer,proLength,Math.min(buffer.length-proLength,available));
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
		} catch (IOException e) {
			if(process!=null){
				process.destroy();
				process = null;
			}
			throw e;
		} finally {
			if(inputStream!=null){
				inputStream.close();
			}
			if(process!=null){
				process.destroy();
				process = null;
			}
		}
		return stringBuilder.toString();
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
	
	interface OnTaskCompliteListener {
		public void onComplite(QuestionTask questionTask);
	}
}
