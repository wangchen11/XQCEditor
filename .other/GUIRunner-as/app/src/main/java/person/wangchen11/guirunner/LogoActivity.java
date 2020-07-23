package person.wangchen11.guirunner;

import org.libsdl.app.SDLActivity;

import person.wangchen11.nativeview.DebugInfo;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

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
		if(requestNeedPermissions()){
			handleIntent();
		}
	}

	private void handleIntent(){
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


	@SuppressLint("NewApi")
	private boolean requestNeedPermissions() {
		if(Build.VERSION.SDK_INT>=Build.VERSION_CODES.M) {
			List<String > needPremissions = new LinkedList<String>();
			needPremissions.add(Manifest.permission.WRITE_EXTERNAL_STORAGE);

			ArrayList<String> permissions=new ArrayList<String>();
			for(String premission:needPremissions){
				if(this.checkSelfPermission(premission)!= PackageManager.PERMISSION_GRANTED) {
					permissions.add(premission);
				}
			}
			if(permissions.size()>0) {
				String []strs=new String[permissions.size()];
				permissions.toArray(strs);
				this.requestPermissions(strs, 0);
				Log.i(TAG, "requestPermissions:"+strs.length);
				return false;
			}
		}
		return true;
	}

	@SuppressLint("NewApi")
	@Override
	public void onRequestPermissionsResult(int requestCode,
										   String[] permissions, int[] grantResults) {
		super.onRequestPermissionsResult(requestCode, permissions, grantResults);
		String msg = "";
		for(int i=0;i<permissions.length;i++) {
			Log.i(TAG,""+i+":"+grantResults[i]+":"+permissions[i]);
			if(grantResults[i]==android.content.pm.PackageManager.PERMISSION_GRANTED) {
				if(permissions[i].equals(Manifest.permission.WRITE_EXTERNAL_STORAGE)){
				}
			} else {
				if(permissions[i].equals(Manifest.permission.WRITE_EXTERNAL_STORAGE)) {
					msg+="The SDCARD read-write permission could not be obtained";
				}
			}
		}
		if(msg!=null&&msg.length()>0){
			Toast.makeText(this,msg ,Toast.LENGTH_LONG).show() ;
		}

		handleIntent();
	}
}
