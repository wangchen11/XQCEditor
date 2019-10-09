package person.wangchen11.qeditor;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import android.annotation.SuppressLint;
import android.annotation.TargetApi;
import android.app.AlertDialog;
import android.content.Context;
import android.graphics.Color;
import android.graphics.Rect;
import android.graphics.drawable.ColorDrawable;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.text.Editable;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.view.WindowManager.LayoutParams;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;
import person.wangchen11.ccode.WantMsg;
import person.wangchen11.codeanalysis.ccode.CCodeFormat;
import person.wangchen11.codeanalysis.ccode.CCodeKeywords;
import person.wangchen11.codeanalysis.ccode.CCodeKeywordsAdapter;
import person.wangchen11.codeanalysis.ccode.CCodeParser;
import person.wangchen11.cproject.CProject;
import person.wangchen11.drawable.CircleDrawable;
import person.wangchen11.editor.codeedittext.CodeEditText;
import person.wangchen11.editor.codeedittext.OnNeedChangeWants;
import person.wangchen11.editor.edittext.AfterTextChangeListener;
import person.wangchen11.gnuccompiler.CheckInfo;
import person.wangchen11.gnuccompiler.GNUCCodeCheck;
import person.wangchen11.util.PublicThreadPool;
import person.wangchen11.util.ToastUtil;
import person.wangchen11.window.ext.Setting;
import person.wangchen11.xqceditor.R;

public class EditorFregment extends Fragment implements OnClickListener, AfterTextChangeListener, OnNeedChangeWants, OnItemClickListener, OnItemSelectedListener{
	protected static final String TAG="EditorFregment";
	public static String mQuickInput="\t{}[]()<>+-*/%=&|!^~,;?:_'\"\\";
	private File mFile=null;
	private RelativeLayout mRelativeLayout;
	private CodeEditText mCodeEditText;
	private LinearLayout mLinearLayoutOfChars;
	private ListView mListView= null;
	private Spinner mCodeTypeSpinner=null;
	private String mCharset=null;
	private boolean mIsChanged=false;
	private Toast mToast=null;
	private OnRunButtonClickListener mOnRunButtonClickListener = null;
	private Handler mHandler = new Handler();
	private int mInitSelectionStart = 0;
	private int mInitSelectionEnd = 0;
	private static String []mCodeTypeNames=new String[]{
		"TXT",
		"C",
		"C++",
		"Java",
		"Shell",
		"ASM",
		"PHP",
	};
	
	private AlertDialog mFindDialog = null;
	public EditorFregment() {
	}

	public EditorFregment(File file) {
		mFile=file;
	}
	
	public EditorFregment(File file,String charset) {
		mCharset=charset;
		mFile=file;
	}
	
	public File getFile(){
		return mFile;
	}
	
	public void setInitSelection(int start,int end){
		mInitSelectionStart = start;
		mInitSelectionEnd   = end;
	}
	
	public int getSelectionStart(){
		return mCodeEditText.getSelectionStart();
	}
	
	public int getSelectionEnd(){
		return mCodeEditText.getSelectionEnd();
	}
	
	public void setOnRunButtonClickListener(OnRunButtonClickListener listener)
	{
		mOnRunButtonClickListener = listener;
	}
	
	@SuppressLint({ "InflateParams", "DefaultLocale" })
	@Override
	public View onCreateView(LayoutInflater inflater,
			@Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
		if(Setting.mConfig.mOtherConfig.mCtrlAtHead){
			mRelativeLayout = (RelativeLayout) inflater.inflate(R.layout.fragment_ceditor, null);
		}else{
			mRelativeLayout = (RelativeLayout) inflater.inflate(R.layout.fragment_ceditor_cdt, null);
		}
		mListView= (ListView) mRelativeLayout.findViewById(R.id.listView1);
		mListView.setVisibility(View.GONE);
		mListView.setOnItemClickListener(this);
		mCodeTypeSpinner=(Spinner) mRelativeLayout.findViewById(R.id.code_type_spinner);
		mCodeTypeSpinner.setOnItemSelectedListener(this);
		mCodeTypeSpinner.setAdapter(new ArrayAdapter<>(getActivity(), android.R.layout.simple_spinner_dropdown_item, mCodeTypeNames));
		mCodeTypeSpinner.setSelection(0);
		mCodeEditText=(CodeEditText) mRelativeLayout.findViewById(R.id.ccode_edittext);
		mCodeEditText.setCodeType(CodeEditText.CodeType.TYPE_NONE);
		if(mFile!=null)
		{
			String name=mFile.getName().toLowerCase();
			if(name.endsWith(".txt"))
			{
				mCodeTypeSpinner.setSelection(0);
			}else
			if(name.endsWith(".c"))
			{
				mCodeTypeSpinner.setSelection(1);
			}else
			if(name.endsWith(".h")||name.endsWith(".hp")||name.endsWith(".hpp")||name.endsWith(".cpp")|| !name.contains("."))
			{
				mCodeTypeSpinner.setSelection(2);
			}else
			if(name.endsWith(".java"))
			{
				mCodeTypeSpinner.setSelection(3);
			}else
			if(name.endsWith(".sh"))
			{
				mCodeTypeSpinner.setSelection(4);
			}else
			if(name.endsWith(".s"))
			{
				mCodeTypeSpinner.setSelection(5);
			}else
			if(name.endsWith(".php"))
			{
				mCodeTypeSpinner.setSelection(6);
			}
		}
		mCodeEditText.setTag(mFile);
		mCodeEditText.setOnNeedChangeWants(this);
		mLinearLayoutOfChars=(LinearLayout) mRelativeLayout.findViewById(R.id.chars_list);
		String str;
		try {
			str = getTextFromFile(mFile);
			if(str!=null)
			{
				mCodeEditText.setText(str);
				mCodeEditText.setSelection(mInitSelectionStart,mInitSelectionEnd);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		configAllView(mRelativeLayout);
		configCharList(mQuickInput);
		
		mCodeEditText.setOnKeyListener(new View.OnKeyListener() {
			@Override
			public boolean onKey(View v, int keyCode, KeyEvent event) {
				Log.i(TAG, "onKey:"+keyCode);
				if(mListView.getVisibility()==View.VISIBLE) {
					if(keyCode == KeyEvent.KEYCODE_DPAD_DOWN){
						mListView.setItemChecked(0, true);
						return true;
					}else if(keyCode == KeyEvent.KEYCODE_DPAD_UP){
					}else if(keyCode == KeyEvent.KEYCODE_DPAD_CENTER || keyCode == KeyEvent.KEYCODE_ENTER){
					}
				}else{
				}
				return false;
			}
		});
		
		return mRelativeLayout;
	}
	
	@Override
	public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
		mCodeEditText.cleanRedo();
		mCodeEditText.cleanUndo();
		mCodeEditText.setMaxSaveHistory(120);
		mCodeEditText.setAfterTextChangeListener(this);
		mIsChanged=false;
		if(mChangeFlagChanged!=null)
			mChangeFlagChanged.onChangeFlagChanged();
		onChangeFlagChanged();
		super.onViewCreated(view, savedInstanceState);
	}
	
	private void configAllView(View view){
		if(view instanceof ViewGroup )
		{
			int count = ((ViewGroup)(view)).getChildCount();
			for(int i=0;i<count;i++){
				configAllView(((ViewGroup)(view)).getChildAt(i));
			}
		}
		if( view instanceof ImageButton || view instanceof TextView ){
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


	//private static ExecutorService mExecutorService = null;
	public void onChangeFlagChanged() {

		if(!isChanged())
		{
			/*
			if(mExecutorService==null)
			{
				mExecutorService = Executors.newSingleThreadExecutor();
			}*/
			if(mCodeCheck==null)
			{
				
				PublicThreadPool.getPublicThreadPool().execute(new Runnable() {
					@Override
					public void run() {
						checkCode();
					}
				});
			}
		}
	}
	
	GNUCCodeCheck mCodeCheck = null;
	@SuppressLint("DefaultLocale")
	private void checkCode()
	{
		synchronized (EditorFregment.this) {
			if(mCodeCheck!=null)
				mCodeCheck.stop();
		}
		Context context = getActivity();
		File file = getFile();
		if(file!=null&&context!=null)
		{
			mCodeCheck = new GNUCCodeCheck(context, file);
			String name = file.getName().toLowerCase();
			if(name.endsWith(".c")||name.endsWith(".cpp")/*||name.endsWith(".h")*/||name.endsWith(".hpp")||name.endsWith(".hp"))
			{
				final LinkedList<CheckInfo> checkInfos = mCodeCheck.start();
				if(checkInfos!=null){
					mHandler.post(new Runnable() {
						@Override
						public void run() {
							if(mCodeEditText!=null)
								mCodeEditText.setWarnAndError( CheckCodeAdapt.getCWarnAndErrors(checkInfos,mFile) );
						}
					});
				}
			}
		}
		synchronized (EditorFregment.this) {
			mCodeCheck = null;
		}
	}
	
	private void configCharList(String chars){
		float d=getActivity().getResources().getDisplayMetrics().density;
		Rect rect=new Rect( (int)(d*12),(int)(d*1),(int)(d*12),(int)(d*1) );
		mLinearLayoutOfChars.removeAllViews();
		for(int i=0;i<chars.length();i++){
			char ch=chars.charAt(i);
			TextView textView;
			textView=new TextView(getActivity());
			if(ch=='\t'){
				textView.setText(String.valueOf("-->|"));
			}else{
				textView.setText(String.valueOf(ch));
			}
			textView.setTag(String.valueOf(ch));
			textView.setTextSize(d*8);
			textView.setPadding(rect.left, rect.top, rect.right, rect.bottom);
			mLinearLayoutOfChars.addView(textView);
		}
		configAllView(mLinearLayoutOfChars);
	}
	
	private String getTextFromFile(File file) throws Exception{
		if(file==null)
		{
			return "";
		}
		String str=null;
		file=new File(file.getPath());
		int length=(int) file.length();
		FileInputStream fileInputStream=new FileInputStream(file);
		try {
			byte []data=new byte[length];
			int readlen=fileInputStream.read(data);
			mCharset = guessEncoding(data,0,readlen);
			Log.i(TAG, "mChartset:"+mCharset);
			try {
				if(mCharset!=null)
					str=new String(data, 0, readlen,mCharset);
				else
					str=new String(data, 0, readlen);
			} catch (Exception e) 
			{
				str=new String(data, 0, readlen);
			}
			if(mCharset!=null&&(!mCharset.equals("UTF-8"))){
				ToastUtil.showToast(String.format(getActivity().getString(R.string.s_to_utf_8),mCharset), Toast.LENGTH_SHORT);
				mCharset = "UTF-8";
			}
		} catch (OutOfMemoryError error) {
			throw new Exception("getTextFromFile OOM!");
		}
		finally
		{
			fileInputStream.close();
		}
		return str;
	}
	
	public static String guessEncoding(byte[] bytes,int offset,int length) {
	    org.mozilla.universalchardet.UniversalDetector detector =  
	        new org.mozilla.universalchardet.UniversalDetector(null);  
	    detector.handleData(bytes, offset, length);  
	    detector.dataEnd();  
	    String encoding = detector.getDetectedCharset();  
	    detector.reset();
	    return encoding;  
	}
	
	@TargetApi(Build.VERSION_CODES.HONEYCOMB)
	@SuppressLint("InflateParams")
	@Override
	public void onClick(View v) {
		AlertDialog alertDialog;
		AlertDialog.Builder builder;
		Editable str,str1;
		switch (v.getId()) {
		case R.id.editor_button_more:
			if(mLinearLayoutOfChars.getVisibility() == View.GONE)
			{
				mLinearLayoutOfChars.setVisibility(View.VISIBLE);
			}else{
				mLinearLayoutOfChars.setVisibility(View.GONE);
			}
			break;
		case R.id.button_save:
			if(save()==false){
				if(mToast!=null)
					mToast.cancel();
				mToast=Toast.makeText(v.getContext(), R.string.save_failed,Toast.LENGTH_SHORT );
				mToast.show();
			}
			break;
		case R.id.button_undo:
			mCodeEditText.undo();
			break;
			
		case R.id.button_redo:
			mCodeEditText.redo();
			break;
		case R.id.button_play:
			if(save()==false){
				if(mToast!=null)
					mToast.cancel();
				mToast=Toast.makeText(v.getContext(), R.string.save_failed,Toast.LENGTH_SHORT );
				mToast.show();
			}
			if(mOnRunButtonClickListener!=null && mOnRunButtonClickListener.onRunButtonClick())
			{
				;
			}
			else
			{
				if(mToast!=null)
					mToast.cancel();
				mToast=Toast.makeText(v.getContext(), getText(R.string.this_file_can_not_run),Toast.LENGTH_SHORT );
				mToast.show();
			}
			
			break;
		case R.id.button_find:
			str=((EditText)(mFindDialog.findViewById(R.id.et_find))).getText();
			if( !mCodeEditText.findString(str.toString()) )
			{
				if(mToast!=null)
					mToast.cancel();
				mToast=Toast.makeText(v.getContext(), getText(R.string.failed_find),Toast.LENGTH_SHORT );
				mToast.show();
			}
			break;
		case R.id.button_replace:
			str=((EditText)(mFindDialog.findViewById(R.id.et_replace))).getText();
			if( !mCodeEditText.replaceString(str.toString()) )
			{
			}
			break;
		case R.id.button_replace_find:
			str=((EditText)(mFindDialog.findViewById(R.id.et_find))).getText();
			str1=((EditText)(mFindDialog.findViewById(R.id.et_replace))).getText();
			if( !mCodeEditText.replaceFindString(str.toString(),str1.toString()) )
			{
				if(mToast!=null)
					mToast.cancel();
				mToast=Toast.makeText(v.getContext(), getText(R.string.failed_find_replace),Toast.LENGTH_SHORT );
				mToast.show();
			}
			break;
		case R.id.button_replace_all:
			str=((EditText)(mFindDialog.findViewById(R.id.et_find))).getText();
			str1=((EditText)(mFindDialog.findViewById(R.id.et_replace))).getText();
			if( !mCodeEditText.replaceAll(str.toString(),str1.toString()) )
			{
				if(mToast!=null)
					mToast.cancel();
				mToast=Toast.makeText(v.getContext(), getText(R.string.failed_replace_all),Toast.LENGTH_SHORT );
				mToast.show();
			}
			break;
		case R.id.image_button_find:
			if(mFindDialog==null)
			{
				LayoutInflater inflater = LayoutInflater.from(getActivity());
				LinearLayout layout = (LinearLayout) inflater.inflate(R.layout.dialog_find, null);
				((Button)(layout.findViewById(R.id.button_find))).setOnClickListener(this);
				((Button)(layout.findViewById(R.id.button_replace))).setOnClickListener(this);
				((Button)(layout.findViewById(R.id.button_replace_all))).setOnClickListener(this);
				((Button)(layout.findViewById(R.id.button_replace_find))).setOnClickListener(this);
				
				builder=new AlertDialog.Builder(getActivity());
				mFindDialog=builder.create();
				mFindDialog.show();
				mFindDialog.getWindow().clearFlags(WindowManager.LayoutParams.FLAG_ALT_FOCUSABLE_IM);
				mFindDialog.getWindow().setContentView(layout);
				mFindDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.argb(55,255,255,255) ));
				
				LayoutParams attributes = mFindDialog.getWindow().getAttributes();  
				attributes.flags = LayoutParams.FLAG_DIM_BEHIND;  
				attributes.dimAmount = 0.4f;
				mFindDialog.getWindow().setAttributes(attributes);
				/*
				if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB)
					layout.setAlpha(0.8f);*/
			}
			else
				mFindDialog.show();
			break;
		case R.id.button_info:
			if(mFile!=null)
			{
				builder=new AlertDialog.Builder(getActivity());
				builder.setNegativeButton(android.R.string.ok, null);
				alertDialog=builder.create();
				alertDialog.setTitle(R.string.info);
				String message="";
				message+=getActivity().getString(R.string.charset)+(mCharset!=null?mCharset:"unknow")+"\n";
				message+=getActivity().getString(R.string.file_name)+mFile.getName()+"\n";
				message+=getActivity().getString(R.string.file_path)+mFile.getPath()+"\n";
				
				CProject cProject=CProject.findCProjectByFile(mFile);
				if(cProject!=null){
					message+=getActivity().getString(R.string.project_name)+cProject.getProjectName()+"\n";
					message+=getActivity().getString(R.string.src_path)+cProject.getSrcName()+"\n";
					message+=getActivity().getString(R.string.bin_path)+cProject.getBinName()+"\n";
					message+=getActivity().getString(R.string.project_path)+cProject.getProjectPath()+"\n";
				}
				else{
					message+=getActivity().getString(R.string.not_project);
				}
				alertDialog.setMessage(message);
				alertDialog.show();
			}
			break;
		default:
			if(v instanceof TextView && v.getTag() instanceof String )
				mCodeEditText.insertText(v.getTag().toString());
			break;
		}
	}

	public boolean save() {
		if(mFile == null)
			return false;
		try {
			FileOutputStream fileOutputStream=new FileOutputStream(mFile);
			try {
				try {
					byte []data=null;
					if(mCharset!=null)
						data=mCodeEditText.getText().toString().getBytes(mCharset);
					else
						data=mCodeEditText.getText().toString().getBytes();
					fileOutputStream.write(data);
					mIsChanged=false;
					if(mChangeFlagChanged!=null)
						mChangeFlagChanged.onChangeFlagChanged();
					onChangeFlagChanged();
					return true;
				} catch (Error e) {
					e.printStackTrace();
				}
			} catch (IOException e) {
				e.printStackTrace();
			}
			finally{
				try {
					fileOutputStream.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}
		return false;
	}
	@Override
	public void onDestroyView() {
		mCodeEditText=null;
		mRelativeLayout=null;
		super.onDestroyView();
	}
	@Override
	public void onDestroy() {
		super.onDestroy();
	}
	
	public void closeInputMethod(){
		if(mCodeEditText!=null)
			mCodeEditText.closeInputMethod();
	}
	
	
	@Override
	public void afterTextChange() {
		Log.i(TAG, "afterTextChange");
		mIsChanged=true;
		if(mCodeEditText.canRedo())
			mRelativeLayout.findViewById(R.id.button_redo).setVisibility(View.VISIBLE);
		else 
			mRelativeLayout.findViewById(R.id.button_redo).setVisibility(View.INVISIBLE);
		
		if(mCodeEditText.canUndo())
			mRelativeLayout.findViewById(R.id.button_undo).setVisibility(View.VISIBLE);
		else 
			mRelativeLayout.findViewById(R.id.button_undo).setVisibility(View.INVISIBLE);
		if(mChangeFlagChanged!=null)
			mChangeFlagChanged.onChangeFlagChanged();
		onNeedChangeWants(0,0,null);
		mCodeEditText.requestFocus();

		synchronized (EditorFregment.this) {
			if(mCodeCheck!=null)
				mCodeCheck.stop();
		}
		mCodeEditText.setWarnAndError( null );
	}
	
	public boolean isChanged(){
		return mIsChanged;
	}
	
	ChangeFlagChanged mChangeFlagChanged=null;
	public void setChangeFlagChanged(ChangeFlagChanged flagChanged){
		mChangeFlagChanged=flagChanged;
	}
	
	private int mWantChangeStart=0;
	private int mWantChangeEnd=0;
	
	@Override
	public void onNeedChangeWants(int start, int end, List<WantMsg> wants) {
		Log.i(TAG, "onNeedChangeWants:"+start+ " "+end);
		mWantChangeStart=start;
		mWantChangeEnd=end;
		if(mCodeEditText==null)
			return;
		if(wants==null||wants.size()<=0)
		{
			mListView.setVisibility(View.GONE);
			mCodeEditText.setOnKeyListener(null);
		}
		else{
			mListView.setVisibility(View.VISIBLE);
			WantListAdapter wantListAdapter = new WantListAdapter(wants,mListView){
				@Override
				public boolean onSelected(int position) {
					if(position>=0){
						WantMsg wantMsg = getItem(position);
						mCodeEditText.getText().replace(mWantChangeStart, mWantChangeEnd, wantMsg.toString());
					}
					onNeedChangeWants(0,0,null);
					return super.onSelected(position);
				}
			};
			mListView.setAdapter(wantListAdapter);
			mCodeEditText.setOnKeyListener(wantListAdapter);
		}
		//Log.i(TAG, "wants:"+wants);
	}
	
	@Override
	public void onItemClick(AdapterView<?> parent, View view, int position,
			long id) {
		String string=parent.getAdapter().getItem(position).toString();
		mCodeEditText.getText().replace(mWantChangeStart, mWantChangeEnd, string);
	}

	@Override
	public void onItemSelected(AdapterView<?> parent, View view, int position,
			long id) {
		switch (position) {
		case 0:
			mCodeEditText.setCodeType(CodeEditText.CodeType.TYPE_NONE);
			break;
		case 1:
			mCodeEditText.setCodeType(CodeEditText.CodeType.TYPE_C);
			break;
		case 2:
			mCodeEditText.setCodeType(CodeEditText.CodeType.TYPE_CPP);
			break;
		case 3:
			mCodeEditText.setCodeType(CodeEditText.CodeType.TYPE_JAVA);
			break;
		case 4:
			mCodeEditText.setCodeType(CodeEditText.CodeType.TYPE_SHELL);
			break;
		case 5:
			mCodeEditText.setCodeType(CodeEditText.CodeType.TYPE_ARM_ASM);
			break;
		case 6:
			mCodeEditText.setCodeType(CodeEditText.CodeType.TYPE_PHP);
			break;
		default:
			mCodeEditText.setCodeType(CodeEditText.CodeType.TYPE_NONE);
			break;
		}
	}
	
	public void setText(String text){
		mCodeEditText.setText(text);
	}
	
	public boolean codeFormat()
	{
		Editable editable = mCodeEditText.getText();
		CCodeParser codeParser = new CCodeParser(editable.toString(), new CCodeKeywordsAdapter() {
			
			@Override
			public String[] getCCodeProKeywords() {
				return CCodeKeywords.mProKeyWord;
			}
			
			@Override
			public String[] getCCodeKeywords() {
				return CCodeKeywords.mKeyWord;
			}
		}
		);
		codeParser.run();
		CCodeFormat codeFormat = new CCodeFormat(codeParser);
		editable.replace(0, editable.length(), codeFormat.getFormatedCode());
		return true;
	}
	
	@Override
	public void onNothingSelected(AdapterView<?> parent) {
	}
	
}
