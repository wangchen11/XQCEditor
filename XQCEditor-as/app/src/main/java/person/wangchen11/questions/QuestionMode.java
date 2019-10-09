package person.wangchen11.questions;

import java.util.List;

import person.wangchen11.window.MenuTag;
import person.wangchen11.window.Window;
import person.wangchen11.window.WindowPointer;
import person.wangchen11.window.WindowsManager;
import person.wangchen11.window.WindowsManager.WindowsManagerLintener;
import person.wangchen11.window.ext.Setting;
import person.wangchen11.xqceditor.R;
import android.annotation.SuppressLint;
import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseExpandableListAdapter;
import android.widget.ExpandableListView;
import android.widget.ExpandableListView.OnChildClickListener;
import android.widget.TextView;

@SuppressLint("InflateParams") 
public class QuestionMode extends Fragment implements Window, WindowsManagerLintener {
	private WindowsManager mWindowsManager = null;
	private ExpandableListView mExpandableListView = null;
	private QuestionAdapter mQuestionAdapter = null;
	public QuestionMode(WindowsManager windowsManager) {
		mWindowsManager = windowsManager;
		windowsManager.addListener(this);
	}
	
	@Override
	@Nullable
	public View onCreateView(LayoutInflater inflater,
			@Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
		QuestionManager.init(inflater.getContext());
		View view = inflater.inflate(R.layout.fragment_answer_and_qusestion, null);
		mExpandableListView = (ExpandableListView) view.findViewById(R.id.expandableListView);
		mQuestionAdapter = new QuestionAdapter();
		mExpandableListView.setAdapter(mQuestionAdapter);
		mExpandableListView.setOnChildClickListener(new OnChildClickListener() {
			@Override
			public boolean onChildClick(ExpandableListView parent, View v,
					int groupPosition, int childPosition, long id) {
				mWindowsManager.addWindow(new QuestionCEditor(mWindowsManager,mQuestionAdapter.getChild(groupPosition, childPosition)));
				return false;
			}
		});
		return view;
	}
	
	@Override
	public Fragment getFragment() {
		return this;
	}

	@Override
	public CharSequence getTitle(Context context) {
		return context.getText(R.string.answer_and_question);
	}

	@Override
	public boolean onBackPressed() {
		return false;
	}

	@Override
	public boolean canAddNewWindow(Window window) {
		if(window instanceof QuestionMode)
			return false;
		return true;
	}

	@Override
	public boolean onClose() {
		mWindowsManager.removeListener(this);
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
	public void resumeByCmd(String[] cmd) throws Exception {
	}

	@Override
	public void onChangeWindow(WindowsManager manager) {
		if(manager.getSelectWindow().mWindow==this){
			if(mQuestionAdapter!=null)
				mQuestionAdapter.notifyDataSetChanged();
		}
	}

	@Override
	public void onAddWindow(WindowsManager manager, WindowPointer pointer) {
		
	}

	@Override
	public void onCloseWindow(WindowsManager manager, WindowPointer pointer) {
	}
}

@SuppressLint("InflateParams") 
class QuestionAdapter extends BaseExpandableListAdapter {
	public QuestionAdapter() {
	}
	
	@Override
	public int getGroupCount() {
		return QuestionManager.instance().getQuestionLevelCount();
	}

	@Override
	public int getChildrenCount(int groupPosition) {
		return getGroup(groupPosition).getQuestions().size();
	}

	@Override
	public QuestionGroup getGroup(int groupPosition) {
		return QuestionManager.instance().getQuestionGroupByLevel(groupPosition);
	}

	@Override
	public Question getChild(int groupPosition, int childPosition) {
		return getGroup(groupPosition).getQuestions().get(childPosition);
	}

	@Override
	public long getGroupId(int groupPosition) {
		return 0;
	}

	@Override
	public long getChildId(int groupPosition, int childPosition) {
		return 0;
	}

	@Override
	public boolean hasStableIds() {
		return false;
	}

	@Override
	public View getGroupView(int groupPosition, boolean isExpanded,
			View convertView, ViewGroup parent) {
		LayoutInflater inflater = LayoutInflater.from(parent.getContext());
		View view = inflater.inflate(R.layout.item_question_group, null);
		
		TextView textView = (TextView) view.findViewById(R.id.textView);
		textView.setText(getGroup(groupPosition).getName());

		TextView textViewScore = (TextView) view.findViewById(R.id.textViewScore);
		QuestionGroup questionGroup = getGroup(groupPosition);
		textViewScore.setText(questionGroup.getMarks()+"/"+questionGroup.getFullMarks());
		
		Setting.applySettingConfigToAllView(view);
		return view;
	}

	@Override
	public View getChildView(int groupPosition, int childPosition,
			boolean isLastChild, View convertView, ViewGroup parent) {
		LayoutInflater inflater = LayoutInflater.from(parent.getContext());
		View view = inflater.inflate(R.layout.item_question_child, null);
		Question question = getChild(groupPosition, childPosition);
		
		TextView textView = (TextView) view.findViewById(R.id.textView);
		textView.setText(question.mTitle);

		TextView textViewScore = (TextView) view.findViewById(R.id.textViewScore);
		textViewScore.setText(question.getMarks()+"/"+question.getFullMarks());
		
		Setting.applySettingConfigToAllView(view);
		return view;
	}

	@Override
	public boolean isChildSelectable(int groupPosition, int childPosition) {
		return true;
	}
	
}