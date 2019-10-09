package person.wangchen11.gnuccompiler;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

import person.wangchen11.cproject.CProject;
import person.wangchen11.window.ext.Setting;
import person.wangchen11.xqceditor.R;

import android.annotation.SuppressLint;
import android.content.Context;
import android.os.Environment;
import android.util.Log;

@SuppressLint("DefaultLocale") public class GNUCCompiler2 {
	static final String TAG = "GNUCCompiler2";

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
		return " -lm -lstdc++ -pie ";
	}
	
	
	public static String getExportEnvPathCmd(Context context)
	{
		String cmd="";
		return cmd;
	}

	private static String getFilesString(List <File> files){
		Iterator<File> iterator=files.iterator();
		StringBuilder cmdBuilder = new StringBuilder();
		while(iterator.hasNext()){
			cmdBuilder.append(" \""+iterator.next().getPath()+"\" ");
		}
		return cmdBuilder.toString();
	}
	
	private static String getRelativePath(File file,File dir)
	{
		try {
			String dirPath=dir.getCanonicalPath();
			String filepath=file.getCanonicalPath();
			if(filepath.startsWith(dirPath))
			{
				return filepath.substring(dirPath.length());
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
		return null;
	}
	
	private static List<File> getObjFiles(List <File> files,File objPath,File srcPath) throws Exception
	{
		List<File> retFiles=new LinkedList<File>();
		Iterator<File > iterator = files.iterator();
		while(iterator.hasNext())
		{
			File file = iterator.next();
			String relPath = getRelativePath(file,srcPath);
			if(relPath==null)
			{
				throw new Exception("source file not in src path:"+file.getAbsolutePath());
			}
			File objFile = new File(objPath.getAbsolutePath()+"/"+relPath+".o");
			retFiles.add(objFile);
		}
		return retFiles;
	}
	
	
	public static String getCompilerOnlyCmd(File file,File objFile,String compileOption)
	{
		boolean isCpp = file.getName().toLowerCase().endsWith(".cpp");
		String cmd = ""
				+"gcc -c "
				+" \""+file.getAbsolutePath()+"\" "
				+" -o \""+objFile.getAbsolutePath()+"\" "
				+(isCpp?getCppNeedOption():getCNeedOption())
				+" "+(compileOption!=null?compileOption:"")
				+"\n";
		return cmd;
	}
	
	private static String getCompilerToObjCmd(List <File> files,File objPath,File srcPath,String compileOption) throws Exception
	{
		StringBuilder cmdBuilder = new StringBuilder();
		
		Iterator<File > iterator = files.iterator();
		while(iterator.hasNext())
		{
			File file = iterator.next();
			String relPath = getRelativePath(file,srcPath);
			if(relPath==null)
			{
				throw new Exception("source file not in src path!");
			}
			File objFile = new File(objPath.getAbsolutePath()+"/"+relPath+".o");
			if(true)//if(!objFile.isFile() || objFile.lastModified() <= file.lastModified() )
			{
				objFile.getParentFile().mkdirs();
				objFile.delete();
				
				boolean isCpp = file.getName().toLowerCase().endsWith(".cpp");
				cmdBuilder.append(
						"echo \""+file.getName()+"\t-->\t"+objFile.getName()+"\"\n"
						+"gcc -c "
						+" \""+file.getAbsolutePath()+"\" "
						+" -o \""+objFile.getAbsolutePath()+"\" "
						+(isCpp?getCppNeedOption():getCNeedOption())
						+" -O "
						+" "+(compileOption!=null?compileOption:"")
						+"\n"
						/*
						//+"if [ ! -f \""+objFile.getPath()+"\" ]; then \n"
						+"if [  $? -ne 0 ]; then \n"
				      	+"compiler_to_obj_success=0\n"
						+"fi\n"*/ );
			}
		}
		cmdBuilder.append("\n");
		File file = File.createTempFile("exec", ".sh");
		FileOutputStream fileOutputStream = new FileOutputStream(file);
		fileOutputStream.write(cmdBuilder.toString().getBytes());
		fileOutputStream.close();
		
		cmdBuilder.delete(0, cmdBuilder.length());
		cmdBuilder.append("compiler_to_obj_success=1\n");
		cmdBuilder.append("mutexec "+Setting.mConfig.mOtherConfig.mThreadNumber+" "+file.getAbsolutePath()+"\n");
		cmdBuilder.append(""
				+"if [  $? -ne 0 ]; then \n"
		      	+"compiler_to_obj_success=0\n"
				+"fi\n");
		cmdBuilder.append("myrm \""+file.getAbsolutePath()+"\"\n");
		return cmdBuilder.toString();
	}
	
	private static boolean hasCppFile(List <File> files)
	{
		Iterator<File > iterator = files.iterator();
		while(iterator.hasNext())
		{
			File file = iterator.next();
			boolean isCpp = file.getName().toLowerCase().endsWith(".cpp");
			if(isCpp)
				return true;
		}
		return false;
	}
	
	public static String getCompilerCmd(Context context,CProject project,boolean toSo,File exclude){
		File outFile = null;
		if(toSo)
			outFile = new File(project.getSoFilePath());
		else
			outFile = new File(project.getBinFilePath());
		outFile.delete();
		outFile.getParentFile().mkdirs();
		File objPath = new File(project.getObjPath());
		File srcPath = new File(project.getSrcPath());
		String otherOption = project.getOtherOption();
		StringBuilder cmdBuilder = new StringBuilder();
		cmdBuilder.append("cd \""+project.getProjectPath()+"\"\n");
		//cmdBuilder.append(getExportEnvPathCmd(context));
		List<File> allFiles = project.getAllCFiles();
		if(exclude!=null){
			allFiles.remove(exclude);
			Log.i(TAG, "exclude:"+exclude);
		}
		try {
			cmdBuilder.append(getCompilerToObjCmd(allFiles, objPath, srcPath,project.getCompileOption()));
		} catch (Exception e) {
			e.printStackTrace();
			return "echo \"Exception:"+e.getMessage()+"\"\n";
		}
		cmdBuilder.append("if [ \"$compiler_to_obj_success\" = \"1\" ] \n");
		cmdBuilder.append("then\n");
		cmdBuilder.append("echo linking...\n");
		cmdBuilder.append("gcc ");
		try {
			cmdBuilder.append( getFilesString(getObjFiles(allFiles, objPath, srcPath)));
		} catch (Exception e) {
			e.printStackTrace();
			return "echo \"Exception:"+e.getMessage()+"\"\n";
		}
		if(hasCppFile(allFiles))
		{
			cmdBuilder.append(" \""+GNUCCompiler.getFixCppObj(context)+"\" ");
			cmdBuilder.append(getCppLinkNeedOption());
		}
		else
		{
			cmdBuilder.append(getCLinkNeedOption());
		}
		cmdBuilder.append(" -O ");
		cmdBuilder.append(" -o \""+outFile.getAbsolutePath()+"\" ");
		if(toSo)
			cmdBuilder.append(" -llog -landroid -shared ");
		else
			cmdBuilder.append(" ");
		cmdBuilder.append(" "+ (otherOption!=null?otherOption:""));
		cmdBuilder.append("\n");

		//cmdBuilder.append("if [ ! -f \""+outFile.getPath()+"\" ]; then \n");
		cmdBuilder.append("if [  $? -ne 0 ]; then \n");
		cmdBuilder.append("echo \""+context.getText(R.string.compilation_fails)+"\"\n");
		cmdBuilder.append("else\n");
		cmdBuilder.append("echo Install:\""+outFile.getAbsolutePath()+"\"\n");
		cmdBuilder.append("strip \""+outFile.getAbsolutePath()+"\"\n");
		cmdBuilder.append("echo \""+context.getText(R.string.successfully_compiled)+"\"\n");
		cmdBuilder.append("fi\n");
		
		cmdBuilder.append("else\n");
		cmdBuilder.append( "echo \""+context.getText(R.string.compilation_fails)+"\"\n");
		cmdBuilder.append("fi\n");
		return cmdBuilder.toString();
	}

	public static String getCompilerACmd(Context context,CProject project,File exclude){
		File outFile = null;
		outFile = new File(project.getAFilePath());
		outFile.delete();
		outFile.getParentFile().mkdirs();
		File objPath = new File(project.getObjPath());
		File srcPath = new File(project.getSrcPath());
		//String otherOption = project.getOtherOption();
		StringBuilder cmdBuilder = new StringBuilder();
		cmdBuilder.append("cd \""+project.getProjectPath()+"\"\n");
		//cmdBuilder.append(getExportEnvPathCmd(context));
		List<File> allFiles = project.getAllCFiles();
		if(exclude!=null){
			allFiles.remove(exclude);
		}
		try {
			cmdBuilder.append(getCompilerToObjCmd(allFiles, objPath, srcPath,project.getCompileOption()));
		} catch (Exception e) {
			e.printStackTrace();
			return "echo \"Exception:"+e.getMessage()+"\"\n";
		}
		cmdBuilder.append("if [ \"$compiler_to_obj_success\" = \"1\" ] \n");
		cmdBuilder.append("then\n");
		cmdBuilder.append("echo linking...\n");
		cmdBuilder.append("ar crv ");
		cmdBuilder.append("\""+outFile.getAbsolutePath()+"\" ");
		try {
			cmdBuilder.append( getFilesString(getObjFiles(allFiles, objPath, srcPath)));
		} catch (Exception e) {
			e.printStackTrace();
			return "echo \"Exception:"+e.getMessage()+"\"\n";
		}
		cmdBuilder.append("\n");

		//cmdBuilder.append("if [ ! -f \""+outFile.getPath()+"\" ]; then \n");
		cmdBuilder.append("if [  $? -ne 0 ]; then \n");
		cmdBuilder.append("echo \""+context.getText(R.string.compilation_fails)+"\"\n");
		cmdBuilder.append("else\n");
		cmdBuilder.append("echo Install:\""+outFile.getAbsolutePath()+"\"\n");
		cmdBuilder.append("echo \""+context.getText(R.string.successfully_compiled)+"\"\n");
		cmdBuilder.append("fi\n");
		
		cmdBuilder.append("else\n");
		cmdBuilder.append( "echo \""+context.getText(R.string.compilation_fails)+"\"\n");
		cmdBuilder.append("fi\n");
		return cmdBuilder.toString();
	}
	
}
