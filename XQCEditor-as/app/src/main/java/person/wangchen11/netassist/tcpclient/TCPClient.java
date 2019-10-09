package person.wangchen11.netassist.tcpclient;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import android.util.Log;

public class TCPClient {
	final static String TAG = "TCPClient";
	private String mAddr = "";
	private int mPort = 0;
	private ExecutorService mExecutorService = null;
	private Socket mSocket = null;
	private boolean mIsAlive = true;
	private int mReadBufferSize = 1024;
	
	public TCPClient(String addr,int port) {
		mAddr = addr;
		mPort = port;
		mIsAlive = true;
	}
	
	public void connectHasException(Throwable throwable)
	{
		Log.d(TAG, ""+throwable.getMessage());
	}
	
	public void onStoped()
	{
		Log.d(TAG, "onStoped");
	}
	
	public void onReceived(Socket sockets,byte []data,int offset,int count)
	{
	}

	
	public void start()
	{
		if(mExecutorService!=null)
		{
			Log.e(TAG, "TCPClient is already started!");
			return ;
		}
		
		mExecutorService = Executors.newFixedThreadPool(2);
		mExecutorService.execute(new ConnectAndSendRunnable());
	}
	
	public void sendData(byte []data)
	{
		sendData(data,0,data.length);
	}
	
	public void sendData(byte []data,int offset,int count)
	{
		mExecutorService.execute(new SendDataRunnable(data, offset, count));
	}
	
	public void sendDataEx(byte []data,int offset,int count)
	{
		if(mSocket!=null)
		{
			OutputStream outputStream = null;
			try {
				outputStream = mSocket.getOutputStream();
			} catch (IOException e) {
				e.printStackTrace();
			}
			if(outputStream!=null)
			{
				try {
					outputStream.write(data, offset, count);
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
	}
	
	public void destory()
	{
		if(mExecutorService==null)
			return;
		mExecutorService.execute(new Runnable() {
			@Override
			public void run() {
				try {
					if(mSocket!=null)
						mSocket.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		});
		mExecutorService = null;
	}
	
	class ConnectAndSendRunnable implements Runnable
	{
		@Override
		public void run() {
			byte buffer[] = new byte[mReadBufferSize];
			try {
				mSocket = new Socket(mAddr, mPort);
			} catch (UnknownHostException e) {
				e.printStackTrace();
				connectHasException(e);
			} catch (IOException e) {
				e.printStackTrace();
				connectHasException(e);
			}
			if(mSocket!=null)
			{
				InputStream inputStream = null;
				try {
					inputStream = mSocket.getInputStream();
				} catch (IOException e) {
					e.printStackTrace();
				}
				if(inputStream!=null)
				{
					while(mIsAlive&&mSocket.isConnected())
					{
						try {
							int readLen = inputStream.read(buffer);
							if(readLen<=0)
								break;
							onReceived(mSocket,buffer,0,readLen);
						} catch (IOException e) {
							e.printStackTrace();
							break;
						}
					}
				}
				try {
					mSocket.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
			onStoped();
		}
	}
	
	class SendDataRunnable implements Runnable
	{
		byte []mBuffer;
		int mOffset;
		public SendDataRunnable(byte []data,int offset,int count) {
			mBuffer = new byte[count];
			System.arraycopy(data, offset, mBuffer, 0, count);
		}

		@Override
		public void run() {
			sendDataEx(mBuffer, 0, mBuffer.length);
		}
	}
}
