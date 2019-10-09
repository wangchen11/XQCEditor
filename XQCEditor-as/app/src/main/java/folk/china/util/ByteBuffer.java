package folk.china.util;

/**
 * @author wangchen11
 *
 */
public class ByteBuffer {
	public static final int DEFAULT_START_SIZE = 64;
	public static final int DEFAULT_STEP_SIZE = 512; 
	private byte []mBuffer = null;
	private int mPos = 0;
	private int mStepSize = 0;

	public ByteBuffer() {
		this(DEFAULT_START_SIZE);
	}

	public ByteBuffer(int startSize) {
		this(startSize,DEFAULT_STEP_SIZE);
	}

	public ByteBuffer(int startSize,int stepSize) {
		setRealSize(startSize);
		setStepSize(stepSize);
	}
	
	public void setStepSize(int stepSize)
	{
		if(stepSize<1)
			stepSize=1;
		mStepSize = stepSize;
	}
	
	public void put(byte data)
	{
		checkSize(mPos+1);
		mBuffer[mPos]=data;
		mPos++;
	}
	
	public void put(byte []data)
	{
		put(data,0,data.length);
	}
	
	public void put(byte []data,int offset,int count)
	{
		checkSize(mPos+count);
		System.arraycopy(data, offset, mBuffer, mPos, count);
		mPos+=count;
	}
	
	private void checkSize(int needSize)
	{
		if(needSize>mBuffer.length)
		{
			setRealSize( needSize + mStepSize );
		}
	}
	
	
	private void setRealSize(int realSize)
	{
		byte []tempBuffer = new byte[realSize];
		if(mBuffer!=null)
		{
			System.arraycopy(mBuffer, 0, tempBuffer, 0, mPos);
		}
		mBuffer = tempBuffer;
	}
	
	public void clear()
	{
		mPos = 0;
	}
	
	public int length()
	{
		return mPos;
	}
	
	public byte[] getBytes()
	{
		return mBuffer;
	}
}
