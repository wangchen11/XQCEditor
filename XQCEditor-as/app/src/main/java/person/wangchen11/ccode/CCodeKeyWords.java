package person.wangchen11.ccode;

public class CCodeKeyWords {
	public static String []mKeyWord={
		"auto",
		"short",
		"int",
		"long",
		"float",
		"double",
		"char",
		"struct",
		"union",
		"enum",
		"typedef",
		"const",
		"unsigned",
		"signed",
		"extern",
		"register",
		"static",
		"void",
		"volatile",
		"if",
		"else",
		"switch",
		"for",
		"do",
		"while",
		"goto",
		"continue",
		"break",
		"default",
		"sizeof",
		"return",
		"case",
	};
	
	public static String []mProKeyWord={
		"include",
		"define",
		"undef",
		"if",
		"ifdef",
		"ifndef",
		"else",
		"elif",
		"endif",
		"error",
		"program",
		"pragma",
	};

	public static char [][]mKeyWord_Char=null;
	static {
		mKeyWord_Char=new char[mKeyWord.length][];
		for(int i=0;i<mKeyWord.length;i++)
		{
			mKeyWord_Char[i] = mKeyWord[i].toCharArray();
		}
	};

	public static char [][]mProKeyWord_Char=null;
	static {
		mProKeyWord_Char=new char[mProKeyWord.length][];
		for(int i=0;i<mProKeyWord.length;i++)
		{
			mProKeyWord_Char[i] = mProKeyWord[i].toCharArray();
		}
	};

}
