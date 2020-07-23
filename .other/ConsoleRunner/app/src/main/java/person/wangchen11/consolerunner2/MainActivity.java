package person.wangchen11.consolerunner2;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

import jackpal.androidterm.ShellTermSession;
import jackpal.androidterm.TermView;
import jackpal.androidterm.emulatorview.EmulatorView;
import jackpal.androidterm.emulatorview.TermSession;
import jackpal.androidterm.util.TermSettings;
import person.wangchen11.drawable.CircleDrawable;

public class MainActivity extends Activity {
    private static final String TAG = MainActivity.class.getSimpleName();
    private TermSession mTermSession = null;
    private EmulatorView mEmulatorView = null;
    private Handler mHandler = null;
    private String mChangePS1Cmd = new File("/system/bin/basename").canExecute() ? "export PS1='$USER:`basename \"$PWD\"`\\$';" : "";
    private LinearLayout mControlLayout = null;

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

    private static final int R_id_layoutController = 1011;
    private static final int R_id_termButton_more = 1012;

    private int [][] mButonMap = {
            //id	isdown	char	key
            {R_id_termButton_1,0,0, KeyEvent.KEYCODE_ESCAPE},
            {R_id_termButton_2,0,0,KeyEvent.KEYCODE_TAB},
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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        overridePendingTransition(0,0);
        setContentView(createView(this));
    }

    private void setAllViewListener(View view){
        mHandler = new Handler();
        if(view instanceof ViewGroup){
            ViewGroup viewGroup = (ViewGroup) view;
            for(int i=0;i<viewGroup.getChildCount();i++){
                setAllViewListener(viewGroup.getChildAt(i));
            }
        }

        if(view instanceof Button && view.getId() == R_id_termButton_more){
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
                            v.setBackgroundDrawable(new CircleDrawable(Color.rgb(0x99, 0x99, 0x99)));
                            break;
                    }
                    return false;
                }
            });
        }
        else
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
        File file = getNextRunnableSh(this);
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

    public View createView(Context context) {
        RelativeLayout relativeLayout = new RelativeLayout(context);
        relativeLayout.setBackgroundColor(Color.BLACK);
        RelativeLayout workSpaceLayout = new RelativeLayout(context);
        RelativeLayout.LayoutParams workSpaceLayoutParams = new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,ViewGroup.LayoutParams.MATCH_PARENT);
        workSpaceLayoutParams.addRule(RelativeLayout.ABOVE,R_id_layoutController);
        relativeLayout.addView(workSpaceLayout,workSpaceLayoutParams);

        {
            Button buttonMore = new Button(context);
            buttonMore.setId(R_id_termButton_more);
            buttonMore.setBackgroundDrawable(new CircleDrawable(Color.rgb(0x99, 0x99, 0x99)));
            int iconSize = getResources().getDimensionPixelSize(android.R.dimen.app_icon_size);
            RelativeLayout.LayoutParams buttonMoreParams = new RelativeLayout.LayoutParams(iconSize,iconSize);
            buttonMoreParams.rightMargin = iconSize/8;
            buttonMoreParams.bottomMargin = iconSize/8;
            buttonMoreParams.addRule(RelativeLayout.ABOVE,R_id_layoutController);
            buttonMoreParams.addRule(RelativeLayout.ALIGN_PARENT_RIGHT);
            relativeLayout.addView(buttonMore,buttonMoreParams);
        }
        mTermSession = createTermSession();
        mTermSession.setFinishCallback(new TermSession.FinishCallback() {
            @Override
            public void onSessionFinish(TermSession session) {
                finish();
            }
        });
        mEmulatorView = createEmulatorView(mTermSession);
        workSpaceLayout.addView(mEmulatorView, ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);

        LinearLayout controlLayoutParent = new LinearLayout(context);
        controlLayoutParent.setId(R_id_layoutController);
        controlLayoutParent.setOrientation(LinearLayout.VERTICAL);
        RelativeLayout.LayoutParams controlLayoutParams = new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);


        controlLayoutParams.addRule(RelativeLayout.ALIGN_PARENT_BOTTOM);
        relativeLayout.addView(controlLayoutParent,controlLayoutParams);

        mControlLayout = new LinearLayout(context);
        mControlLayout.setOrientation(LinearLayout.VERTICAL);

        controlLayoutParent.addView(mControlLayout,ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        mControlLayout.setVisibility(View.GONE);

        {
            LinearLayout linearLayout = new LinearLayout(context);
            linearLayout.setOrientation(LinearLayout.HORIZONTAL);
            mControlLayout.addView(linearLayout, ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
            {
                Button button = new Button(context);
                LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(0,ViewGroup.LayoutParams.WRAP_CONTENT);
                layoutParams.weight = 1;
                button.setId(R_id_termButton_1);
                button.setText("ESC");
                linearLayout.addView(button,layoutParams);
            }
            {
                Button button = new Button(context);
                LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(0,ViewGroup.LayoutParams.WRAP_CONTENT);
                layoutParams.weight = 1;
                button.setId(R_id_termButton_2);
                button.setText("TAB");
                linearLayout.addView(button,layoutParams);
            }
            {
                Button button = new Button(context);
                LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(0,ViewGroup.LayoutParams.WRAP_CONTENT);
                layoutParams.weight = 1;
                button.setId(R_id_termButton_3);
                button.setText("UP");
                linearLayout.addView(button,layoutParams);
            }
            {
                Button button = new Button(context);
                LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(0,ViewGroup.LayoutParams.WRAP_CONTENT);
                layoutParams.weight = 1;
                button.setId(R_id_termButton_4);
                button.setText("$");
                linearLayout.addView(button,layoutParams);
            }
            {
                Button button = new Button(context);
                LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(0,ViewGroup.LayoutParams.WRAP_CONTENT);
                layoutParams.weight = 1;
                button.setId(R_id_termButton_5);
                button.setText("`");
                linearLayout.addView(button,layoutParams);
            }
        }
        {
            LinearLayout linearLayout = new LinearLayout(context);
            linearLayout.setOrientation(LinearLayout.HORIZONTAL);
            mControlLayout.addView(linearLayout, ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
            {
                Button button = new Button(context);
                LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(0,ViewGroup.LayoutParams.WRAP_CONTENT);
                layoutParams.weight = 1;
                button.setId(R_id_termButton_6);
                button.setText("CTRL");
                linearLayout.addView(button,layoutParams);
            }
            {
                Button button = new Button(context);
                LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(0,ViewGroup.LayoutParams.WRAP_CONTENT);
                layoutParams.weight = 1;
                button.setId(R_id_termButton_7);
                button.setText("LEFT");
                linearLayout.addView(button,layoutParams);
            }
            {
                Button button = new Button(context);
                LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(0,ViewGroup.LayoutParams.WRAP_CONTENT);
                layoutParams.weight = 1;
                button.setId(R_id_termButton_8);
                button.setText("DOWN");
                linearLayout.addView(button,layoutParams);
            }
            {
                Button button = new Button(context);
                LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(0,ViewGroup.LayoutParams.WRAP_CONTENT);
                layoutParams.weight = 1;
                button.setId(R_id_termButton_9);
                button.setText("RIGHT");
                linearLayout.addView(button,layoutParams);
            }
            {
                Button button = new Button(context);
                LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(0,ViewGroup.LayoutParams.WRAP_CONTENT);
                layoutParams.weight = 1;
                button.setId(R_id_termButton_10);
                button.setText("'");
                linearLayout.addView(button,layoutParams);
            }
        }

        setAllViewListener(relativeLayout);
        return relativeLayout;
    }



    private EmulatorView createEmulatorView(TermSession session) {
        DisplayMetrics metrics = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(metrics);
        TermView termView = new TermView(this, session, metrics);
        termView.updatePrefs(mTermSettings);
        EmulatorView emulatorView = termView;
        registerForContextMenu(emulatorView);
        return emulatorView;
    }

    private TermSettings mTermSettings = null;
    private TermSession createTermSession() {
        TermSettings settings = new TermSettings(getResources(), getPreferences(0));
        mTermSettings = settings;
        mTermSettings.setHomePath(getFilesDir().getPath());
        String initCmd = "cd;clear;chmod 777 assets/elf.elf;./assets/elf.elf;echo;\n";
        TermSession session = createTermSession(this, settings, getInitCmdEx(initCmd) );
        return session;
    }

    protected static TermSession createTermSession(Context context, TermSettings settings, String initialCommand) {
        ShellTermSession session = new ShellTermSession(settings, initialCommand);
        session.initializeEmulator(64, 64);
        session.setProcessExitMessage("do you want exit?");
        return session;
    }

    @Override
    protected void onDestroy() {
        if(mTermSession!=null && mTermSession.isRunning())
            mTermSession.finish();
        super.onDestroy();
    }

    private TermSession.FinishCallback mFinishCallback = null;
    public void setFinishCallback(TermSession.FinishCallback callback)
    {
        mFinishCallback = callback;
    }

}
