package person.wangchen11.ccode;

public class JavaCodeParser extends CCodeParser{
	
	public JavaCodeParser(String code) {
		super(code);
		reinit();
	}
	
	public JavaCodeParser(String code, String dir, String sysDir) {
		super(code, dir, sysDir);
		reinit();
	}
	
	private void reinit()
	{
		mCodeKeyWords=JavaCodeKeyWords.mKeyWord;
		mCodeKeyWords_Char=JavaCodeKeyWords.mKeyWord_Char;
		mCodeProKeyWords=JavaCodeKeyWords.mProKeyWord;
		mCodeProKeyWords_Char=JavaCodeKeyWords.mProKeyWord_Char;
	}
	
	@Override
	public CCodeParser createParser(String code, String dir, String sysDir) {
		return new CCodeParser(code, dir, sysDir);
	}
}
