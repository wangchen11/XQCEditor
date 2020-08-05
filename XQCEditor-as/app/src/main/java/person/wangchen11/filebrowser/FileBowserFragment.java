package person.wangchen11.filebrowser;
import java.io.File;
import java.io.IOException;
import java.io.OutputStream;
import java.util.LinkedList;

import person.wangchen11.util.PublicThreadPool;
import person.wangchen11.waps.Waps;
import person.wangchen11.xqceditor.R;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.AlertDialog.Builder;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.AbsListView;
import android.widget.AbsListView.OnScrollListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AdapterView.OnItemLongClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;


public class FileBowserFragment extends Fragment implements OnItemClickListener,OnItemLongClickListener
				,PathChangeCallBack,OnScrollListener,OnClickListener
{
	public static File mDefaultFile=Environment.getExternalStorageDirectory();
	protected static final String TAG="FileBowserFragment";
	private String mDefaultPath=null;
	OnOpenListener mOpenListener=null;
	FileListAdapter mFileListAdapter=null;
	TextView mTextView=null;
	Toast mToast=null;
	ListView mListView=null;
	LinearLayout mRenameLayout=null;
	LinearLayout mOptionLayout=null;
	LinearLayout mPasteLayout=null;
	LinearLayout mDecompileLayout=null;
	Button mDecodeButton=null;
	Button mBuildButton=null;
	Button mSignerButton=null;
	Integer mPos=0;
	LinkedList< Integer > mHistroyPos=new LinkedList<Integer>();
	Runtime mRuntime=null;
	Process mProcess=null;
	boolean mIsAlive=true;
	@SuppressLint("InflateParams")
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,Bundle savedInstanceState) {
		final View view=inflater.inflate(R.layout.filebowserlayout, null);
		mFileListAdapter=new FileListAdapter(inflater);
		mListView=(ListView)view.findViewById(R.id.filelist);
		mListView.setAdapter(mFileListAdapter);
		mListView.setOnItemClickListener(this);
		mListView.setOnItemLongClickListener(this);
		mListView.setOnScrollListener(this);
		mTextView=(TextView)view.findViewById(R.id.prompt);
		mFileListAdapter.SetCallBack(this);
		mFileListAdapter.OpenPath(mDefaultPath!=null?new File(mDefaultPath):mDefaultFile);
		mRenameLayout=(LinearLayout)view.findViewById(R.id.rename);
		mOptionLayout=(LinearLayout)view.findViewById(R.id.option);
		mPasteLayout=(LinearLayout)view.findViewById(R.id.pastelayout);
		mDecompileLayout=(LinearLayout)view.findViewById(R.id.decompile);
		OnTouchListener touchListener=new OnTouchListener() {
			@SuppressLint({ "ClickableViewAccessibility", "NewApi" })
			@Override
			public boolean onTouch(View arg0, MotionEvent arg1) {
				if(arg1.getAction()==MotionEvent.ACTION_DOWN)
				{
					if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {  
						arg0.setAlpha(0.2f);
					}
				}
				if(arg1.getAction()!=MotionEvent.ACTION_DOWN&&arg1.getAction()!=MotionEvent.ACTION_MOVE)
				{
					if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {  
						arg0.setAlpha(1.0f);
					}
				}
				return arg0.onTouchEvent(arg1);
			}
		};
		ImageButton imageButton;
		imageButton=(ImageButton)view.findViewById(R.id.copy);
		imageButton.setOnTouchListener(touchListener);
		imageButton.setOnClickListener(this);
		imageButton=(ImageButton)view.findViewById(R.id.cut);
		imageButton.setOnTouchListener(touchListener);
		imageButton.setOnClickListener(this);
		imageButton=(ImageButton)view.findViewById(R.id.share);
		imageButton.setOnTouchListener(touchListener);
		imageButton.setOnClickListener(this);
		imageButton=(ImageButton)view.findViewById(R.id.delete);
		imageButton.setOnTouchListener(touchListener);
		imageButton.setOnClickListener(this);
		imageButton=(ImageButton)view.findViewById(R.id.info);
		imageButton.setOnTouchListener(touchListener);
		imageButton.setOnClickListener(this);
		imageButton=(ImageButton)view.findViewById(R.id.selectall);
		imageButton.setOnTouchListener(touchListener);
		imageButton.setOnClickListener(this);
		imageButton=(ImageButton)view.findViewById(R.id.donerename);
		imageButton.setOnTouchListener(touchListener);
		imageButton.setOnClickListener(this);
		imageButton=(ImageButton)view.findViewById(R.id.paste);
		imageButton.setOnTouchListener(touchListener);
		imageButton.setOnClickListener(this);
		imageButton=(ImageButton)view.findViewById(R.id.cancel);
		imageButton.setOnTouchListener(touchListener);
		imageButton.setOnClickListener(this);
		mDecodeButton =(Button)view.findViewById(R.id.decode);
		mDecodeButton.setOnClickListener(this);
		mBuildButton =(Button)view.findViewById(R.id.build);
		mBuildButton.setOnClickListener(this);
		mSignerButton =(Button)view.findViewById(R.id.signer);
		mSignerButton.setOnClickListener(this);
		mOptionLayout.setVisibility(View.GONE);
		mPasteLayout.setVisibility(View.GONE);
		//Aapt.init(getActivity());

		view.postDelayed(new Runnable() {
			@Override
			public void run() {
				if(mIsAlive)
				{
					view.postDelayed(this,2000);
					PublicThreadPool.getPublicThreadPool().execute(new Runnable() {
						@Override
						public void run() {
							if(needRefresh()){
								view.post(new Runnable() {
									@Override
									public void run() {
										if(mIsAlive)
											refresh();
									}
								});
							}
						}
					});
				}
			}
		},1000);
		return view;
	}
	
	@Override
	public void onHiddenChanged(boolean hidden) {
		super.onHiddenChanged(hidden);
		refCopyOrCut();
	}
	
	@Override
	public void onResume() {
		super.onResume();
		refCopyOrCut();
	};
	
	private static CopyOrCutOption mCopyOrCutOption = null;
	public void refCopyOrCut(){
		if(mCopyOrCutOption==null){
			mPasteLayout.setVisibility(View.GONE);
		}else{
			mPasteLayout.setVisibility(View.VISIBLE);
		}
	}
	
	Boolean mStopFlag=false;
	public void ShowInfo(final File[] files)
	{
		if(files==null)
			return ;
		final AlertDialog.Builder dialog=new Builder(getActivity());
		dialog.setTitle(R.string.info);
		dialog.setMessage(R.string.loading);
				FilesInfo fileinInfo=FileWork.getFilesInfo(files,mStopFlag=false);
				String str="";
				str+=getActivity().getString(R.string.dirs)+fileinInfo.mDirNum+"\n";
				str+=getActivity().getString(R.string.files)+fileinInfo.mFileNum+"\n";
				str+=getActivity().getString(R.string.unkonw)+fileinInfo.mUnknowNum+"\n";
				str+=getActivity().getString(R.string.size)+FileWork.GetSize(fileinInfo.mSize)+"\n";

				dialog.setMessage(str);
		dialog.setPositiveButton(R.string.i_know, new Dialog.OnClickListener() {
			@Override
			public void onClick(DialogInterface arg0, int arg1) {
				mStopFlag = true;
			}
		});
		dialog.show();
		
	}
	public void sureDeleteFile(final File[] files)
	{
		if(files==null)
			return ;
		AlertDialog.Builder dialog=new Builder(getActivity());
		dialog.setTitle(R.string.confirm_delete);
		String msg=getActivity().getText(R.string.confirm_delete)+"\n";
		for(int i=0;i<files.length;i++)
		{
			msg+=files[i].getName()+"\n";
		}
		msg+="?";
		dialog.setMessage(msg);
		dialog.setNegativeButton(android.R.string.cancel, new Dialog.OnClickListener() {
			@Override
			public void onClick(DialogInterface arg0, int arg1) {
			}
		});
		dialog.setPositiveButton(android.R.string.ok, new Dialog.OnClickListener() {
			@SuppressLint("ShowToast")
			@Override
			public void onClick(DialogInterface arg0, int arg1) {
				int successNum=0,failedNum=0;
				for(int i=0;i<files.length;i++)
				{
					if(FileWork.deleteFile(files[i]))
						successNum++;
					else
						failedNum++;
				}
				if(mToast!=null)
					mToast.cancel();
				mToast=Toast.makeText(getActivity(), 
						getActivity().getText(R.string.delete)+
						"\n"+
						getActivity().getText(R.string.success)+successNum+"\n"+
						getActivity().getText(R.string.fail)+failedNum, Toast.LENGTH_LONG);
				mToast.show();
				mFileListAdapter.Refresh();
			}
		});
		dialog.show();
	}
	@Override
	public boolean onItemLongClick(AdapterView<?> arg0, View arg1, int arg2,long arg3) {
		mFileListAdapter.Setect(arg2);
		return true;
	}
	@SuppressLint({ "ShowToast", "NewApi" })
	@Override
	public void onItemClick(AdapterView<?> arg0, View arg1, int arg2, long arg3) {
		File file=new File( ((File)arg0.getItemAtPosition(arg2)).getPath() );
		if(mFileListAdapter.IsSetecting())
		{
			mFileListAdapter.Setect(arg2);
			return ;
		}
		if(!file.canRead())
		{
			Log.i("fbr", "can not read!");
			if(mToast!=null)
				mToast.cancel();
			mToast=Toast.makeText(getActivity(), R.string.file_unreadable, 1000);
			mToast.show();
			ChangeMod(file);
		}
		if(file.isFile())
		{
			if(mOpenListener==null)
				Open.openFile(getActivity(), file);
			else{
				if(!mOpenListener.onOpen(file))
					Open.openFile(getActivity(), file);
			}
		}
		if(file.isDirectory())
		{
			if(mFileListAdapter.OpenPath(file))
			{
				if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.GINGERBREAD) {  
					mHistroyPos.push(mPos);
				}
				mListView.setSelection(0);
			}
		}
	}
	@SuppressLint("NewApi")
	public boolean onBackPressed()
	{
		if(mFileListAdapter.IsSetecting())
		{
			mFileListAdapter.StopSelect();
			return true;
		}
		boolean ret= mFileListAdapter.BackPath();
		if(ret)
		{
			if(mHistroyPos!=null&&!mHistroyPos.isEmpty())
			{
				Integer pos=0;
				if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.GINGERBREAD) {  
					pos=mHistroyPos.pop();
				}
				Log.i("fbr", "111");
				if(pos!=null)
				{
					mListView.setSelection(pos);
				}
			}
		}
		return ret;
	}
	
	@Override
	public void onScroll(AbsListView arg0, int arg1, int arg2, int arg3) {
	}
	@Override
	public void onScrollStateChanged(AbsListView arg0, int arg1) {
		mPos=arg0.getFirstVisiblePosition();
	}
	public void ChangeMod(File file)
	{
		String sh;
		OutputStream out;
		if(mRuntime==null)
		{
			mRuntime=Runtime.getRuntime();
		}
		try 
		{
			mProcess= mRuntime.exec("su\n");
			out=mProcess.getOutputStream();
			sh="\ncd '"+file.getParent()+"'\nchmod 777 '"+file.getName()+"'\n";
			Log.i("fbr", sh );
			out.write(sh.getBytes());
			out.flush();
			out.close();
			try {
				mProcess.waitFor();
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
			
		} catch (IOException e)
		{
			e.printStackTrace();
		}
	}
	@Override
	public void onPathChange(File path) {
		mTextView.setText(path.getPath());
	}
	@SuppressLint("DefaultLocale")
	@Override
	public void onCheckChange() {
		File []files=mFileListAdapter.GetSelectedFiles();
		EditText editText = (EditText)mRenameLayout.findViewById(R.id.nameedit);
		if(files==null)
		{
			mFileListAdapter.StopSelect();
			mRenameLayout.setVisibility(View.INVISIBLE);
			closeInputMethod(editText);
			mOptionLayout.setVisibility(View.GONE);
			{
				mDecodeButton.setVisibility(View.GONE);
				mBuildButton.setVisibility(View.GONE);
				mSignerButton.setVisibility(View.GONE);
			}
		}
		else
		if(files!=null && files.length==1)
		{
			mRenameLayout.findViewById(R.id.donerename).setTag(files[0]);
			editText.setText(files[0].getName());
			mRenameLayout.setVisibility(View.VISIBLE);
			if(files[0].isFile())
				mOptionLayout.findViewById(R.id.share).setVisibility(View .VISIBLE);
			else
				mOptionLayout.findViewById(R.id.share).setVisibility(View .INVISIBLE);
			mOptionLayout.setVisibility(View.VISIBLE);
			mDecompileLayout.setVisibility(View.VISIBLE);
			if(files[0].isDirectory())
			{
				mDecodeButton.setVisibility(View.GONE);
				mBuildButton.setVisibility(View.VISIBLE);
				mSignerButton.setVisibility(View.GONE);
			}
			if(files[0].isFile()&&files[0].getName().toLowerCase().endsWith(".apk"))
			{
				mDecodeButton.setVisibility(View.VISIBLE);
				mBuildButton.setVisibility(View.GONE);
				mSignerButton.setVisibility(View.VISIBLE);
			}
		}
		else
		{
			mRenameLayout.setVisibility(View.INVISIBLE);
			closeInputMethod(editText);
			mOptionLayout.findViewById(R.id.share).setVisibility(View .INVISIBLE);
			mOptionLayout.setVisibility(View.VISIBLE);
			{
				mDecodeButton.setVisibility(View.GONE);
				mBuildButton.setVisibility(View.GONE);
				mSignerButton.setVisibility(View.GONE);
			}
		}
		mDecompileLayout.setVisibility(View.GONE);
		mFileListAdapter.notifyDataSetChanged();
	}
	@SuppressLint("ShowToast")
	@Override
	public void onClick(View arg0) {
		File []files=null;
		switch (arg0.getId()) {
		case R.id.copy:
			files=mFileListAdapter.GetSelectedFiles();
			mCopyOrCutOption = new CopyOrCutOption(files, true);
			mFileListAdapter.StopSelect();
			refCopyOrCut();
			break;
		case R.id.cut:
			files=mFileListAdapter.GetSelectedFiles();
			mCopyOrCutOption = new CopyOrCutOption(files, false);
			mFileListAdapter.StopSelect();
			refCopyOrCut();
			break;
		case R.id.paste:
			if(mCopyOrCutOption!=null){
				CopyOrCutInfo copyInfo=mCopyOrCutOption.Do(mFileListAdapter.mPath,false);//((CopyOrCutOption)mPasteLayout.getTag()).Do(mFileListAdapter.mPath,false);
				mCopyOrCutOption = null;
				if(mToast!=null)
					mToast.cancel();
				mToast=Toast.makeText(getActivity(), getText(R.string.success).toString()+copyInfo.mSuccessNum+"\n"+
					getActivity().getText(R.string.fail)+copyInfo.mFailedNum+"\n"+
					getActivity().getText(R.string.exist)+copyInfo.mExistNum, 1000);
				mToast.show();
				mFileListAdapter.Refresh();
				refCopyOrCut();
			}
		case R.id.cancel:
			mCopyOrCutOption = null;
			refCopyOrCut();
			break;
		case R.id.share:
			files=mFileListAdapter.GetSelectedFiles();
			Share.ShareFile(getActivity(), files[0]);
			break;
		case R.id.delete:
			files=mFileListAdapter.GetSelectedFiles();
			sureDeleteFile(files);
			break;
		case R.id.info:
			files=mFileListAdapter.GetSelectedFiles();
			ShowInfo(files);
			break;
		case R.id.selectall:
			mFileListAdapter.SelectAll();
			break;
		case R.id.donerename:
			EditText nameEdit=(EditText)( mRenameLayout.findViewById(R.id.nameedit) );
			Log.i("fbr", "arg0 "+arg0);
			File file=(File)arg0.getTag();
			if(file.renameTo(new File(file.getParent()+"/"+nameEdit.getText())))
			{
				Toast.makeText(getActivity(), getText(R.string.rename_has_succeed), 1000).show();
				mFileListAdapter.Refresh();
				closeInputMethod(nameEdit);
			}
			else
			{
				Toast.makeText(getActivity(), getText(R.string.rename_has_failed), 1000).show();
			}
			break;
		default:
			break;
		}
		
	}
	
	@Override
	public void onDestroyView() {
		mIsAlive=false;
		super.onDestroyView();
	}
	private void closeInputMethod(EditText editView) {
	    InputMethodManager imm = (InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
	    boolean isOpen = imm.isActive();
	    if (isOpen) {
	        imm.hideSoftInputFromWindow(editView.getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);
	        
	    }
	}

	public Exception decode(String fileIn,String pathOut)
	{
		try {
			//String args[]=new String[]{"d","-f","--frame-path",Environment.getExternalStorageDirectory().getPath(),"-o",pathOut,fileIn};
			//brut.apktool.Main.main(args);
		} catch (Exception e) {
			e.printStackTrace();
			return e;
		} catch(Error e)
		{
			e.printStackTrace();
			return new Exception(e.toString());
		}
		return null;
	}
	
	public Exception build(String pathIn,String fileOut)
	{
		//Log.i(TAG, "build:"+pathIn+" "+fileOut+" aapt:"+Aapt.getPath());
		String key="user.home";
		String oldValue=System.getProperty(key);
		try {
			String value=Environment.getExternalStorageDirectory().getPath();
			System.setProperty(key, value);
			//String args[]=new String[]{"b","-a",Aapt.getPath(),pathIn,fileOut};
			//brut.apktool.Main.main(args);
			
		} catch (Exception e) {
			e.printStackTrace();
			return e;
		} catch(Error e)
		{
			e.printStackTrace();
			return new Exception(e.toString());
		}
		finally{
			System.setProperty(key, oldValue);
		}
		return null;
	}
	
	public void setOnOpenListener(OnOpenListener l){
		mOpenListener=l;
	}

	public boolean newFile(){
		if(mFileListAdapter.mPath==null)
			return false;
		for(int i=0;i<1000;i++){
			File file = new File(mFileListAdapter.mPath.getPath()+File.separatorChar+(i==0?"newfile.c":"newfile("+i+").c"));
			if(file.isDirectory()||file.isFile())
				continue;
			try {
				if(file.createNewFile())
				{
					refresh();
					selectFile(file);
					return true;
				}
			} catch (IOException e) {
				e.printStackTrace();
			}
			
		}
		return false;
	}
	
	public boolean newDir(){
		if(mFileListAdapter.mPath==null)
			return false;
		for(int i=0;i<1000;i++){
			File file = new File(mFileListAdapter.mPath.getPath()+File.separatorChar+(i==0?"newdir":"newdir("+i+")"));
			if(file.isDirectory()||file.isFile())
				continue;
			if(file.mkdirs())
			{
				refresh();
				selectFile(file);
				return true;
			}
			
		}
		return false;
	}

	public void refresh(){
		mFileListAdapter.Refresh();
	}
	
	public boolean needRefresh(){
		return mFileListAdapter.needRefresh();
	}
	
	public void selectFile(File file){
		mFileListAdapter.StopSelect();
		mFileListAdapter.Setect(file);
	}
	
	public String getPath(){
		return mFileListAdapter.mPath.getPath();
	}
	
	public File[] getSelectedFiles()
	{
		return mFileListAdapter.GetSelectedFiles();
	}
	
	public void setDefaultPath(String path){
		mDefaultPath = path;
	}
}
