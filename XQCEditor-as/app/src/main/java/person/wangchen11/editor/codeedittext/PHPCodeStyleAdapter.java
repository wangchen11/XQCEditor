package person.wangchen11.editor.codeedittext;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

import person.wangchen11.ccode.WantMsg;
import person.wangchen11.codeanalysis.phpcode.PHPCodeKeyWords;
import person.wangchen11.codeanalysis.phpcode.PHPCodeKeywordsAdapter;
import person.wangchen11.codeanalysis.phpcode.PHPCodeParser;
import person.wangchen11.codeanalysis.phpcode.PHPCodeSpan;
import person.wangchen11.editor.edittext.SpanBody;
import android.os.Handler;
import android.text.style.ForegroundColorSpan;

public class PHPCodeStyleAdapter extends CodeStyleAdapter{
	PHPCodeParser mCodeParser = null;
	PHPIndexer    mIndexer = null;
	
	public PHPCodeStyleAdapter(Handler handler,String code,int pos,CodeStypeAdapterListener listener) {
		super(handler,code.length(),listener);
		mCodeParser = new PHPCodeParser(code,new PHPCodeKeywordsAdapter() {
			@Override
			public String[] getPHPCodeKeywords() {
				return PHPCodeKeyWords.mKeyWord;
			}
		});
		mIndexer = new PHPIndexer(mCodeParser, pos);
	}

	@Override
	public void parser() {
		mCodeParser.run();
		mIndexer.run();
	}

	@Override
	public List<SpanBody> getStyles() {
		LinkedList<PHPCodeSpan> codeSpans=mCodeParser.getCodeSpans();

		ArrayList<SpanBody> bodies=new ArrayList<SpanBody>();
		Iterator<PHPCodeSpan> iterator=codeSpans.iterator();
		while(iterator.hasNext())
		{
			PHPCodeSpan codeSpan=iterator.next();
			ForegroundColorSpan colorSpan=getColorSpanByCodeEntity(codeSpan);
			if(colorSpan!=null)
			{
				SpanBody spanBody=new SpanBody(colorSpan,codeSpan.mStart,codeSpan.mEnd,0);
				bodies.add(spanBody);
			}
		}
		return bodies;
	}
	
	private ForegroundColorSpan getColorSpanByCodeEntity(PHPCodeSpan codeSpan){
		switch (codeSpan.mType) {
		case PHPCodeSpan.TYPE_COMMENTS:
			return mCommentsColorSpan;
		case PHPCodeSpan.TYPE_CONSTANT:
			return mConstantColorSpan;
		case PHPCodeSpan.TYPE_KEY_WORDS:
			return mKeywordsColorSpan;
		case PHPCodeSpan.TYPE_WORDS:
			return mWordsColorSpan;
		}
		return null;
	}


	@Override
	public LinkedList<WantMsg> getWants() {
		LinkedList<WantMsg> linkedList = new LinkedList<WantMsg>();
		Iterator<String> iterator = mIndexer.getWants().iterator();
		while(iterator.hasNext()){
			linkedList.addLast(new WantMsg(iterator.next()));
		}
		return linkedList;
	}

	@Override
	public int getWantChangeStart() {
		return mIndexer.getWantChangeStart();
	}

	@Override
	public int getWantChangeEnd() {
		return mIndexer.getWantChangeEnd();
	}}
