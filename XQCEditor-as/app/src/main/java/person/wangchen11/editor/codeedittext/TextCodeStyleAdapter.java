package person.wangchen11.editor.codeedittext;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

import person.wangchen11.ccode.WantMsg;
import person.wangchen11.editor.edittext.SpanBody;
import android.os.Handler;

public class TextCodeStyleAdapter  extends CodeStyleAdapter{

	public TextCodeStyleAdapter(Handler handler, int length,
			CodeStypeAdapterListener adapterListener) {
		super(handler, length, adapterListener);
	}

	@Override
	public void parser() {
	}

	@Override
	public List<SpanBody> getStyles() {
		return new ArrayList<SpanBody>();
	}

	@Override
	public LinkedList<WantMsg> getWants() {
		return new LinkedList<WantMsg>();
	}

	@Override
	public int getWantChangeStart() {
		return 0;
	}

	@Override
	public int getWantChangeEnd() {
		return 0;
	}

}
