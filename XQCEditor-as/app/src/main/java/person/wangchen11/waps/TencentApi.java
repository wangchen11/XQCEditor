package person.wangchen11.waps;

import android.content.Context;
import android.util.Log;

import com.tencent.tauth.Tencent;

public class TencentApi {
	private static String APP_ID = "1105556556"; 
	private static Tencent mTencent = null;
	public static void init(Context context){
		mTencent = Tencent.createInstance(APP_ID, context);
	}
	
	public static Tencent getTencent(){
		Log.d("TencentApi",""+mTencent);
		return mTencent;
	}
}
