package person.wangchen11.window.ext;

import java.io.File;
import java.util.LinkedList;
import java.util.List;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.AlertDialog.Builder;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.support.v4.app.Fragment;
import android.widget.Toast;
import person.wangchen11.cproject.CProject;
import person.wangchen11.filebrowser.FileWork;
import person.wangchen11.gnuccompiler.GNUCCompiler;
import person.wangchen11.gnuccompiler.GNUCCompiler2;
import person.wangchen11.packageapk.DebugApk;
import person.wangchen11.packageapk.PackageApk;
import person.wangchen11.phpconfig.PHPConfig;
import person.wangchen11.qeditor.ChangeFlagChanged;
import person.wangchen11.qeditor.EditorFregment;
import person.wangchen11.qeditor.NewEditorFregment;
import person.wangchen11.qeditor.OnRunButtonClickListener;
import person.wangchen11.window.MenuTag;
import person.wangchen11.window.Window;
import person.wangchen11.window.WindowPointer;
import person.wangchen11.window.WindowsManager;
import person.wangchen11.window.WindowsManager.WindowsManagerLintener;
import person.wangchen11.xqceditor.R;

public class CEditor implements Window, ChangeFlagChanged, OnClickListener ,WindowsManagerLintener, OnRunButtonClickListener{
	protected static final String TAG="CEditor";
	private EditorFregment mCEditorFregment = null;
	private NewEditorFregment mNewCEditorFregment = null;
	private WindowsManager mWindowsManager;
	private boolean mIsAlive=true;
	
	public CEditor(WindowsManager windowsManager,File file) {
		mWindowsManager=windowsManager;
		
		if(!Setting.mConfig.mOtherConfig.mNewEditorEnable){
			mCEditorFregment=new EditorFregment(file);
			mCEditorFregment.setChangeFlagChanged(this);
			mCEditorFregment.setOnRunButtonClickListener(this);
		}else{
			mNewCEditorFregment=new NewEditorFregment(file);
			mNewCEditorFregment.setChangeFlagChanged(this);
			mNewCEditorFregment.setOnRunButtonClickListener(this);
		}
		mWindowsManager.addListener(this);
	}
	
	public CEditor(WindowsManager windowsManager) {
		mWindowsManager=windowsManager;
		
		if(!Setting.mConfig.mOtherConfig.mNewEditorEnable){
			mCEditorFregment=new EditorFregment();
			mCEditorFregment.setChangeFlagChanged(this);
			mCEditorFregment.setOnRunButtonClickListener(this);
		}else{
			mNewCEditorFregment=new NewEditorFregment();
			mNewCEditorFregment.setChangeFlagChanged(this);
			mNewCEditorFregment.setOnRunButtonClickListener(this);
		}
		mWindowsManager.addListener(this);
	}

	public File getFile(){
		if(mCEditorFregment!=null)
			return mCEditorFregment.getFile();
		if(mNewCEditorFregment!=null)
			return mNewCEditorFregment.getFile();
		return null;
	}
	
	public boolean isChanged(){
		if(mCEditorFregment!=null)
			return mCEditorFregment.isChanged();
		if(mNewCEditorFregment!=null)
			return mNewCEditorFregment.isChanged();
		return false;
	}
	
	public int getSelectionStart(){
		if(mCEditorFregment!=null)
			return mCEditorFregment.getSelectionStart();
		if(mNewCEditorFregment!=null)
			return mNewCEditorFregment.getSelectionStart();
		return 0;
	}

	public int getSelectionEnd(){
		if(mCEditorFregment!=null)
			return mCEditorFregment.getSelectionEnd();
		if(mNewCEditorFregment!=null)
			return mNewCEditorFregment.getSelectionEnd();
		return 0;
	}
	
	public void closeInputMethod(){
		if(mCEditorFregment!=null)
			mCEditorFregment.closeInputMethod();
		if(mNewCEditorFregment!=null)
			mNewCEditorFregment.closeInputMethod();
	}
	
	public Context getContext(){
		return mWindowsManager.getContext();
	}
	
	public CharSequence getText(int resId){
		Context context = getContext();
		if(context != null)
			return context.getText(resId);
		return null;
	}
	
	public void codeFormat(){
		if(mCEditorFregment!=null)
			mCEditorFregment.codeFormat();
		if(mNewCEditorFregment!=null)
			mNewCEditorFregment.codeFormat();
	}
	
	public boolean save(){
		if(mCEditorFregment!=null)
			return mCEditorFregment.save();
		if(mNewCEditorFregment!=null)
			return mNewCEditorFregment.save();
		return true;
	}

	public void setInitSelection(int start,int end){
		if(mCEditorFregment!=null)
			mCEditorFregment.setInitSelection(start,end);
		if(mNewCEditorFregment!=null)
			mNewCEditorFregment.setInitSelection(start,end);
	}
	
	@Override
	public Fragment getFragment() {
		if(mCEditorFregment!=null)
			return mCEditorFregment;
		if(mNewCEditorFregment!=null)
			return mNewCEditorFregment;
		return null;
	}

	@Override
	public CharSequence getTitle(Context context) {
		if(getFile()==null)
			return context.getText(R.string.no_title);
		if(mCEditorFregment!=null)
			return (mCEditorFregment.isChanged()?"*":"")+mCEditorFregment.getFile().getName();
		if(mNewCEditorFregment!=null)
			return (mNewCEditorFregment.isChanged()?"*":"")+mNewCEditorFregment.getFile().getName();
		return "";
	}

	@Override
	public boolean onBackPressed() {
		return false;
	}
	
	@Override
	public boolean canAddNewWindow(Window window) {
		if(window instanceof CEditor )
		{
			CEditor editor=(CEditor) window;
			if(getFile()!=null && editor.getFile()!=null && getFile().equals(editor.getFile()))
				return false;
		}
		return true;
	}

	@Override
	public boolean onClose() {
		mWindowsManager.removeListener(this);
		closeInputMethod();
		if(mIsAlive)
		if(isChanged() && getFile()!=null)
		{
			Builder alertDialog=new AlertDialog.Builder(mWindowsManager.getContext());
			alertDialog.setCancelable(false);
			alertDialog.setTitle(R.string.file_not_save);
			alertDialog.setMessage( 
					mWindowsManager.getContext().getText(R.string.file)+
					getFile().getName()+
					mWindowsManager.getContext().getText(R.string.unsaved)+"\n"+
					getFile().getPath());
			alertDialog.setNegativeButton(
					mWindowsManager.getContext().getText(R.string.cancel)
					, this);
			alertDialog.setNeutralButton(
					mWindowsManager.getContext().getText(R.string.save_and_quit)
					, this );
			alertDialog.setPositiveButton(
					mWindowsManager.getContext().getText(R.string.force_close)
					, this);
			alertDialog.create();
			alertDialog.show();
			return false;
		}
		return true;
	}

	@SuppressLint("DefaultLocale")
	@Override
	public List<MenuTag> getMenuTags() {
		LinkedList<MenuTag> menuTags=new LinkedList<MenuTag>();
		File file=getFile();
		if(file!=null)
		{
			String name = file.getName().toLowerCase();
			if(name.equals("makefile"))
			{
				menuTags.add(new MenuTag( R.string.make_j8,getText(R.string.make_j8)));
				menuTags.add(new MenuTag( R.string.make_b_j8,getText(R.string.make_b_j8)));
				menuTags.add(new MenuTag( R.string.make_clean,getText(R.string.make_clean)));
				menuTags.add(new MenuTag( R.string.make_option,getText(R.string.make_option)));
				return menuTags;
			}
			
			if(name.endsWith(".lua"))
			{
				menuTags.add(new MenuTag( R.string.run_lua,getText(R.string.run_lua)));
				return menuTags;
			}
			if(name.endsWith(".sh"))
			{
				menuTags.add(new MenuTag( R.string.run_shell, getText(R.string.run_shell) ));
				menuTags.add(new MenuTag( R.string.run_shell_as_root, getText(R.string.run_shell_as_root )));
				return menuTags;
			}
			
			if( name.endsWith(".php")||
					name.endsWith(".html")||
					name.endsWith(".htm") )
			{
				menuTags.add(new MenuTag(R.string.remote_browsing,getText(R.string.remote_browsing)));
				menuTags.add(new MenuTag(R.string.local_browsing,getText(R.string.local_browsing)));
			}
			
			if( name.endsWith(".c")||
					name.endsWith(".cpp")||
					name.endsWith(".s")||
					name.endsWith(".h")||
					name.endsWith(".hp")||
					name.endsWith(".hpp")||
					name.endsWith(".xml")||
					name.endsWith(".project") )
			{
				CProject cProject=CProject.findCProjectByFile(getFile());
				if(cProject!=null)
				{/*
					if(cProject.isGuiProject()&&(cProject.getDebugType()!=null&&cProject.getDebugType().length()>0))
					{
					}
					//else*/
					{
						menuTags.add(new MenuTag(R.string.build_and_run,getText(R.string.build_and_run) ));
					}
					
					menuTags.add(new MenuTag( R.string.pack_and_run, getText(R.string.pack_and_run) ));
				}
				else
				{
					menuTags.add(new MenuTag(R.string.build_and_run,getText(R.string.build_and_run) ));
				}
				menuTags.add(new MenuTag(R.string.build_so,getText(R.string.build_so) ));
				if(cProject!=null){
					menuTags.add(new MenuTag(R.string.build_so_exclude_cur,getText(R.string.build_so_exclude_cur) ));
					menuTags.add(new MenuTag(R.string.build_static_lib_a,getText(R.string.build_static_lib_a) ));
					menuTags.add(new MenuTag(R.string.build_a_exclude_cur,getText(R.string.build_a_exclude_cur) ));
				}
				
				if(name.endsWith(".c")||
						name.endsWith(".cpp"))
				{
					menuTags.add(new MenuTag(R.string.complie_to_s,getText(R.string.complie_to_s) ));
					menuTags.add(new MenuTag(R.string.code_format, getText(R.string.code_format)));
				}
				if(cProject!=null)
				{
					menuTags.add(new MenuTag(R.string.clean_objs, getText(R.string.clean_objs)));
				}
			}
		}
		
		return menuTags;
	}
	
	@Override
	public boolean onMenuItemClick(int id) {
		switch (id) {
		case R.string.make_j8:
			{
				String cmd = "";
				cmd = "cd \"" + getFile().getParent() + "\"\n" +
						"make -j8\n";
				mWindowsManager.addWindow(new Console(mWindowsManager,cmd,true,getFile().getParent(),getFile().getPath()));
			}
			break;
		case R.string.make_b_j8:
			{
				String cmd = "";
				cmd = "cd \"" + getFile().getParent() + "\"\n" +
						"make -B -j8\n";
				mWindowsManager.addWindow(new Console(mWindowsManager,cmd,true,getFile().getParent(),getFile().getPath()));
			}
			break;
		case R.string.make_clean:
			{
				String cmd = "";
				cmd = "cd \"" + getFile().getParent() + "\"\n" +
						"make clean\n";
				mWindowsManager.addWindow(new Console(mWindowsManager,cmd,true,getFile().getParent(),getFile().getPath()));
			}
			break;
		case R.string.make_option:
			{
				String cmd = "";
				cmd = "cd \"" + getFile().getParent() + "\"\n";
				mWindowsManager.addWindow(new Console(mWindowsManager,cmd,true,getFile().getParent(),getFile().getPath()));
			}
			break;
		case R.string.remote_browsing:
		case R.string.local_browsing:
		case R.string.pack_and_run:
		case R.string.build_and_run:
		case R.string.run_shell:
		case R.string.run_shell_as_root:
		case R.string.complie_to_s:
		case R.string.build_so:
			if(isChanged())
			if(!save()){
				Toast.makeText(mWindowsManager.getContext(), R.string.save_fail, Toast.LENGTH_SHORT).show();
			}
			break;
		default:
			break;
		}
		
		CProject cProject;
		File file = getFile();
		switch(id){
		case R.string.remote_browsing:
			if(file!=null)
			{
				PHPConfig config=PHPConfig.load(mWindowsManager.getContext());
				String url=config.getUrl(file);
				if(url==null)
					Toast.makeText(mWindowsManager.getContext(), "该文件不在网站目录内!", Toast.LENGTH_SHORT).show();
				else
					mWindowsManager.addWindow(new BrowserWindow(mWindowsManager, "http://127.0.0.1:"+config.HTTPD_PORT+"/"+url, ""+config.HTTPD_PORT+":"+file.getName() ));
			}
			break;
		case R.string.local_browsing:
			if(file!=null)
			{
				mWindowsManager.addWindow(new BrowserWindow(mWindowsManager, "file://"+getFile().getAbsolutePath(), "file:"+file.getName() ));
			}
			break;
		case R.string.pack_and_run:
			cProject=CProject.findCProjectByFile(getFile());
			if(cProject!=null )
			{
				new PackageApk(mWindowsManager.getContext(), cProject).start();
			}
			break;
		case R.string.build_and_run:
			if(getFile()!=null)
			{
				cProject=CProject.findCProjectByFile(getFile());
				String cmd="";
				String processName=null;
				if(cProject!=null)
				{
					if(cProject.isGuiProject())
					{
						new DebugApk(mWindowsManager.getContext(), cProject).start();
						break;
					}
					else
					{
						List<File > files=cProject.getAllCFiles();
						if(files.size()>0)
						{

							cmd+=GNUCCompiler2.getCompilerCmd( mWindowsManager.getContext(),cProject,false,null);
							cmd+=GNUCCompiler.getRunCmd(mWindowsManager.getContext(), new File(cProject.getBinFilePath()),null,cProject.getPath());
							//cmd=GNUCCompiler.getProjectCompilerAndRunCmd(mWindowsManager.getContext(), files, new File(cProject.getBinFilePath()),  cProject.getOtherOption() );
							processName=GNUCCompiler.getRunCmdProcessName();
						}
						else
							cmd="echo '"+
									mWindowsManager.getContext().getText(R.string.c_file_not_found)+
									"'\n";
					}
				}
				else{
					//not a project 
					cmd = GNUCCompiler.getCompilerAndRunCmd(mWindowsManager.getContext(), getFile(),null);
					processName=GNUCCompiler.getRunCmdProcessName();
					//cmd=TinyCCompiler.getCompilerAndRunCmd(mWindowsManager.getContext(), getFile(),null);
				}
				Console console=new Console(mWindowsManager,cmd,true,getFile().getParent(), cProject!=null ? cProject.getBinFilePath(): getFile().getPath() );
				console.setKillProcessName(processName);
				mWindowsManager.addWindow(console);
			}
			break;
		case R.string.run_shell:
			if(getFile()!=null)
			{
				String cmd="";
				cmd+=GNUCCompiler.getRunCmd(getContext(), getFile());
				mWindowsManager.addWindow(new Console(mWindowsManager,cmd,true,getFile().getParent(),getFile().getPath()));
			}
			break;
		case R.string.run_shell_as_root:
			if(getFile()!=null)
			{
				String cmd="";
				//cmd+="export APP_PATH=\""+mWindowsManager.getContext().getFilesDir().getAbsolutePath()+"\"\n";
				cmd+=GNUCCompiler.getRunCmd(getContext(), getFile());
				mWindowsManager.addWindow(new Console(mWindowsManager,cmd,true,true,getFile().getParent(),getFile().getPath()));
			}
			break;
		case R.string.complie_to_s:
			if(getFile()!=null)
			{
				cProject=CProject.findCProjectByFile(getFile());
				String cmd="";
				if(cProject!=null)
				{
					List<File > files=cProject.getAllCFiles();
					if(files.size()>0)
					{
						file = getFile();
						File fileTo = new File(cProject.getBinPath()+"/"+file.getName()+".s");
						cmd = GNUCCompiler.getCompilerSCmd(mWindowsManager.getContext(),file ,fileTo,null);
					}
					else
						cmd="echo '"+
								mWindowsManager.getContext().getText(R.string.c_file_not_found)+
								"'\n";
				}else
				{
					file = getFile();
					File fileTo = new File(file.getAbsolutePath()+".s");
					cmd = GNUCCompiler.getCompilerSCmd(mWindowsManager.getContext(),file ,fileTo,null);
				}
				mWindowsManager.addWindow(new Console(mWindowsManager,cmd,true,getFile().getParent() , getFile().getPath() ));
			}
			break;
		case R.string.build_so:
			if(getFile()!=null)
			{
				cProject=CProject.findCProjectByFile(getFile());
				String cmd="";
				if(cProject!=null)
				{
					List<File > files=cProject.getAllCFiles();
					if(files.size()>0)
					{
						cmd+=GNUCCompiler2.getCompilerCmd( mWindowsManager.getContext(),cProject,true,null);
						//cmd=GNUCCompiler.getProjectCompilerSoCmd(mWindowsManager.getContext(), files, new File(cProject.getSoFilePath()), cProject.getOtherOption() );
					}
					else
						cmd="echo '"+
								mWindowsManager.getContext().getText(R.string.c_file_not_found)+
								"'\n";
				}else
				{
					cmd = GNUCCompiler.getCompilerSoCmd(mWindowsManager.getContext(), getFile(),null);
				}
				mWindowsManager.addWindow(new Console(mWindowsManager,cmd,true,getFile().getParent() , cProject!=null ? cProject.getSoFilePath(): getFile().getPath()));
			}
			break;
		case R.string.build_so_exclude_cur:
			if(getFile()!=null)
			{
				cProject=CProject.findCProjectByFile(getFile());
				String cmd="";
				if(cProject!=null)
				{
					List<File > files=cProject.getAllCFiles();
					if(files.size()>0)
					{
						cmd+=GNUCCompiler2.getCompilerCmd( mWindowsManager.getContext(),cProject,true,getFile());
						//cmd=GNUCCompiler.getProjectCompilerSoCmd(mWindowsManager.getContext(), files, new File(cProject.getSoFilePath()), cProject.getOtherOption() );
					}
					else
						cmd="echo '"+
								mWindowsManager.getContext().getText(R.string.c_file_not_found)+
								"'\n";
				}
				mWindowsManager.addWindow(new Console(mWindowsManager,cmd,true,getFile().getParent() , cProject!=null ? cProject.getSoFilePath(): getFile().getPath()));
			}
			break;
		case R.string.build_static_lib_a:
			if(getFile()!=null)
			{
				cProject=CProject.findCProjectByFile(getFile());
				String cmd="";
				if(cProject!=null)
				{
					List<File > files=cProject.getAllCFiles();
					if(files.size()>0)
					{
						cmd+=GNUCCompiler2.getCompilerACmd( mWindowsManager.getContext(),cProject,null);
					}
					else
						cmd="echo '"+
								mWindowsManager.getContext().getText(R.string.c_file_not_found)+
								"'\n";
				}
				mWindowsManager.addWindow(new Console(mWindowsManager,cmd,true,getFile().getParent() , cProject!=null ? cProject.getSoFilePath(): getFile().getPath()));
			}
			break;
		case R.string.build_a_exclude_cur:
			if(getFile()!=null)
			{
				cProject=CProject.findCProjectByFile(getFile());
				String cmd="";
				if(cProject!=null)
				{
					List<File > files=cProject.getAllCFiles();
					if(files.size()>0)
					{
						cmd+=GNUCCompiler2.getCompilerACmd( mWindowsManager.getContext(),cProject,getFile());
					}
					else
						cmd="echo '"+
								mWindowsManager.getContext().getText(R.string.c_file_not_found)+
								"'\n";
				}
				mWindowsManager.addWindow(new Console(mWindowsManager,cmd,true,getFile().getParent() , cProject!=null ? cProject.getSoFilePath(): getFile().getPath()));
			}
			break;
		case R.string.code_format:
			codeFormat();
			break;
		case R.string.clean_objs:
			if(getFile()!=null)
			{
				cProject=CProject.findCProjectByFile(getFile());
				if(cProject!=null)
				{
					FileWork.deleteFile(new File(cProject.getObjPath()));
				}
			}
			break;
		case R.string.run_lua:
			if(file!=null)
			{
				String cmd="cd \""+file.getParent()+"\"\n";
				cmd+="lua \""+file.getAbsolutePath()+"\"\n";
				mWindowsManager.addWindow(new Console(mWindowsManager,cmd,true,getFile().getParent(),getFile().getPath()));
			}
			break;
		}
		return true;
	}
	
	@Override
	public void onChangeFlagChanged() {
		mWindowsManager.onTitleChanged(this);
	}

	@Override
	public void onClick(DialogInterface dialog, int which) {
		switch (which) {
		case DialogInterface.BUTTON_NEGATIVE://cancel 
			
			break;
		case DialogInterface.BUTTON_NEUTRAL://quit & save
			if(save()){
				mIsAlive=false;
				mWindowsManager.closeWindow(this);
			}else{
				Toast.makeText(mWindowsManager.getContext(), R.string.save_fail, Toast.LENGTH_SHORT).show();
			}
			break;
		case DialogInterface.BUTTON_POSITIVE://focre close 
			mIsAlive=false;
			mWindowsManager.closeWindow(this);
			break;

		default:
			break;
		}
	}

	@Override
	public void onChangeWindow(WindowsManager manager) {
		closeInputMethod();
	}

	@Override
	public void onAddWindow(WindowsManager manager, WindowPointer pointer) {
	}

	@Override
	public void onCloseWindow(WindowsManager manager, WindowPointer pointer) {
	}

	@SuppressLint("DefaultLocale")
	@Override
	public boolean onRunButtonClick() {
		
		File file=getFile();
		if(file!=null)
		{
			String name = file.getName().toLowerCase();
			

			if(name.equals("makefile"))
			{
				this.onMenuItemClick(R.string.make_j8);
				return true;
			}
			
			if(name.endsWith(".lua"))
			{
				this.onMenuItemClick(R.string.run_lua);
				return true;
			}
			
			if(name.endsWith(".sh"))
			{
				this.onMenuItemClick(R.string.run_shell);
				return true;
			}
			
			if( name.endsWith(".php")||
					name.endsWith(".html")||
					name.endsWith(".htm") )
			{
				this.onMenuItemClick(R.string.remote_browsing);
				return true;
			}
			
			if( name.endsWith(".c")||
					name.endsWith(".cpp")||
					name.endsWith(".s")||
					name.endsWith(".h")||
					name.endsWith(".hp")||
					name.endsWith(".hpp")||
					name.endsWith(".xml")||
					name.endsWith(".project") )
			{
				CProject cProject=CProject.findCProjectByFile(getFile());
				if(cProject!=null)
				{
					/*
					if(cProject.isGuiProject()&&(cProject.getDebugType()!=null&&cProject.getDebugType().length()>0))
					{
						this.onMenuItemClick(R.string.pack_and_run);
					}
					else*/
					this.onMenuItemClick(R.string.build_and_run);
					return true;
				}
				else
				{
					this.onMenuItemClick(R.string.build_and_run);
					return true;
				}
			}
		}
		
		return false;
	}
	
	public void setText(String text){
		if(mCEditorFregment!=null)
			mCEditorFregment.setText(text);
		if(mNewCEditorFregment!=null)
			mNewCEditorFregment.setText(text);
	}
	
	@Override
	public String[] getResumeCmd() {
		String []cmd = new String[3];
		cmd[0] = getFile()!=null?getFile().getPath():null;
		cmd[1] = ""+getSelectionStart();
		cmd[2] = ""+getSelectionEnd();
		return cmd;
	}

	@Override
	public void resumeByCmd(String []cmd) {
		if(cmd==null)
			return;
		if(cmd.length==2){
			if(!Setting.mConfig.mOtherConfig.mNewEditorEnable){
				mCEditorFregment = new EditorFregment(new File(cmd[0]));
				mCEditorFregment.setChangeFlagChanged(this);
				mCEditorFregment.setOnRunButtonClickListener(this);
			}else{
				mNewCEditorFregment = new NewEditorFregment(new File(cmd[0]));
				mNewCEditorFregment.setChangeFlagChanged(this);
				mNewCEditorFregment.setOnRunButtonClickListener(this);
			}
		}
		if(cmd.length==3){
			if(!Setting.mConfig.mOtherConfig.mNewEditorEnable){
				mCEditorFregment = new EditorFregment(new File(cmd[0]));
				mCEditorFregment.setChangeFlagChanged(this);
				mCEditorFregment.setOnRunButtonClickListener(this);
			}else{
				mNewCEditorFregment = new NewEditorFregment(new File(cmd[0]));
				mNewCEditorFregment.setChangeFlagChanged(this);
				mNewCEditorFregment.setOnRunButtonClickListener(this);
			}
			int start = 0;
			int end = 0;
			try {
				start = Integer.parseInt(cmd[1]);
				end = Integer.parseInt(cmd[2]);
			} catch (Exception e) {
			}
			setInitSelection(start, end);
		}
	}

}
