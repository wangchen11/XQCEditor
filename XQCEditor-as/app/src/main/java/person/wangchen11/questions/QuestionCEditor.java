package person.wangchen11.questions;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

import android.content.Context;

import person.wangchen11.window.Window;
import person.wangchen11.window.WindowsManager;
import person.wangchen11.window.ext.CEditor;

public class QuestionCEditor extends CEditor {
	private Question mQuestion = null;
	private boolean mIsFristFcous = true;
	private WindowsManager mWindowsManager = null;
	public QuestionCEditor(WindowsManager windowsManager,Question question) {
		super(windowsManager, new File(QuestionManager.instance().getQuestionCodeFile(question)));
		String codePath = QuestionManager.instance().getQuestionCodeFile(question);
		if(codePath!=null){
			File file = new File(codePath);
			file.getParentFile().mkdirs();
			if(!file.isFile())
			try {
				FileOutputStream fileOutputStream = new FileOutputStream(file);
				fileOutputStream.write(CCodeTemplate.mRunableCode.getBytes());
				fileOutputStream.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		mWindowsManager = windowsManager;
		mQuestion = question;
	}
	
	@Override
	public CharSequence getTitle(Context context) {
		return (isChanged()?"*":"")+mQuestion.mTitle;
	}
	
	@Override
	public boolean canAddNewWindow(Window window) {
		if(window instanceof QuestionCEditor){
			mWindowsManager.closeWindow(this);
		}
		return true;
	}
	
	@Override
	public void onChangeWindow(WindowsManager manager) {
		super.onChangeWindow(manager);
		if(manager.getSelectWindow().mWindow == this){
			if(mIsFristFcous){
				QuestionFloatWindow.startQuestionMode(manager, mQuestion);
				QuestionFloatWindow.showQuestionDialog();
				mIsFristFcous = false;
			}
			QuestionFloatWindow.showFloatBall();
		}else{
			QuestionFloatWindow.hideFloatBall();
		}
	}
	
	@Override
	public boolean onClose() {
		if(isChanged())
			save();
		mWindowsManager.removeListener(this);
		closeInputMethod();
		QuestionFloatWindow.stopQusetionMode();
		return true;
	}
}
