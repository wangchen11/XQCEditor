package person.wangchen11.questions;

import java.util.List;

import android.content.Context;

public class SumMarksTask extends QuestionTask {
	private List <QuestionTask> mQuestionTasks = null;
	
	public SumMarksTask(Context context, String name,
			OnTaskCompliteListener compliteListener, int timeOut,List <QuestionTask> questionTasks) {
		super(context, name, compliteListener, timeOut);
		mQuestionTasks = questionTasks;
	}
	
	@Override
	public String getCompliteMsg(Context context) {
		int marks = getMarks();
		if(marks>0){
			if(marks==100){
				return "完美";
			}
			if(marks>=60){
				return "不错";
			}
			return "还行";
		}
		return "失败";
	}
	
	@Override
	public int getMarks() {
		int sum = 0;
		for(QuestionTask task:mQuestionTasks){
			if(task == this)
				continue;
			sum+=task.getMarks();
		}
		if(sum<0)
			return 0;
		return sum;
	}
}
