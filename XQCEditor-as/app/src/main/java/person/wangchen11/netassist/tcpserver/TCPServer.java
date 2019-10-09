package person.wangchen11.netassist.tcpserver;

import java.io.IOException;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import android.util.Log;

public abstract class TCPServer {
	static final String TAG="TCPServer";
	private boolean mIsAlive = true;
	private int mPort = 0;
	private ExecutorService mExecutorService = null;
	private ServerSocket mServerSocket = null;
	
	public TCPServer(int port) {
		mPort = port;
	}
	
	public synchronized void start()
	{
		mIsAlive = true;
		if(mExecutorService!=null)
		{
			Log.e(TAG, "ERROR:TCPServer is already started!");
		}
		else
		{
			mExecutorService = Executors.newCachedThreadPool();
			execute(new accpetThreadRunnable());
		}
	}
	
	public synchronized void execute(Runnable runnable)
	{
		mExecutorService.execute(runnable);
	}
	
	public void newServerSocketHasException(Throwable throwable)
	{
		Log.i(TAG, "newServerSocketHasException:"+throwable);
	}
	
	public void acceptHasException(Throwable throwable)
	{
		Log.i(TAG, "acceptHasException:"+throwable);
	}
	
	public void serverStoped()
	{
		Log.i(TAG, "serverStoped");
	}
	
	public boolean isAlive()
	{
		return mIsAlive;
	}
	
	public void destory()
	{
		mIsAlive = false;
		if(mExecutorService==null)
			return ;
		execute(new Runnable() {
			@Override
			public void run() {
				try {
					if(mServerSocket!=null)
						mServerSocket.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		});
		mExecutorService.shutdown();
		mExecutorService = null;
	}
	
	public abstract void onAccepted(Socket socket);
	
	class accpetThreadRunnable implements Runnable
	{
		@Override
		public void run() {
			try {
				mServerSocket = new ServerSocket(mPort,0,InetAddress.getByName("0.0.0.0"));
				
			} catch (IOException e) {
				e.printStackTrace();
				newServerSocketHasException(e);
			} catch (Error e) {
				e.printStackTrace();
				newServerSocketHasException(e);
			}
			if(mServerSocket!=null)
			{
				Log.i(TAG,mServerSocket.toString());
				Socket socket = null;
				while( mIsAlive )
				{
					try {
						socket = mServerSocket.accept();
					} catch (IOException e) {
						acceptHasException(e);
						e.printStackTrace();
					}
					if(socket!=null)
						onAccepted(socket);
					socket = null;
				}
			}
			mIsAlive = false;
			serverStoped();
		}
	}
	
}
