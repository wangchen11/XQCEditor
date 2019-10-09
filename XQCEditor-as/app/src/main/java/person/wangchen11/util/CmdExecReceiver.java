package person.wangchen11.util;

import java.io.IOException;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

public class CmdExecReceiver extends BroadcastReceiver {
	public static final String TAG = "CmdExecReceiver";
	public static final String EXEC_ACTION = "person.wangchen11.action.EXEC_ACTION";
	public static final String CMD_KEY = "cmd";
	@Override
	public void onReceive(Context context, Intent intent) {
		String cmd = intent.getStringExtra(CMD_KEY);
		if(cmd!=null){
			try {
				Log.i(TAG, "exec:"+cmd);
				Runtime.getRuntime().exec(cmd);
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}
}
