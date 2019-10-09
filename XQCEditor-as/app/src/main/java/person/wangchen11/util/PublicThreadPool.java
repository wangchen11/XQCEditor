package person.wangchen11.util;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class PublicThreadPool {
	private static ExecutorService mExecutorService = null;
	public static synchronized ExecutorService getPublicThreadPool(){
		if(mExecutorService==null){
			mExecutorService = Executors.newFixedThreadPool(2);
		}
		return mExecutorService;
	}
}
