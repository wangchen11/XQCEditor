package person.wangchen11.filebrowser;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.Iterator;
import java.util.LinkedList;

import person.wangchen11.drawable.RectDrawable;
import person.wangchen11.util.PublicThreadPool;
import person.wangchen11.window.ext.Setting;
import person.wangchen11.xqceditor.R;

import android.annotation.SuppressLint;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.os.Build;
import android.os.Environment;
import android.os.Handler;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.ImageView;
import android.widget.TextView;

@SuppressLint({ "SimpleDateFormat", "DefaultLocale" })
public class FileListAdapter extends BaseAdapter implements OnCheckedChangeListener
{
	File mSdcard;
	Handler mHandler=new Handler();
	LayoutInflater mInflater=null;
	Bitmap DirIcon ;
	LinkedList<FileItem> mFiles=new LinkedList<FileItem>(); 
	File mPath=null;
	PathChangeCallBack mCallBack=null;
	boolean mSelecting=false;
	long mLastModified=0;
	int  mLastFilesNumber = 0;
	public FileListAdapter(LayoutInflater inflater) {
		mInflater=inflater;
		mSdcard=Environment.getExternalStorageDirectory();
		DirIcon=BitmapFactory.decodeResource(inflater.getContext().getResources(), R.drawable.folder);
	}
	public void SetCallBack(PathChangeCallBack callBack)
	{
		mCallBack=callBack;
	}
	
	public void Setect(File file)
	{
		int pos=-1;
		Iterator<FileItem> iterator=mFiles.iterator();
		int t=0;
		while(iterator.hasNext())
		{
			FileItem item=iterator.next();
			if(item.mFile.equals(file)){
				pos=t;
				break;
			}
			t++;
		}
		if(pos==-1)
			return ;
		Setect(pos);
	}
	
	public void Setect(int pos)
	{
		FileItem fileItem=mFiles.get(pos);
		fileItem.mIsClick=!fileItem.mIsClick;
		mSelecting=true;
		if(mCallBack!=null)
			mCallBack.onCheckChange();
		this.notifyDataSetChanged();
	}
	public boolean SelectAll()
	{
		boolean canSelecte=false;
		for(int i=0;i<mFiles.size();i++)
		{
			if(!mFiles.get(i).mIsClick)
				canSelecte=true;
		}
		for(int i=0;i<mFiles.size();i++)
		{
			mFiles.get(i).mIsClick=canSelecte;
		}
		if(mCallBack!=null)
			mCallBack.onCheckChange();
		return canSelecte;
	}
	public boolean IsSetecting()
	{
		return mSelecting;
	}
	public void StopSelect()
	{
		for(int i=0;i<mFiles.size();i++)
			mFiles.get(i).mIsClick=false;
		if(mSelecting)
		{
			mSelecting=false;
			if(mCallBack!=null)
				mCallBack.onCheckChange();
		}
		this.notifyDataSetChanged();
	}
	public File[] GetSelectedFiles()
	{
		File[] files=null;
		LinkedList<File> filelist=new LinkedList<File>();
		for(int i=0;i<mFiles.size();i++)
		{
			FileItem fileItem=mFiles.get(i);
			if(fileItem.mIsClick)
				filelist.add(fileItem.mFile);
		}
		if(filelist.size()>0)
		{
			files=new File[filelist.size()];
			filelist.toArray(files);
		}
		return files;
	}
	public boolean BackPath()
	{
		boolean ret=true;
		File file=mPath.getParentFile();
		if(file==null||!file.isDirectory())
			return false;
		ret=OpenPath(file);
		if(mCallBack!=null)
			mCallBack.onCheckChange();
		return ret;
	}
	
	public boolean Refresh()
	{
		boolean ret= OpenPath(mPath);
		if(mCallBack!=null)
			mCallBack.onCheckChange();
		return ret;
	}
	
	
	
	public boolean needRefresh()
	{
		if((mLastModified==mPath.lastModified()) && (getFileSubFilesNumber(mPath) == mLastFilesNumber) )
		{
			return false;
		}
		return true;
	}
	
	private int getFileSubFilesNumber(File file){
		File []files = file.listFiles();
		if(files!=null)
		{
			return files.length;
		}
		return 0;
	}
	
	public boolean OpenPath(File file)
	{
		Log.i("bsr","open "+file.getName() );
		mPath=file;
		mLastModified=mPath.lastModified();
		
		mLastFilesNumber = getFileSubFilesNumber(file);
				
		if(file.isDirectory())
		{
			mFiles.clear();
			File[] files=file.listFiles();
			boolean hasSdcard=false;
			if(files!=null)
			for(int i=0;i<files.length;i++)
			{
				mFiles.add(new FileItem(files[i]));
				if(files[i].equals(mSdcard))
					hasSdcard=true;
			}
			if(!hasSdcard && mPath.equals(mSdcard.getParentFile())){
				mFiles.add(new FileItem(mSdcard));
			}
			Comparator<FileItem> comparator=new Comparator<FileItem>() {
				@Override
				public int compare(FileItem item0, FileItem item1) {
					File arg0=item0.mFile;
					File arg1=item1.mFile;
					if(arg0.isDirectory())
					{
						if(arg1.isDirectory())
						{
							 return arg0.getName().compareToIgnoreCase(arg1.getName());
						}
						else
						{
							return -1;
						}
					}
					else
					{
						if(arg1.isDirectory())
						{
							return 1;
						}
						else
						{
							return arg0.getName().compareTo(arg1.getName());
						}
					}
				}
			};
			Collections.sort(mFiles, comparator);
			if(mCallBack!=null)
				mCallBack.onPathChange(file);
			this.notifyDataSetChanged();
			return true;
		}
		return false;
	}
	@Override
	public int getCount() {
		return mFiles.size();
	}

	@Override
	public Object getItem(int arg0) {
		return mFiles.get(arg0).mFile;
	}

	@Override
	public long getItemId(int arg0) {
		return arg0;
	}

	@SuppressLint({  "InflateParams", "NewApi"})
	@Override
	public View getView(int pos, View view, ViewGroup arg2) {
		if(view==null)
		{
			view=mInflater.inflate(R.layout.fileitem, null);
		}
		FileItem fileItem=mFiles.get(pos);
		File file=fileItem.mFile;
		TextView textView=(TextView)view.findViewById(R.id.filename);
		textView.setTextColor(Color.argb(0x90, 0, 0, 0));
		textView.setText(file.getName());
		ImageView imageView=(ImageView)view.findViewById(R.id.fileicon);
		Object obj=imageView.getTag();
		if(obj instanceof LoadIconRunnable){
			((LoadIconRunnable)obj).stop();
		}
		
		TextView fileSizeView=(TextView)view.findViewById(R.id.filesize);
		
		imageView.setImageDrawable(new RectDrawable(DirIcon.getWidth(), DirIcon.getHeight(), Color.DKGRAY));
		/*
		if(fileItem.mIcon==null)
			fileItem.mIcon=FileIcon.getFileIconId(mInflater.getContext(),file,DirIcon.getWidth());
		imageView.setImageBitmap(fileItem.mIcon);
		*/
		Bitmap bmp=FileIcon.getFileIconIdQuick(mInflater.getContext(),file,DirIcon.getWidth());
		if(bmp!=null)
		{
			imageView.setImageBitmap(bmp);
		}
		else
		{
			LoadIconRunnable loadIconRunnable=new LoadIconRunnable(fileItem.mFile,imageView,mHandler);
			imageView.setTag(loadIconRunnable);
			PublicThreadPool.getPublicThreadPool().execute(loadIconRunnable);
		}
		
		if(file.isDirectory())
		{
			String size="";
			fileSizeView.setText(size);
		}
		else
		//if(file.isFile())
		{
			fileSizeView.setText(FileWork.GetSize(file.length()));
		}

		if(Build.VERSION.SDK_INT>=Build.VERSION_CODES.HONEYCOMB)
		if(file.isHidden())
		{
			view.setAlpha(0.6f);
		}
		else
		{
			view.setAlpha(1.0f);
		}
		int gray = Color.argb(0x6a, 0, 0, 0);
		fileSizeView.setTextColor(gray);
		TextView fileDateView=(TextView)view.findViewById(R.id.filedate);
		SimpleDateFormat mFormat=new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		fileDateView.setText(mFormat.format(new Date(file.lastModified())));
		fileDateView.setTextColor(gray);
		TextView fileRWView=(TextView)view.findViewById(R.id.readwrite);
		fileRWView.setText(""+(file.canRead()?'r':'-')+(file.canWrite()?'w':'-')+"  ");
		fileRWView.setTextColor(gray);
		CheckBox checkBox=(CheckBox)view.findViewById(R.id.selectbox);
		checkBox.setOnCheckedChangeListener(this);
		checkBox.setTag(fileItem);
		if(mSelecting)
		{
			checkBox.setVisibility(View.VISIBLE);
			checkBox.setChecked(fileItem.mIsClick);
		}
		else
		{
			checkBox.setVisibility(View.GONE);
		}
		Setting.applySettingConfigToAllView(view);
		return view;
	}
	@Override
	public void onCheckedChanged(CompoundButton arg0, boolean arg1) {
		FileItem fileItem=(FileItem)arg0.getTag();
		fileItem.mIsClick=arg1;
		if(mCallBack!=null)
			mCallBack.onCheckChange();
	}
	
	private class LoadIconRunnable implements Runnable{
		File file;
		ImageView imageView;
		Handler handler;
		boolean isAlive=true;
		SetImageViewRunnable imageViewRunnable=null;
		public LoadIconRunnable(File file,ImageView imageView,Handler handler) {
			this.file=file;
			this.imageView=imageView;
			this.handler=handler;
		}
		@Override
		public void run() {
			if(isAlive){
				try {
					Bitmap bmp=FileIcon.getFileIconId(mInflater.getContext(),file,DirIcon.getWidth());
					if(isAlive){
						imageViewRunnable=new SetImageViewRunnable(imageView,bmp);
						handler.post(imageViewRunnable);
					}
				} catch (OutOfMemoryError e) {
					e.printStackTrace();
				}
			}
		}
		
		public void stop(){
			isAlive=false;
			if(imageViewRunnable!=null)
				imageViewRunnable.stop();
		}
	}
	
	private class SetImageViewRunnable implements Runnable{
		ImageView imageView;
		Bitmap bmp;
		boolean isAlive=true;
		public SetImageViewRunnable(ImageView imageView,Bitmap bmp){
			this.imageView=imageView;
			this.bmp=bmp;
		}

		@Override
		public void run() {
			if(isAlive)
				this.imageView.setImageBitmap(bmp);
		}
		
		public void stop(){
			isAlive=false;
		}
	}
}

class FileItem
{
	File mFile;
	//Bitmap mIcon=null;
	boolean mIsClick;
	public FileItem(File file) 
	{
		mFile=file;
		mIsClick=false;
	}
}

interface PathChangeCallBack
{
	public void onPathChange(File path);
	public void onCheckChange();
}
