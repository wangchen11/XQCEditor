package person.wangchen11.questions;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.concurrent.TimeoutException;

import person.wangchen11.process.ProcessState;
import person.wangchen11.util.FileUtil;

import android.content.Context;
import android.util.Log;

public class TestTask extends QuestionTask {
	private CompileTask mCompileTask = null;
	private boolean mTested = false;
	private String mInput = "";
	private String mOutput = "";
	private String mResult = "Not Tested!";
	private Exception mException = null;
	
	public TestTask(Context context, String name,
			OnTaskCompliteListener compliteListener, int timeOut,CompileTask compileTask,String input,String output) {
		
		super(context, name, compliteListener, timeOut);
		mCompileTask = compileTask;
		mInput = input;
		mOutput = output;
	}
	
	public String getInput(){
		return mInput;
	}
	
	public String getOutput(){
		return mOutput;
	}
	
	public String getResult(){
		return mResult;
	}
	
	@Override
	public String getCompliteMsg(Context context) {
		if(mException!=null){
			if(mException instanceof TimeoutException){
				return "超时";
			}else{
				return ""+mException.getMessage();
			}
		}
		if(isPass()){
			return "通过";
		}
		return "未通过";
	}
	
	@Override
	public int getMarks() {
		if(isPass()){
			return 10;
		}
		return 0;
	}

	@Override
	public int getColor() {
		if(mException!=null){
			return RED;
		}
		if(isComplite()){
			if(isPass()){
				return GREEN;
			}else{
				return RED;
			}
		}
		return super.getColor();
	}
	
	public boolean isPass(){
		if(mException!=null){
			return false;
		}
		return  mResult.equals(mOutput);
	}
	
	@Override
	public void inRun() {
		Log.i("", "isSuccess:"+mCompileTask.isSuccess());
		if(mCompileTask.isSuccess()){
			String cmd = "";
			File elfFile = new File(mCompileTask.getFileOut());
			File inputFile = null;
			FileUtil.setFileExecutable(elfFile);
			
			try {
				inputFile = File.createTempFile("test", ".tmp");
				FileOutputStream outputStream = new FileOutputStream(inputFile);
				outputStream.write(mInput.getBytes());
				outputStream.close();
				
				cmd+="'"+mCompileTask.getFileOut()+"'"+" < "+"'"+inputFile+"'\n";
				Log.i("", "cmd:"+cmd);
				mResult = runCmd(cmd, getTimeOut(),false);
				mResult = Question.stripEnd( mResult.replaceAll("\r\n", "\n"),null);
			} catch (IOException e) {
				mException = e;
			} catch (TimeoutException e) {
				mException = e;
			} catch (Exception e) {
				mException = e;
			} finally {
				ProcessState processState = ProcessState.getProcessByName(elfFile.getAbsolutePath());
				if(processState!=null)
					processState.kill();
				//elfFile.delete();
				if(inputFile!=null)
					inputFile.delete();
			}
			mTested = true;
		}
	}
}
