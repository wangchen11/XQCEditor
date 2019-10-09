package person.wangchen11.phpconfig;

import person.wangchen11.xqceditor.R;
import android.annotation.SuppressLint;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.ScrollView;
import android.widget.TextView;

public class PHPConfigFragment extends Fragment implements OnClickListener{
	public static final String mServicePackage="person.wangchen11.phpserver";
	public static final String mServiceClass="person.wangchen11.phpserver.PHPService";
	private ComponentName mComponentName=new ComponentName(mServicePackage,mServiceClass);
	private TextView mStateView;
	private TextView mMsgView;
	private ProgressBar mProgressBar;
	private PHPConfig mPhpConfig=null;
	private ScrollView mScrollView=null;
	private EditText mEditHttpPort=null;
	private EditText mEditMysqlPort=null;
	private EditText mEditWwwPath=null;
	private EditText mEditDataBasePath=null;
	private TextView mTextNoPhp=null;
	private SignalLight mSignalLight=null;
	private BroadcastReceiver mBroadcastReceiver=null;
	@SuppressLint("InflateParams")
	@Override
	public View onCreateView(LayoutInflater inflater,
			@Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
		RelativeLayout relativeLayout=(RelativeLayout) inflater.inflate(R.layout.fragment_php_config, null);
		relativeLayout.findViewById(R.id.start_service).setOnClickListener(this);
		relativeLayout.findViewById(R.id.stop_service).setOnClickListener(this);
		relativeLayout.findViewById(R.id.button_restore).setOnClickListener(this);
		relativeLayout.findViewById(R.id.browser).setOnClickListener(this);
		mStateView=(TextView) relativeLayout.findViewById(R.id.state);
		mMsgView=(TextView) relativeLayout.findViewById(R.id.msg);
		mScrollView=(ScrollView) relativeLayout.findViewById(R.id.scrollView1);
		mProgressBar=(ProgressBar)relativeLayout.findViewById(R.id.progressBar1);
		mEditHttpPort=(EditText)relativeLayout.findViewById(R.id.edit_http_port);
		mEditMysqlPort=(EditText)relativeLayout.findViewById(R.id.edit_mysql_port);
		mEditWwwPath=(EditText)relativeLayout.findViewById(R.id.edit_www_path);
		mProgressBar.setVisibility(View.INVISIBLE);
		mEditDataBasePath=(EditText)relativeLayout.findViewById(R.id.edit_data_base_path);
		mTextNoPhp=(TextView) relativeLayout.findViewById(R.id.text_no_php);
		mTextNoPhp.setVisibility(View.GONE);
		mSignalLight=(SignalLight) relativeLayout.findViewById(R.id.signal_light);
		if(hasPhpApk())
		{
			mTextNoPhp.setVisibility(View.GONE);
		}
		else
		{
			mTextNoPhp.setVisibility(View.VISIBLE);
		}
		mPhpConfig=PHPConfig.load(getActivity());
		startService();
		IntentFilter intentFilter=new IntentFilter();
		intentFilter.addAction(PHPConfig.PHP_SERVER_ACTION_STATE_CHANGE);
		intentFilter.addAction(PHPConfig.PHP_SERVER_ACTION_MSG);
		getActivity().registerReceiver(mBroadcastReceiver=new BroadcastReceiver() {
			@Override
			public void onReceive(Context context, Intent intent) {
				String action=intent.getAction();
				if(PHPConfig.PHP_SERVER_ACTION_STATE_CHANGE.equals(action))
				{
					String state=intent.getStringExtra(PHPConfig.PHP_SERVER_STATE);
					if(state!=null)
					{
						mStateView.setText(""+PHPConfig.getTipByState(context, state));
						if(state.endsWith(PHPConfig.PHP_SERVER_STATE_CLOSING)||state.endsWith(PHPConfig.PHP_SERVER_STATE_OPENING))
							mProgressBar.setVisibility(View.VISIBLE);
						else
							mProgressBar.setVisibility(View.INVISIBLE);
						if(state.endsWith(PHPConfig.PHP_SERVER_STATE_OPENED))
						{
							mSignalLight.setColor(Color.rgb(0x80, 0xff, 0x90));
						} else
						if(state.endsWith(PHPConfig.PHP_SERVER_STATE_OPEN_FAIL))
						{
							mSignalLight.setColor(Color.rgb(0xff, 0x80, 0x90));
						} else
						{
							mSignalLight.setColor(Color.rgb(0xff, 0xff, 0x90));
						}
					}
				}else
				if(PHPConfig.PHP_SERVER_ACTION_MSG.equals(action))
				{
					String text=mMsgView.getText()+intent.getStringExtra(PHPConfig.PHP_SERVER_MSG);
					if(text.length()>1024*10)
						text=text.substring(text.length()-1024*10);
					mMsgView.setText(text);
					mScrollView.post(new Runnable() {
						@Override
						public void run() {
							mScrollView.fullScroll(View.FOCUS_DOWN);
						}
					});
				}
			}
		}, intentFilter);
		applyConfigToView();
		return relativeLayout;
	}
	
	private void applyConfigToView()
	{
		mEditHttpPort.setText(""+mPhpConfig.HTTPD_PORT);
		mEditMysqlPort.setText(""+mPhpConfig.MYSQL_PORT);
		mEditWwwPath.setText(""+mPhpConfig.HTTPD_DOC_ROOT);
		mEditDataBasePath.setText(""+mPhpConfig.MYSQL_DATEBASE_DIR);
	}
	
	private void loadConfigFromView()
	{
		try {
			mPhpConfig.HTTPD_PORT=Integer.parseInt(""+mEditHttpPort.getText());
		} catch (Exception e) {
			e.printStackTrace();
		}
		try {
			mPhpConfig.MYSQL_PORT=Integer.parseInt(""+mEditMysqlPort.getText());
		} catch (Exception e) {
			e.printStackTrace();
		}
		mPhpConfig.HTTPD_DOC_ROOT=""+mEditWwwPath.getText();
		mPhpConfig.MYSQL_DATEBASE_DIR=""+mEditDataBasePath.getText();
		mPhpConfig.save(getActivity());
	}
	
	public void startService()
	{
		Intent intent=new Intent();
		intent.setComponent(mComponentName); 
		intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
		this.getActivity().startService(intent);
	}
	
	public void stopService()
	{
		Intent intent=new Intent();
		intent.setComponent(mComponentName);
		intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
		this.getActivity().stopService(intent);
	}

	@Override
	public void onClick(View v) {
		Intent intent;
		switch (v.getId()) {
		case R.id.start_service:
			loadConfigFromView();
			intent=new Intent(PHPConfig.PHP_SERVER_ACTION_START_SERVER);
			mPhpConfig.putToIntent(intent);
			getActivity().sendBroadcast(intent);
			break;
		case R.id.stop_service:
			loadConfigFromView();
			intent=new Intent(PHPConfig.PHP_SERVER_ACTION_STOP_SERVER);
			getActivity().sendBroadcast(intent);
			break;
		case R.id.button_restore:
			mPhpConfig=new PHPConfig();
			applyConfigToView();
			break;
		case R.id.browser:
			openWithBrowser();
			break;
		default:
			break;
		}
	}
	
	public void openWithBrowser()
	{
		Uri uri = Uri.parse("http://127.0.0.1:"+mPhpConfig.HTTPD_PORT);  
		Intent it = new Intent(Intent.ACTION_VIEW, uri);  
		try {
			getActivity().startActivity(it);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	@Override
	public void onDestroy() {
		getActivity().unregisterReceiver(mBroadcastReceiver);
		super.onDestroy();
	}
	
	private boolean hasPhpApk()
	{
        PackageManager packageManager = getActivity().getPackageManager();
        try {
			PackageInfo info=packageManager.getPackageInfo(mServicePackage, 0);
			if(info!=null&&info.versionName!=null)
			{
				return true;
			}
		} catch (NameNotFoundException e) {
			e.printStackTrace();
		}
        return false;
	}
	
}
