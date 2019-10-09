package person.wangchen11.hexeditor;

import java.io.File;

import person.wangchen11.xqceditor.R;
import android.annotation.SuppressLint;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

public class HexEditorFragment extends Fragment {
	private File mFile = null;
	private HexHelper mHexHelper = null;
	private HexViewer mHexViewer = null;
	
	public HexEditorFragment(File file) {
		mFile = file;
		mHexHelper = new HexHelper(file);
	}

	@SuppressLint("InflateParams")
	@Override
	public View onCreateView(LayoutInflater inflater,
			@Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
		View view = (ViewGroup) inflater.inflate(R.layout.fragment_hex_edit, null);
		mHexViewer = (HexViewer) view.findViewById(R.id.hexViewer);
		mHexViewer.setHexHelper(mHexHelper);
		return view;
	}
	
	public File getFile(){
		return mFile;
	}
	
	public void destroy(){
		mHexHelper.close();
	}
	
}
