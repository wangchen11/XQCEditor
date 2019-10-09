package person.wangchen11.packageapk;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import android.content.Context;
import android.util.Log;
import android.widget.Toast;
import person.wangchen11.cproject.CProject;
import person.wangchen11.filebrowser.FileWork;
import person.wangchen11.xqceditor.R;

public class Aapt {
	public static String getAaptPath(Context context)
	{
		return context.getFilesDir().getAbsolutePath();
	}
	
	public static String getAaptFilePath(Context context)
	{
		return getAaptPath(context)+"/aapt";
	}

	public static String getFrameworkFilePath(Context context)
	{
		return context.getFilesDir().getAbsolutePath()+"/android.jar";
	}
	
	public static void freeResourceIfNeed(Context context)
	{
		if( !new File(getAaptFilePath(context)).isFile())
		{
			String cpu=CPUInfo.getInstance().getInfo("Hardware");
			Log.i("Aapt", ""+cpu);
			if(cpu!=null&&cpu.startsWith("MT6582"))
			{
				if( !freeFile(context, "aapt_mtk", getAaptFilePath(context)) )
				{
					Toast.makeText(context, R.string.failed_to_free_resource, Toast.LENGTH_SHORT).show();
				}
			}
			else
			if( !freeFile(context, "aapt", getAaptFilePath(context)) )
			{
				Toast.makeText(context, R.string.failed_to_free_resource, Toast.LENGTH_SHORT).show();
			}
		}
		if( !new File(getFrameworkFilePath(context)).isFile())
		{
			if( !freeFile(context, "android.jar", getFrameworkFilePath(context)) )
			{
				Toast.makeText(context, R.string.failed_to_free_resource, Toast.LENGTH_SHORT).show();
			}
		}
	}

	
	public static boolean freeFile(Context context,String assetsName,String fileTo)
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
	
	public static String getLibSoShortPath(CProject project)
	{
		return "lib/armeabi/"+project.getPackageSoFile();
	}

	public static String getElfShortPath()
	{
		return "assets/elf.elf";
	}

	public static String getLibSoPath(CProject project)
	{
		return project.getBinPath()+"/"+getLibSoShortPath(project);
	}

	public static String getElfPath(CProject project)
	{
		return project.getBinPath()+"/"+getElfShortPath();
	}

	public static String getClassesShortPath()
	{
		return "classes.dex";
	}
	
	public static String getClassesPath(CProject project)
	{
		return project.getBinPath()+"/"+getClassesShortPath();
	}
	
	public static String getCreateRCmd(Context context,CProject project){
		String cmd="";
		cmd+="echo "+context.getString(R.string.deal_res)+"\n";
		cmd+="chmod 744 \""+getAaptFilePath(context)+"\"\n";
		cmd+="export PATH=$PATH:"+getAaptPath(context)+"\n";
		cmd+="aapt package -f ";
		cmd+=" -I \""+getFrameworkFilePath(context)+"\" ";
		cmd+=" -S \""+project.getResPath()+"\" ";
		cmd+=" -A \""+project.getAssetsPath()+"\" ";
		cmd+=" -M \""+project.getManifestPath()+"\" ";
		//cmd+=" -F \""+project.getResZipPath()+"\" ";
		cmd+=" -J \""+project.getBinPath()+"\" ";// R file dir 
		cmd+="\n";

		cmd+="if [  $? -ne 0 ]; then \n";
		cmd+="echo \""+context.getText(R.string.package_fail)+"\"\n";
		cmd+="exit 1\n";
		cmd+="fi\n";
		return cmd;
	}
	
	public static String getPackageApkCmd(Context context,CProject project)
	{
		new File(project.getAssetsPath()).mkdirs();
		String cmd="";
		cmd+="echo "+context.getString(R.string.deal_res)+"\n";
		cmd+="chmod 744 \""+getAaptFilePath(context)+"\"\n";
		cmd+="export PATH=$PATH:"+getAaptPath(context)+"\n";
		cmd+="aapt package -x -f ";
		cmd+=" -I \""+getFrameworkFilePath(context)+"\" ";
		cmd+=" -S \""+project.getResPath()+"\" ";
		cmd+=" -A \""+project.getAssetsPath()+"\" ";
		cmd+=" -M \""+project.getManifestPath()+"\" ";
		cmd+=" -F \""+project.getResZipPath()+"\" ";
		//cmd+=" -J \""+project.getBinPath()+"\" ";// R file dir 
		cmd+="\n";

		cmd+="if [  $? -ne 0 ]; then \n";
		cmd+="echo \""+context.getText(R.string.package_fail)+"\"\n";
		cmd+="exit 1\n";
		cmd+="fi\n";
		
		cmd+="echo "+context.getString(R.string.deal_res_done)+"\n";
		
		cmd+="echo "+context.getString(R.string.add_files)+"\n";
		byte []buffer=new byte[10*1024];
		if(project.isGuiProject())
		{
			File libso=new File(getLibSoPath(project));
			libso.delete();
			libso.getParentFile().mkdirs();
			FileWork.CopyFile(new File(project.getSoFilePath()), new File(getLibSoPath(project)), buffer);
		}
		else
		{
			File libelf=new File(getElfPath(project));
			libelf.delete();
			libelf.getParentFile().mkdirs();
			FileWork.CopyFile(new File(project.getBinFilePath()), new File(getElfPath(project)), buffer);
		}
		File classesOld=new File(project.getProjectPath()+"/classes.dex");
		File classes=new File(getClassesPath(project));
		if( classesOld.isFile() )
		{
			classes.delete();
			FileWork.CopyFile(classesOld,classes,buffer);
		}
		if(!classes.isFile())
		{
			cmd+="echo \""+context.getString(R.string.tip)+getClassesShortPath()+context.getString(R.string.file_not_exist)+"\"\n";
		}
		cmd+="cd \""+project.getBinPath()+"\"\n";
		cmd+="aapt add \""+project.getResZipPath()+"\"";
		if(classes.isFile())
		{
			cmd+=" \""+getClassesShortPath()+"\" ";
		}
		if(project.isGuiProject())
			cmd+=" \""+getLibSoShortPath(project)+"\" ";
		else
			cmd+=" \""+getElfShortPath()+"\" ";
		cmd+="\n";
		
		cmd+="if [  $? -ne 0 ]; then \n";
		cmd+="echo \""+context.getText(R.string.package_fail)+"\"\n";
		cmd+="exit 1\n";
		cmd+="fi\n";
		
		File libsDir= new File(project.getProjectPath()+"/lib/armeabi/");
		File libs[] = libsDir.listFiles();
		
		if(libs!=null&&libs.length>0)
		{
			cmd+="echo \"add other libs...\"\n";
			cmd+="cd \""+project.getProjectPath()+"\"\n";
			cmd+="aapt add \""+project.getResZipPath()+"\"";
			for(int i=0;i<libs.length;i++)
			{
				cmd+=" \"lib/armeabi/"+libs[i].getName()+"\" ";
			}
			cmd+="\n";
			
			cmd+="if [  $? -ne 0 ]; then \n";
			cmd+="echo \""+context.getText(R.string.package_fail)+"\"\n";
			cmd+="exit 1\n";
			cmd+="fi\n";
		}

		Log.i("tom",cmd);
		
		return cmd;
	}

}
