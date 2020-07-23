package person.wangchen11.nativeview;

import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.os.SystemClock;
import android.text.Editable;
import android.text.NoCopySpan;
import android.text.Selection;
import android.text.Spannable;
import android.text.Spanned;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.text.method.MetaKeyKeyListener;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.CompletionInfo;
import android.view.inputmethod.CorrectionInfo;
import android.view.inputmethod.ExtractedText;
import android.view.inputmethod.ExtractedTextRequest;
import android.view.inputmethod.InputConnection;
import android.view.inputmethod.InputContentInfo;
import android.view.inputmethod.InputMethodManager;

class ComposingText implements NoCopySpan {
}

public class MyInputConnection implements InputConnection{
	protected static final String TAG="MyInputConnection";
	private boolean mIsFristCallGetEditable=true;
    protected InputMethodManager mIMM;
	private View mView;
    static final Object COMPOSING = new ComposingText();
    private Object[] mDefaultComposingSpans;

    public MyInputConnection(View view) {
    	mView=view;
        mIMM = (InputMethodManager)view.getContext().getSystemService( Context.INPUT_METHOD_SERVICE);
	}
	public Editable mEditable=new MyEditable();
	public Editable getEditable() {
		if(mIsFristCallGetEditable)
		{
			mIsFristCallGetEditable=false;
			if(mView instanceof TextWatcher)
				mEditable.setSpan(mView, 0, mEditable.length(), Spanned.SPAN_INCLUSIVE_INCLUSIVE);
		}
		return mEditable;
	}
	
	@Override
	public ExtractedText getExtractedText(ExtractedTextRequest request,int flags) {
		
		//Log.i(TAG, "getExtractedText"+" flags:"+request.flags+" hitMaxC"+request.hintMaxChars+
		//		" hintMaxLines:"+request.hintMaxLines+" token"+request.token
		//		+" describeContents:"+request.describeContents()
		//		);
		//return null;
		
		if(mBatchEditNum!=0)
			return null;
		ExtractedText extractedText=new ExtractedText();
		Editable editable=getEditable();
		extractedText.startOffset=0;
		extractedText.selectionStart=Selection.getSelectionStart(editable);
		extractedText.selectionEnd=Selection.getSelectionEnd(editable);
		extractedText.partialEndOffset=-1;
		extractedText.partialStartOffset=-1;
		
		int retLen=editable.length();
		
		int maxLength=128*1024;
		if(retLen>maxLength)
			retLen=maxLength;
		try {
			extractedText.text=editable.subSequence(0, retLen);
		} catch (OutOfMemoryError e) {
			e.printStackTrace();
		}
		return extractedText;
		
	}
	
	int mBatchEditNum=0;
	@Override
	public boolean beginBatchEdit() {
		Log.i(TAG, "beginBatchEdit");
		mBatchEditNum++;
		return true;
	}
	
	@Override
	public boolean endBatchEdit() {
		Log.i(TAG, "endBatchEdit");
		mBatchEditNum--;
		return true;
	}
	
	@Override
	public boolean commitCompletion(CompletionInfo text) {
		Log.i(TAG, "commitCompletion:"+text);
		return true;
	}
	
	@Override
	public boolean performPrivateCommand(String action, Bundle data) {
		Log.i(TAG, "performPrivateCommand:"+action+" "+data);
		return true;
	}
	
	public boolean requestCursorUpdates(int cursorUpdateMode) {
		Log.i(TAG, "requestCursorUpdates:"+cursorUpdateMode);
		return true;
	}

    @Override
    public Handler getHandler() {
        return null;
    }

    @Override
    public void closeConnection() {

    }

    @Override
    public boolean commitContent(InputContentInfo inputContentInfo, int i, Bundle bundle) {
        return false;
    }

    @Override
    public CharSequence getTextBeforeCursor(int length, int flags) {
		Log.i(TAG, "getTextBeforeCursor:"+length);
        final Editable content = getEditable();
        if (content == null) return null;

        int a = Selection.getSelectionStart(content);
        int b = Selection.getSelectionEnd(content);

        if (a > b) {
            int tmp = a;
            a = b;
            b = tmp;
        }

        if (a <= 0) {
            return "";
        }
        
        if (length > a) {
            length = a;
        }

        if ((flags&GET_TEXT_WITH_STYLES) != 0) {
            return content.subSequence(a - length, a);
        }
        return TextUtils.substring(content, a - length, a);
    }

	@Override
    public CharSequence getTextAfterCursor(int length, int flags) {
		Log.i(TAG, "getTextAfterCursor:"+length);
        final Editable content = getEditable();
        if (content == null) return null;

        int a = Selection.getSelectionStart(content);
        int b = Selection.getSelectionEnd(content);

        if (a > b) {
            int tmp = a;
            a = b;
            b = tmp;
        }

        // Guard against the case where the cursor has not been positioned yet.
        if (b < 0) {
            b = 0;
        }

        if (b + length > content.length()) {
            length = content.length() - b;
        }


        if ((flags&GET_TEXT_WITH_STYLES) != 0) {
            return content.subSequence(b, b + length);
        }
        return TextUtils.substring(content, b, b + length);
    }

	public CharSequence getSelectedText(int flags) {
		Log.i(TAG, "getSelectedText:"+flags);
        final Editable content = getEditable();
        if (content == null) return null;

        int a = Selection.getSelectionStart(content);
        int b = Selection.getSelectionEnd(content);

        if (a > b) {
            int tmp = a;
            a = b;
            b = tmp;
        }

        if (a == b) return null;

        if ((flags&GET_TEXT_WITH_STYLES) != 0) {
            return content.subSequence(a, b);
        }
        return TextUtils.substring(content, a, b);
    }

	@Override
    public int getCursorCapsMode(int reqModes) {
		Log.i(TAG, "getCursorCapsMode:"+reqModes);
		return 0;
		/*
        if (mDummyMode) return 0;
        
        final Editable content = getEditable();
        if (content == null) return 0;
        
        int a = Selection.getSelectionStart(content);
        int b = Selection.getSelectionEnd(content);

        if (a > b) {
            int tmp = a;
            a = b;
            b = tmp;
        }

        return TextUtils.getCapsMode(content, a, reqModes);
        */
    }


    public static int getComposingSpanStart(Spannable text) {
		//Log.i(TAG, "getComposingSpanStart:"+text);
        return text.getSpanStart(COMPOSING);
    }
    
    public static int getComposingSpanEnd(Spannable text) {
		//Log.i(TAG, "getComposingSpanEnd:"+text);
        return text.getSpanEnd(COMPOSING);
    }
    
	@Override
    public boolean deleteSurroundingText(int beforeLength, int afterLength) {
		Log.i(TAG, "deleteSurroundingText:"+beforeLength);
        final Editable content = getEditable();
        if (content == null) return false;

        beginBatchEdit();
        
        int a = Selection.getSelectionStart(content);
        int b = Selection.getSelectionEnd(content);

        if (a > b) {
            int tmp = a;
            a = b;
            b = tmp;
        }

        // ignore the composing text.
        int ca = getComposingSpanStart(content);
        int cb = getComposingSpanEnd(content);
        if (cb < ca) {
            int tmp = ca;
            ca = cb;
            cb = tmp;
        }
        if (ca != -1 && cb != -1) {
            if (ca < a) a = ca;
            if (cb > b) b = cb;
        }

        int deleted = 0;

        if (beforeLength > 0) {
            int start = a - beforeLength;
            if (start < 0) start = 0;
            content.delete(start, a);
            deleted = a - start;
        }

        if (afterLength > 0) {
            b = b - deleted;

            int end = b + afterLength;
            if (end > content.length()) end = content.length();

            content.delete(b, end);
        }
        
        endBatchEdit();
        
        return true;
    }

    @Override
    public boolean deleteSurroundingTextInCodePoints(int i, int i1) {
        return false;
    }


    @Override
    public boolean setComposingText(CharSequence text, int newCursorPosition) {
		Log.i(TAG, "setComposingText:"+text);
        //replaceText(text, newCursorPosition, true);
        return true;
    }

	public boolean setComposingRegion(int start, int end) {
		Log.i(TAG, "setComposingRegion:"+start);
        final Editable content = getEditable();
        if (content != null) {
            beginBatchEdit();
            removeComposingSpans(content);
            int a = start;
            int b = end;
            if (a > b) {
                int tmp = a;
                a = b;
                b = tmp;
            }
            // Clip the end points to be within the content bounds.
            final int length = content.length();
            if (a < 0) a = 0;
            if (b < 0) b = 0;
            if (a > length) a = length;
            if (b > length) b = length;

            if (mDefaultComposingSpans != null) {
                for (int i = 0; i < mDefaultComposingSpans.length; ++i) {
                    content.setSpan(mDefaultComposingSpans[i], a, b,
                            Spanned.SPAN_EXCLUSIVE_EXCLUSIVE | Spanned.SPAN_COMPOSING);
                }
            }

            content.setSpan(COMPOSING, a, b,
                    Spanned.SPAN_EXCLUSIVE_EXCLUSIVE | Spanned.SPAN_COMPOSING);
            endBatchEdit();
        }
        return true;
    }

	@Override
    public boolean finishComposingText() {
		Log.i(TAG, "finishComposingText:");
        final Editable content = getEditable();
        if (content != null) {
            beginBatchEdit();
            removeComposingSpans(content);
            endBatchEdit();
        }
        return true;
    }

	@Override
	public boolean commitText(CharSequence text, int newCursorPosition) {
		Log.i(TAG, "commitText:"+text);
        //replaceText(text, newCursorPosition, false);
		Editable content=getEditable();
		content.replace(Selection.getSelectionStart(content),Selection.getSelectionEnd(content) , text);
		//int cursor=Selection.getSelectionEnd(content);
        //content.replace(cursor, cursor, text);
        //Selection.setSelection(content, cursor+text.length());
        return true;
	}

	@Override
	public boolean setSelection(int start, int end) {
		Log.i(TAG, "setSelection:"+start+end);
        final Editable content = getEditable();
        if (content == null) return false;
        int len = content.length();
        if (start > len || end > len || start < 0 || end < 0) {
            // If the given selection is out of bounds, just ignore it.
            // Most likely the text was changed out from under the IME,
            // and the IME is going to have to update all of its state
            // anyway.
            return true;
        }
        if (start == end && MetaKeyKeyListener.getMetaState(content,
                /*MetaKeyKeyListener.META_SELECTING*/0x800) != 0) {
            // If we are in selection mode, then we want to extend the
            // selection instead of replacing it.
            Selection.extendSelection(content, start);
        } else {
            Selection.setSelection(content, start, end);
        }
        return true;
	}

	@Override
	public boolean performEditorAction(int editorAction) {
		//Log.i(TAG, "performEditorAction:"+editorAction);
        long eventTime = SystemClock.uptimeMillis();
        sendKeyEvent(new KeyEvent(eventTime, eventTime,
                KeyEvent.ACTION_DOWN, KeyEvent.KEYCODE_ENTER, 0, 0,
                /*KeyCharacterMap.VIRTUAL_KEYBOARD*/-1, 0,
                KeyEvent.FLAG_SOFT_KEYBOARD | KeyEvent.FLAG_KEEP_TOUCH_MODE
                | KeyEvent.FLAG_EDITOR_ACTION));
        sendKeyEvent(new KeyEvent(SystemClock.uptimeMillis(), eventTime,
                KeyEvent.ACTION_UP, KeyEvent.KEYCODE_ENTER, 0, 0,
                /*KeyCharacterMap.VIRTUAL_KEYBOARD*/-1, 0,
                KeyEvent.FLAG_SOFT_KEYBOARD | KeyEvent.FLAG_KEEP_TOUCH_MODE
                | KeyEvent.FLAG_EDITOR_ACTION));
        return true;
	}

	@Override
	public boolean performContextMenuAction(int id) {
		Log.i(TAG, "performContextMenuAction:"+id);
		return false;
	}

	@Override
	public boolean sendKeyEvent(KeyEvent event) {
		Log.i(TAG, "sendKeyEvent:"+event);
		if(mView!=null)
		switch(event.getAction() ){
		case KeyEvent.ACTION_DOWN:
			mView.onKeyDown(event.getKeyCode(), event);
			break;
		case KeyEvent.ACTION_UP:
			mView.onKeyUp(event.getKeyCode(), event);
			break;
		}
        return false;
	}

	@Override
	public boolean clearMetaKeyStates(int states) {
		Log.i(TAG, "clearMetaKeyStates:"+states);
		return false;
	}

	@Override
	public boolean reportFullscreenMode(boolean enabled) {
		Log.i(TAG, "reportFullscreenMode:"+enabled);
        return false;
	}
	

    public static final void removeComposingSpans(Spannable text) {
        text.removeSpan(COMPOSING);
        Object[] sps = text.getSpans(0, text.length(), Object.class);
        if (sps != null) {
            for (int i=sps.length-1; i>=0; i--) {
                Object o = sps[i];
                if ((text.getSpanFlags(o)&Spanned.SPAN_COMPOSING) != 0) {
                    text.removeSpan(o);
                }
            }
        }
    }

    @SuppressWarnings("unused")
	private void replaceText(CharSequence text, int newCursorPosition,
            boolean composing) {
        final Editable content = getEditable();
        if (content == null) {
            return;
        }
        
        beginBatchEdit();

        // delete composing text set previously.
        int a = getComposingSpanStart(content);
        int b = getComposingSpanEnd(content);

        
        if (b < a) {
            int tmp = a;
            a = b;
            b = tmp;
        }

        if (a != -1 && b != -1) {
            removeComposingSpans(content);
        } else {
            a = Selection.getSelectionStart(content);
            b = Selection.getSelectionEnd(content);
            if (a < 0) a = 0;
            if (b < 0) b = 0;
            if (b < a) {
                int tmp = a;
                a = b;
                b = tmp;
            }
        }

        // Position the cursor appropriately, so that after replacing the
        // desired range of text it will be located in the correct spot.
        // This allows us to deal with filters performing edits on the text
        // we are providing here.
        if (newCursorPosition > 0) {
            newCursorPosition += b - 1;
        } else {
            newCursorPosition += a;
        }
        Log.i(TAG, "replaceText:"+newCursorPosition);
        if (newCursorPosition < 0) newCursorPosition = 0;
        if (newCursorPosition > content.length())
            newCursorPosition = content.length();
        content.setSpan(COMPOSING, a, b, 0);
        Selection.setSelection(content, newCursorPosition);

        Log.i(TAG, "replaceText:"+newCursorPosition);
        content.replace(a, b, text);
        
        endBatchEdit();
    }

	@Override
	public boolean commitCorrection(CorrectionInfo arg0) {
		return false;
	}
    
}
