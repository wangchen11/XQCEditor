package person.wangchen11.crash;

import java.io.ByteArrayOutputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.PrintStream;
import java.lang.Thread.UncaughtExceptionHandler;
import java.util.Date;

import person.wangchen11.gnuccompiler.GNUCCompiler;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.util.Log;

public class CrashHandler implements UncaughtExceptionHandler {
	private static final String TAG = "CrashHandler";
	@SuppressLint("SdCardPath")
	private static final String mCrashLogFile = GNUCCompiler.getWorkSpaceDir() + "/qeditor_crash.log";
	private int mVersionCodeNow = 0;
	private String mVersionNameNow = "unknown";
	
	public CrashHandler(Context context) {
		PackageManager packageManager=context.getPackageManager();
		PackageInfo packageInfo;
		try {
			packageInfo = packageManager.getPackageInfo(context.getPackageName(), 0);
			mVersionCodeNow=packageInfo.versionCode;
			mVersionNameNow=packageInfo.versionName;
		} catch (NameNotFoundException e) {
			e.printStackTrace();
		}
	}
	
	private static UncaughtExceptionHandler mDefaultCrashHandler = Thread.getDefaultUncaughtExceptionHandler();
	@Override
	public void uncaughtException(Thread thread, Throwable ex) {
		if(!handleUncaughtException(thread,ex))
			mDefaultCrashHandler.uncaughtException(thread, ex);
	}

	@SuppressWarnings("deprecation")
	public boolean handleUncaughtException(Thread thread, Throwable ex) {
		ByteArrayOutputStream arrayOutputStream = new ByteArrayOutputStream();
		PrintStream printStream = new PrintStream(arrayOutputStream);
		ex.printStackTrace(printStream);
		printStream.close();
		String msg = "mVersionCodeNow:"+mVersionCodeNow
				+"\nmVersionNameNow:"+mVersionNameNow+"\n"
				+"\nDate:"+new Date().toLocaleString()+"\n"
				+arrayOutputStream.toString();
		Log.i(TAG, "handleUncaughtException:"+msg);
		try {
			FileOutputStream fileOutputStream = new FileOutputStream(mCrashLogFile);
			try {
				fileOutputStream.write(msg.getBytes());
				fileOutputStream.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}
		return false;
	}
}
