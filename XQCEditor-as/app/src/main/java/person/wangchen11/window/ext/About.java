package person.wangchen11.window.ext;

import java.util.List;

import person.wangchen11.window.MenuTag;
import person.wangchen11.window.Window;
import person.wangchen11.xqceditor.R;
import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

public class About extends Fragment implements Window{
	
	@SuppressLint("InflateParams")
	@Override
	public View onCreateView(LayoutInflater inflater,
			@Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
		ViewGroup viewGroup = (ViewGroup) inflater.inflate(R.layout.fragment_about, null);
		viewGroup.findViewById(R.id.image_game_add).setOnClickListener(new View.OnClickListener() {
			public void onClick(View v) {
				Intent intent=new Intent(getActivity(),person.wangchen11.add.MainActivity.class);
				startActivity(intent);
			}
		});
		
		//State.showUpdateMsg(inflater.getContext());
		return viewGroup;
	}
	
	@Override
	public Fragment getFragment() {
		return this;
	}

	@Override
	public CharSequence getTitle(Context context) {
		return context.getText(R.string.about);
	}

	@Override
	public boolean onBackPressed() {
		return false;
	}

	@Override
	public boolean canAddNewWindow(Window window) {
		if(window instanceof About)
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
		return null;
	}

	@Override
	public void resumeByCmd(String []cmd) {
	}
}
