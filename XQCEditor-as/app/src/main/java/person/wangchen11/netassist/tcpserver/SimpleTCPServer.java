package person.wangchen11.netassist.tcpserver;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

import android.util.Log;

public class SimpleTCPServer extends TCPServer{
	final static String TAG = "SimpleTCPServer";
	private int mReadBufferSize = 1024;
	private SimpleTCPServerListener mSimpleTCPServerListener = null;
	private List<Socket> mClients =  new LinkedList<Socket>();

	public SimpleTCPServer(int port) {
		super(port);
	}
	
	@Override
	public void onAccepted(Socket socket) {
		Log.i(TAG, "onAccepted:"+socket);
		execute(new ReadRunnable(socket));
	}
	
	@Override
	public void newServerSocketHasException(Throwable throwable) {
		super.newServerSocketHasException(throwable);
		if(mSimpleTCPServerListener!=null)
			mSimpleTCPServerListener.onStartFailed(throwable);
	}
	
	@Override
	public void acceptHasException(Throwable throwable) {
		super.acceptHasException(throwable);
	}
	
	@Override
	public void serverStoped() {
		super.serverStoped();
		if(mSimpleTCPServerListener!=null)
			mSimpleTCPServerListener.onServerStoped();
	}

	public void sendData(Socket socket,byte data[])
	{
		execute(new SendDataRunnable(socket, data, 0, data.length));
	}

	public void sendData(Socket socket,byte data[],int offset,int count)
	{
		execute(new SendDataRunnable(socket, data, offset, count));
	}

	public void sendDataEx(Socket socket,byte data[],int offset,int count)
	{
		OutputStream outputStream = null;
		try {
			outputStream = socket.getOutputStream();
			outputStream.write(data, offset, count);
			outputStream.flush();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	private void addClient(Socket socket)
	{
		synchronized (mClients) {
			mClients.add(socket);
		}
		try {
			if(mSimpleTCPServerListener!=null)
				mSimpleTCPServerListener.onSocketAdded(this, socket);
		} catch (NullPointerException e) {
			//ignore it 
		}
	}
	

	private void removeClient(Socket socket)
	{
		synchronized (mClients) {
			if(mClients.contains(socket));
				mClients.remove(socket);
		}
		try {
			if(mSimpleTCPServerListener!=null)
				mSimpleTCPServerListener.onSocketRemoved(this, socket);
		} catch (NullPointerException e) {
			//ignore it 
		}
	}
	
	public List<Socket> getClientsCopy()
	{
		ArrayList<Socket> arrayList = new ArrayList<Socket>();
		synchronized (mClients) {
			arrayList.addAll(mClients);
		}
		return arrayList;
	}
	
	
	public void setSimpleTCPServerListener(SimpleTCPServerListener listener) {
		mSimpleTCPServerListener = listener;
	}
	
	public interface SimpleTCPServerListener{
		public void onReceivedData(SimpleTCPServer simpleTCPServer,Socket socket
				,byte []buffer,int offset,int len);
		
		public void onSocketAdded(SimpleTCPServer simpleTCPServer,Socket socket);
		
		public void onSocketRemoved(SimpleTCPServer simpleTCPServer,Socket socket);
		
		public void onStartFailed(Throwable throwable);
		
		public void onServerStoped();
	}
	
	public class ReadRunnable implements Runnable{
		private Socket mSocket = null;
		private byte []mReadBuffer = null;
		public ReadRunnable(Socket socket) {
			mSocket = socket;
			mReadBuffer = new byte[mReadBufferSize];
		}
		
		@Override
		public void run() {
			addClient(mSocket);
			try {
				doRead();
			} catch (Throwable e) {
			}
			removeClient(mSocket);
			try {
				mSocket.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		
		private void doRead()
		{
			InputStream inputStream = null;
			try {
				inputStream = mSocket.getInputStream();
			} catch (IOException e) {
				e.printStackTrace();
			}
			
			if(inputStream!=null)
			while(isAlive()&&mSocket.isConnected())
			{
				try {
					int readLen = inputStream.read(mReadBuffer);
					if(readLen<=0)
						break;
					try {
						if(mSimpleTCPServerListener!=null)
							mSimpleTCPServerListener.onReceivedData(SimpleTCPServer.this, mSocket
									, mReadBuffer, 0, readLen);
					} catch (NullPointerException e) {
						//just ignore it!
					}
						
				} catch (IOException e) {
					e.printStackTrace();
					break;
				}
			}
			
		}
	}
	
	class SendDataRunnable implements Runnable
	{
		private Socket mSocket = null;
		private byte []mBuffer = null;
		public SendDataRunnable(Socket socket,byte data[],int offset,int count) {
			mSocket = socket;
			mBuffer = new byte[count];
			System.arraycopy(data, offset, mBuffer, 0, count);
		}
		
		@Override
		public void run() {
			SimpleTCPServer.this.sendDataEx(mSocket,mBuffer,0,mBuffer.length);
		}
		
		
	}

}
