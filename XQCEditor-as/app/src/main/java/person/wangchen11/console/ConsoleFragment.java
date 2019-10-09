package person.wangchen11.console;

import person.wangchen11.xqceditor.R;
import android.annotation.SuppressLint;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

public class ConsoleFragment extends Fragment implements OnConsoleColseListener{
	static final String TAG="ConsoleFragment";
	private OnConsoleColseListener mColseListener;
	private ConsoleView mConsoleView;
	private boolean mNeedErrorInput=true;
	private boolean mRunAsSu=false;
	String mInitCmd=null;
	public ConsoleFragment() {
	}

	public ConsoleFragment(String cmd){
		mInitCmd=cmd;
	}

	public ConsoleFragment(String cmd,boolean needErrorInput){
		mInitCmd=cmd;
		mNeedErrorInput=needErrorInput;
	}
	
	public ConsoleFragment(String cmd,boolean needErrorInput,boolean runAsSu){
		mInitCmd=cmd;
		mNeedErrorInput=needErrorInput;
		mRunAsSu=runAsSu;
	}
	
	@SuppressLint("InflateParams")
	@Override
	public View onCreateView(LayoutInflater inflater,
			@Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
		ViewGroup view=(ViewGroup) inflater.inflate(R.layout.fragment_console, null);
		mConsoleView=((ConsoleView)view.findViewById(R.id.consoleView1));
		Log.i(TAG, "mRunAsSu:"+mRunAsSu);
		if(mRunAsSu)
		{
			mConsoleView.getConsole().execute("su\n");
		}
		mConsoleView.getConsole().execute(ConsoleInputConnection.getExportPathCmd(view.getContext()));
		mConsoleView.setConsoleCloseListener(this);
		if(!mNeedErrorInput)
			mConsoleView.getConsole().disableErrorInput();
		if(mInitCmd!=null)
			mConsoleView.getConsole().execute(mInitCmd);
		return view;
	}
	
	public void setConsoleCloseListener( OnConsoleColseListener colseListener){
		mColseListener=colseListener;
	}

	@Override
	public void onConsoleClose(Console console) {
		if(mColseListener!=null)
			mColseListener.onConsoleClose(console);
	}
	
	@Override
	public void onDestroyView() {
		mConsoleView.getConsole().destory();
		mConsoleView=null;
		super.onDestroyView();
	}
	
	@Override
	public void onDestroy() {
		super.onDestroy();
	}
	
	public void closeInputMethod(){
		if(mConsoleView!=null)
			mConsoleView.closeInputMethod();
	}
	
}
