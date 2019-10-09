package person.wangchen11.hexeditor;

import java.io.File;
import java.util.List;

import person.wangchen11.window.MenuTag;
import person.wangchen11.window.Window;
import person.wangchen11.window.WindowsManager;
import person.wangchen11.xqceditor.R;
import android.content.Context;
import android.support.v4.app.Fragment;

public class HexEditor implements Window{
	private HexEditorFragment mEditorFragment = null;

	public HexEditor(WindowsManager windowsManager) {
		this(windowsManager,null);
	}
	
	public HexEditor(WindowsManager mWindowsManager, File file) {
		mEditorFragment = new HexEditorFragment(file);
	}

	@Override
	public Fragment getFragment() {
		return mEditorFragment;
	}

	@Override
	public CharSequence getTitle(Context context) {
		CharSequence title = context.getText(R.string.hex)+"-正在添加此功能";
		return title;
	}

	@Override
	public boolean onBackPressed() {
		return false;
	}

	@Override
	public boolean canAddNewWindow(Window window) {
		return true;
	}

	@Override
	public boolean onClose() {
		if(mEditorFragment!=null){
			mEditorFragment.destroy();
		}
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
		String[] strings = new String[1];
		strings[0] = mEditorFragment.getFile().getAbsolutePath();
		return strings;
	}

	@Override
	public void resumeByCmd(String []cmd) throws Exception {
		if(cmd==null || cmd.length!=1 || cmd[0].equals("/") )
			throw new Exception("can not resume by cmd!");
		if(mEditorFragment!=null){
			mEditorFragment.destroy();
		}
		mEditorFragment = new HexEditorFragment(new File(cmd[0]));
	}
}
