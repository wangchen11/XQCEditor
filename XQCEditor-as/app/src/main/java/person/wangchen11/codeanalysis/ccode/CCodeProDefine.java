package person.wangchen11.codeanalysis.ccode;

import java.util.LinkedList;

public class CCodeProDefine {
	public CCodeSpan mKey = null;
	public LinkedList<CCodeSpan> mCodeSpans = new LinkedList<CCodeSpan>();
	@Override
	public String toString() {
		return mCodeSpans.toString();
	}
}