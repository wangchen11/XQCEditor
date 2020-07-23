package person.wangchen11.guirunner;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.util.Log;

public class State {
	public static int VersionCodePro = 1;
	public static int VersionCodeNow = 1;
	public static String VersionNamePro = null;
	public static String VersionNameNow = null;
	private static final String ConfigName="State";
	private static final String TAG="State";
	public static void init(Context context)
	{
		SharedPreferences sharedPreferences=context.getSharedPreferences(ConfigName, Context.MODE_PRIVATE);
		VersionCodePro=sharedPreferences.getInt("VersionCodePro", 0);
		VersionNamePro=sharedPreferences.getString("VersionNamePro", null);
		PackageManager packageManager=context.getPackageManager();
		try {
			PackageInfo packageInfo=packageManager.getPackageInfo(context.getPackageName(), 0);
			VersionCodeNow=packageInfo.versionCode;
			VersionNameNow=packageInfo.versionName;
		} catch (NameNotFoundException e) {
			e.printStackTrace();
		}
		Log.i(TAG, "VersionCodePro:"+VersionCodePro);
		Log.i(TAG, "VersionCodeNow:"+VersionCodeNow);
		Log.i(TAG, "VersionNamePro:"+VersionNamePro);
		Log.i(TAG, "VersionNameNow:"+VersionNameNow);
	}
	
	/**
	 * @return
	 */
	public static boolean isUpdated()
	{
		if(VersionCodePro!=VersionCodeNow)
			return true;
		if(VersionNameNow==null)
			return true;
		if(!VersionNameNow.equals(VersionNamePro))
			return true;
		return false;
	}
	
	public static void save(Context context)
	{
		SharedPreferences sharedPreferences = context.getSharedPreferences(ConfigName, Context.MODE_PRIVATE);
		Editor editor=sharedPreferences.edit();
		editor.putInt("VersionCodePro", VersionCodeNow);
		editor.putString("VersionNamePro", VersionNameNow);
		editor.commit();
	}
	
}
