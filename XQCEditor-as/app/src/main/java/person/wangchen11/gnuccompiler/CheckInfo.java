package person.wangchen11.gnuccompiler;

public class CheckInfo {
	public String mFilePath = "";
	public String mMsg = "";
	public int mType = 0;
	public int mLineAt = 0;
	public int mCharAt = 0;
	public CheckInfo(String filePath,String msg,int type,int lineAt,int charAt) {
		mFilePath = filePath;
		mMsg = msg;
		mType = type;
		mLineAt = lineAt;
		mCharAt = charAt;
	}
	public static final int TYPE_INFO = 0;
	public static final int TYPE_WARN = 1;
	public static final int TYPE_ERROR = 2;
	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		builder.append("mFilePath:"+mFilePath);
		builder.append("\n");
		builder.append("mMsg:"+mMsg);
		builder.append("\n");
		builder.append("mType:"+mType+",mLineAt:"+mLineAt+",mCharAt:"+mCharAt);
		builder.append("\n");
		return builder.toString();
	}
	
}
