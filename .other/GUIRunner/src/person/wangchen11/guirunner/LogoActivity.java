package person.wangchen11.guirunner;

import org.libsdl.app.SDLActivity;

import person.wangchen11.nativeview.DebugInfo;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;

public class LogoActivity extends Activity {
	static
	{
		try
		{
			System.loadLibrary("test");
		}
		catch (Exception localException)
	    {
			localException.printStackTrace();
	    }
		catch (Error localError)
		{
			localError.printStackTrace();
		}
	}
	
    private static final String TAG = "LogoActivity";
	@SuppressLint("DefaultLocale") 
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		Bundle bundle = getIntent().getExtras();
		if(bundle!=null)
		{
			if(getIntent()!=null){
				DebugInfo.mSoPath=bundle.getString("soPath");
				DebugInfo.mAssetsPath=bundle.getString("assetsPath");
				DebugInfo.mRequestVersion=bundle.getString("requestVersion");
				DebugInfo.mDebugType=bundle.getString("debugType");
				Log.i(TAG, "mSoPath"+DebugInfo.mSoPath);
				Log.i(TAG, "mAssetsPath"+DebugInfo.mAssetsPath);
				Log.i(TAG, "mRequestVersion"+DebugInfo.mRequestVersion);	
				Log.i(TAG, "mDebugType"+DebugInfo.mDebugType);	
			}
		}
		if(DebugInfo.mDebugType==null || DebugInfo.mDebugType.length()<=0){
			new Handler().postDelayed(new Runnable() {
				@Override
				public void run() {
					Intent intent = new Intent(LogoActivity.this,MainActivity.class);
					if(getIntent()!=null&&getIntent().getExtras()!=null)
						intent.putExtras(getIntent().getExtras());
					intent.putExtra("test1", "testValue1");
					intent.putExtra("test2", "testValue2");
					intent.putExtra("test3", "testValue3");
					intent.putExtra("test4", "testValue4");
					intent.putExtra("testInt", 128);
					startActivity(intent);
					LogoActivity.this.finish();
				}
			}, 20);
		}else if(DebugInfo.mDebugType.toLowerCase().equals("sdl2")){
			new Handler().postDelayed(new Runnable() {
				@Override
				public void run() {
					Intent intent = new Intent(LogoActivity.this,SDLActivity.class);
					if(getIntent()!=null&&getIntent().getExtras()!=null)
						intent.putExtras(getIntent().getExtras());
					startActivity(intent);
					LogoActivity.this.finish();
				}
			}, 20);
		}
	}
}
