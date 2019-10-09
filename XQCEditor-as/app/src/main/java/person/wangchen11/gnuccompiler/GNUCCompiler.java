package person.wangchen11.gnuccompiler;

import java.io.File;
import java.io.IOException;

import person.wangchen11.plugins.PluginsManager;
import person.wangchen11.plugins.WaitingProcess;
import person.wangchen11.util.FileUtil;
import person.wangchen11.waps.Waps;
import person.wangchen11.window.ext.Setting;
import person.wangchen11.xqceditor.R;
import person.wangchen11.xqceditor.State;

import android.annotation.SuppressLint;
import android.content.Context;
import android.os.Environment;
import android.support.annotation.Nullable;

/*
#$PLUGINS 
#$PLUGHOME
#$INCLUDE_C
#$INCLUDE_CPP
#$LIB_DIR
 */

@SuppressLint("DefaultLocale") 
public class GNUCCompiler {
	protected final static String TAG="GNUCCompiler";

	public static void freeResourceIfNeed(final Context context){
		new WaitingProcess(context,R.string.free_res) {
			@Override
			public void run() {
				setProcess(0);
				setMsg(R.string.free_example);
				if( State.isUpdated(State.RES_KEY_WORKSPACE) || !new File(getWorkSpaceDir()).isDirectory() )
				{
					String zipFile = "";
					if(Waps.isGoogle()){
						zipFile = "workspace_google.zip";
					}else{
						zipFile = "workspace.zip";
					}
					FileUtil.freeZip(context, zipFile, getSystemDir() );
				}
				setMsg(R.string.free_gcc_res);
				setProcess(10);
				if( State.isUpdated(State.RES_KEY_GCC) ){
					try {
						PluginsManager.getInstance().installPluginReal("gcc", context.getAssets().open("gcc.qplug.zip"));
					} catch (IOException e) {
						e.printStackTrace();
					}
				}
				setProcess(60);
				if( State.isUpdated(State.RES_KEY_FIX_CPP) || !new File(getFixCppObj(context)).isFile() )
				{
					FileUtil.freeFile(context, "fix.cpp.o", getFixCppObj(context));
				}
				setProcess(80);
				if( State.isUpdated(State.RES_KEY_THEMES) || !new File(Setting.getThemeDir()).isDirectory() )
				{
					FileUtil.freeZip(context, "themes.zip", Setting.getThemeDir() );
				}
				setProcess(100);
				State.save(context);
			}
		}.start();
	}

	public static String getFixCppObj(Context context)
	{
		return getRunablePath(context)+"/fix.cpp.o";
	}
	
	public static String getRunablePath(Context context){
		return context.getFilesDir().getAbsolutePath()+File.separatorChar;
	}
	static int mNameStartNumber=0;
	public static synchronized String getTempFilePath(Context context){
		String path="";
		for(int i=0;i<1000;i++)
		{
			path = getRunablePath(context)+mNameStartNumber+".tmp";
			mNameStartNumber++;
			if( !(new File(path).isFile()) )
				break;
		}
		return path;
	}
	
	public static String getSystemDir(){
		return Environment.getExternalStorageDirectory().getAbsolutePath()+File.separatorChar+"qeditor"+File.separatorChar;
	}

	public static String getIncludeDir(){
		return PluginsManager.getInstance().getVar("INCLUDE_C");
	}

	public static String getIncludeDirEx(){
		return PluginsManager.getInstance().getVar("INCLUDE_CPP");
	}
	
	public static String getWorkSpaceDir(){
		return getSystemDir()+"/workspace/";
	}

	public static String getCNeedOption()
	{
		return " -Wall -std=c99 ";
	}
	
	public static String getCppNeedOption()
	{
		return " -Wall ";
	}
	
	public static String getCLinkNeedOption()
	{
		return " -lm -pie ";
	}

	public static String getCppLinkNeedOption()
	{
		return " -lm -lstdc++  -pie ";
	}
	
	public static String getCompilerCmd(Context context,File file,File outFile,String otherOption){
		boolean isCpp = file.getName().toLowerCase().endsWith(".cpp");
		outFile.delete();
		outFile.getParentFile().mkdirs();
		String cmd="";
		cmd+="echo \""+context.getText(R.string.compiling)+"\"\n";
		cmd+="cd \""+outFile.getParent()+"\"\n";
		cmd+="gcc \"";
		cmd+=file.getAbsolutePath()+"\" ";
		if(isCpp)
		{
			cmd+=" \""+getFixCppObj(context)+"\" ";
			cmd+=getCppNeedOption();
			cmd+=getCppLinkNeedOption();
		}
		else
		{
			cmd+=getCNeedOption();
			cmd+=getCLinkNeedOption();
		}
		cmd+=" -O ";
		cmd+=" -o \""+outFile.getPath()+"\" ";
		cmd+="-Wall ";
		cmd+=(otherOption!=null?otherOption:"");
		cmd+="\n";
		//cmd+="if [ ! -f \""+outFile.getPath()+"\" ]; then \n";
		cmd+="if [  $? -ne 0 ]; then \n";
		cmd+="echo \""+context.getText(R.string.compilation_fails)+"\"\n";
		cmd+="else\n";
		if(!outFile.getName().endsWith(".s")){
			cmd+="strip \""+outFile.getAbsolutePath()+"\"\n";
		}
		cmd+="echo \""+context.getText(R.string.successfully_compiled)+"\"\n";
		cmd+="fi\n";
		return cmd;
	}
	
	
	static String mProPocesssName=null;
	public static String getRunCmd(Context context,File executeFile){
		return getRunCmd(context,executeFile,null);
	}
	
	public static String getRunCmd(Context context,File executeFile,String otherOption){
		try {
			//让rm不报警告 
			new File(getRunablePath(context)+"asddsatemp.tmp").createNewFile();
		} catch (IOException e) {
			e.printStackTrace();
		}
		String cmd="";
		//cmd+=Busybox.getCmd(context);
		executeFile=new File(executeFile.getPath());
		cmd+="if [ ! -f \""+executeFile.getPath()+"\" ]; then \n";
		cmd+="echo \""+context.getText(R.string.no_elf_file)+"\"\n";
		cmd+="else\n";
		cmd+="cd \""+getRunablePath(context)+"\"\n";
		cmd+="myrm *.tmp\n";
		cmd+="cd \""+executeFile.getParent()+"\"\n";
		String tempPath=getTempFilePath(context);
		cmd+="mycp \""+executeFile.getPath()+"\" \""+tempPath+"\"\n";
		cmd+="chmod 777 \""+tempPath+"\"\n";
		cmd+=tempPath+(otherOption!=null?(" "+otherOption):"")+"\n";
		cmd+="echo \n";
		//cmd+="myrm \""+tempPath+"\"\n";
		//cmd+="echo \""+context.getText(R.string.program_end)+"\"\n";
		cmd+="if [ -f \""+tempPath+"\" ]; then \n";
		cmd+="myrm \""+tempPath+"\"\n";
		cmd+="fi\n";
		cmd+="fi\n";
		mProPocesssName=tempPath;
		return cmd;
	}
	
	public static String getRunCmdProcessName()
	{
		return mProPocesssName;
	}

	public static String getCompilerAndRunCmd(Context context,File file,@Nullable String otherOption){
		String cmd="";
		File elfFile=new File(file.getPath()+".elf");
		cmd=getCompilerCmd(context,file,elfFile," "+(otherOption!=null?otherOption:""));
		cmd+=getRunCmd(context, elfFile);
		return cmd;
	}

	public static String getCompilerSoCmd(Context context,File file,@Nullable String otherOption){
		String cmd="";
		File elfFile=new File(file.getPath()+".so");
		// -fuse-ld=bfd 
		/*	cmd=getCompilerCmd(context,files,elfFile," -llog -landroid -lEGL -shared "+(otherOption!=null?otherOption:""));
		 */		
		/**** by androids7 **/
		cmd=getCompilerCmd(context,file,elfFile," -llog -landroid -shared "+(otherOption!=null?otherOption:""));
		return cmd;
	}
	
	public static String getCompilerSCmd(Context context,File file,File fileTo,@Nullable String otherOption)
	{
		String cmd="";
		cmd=getCompilerCmd(context,file,fileTo," -S "+(otherOption!=null?otherOption:""));
		return cmd;
	}
	
}
