package person.wangchen11.ccode;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

import person.wangchen11.gnuccompiler.GNUCCompiler;
import android.annotation.SuppressLint;
import android.support.v4.util.LruCache;
import android.util.Log;

public class CCodeParser implements Runnable{
	protected static final String TAG="CCodeParser";
	private char []mCode=null;
	private int mLength=0;
	private String mPath="";
	private String mIncludePath=GNUCCompiler.getIncludeDir();
	private String mIncludePathEx=GNUCCompiler.getIncludeDirEx();
	private LinkedList<CodeEntity> mEntities=new LinkedList<CodeEntity>();
	private int mWantChangeStart=0;
	private int mWantChangeEnd=0;
	public String []mCodeKeyWords=CCodeKeyWords.mKeyWord;
	public char [][]mCodeKeyWords_Char=CCodeKeyWords.mKeyWord_Char;
	public String []mCodeProKeyWords=CCodeKeyWords.mProKeyWord;
	public char [][]mCodeProKeyWords_Char=CCodeKeyWords.mProKeyWord_Char;
	
	private static LruCache<String, CCodeParser> mParserCache=new LruCache<String, CCodeParser>(1024*1024)
			{
				protected int sizeOf(String key, CCodeParser value) {
					return value.length();
				}
			};
	public CCodeParser(String code) {
		mCode=code.toCharArray();
		mLength=code.length();
	}
	
	public CCodeParser(String code,String dir,String sysDir) {
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
	}
	public void runEx() {
		if(mCode!=null)
		{
			mEntities.clear();
			char []code=mCode;
			@SuppressWarnings("unused")
			int proChangeIndex=0;
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
					/*
					if(proChangeIndex<code.length)
						mEntities.add(new CodeEntity(CodeEntity.TYPE_NONE, proChangeIndex, index,183));
					*/
					tempIndex=index+1;;
					mEntities.add(new CodeEntity(CodeEntity.TYPE_COMMENTS, index, tempIndex,144));
					index=tempIndex;
					proChangeIndex=index;
					break;
				case '\'':
					indexFind=findEndOfChar(code,index+1);
					/*
					if(proChangeIndex<code.length)
						mEntities.add(new CodeEntity(CodeEntity.TYPE_NONE, proChangeIndex, index,183));
					*/
					tempIndex=indexFind;
					mEntities.add(new CodeEntity(CodeEntity.TYPE_CONSTANT, index, tempIndex,162));
					index=tempIndex;
					proChangeIndex=index;
					break;
				case '"':
					indexFind=findEndOfString(code,index+1);
					/*
					if(proChangeIndex<code.length)
						mEntities.add(new CodeEntity(CodeEntity.TYPE_NONE, proChangeIndex, index,183));
					*/
					tempIndex=indexFind;
					mEntities.add(new CodeEntity(CodeEntity.TYPE_CONSTANT, index, tempIndex,163));
					index=tempIndex;
					proChangeIndex=index;
					break;
				case '/':
					if(index+1<code.length && code[index+1]=='/')
					{//单行注释
						int find=strchr(code,index+2,'\n');
						if(find!=-1){
							/*
							if(proChangeIndex<index)
								mEntities.add(new CodeEntity(CodeEntity.TYPE_NONE, proChangeIndex, index,183));
							*/
							tempIndex=find;
							mEntities.add(new CodeEntity(CodeEntity.TYPE_COMMENTS, index, tempIndex,181));
							index=tempIndex;
							proChangeIndex=index;
						}
						else
						{
							/*
							if(proChangeIndex<index)
								mEntities.add(new CodeEntity(CodeEntity.TYPE_NONE, proChangeIndex, index,183));
							*/
							tempIndex=code.length;
							mEntities.add(new CodeEntity(CodeEntity.TYPE_COMMENTS, index, tempIndex,181));
							index=tempIndex;
							proChangeIndex=index;
						}
					}
					else
					if(index+1<code.length && code[index+1]=='*')
					{//多行注释
						indexFind=findEndOfMultiComment(code,index+2);
						/*
						if(proChangeIndex<code.length)
							mEntities.add(new CodeEntity(CodeEntity.TYPE_NONE, proChangeIndex, index,183));
						*/
						tempIndex=indexFind;
						mEntities.add(new CodeEntity(CodeEntity.TYPE_COMMENTS, index, tempIndex,182));
						index=tempIndex;
						proChangeIndex=index;
					}
					else
					{
						index++;
					}
					break;
				case '#':
					tempIndex=findNotEmpty(code,index+1);
					indexFind = findEndOfIdentifier(code, tempIndex);
					keyPos=findProKeyWord(code,tempIndex,indexFind-(tempIndex),mCodeProKeyWords_Char);
					//Log.i(TAG, "keyPos:"+keyPos+" len:"+(indexFind-(index+1)));
					if(keyPos>=0){
						/*
						if(proChangeIndex<code.length)
							mEntities.add(new CodeEntity(CodeEntity.TYPE_NONE, proChangeIndex, index,183));
						*/
						tempIndex=indexFind;
						mEntities.add(new CodeEntity(CodeEntity.TYPE_PRO_KEY_WORDS, index, tempIndex,400+keyPos));
						index=tempIndex;
						proChangeIndex=index;
						
						if(keyPos == 0) //include
						{
							index=findNotEmpty(code,index);
							if(code[index]=='<')
							{
								indexFind=findEndOfIncludeSystem(code,index);
								/*
								if(proChangeIndex<code.length)
									mEntities.add(new CodeEntity(CodeEntity.TYPE_NONE, proChangeIndex, index,183));
								*/
								tempIndex=indexFind;
								mEntities.add(new CodeEntity(CodeEntity.TYPE_CONSTANT, index, tempIndex,164));
								index=tempIndex;
								proChangeIndex=index;
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
						indexFind = findEndOfIdentifier(code, index+1);
						keyPos=findProKeyWord(code,index,indexFind-(index),mCodeKeyWords_Char);
						if(keyPos>=0){
							//关键字 
							/*
							if(proChangeIndex<code.length)
								mEntities.add(new CodeEntity(CodeEntity.TYPE_NONE, proChangeIndex, index,183));
							*/
							tempIndex=indexFind;
							mEntities.add(new CodeEntity(CodeEntity.TYPE_KEY_WORDS, index, tempIndex,300+keyPos));
							index=tempIndex;
							proChangeIndex=index;
						}
						else{
							//普通字 
							/*
							if(proChangeIndex<code.length)
								mEntities.add(new CodeEntity(CodeEntity.TYPE_NONE, proChangeIndex, index,183));
							*/
							tempIndex=indexFind;
							mEntities.add(new CodeEntity(CodeEntity.TYPE_WORDS, index, tempIndex,200));
							index=tempIndex;
							proChangeIndex=index;
						}
					}
					else
					if( Character.isDigit(code[index]) )
					{
						indexFind = findEndOfNumber(code, index+1);
						/*
						if(proChangeIndex<code.length)
							mEntities.add(new CodeEntity(CodeEntity.TYPE_NONE, proChangeIndex, index,183));
						*/
						tempIndex=indexFind;
						mEntities.add(new CodeEntity(CodeEntity.TYPE_CONSTANT, index, tempIndex,161));
						index=tempIndex;
						proChangeIndex=index;
					}
					else
						index++;
					break;
				}
			}
			/*
			if(proChangeIndex<code.length)
				mEntities.add(new CodeEntity(CodeEntity.TYPE_NONE, proChangeIndex, code.length,183));
			*/
		}
	}
	
	
	public int getWantChangeStart(){
		return mWantChangeStart;
	}
	
	public int getWantChangeEnd(){
		return mWantChangeEnd;
	}
	
	public LinkedList<WantMsg> getWant(String str,int includeNumber){
		LinkedList<WantMsg> linkedList=new LinkedList<WantMsg>();
		if(str==null||str.length()<=0)
			return linkedList;
		return getWantWolds(str,mCode.length,mCode,includeNumber);
	}

	@SuppressLint("DefaultLocale")
	private LinkedList<WantMsg> getIncludeWant(String str)
	{
		LinkedList<WantMsg> linkedList=new LinkedList<WantMsg>();
		if(str==null)
			return linkedList;
		int last_ = str.lastIndexOf('/');
		String pro = null;
		String end = null;

		if(last_==-1)
		{
			pro="";
			end=str;
		}
		else
		{
			pro = str.substring(0, last_+1);
			end = str.substring(last_+1);
		}

		File incFile = new File(mPath+"/"+pro);

		File[] incFiles = incFile.listFiles();
		if( incFiles!=null && incFiles.length>0 ) 
		{
			for( File tf : incFiles )
			{
				String name = tf.getName();
				//Log.i(TAG, "name:"+name.toLowerCase());
				//Log.i(TAG, "end:"+end.toLowerCase());
				if(name.toLowerCase().startsWith(end.toLowerCase()))
				{
					if(tf.isDirectory())
						linkedList.add(new WantMsg(pro+name+'/'));
					else
						linkedList.add(new WantMsg(pro+name));
				}
			}
		}
		return linkedList;
	}
	
	@SuppressLint("DefaultLocale")
	private LinkedList<WantMsg> getIncludeSystemWant(String str)
	{
		LinkedList<WantMsg> linkedList=new LinkedList<WantMsg>();
		if(str==null)
			return linkedList;
		int last_ = str.lastIndexOf('/');
		String pro = null;
		String end = null;

		if(last_==-1)
		{
			pro="";
			end=str;
		}
		else
		{
			pro = str.substring(0, last_+1);
			end = str.substring(last_+1);
		}

		File sysIncFile = new File(mIncludePath+"/"+pro);
		File sysIncFileEx = new File(mIncludePathEx+"/"+pro);

		File[] sysIncFiles = sysIncFile.listFiles();
		if( sysIncFiles!=null && sysIncFiles.length>0 ) 
		{
			for( File tf : sysIncFiles )
			{
				String name = tf.getName();
				if(name.toLowerCase().startsWith(end.toLowerCase()))
				{
					String msg;
					if(tf.isDirectory())
						msg = pro+name+'/';
					else
						msg = pro+name;
					WantMsg wantMsg = new WantMsg(msg);
					if(!linkedList.contains(wantMsg)){
						linkedList.add(wantMsg);
					}
				}
			}
		}
		File[] sysIncFilesEx = sysIncFileEx.listFiles();
		if( sysIncFilesEx!=null && sysIncFilesEx.length>0 ) 
		{
			for( File tf : sysIncFilesEx )
			{
				String name = tf.getName();
				if(name.toLowerCase().startsWith(end.toLowerCase()))
				{
					String msg;
					if(tf.isDirectory())
						msg = pro+name+'/';
					else
						msg = pro+name;
					WantMsg wantMsg = new WantMsg(msg);
					if(!linkedList.contains(wantMsg)){
						linkedList.add(wantMsg);
					}
				}
			}
		}
		return linkedList;
	}
	
	public LinkedList<WantMsg> getWant(int pos,int includeNumber){
		CodeEntity entity = getEntity(pos-1);
		if(entity==null)
			return null;
		if(pos-entity.mStart<=0)
			return null;
		mWantChangeStart=entity.mStart;
		mWantChangeEnd=pos;
		String str=new String(mCode,entity.mStart,pos-entity.mStart);
		if(entity.mTag == 163 )//""
		{
			int index=mEntities.indexOf(entity);
			CodeEntity preEntity = null;
			if(index-1>=0)
			{
				preEntity = mEntities.get(index-1);
			}
			if(preEntity!=null&&preEntity.mTag==400+0)//#include 
			if(str.length()>=1)
			{
				mWantChangeStart++;
				if(str.length()>=2&&str.endsWith("\""))
				{
					mWantChangeEnd--;
					return getIncludeWant(str.substring(1,str.length()-1));
				}
				else
				{
					return getIncludeWant(str.substring(1));
				}
			}
		}
		if(entity.mTag == 164 )//<>
		{
			if(str.length()>=1)
			{
				mWantChangeStart++;
				if(str.length()>=2&&str.endsWith(">"))
				{
					mWantChangeEnd--;
					return getIncludeSystemWant(str.substring(1,str.length()-1));
				}
				else
				{
					return getIncludeSystemWant(str.substring(1));
				}
			}
		}
		
		LinkedList<WantMsg> linkedList=getWantWolds(str,pos,mCode,includeNumber);
		linkedList=(LinkedList<WantMsg>) addAllIfNotContains(linkedList,getWantKeyWords(str));
		linkedList=(LinkedList<WantMsg>) addAllIfNotContains(linkedList,getWantKeyWordsEx(str));
		return linkedList;
		//getWantKeyWords(str);
		//getWantKeyWordsEx(str);
	}
	
	
	private LinkedList<WantMsg> getWantWolds(String str ,int findEnd,char []code,int includeNumber){
		LinkedList<WantMsg> codeWants1=new LinkedList<WantMsg>();//存开头相同的字 
		LinkedList<WantMsg> codeWants3=new LinkedList<WantMsg>();//存开头相似的字
		
		LinkedList<WantMsg> codeWantsInclude=new LinkedList<WantMsg>();//
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
						String curLine = getLineByPos((codeEntity.mStart+codeEntity.mEnd)/2);
						WantMsg wantMsg = new WantMsg(str2,curLine);
						if( !codeWants1.contains(wantMsg) ){
							codeWants1.addFirst( wantMsg );
						}
						if( codeWants1.size()>100 )
							codeWants1.removeLast();
					}
					else
					if(str2.substring(0, str.length()).equalsIgnoreCase(str)){
						String curLine = getLineByPos((codeEntity.mStart+codeEntity.mEnd)/2);
						WantMsg wantMsg = new WantMsg(str2,curLine);
						if( (!codeWants3.contains(wantMsg)) && (!codeWants1.contains(wantMsg)) ){
							codeWants3.addFirst(wantMsg);
						}
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
					codeWantsInclude=(LinkedList<WantMsg>) addAllIfNotContains(codeWantsInclude,findIncludeFile(path,str,includeNumber));
					//Log.i(TAG, "include <>:"+path);
				}else 
				if( codeEntity.mTag==163 )//include "..."
				{
					String path=mPath+File.separatorChar+new String(code,codeEntity.mStart+1,codeEntity.length()-2);
					codeWantsInclude=(LinkedList<WantMsg>) addAllIfNotContains(codeWantsInclude,findIncludeFile(path,str,includeNumber));
					//Log.i(TAG, "include \"\":"+path);
				}
			}
			
			if(codeEntity.mType!=CodeEntity.TYPE_NONE)
				proEntity=codeEntity;
		}

		codeWants1=(LinkedList<WantMsg>) addAllIfNotContains(codeWants1,codeWantsInclude);
		codeWants1=(LinkedList<WantMsg>) addAllIfNotContains(codeWants1,codeWants3);
		return codeWants1;
	}
	
	private static <E> List<E> addAllIfNotContains(List<E> list1,List<E>  list2)
	{
		Iterator<E> iterator = list2.iterator();
		while(iterator.hasNext())
		{
			E e=iterator.next();
			if(list1.contains(e))
			{
				list1.remove(e);
				list1.add(0,e);
			}
			else
				list1.add(e);
		}
		return list1;
	}
	
	public CCodeParser createParser(String code,String dir,String sysDir)
	{
		return new CCodeParser(code,dir,sysDir);
	}
	
	public LinkedList<WantMsg> findIncludeFile(String path,String str,int includeNumber){
		includeNumber++;
		File file=new File(path);
		if(!file.isFile())
			return new LinkedList<WantMsg>();
		String key=file.getAbsolutePath()+"?"+file.lastModified();
		CCodeParser parser=mParserCache.get(key);
		if(parser==null)
		{
			parser=createParser(getAll(path),file.getParent(),mIncludePath);
			parser.run();
		}
		return parser.getWant(str, includeNumber);
	}
	
	private LinkedList<WantMsg> getWantKeyWords(String str){
		LinkedList<WantMsg> codeWants2=new LinkedList<WantMsg>();//存开头相同的关键字
		for(int i=0;i<mCodeKeyWords.length;i++)
		{
			if(mCodeKeyWords[i].length()>=str.length())
			{
				if(mCodeKeyWords[i].startsWith(str))
				{
					String str2=mCodeKeyWords[i];
					WantMsg wantMsg = new WantMsg(str2);
					if(  (!codeWants2.contains(wantMsg))   )
						codeWants2.addFirst(wantMsg);
					if( codeWants2.size()>200 )
						codeWants2.removeLast();
				}
			}
		}
		for(int i=0;i<mCodeProKeyWords.length;i++)
		{
			if(mCodeProKeyWords[i].length()>=str.length())
			{
				if(mCodeProKeyWords[i].startsWith(str))
				{
					String str2=mCodeProKeyWords[i];
					WantMsg wantMsg = new WantMsg(str2);
					if(  (!codeWants2.contains(wantMsg))   )
						codeWants2.addFirst(wantMsg);
					if( codeWants2.size()>200 )
						codeWants2.removeLast();
				}
			}
		}
		return codeWants2;
	}
	
	private LinkedList<WantMsg> getWantKeyWordsEx(String str){
		LinkedList<WantMsg> codeWants4=new LinkedList<WantMsg>();//存开头相同的关键字 
		for(int i=0;i<mCodeKeyWords.length;i++)
		{
			if(mCodeKeyWords[i].length()>=str.length())
			{
				if( mCodeKeyWords[i].substring(0, str.length()).equalsIgnoreCase(str) )
				{
					String str2=mCodeKeyWords[i];
					WantMsg wantMsg = new WantMsg(str2);
					if(  (!codeWants4.contains(wantMsg)) )
						codeWants4.addFirst(wantMsg);
					if( codeWants4.size()>200 )
						codeWants4.removeLast();
				}
			}
		}
		for(int i=0;i<mCodeProKeyWords.length;i++)
		{
			if(mCodeProKeyWords[i].length()>=str.length())
			{
				if( mCodeProKeyWords[i].substring(0, str.length()).equalsIgnoreCase(str) )
				{
					String str2=mCodeProKeyWords[i];
					WantMsg wantMsg = new WantMsg(str2);
					if(  (!codeWants4.contains(wantMsg)) )
						codeWants4.addFirst(wantMsg);
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
	
	public LinkedList<CodeEntity> getChanges(CCodeParser parser){
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
	
	
	public int findEndOfChar(char []code,int start){
		int i;
		for(i=start;i<code.length;i++){
			if(code[i]=='\\')
			{
				if(i<code.length-1)
					i++;
				continue;
			}
			if(code[i]=='\'' || code[i]=='\n')
				return i+1;
		}
		return i;
	}
	
	public int findEndOfString(char []code,int start){
		int i;
		for(i=start;i<code.length;i++){
			if(code[i]=='\\')
			{
				if(i<code.length-1)
					i++;
				continue;
			}
			if(code[i]=='"' || code[i]=='\n')
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
	
	public String getLineByPos(int pos){
		int start = 0;
		int end = mCode.length;
		for(int i=0;i<mCode.length;i++){
			if(mCode[i]=='\n'){
				if(pos>i){
					start = i+1;
				}else{
					end = i;
					break;
				}
			}
		}
		//Log.i(TAG, "getLineByPos:NULL,start:"+start+" end:"+end+" pos:"+pos);
		if(start>pos||end<pos)
			return "";
		//Log.i(TAG, "getLineByPos:"+new String(mCode,start,end-start));
		return new String(mCode,start,end-start) ;
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
	/*
	public Object findDefine(int pos,int includeNumber)
	{
		CodeEntity entity = getEntity(pos-1);
		if(entity==null)
			return null;
		if(pos-entity.mStart<=0)
			return null;
		Log.i(TAG, entity.toString()+":"+new String(mCode,entity.mStart,entity.mEnd));
		return null;
	}*/
}

/*
+=	1
-=	2
*=	3
/=	4
%=	5
&=	6
|=	7
^=	8
<<=	9
>>=	10

+	21
-	22
*	23
/	24
%	25

&	41
|	42
~	43
^	44
<<	42
>>	43

&&	61
||	62
!	63

==	81	
!=	82
>=	83
<=	84
>	85
<	86

(	101
)	102
[	103
]	104
{	105
}	106
.	107
->	108
;	109
,	110
?	111
:	112
\	113
=	114

空格 141
\t	142
\r	143
\n	144

数字	161
''	162
""	163
<>  164

单行注释		181
多行注释		182 
未知			183

词			200
关键字		300+i
预处理关键字	400+i
*/

