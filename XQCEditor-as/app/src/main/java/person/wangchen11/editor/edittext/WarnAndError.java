package person.wangchen11.editor.edittext;

import android.graphics.Color;
import person.wangchen11.waps.Waps;
import person.wangchen11.xqceditor.R;

public class WarnAndError {
	public int mLevel;
	public int mColor;
	public int mLine;
	public String mMsg;
	public boolean mDrawUnderLine;
	public boolean mFullLine;
	public int mIndex;
	public int mLength;
	public static final int LEVEL_INFO  = 0;
	public static final int LEVEL_WARN  = 1;
	public static final int LEVEL_ERROR = 2;
	public static final int COLOR_INFO  = Color.BLUE;
	public static final int COLOR_WARN  = Color.rgb(0xf0, 0xb0, 0x20);
	public static final int COLOR_ERROR = Color.RED;
	public WarnAndError(int line,int level){
		this(line,level,"");
	}
	public WarnAndError(int line,int level,String msg){
		mLine  = line;
		mLevel = level;
		switch (mLevel) {
		case LEVEL_INFO:
			mColor = COLOR_INFO;
			break;
		case LEVEL_WARN:
			mColor = COLOR_WARN;
			break;
		case LEVEL_ERROR:
			mColor = COLOR_ERROR;
			break;

		default:
			break;
		}
		mMsg   = msg;
		mDrawUnderLine = true;
		mFullLine = true;
		
	}
	
	public int getTitle(){
		switch (mLevel) {
		case LEVEL_INFO:
			return R.string.tip;
		case LEVEL_WARN:
			return R.string.warn;
		case LEVEL_ERROR:
			return R.string.error;
		default:
			return R.string.tip;
		}
	}
	
	public boolean include(int line,int offset){
		if(mLine==line){
			if(mFullLine==true)
				return true;
			// TODO 
		}
		return false;
	}

	public static final String mReplaceStr[][] = {
		{"unused variable","没用到的变量"},
		{"too many arguments for format","format对应的参数过多"},
		{"expects a matching","期望匹配到"},
		{"first use in this function","第一次在这个函数里使用"},
		{"with no value","需要一个值"},
		{"in function returning non-void","这个函数的返回类型不是void"},
		{"data definition has no type or storage class","没有指明类型的数据定义"},
		{"two or more data types in declaration specifiers","两个或两个以上的数据类型的声明"},
		{"redeclared as different kind of symbol","重新声明为不同的符号"},
		{"that defines no instances","并且没有定义实例"},
		{"empty character constant","空字符常数"},
		{"multi-character character constant","多字字符常数"},
		{"conflicting types for","冲突的类型:"},
		{"initialization makes integer from pointer without a cast","使用指针初始化整型数据却没有强转"},
		{"pointer value used where a floating point value was expected",""},
		{"cast from pointer to integer of different size","指针强转时类型大小不一致"},
		{"return type defaults to","没指定返回类型，默认为:"},
		{"control reaches end of non-void function","控制达到非void函数结束"},
		{"implicit declaration of function","隐式声明的函数:"},
		{"too few arguments to function","缺少参数去调用:"},
		{"too many arguments to function","太多的参数去调用:"},
		{"array size missing in","缺少数组大小:"},
		{"lvalue required as left operand of assignment","左值无法被赋值"},
		{"No such file or directory","没有这样的文件或目录"},
		{"in program","在程序里"},
		{"request for member","试图访问成员"},
		{"in something not a structure or union","在一个非结构体或联合体类型中"},
		{"with no expression","后面没有表达式"},
		{"stray ","未知的符号(可能是中文字符):"},
		{"undeclared","未声明"},
		{"expected ","缺少"},
		{"expects ","期望的"},
		{"before ","在这之前:"},
		{"format ","格式"},
		{"arguments","参数"},
		{"argument","参数"},
		{"unnamed ","未命名的"},
		{"expression ","表达式"},
		{"of type ","类型是"},
		{"has type ","的类型是"},
		{"but ","但是 "},
		{"token",""},
	};
	
	public static String translateMsg(String msg){
		if(!Waps.isGoogle())
		for(int i=0;i<mReplaceStr.length;i++){
			msg = msg.replaceAll(mReplaceStr[i][0], mReplaceStr[i][1]);
		}
		return msg;
	}
}
