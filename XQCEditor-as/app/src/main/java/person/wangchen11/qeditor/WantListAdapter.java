package person.wangchen11.qeditor;

import java.util.List;

import person.wangchen11.ccode.WantMsg;
import person.wangchen11.window.ext.Setting;
import android.graphics.Color;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.View.OnKeyListener;
import android.widget.BaseAdapter;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

public class WantListAdapter extends BaseAdapter implements OnKeyListener {
	private static final String TAG = "WantListAdapter";
	private List<WantMsg> mWants;
	private int mSelectedPosition = -1;
	private ListView mListView = null;
	
	public void setSelectedPosition(final int pos){
		mSelectedPosition = pos;
		if(pos>=0){
			mListView.post(new Runnable() {
				@Override
				public void run() {
					mListView.smoothScrollToPosition(pos);
				}
			});
			if(pos<mListView.getFirstVisiblePosition()||pos>mListView.getLastVisiblePosition()){

			}
		}
		notifyDataSetChanged();
	}
	
	public int getSelectedPosition(){
		return mSelectedPosition;
	}
	
	public WantListAdapter(List<WantMsg> wants,ListView listView) {
		mWants=wants;
		mListView = listView;
	}
	@Override
	public int getCount() {
		return mWants.size();
	}

	@Override
	public WantMsg getItem(int position) {
		return mWants.get(position);
	}

	@Override
	public long getItemId(int position) {
		return position;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		WantMsg wantMsg = mWants.get(position);
		float density=parent.getContext().getResources().getDisplayMetrics().density;
		LinearLayout layout = new LinearLayout(parent.getContext());
		layout.setPadding((int)(density*2),(int)( density*4),(int)( density*2),(int)( density*4) );
		layout.setOrientation(LinearLayout.VERTICAL);
		TextView textView=new TextView(parent.getContext());
		textView.setText(wantMsg.mReplace);
		textView.setTextSize(14f);
		layout.addView(textView);
		if(wantMsg.mTip!=null&&wantMsg.mTip.length()>0){
			TextView textView2=new TextView(parent.getContext());
			textView2.setText(wantMsg.mTip);
			textView2.setTextSize(10);
			textView2.setTextColor(Color.rgb(0x80, 0x30, 0x00));
			layout.addView(textView2);
		}
		if(position == mSelectedPosition){
			layout.setBackgroundColor(Setting.mConfig.mEditorConfig.mBackGroundColor);
		}else{
			layout.setBackgroundColor(Color.TRANSPARENT);
		}
		return layout;
	}

	@Override
	public boolean onKey(View v, int keyCode, KeyEvent event) {
		Log.i(TAG, "onKey:"+keyCode);
		if(mListView.getVisibility() != View.VISIBLE)
			return false;
		if(event.getAction() == KeyEvent.ACTION_DOWN){
			if(keyCode == KeyEvent.KEYCODE_DPAD_DOWN){
				int pos = getSelectedPosition();
				pos++;
				if(pos >= getCount())
					pos = 0;
				setSelectedPosition(pos);
				return true;
			}
			if(keyCode == KeyEvent.KEYCODE_DPAD_UP){
				int pos = getSelectedPosition();
				pos--;
				if(pos < 0)
					pos = getCount()-1;
				setSelectedPosition(pos);
				return true;
			}
			if(keyCode == KeyEvent.KEYCODE_DPAD_CENTER||keyCode == KeyEvent.KEYCODE_ENTER){
				return onSelected(getSelectedPosition());
			}
			return onSelected(-1);
		}
		return false;
	}
	
	public boolean onSelected(int position){
		if(position == -1)
			return false;
		return true;
	}
}
