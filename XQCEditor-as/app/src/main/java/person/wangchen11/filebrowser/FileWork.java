package person.wangchen11.filebrowser;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;

import android.util.Log;

public class FileWork {

	public static boolean deleteFile(File file)
	{
		if(file.isDirectory())
		{
			File []files=file.listFiles();
			if(files!=null)
			{
				for(int i=0;i<files.length;i++)
					deleteFile(files[i]);
			}
		}
		return file.delete();
	}
	public static FilesInfo getFileInfo(File file,Boolean stopFlag)
	{
		FilesInfo mFilesInfo=new FilesInfo();
		if(stopFlag)
			return mFilesInfo;
		if(file.isDirectory())
		{
			mFilesInfo.mDirNum++;
			File []files=file.listFiles();
			if(files!=null)
			{
				for(int i=0;i<files.length;i++)
				{
					mFilesInfo.Add(getFileInfo(files[i],stopFlag));
				}
			}
		}
		else
		if(file.isFile())
		{
			mFilesInfo.mFileNum++;
			mFilesInfo.mSize+=file.length();
		}
		else
		{
			mFilesInfo.mUnknowNum++;
		}
		return mFilesInfo;
	}
	public static FilesInfo getFilesInfo(File[] files,Boolean stopFlag)
	{
		FilesInfo mFilesInfo=new FilesInfo();
		if(files==null||stopFlag)
			return mFilesInfo;
		for(int i=0;i<files.length;i++)
		{
			mFilesInfo.Add(getFileInfo(files[i],stopFlag));
		}
		return mFilesInfo;
	}
	public static CopyOrCutInfo CopyFile(File fromFile,File toFile,byte buf[])//公用buf 节省内存 
	{
		CopyOrCutInfo copyOrCutInfo=new CopyOrCutInfo();
		if(toFile.exists())
		{
			copyOrCutInfo.mExistNum++;
		}
		else
		{
			try 
			{
				toFile.createNewFile();
				if(fromFile.isFile()&&fromFile.canRead()&&toFile.canWrite())
				{
					FileInputStream in=new FileInputStream(fromFile);
					FileOutputStream out=new FileOutputStream(toFile);
					int readLen=0;
					while( (readLen=in.read(buf))>0)
					{
						out.write(buf, 0, readLen);
					}
					in.close();
					out.close();
					copyOrCutInfo.mSuccessNum++;
				}
				else
				{
					copyOrCutInfo.mFailedNum++;
				}
			} catch (IOException e) {
				copyOrCutInfo.mFailedNum++;
				e.printStackTrace();
			}
		}
		return copyOrCutInfo;
	}
	public static CopyOrCutInfo CopyFileOrDir(File fromFile,File toPath,byte []buf,Boolean stopFlag)
	{
		CopyOrCutInfo info=new CopyOrCutInfo();
		if(stopFlag)
			return info;
		if(fromFile.isDirectory())
		{
			File path=new File(toPath.getPath()+"/"+fromFile.getName());
			if(!path.exists())
			{
				Log.i("fbr", ""+path.getPath());
				if(path.mkdirs())
					info.mSuccessNum++;
				else
					info.mFailedNum++;
			}
			else
			{
				info.mExistNum++;
			}
			File [] files=fromFile.listFiles();
			if(files!=null)
			for(int i=0;i<files.length&&!stopFlag;i++)
			{
				info.Add(CopyFileOrDir(files[i],path,buf,stopFlag));
			}
		}
		if(fromFile.isFile())
		{
			info.Add(CopyFile(fromFile, new File(toPath.getPath()+"/"+fromFile.getName()), buf));
		}
		return info;
	}
	public static CopyOrCutInfo CopyFiles(File []fromFiles,File toPath,Boolean stopFlag)
	{
		CopyOrCutInfo info=new CopyOrCutInfo();
		if(fromFiles==null || stopFlag )
			return info;
		byte buf[]=new byte[4096];
		for(int i=0;i<fromFiles.length&&!stopFlag;i++)
		{
			info.Add(CopyFileOrDir(fromFiles[i], toPath, buf,stopFlag));
		}
		return info;
	}
	public static CopyOrCutInfo CutFiles(File[] fromfiles,File toPath,Boolean stopFlag)
	{
		CopyOrCutInfo info=new CopyOrCutInfo();
		for(int i=0;i<fromfiles.length&&!stopFlag;i++)
		{
			if(fromfiles[i].renameTo( new File(toPath.getPath()+"/"+fromfiles[i].getName())))
				info.mSuccessNum++;
			else
				info.mFailedNum++;
		}
		return info;
	}
	public static String GetSize(Long size)
	{
		Long len=size;
		String str="";
		int i=0;
		double s;
		while(len>0)
		{
			len/=1024;
			i++;
		}
		switch(i)
		{
		case 0:
		case 1:
			str=String.format("%d B  ", size);
			break;
		case 2:
			s=1024;
			str=String.format("%.2f K  ", size/s);
			break;
		case 3:
			s=1024*1024;
			str=String.format("%.2f M  ", size/s);
			break;
		case 4:
			s=1024*1024*1024;
			str=String.format("%.2f G  ", size/s);
			break;
		default:
			s=1024*1024*1024*1024;
			str=String.format("%.2f T ", size/s);
			break;
		}
		return str;
	}
}

class FilesInfo
{
	public int mFileNum=0;
	public int mDirNum=0;
	public int mUnknowNum=0;
	public long mSize=0;
	public void Add(FilesInfo info)
	{
		mFileNum+=info.mFileNum;
		mDirNum+=info.mDirNum;
		mSize+=info.mSize;
		mUnknowNum+=info.mUnknowNum;
	}
}

class CopyOrCutInfo
{
	public int mSuccessNum=0;
	public int mFailedNum=0;
	public int mExistNum=0;
	public void Add(CopyOrCutInfo info)
	{
		mSuccessNum+=info.mSuccessNum;
		mFailedNum+=info.mFailedNum;
		mExistNum+=info.mExistNum;
	}
}
