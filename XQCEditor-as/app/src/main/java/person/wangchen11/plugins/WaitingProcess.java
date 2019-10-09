package person.wangchen11.plugins;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import person.wangchen11.xqceditor.R;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.Context;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.TextView;

public abstract class WaitingProcess implements Runnable{
	private static ExecutorService mExecutorService = null;
	private AlertDialog mAlertDialog = null;
	private TextView mTextView = null;
	private ProgressBar mProgressBar = null;
	private ProgressBar mProgressBarEx = null;
	private Handler mHandler = null;
	
	public WaitingProcess(Context context) {
		this(context,null);
	}
	
	public WaitingProcess(Context context,int textId){
		this(context,context.getText(textId));
	}
	
	@SuppressLint("InflateParams") 
	public WaitingProcess(Context context,CharSequence title) {
		mHandler = new Handler();
		AlertDialog.Builder builder;
		LayoutInflater inflater = LayoutInflater.from(context);
		ViewGroup viewGroup = (ViewGroup) inflater.inflate(R.layout.dialog_process, null);
		mTextView = (TextView) viewGroup.findViewById(R.id.textViewMsg);
		mProgressBar = (ProgressBar) viewGroup.findViewById(R.id.progressBar_process);
		mProgressBarEx = (ProgressBar) viewGroup.findViewById(R.id.progressBarEx);
		builder=new AlertDialog.Builder(context);
		builder.setView(viewGroup);
		builder.setTitle(title);
		builder.setCancelable(false);
		mAlertDialog=builder.create();
	}
	
	public void start() {
		if(mExecutorService==null)
			mExecutorService = Executors.newSingleThreadExecutor();
		mExecutorService.execute(new Runnable() {
			@Override
			public void run() {
				mHandler.post(new Runnable() {
					@Override
					public void run() {
						mAlertDialog.show();
					}
				});
				WaitingProcess.this.run();
				mHandler.post(new Runnable() {
					@Override
					public void run() {
						onComplete();
					}
				});
			}
		});
	}
	
	public void hideProcess(){
		mHandler.post(new Runnable() {
			@Override
			public void run() {
				mProgressBar.setVisibility(View.GONE);
				mProgressBarEx.setVisibility(View.GONE);
			}
		});
	}
	
	// [0~1]
	public void setProcess(final int process){
		mHandler.post(new Runnable() {
			@Override
			public void run() {
				mProgressBar.setProgress(process);
			}
		});
	}
	
	public void setMsg(int textId) {
		setMsg(mAlertDialog.getContext().getText(textId));
	}
	
	public void setMsg(final CharSequence msg){
		mHandler.post(new Runnable() {
			@Override
			public void run() {
				mTextView.setText(msg);
			}
		});
	}

	public void addMsgLn(int textId) {
		addMsg(mAlertDialog.getContext().getText(textId)+"\n");
	}
	
	public void addMsg(int textId) {
		addMsg(mAlertDialog.getContext().getText(textId));
	}

	public void addMsgLn(final CharSequence msg){
		addMsg(msg+"\n");
	}
	public void addMsg(final CharSequence msg){
		mHandler.post(new Runnable() {
			@Override
			public void run() {
				mTextView.setText(mTextView.getText().toString()+msg);
			}
		});
	}
	
	public void setTitle(int textId){
		setTitle(mAlertDialog.getContext().getText(textId));
	}
	
	public void setTitle(final CharSequence title){
		mHandler.post(new Runnable() {
			@Override
			public void run() {
				mAlertDialog.setTitle(title);
			}
		});
	}
	
	public AlertDialog getDialog(){
		return mAlertDialog;
	}
	
	public void dismiss(){
		mAlertDialog.dismiss();
	}
	
	public void onComplete(){
		dismiss();
	}
}
