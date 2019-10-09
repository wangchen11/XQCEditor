package person.wangchen11.help;

import java.util.List;

import person.wangchen11.browser.Browser;
import person.wangchen11.window.MenuTag;
import person.wangchen11.window.Window;
import person.wangchen11.xqceditor.R;

import android.annotation.SuppressLint;
import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

@SuppressLint("SetJavaScriptEnabled")
public class Help extends Browser implements Window{

	public Help() {
		super("file:///android_asset/help/index.html");
	}
	
	@Override
	public Fragment getFragment() {
		return this;
	}

	@Override
	public CharSequence getTitle(Context context) {
		return context.getText(R.string.help);
	}

	@Override
	public boolean onBackPressed() {
		return back();
	}

	@Override
	public boolean canAddNewWindow(Window window) {
		return true;
	}

	@Override
	public boolean onClose() {
		return true;
	}

	@Override
	public List<MenuTag> getMenuTags() {
		return null;
	}

	@Override
	public boolean onMenuItemClick(int id) {
		return false;
	}

	@Override
	public String[] getResumeCmd() {
		if(mWebView==null)
			return null;
		String url = mWebView.getUrl();
		if(url==null)
			return null;
		String[] cmds = new String[]{url};
		return cmds;
	}

	@Override
	public void resumeByCmd(String []cmd) {
		if(cmd==null||cmd.length<=0)
			return ;
		mUrl = cmd[0];
	}
	
	@Override
	@SuppressLint("InflateParams")
	public View onCreateView(LayoutInflater inflater,
			@Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
		View view = super.onCreateView(inflater, container, savedInstanceState);
        // 设置编码 
        mWebView.getSettings().setDefaultTextEncodingName("GBK");
		return view;
	}
}
