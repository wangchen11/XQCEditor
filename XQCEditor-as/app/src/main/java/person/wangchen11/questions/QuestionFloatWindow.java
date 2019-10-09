package person.wangchen11.questions;

import android.content.Context;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewParent;
import android.widget.RelativeLayout;
import person.wangchen11.window.WindowsManager;
import person.wangchen11.xqceditor.R;

public class QuestionFloatWindow {
	private WindowsManager mWindowsManager = null;
	private QuestionFloatBall mFloatBall = null;
	private QuestionDialog mQuestionDialog = null;
	
	private QuestionFloatWindow(WindowsManager windowsManager,Question question) {
		mWindowsManager = windowsManager;
		mQuestionDialog = new QuestionDialog(mWindowsManager.getContext(),question,mWindowsManager);
		mFloatBall = new QuestionFloatBall(getContext()){
			@Override
			public boolean onSingleTapUp(MotionEvent e) {
				mQuestionDialog.show();
				return true;
			}
		};
		mFloatBall.setAlpha(0.6f);
	}
	
	public Context getContext(){
		return mWindowsManager.getContext();
	}
	
	private static View findParentViewById(View view,int id){
		if(view.getId()==id)
			return view;
		ViewParent viewParent = view.getParent();
		if(viewParent instanceof View)
			return findParentViewById((View)viewParent,id);
		return null;
	}
	
	private static QuestionFloatWindow mQuestionFloatWindow = null;
	public static void startQuestionMode(WindowsManager windowsManager,Question  question){
		QuestionManager.init(windowsManager.getContext());
		stopQusetionMode();
		mQuestionFloatWindow = new QuestionFloatWindow(windowsManager, question);
		RelativeLayout layout = (RelativeLayout) findParentViewById(mQuestionFloatWindow.mWindowsManager.getTitleListView(),R.id.editor_layout);
		layout.addView(mQuestionFloatWindow.mFloatBall);
	}
	
	public static void stopQusetionMode(){
		if(mQuestionFloatWindow!=null){
			final RelativeLayout layout = (RelativeLayout) findParentViewById(mQuestionFloatWindow.mWindowsManager.getTitleListView(),R.id.editor_layout);
			final View removeFloatBall = mQuestionFloatWindow.mFloatBall;
			layout.getHandler().post(new Runnable() {
				@Override
				public void run() {
					layout.removeView(removeFloatBall);
				}
			});
			mQuestionFloatWindow.mQuestionDialog.stopTest();
			mQuestionFloatWindow.mQuestionDialog.dismiss();
			mQuestionFloatWindow = null;
		}
	}
	
	public static void showQuestionDialog(){
		if(mQuestionFloatWindow!=null){
			mQuestionFloatWindow.mQuestionDialog.show();
		}
	}
	
	public static void hideQuestionDialog(){
		if(mQuestionFloatWindow!=null){
			mQuestionFloatWindow.mQuestionDialog.dismiss();
		}
	}
	
	public static void hideFloatBall(){
		if(mQuestionFloatWindow!=null){
			mQuestionFloatWindow.mFloatBall.setVisibility(View.GONE);
		}
	}
	
	public static void showFloatBall(){
		if(mQuestionFloatWindow!=null){
			mQuestionFloatWindow.mFloatBall.setVisibility(View.VISIBLE);
		}
	}
}
