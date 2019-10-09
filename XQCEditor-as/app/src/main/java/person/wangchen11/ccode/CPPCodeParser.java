package person.wangchen11.ccode;

public class CPPCodeParser extends CCodeParser{
	
	public CPPCodeParser(String code) {
		super(code);
		reinit();
	}
	
	public CPPCodeParser(String code, String dir, String sysDir) {
		super(code, dir, sysDir);
		reinit();
	}
	
	private void reinit()
	{
		mCodeKeyWords=CPPCodeKeyWords.mKeyWord;
		mCodeKeyWords_Char=CPPCodeKeyWords.mKeyWord_Char;
		mCodeProKeyWords=CPPCodeKeyWords.mProKeyWord;
		mCodeProKeyWords_Char=CPPCodeKeyWords.mProKeyWord_Char;
	}
	
	@Override
	public CCodeParser createParser(String code, String dir, String sysDir) {
		return new CCodeParser(code, dir, sysDir);
	}
}
