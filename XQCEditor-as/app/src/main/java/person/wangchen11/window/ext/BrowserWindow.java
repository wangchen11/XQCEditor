package person.wangchen11.window.ext;

import java.util.LinkedList;
import java.util.List;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.support.v4.app.Fragment;
import person.wangchen11.browser.Browser;
import person.wangchen11.window.MenuTag;
import person.wangchen11.window.Window;
import person.wangchen11.window.WindowPointer;
import person.wangchen11.window.WindowsManager;
import person.wangchen11.window.WindowsManager.WindowsManagerLintener;
import person.wangchen11.xqceditor.R;

public class BrowserWindow extends Browser implements Window, WindowsManagerLintener{
	private String mName;
	private WindowsManager mWindowsManager;
	public BrowserWindow(WindowsManager windowsManager,String url,String name) {
		super(url);
		mWindowsManager=windowsManager;
		mName=name;
		mWindowsManager.addListener(this);
	}

	@Override
	public Fragment getFragment() {
		// TODO Auto-generated method stub
		return this;
	}

	@Override
	public CharSequence getTitle(Context context) {
		return mName;
	}

	@Override
	public boolean onBackPressed() {
		// TODO Auto-generated method stub
		return back();
	}

	@Override
	public boolean canAddNewWindow(Window window) {
		if(window instanceof BrowserWindow)
		{
			BrowserWindow browserWindow=(BrowserWindow) window;
			if(((BrowserWindow) window).mUrl.equals(this.mUrl))
			{
				browserWindow.refresh();
				return false;
			}
		}
		return true;
	}

	@Override
	public boolean onClose() {
		mWindowsManager.removeListener(this);
		return true;
	}

	@Override
	public List<MenuTag> getMenuTags() {
		LinkedList<MenuTag> menuTags=new LinkedList<MenuTag>();
		menuTags.add(new MenuTag(R.string.open_with_browser,mWindowsManager.getContext().getString(R.string.open_with_browser)));
		menuTags.add(new MenuTag(R.string.refresh,mWindowsManager.getContext().getString(R.string.refresh)));
		return menuTags;
	}

	public void openWithBrowser()
	{
		Uri uri = Uri.parse(mUrl);  
		Intent it = new Intent(Intent.ACTION_VIEW, uri);  
		try {
			getActivity().startActivity(it);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	@Override
	public boolean onMenuItemClick(int id) {
		switch (id) {
		case R.string.open_with_browser:
			openWithBrowser();
			break;
		case R.string.refresh:
			refresh();
			break;
		default:
			break;
		}
		return true;
	}

	@Override
	public void onChangeWindow(WindowsManager manager) {
		if(manager.getSelectWindow().mWindow==this)
		{
			refresh();
		}
	}

	@Override
	public void onAddWindow(WindowsManager manager, WindowPointer pointer) {
	}

	@Override
	public void onCloseWindow(WindowsManager manager, WindowPointer pointer) {
	}

	@Override
	public String[] getResumeCmd() {
		String[] cmd= new String[2];
		cmd[0] = mName;
		cmd[1] = this.mUrl;
		return cmd;
	}

	@Override
	public void resumeByCmd(String []cmd) {
		if(cmd==null)
			return;
		if(cmd.length!=2)
			return;

		mName = cmd[0];
		mUrl  = cmd[0];
	}

}
