package person.wangchen11.ccode;

public class CPPCodeKeyWords {
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

		//c++ :
		"asm",
		"bool",
		"catch",
		"class",
		"const_cast",
		"delete",
		"dynamic_cast",
		"explicit",
		"export",
		"false",
		"friend",
		"inline",
		"mutable",
		"new",
		"namespace",
		"operator",
		"private",
		"protected",
		"public",
		"reinterpret_cast",
		"static_cast",
		"template",
		"this",
		"throw",
		"true",
		"try",
		"typeid",
		"typename",
		"using",
		"virtual",
		"wchar_t",
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
