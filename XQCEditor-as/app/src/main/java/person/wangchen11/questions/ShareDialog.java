package person.wangchen11.questions;

import java.io.File;

import com.tencent.connect.share.QQShare;
import com.tencent.tauth.IUiListener;
import com.tencent.tauth.UiError;

import person.wangchen11.gnuccompiler.GNUCCompiler;
import person.wangchen11.util.ScreenCap;
import person.wangchen11.waps.TencentApi;
import person.wangchen11.window.ext.Setting;
import person.wangchen11.xqceditor.EditorActivity;
import person.wangchen11.xqceditor.R;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Bitmap;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;

public class ShareDialog extends AlertDialog implements AlertDialog.OnClickListener{
	private View mView = null;
	private Question mQuestion = null;
	private static final String SHARE_IMAGE_FILE = GNUCCompiler.getSystemDir()+"/images/share.png";
	private Bitmap mBitmap = null;
	
	protected ShareDialog(Context context,Question question,View view) {
		super(context);
		mQuestion = question;
		try {
			mBitmap = ScreenCap.viewShot(view);
			ScreenCap.saveTo(mBitmap, new File(SHARE_IMAGE_FILE));
		} catch (Exception e) {
		} catch (Error e) {
		}
	}
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		setButton(BUTTON_POSITIVE, getContext().getString(R.string.share_to_qq), this);
		setButton(BUTTON_NEUTRAL, getContext().getString(android.R.string.cancel), this);
		LayoutInflater layoutInflater = LayoutInflater.from(getContext());
		mView = layoutInflater.inflate(R.layout.dialog_share, null);
		setView(mView);
		((ImageView)mView.findViewById(R.id.imageViewShare)).setImageBitmap(mBitmap);
		super.onCreate(savedInstanceState);
	}

	@SuppressWarnings("deprecation")
	@Override
	public void show() {
		super.show();
		getButton(BUTTON_POSITIVE).setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				ShareDialog.this.onClick(ShareDialog.this, BUTTON_POSITIVE);
			}
		});
		getButton(BUTTON_NEGATIVE).setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				ShareDialog.this.onClick(ShareDialog.this, BUTTON_NEGATIVE);
			}
		});
		getButton(BUTTON_NEUTRAL).setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				ShareDialog.this.onClick(ShareDialog.this, BUTTON_NEUTRAL);
			}
		});
		
		Setting.applySettingConfigToAllView(mView);
		mView.setBackgroundDrawable(new ColorDrawable(Setting.mConfig.mEditorConfig.mBackGroundColor));
	}

	@Override
	public void onClick(DialogInterface dialog, int which) {
		switch (which) {
		case BUTTON_POSITIVE:
			share();
			dismiss();
			break;
		case BUTTON_NEUTRAL:
			dismiss();
			break;
		case BUTTON_NEGATIVE:
			dismiss();
			break;
		default:
			break;
		}
	}
	
	private void share(){
		Bundle params = new Bundle();
	    params.putInt(QQShare.SHARE_TO_QQ_KEY_TYPE, QQShare.SHARE_TO_QQ_TYPE_IMAGE);
	    params.putString(QQShare.SHARE_TO_QQ_TITLE, mQuestion.mTitle);
	    params.putString(QQShare.SHARE_TO_QQ_SUMMARY,  getContext().getString(R.string.my_marks)+mQuestion.getMarks());
	    params.putString(QQShare.SHARE_TO_QQ_TARGET_URL, "http://a.app.qq.com/o/simple.jsp?pkgname=person.wangchen11.xqceditor");
	    params.putString(QQShare.SHARE_TO_QQ_IMAGE_LOCAL_URL,SHARE_IMAGE_FILE);
	    params.putString(QQShare.SHARE_TO_QQ_APP_NAME,  getContext().getText(R.string.app_name).toString());
		TencentApi.getTencent().shareToQQ(EditorActivity.getBaseActivity(),params,new IUiListener() {
			@Override
			public void onError(UiError arg0) {
				//onDone(false);
			}
			
			@Override
			public void onComplete(Object arg0) {
				//onDone(true);
			}
			
			@Override
			public void onCancel() {
				//onDone(false);
			}
		});
		onDone(true);
	}
	
	public void onDone(boolean success){
		
	}
}
