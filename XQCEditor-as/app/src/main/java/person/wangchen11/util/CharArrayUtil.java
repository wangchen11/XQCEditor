package person.wangchen11.util;

public class CharArrayUtil {

	public static int findEndOfMultiComment(char []code,int start){
		int i;
		for(i=start;i<code.length;i++){
			if(code[i]=='*' && i+1<code.length && code[i+1]=='/')
				return i+2;
		}
		return start;
	}

	public static int findEndOfNumber(char []code,int start){
		int i;
		for(i=start;i<code.length;i++){
			if( ! (Character.isLetterOrDigit(code[i]) || code[i]=='_'  || code[i]=='.' ) )
				return i;
		}
		return i;
	}
	
	public static int findEndOfIdentifier(char []code,int start){
		int i;
		for(i=start;i<code.length;i++){
			if( ! (Character.isLetterOrDigit(code[i]) || code[i]=='_' ) )
				return i;
		}
		return i;
	}

	public static int findNotEmpty(char []code,int start){
		int i;
		for(i=start;i<code.length;i++){
			if( ! (code[i]==' '||code[i]=='\t' ) )
				return i;
		}
		return start;
	}

	public static int findEndOfIncludeSystem(char []code,int start){
		int i;
		for(i=start;i<code.length;i++){
			if( (code[i]=='\n'||code[i]=='>' ) )
				return i+1;
		}
		return i;
	}
	
	
	public static int findProKeyWord(char []code,int start,int len,char [][]keys){
		for(int i=0;i<keys.length;i++){
			if(len!=keys[i].length)
				continue;
			if(CharArrayUtil.strncmp(code	, start, keys[i], 0, keys[i].length)==0)
				return i;
		}
		return -1;
	}

	public static int findEndOfChar(char []code,int start){
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
	
	public static int findEndOfString(char []code,int start){
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
	
	public static int strncmp(char []str1,int start1,char []str2,int start2,int len){
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
	
	public static int strchr(char []str,int start,char ch){
		for(int i=start;i<str.length;i++){
			if(str[i]==ch)
				return i;
		}
		return -1;
	}
	
}
