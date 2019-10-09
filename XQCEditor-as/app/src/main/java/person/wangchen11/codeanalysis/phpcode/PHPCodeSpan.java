package person.wangchen11.codeanalysis.phpcode;

public class PHPCodeSpan {

	public int mType;
	public int mStart;
	public int mEnd;
	public String mContent="";
	public static final int TYPE_NONE=0;
	public static final int TYPE_KEY_WORDS=1;
	public static final int TYPE_CONSTANT=3;
	public static final int TYPE_WORDS=4;
	public static final int TYPE_COMMENTS=5;

	public PHPCodeSpan(int type,int start,int end,char []code) {
		mType=type;
		mStart=start;
		mEnd=end;
		mContent=String.valueOf(code, start, end-start );
	}
	
	public int length(){
		return mEnd-mStart;
	}
	
	@Override
	public String toString() {
		String str="\n";
		str+="mType:"+mType;
		str+="\t,mStart:"+mStart;
		str+="\t,mEnd:"+mEnd;
		str+="\t,mContent:\t"+mContent;
		return str;
	}
	
	public boolean hasPosition(int pos){
		if(pos>=mStart&&pos<mEnd)
			return true;
		return false;
	}
}
