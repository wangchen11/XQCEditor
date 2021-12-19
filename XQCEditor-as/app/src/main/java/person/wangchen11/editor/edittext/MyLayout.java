package person.wangchen11.editor.edittext;

import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Rect;
import android.text.TextPaint;

public interface MyLayout {
	public void setPaint(TextPaint paint);

	public void setLineHeight(float lineHeight);
	
	public float getLineHeight();

	public int getLineForOffset(int start);
	
	public int getLineStart(int line);

	public int getLineEnd(int line);

	public float getPrimaryHorizontal(int start);

	public void getLineBounds(int line, Rect mBoundsOfCursor);

	public int getLineForVertical(int top);

	public void draw(Canvas canvas);

	public int getLineCount();

	public Paint getPaint();

	public int getOffsetForHorizontal(int line, float x);

	public int getHeight();
}
