package person.wangchen11.filebrowser;

import java.io.File;
import java.util.concurrent.locks.ReentrantLock;

import person.wangchen11.bitmap.ThumbnailFactory;
import person.wangchen11.xqceditor.R;


import android.annotation.SuppressLint;
import android.content.Context;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.PixelFormat;
import android.graphics.drawable.Drawable;
import android.media.ThumbnailUtils;
import android.support.v4.util.LruCache;
import android.util.Log;

public class FileIcon {
	public final static int mDefaultSzie=36;
	public static int mBaseSize=mDefaultSzie;
	private static ReentrantLock mLock=new ReentrantLock();
	private static LruCache<String, Bitmap> mCache=new LruCache<String, Bitmap>((int) (Runtime.getRuntime().maxMemory() / 1024 / 32)){
		protected int sizeOf(String key, Bitmap bitmap) {
            return bitmap.getRowBytes() * bitmap.getHeight() / 1024;
		};
	};

	@SuppressLint("DefaultLocale") public static Bitmap getFileIconIdQuick(Context context,File file,int baseSize)
	{
		Bitmap bmp=null;
		String dirKey="?dir";
		String fileKey="?file";
		if(baseSize>mDefaultSzie)
			mBaseSize=baseSize;
		if(baseSize<=0)
			baseSize=mBaseSize;
		Resources res=context.getResources();
		if(file.isDirectory())
		{
			if((bmp=mCache.get(dirKey))==null)
			{
				bmp = BitmapFactory.decodeResource(res, R.drawable.folder);
				mLock.lock();
				mCache.put(dirKey, bmp);
				mLock.unlock();
			}
			return bmp;
		}
		String str,fileName;
		fileName=file.getName();
		int index=fileName.lastIndexOf('.');

		if((bmp=mCache.get(file.getPath()))!=null)
		{
			return bmp;
		}
		
		if(index>0)
		{
			bmp=null;
			str=fileName.substring(index);
			if(str.compareToIgnoreCase(".bmk")==0)
			{
				bmp = BitmapFactory.decodeResource(res, R.drawable.file_bookmark);
			} else
			if(	str.compareToIgnoreCase(".xml")==0	||
				str.compareToIgnoreCase(".html")==0	||
				str.compareToIgnoreCase(".c")==0	||
				str.compareToIgnoreCase(".h")==0	||
				str.compareToIgnoreCase(".cpp")==0	||
				str.compareToIgnoreCase(".php")==0	||
				str.compareToIgnoreCase(".sh")==0 )
			{
				bmp = BitmapFactory.decodeResource(res, R.drawable.file_code);
			} else
			if(str.compareToIgnoreCase(".elf")==0)
			{
				bmp = BitmapFactory.decodeResource(res, R.drawable.file_elf);
			} else
			if(fileName.toLowerCase().endsWith(".qplug.zip"))
			{
				bmp = BitmapFactory.decodeResource(res, R.drawable.file_plug);
			} else
			if(str.compareToIgnoreCase(".qtheme")==0)
			{
				bmp = BitmapFactory.decodeResource(res, R.drawable.theme);
			} else
			if(str.compareToIgnoreCase(".xls")==0)
			{
				bmp = BitmapFactory.decodeResource(res, R.drawable.file_excel);
			} else
			if(str.compareToIgnoreCase(".apk")==0)
			{
				bmp=LoadApkFromFile(context, file, baseSize);
				if(bmp==null)
					bmp = BitmapFactory.decodeResource(res, R.drawable.file_exe);
			} else
			if(str.compareToIgnoreCase(".ttf")==0)
			{
				bmp = BitmapFactory.decodeResource(res, R.drawable.file_font);
			} else
			if(str.compareToIgnoreCase(".lnk")==0)
			{
				bmp = BitmapFactory.decodeResource(res, R.drawable.file_link);
			} else
			if(str.compareToIgnoreCase(".log")==0)
			{
				bmp = BitmapFactory.decodeResource(res, R.drawable.file_note);
			} else
			if(str.compareToIgnoreCase(".pdf")==0)
			{
				bmp = BitmapFactory.decodeResource(res, R.drawable.file_pdf);
			} else
			if(str.compareToIgnoreCase(".ppt")==0)
			{
				bmp = BitmapFactory.decodeResource(res, R.drawable.file_powerpoint);
			} else
			if(str.compareToIgnoreCase(".sound")==0)
			{
				bmp = BitmapFactory.decodeResource(res, R.drawable.file_sound);
			} else
			if(str.compareToIgnoreCase(".txt")==0)
			{
				bmp = BitmapFactory.decodeResource(res, R.drawable.file_text);
			} else
			if(str.compareToIgnoreCase(".doc")==0||str.compareToIgnoreCase(".docx")==0)
			{
				bmp = BitmapFactory.decodeResource(res, R.drawable.file_word);
			} else
			if(str.compareToIgnoreCase(".bmp")==0||str.compareToIgnoreCase(".png")==0||str.compareToIgnoreCase(".jpg")==0||str.compareToIgnoreCase(".jpeg")==0||str.compareToIgnoreCase(".gif")==0)
			{
				return null;
				/*
				if(bmp==null)
					bmp = BitmapFactory.decodeResource(res, R.drawable.file_picture);
					*/
			} else
			if(str.compareToIgnoreCase(".zip")==0||str.compareToIgnoreCase(".rar")==0||str.compareToIgnoreCase(".gz")==0||str.compareToIgnoreCase(".gzip")==0||str.compareToIgnoreCase(".7z")==0)
			{
				bmp = BitmapFactory.decodeResource(res, R.drawable.file_zip);
			}
			if(bmp!=null){

				mLock.lock();
				mCache.put(file.getPath(), bmp);
				mLock.unlock();
				return bmp;
			}
		}
		if((bmp=mCache.get(fileKey))==null)
		{
			bmp = BitmapFactory.decodeResource(res, R.drawable.file_empty);

			mLock.lock();
			if(bmp!=null)
				mCache.put(fileKey, bmp);
			mLock.unlock();
		}
		return bmp;
	}
	
	public static Bitmap getFileIconId(Context context,File file,int baseSize)
	{
		Bitmap bmp=null;
		String dirKey="?dir";
		String fileKey="?file";
		if(baseSize>mDefaultSzie)
			mBaseSize=baseSize;
		if(baseSize<=0)
			baseSize=mBaseSize;
		Resources res=context.getResources();
		if(file.isDirectory())
		{
			if((bmp=mCache.get(dirKey))==null)
			{
				bmp = BitmapFactory.decodeResource(res, R.drawable.folder);
				mLock.lock();
				mCache.put(dirKey, bmp);
				mLock.unlock();
			}
			return bmp;
		}
		String str,fileName;
		fileName=file.getName();
		int index=fileName.lastIndexOf('.');

		if((bmp=mCache.get(file.getPath()))!=null)
		{
			return bmp;
		}
		
		if(index>0)
		{
			bmp=null;
			str=fileName.substring(index);
			if(str.compareToIgnoreCase(".bmk")==0)
			{
				bmp = BitmapFactory.decodeResource(res, R.drawable.file_bookmark);
			} else
			if(str.compareToIgnoreCase(".xml")==0||str.compareToIgnoreCase(".html")==0||str.compareToIgnoreCase(".c")==0||str.compareToIgnoreCase(".h")==0||str.compareToIgnoreCase(".cpp")==0||str.compareToIgnoreCase(".php")==0)
			{
				bmp = BitmapFactory.decodeResource(res, R.drawable.file_code);
			} else
			if(str.compareToIgnoreCase(".xls")==0)
			{
				bmp = BitmapFactory.decodeResource(res, R.drawable.file_excel);
			} else
			if(str.compareToIgnoreCase(".apk")==0)
			{
				bmp=LoadApkFromFile(context, file, baseSize);
				if(bmp==null)
					bmp = BitmapFactory.decodeResource(res, R.drawable.file_exe);
			} else
			if(str.compareToIgnoreCase(".ttf")==0)
			{
				bmp = BitmapFactory.decodeResource(res, R.drawable.file_font);
			} else
			if(str.compareToIgnoreCase(".lnk")==0)
			{
				bmp = BitmapFactory.decodeResource(res, R.drawable.file_link);
			} else
			if(str.compareToIgnoreCase(".log")==0)
			{
				bmp = BitmapFactory.decodeResource(res, R.drawable.file_note);
			} else
			if(str.compareToIgnoreCase(".pdf")==0)
			{
				bmp = BitmapFactory.decodeResource(res, R.drawable.file_pdf);
			} else
			if(str.compareToIgnoreCase(".ppt")==0)
			{
				bmp = BitmapFactory.decodeResource(res, R.drawable.file_powerpoint);
			} else
			if(str.compareToIgnoreCase(".sound")==0)
			{
				bmp = BitmapFactory.decodeResource(res, R.drawable.file_sound);
			} else
			if(str.compareToIgnoreCase(".txt")==0)
			{
				bmp = BitmapFactory.decodeResource(res, R.drawable.file_text);
			} else
			if(str.compareToIgnoreCase(".doc")==0||str.compareToIgnoreCase(".dox")==0)
			{
				bmp = BitmapFactory.decodeResource(res, R.drawable.file_word);
			} else
			if(str.compareToIgnoreCase(".bmp")==0||str.compareToIgnoreCase(".png")==0||str.compareToIgnoreCase(".jpg")==0||str.compareToIgnoreCase(".jpeg")==0||str.compareToIgnoreCase(".gif")==0)
			{
				bmp=ThumbnailFactory.getImageThumbnail(file.getPath(), baseSize, baseSize);//LoadFromFile(file,baseSize);
				if(bmp==null)
					bmp = BitmapFactory.decodeResource(res, R.drawable.file_picture);
			} else
			if(str.compareToIgnoreCase(".zip")==0||str.compareToIgnoreCase(".rar")==0||str.compareToIgnoreCase(".gz")==0||str.compareToIgnoreCase(".gzip")==0||str.compareToIgnoreCase(".7z")==0)
			{
				bmp = BitmapFactory.decodeResource(res, R.drawable.file_zip);
			}
			if(bmp!=null){

				mLock.lock();
				mCache.put(file.getPath(), bmp);
				mLock.unlock();
				return bmp;
			}
		}
		if((bmp=mCache.get(fileKey))==null)
		{
			bmp = BitmapFactory.decodeResource(res, R.drawable.file_empty);

			mLock.lock();
			if(bmp!=null)
				mCache.put(fileKey, bmp);
			mLock.unlock();
		}
		return bmp;
	}
	public static Bitmap LoadFromFile(File file,int baseSize)
	{
		Bitmap bmp=null;
		bmp=BitmapFactory.decodeFile(file.getPath());
		if(bmp==null)
			return null;
		bmp=ThumbnailUtils.extractThumbnail(bmp, baseSize, baseSize);
		return bmp;
	}
	public static Drawable getApkIcon(Context context, String apkPath) {
        PackageManager pm = context.getPackageManager();
        PackageInfo info = pm.getPackageArchiveInfo(apkPath,
                PackageManager.GET_ACTIVITIES);
        if (info != null) {
            ApplicationInfo appInfo = info.applicationInfo;
            appInfo.sourceDir = apkPath;
            appInfo.publicSourceDir = apkPath;
            try {
                return appInfo.loadIcon(pm);
            } catch (OutOfMemoryError e) {
                Log.e("ApkIconLoader", e.toString());
            }
        }
        return null;
    }
	public static Bitmap LoadApkFromFile(Context context,File file,int baseSize)
	{
		Bitmap bmp=null; 
        Drawable drawable=getApkIcon(context, file.getPath());
        if(drawable==null)
        	return null;
        int w,h;
        w=drawable.getIntrinsicWidth();
        h=drawable.getIntrinsicHeight();
        Bitmap.Config config=drawable.getOpacity() != PixelFormat.OPAQUE ? Bitmap.Config.ARGB_8888 : Bitmap.Config.RGB_565;
        bmp=Bitmap.createBitmap(w,h,config);
        Canvas canvas = new Canvas(bmp);
        drawable.setBounds(0, 0, w, h);
        drawable.draw(canvas);
		bmp=ThumbnailUtils.extractThumbnail(bmp, baseSize, baseSize);
        return bmp;
	}
}
