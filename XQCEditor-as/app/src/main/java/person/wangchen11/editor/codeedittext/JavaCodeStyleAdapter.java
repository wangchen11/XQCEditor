package person.wangchen11.editor.codeedittext;

import person.wangchen11.ccode.CCodeParser;
import person.wangchen11.ccode.JavaCodeParser;
import android.os.Handler;

public class JavaCodeStyleAdapter extends CCodeStyleAdapter {
	public JavaCodeStyleAdapter(String code, Handler handler,CodeStypeAdapterListener listener) {
		super(code, handler,listener);
	}

	public JavaCodeStyleAdapter(String code, int changePosition, String dir,
			String sysDir, Handler handler,CodeStypeAdapterListener listener) {
		super(code, changePosition, dir, sysDir, handler,listener);
	}
	@Override
	protected CCodeParser createCodeParser(String code) {
		// TODO Auto-generated method stub
		return new JavaCodeParser(code);
	}
	@Override
	protected CCodeParser createCodeParser(String code, String dir,
			String sysDir) {
		return new JavaCodeParser(code, dir, sysDir);
	}
}
