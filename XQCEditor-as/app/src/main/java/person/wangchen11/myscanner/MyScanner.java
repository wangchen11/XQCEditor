package person.wangchen11.myscanner;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;

public class MyScanner  extends AdstractScanner
{
	AdstractScanner mAdstractScanner=null;
	public MyScanner(InputStream in) 
	{
		mAdstractScanner=new ScannerForInputStream(in);
	}
	public MyScanner(File file) throws FileNotFoundException 
	{
		FileInputStream in=null;
		in = new FileInputStream(file);
		mAdstractScanner=new ScannerForInputStream(in);
	}
	public MyScanner(String in)
	{
		mAdstractScanner=new ScannerForInputStream(in);
	}
	@Override
	public boolean hasNext() {
		return mAdstractScanner.hasNext();
	}
	@Override
	public String next() {
		return mAdstractScanner.next();
	}
	@Override
	public boolean hasNextDouble() {
		// TODO Auto-generated method stub
		return mAdstractScanner.hasNextDouble();
	}
	@Override
	public double nextDouble() {
		// TODO Auto-generated method stub
		return mAdstractScanner.nextDouble();
	}
	@Override
	public boolean hasNextLine() {
		// TODO Auto-generated method stub
		return mAdstractScanner.hasNextLine();
	}
	@Override
	public String nextLine() {
		// TODO Auto-generated method stub
		return mAdstractScanner.nextLine();
	}
	@Override
	public boolean hasNextFloat() {
		// TODO Auto-generated method stub
		return mAdstractScanner.hasNextFloat();
	}
	@Override
	public float nextFloat() {
		// TODO Auto-generated method stub
		return mAdstractScanner.nextFloat();
	}
	@Override
	public boolean hasNextInt() {
		// TODO Auto-generated method stub
		return mAdstractScanner.hasNextInt();
	}
	@Override
	public int nextInt() {
		// TODO Auto-generated method stub
		return mAdstractScanner.nextInt();
	}
	@Override
	public void close() {
		mAdstractScanner.close();
	}
}


abstract class AdstractScanner
{
	abstract public boolean hasNext();
	abstract public String next();
	abstract public boolean hasNextLine();
	abstract public String nextLine();
	abstract public boolean hasNextDouble();
	abstract public double nextDouble();
	abstract public boolean hasNextFloat();
	abstract public float nextFloat();
	abstract public boolean hasNextInt();
	abstract public int nextInt();
	abstract public void close();
}


class ScannerForInputStream extends AdstractScanner
{
	InputStream mInputStream=null;
	String mString="";
	byte[] mBuf=new byte[512];
	boolean mIsReadComplete=false;
	String mNext=null;
	String mNextLine=null;
	Double mNextDouble=null;
	Float mNextFloat=null;
	Integer mNextInt=null;
	

	public ScannerForInputStream(InputStream in) 
	{
		this.mInputStream=in;
		mIsReadComplete=false;
	}
	public ScannerForInputStream(String in) 
	{
		this.mInputStream=new ByteArrayInputStream(in.getBytes());
		mIsReadComplete=false;
	}
	private void Read()
	{
		int len=0;
		if(mInputStream==null)
		{
			mIsReadComplete = true;
			return;
		}
		try 
		{
			if((len=mInputStream.read(mBuf))<=0)
			{
				mIsReadComplete = true;
			}
			else
			{
				mString+=new String(mBuf,0,len);
			}
		}
		catch (IOException e) 
		{
			mIsReadComplete = true;
			e.printStackTrace();
			return ;
		}
	}
	public int IndexOf(String str,char[] chs)
	{
		int index=-1;
		if(str==null||str.length()<=0)
			return -1;
		char[] chars=str.toCharArray();
		for(int i=0;i<chars.length;i++)
		{
			for(int j=0;j<chs.length;j++)
			{
				if(chs[j]==chars[i])
					return i;
			}
		}
		return index;
	}
	
	@Override
	public boolean hasNext() {
		if(mNext!=null)
			return true;
		if(mInputStream==null&&mString==null)
			return false;
		int index=0;
		String str="";
		while(true)
		{
			if(mString.length()<=0)
				Read();
			final char[] chs=new char[]{ ' ' , '\r', '\n'};
			index=IndexOf(mString,chs);
			
			//System.out.println("index :"+index+" str:"+mString+" len:"+mString.length());
			
			if( index>0 )
			{
				break;
			}
			else
			if( index==0 )
			{
				this.mString=this.mString.substring(1);
			}
			else
			{
				if(mIsReadComplete)
					break;
				Read();
			}
		}

		
		if(mIsReadComplete&&this.mString.length()<=0)
		{
			this.mNext=null;
			return false;
		}
		
		if(index>0)
		{
			str=this.mString.substring(0,index);
			this.mString=this.mString.substring(index+1);
			this.mNext=str;
			return true;
		}
		else
		{
			str=this.mString;
			this.mString="";
			if(str==null||str.length()<=0)
				return false;
			this.mNext=str;
			return true;
		}
	}
	public String next()
	{
		if(!hasNext())
			return null;
		String next=mNext;
		mNext=null;
		return next;
	}
	
	@Override
	public boolean hasNextLine() {
		if(mNextLine!=null)
			return true;
		if(mInputStream==null)
			return false;
		int index=0;
		String str="";
		while(true)
		{
			if(mString.length()<=0)
				Read();
			final char[] chs=new char[]{ '\r', '\n'};
			index=IndexOf(mString,chs);
			
			//System.out.println("index :"+index+" str:"+mString+" len:"+mString.length());
			
			if( index>0 )
			{
				break;
			}
			else
			if( index==0 )
			{
				this.mString=this.mString.substring(1);
			}
			else
			{
				if(mIsReadComplete)
					break;
				Read();
			}
		}

		
		if(mIsReadComplete&&this.mString.length()<=0)
		{
			this.mNextLine=null;
			return false;
		}
		
		if(index>0)
		{
			str=this.mString.substring(0,index);
			this.mString=this.mString.substring(index+1);
			this.mNextLine=str;
			return true;
		}
		else
		{
			str=this.mString;
			this.mString="";
			if(str==null||str.length()<=0)
			{
				this.mNextLine=null;
				return false;
			}
			this.mNextLine=str;
			return true;
		}
	}
	
	@Override
	public String nextLine() {
		if(!hasNextLine())
			return null;
		String next=mNextLine;
		mNextLine=null;
		return next;
	}
	
	@Override
	public boolean hasNextDouble() {
		String str="";
		if(mNextDouble!=null)
			return true;
		while(hasNext())
		{
			str=next();
			//System.out.println("@"+str+"@");
			try 
			{
				mNextDouble=new Double(str);
				return true;
			} catch (Exception e) 
			{
				//System.out.println("err");
			}
		}
		return false;
	}
	@Override
	public double nextDouble() {
		if(!hasNextDouble())
			return 0d;
		Double next=mNextDouble;
		mNextDouble=null;
		return next;
	}
	@Override
	public boolean hasNextFloat() {
		String str="";
		if(mNextFloat!=null)
			return true;
		while(hasNext())
		{
			str=next();
			//System.out.println("@"+str+"@");
			try 
			{
				mNextFloat=new Float(str);
				return true;
			} catch (Exception e) 
			{
				//System.out.println("err");
			}
		}
		return false;
	}
	@Override
	public float nextFloat() {
		if(!hasNextFloat())
			return 0f;
		Float next=mNextFloat;
		mNextFloat=null;
		return next;
	}
	@Override
	public boolean hasNextInt() 
	{
		String str="";
		if(mNextInt!=null)
			return true;
		while(hasNext())
		{
			str=next();
			try 
			{
				mNextInt=new Integer(str);
				return true;
			} catch (Exception e) 
			{
			}
		}
		return false;
	}
	@Override
	public int nextInt() {
		if(!hasNextInt())
			return 0;
		Integer next=mNextInt;
		mNextInt=null;
		return next;
	}
	@Override
	public void close() {
		if(mInputStream!=null)
		try 
		{
			mInputStream.close();
		} catch (IOException e) 
		{
			e.printStackTrace();
		}
	}
}