package jackpal.androidterm;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

import jackpal.androidterm.emulatorview.EmulatorView;
import jackpal.androidterm.emulatorview.TermSession;
import jackpal.androidterm.emulatorview.TermSession.FinishCallback;
import jackpal.androidterm.util.TermSettings;
import person.wangchen11.drawable.CircleDrawable;

import android.annotation.SuppressLint;
import android.app.Fragment;
import android.content.Context;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.util.DisplayMetrics;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.RelativeLayout;

public class TermFragment extends Fragment implements FinishCallback{
	private TermSession mTermSession = null;
	private String mInitCmd="cd;clear;\n";
	private EmulatorView mEmulatorView = null;
	private Handler mHandler = null;
	private String mChangePS1Cmd = new File("/system/bin/basename").canExecute() ? "export PS1='$USER:`basename \"$PWD\"`\\$';" : "";
	private ViewGroup mControlLayout = null;
	public TermFragment() {
		super();
	}

	private static final int R_id_termButton_1 = 1001;
	private static final int R_id_termButton_2 = 1002;
	private static final int R_id_termButton_3 = 1003;
	private static final int R_id_termButton_4 = 1004;
	private static final int R_id_termButton_5 = 1005;

	private static final int R_id_termButton_6 = 1006;
	private static final int R_id_termButton_7 = 1007;
	private static final int R_id_termButton_8 = 1008;
	private static final int R_id_termButton_9 = 1009;
	private static final int R_id_termButton_10 = 1010;

	private int [][] mButonMap = {
			//id	isdown	char	key
			{R_id_termButton_1,0,0,KeyEvent.KEYCODE_ESCAPE},
			{R_id_termButton_1,0,0,KeyEvent.KEYCODE_TAB},
			{R_id_termButton_3,0,0,KeyEvent.KEYCODE_DPAD_UP},
			{R_id_termButton_4,0,'$',0},
			{R_id_termButton_5,0,'`',0},
			
			{R_id_termButton_6,0,0,KeyEvent.KEYCODE_CTRL_LEFT},
			{R_id_termButton_7,0,0,KeyEvent.KEYCODE_DPAD_LEFT},
			{R_id_termButton_8,0,0,KeyEvent.KEYCODE_DPAD_DOWN},
			{R_id_termButton_9,0,0,KeyEvent.KEYCODE_DPAD_RIGHT},
			{R_id_termButton_10,0,'\'',0},
	};
	private int mFristKeyDelay = 200;
	private int mKeyDelay = 60;
	
	private Runnable mKeyLoopRunnable = null;
	private void setAllViewListener(View view){
		mHandler = new Handler();
		if(view instanceof ViewGroup){
			ViewGroup viewGroup = (ViewGroup) view;
			for(int i=0;i<viewGroup.getChildCount();i++){
				setAllViewListener(viewGroup.getChildAt(i));
			}
		}

		if(view instanceof ImageButton){
			view.setOnClickListener(new View.OnClickListener() {
				@Override
				public void onClick(View v) {
					if(mControlLayout.getVisibility() == View.VISIBLE){
						mControlLayout.setVisibility(View.GONE);
					}else{
						mControlLayout.setVisibility(View.VISIBLE);
					}
				}
			});
			view.setOnTouchListener(new View.OnTouchListener() {
				@SuppressWarnings("deprecation")
				@SuppressLint("ClickableViewAccessibility")
				@Override
				public boolean onTouch(View v, MotionEvent event) {
					switch(event.getAction() ){
					case MotionEvent.ACTION_DOWN :
						v.setBackgroundDrawable(new CircleDrawable(Color.rgb(0x80, 0x80, 0xb0)));
						break;
						
					case MotionEvent.ACTION_UP :
					case MotionEvent.ACTION_OUTSIDE :
					case MotionEvent.ACTION_CANCEL :
						v.setBackgroundColor(Color.TRANSPARENT);
						break;
					}
					return false;
				}
			});
		}
		
		if(view instanceof Button){
			view.setOnTouchListener(new View.OnTouchListener() {				
				@SuppressLint("ClickableViewAccessibility")
				@Override
				public boolean onTouch(View v, MotionEvent event) {
					mButonMap[5][3] = mEmulatorView.getControlKeyCode();
					for(final int[] key:mButonMap){
						if(key[0]==v.getId()){
							switch (event.getAction()) {
							case MotionEvent.ACTION_DOWN:
								key[1] = 1;
								break;
							case MotionEvent.ACTION_UP:
								key[1] = 0;
								break;
							default:
								break;
							}
							
							if(key[2]!=0){
								if(event.getAction()==MotionEvent.ACTION_DOWN){
									mEmulatorView.getTermSession().write(key[2]);
									if(mKeyLoopRunnable!=null){
										mHandler.removeCallbacks(mKeyLoopRunnable);
									}
									mKeyLoopRunnable = new Runnable() {
										@Override
										public void run() {
											if(key[1] == 1){
												mEmulatorView.getTermSession().write(key[2]);
												mHandler.postDelayed(this,mKeyDelay);
											}
										}
									};
									mHandler.postDelayed(mKeyLoopRunnable,mFristKeyDelay);
								}
							}else
							switch (event.getAction()) {
							case MotionEvent.ACTION_DOWN:{
									final KeyEvent keyEvent = new KeyEvent(KeyEvent.ACTION_DOWN, key[3]);
									mEmulatorView.onKeyDown(keyEvent.getKeyCode(), keyEvent);
									if(mKeyLoopRunnable!=null){
										mHandler.removeCallbacks(mKeyLoopRunnable);
									}
									mKeyLoopRunnable = new Runnable() {
										@Override
										public void run() {
											if(key[1] == 1){
												mEmulatorView.onKeyDown(keyEvent.getKeyCode(), keyEvent);
												mHandler.postDelayed(this,mKeyDelay);
											}
										}
									};
									mHandler.postDelayed(mKeyLoopRunnable,mFristKeyDelay);
								}
								break;

							case MotionEvent.ACTION_UP:{
									final KeyEvent keyEvent = new KeyEvent(KeyEvent.ACTION_UP, key[3]);
									mEmulatorView.onKeyUp(keyEvent.getKeyCode(), keyEvent);
								}
								break;
							default:
								break;
							}
						}
					}
					
					return v.onTouchEvent(event);
				}
			});
		}
	}
	
	public String getInitCmdEx(String cmd)
	{
		if(cmd == null||cmd.length()<0)
			cmd="";
		cmd = "cd;"+mChangePS1Cmd+cmd;
		String ret = "clear;";//"cd $HOME\n";
		File file = getNextRunnableSh(this.getActivity());
		try {
			FileOutputStream fileOutputStream = new FileOutputStream(file);
			try {
				fileOutputStream.write(cmd.getBytes());
				fileOutputStream.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}
		file.setExecutable(true, false);
		ret += ". "+file.getAbsolutePath();
		return ret;
	}
	
	private static int mShNumber = 0;
	private static File getNextRunnableSh(Context context)
	{
		String runnablePath = context.getFilesDir().getAbsolutePath();
		File []files = new File(runnablePath).listFiles();
		if(files!=null)
		for (File file2 : files) {
			if(file2.getName().endsWith(".tsh"))
				file2.delete();
		}
		File file = null;
		for(int i=0;i<1000;i++)
		{
			file = new File(runnablePath+"/"+mShNumber+".tsh");
			mShNumber++;
			if(!file.isFile())
				break;
		}
		return file;
	}
	
	@SuppressLint("InflateParams")
	@Override
	public View onCreateView(LayoutInflater inflater,
			ViewGroup container, Bundle savedInstanceState) {
		Context context = inflater.getContext();
		RelativeLayout relativeLayout = new RelativeLayout(context);
		relativeLayout.setLayoutParams(new ViewGroup.LayoutParams( ViewGroup.LayoutParams.MATCH_PARENT,ViewGroup.LayoutParams.MATCH_PARENT));
		RelativeLayout workSpaceLayout = new RelativeLayout(context);
		relativeLayout.addView(workSpaceLayout,ViewGroup.LayoutParams.MATCH_PARENT,ViewGroup.LayoutParams.MATCH_PARENT);

		mTermSession = createTermSession();
		mTermSession.setFinishCallback(this);
		mEmulatorView = createEmulatorView(mTermSession);
		workSpaceLayout.addView(mEmulatorView, ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
		//mControlLayout = (ViewGroup) relativeLayout.findViewById(ID_LAYOUT_LAYOUT_CONTROL);
		//mControlLayout.setVisibility(View.GONE);
		setAllViewListener(relativeLayout);
		return relativeLayout;
	}


    private EmulatorView createEmulatorView(TermSession session) {
        DisplayMetrics metrics = new DisplayMetrics();
        getActivity().getWindowManager().getDefaultDisplay().getMetrics(metrics);
        TermView termView = new TermView(getActivity(), session, metrics);
        termView.updatePrefs(mTermSettings);
        EmulatorView emulatorView = termView;
        registerForContextMenu(emulatorView);
        return emulatorView;
    }

    private TermSettings mTermSettings = null;
    private TermSession createTermSession() {
    	TermSettings settings = new TermSettings(getResources(), getActivity().getPreferences(0));
    	mTermSettings = settings;
    	mTermSettings.setHomePath(getActivity().getFilesDir().getPath());
    	TermSession session = createTermSession(getActivity(), settings, getInitCmdEx(mInitCmd) );
        return session;
    }
    
    protected static TermSession createTermSession(Context context, TermSettings settings, String initialCommand) {
        ShellTermSession session = new ShellTermSession(settings, initialCommand);
        session.initializeEmulator(64, 64);
        session.setProcessExitMessage("do you want exit?");
        return session;
    }
    
    public void destory()
    {
    	if(mTermSession.isRunning())
    		mTermSession.finish();
    }

	@Override
	public void onSessionFinish(TermSession session) {
		if(mFinishCallback!=null)
			mFinishCallback.onSessionFinish(session);
	}
	
	private TermSession.FinishCallback mFinishCallback = null;
	public void setFinishCallback(TermSession.FinishCallback callback)
	{
		mFinishCallback = callback;
	}

}
