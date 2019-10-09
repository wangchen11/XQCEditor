package person.wangchen11.editor.edittext;

import android.text.InputFilter;
import android.text.Spanned;

public abstract class CodeInputFilter implements InputFilter {

	@Override
	public CharSequence filter(CharSequence source, int start, int end,
			Spanned dest, int dstart, int dend) {
		return autoIndent(source, start, end, dest, dstart,dend);
	}

	public CharSequence autoIndent(CharSequence source, int start, int end,
									Spanned dest, int dstart, int dend) {
		if(isEnable())
		if( (end-start==1)&&(source.charAt(start)=='\n')){
			return newLine(dest,dstart);
		}
		return source;
	}
	
	public CharSequence newLine(CharSequence editable,int position){
		int numberOfNull=0; //一个'\t' 4个空格 
		int numberOfK=0;
		for(int index=position-1;index>=0;index--){
			//向前查找 '{' 
			char indexch=editable.charAt(index);
			if(indexch=='\n')
				break;
			if(indexch==' '){
				numberOfNull++;
			}else
			if(indexch=='\t'){
				numberOfNull+=4;
			}else
			if(indexch=='{')
			{
				numberOfK+=4;
			}else{
				numberOfNull=0;
			}
		}
		
		for(int index=position;index<editable.length();index++){
			//向后查找 '}' 
			char indexch=editable.charAt(index);
			if(indexch=='\n')
				break;
			if(indexch==' '){
			}else
			if(indexch=='\t'){
			}else
			if(indexch=='}')
			{
				numberOfK-=4;
			}
		}
		if(numberOfK<0)
			numberOfK=0;
		numberOfNull+=numberOfK;
		if(numberOfNull<0)
			numberOfNull=0;
		int end=position;
		for(;end<editable.length()-1;end++){
			char indexch=editable.charAt(end);
			if( !(indexch == '\t' || indexch == ' ') )
				break;
		}
		String str="\n";
		for(;numberOfNull>=4;numberOfNull-=4){
			str+=getTabString();
		}
		for(;numberOfNull>0;numberOfNull--){
			str+=' ';
		}
		
		return str;
	}
	
	public String getTabString(){
		return "\t";
	}
	
	public abstract boolean isEnable();
}
