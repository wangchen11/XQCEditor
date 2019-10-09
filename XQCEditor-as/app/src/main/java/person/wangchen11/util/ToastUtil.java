package person.wangchen11.util;

import android.content.Context;
import android.widget.Toast;

public class ToastUtil {
	private static Context mContext = null;
	private static Toast mToast = null;
	public static void init(Context context){
		mContext = context;
	}
	
	public static void showToast(String text,int duration)
	{
		if(mToast!=null)
			mToast.cancel();
		mToast = Toast.makeText(mContext, text, duration);
		mToast.show();
	}
	
	public static void showToast(int textId,int duration)
	{
		if(mToast!=null)
			mToast.cancel();
		mToast = Toast.makeText(mContext, textId, duration);
		mToast.show();
	}
}
