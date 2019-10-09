package person.wangchen11.process;

import java.io.IOException;

import person.wangchen11.myscanner.MyScanner;

//import android.util.Log;

public class ProcessState {
	static final String TAG="ProcessState";
	public String mProcessName=null;
	public String mPid=null;
	public ProcessState(String line,int nameIndex,int pidIndex) {
		MyScanner scanner=new MyScanner(line);
		for(int i=0;scanner.hasNext();i++)
		{
			String string=scanner.next();
			if(i==nameIndex)
				mProcessName=string;
			if(i==pidIndex)
				mPid=string;
		}
		//Log.i(TAG, "name:"+mProcessName+",id:"+mPid);
		scanner.close();
	}
	
	public static ProcessState getProcessByName(String name)
	{
		ProcessState ret=null;
		try {
			Process process=Runtime.getRuntime().exec("ps");
			MyScanner scanner=new MyScanner(process.getInputStream());
			String line=null;
			int nameIndex=8;
			int pidIndex=1;
			while(scanner.hasNextLine())
			{
				line=scanner.nextLine();
				ProcessState processState=new ProcessState(line, nameIndex, pidIndex);
				//Log.i(TAG, "line:"+line);
				if(name.equals(processState.mProcessName))
				{
					ret=processState;
					break;
				}
			}
			scanner.close();
			process.destroy();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return ret;
	}
	
	public void kill()
	{
		try {
			if(mPid!=null)
				Runtime.getRuntime().exec(new String[]{"kill",mPid});
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}
