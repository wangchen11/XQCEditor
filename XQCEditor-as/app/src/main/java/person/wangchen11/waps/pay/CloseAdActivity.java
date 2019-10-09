package person.wangchen11.waps.pay;

import java.io.InputStream;
import java.net.URL;
import java.util.Scanner;

import com.wanpu.pay.PayConnect;
import com.wanpu.pay.PayResultListener;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;
import person.wangchen11.tools.ThreadPool;
import person.wangchen11.util.ToastUtil;
import person.wangchen11.waps.Key;
import person.wangchen11.waps.Waps;
import person.wangchen11.window.ext.Setting;
import person.wangchen11.xqceditor.R;

public class CloseAdActivity extends Activity implements OnClickListener {
	private Handler mHandler = new Handler();
	private static final String mNotifyUrl = "http://activate.wangchen11.top:8080/Activate/activate";
	private static final String mGetBackUrl = "http://activate.wangchen11.top:8080/Activate/isactivated";
	private String mUserId = "";
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		mUserId = "qeditor_"+Key.getIMEI(this);
		PayConnect.getInstance(Waps.APP_ID, Waps.APP_PID, this);
		setContentView(R.layout.activity_colse_ad);
		((Button)(findViewById(R.id.button_show_ad))).setOnClickListener(this);
		((Button)(findViewById(R.id.button_close_ad_by_points))).setOnClickListener(this);
		((Button)(findViewById(R.id.button_pay))).setOnClickListener(this);
		((Button)(findViewById(R.id.button_get_back))).setOnClickListener(this);
		Waps.updatePoints(this, mAdListener);
		findViewById(R.id.layout_base).setBackgroundColor(Setting.mConfig.mEditorConfig.mBackGroundColor);
		Setting.applySettingConfigToAllView(findViewById(R.id.layout_base));
		Setting.applySettingConfigToActivity(this);
		refresh();
		super.onCreate(savedInstanceState);
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.button_show_ad:
			Waps.showOffers(this);
			Waps.updatePoints(this, mAdListener);
			break;

		case R.id.button_close_ad_by_points:
			if(Key.hasRealKey(this))
			{
				showToast( R.string.already_closed_ad);
			}
			else
			{
				if(mPoints<200)
				{
					showToast(R.string.need_200_points);
				}
				else
				{
					if(Key.createKey(this))
					{
						showToast(R.string.close_ad_success);
					}
					else
					{
						showToast(R.string.close_ad_failed);
					}
				}
			}
			Waps.updatePoints(this, mAdListener);
			break;

		case R.id.button_pay:
		{
			String userId = getUserId();
			String orderId = userId+"_0000";
			float price = 8.8f;
			String goodsName = "qeditor";
			String goodsDesc = "C/C++ IDE.";
			PayConnect.getInstance(this).pay(this, 
					orderId,
					userId,
					price,
					goodsName,
					goodsDesc,
					mNotifyUrl,
					new MyPayResultListener(orderId));
		}
			break;

		case R.id.button_get_back:
			ThreadPool.instance().execute(new GetBack());
			break;
		default:
			break;
		}
	}

	public void showToast(String string) {
		ToastUtil.showToast(string, Toast.LENGTH_SHORT);
	}
	
	public void showToast(int strId) {
		ToastUtil.showToast(strId, Toast.LENGTH_SHORT);
	}
	
	private void refresh() {
		TextView textViewMyId = findViewById(R.id.text_my_id);
		TextView textViewMyPoints = findViewById(R.id.text_my_points);
		textViewMyId.setText(Key.getIMEI(this));
		textViewMyPoints.setText(""+mPoints);
	}

	private String getUserId() {
		return mUserId;
	}

	@Override
	protected void onDestroy() {
		PayConnect.getInstance(this).close();
		super.onDestroy();
	}
	
	private static int mPoints=0;
	private Waps.AdListener mAdListener = new Waps.AdListener() {
		@Override
		public void getUpdatePointsFailed(String arg0) {
		}
		
		@Override
		public void getUpdatePoints(String arg0, int arg1) {
			mPoints = arg1;
			mHandler.post(new Runnable() {
				@Override
				public void run() {
					refresh();
				}
			});
		}
	};
	
	private class MyPayResultListener implements PayResultListener {
		private String mOrderId;
		
		public MyPayResultListener(String orderId) {
			mOrderId = orderId;
		}
		
		@Override
		public void onPayFinish(Context payViewContext,String order_id,int resultCode, String resultString, int payType, float amount, String goods_name) {
			if(mOrderId.equals(order_id)) {
				if(resultCode==0) {
					// pay success 
					Key.createKey(CloseAdActivity.this);
					showToast(R.string.pay_success);
					PayConnect.getInstance(CloseAdActivity.this).closePayView(payViewContext);
				} else {
					showToast(R.string.pay_failed);
				}
			} else {
				PayConnect.getInstance(CloseAdActivity.this).confirm(order_id, payType);
			}
		}
	};
	
	private Runnable mGetBackSuccessRunnable = new Runnable() {
		@Override
		public void run() {
			Key.createKey(CloseAdActivity.this);
			showToast(R.string.get_back_success);
		}
	};
	
	private Runnable mGetBackFailedRunnable = new Runnable() {
		@Override
		public void run() {
			showToast(R.string.get_back_failed);
		}
	};
	
	class GetBack implements Runnable {
		public void run() {
			String result = "";
			try {
				URL url = new URL(mGetBackUrl+"?"+"user_id="+getUserId());
				InputStream in =url.openStream();
				Scanner scanner = new Scanner(in);
				result = scanner.next().trim();
				scanner.close();
			} catch (Exception e) {
				e.printStackTrace();
			}
			if(result.equals("true")) {
				mHandler.post(mGetBackSuccessRunnable);
			} else {
				mHandler.post(mGetBackFailedRunnable);
			}
		}
	}
}
