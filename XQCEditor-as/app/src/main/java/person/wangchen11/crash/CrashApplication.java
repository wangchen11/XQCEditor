package person.wangchen11.crash;

import android.app.Application;

public class CrashApplication extends Application {
	@Override
	public void onCreate() {
		/*
		final CrashHandler mCrashHandler = new CrashHandler(getApplicationContext());
		Thread.setDefaultUncaughtExceptionHandler(mCrashHandler);
		new Handler().postDelayed(new Runnable() {
			@Override
			public void run() {
				Thread.setDefaultUncaughtExceptionHandler(mCrashHandler);
			}
		},1000);*/
		super.onCreate();
	}
}
