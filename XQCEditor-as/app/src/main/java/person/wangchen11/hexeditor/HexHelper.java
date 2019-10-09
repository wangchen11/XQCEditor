package person.wangchen11.hexeditor;

import java.io.File;
import java.io.IOException;
import java.io.RandomAccessFile;

import android.util.Log;

public class HexHelper {
	private File mFile  = null;
	private RandomAccessFile mRandomAccessFile = null;
	private byte[] mBuffer = new byte[4096];
	private long mBufferOffset = -1;
	private int mBufferReadLen = -1;
	
	public HexHelper(File file) {
		mFile = file;
		try {
			if(mFile!=null&&mFile.exists()){
				mRandomAccessFile = new RandomAccessFile(mFile, "rw");
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	public boolean isFileOpend(){
		return mRandomAccessFile==null;
	}
	
	public void close() {
		if(mRandomAccessFile!=null){
			try {
				mRandomAccessFile.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
			mRandomAccessFile = null;
		}
	}
	
	private boolean isPositionInBuffer(long position){
		if(
				mBuffer!=null &&
				mBufferOffset>=0 &&
				mBufferReadLen>0 &&
				mBufferOffset<=position &&
				(mBufferOffset+mBufferReadLen)>position){
			return true;
		}
		return false;
	}
	
	public int readAt(long position){
		if(mRandomAccessFile!=null){
			if(isPositionInBuffer(position)){
				return mBuffer[(int) (position-mBufferOffset)];
			}
			
			try {
				long newPosition = position - mBuffer.length/2;
				if(newPosition<0){
					newPosition = 0;
				}
				mRandomAccessFile.seek(newPosition);
				mBufferReadLen = mRandomAccessFile.read(mBuffer);
				mBufferOffset = newPosition;
				Log.i("", "mBufferReadLen:"+mBufferReadLen);
				if(isPositionInBuffer(position)){
					return mBuffer[(int) (position-mBufferOffset)];
				}
			} catch (IOException e) {
				e.printStackTrace();
				mBufferOffset = -1;
				mBufferOffset = -1;	
			}
		}
		return -1;
	}
	
	public void writeAt(int position,byte b){
		if(mRandomAccessFile!=null){
			try {
				mRandomAccessFile.seek(position);
				mRandomAccessFile.write(b);
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}
	
	public long getLength() {
		if(mRandomAccessFile!=null){
			try {
				return mRandomAccessFile.length();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		return 0;
	}
}
