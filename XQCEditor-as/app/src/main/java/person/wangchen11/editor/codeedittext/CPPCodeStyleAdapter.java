package person.wangchen11.editor.codeedittext;

import person.wangchen11.ccode.CCodeParser;
import person.wangchen11.ccode.CPPCodeParser;
import android.os.Handler;

public class CPPCodeStyleAdapter extends CCodeStyleAdapter {
	public CPPCodeStyleAdapter(String code, Handler handler,CodeStypeAdapterListener listener) {
		super(code, handler, listener);
	}

	public CPPCodeStyleAdapter(String code, int changePosition, String dir,
			String sysDir, Handler handler,CodeStypeAdapterListener listener) {
		super(code, changePosition, dir, sysDir, handler,listener);
	}
	@Override
	protected CCodeParser createCodeParser(String code) {
		// TODO Auto-generated method stub
		return new CPPCodeParser(code);
	}
	@Override
	protected CCodeParser createCodeParser(String code, String dir,
			String sysDir) {
		return new CPPCodeParser(code, dir, sysDir);
	}
}
