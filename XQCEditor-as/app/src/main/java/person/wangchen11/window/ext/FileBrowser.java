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
import android.view.LayoutInflater;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.Toast;
import person.wangchen11.cproject.CProject;
import person.wangchen11.filebrowser.FileBowserFragment;
import person.wangchen11.filebrowser.OnOpenListener;
import person.wangchen11.gnuccompiler.GNUCCompiler;
import person.wangchen11.hexeditor.HexEditor;
import person.wangchen11.plugins.PluginsManager;
import person.wangchen11.util.FileUtil;
import person.wangchen11.util.ToastUtil;
import person.wangchen11.window.MenuTag;
import person.wangchen11.window.Window;
import person.wangchen11.window.WindowsManager;
import person.wangchen11.xqceditor.R;
import person.wangchen11.zipfile.ZipCompressing;

public class FileBrowser implements Window,OnOpenListener, OnClickListener{
	private FileBowserFragment mFileBowserFragment=new FileBowserFragment();
	private WindowsManager mWindowsManager;
	public FileBrowser(WindowsManager windowsManager) {
		this(windowsManager,null);
	}
	
	public FileBrowser(WindowsManager windowsManager,String path) {
		mFileBowserFragment.setOnOpenListener(this);
		mWindowsManager=windowsManager;
		mFileBowserFragment.setDefaultPath(path);
	}
	
	@Override
	public Fragment getFragment() {
		return mFileBowserFragment;
	}

	@Override
	public CharSequence getTitle(Context context) {
		return context.getText(R.string.file_browser);
	}

	@Override
	public boolean onBackPressed() {
		return mFileBowserFragment.onBackPressed();
	}
	
	AlertDialog mRunElfDialog = null;
	EditText mParamEditText = null;
	@SuppressLint({ "DefaultLocale", "InflateParams" })
	@Override
	public boolean onOpen(File file) {
		String name=file.getName().toLowerCase();
		if(name.endsWith(".elf")){
			if(mRunElfDialog==null)
			{
				AlertDialog.Builder builder = new AlertDialog.Builder(mWindowsManager.getContext());
				LayoutInflater inflater = LayoutInflater.from(mWindowsManager.getContext());
				ViewGroup viewGroup = (ViewGroup) inflater.inflate(R.layout.dialog_run_elf, null);
				mParamEditText =(EditText) viewGroup.findViewById(R.id.edit_elf_param);
				builder.setView(viewGroup);
				builder.setNegativeButton(android.R.string.cancel, this);
				builder.setPositiveButton(android.R.string.ok, this);
				mRunElfDialog=builder.create();
				mRunElfDialog.setTitle(R.string.input_param);
			}
			mParamEditText.setTag(file);
			mRunElfDialog.show();
			
			return true;
		}
		if(name.endsWith(".qplug.zip")){
			if(!PluginsManager.getInstance().installPlugin(file))
				;// TODO show install failed!
			return true;
		}
		if(name.endsWith(".hp")){
			mWindowsManager.addWindow(new CEditor(mWindowsManager,file));
			return true;
		}
		if(name.endsWith(".hpp")){
			mWindowsManager.addWindow(new CEditor(mWindowsManager,file));
			return true;
		}
		if(name.endsWith(".sh")){
			mWindowsManager.addWindow(new CEditor(mWindowsManager,file));
			return true;
		}
		if(name.endsWith(".project")){
			mWindowsManager.addWindow(new CEditor(mWindowsManager,file));
			return true;
		}
		if(name.endsWith(".c")){
			mWindowsManager.addWindow(new CEditor(mWindowsManager,file));
			return true;
		}
		if(name.endsWith(".cpp")){
			mWindowsManager.addWindow(new CEditor(mWindowsManager,file));
			return true;
		}
		if(name.endsWith(".h")){
			mWindowsManager.addWindow(new CEditor(mWindowsManager,file));
			return true;
		}
		if(name.endsWith(".java")){
			mWindowsManager.addWindow(new CEditor(mWindowsManager,file));
			return true;
		}
		if(name.endsWith(".txt")){
			mWindowsManager.addWindow(new CEditor(mWindowsManager,file));
			return true;
		}
		if(name.endsWith(".html")){
			mWindowsManager.addWindow(new CEditor(mWindowsManager,file));
			return true;
		}
		if(name.endsWith(".css")){
			mWindowsManager.addWindow(new CEditor(mWindowsManager,file));
			return true;
		}
		if(name.endsWith(".htm")){
			mWindowsManager.addWindow(new CEditor(mWindowsManager,file));
			return true;
		}
		if(name.endsWith(".xml")){
			mWindowsManager.addWindow(new CEditor(mWindowsManager,file));
			return true;
		}
		if(name.endsWith(".php")){
			mWindowsManager.addWindow(new CEditor(mWindowsManager,file));
			return true;
		}
		if(name.endsWith(".ini")){
			mWindowsManager.addWindow(new CEditor(mWindowsManager,file));
			return true;
		}
		if(name.endsWith(".js")){
			mWindowsManager.addWindow(new CEditor(mWindowsManager,file));
			return true;
		}
		if(name.endsWith(".log")){
			mWindowsManager.addWindow(new CEditor(mWindowsManager,file));
			return true;
		}
		if(name.endsWith(".s")){
			mWindowsManager.addWindow(new CEditor(mWindowsManager,file));
			return true;
		}
		if(name.endsWith(".lua")){
			mWindowsManager.addWindow(new CEditor(mWindowsManager,file));
			return true;
		}
		if(name.endsWith(".mp4") 
				|| name.endsWith(".avi")
				|| name.endsWith(".rm")
				|| name.endsWith(".rmvb")
				|| name.endsWith(".asf")
				|| name.endsWith(".wmv")
				|| name.endsWith(".mov")
				|| name.endsWith(".mpeg")
				|| name.endsWith(".mpg")
				|| name.endsWith(".dat")
				|| name.endsWith(".avi") ){
			mWindowsManager.addWindow(new VideoPlayer(file));
			return true;
		}
		if(!name.contains(".") && file.length()<1024*512 )
		{
			mWindowsManager.addWindow(new CEditor(mWindowsManager,file));
			return true;
		}
		if(name.endsWith(".qtheme")){
			if(Setting.applyTheme(mWindowsManager.getContext(), file)){
				mWindowsManager.sendConfigChanged();
				ToastUtil.showToast(R.string.change_theme_success, Toast.LENGTH_SHORT);
			}
			return true;
		}
		return false;
	}
	
	@Override
	public boolean canAddNewWindow(Window window) {
		//if(window instanceof FileBrowser)
		//	return false;
		return true;
	}

	@Override
	public boolean onClose() {
		return true;
	}

	@Override
	public List<MenuTag> getMenuTags() {
		LinkedList<MenuTag> menuTags=new LinkedList<MenuTag>();
		File []files=mFileBowserFragment.getSelectedFiles();
		if(files!=null && files.length>0)
		{
			menuTags.add(new MenuTag(R.string.edit, mWindowsManager.getContext().getResources().getText(R.string.edit)));
			menuTags.add(new MenuTag(R.string.edit_with_hex, mWindowsManager.getContext().getResources().getText(R.string.edit_with_hex)));
			menuTags.add(new MenuTag(R.string.zip, mWindowsManager.getContext().getResources().getText(R.string.zip)));
		}
		menuTags.add(new MenuTag(R.string.new_file, mWindowsManager.getContext().getResources().getText(R.string.new_file)));
		menuTags.add(new MenuTag(R.string.new_dir, mWindowsManager.getContext().getResources().getText(R.string.new_dir)));
		menuTags.add(new MenuTag(R.string.new_project, mWindowsManager.getContext().getResources().getText(R.string.new_project)));
		menuTags.add(new MenuTag(R.string.refresh, mWindowsManager.getContext().getResources().getText(R.string.refresh)));
		
		return menuTags;
	}
	
	private AlertDialog mNewProjectDialog=null;
	private EditText mNewProjectEditText=null;
	private RadioButton mRadioConsole =null;
	private RadioButton mRadioSdl =null;

	private AlertDialog mZipDialog=null;
	private EditText mZipEditText=null;
	
	@SuppressLint("InflateParams")
	@Override
	public boolean onMenuItemClick(int id) {
		File []files=mFileBowserFragment.getSelectedFiles();
		switch (id) {
		case R.string.new_file:
			if(!mFileBowserFragment.newFile())
				Toast.makeText(mWindowsManager.getContext(), R.string.craete_file_fail, Toast.LENGTH_SHORT).show();
			break;
		case R.string.new_dir:
			if(!mFileBowserFragment.newDir())
				Toast.makeText(mWindowsManager.getContext(), R.string.create_dir_fail, Toast.LENGTH_SHORT).show();
			break;
		case R.string.new_project:
			if(mNewProjectDialog==null){
				LayoutInflater inflater = LayoutInflater.from(mWindowsManager.getContext());
				ViewGroup viewGroup = (ViewGroup) inflater.inflate(R.layout.dialog_new_project, null);
				Builder builder=new AlertDialog.Builder(mWindowsManager.getContext());
				mNewProjectEditText = (EditText) viewGroup.findViewById(R.id.edit_project_name);
				mRadioConsole = (RadioButton) viewGroup.findViewById(R.id.radio_console);
				mRadioSdl = (RadioButton) viewGroup.findViewById(R.id.radio_sdl);
				builder.setView(viewGroup);
				builder.setNegativeButton(android.R.string.cancel, this);
				builder.setPositiveButton(android.R.string.ok, this);
				mNewProjectDialog=builder.create();
				mNewProjectDialog.setTitle(R.string.new_project);
				mNewProjectDialog.setCancelable(false);
			}
			
			mNewProjectDialog.show();
			break;
		case R.string.zip:
			if(files!=null && files.length>0)
			{
				if(mZipDialog==null){
					LayoutInflater inflater = LayoutInflater.from(mWindowsManager.getContext());
					ViewGroup viewGroup = (ViewGroup) inflater.inflate(R.layout.dialog_zip, null);
					Builder builder=new AlertDialog.Builder(mWindowsManager.getContext());
					mZipEditText=(EditText) viewGroup.findViewById(R.id.edit_zip_name);
					builder.setView(viewGroup);
					builder.setNegativeButton(android.R.string.cancel, this);
					builder.setPositiveButton(android.R.string.ok, this);
					mZipDialog=builder.create();
					mZipDialog.setTitle(R.string.zip);
				}
				File baseFile=null;
				if(files.length==1)
				{
					baseFile=files[0];
				}
				else
				{
					baseFile=files[0].getParentFile();
				}
				mZipEditText.setText(baseFile.getName()+".zip");
				mZipDialog.show();
				
			}
			break;
		case R.string.refresh:
			mFileBowserFragment.refresh();
			break;

		case R.string.edit:
			if(files!=null && files.length>0)
			{
				for(File file:files){
					if(file.isFile())
						mWindowsManager.addWindow(new CEditor(mWindowsManager,file));
				}
			}
			break;
		case R.string.edit_with_hex:
			if(files!=null && files.length>0)
			{
				for(File file:files){
					if(file.isFile())
						mWindowsManager.addWindow(new HexEditor(mWindowsManager,file));
				}
			}
			break;
		default:
			break;
		}
		return true;
	}

	@Override
	public void onClick(DialogInterface dialog, int which) {
		switch (which) {
		case DialogInterface.BUTTON_POSITIVE:
			if(dialog==mNewProjectDialog)
			{
				if(mRadioSdl.isChecked())
				{
					File file = new File(mFileBowserFragment.getPath()+"/"+mNewProjectEditText.getText());
					if(file.exists())
					{
						Toast.makeText(mWindowsManager.getContext(), "failed to create project:project is exists!",Toast.LENGTH_SHORT).show();
					}
					else
					{
						file.mkdirs();
						FileUtil.freeZip(mWindowsManager.getContext(), "sdl project.zip", file.getAbsolutePath());
					}
					
				}
				else
				{
					CProject cProject=CProject.newProject(mNewProjectEditText.getText().toString(), mFileBowserFragment.getPath()+File.separatorChar+mNewProjectEditText.getText(), "src", "bin",mRadioConsole.isChecked() ? false : true);
					try {
						cProject.createProject( mWindowsManager.getContext() );
					} catch (Exception e) {
						Toast.makeText(mWindowsManager.getContext(), mWindowsManager.getContext().getText( R.string.fail )+e.getMessage(), Toast.LENGTH_SHORT).show();
						e.printStackTrace();
					}
				}
				mFileBowserFragment.refresh();
			}
			else
			if(dialog==mZipDialog)
			{
				File []files=mFileBowserFragment.getSelectedFiles();
				if(files!=null && files.length>0)
				{
					try {
						String zipFile=files[0].getParent().toString()+"/"+mZipEditText.getText();
						ZipCompressing.zip(zipFile, files);
					} catch (Exception e) {
						e.printStackTrace();
					} catch (Error e) {
						e.printStackTrace();
					}
				}
				mFileBowserFragment.refresh();
			}
			else
			if(dialog==mRunElfDialog)
			{
				if(mParamEditText!=null)
				{
					Object obj = mParamEditText.getTag();
					if(obj instanceof File)
					{
						File file = (File) obj; 
						String parm = mParamEditText.getText().toString().replaceAll("\n", " ");
						Console console=new Console(mWindowsManager, GNUCCompiler.getRunCmd(mWindowsManager.getContext(), file,parm),file.getParent(),file.getPath());
						console.setKillProcessName(GNUCCompiler.getRunCmdProcessName());
						mWindowsManager.addWindow(console);
					}
				}
			}
			
			break;

		default:
			break;
		}
	}

	@Override
	public String[] getResumeCmd() {
		String[] strings = new String[1];
		strings[0] = mFileBowserFragment.getPath();
		return strings;
	}

	@Override
	public void resumeByCmd(String []cmd) throws Exception {
		if(cmd==null || cmd.length!=1 || cmd[0].equals("/") )
			throw new Exception("can not resume by cmd!");
		mFileBowserFragment.setDefaultPath(cmd[0]);
	}
}
