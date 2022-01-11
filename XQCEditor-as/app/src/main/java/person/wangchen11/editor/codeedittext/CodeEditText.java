package person.wangchen11.editor.codeedittext;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import person.wangchen11.ccode.WantMsg;
import person.wangchen11.editor.codeedittext.CodeStyleAdapter.CodeStypeAdapterListener;
import person.wangchen11.editor.edittext.AfterTextChangeListener;
import person.wangchen11.editor.edittext.CodeInputFilter;
import person.wangchen11.editor.edittext.EditableWithLayout;
import person.wangchen11.editor.edittext.MyEditText;
import person.wangchen11.editor.edittext.MyLayout;
import person.wangchen11.editor.edittext.SpanBody;
import person.wangchen11.gnuccompiler.GNUCCompiler;
import person.wangchen11.xqceditor.R;

import android.annotation.SuppressLint;
import android.content.Context;
import android.support.v7.widget.PopupMenu;
import android.text.Editable;
import android.text.InputFilter;
import android.util.AttributeSet;
import android.util.Log;
import android.view.Menu;
import android.view.MotionEvent;

public class CodeEditText extends MyEditText implements CodeStypeAdapterListener{
	protected static final String TAG="CCodeEditText";
	public enum CodeType {
		TYPE_NONE,
		TYPE_C,
		TYPE_CPP,
		TYPE_JAVA,
		TYPE_SHELL,
		TYPE_ARM_ASM,
		TYPE_PHP,
	}
	private CodeType mCodeType=CodeType.TYPE_CPP;
	
	public void setCodeType(CodeType type)
	{
		mCodeType = type;
		postUpdateCodeStyle();
	}
	
	public CodeEditText(Context context) {
		super(context);
	}
	
	public CodeEditText(Context context, AttributeSet attrs) {
		super(context, attrs);
	}
	
	public void setText(CharSequence charSequence) {
		super.setText(charSequence);
		InputFilter []filters = new InputFilter[]{new CodeInputFilter(){
			@Override
			public boolean isEnable() {
				Editable editable = getText();
				if(editable instanceof EditableWithLayout){
					EditableWithLayout editableWithLayout = (EditableWithLayout) editable;
					return editableWithLayout.isSaveToHistory();
				}
				return true;
			}
		}};
		getText().setFilters(filters);
	}
	
	@SuppressLint("ClickableViewAccessibility")
	@Override
	public boolean onTouchEvent(MotionEvent event) {
		if(mOnNeedChangeWants!=null && event.getAction() == MotionEvent.ACTION_DOWN )
			mOnNeedChangeWants.onNeedChangeWants(0, 0, null);
		return super.onTouchEvent(event);
	};
	
	private static ExecutorService mExecutor = null;
	
	@Override
	public void afterTextChanged(Editable s) {
		super.afterTextChanged(s);
		if(mAfterTextChangeListener!=null)
			mAfterTextChangeListener.afterTextChange();
		postUpdateCodeStyle();
	}
	
	private Runnable mUpdateChodeStyleRunnable = new Runnable() {
		@Override
		public void run() {
			updateCodeStyle();
		}
	};
	
	private void postUpdateCodeStyle(){
		if(getHandler()!=null){
			getHandler().removeCallbacks(mUpdateChodeStyleRunnable);
			getHandler().postDelayed(mUpdateChodeStyleRunnable,20);
		}
	}
	
	private void updateCodeStyle(){
		Log.i(TAG, "updateCodeStyle");
		if(mExecutor==null){
			mExecutor=Executors.newSingleThreadExecutor();
		}
		try {
			Log.i(TAG, "mExecutor.execute");
			File file=(getTag() instanceof File )?((File)getTag()):null;
			String path="";
			if(file !=null)
				path=file.getParent();
			Runnable runnable=null;
			if(mCodeType==CodeType.TYPE_C){
				runnable=new CCodeStyleAdapter(getText().toString(),getCursor(),path
						,GNUCCompiler.getIncludeDir(),getHandler(),this) ;
			}
			else
			if(mCodeType==CodeType.TYPE_CPP){
				runnable=new CPPCodeStyleAdapter(getText().toString(),getCursor(),path
						,GNUCCompiler.getIncludeDir(),getHandler(),this);
			}
			else
			if(mCodeType==CodeType.TYPE_JAVA){
				runnable=new JavaCodeStyleAdapter(getText().toString(),getCursor(),"","",getHandler(),this);
			}
			else
			if(mCodeType==CodeType.TYPE_SHELL){
				runnable=new ShellCodeStyleAdapter(getText().toString(),getCursor(),"","",getHandler(),this);
			}
			else
			if(mCodeType==CodeType.TYPE_ARM_ASM){
				runnable=new ArmAsmCodeStyleAdapter(getText().toString(),getCursor(),"","",getHandler(),this);
			}
			else
			if(mCodeType==CodeType.TYPE_PHP){
				runnable=new PHPCodeStyleAdapter(getHandler(),getText().toString(),getCursor(), this);
			}
			else
			{
				EditableWithLayout editableWithLayout=(EditableWithLayout)getText();
				editableWithLayout.applyColorSpans(new ArrayList<SpanBody>());
				postInvalidate();
			}
			if(runnable!=null)
				mExecutor.execute(runnable);
		} catch (OutOfMemoryError e) {
			e.printStackTrace();
		}
	}

	@Override
	public int checkLength()
	{
		return getText().length();
	}
	
	@Override
	public void parserComplete(CodeStyleAdapter adapter,List<SpanBody> spanBodies) {
		Log.i(TAG, "runComplete");
		if(checkLength()==adapter.length()  && getText() instanceof EditableWithLayout )
		{
			Log.i(TAG, "applyColorSpans");
			EditableWithLayout editableWithLayout=(EditableWithLayout)getText();
			if(spanBodies==null)
				spanBodies=new ArrayList<SpanBody>();
			editableWithLayout.applyColorSpans(spanBodies);
			postInvalidate();
		}
	}

	@Override
	public void getWantComplete(CodeStyleAdapter adapter,int wantChangeStart,int wantChangeEnd,List<WantMsg> wants) {
		if(checkLength()==adapter.length()  && getText() instanceof EditableWithLayout )
		{
			if(mOnNeedChangeWants!=null)
				mOnNeedChangeWants.onNeedChangeWants(wantChangeStart, wantChangeEnd, wants);
		}
	}
	
	public void onWantSelect(int start,int end,String str){
		
	}
	
	private AfterTextChangeListener mAfterTextChangeListener=null;
	public void setAfterTextChangeListener(AfterTextChangeListener afterTextChangeListener){
		mAfterTextChangeListener=afterTextChangeListener;
	}
	
	private OnNeedChangeWants mOnNeedChangeWants=null;
	public void setOnNeedChangeWants(OnNeedChangeWants onNeedChangeWants){
		mOnNeedChangeWants=onNeedChangeWants;
	}

	@Override
	public PopupMenu createMenu() {
		PopupMenu popupMenu = super.createMenu();
		Menu menu=popupMenu.getMenu();
		menu.add(0, R.string.comment_uncomment, 0, R.string.comment_uncomment);
		return popupMenu;
	}

	@Override
	public boolean performContextMenuAction(int id) {
		switch (id) {
			case R.string.comment_uncomment: {
				commentOrUncomment();
			}
		}
		return super.performContextMenuAction(id);
	}

	public void commentOrUncomment() {
		final String commentStart = "//";
		Editable editable = getText();
		int selectStart = getSelectionStart();
		int selectEnd = getSelectionEnd();

		MyLayout layout = getLayout();
		int start = layout.getLineStart(layout.getLineForOffset(selectStart));
		int end   = layout.getLineEnd(layout.getLineForOffset(selectEnd)) - 1;
		Log.i(TAG, "commentOrUncomment start:"+start+" end:"+end);
		if (start<0||end<start||end>editable.length()) {
			return;
		}

		String content = editable.subSequence(start,end).toString();

		String[] lines = splitStringKeepSplitter(content,"\n");
		boolean alreadyComment = false;
		for (String line : lines) {
			if (line.startsWith(commentStart)) {
				alreadyComment = true;
				break;
			}
		}

		final boolean doComment = !alreadyComment;
		StringBuilder stringBuilder = new StringBuilder();
		for (int i=0;i<lines.length;i++) {
			final String line = lines[i];
			if (doComment) {
				if (line.startsWith(commentStart)) {
					stringBuilder.append(line);
				} else {
					stringBuilder.append(commentStart).append(line);
				}
			} else { // uncomment
				if (line.startsWith(commentStart)) {
					stringBuilder.append(line.substring(commentStart.length()));
				} else {
					stringBuilder.append(line);
				}
			}
		}
		String replaceStr = stringBuilder.toString();
		editable.replace(start,end,replaceStr);
		int afterSelectEnd = start + replaceStr.length();
		setSelection(start,afterSelectEnd);
	}

	public String[] splitStringKeepSplitter(String content,String splitter) {
		List<String> list = new ArrayList();
		int currentStart = 0;
		int index = 0;
		while( (index = content.indexOf(splitter,currentStart))>=currentStart ) {
			int end = index+splitter.length();
			String line = content.substring(currentStart,end);
			list.add(line);
			currentStart=end;
		}
		String line = content.substring(currentStart);
		if (line.length()>0) {
			list.add(line);
		}

		String[] array = new String[list.size()];
		return list.toArray(array);
	}
}


