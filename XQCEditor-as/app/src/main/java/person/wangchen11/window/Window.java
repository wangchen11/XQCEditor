package person.wangchen11.window;

import java.util.List;

import android.content.Context;
import android.support.v4.app.Fragment;

public interface Window {
	public Fragment getFragment();
	public CharSequence getTitle(Context context);
	public boolean onBackPressed();
	public boolean canAddNewWindow(Window window);
	public boolean onClose();
	public List<MenuTag> getMenuTags();
	public boolean onMenuItemClick(int id);
	public String[] getResumeCmd();
	public void     resumeByCmd(String []cmd) throws Exception;
}
