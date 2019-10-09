package person.wangchen11.tools;

import java.util.HashMap;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

import android.util.Log;

/**
 * 线程池
 */
public class ThreadPool {
	private static final String TAG = "ThreadPool";
	//默认线程池 
	public static int DEFAULT_POOL = 0;

	//<POOLNAME,THREAD_NUMBER> 
	//线程池配置表，存储每一类线程池有多少个线程 
	private HashMap<Integer, Integer> mPoolConfig = new HashMap<>();
	//线程池表
	private HashMap<Integer, Executor> mPool = new HashMap<>();
	
	private ThreadPool() {
		//默认线程池，配置一个线程 
		mPoolConfig.put(DEFAULT_POOL, 1);
	}

	//单例模式 
	private static ThreadPool mThreadPool;

	public static synchronized ThreadPool instance(){
		if(mThreadPool==null){
			mThreadPool = new ThreadPool();
		}
		return mThreadPool;
	}
	
	/**
	 * 使用默认线程池执行任务
	 * @param runnable 任务
	 */
	public void execute(Runnable runnable){
		execute(runnable,DEFAULT_POOL);
	}

	/**
	 * 使用某一线程池执行任务
	 * @param runnable 任务
	 * @param pool 哪一个线程池
	 */
	public synchronized void execute(Runnable runnable,int pool){
		Integer threadConfig = mPoolConfig.get(pool);
		if(threadConfig==null){
			Log.e(TAG, "no such pool:"+pool);
			return ;
		}
		Executor executor = mPool.get(pool);
		if(executor==null){
			executor = Executors.newFixedThreadPool(threadConfig);
			mPool.put(pool, executor);
		}
		executor.execute(runnable);
	}
}
