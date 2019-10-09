package person.wangchen11.codeanalysis.ccode;

public class CCodeSpan{
	public int mType;
	public int mStart;
	public int mEnd;
	public String mContent="";
	public static final int TYPE_NONE=0;
	public static final int TYPE_KEY_WORDS=1;
	public static final int TYPE_PRO_KEY_WORDS=2;
	public static final int TYPE_CONSTANT=3;
	public static final int TYPE_WORDS=4;
	public static final int TYPE_COMMENTS=5;

	public CCodeSpan(int type,int start,int end,char []code) {
		mType=type;
		mStart=start;
		mEnd=end;
		mContent=String.valueOf(code, start, end-start );
		if(type == TYPE_PRO_KEY_WORDS)
		{
			mContent = mContent.replaceAll(" ", "");
			mContent = mContent.replaceAll("\r", "");
			mContent = mContent.replaceAll("\t", "");
		}
	}
	
	public int length(){
		return mEnd-mStart;
	}
	
	@Override
	public String toString() {
		/*
		String str="";
		str+="mType:"+mType;
		str+="\t,mStart:"+mStart;
		str+="\t,mEnd:"+mEnd;
		str+="\t,mContent:\t"+mContent;
		return str;
		*/
		return mContent;
	}
	
	public boolean hasPosition(int pos){
		if(pos>=mStart&&pos<mEnd)
			return true;
		return false;
	}
}
