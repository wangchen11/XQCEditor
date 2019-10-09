package person.wangchen11.nativeview;

//by wangchen11

import android.app.Activity;
import android.os.Bundle;
import android.widget.LinearLayout;

public class MainActivity extends Activity {
	NativeView   mNativeView;
	LinearLayout mLinearLayout;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		mNativeView=new NativeView(this);
		mLinearLayout=new LinearLayout(this);
		mLinearLayout.setOrientation(LinearLayout.VERTICAL);
		LinearLayout layout=new LinearLayout(this);
		mLinearLayout.addView(layout);
		mLinearLayout.addView(mNativeView);
		setContentView(mLinearLayout);
		NativeInterface.initActivity(this);
	}
	
	@Override
	protected void onPause() {
		NativeInterface.onPause();
		super.onPause();
	}
	
	@Override
	protected void onResume() {
		NativeInterface.onResume();
		super.onResume();
	}
	
	@Override
	protected void onDestroy() {
		NativeInterface.destroy();
		super.onDestroy();
	}
	
	@Override
	public void onBackPressed() {
		if(NativeInterface.backPressed())
		{
			return ;
		}
		super.onBackPressed();
	}

}
