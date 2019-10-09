package person.wangchen11.window.ext;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;

import org.json.JSONException;
import org.json.JSONObject;

import android.app.AlertDialog;
import android.app.AlertDialog.Builder;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Handler;
import android.util.Log;
import android.widget.Toast;

import person.wangchen11.waps.Waps;
import person.wangchen11.xqceditor.R;
import person.wangchen11.xqceditor.State;

public class CheckUpdate {
	private final String mCheckUpdateUrl = "http://update.wangchen11.top/api.php";
	private Context mContext = null;
	private boolean mChecking = false;
	private Handler mHandler = null;
	private boolean mShowNewVersionOnly = false;
	public CheckUpdate(Context context) {
		this(context,false);
	}

	public CheckUpdate(Context context,boolean showNewVersionOnly) {
		mContext = context;
		mHandler = new Handler();
		mShowNewVersionOnly = showNewVersionOnly;
		//State.VersionNameNow = "2.1.1";
	}
	public void checkForUpdate(){
		if(Waps.isGoogle())
			return;
		if(mChecking == true){
			showToast(R.string.checking);
			return ;
		}
		mChecking = true;
		new Thread(new Runnable() {
			@Override
			public void run() {
				try {
					String msg = getNewVersionMsg();
					final Version version = parseUpdateInfoJson(msg);
					mHandler.post(new Runnable() {
						@Override
						public void run() {
							onReceivedVersion(version);
						}
					});
				} catch (Throwable e) {
					e.printStackTrace();
				}
				mChecking = false;
			}

		}).start();
	}
	
	private Version   parseUpdateInfoJson(String string){
		try {
			JSONObject jsonObject = new JSONObject(string);
			return Version.loadFromJson(jsonObject);
		} catch (JSONException e) {
			e.printStackTrace();
		}
		return null;
	}
	
	private String getNewVersionMsg(){
		try {
			URL url = new URL(mCheckUpdateUrl);
			try {
				URLConnection connection = url.openConnection();
				connection.setConnectTimeout(2000);
				connection.connect();
				InputStreamReader inputStreamReader = new InputStreamReader( connection.getInputStream());
				BufferedReader bufferedReader = new BufferedReader(inputStreamReader);
				String line = null;
				StringBuilder stringBuilder = new StringBuilder();
				while( (line=bufferedReader.readLine())!=null ){
					stringBuilder.append(line);
				}
				bufferedReader.close();
				return stringBuilder.toString();
			} catch (IOException e) {
				e.printStackTrace();
			}
		} catch (MalformedURLException e) {
			e.printStackTrace();
		}
		return "";
	}
	
	private void onReceivedVersion(Version version){
		if(version==null){
			if(!mShowNewVersionOnly)
				showToast(R.string.can_not_get_version_info);
		}else{
			if(version.needUpdate()){
				showNewVersion(mContext,version);
			}else{
				if(!mShowNewVersionOnly)
					showNoNewVersion(mContext);
			}
		}
	}
	
	static class Version {
		private Version() {
		}
		
		public String version = "";
		public String time = "";
		public String down = "";
		public String size = "";
		public String text = "";
		@Override
		public String toString() {
			String string = "";
			string+="version:"+version+"\n";
			string+="time:"+time+"\n";
			string+="down:"+down+"\n";
			string+="size:"+size+"\n";
			string+="text:"+text+"\n";
			return string;
		}

		public static Version loadFromJson(JSONObject jsonObject){
			try {
				Version version = new Version();
				version.version = jsonObject.getString("version");
				version.time = jsonObject.getString("time");
				version.down = jsonObject.getString("down");
				version.size = jsonObject.getString("size");
				version.text = jsonObject.getString("text");
				return version;
			} catch (Exception e) {
				e.printStackTrace();
			}
			return null;
		}
		
		public boolean needUpdate(){
			Log.i("cmp", ""+version+":"+State.VersionNameNow);
			String []newVersons = version.split("\\.");
			String []curVersons = State.VersionNameNow.split("\\.");
			if(newVersons==null || curVersons==null)
				return false;
			for(int i=0;i<newVersons.length&&i<curVersons.length;i++){
				try {
					int newCode = Integer.parseInt(newVersons[i]);
					int curCode = Integer.parseInt(curVersons[i]);
					Log.i("cmp", ""+newCode+":"+curCode);
					if(newCode>curCode){
						return true;
					}else if(newCode==curCode)
					{
						// cmp next code
					}else {
						// newCode < curCode; 
						break;
					}
				} catch (Exception e) {
					e.printStackTrace();
					return false;
				}
			}
			return false;
		}
	}

	private Toast mToast=null;
	private void showToast(int textid)
	{
		if(mToast!=null)
			mToast.cancel();
		mToast=Toast.makeText(mContext, textid, Toast.LENGTH_LONG);
		mToast.show();
	}
	
	public void showNoNewVersion(Context context)
	{
		AlertDialog.Builder builder=new Builder(context);
		builder.setTitle(R.string.is_the_latest_version);
		builder.setMessage(R.string.is_the_latest_version_thanku);
		builder.setCancelable(false);
		builder.setPositiveButton(android.R.string.ok, null);
		builder.create();
		builder.show();
	}

	public void showNewVersion(Context context,final Version version)
	{
		AlertDialog.Builder builder=new Builder(context);
		builder.setTitle(R.string.find_new_version);
		String msg = "";
		msg += "版本:"+version.version+"\n";
		msg += "时间:"+version.time+"\n";
		msg += "大小:"+version.size+"\n";
		msg += "说明:"+version.text+"\n";
		builder.setMessage(msg);
		builder.setCancelable(false);
		builder.setNegativeButton(android.R.string.cancel, null);
		builder.setPositiveButton(R.string.update_now, new DialogInterface.OnClickListener() {
			@Override
			public void onClick(DialogInterface dialog, int which) {
				try {
					Intent intent = new Intent();
					intent.setAction("android.intent.action.VIEW");
					Uri content_url = Uri.parse(version.down);
					intent.setData(content_url);
					mContext.startActivity(intent);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
		builder.create();
		builder.show();
	}
	
	
}
