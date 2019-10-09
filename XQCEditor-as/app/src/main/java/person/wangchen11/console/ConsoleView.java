package person.wangchen11.console;

import person.wangchen11.editor.edittext.MyEditText;
import android.content.Context;
import android.util.AttributeSet;
import android.view.KeyEvent;

public class ConsoleView extends MyEditText  {
	public ConsoleView(Context context) {
		super(context);
		init();
	}
	
	public ConsoleView(Context context, AttributeSet attrs) {
		super(context, attrs);
		init();
	}
	
	private void init() {
		//setTextSize(getContext().getResources().getDisplayMetrics().density*12);
	}
	
	@Override
	public boolean onKeyUp(int keyCode, KeyEvent event) {
		if(keyCode==KeyEvent.KEYCODE_ENTER){
			if(mBaseInputConnection instanceof ConsoleInputConnection ){
				boolean ret=((ConsoleInputConnection)mBaseInputConnection).onEnterKey();
				if(ret)
					return ret;
			}
		}
		if(keyCode==KeyEvent.KEYCODE_DEL){
			if(mBaseInputConnection instanceof ConsoleInputConnection ){
				boolean ret=((ConsoleInputConnection)mBaseInputConnection).onDeleteKey();
				if(ret)
					return ret;
			}
		}
		
		return super.onKeyUp(keyCode, event);
	}
	
	@Override
	public void setText(CharSequence charSequence){
		if(mBaseInputConnection==null)
			mBaseInputConnection=new ConsoleInputConnection(this)
		{
		    public boolean clearMetaKeyStates(int states) {
		    	mDownState=0;
		    	return super.clearMetaKeyStates(states);
		    }
		    
			@Override
		    public boolean performContextMenuAction(int id) {
				if(id==android.R.id.cut ){
					
				}
				else
				if(id==android.R.id.paste && isSelection()){
					
				}
				else{
					ConsoleView.this.performContextMenuAction(id);
				}
		        return super.performContextMenuAction(id);
		    }
			
			@Override
			public boolean setSelection(int start, int end) {
				
				if(start==end){
					post(new Runnable() {
						@Override
						public void run() {
							bringPosToVisible(getCursor());
						}
					});
				}
				
				if(isMoveSelectionStart()){
					postDelayed(new Runnable() {
						@Override
						public void run() {
							bringPosToVisible(getSelectionStart());
						}
					},100);
				}else
				if(isMoveSelectionEnd()){
					postDelayed(new Runnable() {
						@Override
						public void run() {
							bringPosToVisible(getSelectionEnd());
						}
					},100);
				}
				return super.setSelection(start, end);
			}
		};
		
		mBaseInputConnection.getEditable().clear();
		if(charSequence!=null){
			mBaseInputConnection.getEditable().insert(0, charSequence);
		}
		else{
		}

		if(getCursor()>mBaseInputConnection.getEditable().length())
			setCursor(mBaseInputConnection.getEditable().length());
		makeLayout();
	}
	
	public void destory(){
		if(mBaseInputConnection instanceof ConsoleInputConnection)
		{
			((ConsoleInputConnection)mBaseInputConnection).destory();
		}
	}
	public void setConsoleCloseListener( OnConsoleColseListener colseListener){
		if(mBaseInputConnection instanceof ConsoleInputConnection)
		{
			((ConsoleInputConnection)mBaseInputConnection).setConsoleCloseListener(colseListener);
		}
	}
	public Console getConsole(){
		//Log.i(TAG, "getConsole:"+mBaseInputConnection);
		if(mBaseInputConnection instanceof ConsoleInputConnection)
		{
			return ((ConsoleInputConnection)mBaseInputConnection).getConsole();
		}
		return null;
	}
}
