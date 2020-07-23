package person.wangchen11.util;

import android.content.Context;
import android.util.Log;

import com.file.zip.ZipEntry;
import com.file.zip.ZipFile;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Enumeration;

public class FileUtil {
	protected final static String TAG="FileUtil";
	private final static int BUFFER=4096; 
	public static String getAll(String fileName, String charSet){
		File file=new File(fileName);
		try {
			FileInputStream fileInputStream=new FileInputStream(file);
			try {
				byte data[]=new byte[(int) file.length()];
				try {
					int readLen=fileInputStream.read(data);
					if(charSet!=null)
						return new String(data,0,readLen,charSet);
					return new String(data,0,readLen);
				} catch (IOException e) {
					e.printStackTrace();
				}
			} catch (Error e) {
			}
			finally{
					try {
						fileInputStream.close();
					} catch (IOException e) {
						e.printStackTrace();
					}
			}
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}
		return "";
	}
	
	public static boolean setFileExecutable(File file)
	{
		return file.setExecutable(true, true);
	}
	
	public static void setFileAllChildsExecutable(File file)
	{
		if(file.isFile())
		{
			setFileExecutable(file);
			return ;
		}
		if(file.isDirectory())
		{
			File[]files = file.listFiles();
			if(files!=null)
			{
				for(File item : files)
				{
					setFileAllChildsExecutable(item);
				}
			}
		}
	}
	

	public static int freeZip(Context context, String assetsName, String pathTo,FreeZipFilter filter){
		Log.i(TAG, "freeZip:"+assetsName);
		File file = null;
		try {
			file = File.createTempFile("tmp", ".zip");
			file.deleteOnExit();
		} catch (IOException e) {
			e.printStackTrace();
			return 0;
		}
		if(!freeFile(context,assetsName,file.getAbsolutePath())){
			file.delete();
			return 0;
		}
		
		int ret = freeZip(file.getAbsolutePath(),pathTo,filter);
		file.delete();
		return ret;
	}

	public static int freeZip(InputStream inputStream, String pathTo,FreeZipFilter filter){
		File file = null;
		try {
			file = File.createTempFile("tmp", ".zip");
			file.deleteOnExit();
		} catch (IOException e) {
			e.printStackTrace();
			return 0;
		}

		byte data[] = new byte[BUFFER];
		int readLen = 0;
		try {
			FileOutputStream fileOutputStream = new FileOutputStream(file);
			while( (readLen=inputStream.read(data))>0 ){
				fileOutputStream.write(data,0,readLen);
			}
			fileOutputStream.close();
		} catch (IOException e) {
			e.printStackTrace();
			file.delete();
			return 0;
		}
		
		int ret = freeZip(file.getAbsolutePath(),pathTo,filter);
		file.delete();
		return ret;
	}

	public static int freeZip(String fromFile, String pathTo,FreeZipFilter filter){
		Log.i(TAG, "freeZip:"+fromFile);
		if(pathTo!=null)
			pathTo+= File.separatorChar;
		File file = new File(fromFile);
		int ret = 0;
		
		try {
			ZipFile zipFile = new ZipFile(file,"GBK");
			byte data[] = new byte[BUFFER];
			Enumeration<ZipEntry> enumeration = zipFile.getEntries();
			while(enumeration.hasMoreElements()){
				ZipEntry entry = enumeration.nextElement();
				Log.i(TAG, "Unzip:" + entry);
				int count;
				String strEntry = entry.getName();

				File entryFile = new File(pathTo + strEntry);
				File entryDir = new File(entryFile.getParent());
				if(filter!=null && filter.needFree(strEntry,entryFile)){
					continue;
				}

				entryDir.mkdirs();
				if (!entryDir.exists()) {
					Log.i(TAG, "mkdirs");
					if(!entryDir.mkdirs())
						Log.i(TAG, "mkdirs failed :"+entryDir.getAbsolutePath());;
				}
				if(entry.isDirectory())
				{
					entryFile.mkdirs();
				}else
				{
					FileOutputStream fos = new FileOutputStream(entryFile);
					BufferedOutputStream dest = new BufferedOutputStream(fos, BUFFER);
					InputStream zis = zipFile.getInputStream(entry);
					while ((count = zis.read(data, 0, BUFFER)) != -1) {
						dest.write(data, 0, count);
					}
					dest.flush();
					dest.close();
				}
				ret++;
			}
			zipFile.close();
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
		}
		return ret;
	}
	
	public static boolean freeFile(Context context, String assetsName, String fileTo)
	{
		try {
			OutputStream outputStream=new FileOutputStream(new File(fileTo));
			try {
				InputStream inputStream=context.getAssets().open(assetsName);
				byte data[] = new byte[4096];
				int readLen=0;
				while( ( readLen=inputStream.read(data))>0 )
				{
					outputStream.write(data,0,readLen);
				}
				inputStream.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
			try {
				outputStream.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		} catch (FileNotFoundException e) {
			e.printStackTrace();
			return false;
		}
		return true;
	}

	public static void deleteDir(File dir) {
		if(dir.isDirectory()){
			File[] files = dir.listFiles();
			if(files!=null){
				for(File file : files){
					deleteDir(file);
				}
			}
		}
		dir.delete();
	}

	public interface FreeZipFilter {
		boolean needFree(String entryName,File to);
	}
}
