package person.wangchen11.window.ext;

import java.io.File;
import java.util.List;

import android.annotation.SuppressLint;
import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.MediaController;
import android.widget.RelativeLayout;
import android.widget.VideoView;
import person.wangchen11.window.MenuTag;
import person.wangchen11.window.Window;
import person.wangchen11.xqceditor.R;

public class VideoPlayer extends Fragment implements Window{
	private File mVideoFile ;
	private VideoView mVideoView ;
	public VideoPlayer( File file) {
		mVideoFile = file;
	}
	
	@SuppressLint("InflateParams")
	@Override
	public View onCreateView(LayoutInflater inflater,
			@Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
		RelativeLayout relativeLayout = (RelativeLayout) inflater.inflate(R.layout.fragment_vedio_player, null);
		mVideoView = (VideoView) relativeLayout.findViewById(R.id.videoView1);
		if(mVideoFile!=null){
			mVideoView.setVideoPath(mVideoFile.getAbsolutePath());
			mVideoView.start();
		}
		MediaController mediaController = new MediaController(inflater.getContext());
		mVideoView.setMediaController(mediaController);
		return relativeLayout;
	}
	
	@Override
	public Fragment getFragment() {
		return this;
	}

	@Override
	public CharSequence getTitle(Context context) {
		if(mVideoFile!=null){
			return mVideoFile.getName();
		}else{
			return "No Title";
		}
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
		mVideoView.stopPlayback();
		return true;
	}

	@Override
	public List<MenuTag> getMenuTags() {
		return null;
	}

	@Override
	public boolean onMenuItemClick(int id) {
		// TODO Auto-generated method stub
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
