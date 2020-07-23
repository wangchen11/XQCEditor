package person.wangchen11.packageapk;

import java.io.File;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import person.wangchen11.console.Console;
import person.wangchen11.console.Terminal;
import person.wangchen11.cproject.CProject;
import person.wangchen11.filebrowser.Open;
import person.wangchen11.gnuccompiler.GNUCCompiler2;
import person.wangchen11.plugins.PluginsManager;
import person.wangchen11.xqceditor.R;
import android.annotation.SuppressLint;
import android.app.ActivityManager;
import android.app.AlertDialog;
import android.app.AlertDialog.Builder;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.DialogInterface.OnClickListener;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

public class DebugApk implements OnClickListener {
	final static String TAG="DebugApk";
	private Context mContext;
	private CProject mProject;
	private AlertDialog mAlertDialog;
	private Terminal mTerminal;
	private TextView mConsoleView;
	private ScrollView mScrollView;
	private boolean mIsAlive=true;
	private static ExecutorService mExecutorService;
	private Handler mHandler=null;
	private static String mGUIRunnerPackage="person.wangchen11.guirunner";
	private static String mRequestVersion="1.0.7";

	@SuppressLint("InflateParams")
	public DebugApk(@NonNull Context context,@NonNull CProject cProject) {
		mHandler=new Handler();
		mContext=context;
		mProject=cProject;
		LayoutInflater inflater=LayoutInflater.from(context);
		ViewGroup viewGroup=(ViewGroup) inflater.inflate(R.layout.dialog_package_apk, null);
		mScrollView=(ScrollView) viewGroup.findViewById(R.id.scroll_view_package_apk);
		mConsoleView = (TextView) viewGroup.findViewById(R.id.text_view_console);
		AlertDialog.Builder builder=new Builder(context);
		builder.setTitle(R.string.build_and_run);
		builder.setView(viewGroup);
		builder.setCancelable(false);
		builder.setNegativeButton(R.string.cancel, this);
		mAlertDialog=builder.create();
		
	}
	
	private void addTextLn(String str)
	{
		addText(str+"\n");
	}

	private void addText(final String str)
	{
		mScrollView.post(new Runnable() {
			@Override
			public void run() {
				mConsoleView.setText( mConsoleView.getText().toString()+str);
				mScrollView.post(new Runnable() {
					@Override
					public void run() {
						mScrollView.fullScroll(View.FOCUS_DOWN);
					}
				});
			}
		});
	}

	private void showToast(final String str)
	{
		mScrollView.post(new Runnable() {
			@Override
			public void run() {
				Toast.makeText(mContext	, str, Toast.LENGTH_LONG).show();
			}
		});
	}
	
	public void start()
	{
		String path=mProject.getBinPath()+"/GUIRunner.apk";
		File runner = new File(path);
		runner.delete();
		runner.getParentFile().mkdirs();
		if(!hasGUIRunner())
		{
			Aapt.freeFile(mContext, "GUIRunner.apk",path);
			Open.openFile(mContext, new File(path));
			// TODO　需要修复7.0无法安装GUIRunner的问题　
			showToast("需要安装运行器！\n安装成功后请重新编译运行！");
			return ;
		}
		mIsAlive=true;
		mAlertDialog.show();
		mTerminal=new Terminal(mHandler, new Console.ConsoleCallback() {
			
			@Override
			public void onReadData(Console console, byte[] data, int len,
					boolean isError) {
				if(mIsAlive)
				{
					addText( new String(data,0,len) );
				}
			}
			@Override
			public void onConsoleClosed(Console console) {
				if(mExecutorService==null)
				{
					mExecutorService=Executors.newSingleThreadExecutor();
				}
				mExecutorService.execute(new Runnable() {
					@Override
					public void run() {
						onCompileComplete();
					}
				});
			}
		},mContext);
		String cmd=PluginsManager.getInstance().getSourceCmd();
		List<File > files=mProject.getAllCFiles();
		if(files.size()>0)
		{
			if(mProject.isGuiProject())
				cmd+=GNUCCompiler2.getCompilerCmd(mContext, mProject, true,null);
				//cmd+=GNUCCompiler.getProjectCompilerSoCmd(mContext, files, new File(mProject.getSoFilePath()), mProject.getOtherOption() );
			else
				cmd+=GNUCCompiler2.getCompilerCmd(mContext, mProject, false,null);
				//cmd+=GNUCCompiler.getProjectCompilerCmd(mContext, files, new File(mProject.getBinFilePath()), mProject.getOtherOption() );
		}
		else
			cmd+="echo '"+
					mContext.getText(R.string.c_file_not_found)+
					"'\n";
		mTerminal.execute(cmd+"\nexit\n");
	}

	private void onCompileComplete() {
		String path=mProject.getBinPath()+"/GUIRunner.apk";
		new File(path).delete();
		if(mIsAlive)
		{
			if( (mProject.isGuiProject()&&new File(mProject.getSoFilePath()).isFile()) )
			{
				addTextLn("");
				addTextLn( mContext.getString(R.string.debug_run) );
				try {
					runSo(mProject.getSoFilePath(), mProject.getAssetsPath(),mRequestVersion,mProject.getDebugType());
				} catch (Exception e) {
					e.printStackTrace();
				} catch (Error e) {
					e.printStackTrace();
				}
				mAlertDialog.dismiss();
			}
			else
			{
				addTextLn("");
				addTextLn( mContext.getString(R.string.compile_has_error) );
			}
		}
	}
	
	private boolean hasGUIRunner()
	{
        PackageManager packageManager = mContext.getPackageManager();
        try {
			PackageInfo info=packageManager.getPackageInfo(mGUIRunnerPackage, 0);
			if(info!=null&&info.versionName!=null)
			{
				if(info.versionName.equals(mRequestVersion))
					return true;
			}
		} catch (NameNotFoundException e) {
			e.printStackTrace();
		}
        return false;
	}
	
	private void killGUIRunner()
	{
		ActivityManager am = (ActivityManager) mContext.getSystemService(Context.ACTIVITY_SERVICE);
		am.killBackgroundProcesses(mGUIRunnerPackage);//it is do not work!!!
	}
	
	private void runSo(final String soPath,final String assetsPath,final String requestVersion,String debugType)
	{
		killGUIRunner();
		/*
		ComponentName componentName = new ComponentName(
				"person.wangchen11.guirunner",
				"person.wangchen11.guirunner.MainActivity");
		Intent intent = new Intent();
		intent.setComponent(componentName);
		intent.putExtra("soPath", soPath);
		intent.putExtra("assetsPath", assetsPath);
		intent.putExtra("requestVersion", requestVersion);
		intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
		mContext.startActivity(intent);*/
		/*
        PackageManager packageManager = mContext.getPackageManager();
        Intent intent = packageManager.getLaunchIntentForPackage(mGUIRunnerPackage);
		intent.putExtra("soPath", soPath);
		intent.putExtra("assetsPath", assetsPath);
		intent.putExtra("requestVersion", requestVersion);
		intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        mContext.startActivity(intent);   */
		
		PackageManager packageManager = mContext.getPackageManager();
		Intent intent = packageManager.getLaunchIntentForPackage(mGUIRunnerPackage);
		intent.setAction("android.intent.action.MAIN");
		intent.putExtra("soPath", soPath);
		intent.putExtra("assetsPath", assetsPath);
		intent.putExtra("requestVersion", requestVersion);
		intent.putExtra("debugType", debugType!=null?debugType:"");
		intent.addFlags(
				Intent.FLAG_ACTIVITY_NEW_TASK
				);
		mContext.startActivity(intent);
	}

	@Override
	public void onClick(DialogInterface dialog, int which) {
		switch (which) {
		case DialogInterface.BUTTON_NEGATIVE:
			mIsAlive=false;
			mAlertDialog.cancel();
			if(mTerminal!=null)
				mTerminal.destory();
			break;

		default:
			break;
		}
	}
}
