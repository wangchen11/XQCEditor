package person.wangchen11.filebrowser;

import java.io.File;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.support.v4.content.FileProvider;

public class Open {
	public static void openFile(Context context,File file){
		
		Intent intent = new Intent();
		intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
		intent.setAction(Intent.ACTION_VIEW);
		String type = Mime.getMime(file);
		if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
			intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION|Intent.FLAG_GRANT_WRITE_URI_PERMISSION);
			Uri contentUri = FileProvider.getUriForFile(context, context.getPackageName()+".fileprovider", file);
		    intent.setDataAndType(contentUri, type);
		}else{
			intent.setDataAndType(Uri.fromFile(file), type);
		}
		context.startActivity(intent);
	}

}
