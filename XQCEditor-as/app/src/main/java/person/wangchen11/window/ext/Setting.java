package person.wangchen11.window.ext;

import jackpal.androidterm.TermView;
import jackpal.androidterm.emulatorview.ColorScheme;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import person.wangchen11.editor.codeedittext.CodeStyleAdapter;
import person.wangchen11.editor.edittext.EditableWithLayout;
import person.wangchen11.editor.edittext.MyEditText;
import person.wangchen11.filebrowser.FileWork;
import person.wangchen11.gnuccompiler.GNUCCompiler;
import person.wangchen11.qeditor.EditorFregment;
import person.wangchen11.util.ToastUtil;
import person.wangchen11.waps.Waps;
import person.wangchen11.window.MenuTag;
import person.wangchen11.window.TitleView;
import person.wangchen11.window.Window;
import person.wangchen11.window.WindowsManager;
import person.wangchen11.xqceditor.PrivacyPolicy;
import person.wangchen11.xqceditor.R;
import person.wangchen11.xqceditor.State;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.AlertDialog.Builder;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.Typeface;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.SwitchCompat;
import android.text.Editable;
import android.text.TextPaint;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

@SuppressLint("ValidFragment")
public class Setting extends Fragment implements Window, TextWatcher, OnClickListener, OnCheckedChangeListener {
	public static final String ConfigName="ceditor_config";
	RelativeLayout mRelativeLayout;
	public static Config mConfig = new Config();
	private WindowsManager mWindowsManager;
	private PrivacyPolicy mPrivacyPolicy;
	public Setting(WindowsManager windowsManager)
	{
		mWindowsManager = windowsManager;
	}
	
	@Override
	public void onHiddenChanged(boolean hidden) {
		super.onHiddenChanged(hidden);
		if(!hidden){
			refEditView();
			refColorView();
			refSwitchView();
		}
	}
	
	@SuppressLint("InflateParams")
	@Override
	public View onCreateView(LayoutInflater inflater,
			@Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
		mRelativeLayout=(RelativeLayout) inflater.inflate(R.layout.fragment_setting, null);
		if(Waps.isGoogle()){
			mRelativeLayout.findViewById(R.id.layout_update).setVisibility(View.GONE);
		}
		if(!Waps.isTimeToShow()){
			mRelativeLayout.findViewById(R.id.layout_ad).setVisibility(View.GONE);
		}
		mConfig=loadConfig(getActivity());
		mPrivacyPolicy = new PrivacyPolicy(getActivity());
		refEditView();
		refColorView();
		refSwitchView();
		((EditText)(mRelativeLayout.findViewById(R.id.et_bk_col))).addTextChangedListener(this);
		((EditText)(mRelativeLayout.findViewById(R.id.et_bk_col))).addTextChangedListener(this);
		((EditText)(mRelativeLayout.findViewById(R.id.et_ft_col))).addTextChangedListener(this);
		((EditText)(mRelativeLayout.findViewById(R.id.et_ft_scl))).addTextChangedListener(this);
		((EditText)(mRelativeLayout.findViewById(R.id.et_lh_scl))).addTextChangedListener(this);
		
		((EditText)(mRelativeLayout.findViewById(R.id.et_kw_col))).addTextChangedListener(this);
		((EditText)(mRelativeLayout.findViewById(R.id.et_pk_col))).addTextChangedListener(this);
		((EditText)(mRelativeLayout.findViewById(R.id.et_wd_col))).addTextChangedListener(this);
		((EditText)(mRelativeLayout.findViewById(R.id.et_cs_col))).addTextChangedListener(this);
		((EditText)(mRelativeLayout.findViewById(R.id.et_cm_col))).addTextChangedListener(this);
		
		((EditText)(mRelativeLayout.findViewById(R.id.et_thread_number))).addTextChangedListener(this);
		((EditText)(mRelativeLayout.findViewById(R.id.et_qk_ipt))).addTextChangedListener(this);
		((EditText)(mRelativeLayout.findViewById(R.id.et_til_bk_col))).addTextChangedListener(this);
		((EditText)(mRelativeLayout.findViewById(R.id.et_til_sl_col))).addTextChangedListener(this);
		((EditText)(mRelativeLayout.findViewById(R.id.et_close_col))).addTextChangedListener(this);

		((Button)(mRelativeLayout.findViewById(R.id.button_close_ad))).setOnClickListener(this);
		(mRelativeLayout.findViewById(R.id.button_close_ad)).setVisibility(Waps.isTimeToShow() ? View.VISIBLE : View.GONE);
		((Button)(mRelativeLayout.findViewById(R.id.button_show_ad))).setOnClickListener(this);
		((Button)(mRelativeLayout.findViewById(R.id.button_ok))).setOnClickListener(this);
		((Button)(mRelativeLayout.findViewById(R.id.button_to_default))).setOnClickListener(this);
		((Button)(mRelativeLayout.findViewById(R.id.button_save_theme))).setOnClickListener(this);
		((SwitchCompat)(mRelativeLayout.findViewById(R.id.high_light_switch))).setOnCheckedChangeListener(this);
		((SwitchCompat)(mRelativeLayout.findViewById(R.id.use_nice_font_switch))).setOnCheckedChangeListener(this);
		((SwitchCompat)(mRelativeLayout.findViewById(R.id.quick_close_window_switch))).setOnCheckedChangeListener(this);
		((SwitchCompat)(mRelativeLayout.findViewById(R.id.use_new_console_switch))).setOnCheckedChangeListener(this);
		((SwitchCompat)(mRelativeLayout.findViewById(R.id.use_new_edittext_switch))).setOnCheckedChangeListener(this);
		((SwitchCompat)(mRelativeLayout.findViewById(R.id.title_at_head))).setOnCheckedChangeListener(this);
		((SwitchCompat)(mRelativeLayout.findViewById(R.id.ctrl_at_head))).setOnCheckedChangeListener(this);
		((SwitchCompat)(mRelativeLayout.findViewById(R.id.animation))).setOnCheckedChangeListener(this);
		((SwitchCompat)(mRelativeLayout.findViewById(R.id.auto_update_switch))).setOnCheckedChangeListener(this);

		((Button)(mRelativeLayout.findViewById(R.id.button_check_update))).setOnClickListener(this);
		((TextView)(mRelativeLayout.findViewById(R.id.text_cur_version))).setText("v"+State.VersionNameNow);
		((TextView)(mRelativeLayout.findViewById(R.id.text_my_id))).setVisibility(View.GONE);
		((TextView)(mRelativeLayout.findViewById(R.id.button_privacy_policy))).setOnClickListener(this);

		if(!mPrivacyPolicy.isRequested()){
			((TextView)(mRelativeLayout.findViewById(R.id.button_privacy_policy))).setVisibility(View.GONE);
		}
		return mRelativeLayout;
	}
	
	public void refEditView(){
		((EditText)(mRelativeLayout.findViewById(R.id.et_bk_col))).setText(String.format("%08x", mConfig.mEditorConfig.mBackGroundColor ));
		((EditText)(mRelativeLayout.findViewById(R.id.et_ft_col))).setText(String.format("%08x", mConfig.mEditorConfig.mBaseFontColor ));
		((EditText)(mRelativeLayout.findViewById(R.id.et_ft_scl))).setText(String.format("%.2f", mConfig.mEditorConfig.mFontScale ));
		((EditText)(mRelativeLayout.findViewById(R.id.et_lh_scl))).setText(String.format("%.2f", mConfig.mEditorConfig.mLineScale ));
		
		((EditText)(mRelativeLayout.findViewById(R.id.et_kw_col))).setText(String.format("%08x", mConfig.mCEditorConfig.mKeywordsColor ));
		((EditText)(mRelativeLayout.findViewById(R.id.et_pk_col))).setText(String.format("%08x", mConfig.mCEditorConfig.mProKeywordsColor ));
		((EditText)(mRelativeLayout.findViewById(R.id.et_wd_col))).setText(String.format("%08x", mConfig.mCEditorConfig.mWordsColor ));
		((EditText)(mRelativeLayout.findViewById(R.id.et_cs_col))).setText(String.format("%08x", mConfig.mCEditorConfig.mConstantColor ));
		((EditText)(mRelativeLayout.findViewById(R.id.et_cm_col))).setText(String.format("%08x", mConfig.mCEditorConfig.mCommentsColor ));
		
		((EditText)(mRelativeLayout.findViewById(R.id.et_thread_number))).setText(""+mConfig.mOtherConfig.mThreadNumber);
		((EditText)(mRelativeLayout.findViewById(R.id.et_qk_ipt))).setText(mConfig.mOtherConfig.mQuickInput);

		((EditText)(mRelativeLayout.findViewById(R.id.et_til_bk_col))).setText(String.format("%08x", mConfig.mOtherConfig.mTitleBarColor ));
		((EditText)(mRelativeLayout.findViewById(R.id.et_til_sl_col))).setText(String.format("%08x", mConfig.mOtherConfig.mSelectTitleColor ));
		((EditText)(mRelativeLayout.findViewById(R.id.et_close_col))).setText(String.format("%08x", mConfig.mOtherConfig.mQuickCloseColor ));
	}
	
	public void loadEditView(){
		EditText editText;
		String str;
		editText=((EditText)(mRelativeLayout.findViewById(R.id.et_bk_col)));
		str=editText.getText().toString();
		mConfig.mEditorConfig.mBackGroundColor=HexStrToIntWithTry(str);
		
		editText=((EditText)(mRelativeLayout.findViewById(R.id.et_ft_col)));
		str=editText.getText().toString();
		mConfig.mEditorConfig.mBaseFontColor=HexStrToIntWithTry(str);
		
		editText=((EditText)(mRelativeLayout.findViewById(R.id.et_ft_scl)));
		str=editText.getText().toString();
		mConfig.mEditorConfig.mFontScale=StrToFloatWithTry(str);
		
		editText=((EditText)(mRelativeLayout.findViewById(R.id.et_lh_scl)));
		str=editText.getText().toString();
		mConfig.mEditorConfig.mLineScale=StrToFloatWithTry(str);
		
		
		editText=((EditText)(mRelativeLayout.findViewById(R.id.et_kw_col)));
		str=editText.getText().toString();
		mConfig.mCEditorConfig.mKeywordsColor=HexStrToIntWithTry(str);
		
		editText=((EditText)(mRelativeLayout.findViewById(R.id.et_pk_col)));
		str=editText.getText().toString();
		mConfig.mCEditorConfig.mProKeywordsColor=HexStrToIntWithTry(str);
		
		editText=((EditText)(mRelativeLayout.findViewById(R.id.et_wd_col)));
		str=editText.getText().toString();
		mConfig.mCEditorConfig.mWordsColor=HexStrToIntWithTry(str);
		
		editText=((EditText)(mRelativeLayout.findViewById(R.id.et_cs_col)));
		str=editText.getText().toString();
		mConfig.mCEditorConfig.mConstantColor=HexStrToIntWithTry(str);
		
		editText=((EditText)(mRelativeLayout.findViewById(R.id.et_cm_col)));
		str=editText.getText().toString();
		mConfig.mCEditorConfig.mCommentsColor=HexStrToIntWithTry(str);
		
		editText=((EditText)(mRelativeLayout.findViewById(R.id.et_thread_number)));
		str=editText.getText().toString();
		mConfig.mOtherConfig.mThreadNumber=StrToIntWithTry(str);
		
		editText=((EditText)(mRelativeLayout.findViewById(R.id.et_qk_ipt)));
		str=editText.getText().toString();
		mConfig.mOtherConfig.mQuickInput=str;
		

		editText=((EditText)(mRelativeLayout.findViewById(R.id.et_til_bk_col)));
		str=editText.getText().toString();
		mConfig.mOtherConfig.mTitleBarColor=HexStrToIntWithTry(str);
		
		editText=((EditText)(mRelativeLayout.findViewById(R.id.et_til_sl_col)));
		str=editText.getText().toString();
		mConfig.mOtherConfig.mSelectTitleColor=HexStrToIntWithTry(str);

		editText=((EditText)(mRelativeLayout.findViewById(R.id.et_close_col)));
		str=editText.getText().toString();
		mConfig.mOtherConfig.mQuickCloseColor=HexStrToIntWithTry(str);
	}
	
	public static int HexStrToIntWithTry(String str){
		try {
			return (int) Long.parseLong(str, 16);
		} catch (Exception e) {
			e.printStackTrace();
			return 0;
		}
	}
	public static int StrToIntWithTry(String str){
		try {
			return (int) Long.parseLong(str, 10);
		} catch (Exception e) {
			e.printStackTrace();
			return 0;
		}
	}

	public static float StrToFloatWithTry(String str){
		try {
			return Float.parseFloat(str);
		} catch (Exception e) {
			e.printStackTrace();
			return 0;
		}
	}
	
	public void refColorView(){
		mRelativeLayout.findViewById(R.id.bk_col).setBackgroundColor(mConfig.mEditorConfig.mBackGroundColor);
		mRelativeLayout.findViewById(R.id.ft_col).setBackgroundColor(mConfig.mEditorConfig.mBaseFontColor);

		mRelativeLayout.findViewById(R.id.kw_col).setBackgroundColor(mConfig.mCEditorConfig.mKeywordsColor);
		mRelativeLayout.findViewById(R.id.pk_col).setBackgroundColor(mConfig.mCEditorConfig.mProKeywordsColor);
		mRelativeLayout.findViewById(R.id.wd_col).setBackgroundColor(mConfig.mCEditorConfig.mWordsColor);
		mRelativeLayout.findViewById(R.id.cs_col).setBackgroundColor(mConfig.mCEditorConfig.mConstantColor);
		mRelativeLayout.findViewById(R.id.cm_col).setBackgroundColor(mConfig.mCEditorConfig.mCommentsColor);
		
		mRelativeLayout.findViewById(R.id.til_bk_col).setBackgroundColor(mConfig.mOtherConfig.mTitleBarColor);
		mRelativeLayout.findViewById(R.id.til_sl_col).setBackgroundColor(mConfig.mOtherConfig.mSelectTitleColor);
		mRelativeLayout.findViewById(R.id.close_col).setBackgroundColor(mConfig.mOtherConfig.mQuickCloseColor);
	}
	
	public void loadSwitchView()
	{
		mConfig.mCEditorConfig.mEnableHighLight=((SwitchCompat)mRelativeLayout.findViewById(R.id.high_light_switch)).isChecked();
		mConfig.mEditorConfig.mUseNiceFont=((SwitchCompat)mRelativeLayout.findViewById(R.id.use_nice_font_switch)).isChecked();
		mConfig.mEditorConfig.mAutoUpdate=((SwitchCompat)mRelativeLayout.findViewById(R.id.auto_update_switch)).isChecked();
		mConfig.mOtherConfig.mQuickCloseEnable=((SwitchCompat)mRelativeLayout.findViewById(R.id.quick_close_window_switch)).isChecked();
		mConfig.mOtherConfig.mNewConsoleEnable=((SwitchCompat)mRelativeLayout.findViewById(R.id.use_new_console_switch)).isChecked();
		mConfig.mOtherConfig.mNewEditorEnable=((SwitchCompat)mRelativeLayout.findViewById(R.id.use_new_edittext_switch)).isChecked();
		mConfig.mOtherConfig.mTitleAtHead=((SwitchCompat)mRelativeLayout.findViewById(R.id.title_at_head)).isChecked();
		mConfig.mOtherConfig.mCtrlAtHead=((SwitchCompat)mRelativeLayout.findViewById(R.id.ctrl_at_head)).isChecked();
		mConfig.mOtherConfig.mAnimation=((SwitchCompat)mRelativeLayout.findViewById(R.id.animation)).isChecked();
		mConfig.mOtherConfig.mAnimation=((SwitchCompat)mRelativeLayout.findViewById(R.id.animation)).isChecked();
	}
	
	public void refSwitchView(){
		((SwitchCompat)mRelativeLayout.findViewById(R.id.high_light_switch)).setChecked(mConfig.mCEditorConfig.mEnableHighLight);
		((SwitchCompat)mRelativeLayout.findViewById(R.id.use_nice_font_switch)).setChecked(mConfig.mEditorConfig.mUseNiceFont);
		((SwitchCompat)mRelativeLayout.findViewById(R.id.auto_update_switch)).setChecked(mConfig.mEditorConfig.mAutoUpdate);
		((SwitchCompat)mRelativeLayout.findViewById(R.id.quick_close_window_switch)).setChecked(mConfig.mOtherConfig.mQuickCloseEnable);
		((SwitchCompat)mRelativeLayout.findViewById(R.id.use_new_console_switch)).setChecked(mConfig.mOtherConfig.mNewConsoleEnable);
		((SwitchCompat)mRelativeLayout.findViewById(R.id.use_new_edittext_switch)).setChecked(mConfig.mOtherConfig.mNewEditorEnable);
		((SwitchCompat)mRelativeLayout.findViewById(R.id.title_at_head)).setChecked(mConfig.mOtherConfig.mTitleAtHead);
		((SwitchCompat)mRelativeLayout.findViewById(R.id.ctrl_at_head)).setChecked(mConfig.mOtherConfig.mCtrlAtHead);
		((SwitchCompat)mRelativeLayout.findViewById(R.id.animation)).setChecked(mConfig.mOtherConfig.mAnimation);
	}
	
	@Override
	public Fragment getFragment() {
		return this;
	}

	@Override
	public CharSequence getTitle(Context context) {
		return context.getText(R.string.setting);//"设置";
	}

	@Override
	public boolean onBackPressed() {
		return false;
	}

	@Override
	public boolean canAddNewWindow(Window window) {
		if(window instanceof Setting)
			return false;
		return true;
	}

	@Override
	public boolean onClose() {
		return true;
	}

	@Override
	public List<MenuTag> getMenuTags() {
		List<MenuTag>  menuTags = new ArrayList<MenuTag>();
		menuTags.add(new MenuTag( R.string.theme, mWindowsManager.getContext().getText(R.string.theme) ));
		return menuTags;
	}

	@Override
	public boolean onMenuItemClick(int id) {
		switch (id) {
		case R.string.theme:
			mWindowsManager.addWindow(new FileBrowser(mWindowsManager,getThemeDir()));
			break;

		default:
			break;
		}
		return true;
	}
	
	public static Config loadConfig(Context context){
		SharedPreferences sharedPreferences=context.getSharedPreferences(ConfigName, Context.MODE_PRIVATE);
		return Config.load(sharedPreferences);
	}

	public static void applyChangeDefault(Config config){
		CodeStyleAdapter.mCommentsColor=config.mCEditorConfig.mCommentsColor;
		CodeStyleAdapter.mConstantColor=config.mCEditorConfig.mConstantColor;
		CodeStyleAdapter.mKeywordsColor=config.mCEditorConfig.mKeywordsColor;
		CodeStyleAdapter.mProKeywordsColor=config.mCEditorConfig.mProKeywordsColor;
		CodeStyleAdapter.mWordsColor=config.mCEditorConfig.mWordsColor;
		MyEditText.mFontScale=config.mEditorConfig.mFontScale;
		MyEditText.mLineScale=config.mEditorConfig.mLineScale;
		CodeStyleAdapter.refColorSpan();
		EditorFregment.mQuickInput=config.mOtherConfig.mQuickInput;
		WindowsManager.mSelectTitleColor=config.mOtherConfig.mSelectTitleColor;
		EditableWithLayout.mEnableHightLight=config.mCEditorConfig.mEnableHighLight;
		mConfig = config;
	}
	
	public static void save(Context context){
		SharedPreferences sharedPreferences = context.getSharedPreferences(ConfigName, Context.MODE_PRIVATE);
		Editor editor=sharedPreferences.edit();
		mConfig.save(editor);
		editor.commit();
	}
	
	@Override
	public void beforeTextChanged(CharSequence s, int start, int count,
			int after) {
	}

	@Override
	public void onTextChanged(CharSequence s, int start, int before, int count) {
	}

	@Override
	public void afterTextChanged(Editable s) {
		mRelativeLayout.getHandler().post(new Runnable() {
			@Override
			public void run() {
				loadEditView();
				refColorView();
				refSwitchView();
				mWindowsManager.sendConfigChanged();
			}
		});
	}
	
	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.button_check_update:
			new CheckUpdate(mWindowsManager.getContext()).checkForUpdate();
			break;
		case R.id.button_close_ad:
			break;
		case R.id.button_show_ad:
			break;
		case R.id.button_ok:
			applyChangeDefault(mConfig);
			save(getActivity());
			mWindowsManager.sendConfigChanged();
			showToast(getContext().getString(R.string.setting_save_success));
			break;
		case R.id.button_to_default:
			mConfig=Config.load(getActivity().getSharedPreferences("default", Context.MODE_PRIVATE));
			Log.i("Setting", "button_to_default:"+mConfig.mEditorConfig.mFontScale );
			refEditView();
			refColorView();
			refSwitchView();
			break;
		case R.id.button_save_theme:
			showSaveThemeDialog(getActivity());
			break;
		case R.id.button_privacy_policy:
			mPrivacyPolicy.showDialog(getActivity());
			break;
		default:
			break;
		}
	}
	
	private AlertDialog mSaveThemeDialog = null;
	private void showSaveThemeDialog(final Context context){
		if(mSaveThemeDialog==null)
		{
			AlertDialog.Builder builder = new Builder(context);
			builder.setTitle(R.string.save_cur_teme);
			builder.setCancelable(true);
			builder.setNegativeButton(android.R.string.cancel, null );
			final EditText editText = new EditText(context);
			editText.setText(R.string.saved_theme);
			builder.setView(editText);
			builder.setPositiveButton(android.R.string.ok, new AlertDialog.OnClickListener() {
				@Override
				public void onClick(DialogInterface dialog, int which) {
					String path = getThemeDir()+"/"+editText.getText()+".qtheme";
					if(new File(path).exists()){
						ToastUtil.showToast(R.string.theme_exits, Toast.LENGTH_SHORT);
					}else{
						new File(getThemeDir()).mkdirs();
						if(saveConfigToFile(getActivity(), mConfig,path )){
							ToastUtil.showToast(R.string.save_success, Toast.LENGTH_SHORT);
						}else{
							ToastUtil.showToast(R.string.save_failed, Toast.LENGTH_SHORT);
						}
					}
				}
			});
			mSaveThemeDialog = builder.create();
		}
		mSaveThemeDialog.show();
	}

	@Override
	public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
		loadSwitchView();
		mWindowsManager.sendConfigChanged();
	}
	
	private static int mPoints=0;
	
	private Toast mToast=null; 
	private void showToast(String str)
	{
		if(mToast!=null)
			mToast.cancel();
		mToast=Toast.makeText(mRelativeLayout.getContext(), str, Toast.LENGTH_LONG);
		mToast.show();
	}
	
	public static void  applySettingConfigToAllView(View view)
	{
		Config config = mConfig;
		if(config==null)
			return ; 
		if( view instanceof TextView && !(view instanceof Button) )
		{
			TextView textView = (TextView) view;
			int color = textView.getTextColors().getDefaultColor();
			textView.setTextColor( ((config.mEditorConfig.mBaseFontColor &0xffffff)) | (0xff000000&color) );
			TextPaint textPaint = textView.getPaint();
			boolean useNiceFont=config.mEditorConfig.mUseNiceFont;
			textPaint.setAntiAlias(true);
			textPaint.setDither(useNiceFont);
			textPaint.setFakeBoldText(useNiceFont);
			textPaint.setSubpixelText(useNiceFont);
			textView.postInvalidate();
		} 
		if( view instanceof MyEditText )
		{
			boolean useNiceFont=config.mEditorConfig.mUseNiceFont;
			MyEditText myEditText = (MyEditText) view;
			TextPaint textPaint = myEditText.getPaint();
			textPaint.setColor(config.mEditorConfig.mBaseFontColor);
			textPaint.setTypeface(Typeface.MONOSPACE);
			textPaint.setAntiAlias(true);
			textPaint.setDither(useNiceFont);
			textPaint.setFakeBoldText(useNiceFont);
			textPaint.setSubpixelText(useNiceFont);
			myEditText.setTextSize(myEditText.getTextSize());
			myEditText.postInvalidate();
		}  
		if(view instanceof TitleView)
		{
			TitleView titleView = (TitleView) view;
			titleView.setQuickCloseEnable(config.mOtherConfig.mQuickCloseEnable);
			titleView.setQuickColseColor(config.mOtherConfig.mQuickCloseColor);
		} 
		if(view instanceof ViewGroup)
		{
			ViewGroup viewGroup = (ViewGroup) view ;
			int count = viewGroup.getChildCount();
			if(count>0)
			{
				for(int i=0;i<count;i++)
				{
					View child = viewGroup.getChildAt(i);
					applySettingConfigToAllView(child);
				}
			}
		}
		if(view instanceof TermView)
		{
			TermView termView = (TermView) view;
			termView.setColorScheme(new ColorScheme(config.mEditorConfig.mBaseFontColor, 0x00000000));
		}
	}
	
	@SuppressLint("NewApi")
	public static void applySettingConfigToActivity(Activity activity){
		Config config = mConfig;
		if(config==null)
			return ; 
		android.view.Window window = activity.getWindow();
		if(Build.VERSION.SDK_INT>=21){
			window.setNavigationBarColor(config.mOtherConfig.mTitleBarColor);
		}
	}

	public static class Config {
		public EditorConfig mEditorConfig = new EditorConfig();
		public CEditorConfig mCEditorConfig = new CEditorConfig();
		public OtherConfig mOtherConfig = new OtherConfig();
		public static Config load(SharedPreferences sharedPreferences){
			Config config=new Config();
			config.mEditorConfig=EditorConfig.load(sharedPreferences);
			config.mCEditorConfig=CEditorConfig.load(sharedPreferences);
			config.mOtherConfig=OtherConfig.load(sharedPreferences);
			return config;
		}
		
		public void save(Editor editor){
			mEditorConfig.save(editor);
			mCEditorConfig.save(editor);
			mOtherConfig.save(editor);
		}
		
	}
	
	public static class EditorConfig{
		public boolean mAutoUpdate = true ; 
		public boolean mUseNiceFont = true ; 
		public int mBackGroundColor = Color.rgb(0xff, 0xef, 0xd5) ;
		public int mBaseFontColor = Color.rgb(0x44, 0x44, 0x44);
		public float mFontScale = 1.0f ;
		public float mLineScale = 1.0f ;
		public String mBackgroundImage = "";
		public Bitmap mBitmap = null;
		public static EditorConfig load(SharedPreferences sharedPreferences){
			EditorConfig editorConfig=new EditorConfig();
			editorConfig.mAutoUpdate=sharedPreferences.getBoolean("mAutoUpdate",editorConfig.mAutoUpdate);
			editorConfig.mUseNiceFont=sharedPreferences.getBoolean("mUseNiceFont",editorConfig.mUseNiceFont);
			editorConfig.mBackGroundColor=sharedPreferences.getInt("mBackGroundColor", editorConfig.mBackGroundColor);
			editorConfig.mBaseFontColor=sharedPreferences.getInt("mBaseFontColor", editorConfig.mBaseFontColor);
			editorConfig.mFontScale=sharedPreferences.getFloat("mFontScale", editorConfig.mFontScale);
			editorConfig.mLineScale=sharedPreferences.getFloat("mLineScale", editorConfig.mLineScale);
			editorConfig.setImage( sharedPreferences.getString("mBackgroundImage", "") );
			return editorConfig;
		}
		public void save(Editor editor){
			editor.putBoolean("mAutoUpdate",mAutoUpdate );
			editor.putBoolean("mUseNiceFont",mUseNiceFont );
			editor.putInt("mBackGroundColor",mBackGroundColor );
			editor.putInt("mBaseFontColor",mBaseFontColor );
			editor.putFloat("mFontScale",mFontScale );
			editor.putFloat("mLineScale",mLineScale );
			editor.putString("mBackgroundImage",mBackgroundImage );
		}
		
		public void setImage(String file){
			mBackgroundImage = file;
			mBitmap = BitmapFactory.decodeFile(mBackgroundImage);
		}
	}
	
	public static class CEditorConfig{
		public boolean mEnableHighLight = true;
		public int mCommentsColor = Color.rgb( 0x60, 0xa0, 0x60);
		public int mConstantColor = Color.rgb( 0xff, 0x80, 0x80) ;
		public int mKeywordsColor = Color.rgb( 0x80, 0x80, 0xff) ;
		public int mProKeywordsColor = Color.rgb( 0x80, 0x80, 0xff) ;
		public int mWordsColor = Color.rgb( 0x80, 0x80, 0x80) ;
		public static CEditorConfig load(SharedPreferences sharedPreferences){
			CEditorConfig editorConfig=new CEditorConfig();
			editorConfig.mCommentsColor=sharedPreferences.getInt("mCommentsColor", editorConfig.mCommentsColor);
			editorConfig.mConstantColor=sharedPreferences.getInt("mConstantColor", editorConfig.mConstantColor);
			editorConfig.mKeywordsColor=sharedPreferences.getInt("mKeywordsColor", editorConfig.mKeywordsColor);
			editorConfig.mProKeywordsColor=sharedPreferences.getInt("mProKeywordsColor", editorConfig.mProKeywordsColor);
			editorConfig.mWordsColor=sharedPreferences.getInt("mWordsColor", editorConfig.mWordsColor);
			editorConfig.mEnableHighLight=sharedPreferences.getBoolean("mEnableHighLight", editorConfig.mEnableHighLight );
			return editorConfig;
		}

		public void save(Editor editor){
			editor.putInt("mCommentsColor",mCommentsColor );
			editor.putInt("mConstantColor",mConstantColor );
			editor.putInt("mKeywordsColor",mKeywordsColor );
			editor.putInt("mProKeywordsColor",mProKeywordsColor );
			editor.putInt("mWordsColor",mWordsColor );
			editor.putBoolean("mEnableHighLight",mEnableHighLight );
		}
		
	}
	

	public static class OtherConfig{
		public int mThreadNumber = 8;
		public String mQuickInput = "\t'\"`$[]{}<>()+-*%=&|!^~,;?:_\\";
		public int mTitleBarColor = Color.rgb(0xff, 0xd3, 0x9b);
		public int mSelectTitleColor = Color.rgb(0xff, 0xef, 0xd5) ;
		public boolean mQuickCloseEnable = true;
		public int mQuickCloseColor = Color.argb(0xff, 0xff, 0x6f, 0x00);
		public boolean mNewConsoleEnable = true;
		public boolean mNewEditorEnable = false;
		public boolean mTitleAtHead = true;
		public boolean mCtrlAtHead = true;
		public boolean mAnimation = true;
		public static OtherConfig load(SharedPreferences sharedPreferences){
			OtherConfig config=new OtherConfig();
			config.mThreadNumber=sharedPreferences.getInt("mThreadNumber",config.mThreadNumber);
			config.mQuickInput=sharedPreferences.getString("mQuickInput", config.mQuickInput);
			config.mTitleBarColor=sharedPreferences.getInt("mTitleBarColor",config.mTitleBarColor);
			config.mSelectTitleColor=sharedPreferences.getInt("mSelectTitleColor",config.mSelectTitleColor);
			config.mQuickCloseEnable=sharedPreferences.getBoolean("mQuickCloseEnable",config.mQuickCloseEnable);
			config.mQuickCloseColor=sharedPreferences.getInt("mQuickCloseColor",config.mQuickCloseColor);
			config.mNewConsoleEnable=sharedPreferences.getBoolean("mNewConsoleEnable",config.mNewConsoleEnable);
			config.mNewEditorEnable=sharedPreferences.getBoolean("mNewEditorEnable",config.mNewEditorEnable);
			config.mTitleAtHead=sharedPreferences.getBoolean("mTitleAtHead",config.mTitleAtHead);
			config.mCtrlAtHead=sharedPreferences.getBoolean("mCtrlAtHead",config.mCtrlAtHead);
			config.mAnimation=sharedPreferences.getBoolean("mAnimation",config.mAnimation);
			return config;
		}

		public void save(Editor editor){
			editor.putInt("mThreadNumber", mThreadNumber);
			editor.putString("mQuickInput",mQuickInput );
			editor.putInt("mTitleBarColor", mTitleBarColor);
			editor.putInt("mSelectTitleColor", mSelectTitleColor);
			editor.putBoolean("mQuickCloseEnable", mQuickCloseEnable);
			editor.putInt("mQuickCloseColor", mQuickCloseColor);
			editor.putBoolean("mNewConsoleEnable", mNewConsoleEnable);
			editor.putBoolean("mNewEditorEnable", mNewEditorEnable);
			editor.putBoolean("mTitleAtHead", mTitleAtHead);
			editor.putBoolean("mCtrlAtHead", mCtrlAtHead);
			editor.putBoolean("mAnimation", mAnimation);
		}
	}
	
	public static boolean saveConfigToFile(Context context,Config config,String file) {
		String tempName = "temp_config";
		SharedPreferences sharedPreferences=context.getSharedPreferences(tempName, Context.MODE_MULTI_PROCESS );
		Editor editor = sharedPreferences.edit();
		config.save(editor);
		editor.commit();
		String configPath = context.getFilesDir().getAbsolutePath()+"/../shared_prefs/"+tempName+".xml";
		new File(file).delete();
		FileWork.CopyFile(new File(configPath), new File(file), new byte[1024]);
		return new File(file).length()>0;
	}
	
	public static boolean applyTheme(Context context,File file){
		Config config = loadConfigFromFile(context,file.getAbsolutePath());
		if(config!=null){
			applyChangeDefault(config);
			save(context);
			return true;
		}
		return false;
	}
	
	private static Config loadConfigFromFile(Context context,String file){
		String tempName = "temp_config";
		String configPath = context.getFilesDir().getAbsolutePath()+"/../shared_prefs/"+tempName+".xml";
		if(!new File(configPath).delete()){
			Log.i("Setting","delete failed!" );
		}
		FileWork.CopyFile(new File(file), new File(configPath), new byte[1024]);
		if( new File(configPath).length()<=0)
			return null;
		SharedPreferences sharedPreferences=context.getSharedPreferences(tempName, Context.MODE_MULTI_PROCESS );
		return Config.load(sharedPreferences);
	}

	@Override
	public String[] getResumeCmd() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void resumeByCmd(String []cmd) {
		// TODO Auto-generated method stub
		
	}
	
	public static String getThemeDir(){
		return GNUCCompiler.getSystemDir()+"/themes/";
	}
}
