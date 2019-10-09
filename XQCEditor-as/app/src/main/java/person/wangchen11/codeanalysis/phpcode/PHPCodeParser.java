package person.wangchen11.codeanalysis.phpcode;

import java.util.LinkedList;

import person.wangchen11.util.CharArrayUtil;

public class PHPCodeParser implements Runnable{
	private char []mCode=null;
	private boolean mIsParsed = false;
	private LinkedList<PHPCodeSpan> mCodeSpans = new LinkedList<PHPCodeSpan>();
	private char [][] mCodeKeyWords_Char;
	
	public PHPCodeParser(String code,PHPCodeKeywordsAdapter keywordsAdapter) {
		mCode=code.toCharArray();
		mCodeKeyWords_Char = keywordsAdapter.getPHPCodeKeywords_char();
	}

	public LinkedList<PHPCodeSpan> getCodeSpans()
	{
		return mCodeSpans;
	}
	
	public char []getCode()
	{
		return mCode;
	}
	
	public boolean isParsed()
	{
		return mIsParsed;
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
		mCodeSpans.clear();
		if(mCode!=null)
		{
			char []phpLable = "<?php".toCharArray();
			char []code=mCode;
			int indexFind = 0;
			int tempIndex = 0;
			for(int index=0;index<code.length;){
				switch (code [index]) {
				case '<':
					if( CharArrayUtil.strncmp(code, index,phpLable, 0, phpLable.length)==0 )
					{
						indexFind=index+phpLable.length;
						tempIndex=indexFind;
						mCodeSpans.add(new PHPCodeSpan(PHPCodeSpan.TYPE_CONSTANT, index, tempIndex,code));
						index=tempIndex;
						index = intoPhpMode(code,index);
					} else
					{
						index ++ ;
					}
					break;
					/*
				case '\'':
					indexFind=CharArrayUtil.findEndOfChar(code,index+1);
					tempIndex=indexFind;
					mCodeSpans.add(new PHPCodeSpan(PHPCodeSpan.TYPE_CONSTANT, index, tempIndex,code));
					index=tempIndex;
					break;
				case '"':
					indexFind=CharArrayUtil.findEndOfString(code,index+1);
					tempIndex=indexFind;
					mCodeSpans.add(new PHPCodeSpan(PHPCodeSpan.TYPE_CONSTANT, index, tempIndex,code));
					index=tempIndex;
					break;*/
				default:
					index ++;
					break;
				}
			}
		}
	}
	
	private int intoPhpMode(char []code,int index)
	{
		char []phpLableEnd = "?>".toCharArray();
		int indexFind = 0;
		int tempIndex = 0;
		for(;index<code.length;){
			switch (code [index]) {
			case '+':
				if(index+1<code.length && code[index+1]=='=')
				{
					tempIndex=index+2;
					mCodeSpans.add(new PHPCodeSpan(PHPCodeSpan.TYPE_NONE, index, tempIndex,code));
					index=tempIndex;
				}
				else
				if(index+1<code.length && code[index+1]=='+')
				{
					tempIndex=index+2;
					mCodeSpans.add(new PHPCodeSpan(PHPCodeSpan.TYPE_NONE, index, tempIndex,code));
					index=tempIndex;
				}
				else
				{
					tempIndex=index+1;
					mCodeSpans.add(new PHPCodeSpan(PHPCodeSpan.TYPE_NONE, index, tempIndex,code));
					index=tempIndex;
				}
				break;
			case '-':
				if(index+1<code.length && code[index+1]=='=')
				{
					tempIndex=index+2;
					mCodeSpans.add(new PHPCodeSpan(PHPCodeSpan.TYPE_NONE, index, tempIndex,code));
					index=tempIndex;
				}
				else
				if(index+1<code.length && code[index+1]=='-')
				{
					tempIndex=index+2;
					mCodeSpans.add(new PHPCodeSpan(PHPCodeSpan.TYPE_NONE, index, tempIndex,code));
					index=tempIndex;
				}
				else
				if(index+1<code.length && code[index+1]=='>')
				{
					tempIndex=index+2;
					mCodeSpans.add(new PHPCodeSpan(PHPCodeSpan.TYPE_NONE, index, tempIndex,code));
					index=tempIndex;
				}
				else
				{
					tempIndex=index+1;
					mCodeSpans.add(new PHPCodeSpan(PHPCodeSpan.TYPE_NONE, index, tempIndex,code));
					index=tempIndex;
				}
				break;
			case '\'':
				indexFind=CharArrayUtil.findEndOfChar(code,index+1);
				tempIndex=indexFind;
				mCodeSpans.add(new PHPCodeSpan(PHPCodeSpan.TYPE_CONSTANT, index, tempIndex,code));
				index=tempIndex;
				break;
			case '"':
				indexFind=CharArrayUtil.findEndOfString(code,index+1);
				tempIndex=indexFind;
				mCodeSpans.add(new PHPCodeSpan(PHPCodeSpan.TYPE_CONSTANT, index, tempIndex,code));
				index=tempIndex;
				break;
			case '*':
				if(index+1<code.length && code[index+1]=='=')
				{
					tempIndex=index+2;
					mCodeSpans.add(new PHPCodeSpan(PHPCodeSpan.TYPE_NONE, index, tempIndex,code));
					index=tempIndex;
				}
				else
				{
					tempIndex=index+1;
					mCodeSpans.add(new PHPCodeSpan(PHPCodeSpan.TYPE_NONE, index, tempIndex,code));
					index=tempIndex;
				}
				break;
			case '/':
				if(index+1<code.length && code[index+1]=='/')
				{//单行注释
					int find=CharArrayUtil.strchr(code,index+2,'\n');
					if(find!=-1){
						tempIndex=find;
						mCodeSpans.add(new PHPCodeSpan(PHPCodeSpan.TYPE_COMMENTS, index, tempIndex,code));
						index=tempIndex;
					}
					else
					{
						tempIndex=code.length;
						mCodeSpans.add(new PHPCodeSpan(PHPCodeSpan.TYPE_NONE, index, tempIndex,code));
						index=tempIndex;
					}
				}
				else
				if(index+1<code.length && code[index+1]=='*')
				{//多行注释
					indexFind=CharArrayUtil.findEndOfMultiComment(code,index+2);
					tempIndex=indexFind;
					mCodeSpans.add(new PHPCodeSpan(PHPCodeSpan.TYPE_COMMENTS, index, tempIndex,code));
					index=tempIndex;
				}
				else
				if(index+1<code.length && code[index+1]=='=')
				{
					tempIndex=index+2;
					mCodeSpans.add(new PHPCodeSpan(PHPCodeSpan.TYPE_NONE, index, tempIndex,code));
					index=tempIndex;
				}
				else
				{
					tempIndex=index+1;
					mCodeSpans.add(new PHPCodeSpan(PHPCodeSpan.TYPE_NONE, index, tempIndex,code));
					index=tempIndex;
				}
				break;
			case '%':
				if(index+1<code.length && code[index+1]=='=')
				{
					tempIndex=index+2;
					mCodeSpans.add(new PHPCodeSpan(PHPCodeSpan.TYPE_NONE, index, tempIndex,code));
					index=tempIndex;
				}
				else
				{
					tempIndex=index+1;
					mCodeSpans.add(new PHPCodeSpan(PHPCodeSpan.TYPE_NONE, index, tempIndex,code));
					index=tempIndex;
				}
				break;
			case '&':
				if(index+1<code.length && code[index+1]=='=')
				{
					tempIndex=index+2;
					mCodeSpans.add(new PHPCodeSpan(PHPCodeSpan.TYPE_NONE, index, tempIndex,code));
					index=tempIndex;
				}
				else
				if(index+1<code.length && code[index+1]=='&')
				{
					tempIndex=index+2;
					mCodeSpans.add(new PHPCodeSpan(PHPCodeSpan.TYPE_NONE, index, tempIndex,code));
					index=tempIndex;
				}
				else
				{
					tempIndex=index+1;
					mCodeSpans.add(new PHPCodeSpan(PHPCodeSpan.TYPE_NONE, index, tempIndex,code));
					index=tempIndex;
				}
				break;
			case '|':
				if(index+1<code.length && code[index+1]=='=')
				{
					tempIndex=index+2;
					mCodeSpans.add(new PHPCodeSpan(PHPCodeSpan.TYPE_NONE, index, tempIndex,code));
					index=tempIndex;
				}
				else
				if(index+1<code.length && code[index+1]=='|')
				{
					tempIndex=index+2;
					mCodeSpans.add(new PHPCodeSpan(PHPCodeSpan.TYPE_NONE, index, tempIndex,code));
					index=tempIndex;
				}
				else
				{
					tempIndex=index+1;
					mCodeSpans.add(new PHPCodeSpan(PHPCodeSpan.TYPE_NONE, index, tempIndex,code));
					index=tempIndex;
				}
				break;
			case '^':
				if(index+1<code.length && code[index+1]=='=')
				{
					tempIndex=index+2;
					mCodeSpans.add(new PHPCodeSpan(PHPCodeSpan.TYPE_NONE, index, tempIndex,code));
					index=tempIndex;
				}
				else
				{
					tempIndex=index+1;
					mCodeSpans.add(new PHPCodeSpan(PHPCodeSpan.TYPE_NONE, index, tempIndex,code));
					index=tempIndex;
				}
				break;
			case '~':
				tempIndex=index+1;
				mCodeSpans.add(new PHPCodeSpan(PHPCodeSpan.TYPE_NONE, index, tempIndex,code));
				index=tempIndex;
				break;
			case '!':
				if(index+1<code.length && code[index+1]=='=')
				{
					tempIndex=index+2;
					mCodeSpans.add(new PHPCodeSpan(PHPCodeSpan.TYPE_NONE, index, tempIndex,code));
					index=tempIndex;
				}
				else
				{
					tempIndex=index+1;
					mCodeSpans.add(new PHPCodeSpan(PHPCodeSpan.TYPE_NONE, index, tempIndex,code));
					index=tempIndex;
				}
				break;
			case '<':
				if(index+1<code.length && code[index+1]=='<')
				{
					if(index+2<code.length && code[index+2]=='=')
					{
						tempIndex=index+3;
						mCodeSpans.add(new PHPCodeSpan(PHPCodeSpan.TYPE_NONE, index, tempIndex,code));
						index=tempIndex;
					}
					else
					{
						tempIndex=index+2;
						mCodeSpans.add(new PHPCodeSpan(PHPCodeSpan.TYPE_NONE, index, tempIndex,code));
						index=tempIndex;
					}
				}
				else
				if(index+1<code.length && code[index+1]=='=')
				{
					tempIndex=index+2;
					mCodeSpans.add(new PHPCodeSpan(PHPCodeSpan.TYPE_NONE, index, tempIndex,code));
					index=tempIndex;
				}
				else
				{
					tempIndex=index+1;
					mCodeSpans.add(new PHPCodeSpan(PHPCodeSpan.TYPE_NONE, index, tempIndex,code));
					index=tempIndex;
				}
				break;
			case '>':
				if(index+1<code.length && code[index+1]=='>')
				{
					if(index+2<code.length && code[index+2]=='=')
					{
						tempIndex=index+3;
						mCodeSpans.add(new PHPCodeSpan(PHPCodeSpan.TYPE_NONE, index, tempIndex,code));
						index=tempIndex;
					}
					else
					{
						tempIndex=index+2;
						mCodeSpans.add(new PHPCodeSpan(PHPCodeSpan.TYPE_NONE, index, tempIndex,code));
						index=tempIndex;
					}
				}
				else
				if(index+1<code.length && code[index+1]=='=')
				{
					tempIndex=index+2;
					mCodeSpans.add(new PHPCodeSpan(PHPCodeSpan.TYPE_NONE, index, tempIndex,code));
					index=tempIndex;
				}
				else
				{
					tempIndex=index+1;
					mCodeSpans.add(new PHPCodeSpan(PHPCodeSpan.TYPE_NONE, index, tempIndex,code));
					index=tempIndex;
				}
				break;
			case '(':
				tempIndex=index+1;
				mCodeSpans.add(new PHPCodeSpan(PHPCodeSpan.TYPE_NONE, index, tempIndex,code));
				index=tempIndex;
				break;
			case ')':
				tempIndex=index+1;
				mCodeSpans.add(new PHPCodeSpan(PHPCodeSpan.TYPE_NONE, index, tempIndex,code));
				index=tempIndex;
				break;
			case '[':
				tempIndex=index+1;
				mCodeSpans.add(new PHPCodeSpan(PHPCodeSpan.TYPE_NONE, index, tempIndex,code));
				index=tempIndex;
				break;
			case ']':
				tempIndex=index+1;
				mCodeSpans.add(new PHPCodeSpan(PHPCodeSpan.TYPE_NONE, index, tempIndex,code));
				index=tempIndex;
				break;
			case '{':
				tempIndex=index+1;
				mCodeSpans.add(new PHPCodeSpan(PHPCodeSpan.TYPE_NONE, index, tempIndex,code));
				index=tempIndex;
				break;
			case '}':
				tempIndex=index+1;
				mCodeSpans.add(new PHPCodeSpan(PHPCodeSpan.TYPE_NONE, index, tempIndex,code));
				index=tempIndex;
				break;
			case '.':
				tempIndex=index+1;
				mCodeSpans.add(new PHPCodeSpan(PHPCodeSpan.TYPE_NONE, index, tempIndex,code));
				index=tempIndex;
				break;
			case ';':
				tempIndex=index+1;
				mCodeSpans.add(new PHPCodeSpan(PHPCodeSpan.TYPE_NONE, index, tempIndex,code));
				index=tempIndex;
				break;
			case ',':
				tempIndex=index+1;
				mCodeSpans.add(new PHPCodeSpan(PHPCodeSpan.TYPE_NONE, index, tempIndex,code));
				index=tempIndex;
				break;
			case '?':
				if( CharArrayUtil.strncmp(code, index,phpLableEnd, 0, phpLableEnd.length)==0 )
				{
					//结束php部分
					indexFind=index+phpLableEnd.length;
					tempIndex=indexFind;
					mCodeSpans.add(new PHPCodeSpan(PHPCodeSpan.TYPE_CONSTANT, index, tempIndex,code));
					index=tempIndex;
					return index;
				} else
				{
					tempIndex=index+1;
					mCodeSpans.add(new PHPCodeSpan(PHPCodeSpan.TYPE_NONE, index, tempIndex,code));
					index=tempIndex;
				}
				break;
			case ':':
				tempIndex=index+1;
				mCodeSpans.add(new PHPCodeSpan(PHPCodeSpan.TYPE_NONE, index, tempIndex,code));
				index=tempIndex;
				break;
			case '\\':
				tempIndex=index+1;
				mCodeSpans.add(new PHPCodeSpan(PHPCodeSpan.TYPE_NONE, index, tempIndex,code));
				index=tempIndex;
				break;
			case '=':
				if(index+1<code.length && code[index+1]=='=')
				{
					tempIndex=index+2;
					mCodeSpans.add(new PHPCodeSpan(PHPCodeSpan.TYPE_NONE, index, tempIndex,code));
					index=tempIndex;
				}
				else
				{
					tempIndex=index+1;
					mCodeSpans.add(new PHPCodeSpan(PHPCodeSpan.TYPE_NONE, index, tempIndex,code));
					index=tempIndex;
				}
				break;
			default:
				if( Character.isLetter(code[index]) || code[index]=='_' )
				{
					indexFind = CharArrayUtil.findEndOfIdentifier(code, index+1);
					
					int keyPos=CharArrayUtil.findProKeyWord(code,index,indexFind-(index),mCodeKeyWords_Char);
					if(keyPos>=0){
						//关键字 
						tempIndex=indexFind;
						mCodeSpans.add(new PHPCodeSpan(PHPCodeSpan.TYPE_KEY_WORDS, index, tempIndex,code));
						index=tempIndex;
					}
					else{
						//普通字 
						tempIndex=indexFind;
						mCodeSpans.add(new PHPCodeSpan(PHPCodeSpan.TYPE_WORDS, index, tempIndex,code));
						index=tempIndex;
					}
				}
				else
				if( Character.isDigit(code[index]) )
				{
					indexFind = CharArrayUtil.findEndOfNumber(code, index+1);
					tempIndex=indexFind;
					mCodeSpans.add(new PHPCodeSpan(PHPCodeSpan.TYPE_CONSTANT, index, tempIndex,code));
					index=tempIndex;
				}
				else
					index++;
				break;
			}
		}
		return index;
	}
	@Override
	public String toString() {
		return mCodeSpans.toString();
	}
}
