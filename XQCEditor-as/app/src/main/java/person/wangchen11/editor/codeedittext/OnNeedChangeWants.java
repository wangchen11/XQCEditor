package person.wangchen11.editor.codeedittext;

import java.util.List;

import person.wangchen11.ccode.WantMsg;

public interface OnNeedChangeWants {
	public void onNeedChangeWants(int start,int end,List<WantMsg>wants);
}
