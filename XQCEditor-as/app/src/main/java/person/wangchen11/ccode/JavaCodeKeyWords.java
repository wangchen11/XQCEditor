package person.wangchen11.ccode;

public class JavaCodeKeyWords {
	public static String []mKeyWord={
		"abstract",
		"assert",
		"boolean",
		"break",
		"byte",
		"case",
		"catch",
		"char",
		"class",
		"const",
		"continue",
		"default",
		"do",
		"double",
		"else",
		"enum",
		"extends",
		"final",
		"finally",
		"float",
		"for",
		"if",
		"implements",
		"import",
		"instanceof",
		"int",
		"interface",
		"long",
		"native",
		"new",
		"package",
		"private",
		"protected",
		"public",
		"return",
		"short",
		"static",
		"strictfp",
		"super",
		"switch",
		"synchronized",
		"this",
		"throw",
		"throws",
		"transient",
		"try",
		"void",
		"volatile",
		"while"
	};
	
	public static String []mProKeyWord={
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
