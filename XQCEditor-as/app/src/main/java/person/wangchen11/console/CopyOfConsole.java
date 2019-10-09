package person.wangchen11.console;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import android.os.Handler;
import android.util.Log;

public abstract class CopyOfConsole {
	protected static final String TAG="Console";
	private Handler mHandler;
	protected boolean mIsAlive;
	private ConsoleCallback mCallback=null;
	private OutputStream mOutputStream=null;
	private int mMaxLength=4096;
	private String mCachedCmd="";
	private boolean mNeedErrorIntputStream=true;

	public CopyOfConsole(Handler handler,ConsoleCallback callback) {
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
					mOutputStream=getOutputStream();
					mHandler.post(new Runnable() {
						@Override
						public void run() {
							execute(mCachedCmd);
						}
					});
					InputStreamReader inputStreamReader=new InputStreamReader(inputStream);
					char[] buffer=new char[256];
					while(mIsAlive){
						try {
							Thread.sleep(20);
						} catch (Exception e) {
						}
						int readLen =inputStreamReader.read(buffer);//= inputStream.read(buffer);
						Log.i(TAG, "post:"+readLen);
						if(readLen>0)
						{
							mHandler.post(new PostDataRunnable(CopyOfConsole.this, buffer, readLen, false));
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
								mCallback.onConsoleClosed(CopyOfConsole.this);
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
						char[] buffer=new char[256];
						InputStreamReader inputStreamReader=new InputStreamReader(inputStream);
						while(mIsAlive){
							try {
								Thread.sleep(0);
							} catch (Exception e) {
							}
							int readLen = inputStreamReader.read(buffer);//inputStream.read(buffer);
							Log.i(TAG, "err post:"+readLen);
							if(readLen>0)
							{
								if(mNeedErrorIntputStream)
									mHandler.post(new PostDataRunnable(CopyOfConsole.this, buffer, readLen, true));
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
	
	class PostDataRunnable implements Runnable{
		CopyOfConsole mConsole;
		char []mData;
		int mLen;
		boolean mIsError;
		public PostDataRunnable(CopyOfConsole console,char []data,int len,boolean isError) {
			mConsole=console;
			mData=new char[len];
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
	
	interface ConsoleCallback {
		public void onReadData(CopyOfConsole console,char []data,int len,boolean isError);
		public void onConsoleClosed(CopyOfConsole console);
	}

	public abstract void doProcess();
	public abstract void onDestory();
	public abstract InputStream getInputStream();
	public abstract InputStream getErrorStream();
	public abstract OutputStream getOutputStream();
}
