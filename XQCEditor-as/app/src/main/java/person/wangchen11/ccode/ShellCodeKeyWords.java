package person.wangchen11.ccode;

public class ShellCodeKeyWords {
	public static String []mKeyWord={
		"if",
		"then",
		"elif",
		"else",
		"fi",
		"for",
		"do",
		"done",
		"while",
		"do",
		"break",
		"continue",
		"function",
		"return",
		
		"man",
		"ls",
		"touch",
		"cp",
		"mv",
		"rm",
		"cd",
		"mkdir",
		"rmdir",
		"file",
		"cat",
		"more",
		"head",
		"sort",
		"uniq",
		"pr",
		"ln",
		"wc",
		"which",
		"du",
		"find",
		"grep",
		"tar",
		"cal",
		"bc",
		"date",
		"df",
		"ping",
		"ifconfig",
		"passwd",
		"su",
		"umask",
		"chgrp",
		"chmod",
		"chown",
		"chattr",
		"sudo",
		"ps",
		"who",
		"echo",
		"export",
	};
	
	public static char [][]mKeyWord_Char=null;
	static {
		mKeyWord_Char=new char[mKeyWord.length][];
		for(int i=0;i<mKeyWord.length;i++)
		{
			mKeyWord_Char[i] = mKeyWord[i].toCharArray();
		}
	};

}
