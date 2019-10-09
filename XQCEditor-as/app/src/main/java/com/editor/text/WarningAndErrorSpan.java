package com.editor.text;

import android.graphics.Color;
import android.text.TextPaint;
import android.text.style.UnderlineSpan;

public class WarningAndErrorSpan extends UnderlineSpan {
	@Override
	public void updateDrawState(TextPaint ds) {
		ds.setColor(Color.RED);
		super.updateDrawState(ds);
	}
}
