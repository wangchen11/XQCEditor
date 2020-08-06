package person.wangchen11.xqceditor;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;

public class PrivacyPolicy {
    private static final String TAG=PrivacyPolicy.class.getSimpleName();
    private static final String ConfigName="PrivacyPolicy";
    private static final String KEY_AGREE = "agree";
    private String mPrivacyPolicyString = "";

    private SharedPreferences mSharedPreferences;

    public PrivacyPolicy(Context context) {
        try {
            mPrivacyPolicyString = getPrivacyPolicyString(context);
        } catch (IOException e) {
            e.printStackTrace();
        }
        mSharedPreferences = context.getSharedPreferences(ConfigName, Context.MODE_PRIVATE);
    }

    public boolean isAgree(){
        if(!isRequested())
            return true;
        return mSharedPreferences.getBoolean(KEY_AGREE,false);
    }

    public boolean isRequested(){
        if(mPrivacyPolicyString!=null && mPrivacyPolicyString.length()>10)
            return true;
        return false;
    }

    public void saveAgree(boolean agree){
        mSharedPreferences.edit()
                .putBoolean(KEY_AGREE,agree)
                .apply();
    }

    public void showDialog(final Context context){
        AlertDialog.Builder builder=new AlertDialog.Builder(context);
        builder.setTitle(R.string.privacy_policy);
        builder.setMessage(mPrivacyPolicyString);
        builder.setCancelable(false);
        builder.setPositiveButton(R.string.agree, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                saveAgree(true);
            }
        });
        builder.setNegativeButton(R.string.disagree, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                saveAgree(false);
                if(context instanceof Activity){
                    if(context instanceof EditorActivity){
                        if(((EditorActivity) context).mWindowsManager.closeAllWindow()){
                            ((Activity) context).finish();
                        }
                    } else {
                        ((Activity) context).finish();
                    }
                }
            }
        });
        builder.create();
        builder.show();
    }

    public String getPrivacyPolicyString(Context context) throws IOException {
        InputStream inputStream = context.getResources().openRawResource(R.raw.privacy_policy);
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        byte[] buffer = new byte[1024];
        int readLen = 0;
        while( (readLen = inputStream.read(buffer))>0 ){
            outputStream.write(buffer,0,readLen);
        }
        inputStream.close();
        return new String(outputStream.toByteArray());
    }
}
