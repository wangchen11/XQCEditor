package person.wangchen11.filebrowser;

import java.io.File;

public class CopyOrCutOption
{
	private boolean mIsCopy=true;
	private File []mFiles=null;
	public CopyOrCutOption(File[] files,boolean isCopy) 
	{
		mIsCopy=isCopy;
		mFiles=files;
	}
	public CopyOrCutInfo Do(File path,Boolean stopFlag)
	{
		if(mIsCopy)
			return FileWork.CopyFiles(mFiles, path, stopFlag);
		else
			return FileWork.CutFiles(mFiles, path,stopFlag);
	}
}
