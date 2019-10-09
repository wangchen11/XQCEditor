package person.wangchen11.console;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import android.content.Context;
import android.os.Handler;
import android.util.Log;

public abstract class Console {
	protected static final String TAG="Console";
	private Handler mHandler;
	protected boolean mIsAlive;
	private ConsoleCallback mCallback=null;
	private OutputStream mOutputStream=null;
	private int mMaxLength=4096;
	private String mCachedCmd="";
	private boolean mNeedErrorIntputStream=true;
	protected Context mContext;
	
	public Console(Handler handler,ConsoleCallback callback,Context context) {
		mContext = context;
		setCallback(callback);
		mHandler=handler;
		if(mHandler==null)
			mHandler=new Handler();
		mIsAlive=true;
		new Thread(new Runnable() {
			@Override
			public void run() {
				try 
				{
					doProcess();
					InputStream inputStream=getInputStream();
					//ConsoleName:java.lang.ProcessManager$ProcessInputStream
					Log.d(TAG, "ConsoleName:"+inputStream.getClass().getName());
					mOutputStream=getOutputStream();
					onCreatedProcess();
					mHandler.post(new Runnable() {
						@Override
						public void run() {
							execute(mCachedCmd);
						}
					});
					int proLength=0;
					byte[] buffer=new byte[100];
					while(mIsAlive){
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
								mHandler.post(new PostDataRunnable(Console.this, buffer, utf8len, false));
								proLength=0;
							}
							else
							if(nowLength>utf8len)
							{
								mHandler.post(new PostDataRunnable(Console.this, buffer, utf8len, false));
								proLength=nowLength-utf8len;
								System.arraycopy(buffer, utf8len, buffer, 0, proLength);
							}
							else
							{
								proLength=0;
								Log.e(TAG, "nowLength<utf8len");
							}
						}
						else
							break;
					}
				} catch (IOException e) {
					e.printStackTrace();
				}
				if(mIsAlive)
				{
					mHandler.post(new Runnable() {
						@Override
						public void run() {
							if(mCallback!=null)
								mCallback.onConsoleClosed(Console.this);
						}
					});
				}
				mIsAlive=false;
			}
		}).start();
		
		new Thread(new Runnable() {
			@Override
			public void run() {
				try 
				{
					doProcess();
					InputStream inputStream=getErrorStream();
					if(inputStream!=null)
					{
						int proLength=0;
						byte[] buffer=new byte[1024];
						while(mIsAlive){
							try {
								Thread.sleep(0,1000);
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
									if(mNeedErrorIntputStream)
										mHandler.post(new PostDataRunnable(Console.this, buffer, utf8len, true));
									proLength=0;
								}
								else
								if(nowLength>utf8len)
								{
									mHandler.post(new PostDataRunnable(Console.this, buffer, utf8len, false));
									proLength=nowLength-utf8len;
									System.arraycopy(buffer, utf8len, buffer, 0, proLength);
								}
								else
								{
									proLength=0;
									Log.e(TAG, "nowLength<utf8len");
								}
							}
							else
								break;
						}
					}
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}).start();
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
	
	public void execute(byte data[],int offset,int count){
		if(mIsAlive)
		{
			if(mOutputStream!=null){
				try {
					mOutputStream.write(data,offset,count);
					mOutputStream.flush();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}else{
				String str=new String(data,offset,count);
				if(mCachedCmd.length()>mMaxLength){
					mCachedCmd=str;
				}else{
					mCachedCmd+=str;
				}
			}
		}
	}

	public void execute(byte data[]){
		execute(data,0,data.length);
	}
	
	public void execute(String cmd){
		execute(cmd.getBytes());
	}
	/*
	public void showPs1()
	{
		String cmd="echo \"$PS1\\c\"\n";
		execute(cmd);
	}*/
	
	class PostDataRunnable implements Runnable{
		Console mConsole;
		byte []mData;
		int mLen;
		boolean mIsError;
		public PostDataRunnable(Console console,byte []data,int offset,int len,boolean isError) {
			mConsole=console;
			mData=new byte[len];
			System.arraycopy(data, offset, mData, 0, len);
			mLen=len;
			mIsError=isError;
		}
		public PostDataRunnable(Console console,byte []data,int len,boolean isError) {
			mConsole=console;
			mData=new byte[len];
			System.arraycopy(data, 0, mData, 0, len);
			mLen=len;
			mIsError=isError;
		}
		
		@Override
		public void run() {
			if(mCallback!=null)
				mCallback.onReadData(mConsole, mData, mLen, mIsError);
		}
		
	}
	
	public void setCallback(ConsoleCallback callback){
		mCallback=callback;
	}
	
	public void destory(){
		onDestory();
		mIsAlive=false;
	}

	public void disableErrorInput(){
		mNeedErrorIntputStream=false;
	}
	
	public void EnableErrorInput(){
		mNeedErrorIntputStream=true;
	}
	
	public interface ConsoleCallback {
		public void onReadData(Console console,byte []data,int len,boolean isError);
		public void onConsoleClosed(Console console);
	}

	public abstract void doProcess();
	public abstract void onCreatedProcess();
	public abstract void onDestory();
	public abstract InputStream getInputStream();
	public abstract InputStream getErrorStream();
	public abstract OutputStream getOutputStream();
}
