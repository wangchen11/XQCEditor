package person.wangchen11.cproject;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.LinkedList;
import java.util.List;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;
import org.xmlpull.v1.XmlPullParserFactory;
import org.xmlpull.v1.XmlSerializer;

import person.wangchen11.util.FileUtil;

import android.annotation.SuppressLint;
import android.content.Context;
import android.util.Log;
import android.util.Xml;

public class CProject {
	protected static final String TAG="CProject";
	public static final String PROJECT_FILE_NAME="qeditor.project";
	private String mProjectName;
	private String mPath;		//绝对路径  
	private String mSrcName;	//相对路径  
	private String mBinName;	//相对路径  
	private String mCompileOption;	
	private String mPackageSoFile;	
	private String mDebugType;	
	private boolean mIsGuiProject = false;	//是否为图形界面的项目
	private String mOtherOption = "";	//其它编译选项
	
	public String getProjectName(){
		return mProjectName;
	}
	
	public String getPath(){
		return mPath;
	}

	public String getSrcName(){
		return mSrcName;
	}
	
	public String getBinName(){
		return mBinName;
	}
	
	public String getOtherOption(){
		return mOtherOption;
	}
	
	public boolean isGuiProject(){
		return mIsGuiProject;
	}
	
	public String getPackageSoFile()
	{
		return mPackageSoFile;
	}
	
	public String getCompileOption()
	{
		return mCompileOption;
	}
	
	public String getDebugType()
	{
		Log.i(TAG,"mDebugType:"+  mDebugType);
		return mDebugType;
	}
	
	private CProject(String projectName,String path,String srcPath,String binPath) {
		mProjectName=projectName;
		mPath=path;
		mSrcName=srcPath;
		mBinName=binPath;
		mIsGuiProject=false;
		mCompileOption="";
		mPackageSoFile="libNativeActivity.so";
		mOtherOption="";
		mDebugType = "";
	}

	private CProject(String projectName,String path,String srcPath,String binPath,boolean isGuiProject) {
		mProjectName=projectName;
		mPath=path;
		mSrcName=srcPath;
		mBinName=binPath;
		mIsGuiProject=isGuiProject;
		mCompileOption="";
		if(isGuiProject)
		{
			mOtherOption=" -ljnigraphics -Lslib/armeabi -lapi ";
			mPackageSoFile="libNativeActivity.so";
			mDebugType = "";
		}
		else
		{
			mOtherOption="";
		}
	}
	
	public static CProject newProject(String projectName,String path,String srcPath,String binPath){
		return new CProject(projectName, path, srcPath, binPath);
	}
	
	public static CProject newProject(String projectName,String path,String srcPath,String binPath,boolean isGuiProject){
		return new CProject(projectName, path, srcPath, binPath,isGuiProject);
	}


	public String getProjectPath(){
		return mPath;
	}
	public String getProjectFilePath(){
		return getProjectPath()+File.separatorChar+PROJECT_FILE_NAME;
	}
	public String getSrcPath(){
		return getProjectPath()+File.separatorChar+mSrcName;
	}
	public String getBinPath(){
		return getProjectPath()+File.separatorChar+mBinName;
	}
	public String getBinFilePath(){
		return getBinPath()+File.separatorChar+mProjectName+".elf";
	}
	public String getAFilePath(){
		return getBinPath()+File.separatorChar+mProjectName+".a";
	}
	public String getSoFilePath(){
		return getBinPath()+File.separatorChar+mProjectName+".so";
	}
	public String getObjPath(){
		return getBinPath()+"/obj";
	}
	
	public String getResPath(){
		return getProjectPath()+"/res";
	}
	
	public String getAssetsPath()
	{
		return getProjectPath()+"/assets";
	}

	public String getManifestPath()
	{
		return getProjectPath()+"/AndroidManifest.xml";
	}

	public String getResZipPath()
	{
		return getProjectPath()+"/bin/resources.zip";
	}
	
	public void createProject(Context context) throws Exception{
		if(mIsGuiProject)
		{
			createGuiProject(context);
			return ;
		}
		File dir=new File(getProjectPath());
		if(dir.isDirectory())
			throw new Exception("目录或项目已经存在!");
		if(!dir.mkdirs())
			throw new Exception("创建项目目录失败!");
		if(!saveProjectFile())
			throw new Exception("创建项目文件失败!");
		dir=new File(getSrcPath());
		if(!dir.mkdirs())
			throw new Exception("创建源码目录失败!");
		dir=new File(getBinPath());
		if(!dir.mkdirs())
			throw new Exception("创建生成目录失败!");
		if( FileUtil.freeZip(context, "console project.zip", getProjectPath())<=0 )
			throw new Exception("释放资源失败!");
	}
	
	private void createGuiProject(Context context) throws Exception{
		File dir=new File(getProjectPath());
		if(dir.isDirectory())
			throw new Exception("目录或项目已经存在!");
		if(!dir.mkdirs())
			throw new Exception("创建项目目录失败!");
		File file=new File(getProjectFilePath());
		
		if( !file.isFile() )
		if(  !saveProjectFile() )
			throw new Exception("创建项目文件失败!");
		dir=new File(getSrcPath());
		if(!dir.mkdirs())
			throw new Exception("创建源码目录失败!");
		dir=new File(getBinPath());
		if(!dir.mkdirs())
			throw new Exception("创建生成目录失败!");
		if( FileUtil.freeZip(context, "gui project.zip", getProjectPath())<=0 )
			throw new Exception("释放资源失败!");
	}
	
	public boolean saveProjectFile() {
		File file=new File(getProjectFilePath());
		try {
			try {
				XmlPullParserFactory factory;
				factory = XmlPullParserFactory.newInstance();
				XmlSerializer xmlSerializer = factory.newSerializer(); 
				FileOutputStream fileOutputStream=new FileOutputStream(file);
				try {
					xmlSerializer.setOutput(fileOutputStream, "utf-8");
					xmlSerializer.startDocument("utf-8", true);
					xmlSerializer.text("\n");
					
					xmlSerializer.startTag(null, "project_name");
					xmlSerializer.text(mProjectName);
					xmlSerializer.endTag(null, "project_name"); 
					xmlSerializer.text("\n");

					xmlSerializer.startTag(null, "src_name");
					xmlSerializer.text(mSrcName);
					xmlSerializer.endTag(null, "src_name"); 
					xmlSerializer.text("\n");

					xmlSerializer.startTag(null, "bin_name");
					xmlSerializer.text(mBinName);
					xmlSerializer.endTag(null, "bin_name");
					xmlSerializer.text("\n");

					xmlSerializer.startTag(null, "other_option");
					xmlSerializer.text(mOtherOption);
					xmlSerializer.endTag(null, "other_option");
					xmlSerializer.text("\n");
					
					xmlSerializer.startTag(null, "complie_option");
					xmlSerializer.text(mCompileOption);
					xmlSerializer.endTag(null, "complie_option");
					xmlSerializer.text("\n");
					
					xmlSerializer.startTag(null, "is_gui_project");
					xmlSerializer.text( String.valueOf(mIsGuiProject) );
					xmlSerializer.endTag(null, "is_gui_project");
					xmlSerializer.text("\n");
					
					if(mIsGuiProject)
					{
						xmlSerializer.startTag(null, "package_so_file");
						xmlSerializer.text( mPackageSoFile );
						xmlSerializer.endTag(null, "package_so_file");
						xmlSerializer.text("\n");
						
						xmlSerializer.startTag(null, "debug_type");
						xmlSerializer.text( mDebugType );
						xmlSerializer.endTag(null, "debug_type");
						xmlSerializer.text("\n");
					}
					
					xmlSerializer.endDocument();  

					return true;
					
				} catch (IllegalArgumentException e) {
					e.printStackTrace();
				} catch (IllegalStateException e) {
					e.printStackTrace();
				} catch (IOException e) {
					e.printStackTrace();
				}
			} catch (XmlPullParserException e) {
				e.printStackTrace();
			}
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}
		return false;
	}

	public static CProject LoadProjectByDir(File file){
		return LoadProject(new File(file.getPath()+File.separatorChar+PROJECT_FILE_NAME));
	}
	public static CProject LoadProject(File file){
		if( !(file.isFile()&&file.canRead()) )
			return null;
		try {
			FileInputStream fileInputStream=new FileInputStream(file);
			XmlPullParser parser=Xml.newPullParser();
			try {
				parser.setInput(fileInputStream, "utf-8");
				CProject temp=new CProject(null, null, null, null);
				

		        int eventType = parser.getEventType();  

	            try {
			        while (eventType != XmlPullParser.END_DOCUMENT) {  
			            switch (eventType) {  
			            case XmlPullParser.START_DOCUMENT:  
			                break;  
			            case XmlPullParser.START_TAG:  
			                if (parser.getName().equals("project_name")) { 
			                    eventType = parser.next();  
								temp.mProjectName=parser.getText();
			                } else if (parser.getName().equals("src_name")) {  
			                    eventType = parser.next();  
								temp.mSrcName=parser.getText();
			                } else if (parser.getName().equals("bin_name")) {  
			                    eventType = parser.next();  
								temp.mBinName=parser.getText();
			                } else if (parser.getName().equals("other_option")) {  
			                    eventType = parser.next();  
								temp.mOtherOption=parser.getText();
			                } else if (parser.getName().equals("is_gui_project")) {  
			                    eventType = parser.next();
			                    String text=parser.getText();
			                    if(text.equalsIgnoreCase("true"))
			                    	temp.mIsGuiProject=true;
			                    else
			                    	temp.mIsGuiProject=false;
			                } else if (parser.getName().equals("package_so_file")) {  
			                    eventType = parser.next();  
								temp.mPackageSoFile=parser.getText();
			                }  else if (parser.getName().equals("complie_option")) {  
			                    eventType = parser.next();  
								temp.mCompileOption=parser.getText();
			                }   else if (parser.getName().equals("debug_type")) {  
			                    eventType = parser.next();  
								temp.mDebugType=parser.getText();
			                } 
			                break;  
			            case XmlPullParser.END_TAG:  
			                break; 
			            }  
						eventType = parser.next();
			        }
				} catch (IOException e) {
					e.printStackTrace();
				}
				temp.mPath=file.getParent();
				Log.i(TAG, "temp:"+temp);
				if(temp.canUse())
					return temp;
				return null;
			} catch (XmlPullParserException e) {
				e.printStackTrace();
			}
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}
		return null;
	}
	
	public boolean canUse(){
		if(mBinName!=null && mPath!=null && mProjectName!=null && mSrcName!=null)
			return true;
		return false;
	}
	/*
	private boolean createHelloWord_C(){
		File file=new File(getSrcPath()+File.separatorChar+"main.c");
		try {
			FileOutputStream fileOutputStream=new FileOutputStream(file);
			StringBuilder stringBuilder=new StringBuilder();
			stringBuilder.append("#include <stdio.h>\n");
			stringBuilder.append("\n");
			stringBuilder.append("int main(int argc,char **argv)\n");
			stringBuilder.append("{\n");
			stringBuilder.append("\tprintf(\"Hello World!\");\n");
			stringBuilder.append("\treturn 0;\n");
			stringBuilder.append("}\n");
			try {
				fileOutputStream.write(stringBuilder.toString().getBytes());
				fileOutputStream.close();
				return true;
			} catch (IOException e) {
				e.printStackTrace();
			}
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}
		return false;
	}
	*/
	/**
	 * 判断一个文件是否在该工程的源文件目录 
	 * @param file 
	 * @return 
	 */
	public boolean srcInProject(File file){
		Log.i(TAG, "compile:"+file.getAbsolutePath());
		Log.i(TAG, "to:"+new File(getSrcPath()).getPath());
		if(  file.getAbsolutePath().startsWith(new File(getSrcPath()).getPath())  )
			return true;;
		return false;
	}
	
	
	//通过文件向上查找其工程文件，如果没找到则返回空 
	public static CProject findCProjectByFile(File file){
		File parent=file.getParentFile();
		CProject cProject=null;
		if(parent==null)
			return null;
		try {
			cProject=LoadProjectByDir(parent);
		} catch (Exception e) {
			e.printStackTrace();
		}
		Log.i(TAG, "cProject:"+cProject);
		if(cProject!=null)
			return cProject;
		return findCProjectByFile(parent);
	}
	
	@Override
	public String toString() {
		StringBuilder builder=new StringBuilder();
		builder.append("mProjectName:"+mProjectName);
		builder.append("\n");
		builder.append("mPath:"+mPath);
		builder.append("\n");
		builder.append("mSrcName:"+mSrcName);
		builder.append("\n");
		builder.append("mBinName:"+mBinName);
		return builder.toString();
	}
	/**
	 * 遍历得到所有的.c文件  
	 * @return
	 */
	public List<File> getAllCFiles(){
		return getCFilesEx(new File(getSrcPath()));
	}
	
	@SuppressLint("DefaultLocale")
	public List<File> getCFilesEx(File file){
		LinkedList<File > files=new LinkedList<File>();
		if(file.isDirectory()){
			File []list=file.listFiles();
			if(list!=null){
				for(int i=0;i<list.length;i++){
					String name=list[i].getName().toLowerCase();
					if(list[i].isFile() && (name.endsWith(".c")||(name.endsWith(".cpp"))||name.endsWith(".s") ) ){
						files.add(list[i]);
					}else
					if(list[i].isDirectory())
					{
						files.addAll(getCFilesEx(list[i]));
					}
				}
			}
		}
		return files;
	}
}
