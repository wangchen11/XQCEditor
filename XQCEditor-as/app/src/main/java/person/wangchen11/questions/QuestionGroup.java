package person.wangchen11.questions;

import java.io.IOException;
import java.util.ArrayList;

import android.content.Context;

public class QuestionGroup {
	private ArrayList<Question> mQuestions = new ArrayList<Question>();
	private String mName = "";
	
	public QuestionGroup(Context context,String assetsPath,String prefix,int number,String name){
		for(int i=1;i<=number;i++){
			try {
				mQuestions.add(new Question(context, assetsPath+prefix+i+"/", prefix+i));
			} catch (IOException e) {
			}
		}
		mName = name;
	}
	
	public ArrayList<Question> getQuestions(){
		return mQuestions;
	}
	
	public String getName(){
		return mName;
	}
	
	public int getQuestionIndex(Question question){
		return mQuestions.indexOf(question);
	}

	public int getFullMarks(){
		int marks = 0;
		for(Question question:mQuestions){
			marks+=question.getFullMarks();
		}
		return marks;
	}
	
	public int getMarks(){
		int marks = 0;
		for(Question question:mQuestions){
			marks+=question.getMarks();
		}
		return marks;
	}
}
