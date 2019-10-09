package person.wangchen11.codeanalysis.ccode;

public abstract class CCodeKeywordsAdapter {
	public abstract String []getCCodeKeywords();
	public abstract String []getCCodeProKeywords();
	
	public char [][]getCCodeKeywords_char(){
		String []keyWord = getCCodeKeywords();
		char [][]keyWord_Char=new char[keyWord.length][];
		for(int i=0;i<keyWord.length;i++)
		{
			keyWord_Char[i] = keyWord[i].toCharArray();
		}
		return keyWord_Char;
	}
	
	public char [][]getCCodeProKeywords_char(){
		String []proKeyWord = getCCodeProKeywords();
		char [][]proKeyWord_Char=new char[proKeyWord.length][];
		for(int i=0;i<proKeyWord.length;i++)
		{
			proKeyWord_Char[i] = proKeyWord[i].toCharArray();
		}
		return proKeyWord_Char;
	}
}
