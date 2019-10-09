package person.wangchen11.ccode;

public class CodeEntity{
	public int mType;
	public int mStart;
	public int mEnd;
	public int mTag=0;
	public static final int TYPE_NONE=0;
	public static final int TYPE_KEY_WORDS=1;
	public static final int TYPE_PRO_KEY_WORDS=2;
	public static final int TYPE_CONSTANT=3;
	public static final int TYPE_WORDS=4;
	public static final int TYPE_COMMENTS=5;
	public CodeEntity(int type,int start,int end) {
		mType=type;
		mStart=start;
		mEnd=end;
	}

	public CodeEntity(int type,int start,int end,int tag) {
		mType=type;
		mStart=start;
		mEnd=end;
		mTag=tag;
	}
	
	public int length(){
		return mEnd-mStart;
	}
	
	@Override
	public String toString() {
		String str="@CCodeEntity:[";
		str+="mType:"+mType;
		str+=",mStart:"+mStart;
		str+=",mEnd:"+mEnd;
		str+="]";
		return str;
	}
	
	public boolean hasPosition(int pos){
		if(pos>=mStart&&pos<mEnd)
			return true;
		return false;
	}
	
	@Override
	public boolean equals(Object o) {
		CodeEntity entity=(CodeEntity)o;
		if(this.mType==entity.mType && this.mTag==entity.mTag && (this.mStart-entity.mStart == this.mEnd-entity.mEnd) )
			return true;
		return false;
	}
	
	public boolean startsWithIgnoreCase(String str,char[] data){
		int otherLen=str.length();
		if(this.length()>otherLen)
			return false;
		for(int i=0;i<otherLen;i++){
			if( Character.toLowerCase(data[mStart+i]) == Character.toLowerCase(str.charAt(i)) )
				continue;
			return false;
		}
		return true;
	}
}
