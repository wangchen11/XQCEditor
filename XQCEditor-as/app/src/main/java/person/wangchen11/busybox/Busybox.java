package person.wangchen11.busybox;

import java.io.File;
import person.wangchen11.util.FileUtil;
import person.wangchen11.xqceditor.State;

import android.content.Context;

public class Busybox {
	static final String TAG="Busybox";
	
	
	public static void freeResourceIfNeed(final Context context){
		if( State.isUpdated() || !new File(getWorkDir(context)).isDirectory() )
		{
			new File(getWorkDir(context)).mkdirs();
			FileUtil.freeZip(context, "busybox.zip", getRunnablePath(context) );
		}
		FileUtil.setFileAllChildsExecutable(new File(getWorkDir(context)));
	}
	
	public static String getRunnablePath(Context context)
	{
		return context.getFilesDir().getAbsolutePath();
	}
	
	public static String getWorkDir(Context context)
	{
		return getRunnablePath(context)+"/busybox/";
	}
	
	public static String getCmd(Context context)
	{
		String cmd="\n";
		cmd+="export PATH=$PATH:"+getWorkDir(context)+"\n";
		cmd+="chmod 777 "+getWorkDir(context)+"/*\n";
		return cmd;
	}
}
