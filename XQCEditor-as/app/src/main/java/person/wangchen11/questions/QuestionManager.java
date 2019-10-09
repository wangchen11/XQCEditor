package person.wangchen11.questions;

import java.io.File;
import java.util.ArrayList;

import person.wangchen11.gnuccompiler.GNUCCompiler;
import person.wangchen11.xqceditor.R;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;

public class QuestionManager {
	private static QuestionManager mQuestionManager = null;

	private static final String ASSETS_PATH = "questions/";
	
	private static final String DEBUG_QUESTION_PATH = GNUCCompiler.getWorkSpaceDir()+"questions/";
	
	private ArrayList<QuestionGroup> mAllLevelQuestions = new ArrayList<QuestionGroup>();
	
	private boolean mIsDebug = false;
	
	public boolean isDebug(){
		return mIsDebug;
	}
	
	private QuestionManager(Context context) {
		String path = ASSETS_PATH;
		mIsDebug = false;
		if(new File(DEBUG_QUESTION_PATH).isDirectory()){
			path = DEBUG_QUESTION_PATH;
			mIsDebug = true;
		}
		mAllLevelQuestions.add(new QuestionGroup(context,path,"1.",100,context.getString(R.string.qc_group_0)));
		mAllLevelQuestions.add(new QuestionGroup(context,path,"2.",100,context.getString(R.string.qc_group_1)));
		mAllLevelQuestions.add(new QuestionGroup(context,path,"3.",100,context.getString(R.string.qc_group_2)));
		mAllLevelQuestions.add(new QuestionGroup(context,path,"4.",100,context.getString(R.string.qc_group_3)));
		loadAllQuestionInfo(context);
	}
	public static void init(Context context){
		if(new File(DEBUG_QUESTION_PATH).isDirectory()){
			mQuestionManager = new QuestionManager(context);
		}
		else
		if(mQuestionManager==null)
			mQuestionManager = new QuestionManager(context);
	}
	
	public static QuestionManager instance(){
		return mQuestionManager;
	}
	
	public QuestionGroup getQuestionGroupByLevel(int level){
		return mAllLevelQuestions.get(level);
	}
	
	public int getQuestionLevelCount(){
		return mAllLevelQuestions.size();
	}
	
	public QuestionGroup getQuestionGroup(Question question){
		for(QuestionGroup questionGroup:mAllLevelQuestions){
			if(questionGroup.getQuestionIndex(question)>=0)
				return questionGroup;
		}
		return null;
	}
	
	public Question getNextQuestion(Question question){
		QuestionGroup questionGroup = getQuestionGroup(question);
		if(questionGroup==null)
			return null;
		int index = questionGroup.getQuestionIndex(question);
		if(index+1 >= questionGroup.getQuestions().size()){
			int questionGroupIndex = mAllLevelQuestions.indexOf(questionGroup);
			if(questionGroupIndex==-1)
				return null;
			if(questionGroupIndex+1 >= mAllLevelQuestions.size())
				return null;
			return mAllLevelQuestions.get(questionGroupIndex+1).getQuestions().get(0);
		}
			
		return questionGroup.getQuestions().get(index+1);
	}
	
	public Question getPreQuestion(Question question){
		QuestionGroup questionGroup = getQuestionGroup(question);
		if(questionGroup==null)
			return null;
		int index = questionGroup.getQuestionIndex(question);
		if(index-1 < 0){
			int questionGroupIndex = mAllLevelQuestions.indexOf(questionGroup);
			if(questionGroupIndex-1 < 0)
				return null;
			return mAllLevelQuestions.get(questionGroupIndex-1).getQuestions().get(0);
		}
		
		return questionGroup.getQuestions().get(index-1);
	}
	
	public String getQuestionCodeFile(Question question){
		QuestionGroup questionGroup = getQuestionGroup(question);
		if(questionGroup==null)
			return null;
		int questionGroupIndex = mAllLevelQuestions.indexOf(questionGroup);
		int questionIndex = questionGroup.getQuestionIndex(question);
		
		return GNUCCompiler.getSystemDir()+"/answers/answer"+String.format("%03d", questionGroupIndex+1)+"_"+String.format("%03d", questionIndex+1)+".cpp";
	}

	public void loadAllQuestionInfo(Context context){
		SharedPreferences preferences = context.getSharedPreferences("question_marks", Context.MODE_PRIVATE);
		for(QuestionGroup questionGroup:mAllLevelQuestions){
			for(Question question:questionGroup.getQuestions()){
				int marks = preferences.getInt("marks_"+question.getKey(), 0);
				question.setMarks(marks);
			}
		}
	}
	
	public static void saveQuestionInfo(Context context,Question question){
		SharedPreferences preferences = context.getSharedPreferences("question_marks", Context.MODE_PRIVATE);
		Editor editor = preferences.edit();
		editor.putInt("marks_"+question.getKey(), question.getMarks() );
		editor.commit();
	}
	
}
