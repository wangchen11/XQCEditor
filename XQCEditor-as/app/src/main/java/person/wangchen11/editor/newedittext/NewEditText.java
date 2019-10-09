package person.wangchen11.editor.newedittext;

import java.io.File;
import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import person.wangchen11.ccode.WantMsg;
import person.wangchen11.editor.codeedittext.CodeEditText.CodeType;
import person.wangchen11.editor.codeedittext.ArmAsmCodeStyleAdapter;
import person.wangchen11.editor.codeedittext.CCodeStyleAdapter;
import person.wangchen11.editor.codeedittext.CPPCodeStyleAdapter;
import person.wangchen11.editor.codeedittext.CodeStyleAdapter;
import person.wangchen11.editor.codeedittext.CodeStyleAdapter.CodeStypeAdapterListener;
import person.wangchen11.editor.codeedittext.JavaCodeStyleAdapter;
import person.wangchen11.editor.codeedittext.OnNeedChangeWants;
import person.wangchen11.editor.codeedittext.PHPCodeStyleAdapter;
import person.wangchen11.editor.codeedittext.ShellCodeStyleAdapter;
import person.wangchen11.editor.codeedittext.TextCodeStyleAdapter;
import person.wangchen11.editor.edittext.AfterTextChangeListener;
import person.wangchen11.editor.edittext.SpanBody;
import person.wangchen11.editor.edittext.WarnAndError;
import person.wangchen11.gnuccompiler.GNUCCompiler;
import person.wangchen11.waps.Waps;
import person.wangchen11.window.ext.Setting;

import com.editor.text.TextEditorView;
import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Rect;
import android.os.Handler;
import android.text.Editable;
import android.text.Layout;
import android.text.Spannable;
import android.text.style.ForegroundColorSpan;
import android.util.AttributeSet;
import android.util.Log;
import android.view.ContextMenu;
import android.view.MotionEvent;
import android.widget.TextView;

public class NewEditText extends TextEditorView implements CodeStypeAdapterListener {
	protected static final String TAG="NewEditText";
	private CodeType mCodeType;
	private OnNeedChangeWants mOnNeedChangeWants;
	private int mValidHeadLen = -1;
	private int mValidTailLen = -1;
	private Handler mHandler;
	private LinkedList<WarnAndError> mWarnAndErrors = null;
	
	public NewEditText(Context context) {
		super(context);
		init();
	}

	public NewEditText(Context context, AttributeSet attrs) {
		super(context, attrs);
		init();
	}
	
	private void init() {
		mHandler = new Handler();
	}

	@Override
	public Handler getHandler() {
		return mHandler;
	}
	
	public void setOnNeedChangeWants(OnNeedChangeWants onNeedChangeWants) {
		mOnNeedChangeWants = onNeedChangeWants;
	}

	private static ExecutorService mExecutor = null;

	private AfterTextChangeListener mAfterTextChangeListener = null;
	public void setAfterTextChangeListener(AfterTextChangeListener afterTextChangeListener) {
		mAfterTextChangeListener = afterTextChangeListener;
	}
	
	@Override
	protected void onDraw(Canvas canvas) {
		super.onDraw(canvas);
		if(mWarnAndErrors!=null){
			Layout layout = getLayout();
			for(WarnAndError warnAndError : mWarnAndErrors){
				if(warnAndError.mDrawUnderLine&&
						warnAndError.mFullLine&&
						warnAndError.mLevel==WarnAndError.LEVEL_INFO&&
						warnAndError.mLine>=0 && warnAndError.mLine<layout.getLineCount()
						){
					drawWarnAndError(canvas,warnAndError,layout);
				}
			}
			for(WarnAndError warnAndError : mWarnAndErrors){
				if(warnAndError.mDrawUnderLine&&
						warnAndError.mFullLine&&
						warnAndError.mLevel==WarnAndError.LEVEL_WARN&&
						warnAndError.mLine>=0 && warnAndError.mLine<layout.getLineCount()
						){
					drawWarnAndError(canvas,warnAndError,layout);
				}
			}
			for(WarnAndError warnAndError : mWarnAndErrors){
				if(warnAndError.mDrawUnderLine&&
						warnAndError.mFullLine&&
						warnAndError.mLevel==WarnAndError.LEVEL_ERROR &&
						warnAndError.mLine>=0 && warnAndError.mLine<layout.getLineCount()
						){
					drawWarnAndError(canvas,warnAndError,layout);
				}
			}
		}
	}
	
	private void drawWarnAndError(Canvas canvas,WarnAndError warnAndError,Layout layout){
		Rect bounds = new Rect();
		Paint paint = new Paint();
		paint.setStrokeWidth(getTextSize()/8);
		paint.setColor(warnAndError.mColor);
		layout.getLineBounds(warnAndError.mLine, bounds);
		float left = getPaddingLeft();
		float right = left+layout.getLineRight(warnAndError.mLine);
		float bottom = layout.getLineBaseline(warnAndError.mLine);
		canvas.drawLine(left,bottom,right,bottom, paint);
	} 
	
	private int mTouchDownLine = -1;
	@SuppressLint("ClickableViewAccessibility")
	@Override
	public boolean onTouchEvent(MotionEvent event) {
		if(event.getAction()==MotionEvent.ACTION_DOWN){
			if(mOnNeedChangeWants!=null)
				mOnNeedChangeWants.onNeedChangeWants(0, 0, null);
			Layout layout = getLayout();
			if(layout!=null)
				mTouchDownLine = layout.getLineForVertical((int) event.getY());
		}
		return super.onTouchEvent(event);
	}

	@Override
	public void onTextChanged(CharSequence s, int start, int before, int count) {
		long startTime = System.currentTimeMillis();
		int end   = start+count;
		Editable editable = getText();
		ForegroundColorSpan foreSpans[] = editable.getSpans(start,start+count,
													ForegroundColorSpan.class);

		Log.i(TAG, "onTextChanged get spans used:"+(System.currentTimeMillis()-startTime));
		start = start-1;
		for(ForegroundColorSpan foregroundColorSpan:foreSpans){
			start = Math.min(start, editable.getSpanStart(foregroundColorSpan));
			end   = Math.max(end  , editable.getSpanEnd  (foregroundColorSpan));
		}
		Log.i(TAG, "onTextChanged list spans used:"+(System.currentTimeMillis()-startTime));
		if(mValidHeadLen==-1){
			mValidHeadLen = start;
		} else {
			mValidHeadLen = Math.min(start, mValidHeadLen);
		}
		if(mValidTailLen==-1){
			mValidTailLen = s.length()-end;
		}else{
			mValidTailLen = Math.min(s.length()-end, mValidTailLen);
		}
		Log.i(TAG, "onTextChanged used:"+(System.currentTimeMillis()-startTime));
	}

	@Override
	public void afterTextChanged(Editable s) {
		long startTime = System.currentTimeMillis();
		if(mAfterTextChangeListener!=null)
			mAfterTextChangeListener.afterTextChange();
		getHandler().removeCallbacks(mUpdateChodeStyleRunnable);
		
		mHandler.postDelayed(new Runnable() {
			@Override
			public void run() {
				postUpdateCodeStyle();
			}
		},10);
		Log.i(TAG, "afterTextChanged used:"+(System.currentTimeMillis()-startTime));
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
			getHandler().postDelayed(mUpdateChodeStyleRunnable,100);
		}else{
			Log.i(TAG, "postUpdateCodeStyle getHandler()==null");
		}
	}
	
	private void updateCodeStyle(){
		long startTime = System.currentTimeMillis();
		
		if(mExecutor==null){
			Log.i(TAG, "newSingleThreadExecutor");
			mExecutor=Executors.newSingleThreadExecutor();
		}
		try {
			Log.i(TAG, "mExecutor.execute");
			Log.i(TAG, "updateCodeStyle:"+mCodeType);
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
				runnable=new TextCodeStyleAdapter(getHandler(),getText().length(), this);
				//EditableWithLayout editableWithLayout=getText();
				//editableWithLayout.applyColorSpans(new ArrayList<SpanBody>());
				//postInvalidate();
			}
			if(runnable!=null)
				mExecutor.execute(runnable);
		} catch (OutOfMemoryError e) {
			e.printStackTrace();
		}
		Log.i(TAG, "updateCodeStyle used:"+(System.currentTimeMillis()-startTime));
	}

	private int getCursor() {
		return getSelectionEnd();
	}

	@Override
	public int checkLength()
	{
		return getText().length();
	}
	
	@Override
	public void parserComplete(CodeStyleAdapter adapter,List<SpanBody> spanBodies) {
		Log.i(TAG, "parserComplete");
		long startTime = System.currentTimeMillis();
		if (!Setting.mConfig.mCEditorConfig.mEnableHighLight) {
			Editable editable = getText();
			ForegroundColorSpan foreSpans[] = editable.getSpans(0,
					editable.length(), ForegroundColorSpan.class);
			for (int n = foreSpans.length; n-- > 0;)
				editable.removeSpan(foreSpans[n]);
		}
		else
		if(checkLength()==adapter.length())
		{
			Editable editable = getText();
			
			if(mValidHeadLen==-1||mValidTailLen==-1){
				mValidHeadLen = 0;
				mValidTailLen = 0;
			}
			int invalidStart = mValidHeadLen;
			int invalidEnd   = editable.length()-mValidTailLen;
			

			for(SpanBody spanBody:spanBodies){
				if(		spanBody.mSpan == CodeStyleAdapter.mCommentsColorSpan ||
						(spanBody.mSpan == CodeStyleAdapter.mConstantColorSpan /*&& 161 != spanBody.mFlag */) ||
						spanBody.mSpan == CodeStyleAdapter.mKeywordsColorSpan ||
						spanBody.mSpan == CodeStyleAdapter.mProKeywordsColorSpan
						)
				if(spanBody.hasSub(invalidStart, invalidEnd)){
					invalidStart = Math.min(invalidStart, spanBody.mStart);
					invalidEnd = Math.max(invalidEnd, spanBody.mEnd);
				}
			}
			
			ForegroundColorSpan foreSpans[] = editable.getSpans(invalidStart, invalidEnd,
														ForegroundColorSpan.class);

			for (int n = foreSpans.length; n-- > 0;)
				editable.removeSpan(foreSpans[n]);

			for(SpanBody spanBody:spanBodies){
				if(		spanBody.mSpan == CodeStyleAdapter.mCommentsColorSpan ||
						(spanBody.mSpan == CodeStyleAdapter.mConstantColorSpan /*&& 161 != spanBody.mFlag*/ ) ||
						spanBody.mSpan == CodeStyleAdapter.mKeywordsColorSpan ||
						spanBody.mSpan == CodeStyleAdapter.mProKeywordsColorSpan
						)
				if(spanBody.hasSub(invalidStart, invalidEnd)){
					//Log.i(TAG, "mFlag:"+spanBody.mFlag);
					editable.setSpan(new ForegroundColorSpan( ((ForegroundColorSpan)spanBody.mSpan).getForegroundColor() )
						, spanBody.mStart, spanBody.mEnd, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
				}
			}
			mValidHeadLen = -1;
			mValidTailLen = -1;
		}
		Log.i(TAG, "parserComplete used:"+(System.currentTimeMillis()-startTime));
	}

	@Override
	public void getWantComplete(CodeStyleAdapter adapter,int wantChangeStart,int wantChangeEnd,List<WantMsg> wants) {
		if(checkLength()==adapter.length())
		{
			if(mOnNeedChangeWants!=null)
				mOnNeedChangeWants.onNeedChangeWants(wantChangeStart, wantChangeEnd, wants);
		}
	}
	
	public void setWarnAndErrors(LinkedList<WarnAndError> warnAndErrors) {
		if(warnAndErrors!=null)
			Log.i(TAG, "setWarnAndError:"+warnAndErrors.size());
		mWarnAndErrors = warnAndErrors;
	}

	public void setCodeType(CodeType type) {
		Log.i(TAG, "setCodeType:"+type);
		mValidHeadLen = 0;
		mValidTailLen = 0;
		mCodeType = type;
		mHandler.post(new Runnable() {
			@Override
			public void run() {
				postUpdateCodeStyle();
			}
		});
	}
	
	@Override
	protected void onCreateContextMenu(ContextMenu menu) {
		if(mWarnAndErrors!=null){
			List<WarnAndError> warnAndErrors = new LinkedList<WarnAndError>();
			for(WarnAndError warnAndError:mWarnAndErrors){
				if(warnAndError.mLine==mTouchDownLine){
					warnAndErrors.add(warnAndError);
				}
			}
			if(warnAndErrors.size()>0){
				showWarnAndErrors(warnAndErrors);
				mHandler.post(new Runnable() {
					@Override
					public void run() {
						setSelection(getSelectionEnd());
					}
				});
			}
		}
		super.onCreateContextMenu(menu);
	}
	
	protected void showWarnAndErrors(List<WarnAndError> warnAndErrors){
		if(warnAndErrors.size()<=0)
			return;
		AlertDialog alertDialog = null;
		AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
		builder.setCancelable(true);
		TextView textView = new TextView(getContext());
		
		String msg = "";
		for(WarnAndError warnAndError:warnAndErrors){
			msg+=""+getResources().getText(warnAndError.getTitle())+":\n";
			msg+=""+warnAndError.mMsg+"\n";
			String trasMsg = WarnAndError.translateMsg(warnAndError.mMsg);
			if(!Waps.isGoogle())
			if(!warnAndError.mMsg.equals(trasMsg))
				msg+=""+trasMsg+"\n";
			msg+="\n";
		}
		textView.setText(msg);
		textView.setPadding(12, 12, 12, 12);
		textView.setTextIsSelectable(true);
		builder.setView(textView);
		alertDialog = builder.create();
		alertDialog.show();
	}
	
	@Override
	public void setPadding(int left, int top, int right, int bottom) {
		super.setPadding(left, top, right, bottom);
	}
}
