package person.wangchen11.console;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import person.wangchen11.busybox.Busybox;
import person.wangchen11.gnuccompiler.GNUCCompiler;

import android.content.Context;
import android.os.Handler;
import android.util.Log;

public class Terminal extends Console{
	private Process mProcess;
	public Terminal(Handler handler, ConsoleCallback callback,Context context) {
		super(handler, callback,context);
	}

	@Override
	public InputStream getInputStream() {
		//createProcessIfNeed();
		return mProcess.getInputStream();
	}

	@Override
	public InputStream getErrorStream() {
		//createProcessIfNeed();
		return mProcess.getErrorStream();
	}

	@Override
	public OutputStream getOutputStream() {
		return mProcess.getOutputStream();
	}
	
	private synchronized void createProcessIfNeed(){
		if(mProcess==null)
			try {
				String shell="sh";
				mProcess=Runtime.getRuntime().exec(shell);
			} catch (IOException e) {
				e.printStackTrace();
			}
	}
	
	@Override
	public void doProcess() {
		createProcessIfNeed();
	}

	@Override
	public void onDestory() {
		Log.i(TAG, "Terminal onDestory");
		if(mProcess!=null)
		{
			/*
			try {
				byte data[]=new byte[1];
				data[0]=0x03;
				mProcess.getOutputStream().write(data);//发送Ctrl+c
				mProcess.getOutputStream().flush();
			} catch (IOException e) {
				e.printStackTrace();
			}*/
			mProcess.destroy();
			try {
				mProcess.waitFor();
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
			mProcess=null;
			mIsAlive=false;
		}
	}

	@Override
	public void onCreatedProcess() {
		String appendEnvPath = Busybox.getWorkDir(mContext);
		execute("export PATH=$PATH:"+appendEnvPath+"\n");
	}
}
