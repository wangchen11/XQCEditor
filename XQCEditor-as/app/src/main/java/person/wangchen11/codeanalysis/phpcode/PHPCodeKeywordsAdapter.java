package person.wangchen11.codeanalysis.phpcode;

public abstract class PHPCodeKeywordsAdapter {
	public abstract String []getPHPCodeKeywords();
	
	public char [][]getPHPCodeKeywords_char(){
		String []keyWord = getPHPCodeKeywords();
		char [][]keyWord_Char=new char[keyWord.length][];
		for(int i=0;i<keyWord.length;i++)
		{
			keyWord_Char[i] = keyWord[i].toCharArray();
		}
		return keyWord_Char;
	}
	
}
