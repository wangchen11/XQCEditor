package person.wangchen11.consolerunner2;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.widget.Toast;

import java.io.File;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

import person.wangchen11.util.FileUtil;

public class LogoActivity extends Activity {
    private static final String TAG = LogoActivity.class.getSimpleName();
    private Handler mHandler = new Handler();
	private static final String APP_INFO_PREFERENCES_NAME = "app_info";
	private static final String APP_INFO_LAST_UPDATE_TIME = "last_update_time";

    private SharedPreferences mAppInfoSharedPreferences = null;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		mAppInfoSharedPreferences = getSharedPreferences(APP_INFO_PREFERENCES_NAME, Context.MODE_PRIVATE);
		if(isAppUpdate()){
			final ProgressDialog progressDialog = new ProgressDialog(this);
			progressDialog.setTitle("Releasing resources");
			progressDialog.show();
			new Thread(new Runnable() {
				@Override
				public void run() {
					releaseAssets();
					writeAppUpdateComplete();
					mHandler.post(mRequestPermissionRunnable);
				}
			}).start();
		} else {
			mHandler.post(mRequestPermissionRunnable);
		}
	}

	private void handleIntent(){
		Bundle bundle = getIntent().getExtras();
		Intent intent = new Intent(this,MainActivity.class);
		startActivity(intent);
		overridePendingTransition(0,0);
		finish();
	}

	private boolean isAppUpdate(){
		long appLastUpdateTime = getAppLastUpdateTime();
		long savedLastUpdateTime = getSavedLastUpdateTime();
		if(appLastUpdateTime == 0){
			return true;
		}
		return appLastUpdateTime!=savedLastUpdateTime;
	}

	private void releaseAssets(){
		FileUtil.freeZip(getPackageCodePath(), getFilesDir().getAbsolutePath(),
				new FileUtil.FreeZipFilter() {
					@Override
					public boolean needFree(String entryName, File to) {
						if(entryName.startsWith("assets")){
							return true;
						}
						return false;
					}
				} );
	}

	private void writeAppUpdateComplete(){
		mAppInfoSharedPreferences.edit()
				.putLong(APP_INFO_LAST_UPDATE_TIME,getAppLastUpdateTime())
				.apply();
	}

	private long getAppLastUpdateTime(){
		try {
			PackageInfo packageInfo = getPackageManager().getPackageInfo(getPackageName(),0);
			if(packageInfo!=null) return packageInfo.lastUpdateTime;
		} catch (PackageManager.NameNotFoundException e) {
			e.printStackTrace();
		}
		return 0;
	}

	private long getSavedLastUpdateTime(){
		return mAppInfoSharedPreferences.getLong(APP_INFO_LAST_UPDATE_TIME,0);
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
			if(grantResults[i]== PackageManager.PERMISSION_GRANTED) {
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

	private Runnable mRequestPermissionRunnable = new Runnable() {
		@Override
		public void run() {
			if(requestNeedPermissions()){
				handleIntent();
			}
		}
	};
}
