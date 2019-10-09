package person.wangchen11.editor.edittext;


import java.util.Stack;

import android.text.Editable;
import android.text.Selection;
import android.text.TextWatcher;
import android.util.Log;

public class RedoUndo implements TextWatcher {
	private boolean mSaveToHistory = true;
	private int mMaxSaveHistory = 20;
	private Stack<ReplaceBody> mUndoBodies = new Stack<ReplaceBody>();
	private Stack<ReplaceBody> mRedoBodies = new Stack<ReplaceBody>();
	private Editable mEditable = null;
	

	private int beforeStart = 0;
	private int beforEnd = 0;
	private CharSequence beforCharSequence = "";
	
	public RedoUndo(Editable editable) {
		mEditable = editable;
	}
	

    @Override
    public void beforeTextChanged(CharSequence s, int start, int count, int after) {
    	beforeStart = start;
    	beforEnd = start+count;
    	beforCharSequence = s.subSequence(beforeStart, beforEnd);
    }

    @Override
    public void onTextChanged(CharSequence s, int start, int before, int count) {
		//saved to history to be undo or redo 
		if(mSaveToHistory){
			mRedoBodies.clear();
			if(mMaxSaveHistory>0){
				if(mUndoBodies.size()>mMaxSaveHistory)
					mUndoBodies.remove(0);
				ReplaceBody body=new ReplaceBody(beforeStart, beforEnd, beforCharSequence,s.subSequence(start, start+count), 0, count, Selection.getSelectionStart(mEditable), Selection.getSelectionEnd(mEditable));
				if(!mUndoBodies.isEmpty() && mUndoBodies.peek().addBody(body))
				{
				}
				else
					mUndoBodies.push(body);
			}
		}else{
		}
    }

    @Override
    public void afterTextChanged(Editable edit) {
    	mEditable = edit;
    }

	public void cleanRedo() {
		mRedoBodies.clear();
	}

	public void cleanUndo() {
		mUndoBodies.clear();
	}

	public void setMaxSaveHistory(int i) {
		mMaxSaveHistory = i;
	}

	public boolean canRedo() {
		return mRedoBodies.size()>0;
	}

	public boolean canUndo() {
		return mUndoBodies.size()>0;
	}

	public boolean redo() {
		if(!canRedo())
			return false;
		ReplaceBody body=mRedoBodies.pop();
		ReplaceBody replaceBody=body.getRedoBody();
		if(mMaxSaveHistory>0){
			if(mUndoBodies.size()>mMaxSaveHistory)
				mUndoBodies.remove(0);
			mUndoBodies.push(body);
		}
		mSaveToHistory = false;
		mEditable.replace(replaceBody.mSt, replaceBody.mEn, replaceBody.mText, replaceBody.mStart, replaceBody.mEnd);
		mSaveToHistory = true;
		return true;
	}
	
	public boolean undo() {
		if(!canUndo())
			return false;
		ReplaceBody body=mUndoBodies.pop();
		ReplaceBody replaceBody=body.getUndoBody();
		if(mMaxSaveHistory>0){
			if(mRedoBodies.size()>mMaxSaveHistory)
				mRedoBodies.remove(0);
			mRedoBodies.push(body);
		}
		mSaveToHistory = false;
		mEditable.replace(replaceBody.mSt, replaceBody.mEn, replaceBody.mText, replaceBody.mStart, replaceBody.mEnd);
		mSaveToHistory = true;
		return true;
	}

	class ReplaceBody {
		int mSt;
		int mEn;
		CharSequence mSubtext;
		CharSequence mText;
		int mStart;
		int mEnd;
		int mSelectionStart;
		int mSelectionEnd;

		public ReplaceBody(int st, int en, CharSequence subtext,
				CharSequence text, int start, int end, int selectionStart,
				int selectionEnd) {
			mSt = st;
			mEn = en;
			mSubtext = subtext;
			mText = text;
			mStart = start;
			mEnd = end;
			mSelectionStart = selectionStart;
			mSelectionEnd = selectionEnd;
		}

		public ReplaceBody getUndoBody() {
			return new ReplaceBody(mSt, mSt + mEnd - mStart, mText, mSubtext,
					0, mSubtext.length(), mSelectionStart, mSelectionEnd);
		}

		public ReplaceBody getRedoBody() {
			return this;
		}

		public boolean isDelete() {
			if (mEn - mSt > 0 && mStart == 0 && mEnd == 0)
				return true;
			return false;
		}

		public boolean isInsert() {
			if (mSt == mEn && mText != null && mText.length() != 0
					&& mEnd - mStart > 0)
				return true;
			Log.i("isInsert", "false");
			return false;
		}

		public boolean addBody(ReplaceBody body) {
			if (isDelete() && body.isDelete() && mSt == body.mEn) {// 合并相连的删除
				this.mSt = body.mSt;
				this.mSubtext = body.mSubtext.toString() + this.mSubtext;
				this.mSelectionStart = body.mSelectionStart;
				return true;
			}
			Log.i("addBody", "addBody");
			if (isInsert() && body.isInsert()
					&& mSt + mText.length() == body.mSt) {// 合并相连的插入
				Log.i("addBody", "合并相连的插入 ");
				if (body.mText.toString().contains("\n")) {
					return false;
				}
				this.mText = this.mText
						+ body.mText.subSequence(body.mStart, body.mEnd)
								.toString();
				this.mEnd += body.mEnd - body.mStart;
				this.mSelectionEnd = body.mSelectionEnd;
				return true;
			}
			return false;
		}
	}
	
}
