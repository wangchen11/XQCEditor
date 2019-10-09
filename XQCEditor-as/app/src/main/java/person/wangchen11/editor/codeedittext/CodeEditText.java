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
import person.wangchen11.editor.edittext.SpanBody;
import person.wangchen11.gnuccompiler.GNUCCompiler;
import android.annotation.SuppressLint;
import android.content.Context;
import android.text.Editable;
import android.text.InputFilter;
import android.util.AttributeSet;
import android.util.Log;
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
}


