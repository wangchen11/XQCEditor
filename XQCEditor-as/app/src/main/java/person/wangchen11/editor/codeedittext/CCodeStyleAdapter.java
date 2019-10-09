package person.wangchen11.editor.codeedittext;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import android.os.Handler;
import android.text.style.ForegroundColorSpan;
import person.wangchen11.ccode.CodeEntity;
import person.wangchen11.ccode.CCodeParser;
import person.wangchen11.ccode.WantMsg;
import person.wangchen11.editor.edittext.SpanBody;

public class CCodeStyleAdapter extends CodeStyleAdapter{
	private CCodeParser mCodeParser;
	private int mChangePosition=0;
	public CCodeStyleAdapter(String code,Handler handler,CodeStypeAdapterListener listener) {
		super(handler,code.length(),listener);
		mCodeParser=createCodeParser(code);
	}

	public CCodeStyleAdapter(String code,int changePosition,String dir,String sysDir,Handler handler,CodeStypeAdapterListener listener) {
		super(handler,code.length(),listener);
		mChangePosition=changePosition;
		mCodeParser=createCodeParser(code,dir,sysDir);
	}

	protected CCodeParser createCodeParser(String code)
	{
		return new CCodeParser(code);
	}
	
	protected CCodeParser createCodeParser(String code,String dir,String sysDir)
	{
		return new CCodeParser(code,dir,sysDir);
	}

	@Override
	public void parser() {
		mCodeParser.run();
	}
	
	public List<SpanBody> getStyles() {
		LinkedList<CodeEntity> entities=mCodeParser.getEntities();
		ArrayList<SpanBody> bodies=new ArrayList<SpanBody>();
		Iterator<CodeEntity> iterator=entities.iterator();
		while(iterator.hasNext())
		{
			CodeEntity entity=iterator.next();
			ForegroundColorSpan colorSpan=getColorSpanByCodeEntity(entity);
			if(colorSpan!=null)
			{
				//Log.i("getStyles", "colorSpan:"+colorSpan.getForegroundColor());
				SpanBody spanBody=new SpanBody(colorSpan,entity.mStart,entity.mEnd,entity.mTag);
				bodies.add(spanBody);
			}
		}
		return bodies;
	}
	
	public int getWantChangeStart(){
		return mCodeParser.getWantChangeStart();
	} 
	
	public int getWantChangeEnd(){
		return mCodeParser.getWantChangeEnd();
	} 
	
	public LinkedList<WantMsg> getWants(){
		return mCodeParser.getWant(mChangePosition,0);
	}
	
	private ForegroundColorSpan getColorSpanByCodeEntity(CodeEntity entity){
		switch (entity.mType) {
		case CodeEntity.TYPE_COMMENTS:
			return mCommentsColorSpan;
		case CodeEntity.TYPE_CONSTANT:
			return mConstantColorSpan;
		case CodeEntity.TYPE_KEY_WORDS:
			return mKeywordsColorSpan;
		case CodeEntity.TYPE_PRO_KEY_WORDS:
			return mProKeywordsColorSpan;
		case CodeEntity.TYPE_WORDS:
			return mWordsColorSpan;
		}
		return null;
	}

	public int length(){
		return mCodeParser.length();
	}
	
}
