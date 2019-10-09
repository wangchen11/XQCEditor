package person.wangchen11.questions;

import java.util.ArrayList;
import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;
import person.wangchen11.questions.QuestionTask.OnTaskCompliteListener;
import person.wangchen11.util.PublicThreadPool;
import person.wangchen11.util.ToastUtil;
import person.wangchen11.window.WindowPointer;
import person.wangchen11.window.WindowsManager;
import person.wangchen11.window.ext.CEditor;
import person.wangchen11.window.ext.Setting;
import person.wangchen11.xqceditor.R;

@SuppressLint("InflateParams") 
public class QuestionDialog extends AlertDialog implements AlertDialog.OnClickListener, OnTaskCompliteListener {
	private Question mQuestion = null;
	private View mView = null;
	private WindowsManager mWindowsManager = null;
	private ArrayList<QuestionTask> mQuestionTasks = new ArrayList<QuestionTask>();
	private ArrayList<TestTask> mTestTasks = new ArrayList<TestTask>();
	private boolean mTesting = false;
	private Handler mHandler = null;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		setButton(BUTTON_POSITIVE, getContext().getString(R.string.commit_cur_code), this);
		//setButton(BUTTON_NEGATIVE, getContext().getString(R.string.quit_question_mode), this);
		setButton(BUTTON_NEUTRAL, getContext().getString(R.string.wait), this);
		LayoutInflater layoutInflater = LayoutInflater.from(getContext());
		mView = layoutInflater.inflate(R.layout.dialog_question, null);
		setView(mView);
		configQuestion();
		stopTest();
		super.onCreate(savedInstanceState);
	}
	
	protected QuestionDialog(Context context,Question question,WindowsManager windowsManager) {
		super(context);
		mQuestion = question;
		mWindowsManager = windowsManager;
		mHandler = new Handler();
	}
	
	public void configQuestion(){
		if(mView==null)
			return ;
		TextView textViewQuestion = (TextView) mView.findViewById(R.id.textViewQuestion);
		textViewQuestion.setText(mQuestion.getQuestion(getContext()));

		TextView textViewTitle = (TextView) mView.findViewById(R.id.textViewTitle);
		QuestionGroup questionGroup = QuestionManager.instance().getQuestionGroup(mQuestion);
		textViewTitle.setText(questionGroup.getName()+" - "+mQuestion.mTitle);
		

		TextView textViewInput1 = (TextView) mView.findViewById(R.id.textViewInput1);
		TextView textViewInput2 = (TextView) mView.findViewById(R.id.textViewInput2);
		TextView textViewInput3 = (TextView) mView.findViewById(R.id.textViewInput3);
		TextView textViewOutput1 = (TextView) mView.findViewById(R.id.textViewOutput1);
		TextView textViewOutput2 = (TextView) mView.findViewById(R.id.textViewOutput2);
		TextView textViewOutput3 = (TextView) mView.findViewById(R.id.textViewOutput3);

		textViewInput1.setText(""+mQuestion.getInput(getContext(), 0));
		textViewInput2.setText(""+mQuestion.getInput(getContext(), 1));
		textViewInput3.setText(""+mQuestion.getInput(getContext(), 2));

		textViewOutput1.setText(""+mQuestion.getOutput(getContext(), 0));
		textViewOutput2.setText(""+mQuestion.getOutput(getContext(), 1));
		textViewOutput3.setText(""+mQuestion.getOutput(getContext(), 2));

		Button buttonPre =  (Button) mView.findViewById(R.id.buttonPreQuestion);
		Button buttonAnwser =  (Button) mView.findViewById(R.id.buttonAnswer);
		Button buttonNext =  (Button) mView.findViewById(R.id.buttonNextQuestion);
		buttonPre.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				Question question = QuestionManager.instance().getPreQuestion(mQuestion);
				if(question==null){
					ToastUtil.showToast(R.string.no_more_questions, Toast.LENGTH_SHORT);
				}else{
					dismiss();
					//QuestionFloatWindow.startQuestionMode(mWindowsManager, question);
					mWindowsManager.addWindow(new QuestionCEditor(mWindowsManager,question));
				}
			}
		});
		
		buttonNext.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				Question question = QuestionManager.instance().getNextQuestion(mQuestion);
				if(question==null){
					ToastUtil.showToast(R.string.no_more_questions, Toast.LENGTH_SHORT);
				}else{
					dismiss();
					mWindowsManager.addWindow(new QuestionCEditor(mWindowsManager,question));
					//QuestionFloatWindow.startQuestionMode(mWindowsManager, question);
				}
			}
		});
		
		buttonAnwser.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				WindowPointer windowPointer = mWindowsManager.getSelectWindow();
				if(windowPointer.mWindow instanceof CEditor){
					CEditor ceditor = (CEditor) windowPointer.mWindow;
					ceditor.setText(mQuestion.getAnwser(getContext()));
					QuestionDialog.this.dismiss();
				} else {
					ToastUtil.showToast("请在c/c++代码编辑界面点击此选项!", Toast.LENGTH_SHORT);
				}
			}
		});
		if(QuestionManager.instance().isDebug()){
			buttonAnwser.setVisibility(View.VISIBLE);
		}else{
			buttonAnwser.setVisibility(View.GONE);
		}
		hideTestInfo();
	}
	
	@SuppressWarnings("deprecation")
	@Override
	public void show() {
		super.show();

		getButton(BUTTON_POSITIVE).setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				QuestionDialog.this.onClick(QuestionDialog.this, BUTTON_POSITIVE);
			}
		});
		getButton(BUTTON_NEGATIVE).setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				QuestionDialog.this.onClick(QuestionDialog.this, BUTTON_NEGATIVE);
			}
		});
		getButton(BUTTON_NEUTRAL).setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				QuestionDialog.this.onClick(QuestionDialog.this, BUTTON_NEUTRAL);
			}
		});
		
		Setting.applySettingConfigToAllView(mView);
		mView.setBackgroundDrawable(new ColorDrawable(Setting.mConfig.mEditorConfig.mBackGroundColor));
		/*
		LayoutParams attributes = getWindow().getAttributes();
		attributes.flags = LayoutParams.FLAG_DIM_BEHIND;
		attributes.dimAmount = 0.4f;
		getWindow().setAttributes(attributes);*/
	}

	@Override
	public void onClick(DialogInterface dialog, int which) {
		switch (which) {
		case BUTTON_POSITIVE:
			if(mTesting){
				ToastUtil.showToast(R.string.try_angin_later, Toast.LENGTH_SHORT);
			} else 
			{
				WindowPointer windowPointer = mWindowsManager.getSelectWindow();
				if(windowPointer.mWindow instanceof CEditor){
					CEditor ceditor = (CEditor) windowPointer.mWindow;
					if(ceditor.isChanged())
						ceditor.save();
					startTest(ceditor.getFile().getAbsolutePath());
				} else {
					ToastUtil.showToast("请在c/c++代码编辑界面点击此选项!", Toast.LENGTH_SHORT);
				}
			}
			break;
		case BUTTON_NEUTRAL:
			dismiss();
			break;
		case BUTTON_NEGATIVE:
			QuestionFloatWindow.stopQusetionMode();
			dismiss();
			break;

		default:
			break;
		}
	}
	
	public void stopTest(){
		LinearLayout layoutTestResult = (LinearLayout) mView.findViewById(R.id.layoutTestResult);
		layoutTestResult.setVisibility(View.GONE);
		synchronized (mQuestionTasks) {
			mQuestionTasks.clear();
		}
	}
	
	public void startTest(String file){
		stopTest();
		synchronized (mQuestionTasks) {
			CompileTask compileTask = new CompileTask(getContext(),"编译",this, 20*1000, file);
			mQuestionTasks.add(compileTask);
			for(int i=0;i<mQuestion.getInputCount();i++){
				mQuestionTasks.add(new TestTask(getContext(), "测试项"+(i+1), this, 1000, compileTask,
						mQuestion.getInput(getContext(), i), mQuestion.getOutput(getContext(), i)));
			}
			mQuestionTasks.add(new SumMarksTask(getContext(), "汇总", this, 1000, mQuestionTasks));
		}
		addAllTaskView();
		hideTestInfo();
		runNextTask();
		Button buttonShowTestResult = (Button) mView.findViewById(R.id.buttonShowTestResult);
		buttonShowTestResult.setOnClickListener(mOnClickListener);
		buttonShowTestResult.setVisibility(View.GONE);
	}

	private void runNextTask(){
		synchronized (mQuestionTasks) {
			final QuestionTask task = getNextTask();
			if(task!=null){
				task.mTesting = true;
				mTesting = true;
				PublicThreadPool.getPublicThreadPool().execute(task);
				mHandler.post(new Runnable() {
					@Override
					public void run() {
						updateTaskResult(task);
					}
				});
			}
		}
	}
	
	private boolean hasNextTask(){
		synchronized (mQuestionTasks) {
			return getNextTask()!=null;
		}
	}
	
	private QuestionTask getNextTask(){
		synchronized (mQuestionTasks) {
			for(QuestionTask questionTask:mQuestionTasks){
				if(!questionTask.isComplite())
					return questionTask;
			}
		}
		return null;
	}
	
	@Override
	public void onComplite(final QuestionTask questionTask) {
		mHandler.post(new Runnable() {
			@Override
			public void run() {
				updateTaskResult(questionTask);
			}
		});
		if(hasNextTask()){
			runNextTask();
		}else{
			mTesting = false;
		}
	}
	
	public void addAllTaskView(){
		LinearLayout layoutTestResult = (LinearLayout) mView.findViewById(R.id.layoutTestResult);
		layoutTestResult.setVisibility(View.VISIBLE);
		
		LinearLayout layoutResultList = (LinearLayout) mView.findViewById(R.id.layoutResultList);
		layoutResultList.removeAllViews();
		
		LayoutInflater inflater = LayoutInflater.from(getContext());
		
		synchronized (mQuestionTasks) {
			for(QuestionTask questionTask:mQuestionTasks){
				View layout = inflater.inflate(R.layout.item_test_result, null);
				layoutResultList.addView(layout);
				updateTaskResult(questionTask);
			}
		}
		Setting.applySettingConfigToAllView(layoutResultList);
	}
	
	public void updateTaskResult(QuestionTask questionTask){
		LinearLayout layoutResultList = (LinearLayout) mView.findViewById(R.id.layoutResultList);
		int index = 0;
		synchronized (mQuestionTasks) {
			index = mQuestionTasks.indexOf(questionTask);
		}
		if(index>=0&&index<layoutResultList.getChildCount()){
			View layout = layoutResultList.getChildAt(index);
			
			TextView textViewTestName = (TextView) layout.findViewById(R.id.textViewTestName);
			textViewTestName.setText(questionTask.getName());

			TextView textViewTestResult = (TextView) layout.findViewById(R.id.textViewTestResult);
			ProgressBar progressBar = (ProgressBar) layout.findViewById(R.id.progressBarTesting);
			if(questionTask.mTesting){
				textViewTestResult.setVisibility(View.INVISIBLE);
				progressBar.setVisibility(View.VISIBLE);
			}else{
				textViewTestResult.setVisibility(View.VISIBLE);
				progressBar.setVisibility(View.INVISIBLE);
			}
			if(questionTask.isComplite()){
				textViewTestResult.setText(questionTask.getCompliteMsg(getContext()));
			}else{
				textViewTestResult.setText(R.string.waiting_for_test);
			}
			textViewTestResult.setBackgroundDrawable(new ColorDrawable(questionTask.getColor()));
			TextView textViewMarks = (TextView) layout.findViewById(R.id.textViewMarks);
			textViewMarks.setText(""+questionTask.getMarks());
			if(questionTask instanceof SumMarksTask && questionTask.isComplite()){
				mQuestion.mMarks = questionTask.getMarks();
				QuestionManager.saveQuestionInfo(getContext(), mQuestion);
				Button buttonShowTestResult = (Button) mView.findViewById(R.id.buttonShowTestResult);
				buttonShowTestResult.setVisibility(View.VISIBLE);
				
				mTestTasks.clear();
				for(QuestionTask task:mQuestionTasks){
					if(task instanceof TestTask){
						mTestTasks.add((TestTask)task);
					}
				}
				mQuestionTasks.clear();
			}
		}
	}
	
	private void showTestInfo(){
		mView.findViewById(R.id.layoutTestInfo).setVisibility(View.VISIBLE);
		mView.findViewById(R.id.buttonShowTestResult).setVisibility(View.GONE);
		LinearLayout linearLayout = (LinearLayout) mView.findViewById(R.id.layoutInfoList);
		linearLayout.removeAllViews();
		Setting.applySettingConfigToAllView(linearLayout);
		LayoutInflater inflater = LayoutInflater.from(getContext());
		
			for(TestTask task:mTestTasks){
				View layout = inflater.inflate(R.layout.item_test_info, null);
				linearLayout.addView(layout);
				TextView textView2 = (TextView) layout.findViewById(R.id.textViewTestInput);
				TextView textView3 = (TextView) layout.findViewById(R.id.textViewTestOutput);
				TextView textView4 = (TextView) layout.findViewById(R.id.textViewAnswer);
				textView2.setText(task.getInput());
				textView3.setText(task.getResult());
				textView4.setText(task.getOutput());
			}
	}
	
	private void hideTestInfo(){
		mView.findViewById(R.id.layoutTestInfo).setVisibility(View.GONE);
		mView.findViewById(R.id.buttonShowTestResult).setVisibility(View.VISIBLE);
	}
	
	private View.OnClickListener mOnClickListener = new View.OnClickListener() {
		@Override
		public void onClick(View v) {
			switch (v.getId()) {
			case R.id.buttonShowTestResult:
			{
				new ShareDialog(getContext(),mQuestion,mView){
					public void onDone(boolean success) {
						Log.i("QuestionDialog", "onDone:"+success);
						showTestInfo();
					};
				}.show();
			}
				break;

			default:
				break;
			}
		}
	};
	
}
