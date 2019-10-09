package person.wangchen11.window.ext;

import java.util.List;

import person.wangchen11.phpconfig.PHPConfigFragment;
import person.wangchen11.window.MenuTag;
import person.wangchen11.window.Window;
import android.content.Context;
import android.support.v4.app.Fragment;

public class PHPServerConfig implements Window{
	private PHPConfigFragment mPhpConfigFragment=null;
	public PHPServerConfig() {
		mPhpConfigFragment=new PHPConfigFragment();
	}
	
	@Override
	public Fragment getFragment() {
		return mPhpConfigFragment;
	}

	@Override
	public CharSequence getTitle(Context context) {
		return "PHPConfig";
	}

	@Override
	public boolean onBackPressed() {
		return false;
	}

	@Override
	public boolean canAddNewWindow(Window window) {
		if(window instanceof PHPServerConfig)
			return false;
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
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void resumeByCmd(String []cmd) {
		// TODO Auto-generated method stub
		
	}
}
