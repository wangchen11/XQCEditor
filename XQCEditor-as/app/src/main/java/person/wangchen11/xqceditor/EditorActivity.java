package person.wangchen11.xqceditor;
/*
 * 开放源代码声明：
 * 本软件源码版权归望尘11所有。
 * 如需用于商业用途请联系原作者：望尘11
 * q:1012371864
 * 如果个人开发者修改或添加功能需遵守以下协议：
 * (1):不修改广告的渠道号和id。
 * (2):不删除和修改广告功能。
 * (3):不擅自发布版本到任何apk市场，如需发布请联系作者征求其同意。
 * [wangchen11 software]
 */

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

import person.wangchen11.busybox.Busybox;
import person.wangchen11.drawable.CircleDrawable;
import person.wangchen11.filebrowser.FileBowserFragment;
import person.wangchen11.gnuccompiler.GNUCCompiler;
import person.wangchen11.plugins.PluginsManager;
import person.wangchen11.util.ToastUtil;
import person.wangchen11.waps.TencentApi;
import person.wangchen11.waps.Waps;
import person.wangchen11.window.MenuTag;
import person.wangchen11.window.WindowPointer;
import person.wangchen11.window.WindowsManager;
import person.wangchen11.window.ext.CEditor;
import person.wangchen11.window.ext.CheckUpdate;
import person.wangchen11.window.ext.Console;
import person.wangchen11.window.ext.FileBrowser;
import person.wangchen11.window.ext.Setting;
import android.Manifest;
import android.annotation.SuppressLint;
import android.annotation.TargetApi;
import android.app.Activity;
import android.content.ContentResolver;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.res.Configuration;
import android.graphics.Color;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.widget.PopupMenu;
import android.util.Log;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.View.OnClickListener;
import android.view.ViewGroup.LayoutParams;
import android.view.WindowManager;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.Toast;


@TargetApi(Build.VERSION_CODES.HONEYCOMB)
public class EditorActivity extends FragmentActivity implements OnClickListener,WindowsManager.WindowsManagerLintener{
	protected static final String TAG="MainActivity"; 
	private WindowsManager mWindowsManager;
	private LinearLayout mWindowTitleList;
	private PopupMenu mPopupMenu;
	private EmptyFragment mEmptyFragment=new EmptyFragment();
	private Handler mHandler = new Handler();
	private static Activity mBaseActivity = null;
	public static Activity getBaseActivity(){
		return mBaseActivity;
	}
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		try {
			// sleep for LOGO. 
			Thread.sleep(100);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		mBaseActivity = this;
		ToastUtil.init(this);
		Waps.init(this);
		TencentApi.init(this);
		State.init(this);
		PluginsManager.init(this);
		Setting.applyChangeDefault(Setting.loadConfig(this));
		Busybox.freeResourceIfNeed(this);
		GNUCCompiler.freeResourceIfNeed(this);
        mContent=null;
        File workspace = new File(GNUCCompiler.getSystemDir()+File.separatorChar+"workspace"+File.separatorChar);
		FileBowserFragment.mDefaultFile=workspace;
		try {
			new File(GNUCCompiler.getSystemDir()+File.separatorChar+".nomedia").createNewFile();
		} catch (IOException e) {
			e.printStackTrace();
		}
		Console.mDefaultFile=workspace;
		
		if(!FileBowserFragment.mDefaultFile.isDirectory()){
			if(!FileBowserFragment.mDefaultFile.mkdirs()){
				Toast.makeText(this, R.string.create_workspace_failed, Toast.LENGTH_SHORT).show();
			}
		}
		if(savedInstanceState!=null)
		{
			FragmentManager fragmentManager = getSupportFragmentManager();
			List<Fragment> fragments = fragmentManager.getFragments();
			FragmentTransaction transaction = fragmentManager.beginTransaction();
			if(fragments!=null)
			for (int i = 0; i < fragments.size(); i++) {
				transaction.detach(fragments.get(i));
			}
			transaction.commit();
		}
		if(Setting.mConfig.mOtherConfig.mTitleAtHead){
			setContentView(R.layout.activity_editor_main );
		}else{
			setContentView(R.layout.activity_editor_main_ctd );
		}
		transparentStatus();
		mWindowTitleList=(LinearLayout) findViewById(R.id.windows_list);
		configAllView(findViewById(R.id.editor_layout));
		mPopupMenu=new PopupMenu(this, findViewById(R.id.editor_button_more));
		mWindowsManager=new WindowsManager(this) ;
		mPopupMenu.setOnMenuItemClickListener(mWindowsManager);
	    mWindowsManager.addListener(this);
	    mWindowsManager.resumeWindowState();
	    mWindowsManager.clearWindowState();
		FragmentManager fragmentManager = getSupportFragmentManager();//getFragmentManager();
		FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
		
		if(fragmentTransaction.isEmpty()&&mWindowsManager.size()==0)
		{
			mWindowsManager.addWindow(new FileBrowser(mWindowsManager));
		}

		openNewIntent(getIntent());
		
		fragmentTransaction.commit();
		onContentChanged();
		if(Setting.mConfig.mEditorConfig.mAutoUpdate)
			new CheckUpdate(this,true).checkForUpdate();
		requestNeedPermissions();
	}
	
	@Override
	protected void onNewIntent(final Intent intent) {
		mHandler.postDelayed(new Runnable() {
			@Override
			public void run() {
				openNewIntent(intent);
			}
		},300);
	}
	
	private void openNewIntent(Intent intent){
		Log.i(TAG, "openNewIntent:"+intent);
		String action = intent.getAction();
		if(Intent.ACTION_VIEW.equals(action)){
			Uri uri = intent.getData();
			String file = uri.getPath();
			if(ContentResolver.SCHEME_CONTENT.equals(uri.getScheme())){
				if(!new File(file).exists()){
					String externalFilesKey = "/external_files/";
					if(file.startsWith(externalFilesKey)){
						file = "/"+file.substring(externalFilesKey.length());
						if(!new File(file).exists()){
							file = Environment.getExternalStorageDirectory().getAbsolutePath()+"/"+file;
						}
					}
				}
			}
			mWindowsManager.addWindow(new CEditor(mWindowsManager, new File(file)));
		}
	}
	
	@SuppressLint("InlinedApi")
	private void transparentStatus()
	{
		if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
			//透明状态栏
			getWindow().addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
		}
	}
	
	private void configAllView(View view){
		if(view instanceof ViewGroup )
		{
			int count = ((ViewGroup)(view)).getChildCount();
			for(int i=0;i<count;i++){
				configAllView(((ViewGroup)(view)).getChildAt(i));
			}
		}
		
		if( view instanceof ImageButton ){
			view.setOnClickListener(this);
			view.setOnTouchListener(new View.OnTouchListener() {
				@SuppressWarnings("deprecation")
				@SuppressLint("ClickableViewAccessibility")
				@Override
				public boolean onTouch(View v, MotionEvent event) {
					switch(event.getAction() ){
					case MotionEvent.ACTION_DOWN :
						v.setBackgroundDrawable(new CircleDrawable(Color.rgb(0x80, 0x80, 0xb0)));
						break;
					case MotionEvent.ACTION_UP :
					case MotionEvent.ACTION_OUTSIDE :
					case MotionEvent.ACTION_CANCEL :
						v.setBackgroundColor(Color.TRANSPARENT);
						break;
					}
					return false;
				}
			});
		}
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.editor_button_more:
			showPopMenu();
			break;
		default:
			break;
		}
	}
	
	private void showPopMenu(){
		Menu menu=mPopupMenu.getMenu();
		menu.clear();
		List<MenuTag> menuTags=mWindowsManager.getMenuTags();
		for(int i=0;i<menuTags.size();i++){
			menu.add(0, menuTags.get(i).mId , 0, menuTags.get(i).mTitle );
		}
		mPopupMenu.show();
	}
	
	public void changeFragment(Fragment fragment)
	{
		FragmentManager fragmentManager = getSupportFragmentManager();//getFragmentManager();
		FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
		fragmentTransaction.replace(R.id.window, fragment);
		fragmentTransaction.commit();
		mContent=fragment;
	}
	
	private Fragment mContent=null;
	public void switchContent(final Fragment to) {
		if(mContent!=to)
		{
			FragmentManager fragmentManager = getSupportFragmentManager();
			FragmentTransaction transaction = fragmentManager.beginTransaction();
			if(Setting.mConfig.mOtherConfig.mAnimation){
				transaction = transaction.setCustomAnimations(R.anim.scale_big_trans, R.anim.scale_small_trans); 
			}
			if (!to.isAdded()) {    
				if(mContent!=null)
					transaction.hide(mContent);
				transaction.add(R.id.window, to).commit(); 
            }
			else
			{  
				if(mContent!=null)
					transaction.hide(mContent);
                transaction.show(to).commit(); 
            }
            mContent = to;  
		}
		if(to!=null)
		{
			mHandler.post(new Runnable() {
				@Override
				public void run() {
					View view = to.getView();
					if(view !=null)
						Setting.applySettingConfigToAllView(view);
				}
			});
		}
    }

	@Override
	public void onChangeWindow(WindowsManager manager) {
		WindowPointer pointer=manager.getSelectWindow();
		if(pointer==null)
		{
			switchContent(mEmptyFragment);
		}
		else
		{
			switchContent(pointer.mWindow.getFragment());
		}
	}

	@Override
	public void onAddWindow(WindowsManager manager,WindowPointer pointer) {
		mWindowTitleList.removeAllViews();
		mWindowTitleList.addView(manager.getTitleListView(),LayoutParams.MATCH_PARENT,LayoutParams.MATCH_PARENT);
	}

	@Override
	public void onCloseWindow(WindowsManager manager,WindowPointer pointer) {
		FragmentManager fragmentManager = getSupportFragmentManager();
		FragmentTransaction transaction = fragmentManager.beginTransaction();
		if(Setting.mConfig.mOtherConfig.mAnimation){
			transaction = transaction.setCustomAnimations(R.anim.scale_big_trans, R.anim.scale_small_trans); 
		}
		transaction.detach(pointer.mWindow.getFragment());
		transaction.commit();
	}
	
	private long mPreBackTime=0;
	Toast mToast;
	@Override
	public void onBackPressed() {
		if(!mWindowsManager.onBackPressed())
		{
			long time=System.currentTimeMillis();
			if(time-mPreBackTime>800)
			{
				if(mToast!=null)
				{
					mToast.cancel();
				}
				mToast=Toast.makeText(this, R.string.press_back_more_to_exit, Toast.LENGTH_SHORT);
				mToast.show();
			}else{
				if(mWindowsManager.closeAllWindow())
				{
					super.onBackPressed();
					android.os.Process.killProcess(android.os.Process.myPid());
				}
			}
			mPreBackTime=time;
		}
		else{
			
		}
	}
	
	@Override
	public void onConfigurationChanged(Configuration newConfig) {
		super.onConfigurationChanged(newConfig);
	}
	
	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if(keyCode == KeyEvent.KEYCODE_MENU)
		{
			showPopMenu();
			return true;
		}
		return super.onKeyDown(keyCode, event);
	}
	
	@Override
	public void onContentChanged() {
		Log.i(TAG, "onContentChanged");
		findViewById(R.id.titleBar).setBackgroundColor(Setting.mConfig.mOtherConfig.mTitleBarColor);
		findViewById(R.id.base_ground).setBackgroundColor(Setting.mConfig.mOtherConfig.mTitleBarColor);
		findViewById(R.id.editor_layout).setBackgroundColor(Setting.mConfig.mEditorConfig.mBackGroundColor);
		Setting.applySettingConfigToAllView(findViewById(R.id.editor_layout));
		Setting.applySettingConfigToActivity(this);
		super.onContentChanged();
	}
	
	@Override
	public boolean onTouchEvent(MotionEvent event) {
		return super.onTouchEvent(event);
	}
	
	@Override
	protected void onSaveInstanceState(Bundle outState) {
		mWindowsManager.saveWindowState();
	}
	
	@Override
	protected void onRestoreInstanceState(Bundle savedInstanceState) {
		//do nothing
		//super.onRestoreInstanceState(savedInstanceState);
	}
	

	@SuppressLint("NewApi")
	private void requestNeedPermissions() {
		if(Build.VERSION.SDK_INT>=Build.VERSION_CODES.M) {
			List<String > needPremissions = new LinkedList<String>();
			needPremissions.add(Manifest.permission.WRITE_EXTERNAL_STORAGE);
			if(!Waps.isGoogle()){
				needPremissions.add(Manifest.permission.READ_PHONE_STATE);
			}
			
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
			}
		}
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
					GNUCCompiler.freeResourceIfNeed(this);
				}
			} else {
				if(permissions[i].equals(Manifest.permission.READ_PHONE_STATE)) {
					msg+=getString(R.string.can_not_read_imei);
				}
				if(permissions[i].equals(Manifest.permission.WRITE_EXTERNAL_STORAGE)) {
					msg+=getString(R.string.can_not_read_sdcard);
				}
			}
		}
		if(msg!=null&&msg.length()>0){
			msg+=getString(R.string.please_authorize);
			Toast.makeText(this,msg ,Toast.LENGTH_LONG).show() ;
		}
	}
}
