package person.wangchen11.ccode;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

import person.wangchen11.gnuccompiler.GNUCCompiler;
import android.support.v4.util.LruCache;
import android.util.Log;

public class ArmAsmCodeParser implements Runnable{
	protected static final String TAG="ArmAsmCodeParser";
	private char []mCode=null;
	private int mLength=0;
	private String mPath="";
	private String mIncludePath=GNUCCompiler.getIncludeDir();
	private String mIncludePathEx=GNUCCompiler.getIncludeDirEx();
	private LinkedList<CodeEntity> mEntities=new LinkedList<CodeEntity>();
	private int mWantChangeStart=0;
	private int mWantChangeEnd=0;
	public String []mCodeKeyWords=ArmAsmCodeKeyWords.mKeyWord;
	public char [][]mCodeKeyWords_Char=ArmAsmCodeKeyWords.mKeyWord_Char;
	
	private static LruCache<String, ArmAsmCodeParser> mParserCache=new LruCache<String, ArmAsmCodeParser>(1024*1024)
			{
				protected int sizeOf(String key, ArmAsmCodeParser value) {
					return value.length();
				}
			};
	public ArmAsmCodeParser(String code) {
		mCode=code.toCharArray();
		mLength=code.length();
	}
	
	public ArmAsmCodeParser(String code,String dir,String sysDir) {
		mPath=dir;
		mIncludePath=sysDir;
		mCode=code.toCharArray();
		mLength=code.length();
	}
	
	public int length(){
		return mLength;
	}
	
	@Override
	public void run() {
		try {
			try {
				runEx();
			} catch (Error e) {
			}
		} catch (Exception e) {
		}
		Log.i(TAG, "run");
	}
	public void runEx() {
		if(mCode!=null)
		{
			mEntities.clear();
			char []code=mCode;
			int tempIndex;
			int indexFind;
			int keyPos;
			for(int index=0;index<code.length;){
				switch(code[index]){
				case ' ':
				case '\r':
				case '\n':
					index++;
					break;
				case '\t':
					tempIndex=index+1;;
					mEntities.add(new CodeEntity(CodeEntity.TYPE_COMMENTS, index, tempIndex,144));
					index=tempIndex;
					break;
				case '\'':
					indexFind=findEndOfChar(code,index+1);
					tempIndex=indexFind;
					mEntities.add(new CodeEntity(CodeEntity.TYPE_CONSTANT, index, tempIndex,162));
					index=tempIndex;
					break;
				case '"':
					indexFind=findEndOfString(code,index+1);
					tempIndex=indexFind;
					mEntities.add(new CodeEntity(CodeEntity.TYPE_CONSTANT, index, tempIndex,163));
					index=tempIndex;
					break;
				case '@':
				case ';'://单行注释
					int find=strchr(code,index+1,'\n');
					if(find!=-1){
						tempIndex=find;
						mEntities.add(new CodeEntity(CodeEntity.TYPE_COMMENTS, index, tempIndex,181));
						index=tempIndex;
					}
					else
					{
						tempIndex=code.length;
						mEntities.add(new CodeEntity(CodeEntity.TYPE_COMMENTS, index, tempIndex,181));
						index=tempIndex;
					}
					break;
				default:
					if( Character.isLetter(code[index]) || code[index]=='_' )
					{
						indexFind = findEndOfIdentifier(code, index+1);
						keyPos=findProKeyWord(code,index,indexFind-(index),mCodeKeyWords_Char);
						if(keyPos>=0){
							//关键字 
							tempIndex=indexFind;
							mEntities.add(new CodeEntity(CodeEntity.TYPE_KEY_WORDS, index, tempIndex,300+keyPos));
							index=tempIndex;
						}
						else{
							//普通字 
							tempIndex=indexFind;
							mEntities.add(new CodeEntity(CodeEntity.TYPE_WORDS, index, tempIndex,200));
							index=tempIndex;
						}
					}
					else
					if( Character.isDigit(code[index]) )
					{
						indexFind = findEndOfNumber(code, index+1);
						tempIndex=indexFind;
						mEntities.add(new CodeEntity(CodeEntity.TYPE_CONSTANT, index, tempIndex,161));
						index=tempIndex;
					}
					else
						index++;
					break;
				}
			}
		}
	}
	
	
	public int getWantChangeStart(){
		return mWantChangeStart;
	}
	
	public int getWantChangeEnd(){
		return mWantChangeEnd;
	}
	
	public LinkedList<String> getWant(String str,int includeNumber){
		LinkedList<String> linkedList=new LinkedList<String>();
		if(str==null||str.length()<=0)
			return linkedList;
		return getWantWolds(str,mCode.length,mCode,includeNumber);
	}
	
	
	public LinkedList<String> getWant(int pos,int includeNumber){
		CodeEntity entity = getEntity(pos-1);
		if(entity==null)
			return null;
		if(pos-entity.mStart<=0)
			return null;
		mWantChangeStart=entity.mStart;
		mWantChangeEnd=pos;
		String str=new String(mCode,entity.mStart,pos-entity.mStart);
		LinkedList<String> linkedList=getWantWolds(str,pos,mCode,includeNumber);
		linkedList=(LinkedList<String>) addAllIfNotContains(linkedList,getWantKeyWords(str));
		linkedList=(LinkedList<String>) addAllIfNotContains(linkedList,getWantKeyWordsEx(str));
		return linkedList;
		//getWantKeyWords(str);
		//getWantKeyWordsEx(str);
	}
	
	
	private LinkedList<String> getWantWolds(String str ,int findEnd,char []code,int includeNumber){
		LinkedList<String> codeWants1=new LinkedList<String>();//存开头相同的字 
		LinkedList<String> codeWants3=new LinkedList<String>();//存开头相似的字
		
		LinkedList<String> codeWantsInclude=new LinkedList<String>();//存开头相似的字
		Iterator<CodeEntity> iterator = mEntities.iterator();
		CodeEntity proEntity=null;
		while(iterator.hasNext()){
			CodeEntity codeEntity=iterator.next();
			if(codeEntity.mEnd>=findEnd)
				break;
			if(codeEntity.mTag==200)//普通字 
			{
				String str2=new String(code,codeEntity.mStart,codeEntity.length());
				if(str2.length()>=str.length())
				{
					if(str2.startsWith(str))
					{
						if( !codeWants1.contains(str2) )
							codeWants1.addFirst( str2 );
						if( codeWants1.size()>100 )
							codeWants1.removeLast();
					}
					else
					if(str2.substring(0, str.length()).equalsIgnoreCase(str)){
						if( (!codeWants3.contains(str2)) && (!codeWants1.contains(str2)) )
							codeWants3.addFirst(str2);
						if( codeWants3.size()>100 )
							codeWants3.removeLast();
					}
				}
			}
			if( includeNumber<2 ) //是否继续包含 
			if( proEntity!=null && proEntity.mTag==400+0 ) //#include 
			{
				if( codeEntity.mTag==164 && codeEntity.length()>2)//include <...>
				{
					String path=mIncludePath+File.separatorChar+new String(code,codeEntity.mStart+1,codeEntity.length()-2);
					if(!new File(path).isFile())
						path=mIncludePathEx+File.separatorChar+new String(code,codeEntity.mStart+1,codeEntity.length()-2);
					codeWantsInclude=(LinkedList<String>) addAllIfNotContains(codeWantsInclude,findIncludeFile(path,str,includeNumber));
					Log.i(TAG, "include <>:"+path);
				}else 
				if( codeEntity.mTag==163 )//include "..."
				{
					String path=mPath+File.separatorChar+new String(code,codeEntity.mStart+1,codeEntity.length()-2);
					codeWantsInclude=(LinkedList<String>) addAllIfNotContains(codeWantsInclude,findIncludeFile(path,str,includeNumber));
					Log.i(TAG, "include \"\":"+path);
				}
			}
			
			if(codeEntity.mType!=CodeEntity.TYPE_NONE)
				proEntity=codeEntity;
		}

		codeWants1=(LinkedList<String>) addAllIfNotContains(codeWants1,codeWantsInclude);
		codeWants1=(LinkedList<String>) addAllIfNotContains(codeWants1,codeWants3);
		return codeWants1;
	}
	
	private static <E> List<E> addAllIfNotContains(List<E> list1,List<E>  list2)
	{
		Iterator<E> iterator = list2.iterator();
		while(iterator.hasNext())
		{
			E e=iterator.next();
			if(!list1.contains(e))
			{
				list1.add(e);
			}
		}
		return list1;
	}
	
	public ArmAsmCodeParser createParser(String code)
	{
		return new ArmAsmCodeParser(code);
	}
	
	public LinkedList<String> findIncludeFile(String path,String str,int includeNumber){
		includeNumber++;
		File file=new File(path);
		String key=file.getAbsolutePath()+"?"+file.lastModified();
		ArmAsmCodeParser parser=mParserCache.get(key);
		if(parser==null)
		{
			parser=createParser(getAll(path));
			parser.run();
		}
		return parser.getWant(str, includeNumber);
	}
	
	private LinkedList<String> getWantKeyWords(String str){
		LinkedList<String> codeWants2=new LinkedList<String>();//存开头相同的关键字
		for(int i=0;i<mCodeKeyWords.length;i++)
		{
			if(mCodeKeyWords[i].length()>=str.length())
			{
				if(mCodeKeyWords[i].startsWith(str))
				{
					String str2=mCodeKeyWords[i];
					if(  (!codeWants2.contains(str2))   )
						codeWants2.addFirst(str2);
					if( codeWants2.size()>200 )
						codeWants2.removeLast();
				}
			}
		}
		return codeWants2;
	}
	
	private LinkedList<String> getWantKeyWordsEx(String str){
		LinkedList<String> codeWants4=new LinkedList<String>();//存开头相同的关键字 
		for(int i=0;i<mCodeKeyWords.length;i++)
		{
			if(mCodeKeyWords[i].length()>=str.length())
			{
				if( mCodeKeyWords[i].substring(0, str.length()).equalsIgnoreCase(str) )
				{
					String str2=mCodeKeyWords[i];
					if(  (!codeWants4.contains(str2)) )
						codeWants4.addFirst(str2);
					if( codeWants4.size()>200 )
						codeWants4.removeLast();
				}
			}
		}
		return codeWants4;
	}
	
	public LinkedList<CodeEntity> getEntities(){
		return mEntities;
	}
	
	public LinkedList<CodeEntity> getChanges(ArmAsmCodeParser parser){
		if(parser==null)
			return mEntities;
		LinkedList<CodeEntity> linkedList=new LinkedList<CodeEntity>();
		Iterator<CodeEntity> iterator=mEntities.iterator();
		Iterator<CodeEntity> iteratorOther=parser.mEntities.iterator();
		if(!iterator.hasNext() )
			return linkedList;
		if(!iteratorOther.hasNext())
			return mEntities;
		int indexStart=0;
		while(iterator.hasNext() && iteratorOther.hasNext()){
			if(!iterator.next().equals(iteratorOther.next()))
				break;
			indexStart++;
		}
		int indexEnd=0; 
		int otherEnd=0;
		indexEnd=mEntities.size()-1;
		otherEnd=parser.mEntities.size()-1;
		for( ;indexEnd>=0 && otherEnd>=indexStart && indexEnd>=indexStart;indexEnd--,otherEnd--)
		{
			if( !mEntities.get(indexEnd).equals(parser.mEntities.get(otherEnd)) )
				break;
		}
		iterator=mEntities.listIterator(indexStart);
		for(;iterator.hasNext()&&indexStart<=indexEnd;indexStart++){
			linkedList.addLast(iterator.next());
		}
		return linkedList;
	}
	

	public CodeEntity getEntity(int position){
		Iterator<CodeEntity> iterator=mEntities.iterator();
		while(iterator.hasNext()){
			CodeEntity entity=iterator.next();
			if(entity.hasPosition(position))
				return entity;
		}
		return null;
	}
	
	public boolean isNewLine(char []code,int start)
	{
		for(int i=start-1;i>0;i--){
			if( (code[i]=='\n') )
				return true;
			if( (code[i]!=' ')&&(code[i]!='\t') )
			{
				return false;
			}
		}
		return true;
	}
	
	public int findEndOfChar(char []code,int start){
		int i;
		for(i=start;i<code.length;i++){
			if(code[i]=='\\')
			{
				i++;
				continue;
			}
			if(code[i]=='\'' )
				return i+1;
		}
		return i;
	}
	
	public int findEndOfString(char []code,int start){
		int i;
		for(i=start;i<code.length;i++){
			if(code[i]=='\\')
			{
				i++;
				continue;
			}
			if(code[i]=='"')
				return i+1;
		}
		return i;
	}
	
	public int findEndOfMultiComment(char []code,int start){
		int i;
		for(i=start;i<code.length;i++){
			if(code[i]=='*' && i+1<code.length && code[i+1]=='/')
				return i+2;
		}
		return start;
	}

	public int findEndOfNumber(char []code,int start){
		int i;
		for(i=start;i<code.length;i++){
			if( ! (Character.isLetterOrDigit(code[i]) || code[i]=='_'  || code[i]=='.' ) )
				return i;
		}
		return i;
	}
	
	public int findEndOfIdentifier(char []code,int start){
		int i;
		for(i=start;i<code.length;i++){
			if( ! (Character.isLetterOrDigit(code[i]) || code[i]=='_' ) )
				return i;
		}
		return i;
	}

	public int findNotEmpty(char []code,int start){
		int i;
		for(i=start;i<code.length;i++){
			if( ! (code[i]==' '||code[i]=='\t' ) )
				return i;
		}
		return start;
	}

	public int findEndOfIncludeSystem(char []code,int start){
		int i;
		for(i=start;i<code.length;i++){
			if( (code[i]=='\n'||code[i]=='>' ) )
				return i+1;
		}
		return i;
	}
	
	
	public int findProKeyWord(char []code,int start,int len,char [][]keys){
		for(int i=0;i<keys.length;i++){
			if(len!=keys[i].length)
				continue;
			if(strncmp(code	, start, keys[i], 0, keys[i].length)==0)
				return i;
		}
		return -1;
	}
	
	public int strncmp(char []str1,int start1,char []str2,int start2,int len){
		if(start1+len>str1.length)
			return -1;
		if(start2+len>str2.length)
			return 1;
		for(int i=0;i<len;i++){
			if(str1[start1]!=str2[start2])
				return str1[start1]-str2[start2];
			start1++;
			start2++;
		}
		return 0;
	}
	
	public int strchr(char []str,int start,char ch){
		for(int i=start;i<str.length;i++){
			if(str[i]==ch)
				return i;
		}
		return -1;
	}
	
	public static String getAll(String fileName){
		File file=new File(fileName);
		try {
			FileInputStream fileInputStream=new FileInputStream(file);
			try {
				byte data[]=new byte[(int) file.length()];
				try {
					int readLen=fileInputStream.read(data);
					return new String(data,0,readLen);
				} catch (IOException e) {
					e.printStackTrace();
				}
			} catch (Error e) {
			}
			finally{
					try {
						fileInputStream.close();
					} catch (IOException e) {
						e.printStackTrace();
					}
			}
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}
		return "";
		
	}
}
