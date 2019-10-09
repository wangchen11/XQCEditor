package person.wangchen11.codeanalysis.ccode;

import java.util.ArrayList;
import java.util.Iterator;

import person.wangchen11.util.CharArrayUtil;


public class CCodeParser implements Runnable{
	protected static final String TAG="CCodeParser";
	private char []mCode=null;
	private int mLength=0;
	private ArrayList<CCodeSpan> mCodeSpans=new ArrayList<CCodeSpan>();
	private char [][]mCodeKeyWords_Char=null;
	private char [][]mCodeProKeyWords_Char=null;
	private boolean mIsParsed = false;
	
	public CCodeParser(String code,CCodeKeywordsAdapter keywordsAdapter) {
		mCode=code.toCharArray();
		mLength=code.length();
		mCodeKeyWords_Char = keywordsAdapter.getCCodeKeywords_char();
		mCodeProKeyWords_Char = keywordsAdapter.getCCodeProKeywords_char();
	}
	
	public int length() {
		return mLength;
	}
	
	public boolean isParsed() {
		return mIsParsed;
	}
	
	public char []getCode() {
		return mCode;
	}
	
	public ArrayList<CCodeSpan> getCodeSpans() {
		return mCodeSpans;
	}
	
	@Override
	public void run() {
		try {
			try {
				runEx();
			} catch (Error e) {
				e.printStackTrace();
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		mIsParsed = true;
	}
	
	private void runEx() {
		if(mCode!=null)
		{
			mCodeSpans.clear();
			char []code=mCode;
			int tempIndex;
			int indexFind;
			int keyPos;
			for(int index=0;index<code.length;){
				switch(code[index]){
				case ' ':
				case '\r':
				case '\t':
					index++;
					break;
				case '\n':
					tempIndex=index+1;
					mCodeSpans.add(new CCodeSpan(CCodeSpan.TYPE_NONE, index, tempIndex,code));
					index=tempIndex;
					break;
				case '+':
					if(index+1<code.length && code[index+1]=='=')
					{
						tempIndex=index+2;
						mCodeSpans.add(new CCodeSpan(CCodeSpan.TYPE_NONE, index, tempIndex,code));
						index=tempIndex;
					}
					else
					if(index+1<code.length && code[index+1]=='+')
					{
						tempIndex=index+2;
						mCodeSpans.add(new CCodeSpan(CCodeSpan.TYPE_NONE, index, tempIndex,code));
						index=tempIndex;
					}
					else
					{
						tempIndex=index+1;
						mCodeSpans.add(new CCodeSpan(CCodeSpan.TYPE_NONE, index, tempIndex,code));
						index=tempIndex;
					}
					break;
				case '-':
					if(index+1<code.length && code[index+1]=='=')
					{
						tempIndex=index+2;
						mCodeSpans.add(new CCodeSpan(CCodeSpan.TYPE_NONE, index, tempIndex,code));
						index=tempIndex;
					}
					else
					if(index+1<code.length && code[index+1]=='-')
					{
						tempIndex=index+2;
						mCodeSpans.add(new CCodeSpan(CCodeSpan.TYPE_NONE, index, tempIndex,code));
						index=tempIndex;
					}
					else
					if(index+1<code.length && code[index+1]=='>')
					{
						tempIndex=index+2;
						mCodeSpans.add(new CCodeSpan(CCodeSpan.TYPE_NONE, index, tempIndex,code));
						index=tempIndex;
					}
					else
					{
						tempIndex=index+1;
						mCodeSpans.add(new CCodeSpan(CCodeSpan.TYPE_NONE, index, tempIndex,code));
						index=tempIndex;
					}
					break;
				case '\'':
					indexFind=CharArrayUtil.findEndOfChar(code,index+1);
					tempIndex=indexFind;
					mCodeSpans.add(new CCodeSpan(CCodeSpan.TYPE_CONSTANT, index, tempIndex,code));
					index=tempIndex;
					break;
				case '"':
					indexFind=CharArrayUtil.findEndOfString(code,index+1);
					tempIndex=indexFind;
					mCodeSpans.add(new CCodeSpan(CCodeSpan.TYPE_CONSTANT, index, tempIndex,code));
					index=tempIndex;
					break;
				case '*':
					if(index+1<code.length && code[index+1]=='=')
					{
						tempIndex=index+2;
						mCodeSpans.add(new CCodeSpan(CCodeSpan.TYPE_NONE, index, tempIndex,code));
						index=tempIndex;
					}
					else
					{
						tempIndex=index+1;
						mCodeSpans.add(new CCodeSpan(CCodeSpan.TYPE_NONE, index, tempIndex,code));
						index=tempIndex;
					}
					break;
				case '/':
					if(index+1<code.length && code[index+1]=='/')
					{//单行注释
						int find=CharArrayUtil.strchr(code,index+2,'\n');
						if(find!=-1){
							tempIndex=find;
							mCodeSpans.add(new CCodeSpan(CCodeSpan.TYPE_COMMENTS, index, tempIndex,code));
							index=tempIndex;
						}
						else
						{
							tempIndex=code.length;
							mCodeSpans.add(new CCodeSpan(CCodeSpan.TYPE_NONE, index, tempIndex,code));
							index=tempIndex;
						}
					}
					else
					if(index+1<code.length && code[index+1]=='*')
					{//多行注释
						indexFind=CharArrayUtil.findEndOfMultiComment(code,index+2);
						tempIndex=indexFind;
						mCodeSpans.add(new CCodeSpan(CCodeSpan.TYPE_COMMENTS, index, tempIndex,code));
						index=tempIndex;
					}
					else
					if(index+1<code.length && code[index+1]=='=')
					{
						tempIndex=index+2;
						mCodeSpans.add(new CCodeSpan(CCodeSpan.TYPE_NONE, index, tempIndex,code));
						index=tempIndex;
					}
					else
					{
						tempIndex=index+1;
						mCodeSpans.add(new CCodeSpan(CCodeSpan.TYPE_NONE, index, tempIndex,code));
						index=tempIndex;
					}
					break;
				case '%':
					if(index+1<code.length && code[index+1]=='=')
					{
						tempIndex=index+2;
						mCodeSpans.add(new CCodeSpan(CCodeSpan.TYPE_NONE, index, tempIndex,code));
						index=tempIndex;
					}
					else
					{
						tempIndex=index+1;
						mCodeSpans.add(new CCodeSpan(CCodeSpan.TYPE_NONE, index, tempIndex,code));
						index=tempIndex;
					}
					break;
				case '&':
					if(index+1<code.length && code[index+1]=='=')
					{
						tempIndex=index+2;
						mCodeSpans.add(new CCodeSpan(CCodeSpan.TYPE_NONE, index, tempIndex,code));
						index=tempIndex;
					}
					else
					if(index+1<code.length && code[index+1]=='&')
					{
						tempIndex=index+2;
						mCodeSpans.add(new CCodeSpan(CCodeSpan.TYPE_NONE, index, tempIndex,code));
						index=tempIndex;
					}
					else
					{
						tempIndex=index+1;
						mCodeSpans.add(new CCodeSpan(CCodeSpan.TYPE_NONE, index, tempIndex,code));
						index=tempIndex;
					}
					break;
				case '|':
					if(index+1<code.length && code[index+1]=='=')
					{
						tempIndex=index+2;
						mCodeSpans.add(new CCodeSpan(CCodeSpan.TYPE_NONE, index, tempIndex,code));
						index=tempIndex;
					}
					else
					if(index+1<code.length && code[index+1]=='|')
					{
						tempIndex=index+2;
						mCodeSpans.add(new CCodeSpan(CCodeSpan.TYPE_NONE, index, tempIndex,code));
						index=tempIndex;
					}
					else
					{
						tempIndex=index+1;
						mCodeSpans.add(new CCodeSpan(CCodeSpan.TYPE_NONE, index, tempIndex,code));
						index=tempIndex;
					}
					break;
				case '^':
					if(index+1<code.length && code[index+1]=='=')
					{
						tempIndex=index+2;
						mCodeSpans.add(new CCodeSpan(CCodeSpan.TYPE_NONE, index, tempIndex,code));
						index=tempIndex;
					}
					else
					{
						tempIndex=index+1;
						mCodeSpans.add(new CCodeSpan(CCodeSpan.TYPE_NONE, index, tempIndex,code));
						index=tempIndex;
					}
					break;
				case '~':
					tempIndex=index+1;
					mCodeSpans.add(new CCodeSpan(CCodeSpan.TYPE_NONE, index, tempIndex,code));
					index=tempIndex;
					break;
				case '!':
					if(index+1<code.length && code[index+1]=='=')
					{
						tempIndex=index+2;
						mCodeSpans.add(new CCodeSpan(CCodeSpan.TYPE_NONE, index, tempIndex,code));
						index=tempIndex;
					}
					else
					{
						tempIndex=index+1;
						mCodeSpans.add(new CCodeSpan(CCodeSpan.TYPE_NONE, index, tempIndex,code));
						index=tempIndex;
					}
					break;
				case '<':
					if(index+1<code.length && code[index+1]=='<')
					{
						if(index+2<code.length && code[index+2]=='=')
						{
							tempIndex=index+3;
							mCodeSpans.add(new CCodeSpan(CCodeSpan.TYPE_NONE, index, tempIndex,code));
							index=tempIndex;
						}
						else
						{
							tempIndex=index+2;
							mCodeSpans.add(new CCodeSpan(CCodeSpan.TYPE_NONE, index, tempIndex,code));
							index=tempIndex;
						}
					}
					else
					if(index+1<code.length && code[index+1]=='=')
					{
						tempIndex=index+2;
						mCodeSpans.add(new CCodeSpan(CCodeSpan.TYPE_NONE, index, tempIndex,code));
						index=tempIndex;
					}
					else
					{
						tempIndex=index+1;
						mCodeSpans.add(new CCodeSpan(CCodeSpan.TYPE_NONE, index, tempIndex,code));
						index=tempIndex;
					}
					break;
				case '>':
					if(index+1<code.length && code[index+1]=='>')
					{
						if(index+2<code.length && code[index+2]=='=')
						{
							tempIndex=index+3;
							mCodeSpans.add(new CCodeSpan(CCodeSpan.TYPE_NONE, index, tempIndex,code));
							index=tempIndex;
						}
						else
						{
							tempIndex=index+2;
							mCodeSpans.add(new CCodeSpan(CCodeSpan.TYPE_NONE, index, tempIndex,code));
							index=tempIndex;
						}
					}
					else
					if(index+1<code.length && code[index+1]=='=')
					{
						tempIndex=index+2;
						mCodeSpans.add(new CCodeSpan(CCodeSpan.TYPE_NONE, index, tempIndex,code));
						index=tempIndex;
					}
					else
					{
						tempIndex=index+1;
						mCodeSpans.add(new CCodeSpan(CCodeSpan.TYPE_NONE, index, tempIndex,code));
						index=tempIndex;
					}
					break;
				case '(':
					tempIndex=index+1;
					mCodeSpans.add(new CCodeSpan(CCodeSpan.TYPE_NONE, index, tempIndex,code));
					index=tempIndex;
					break;
				case ')':
					tempIndex=index+1;
					mCodeSpans.add(new CCodeSpan(CCodeSpan.TYPE_NONE, index, tempIndex,code));
					index=tempIndex;
					break;
				case '[':
					tempIndex=index+1;
					mCodeSpans.add(new CCodeSpan(CCodeSpan.TYPE_NONE, index, tempIndex,code));
					index=tempIndex;
					break;
				case ']':
					tempIndex=index+1;
					mCodeSpans.add(new CCodeSpan(CCodeSpan.TYPE_NONE, index, tempIndex,code));
					index=tempIndex;
					break;
				case '{':
					tempIndex=index+1;
					mCodeSpans.add(new CCodeSpan(CCodeSpan.TYPE_NONE, index, tempIndex,code));
					index=tempIndex;
					break;
				case '}':
					tempIndex=index+1;
					mCodeSpans.add(new CCodeSpan(CCodeSpan.TYPE_NONE, index, tempIndex,code));
					index=tempIndex;
					break;
				case '.':
					tempIndex=index+1;
					mCodeSpans.add(new CCodeSpan(CCodeSpan.TYPE_NONE, index, tempIndex,code));
					index=tempIndex;
					break;
				case ';':
					tempIndex=index+1;
					mCodeSpans.add(new CCodeSpan(CCodeSpan.TYPE_NONE, index, tempIndex,code));
					index=tempIndex;
					break;
				case ',':
					tempIndex=index+1;
					mCodeSpans.add(new CCodeSpan(CCodeSpan.TYPE_NONE, index, tempIndex,code));
					index=tempIndex;
					break;
				case '?':
					tempIndex=index+1;
					mCodeSpans.add(new CCodeSpan(CCodeSpan.TYPE_NONE, index, tempIndex,code));
					index=tempIndex;
					break;
				case ':':
					tempIndex=index+1;
					mCodeSpans.add(new CCodeSpan(CCodeSpan.TYPE_NONE, index, tempIndex,code));
					index=tempIndex;
					break;
				case '\\':
					tempIndex=index+1;
					mCodeSpans.add(new CCodeSpan(CCodeSpan.TYPE_NONE, index, tempIndex,code));
					index=tempIndex;
					break;
				case '=':
					if(index+1<code.length && code[index+1]=='=')
					{
						tempIndex=index+2;
						mCodeSpans.add(new CCodeSpan(CCodeSpan.TYPE_NONE, index, tempIndex,code));
						index=tempIndex;
					}
					else
					{
						tempIndex=index+1;
						mCodeSpans.add(new CCodeSpan(CCodeSpan.TYPE_NONE, index, tempIndex,code));
						index=tempIndex;
					}
					break;
				case '#':
					tempIndex=CharArrayUtil.findNotEmpty(code,index+1);
					indexFind = CharArrayUtil.findEndOfIdentifier(code, tempIndex);
					keyPos=CharArrayUtil.findProKeyWord(code,tempIndex,indexFind-(tempIndex),mCodeProKeyWords_Char);
					if(keyPos>=0){
						String key = String.valueOf(code,tempIndex,indexFind-(tempIndex));
						tempIndex=indexFind;
						mCodeSpans.add(new CCodeSpan(CCodeSpan.TYPE_PRO_KEY_WORDS, index, tempIndex,code));
						index=tempIndex;
						if(key.endsWith("include")) 
						{
							index=CharArrayUtil.findNotEmpty(code,index);
							if(code[index]=='<')
							{
								indexFind=CharArrayUtil.findEndOfIncludeSystem(code,index);
								tempIndex=indexFind;
								mCodeSpans.add(new CCodeSpan(CCodeSpan.TYPE_CONSTANT, index, tempIndex,code));
								index=tempIndex;
							}else{
								break;
							}
						}
					}
					else{
						index++;
					}
					break;
				default:
					if( Character.isLetter(code[index]) || code[index]=='_' )
					{
						indexFind = CharArrayUtil.findEndOfIdentifier(code, index+1);
						keyPos=CharArrayUtil.findProKeyWord(code,index,indexFind-(index),mCodeKeyWords_Char);
						if(keyPos>=0){
							//关键字 
							tempIndex=indexFind;
							mCodeSpans.add(new CCodeSpan(CCodeSpan.TYPE_KEY_WORDS, index, tempIndex,code));
							index=tempIndex;
						}
						else{
							//普通字 
							tempIndex=indexFind;
							mCodeSpans.add(new CCodeSpan(CCodeSpan.TYPE_WORDS, index, tempIndex,code));
							index=tempIndex;
						}
					}
					else
					if( Character.isDigit(code[index]) )
					{
						indexFind = CharArrayUtil.findEndOfNumber(code, index+1);
						tempIndex=indexFind;
						mCodeSpans.add(new CCodeSpan(CCodeSpan.TYPE_CONSTANT, index, tempIndex,code));
						index=tempIndex;
					}
					else
						index++;
					break;
				}
			}
		}
	}
	
	@Override
	public String toString() {
		Iterator<CCodeSpan> iterator = mCodeSpans.iterator();
		StringBuilder builder = new StringBuilder();
		while(iterator.hasNext())
		{
			CCodeSpan codeSpan = iterator.next();
			builder.append(codeSpan.toString());
			builder.append("\n");
		}
		return builder.toString();
	}
}
