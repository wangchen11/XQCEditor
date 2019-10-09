package person.wangchen11.zipfile;

import java.io.File;

public class ZipFile {
	private String mZipFilePath;
	private String mSerchPath;
	boolean mIncludeSerchPath;
	public ZipFile(String zipFilePath,String serchPath,boolean includeSerchPath) {
		mZipFilePath = zipFilePath;
		mSerchPath=serchPath;
		mIncludeSerchPath=includeSerchPath;
	}
	public void start()
	{
		ZipCompressing.zip(mZipFilePath, getNeedPackageFiles() );
	}

	private File[] getNeedPackageFiles()
	{
		File tempDir=new File(mSerchPath);
		if(mIncludeSerchPath)
			return new File[]{tempDir};
		else
			return tempDir.listFiles();
	}
	
}

