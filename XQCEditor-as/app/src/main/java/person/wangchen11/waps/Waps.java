package person.wangchen11.waps;

import java.util.Date;

import cn.waps.AppConnect;
import cn.waps.UpdatePointsListener;
import android.content.Context;
import android.view.ViewGroup;
import android.widget.LinearLayout;

@SuppressWarnings("deprecation")
public class Waps {
	static final String TAG="Waps";
	public static String APP_ID="f29592b5daa7915e1048e659e7e930cf";
	public static String APP_PID="qq";
	private static boolean mInited = false;
	//private static String APP_PID="google";
	
	private static Date mDurTime;
	static{
		int year=2017;
		int month=6;
		int day=1;
		int hour=0;
		int minute=0;
		int second=0;
		mDurTime=new Date(year-1900, month-1, day, hour, minute, second);
	}
	
	public static void init(Context context)
	{
		try {
			try {
				//AppConnect.getInstance(context);
				AppConnect.getInstance(APP_ID, APP_PID, context);
				AppConnect.getInstance(context).initAdInfo();
				AppConnect.getInstance(context).setCrashReport(true);
				mInited = true;
			} catch (Error e) {
				e.printStackTrace();
			}
			
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	public static void showPop(Context context)
	{
		if(!isTimeToShow())
			return ;
		AppConnect.getInstance(context).showPopAd(context);
		//AppConnect.getInstance(context).setPopAdBack(true);
	}
	
	public static void showBanner(Context context,LinearLayout linearLayout)
	{
		if(!isTimeToShow())
			return ;
		if(Key.hasRealKey(context))
			return ;
		try {
			try {
				AdRelativeLayoutWithClose adRelativeLayoutWithClose = new AdRelativeLayoutWithClose(context);
				AdLinearLayout adLinearLayout=new AdLinearLayout(context);
				adRelativeLayoutWithClose.addView(adLinearLayout);
				adRelativeLayoutWithClose.addCloseButton();
				linearLayout.addView(adRelativeLayoutWithClose,ViewGroup.LayoutParams.MATCH_PARENT,ViewGroup.LayoutParams.MATCH_PARENT);
				AppConnect.getInstance(context).showBannerAd(context, adLinearLayout);
			} catch (Error e) {
				e.printStackTrace();
			}
			
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	public static boolean isGoogle(){
		return APP_PID.equals("google");
	}
	
	public static boolean isTimeToShow()
	{
		// never show advertisement
		if(true)
			return false;
		if(!mInited)
			return false;
		
		if(isGoogle())
			return false;
		
		Date timeNow=new Date();
		if(timeNow.after(mDurTime))
			return true;
		return false;
	}

	public static void showOffers(Context context)
	{
		if(!isTimeToShow())
			return ;
		try{
			AppConnect.getInstance(context).showAppOffers(context);
		}catch(Error e) {
			e.printStackTrace();
		}catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	private static AdListener mAdListener = null;
	public static void updatePoints(Context context,AdListener listener)
	{
		if(!isTimeToShow())
			return ;
		mAdListener = listener;
		try{
			AppConnect.getInstance(context).getPoints(new UpdatePointsListener() {
				
				@Override
				public void getUpdatePointsFailed(String arg0) {
					if(mAdListener!=null)
						mAdListener.getUpdatePointsFailed(arg0);
				}
				
				@Override
				public void getUpdatePoints(String arg0, int arg1) {
					if(mAdListener!=null)
						mAdListener.getUpdatePoints(arg0, arg1);
				}
			});
		}catch(Error e) {
			e.printStackTrace();
		}catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	public interface AdListener{
		public void getUpdatePointsFailed(String arg0);
		
		public void getUpdatePoints(String arg0, int arg1);
	}
}
