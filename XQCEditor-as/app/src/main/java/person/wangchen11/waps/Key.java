package person.wangchen11.waps;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

import android.annotation.SuppressLint;
import android.content.Context;
import android.telephony.TelephonyManager;

public class Key {
	@SuppressLint("SdCardPath")
	public static String mKeyPath="/sdcard/qeditor/data/key.key";
	public static String mDefaultKey="1234567890";
	public static boolean createKey(Context context)
	{
		File file = new File(mKeyPath);
		file.getParentFile().mkdirs();
		try {
			FileOutputStream out=new FileOutputStream(file);
			try {
				out.write(getKey(context).getBytes());
				out.close();
			} catch (IOException e) {
				e.printStackTrace();
				return false;
			}
		} catch (FileNotFoundException e) {
			e.printStackTrace();
			return false;
		}
		return hasRealKey(context);
	}
	
	public static boolean hasRealKey(Context context)
	{
		try {
			String key=getTextFromFile(new File(mKeyPath));
			if(getKey(context).trim().equals(key.trim()))
				return true;
		} catch (Exception e) {
		}
		return false;
	}
	
	public static String getIMEI(Context context) {
		return ((TelephonyManager)context.getSystemService(Context.TELEPHONY_SERVICE)).getDeviceId();
	}
	
	private static String getKey(Context context)
	{
		String imei=getIMEI(context);
		if(imei==null)
		{
			imei=mDefaultKey;
		}
		return MD5.getMD5(imei+mDefaultKey);
	}
	
	private static String getTextFromFile(File file) throws Exception{
		if(file==null)
		{
			return "";
		}
		String str="";
		file=new File(file.getPath());
		int length=(int) file.length();
		FileInputStream fileInputStream=new FileInputStream(file);
		try {
			byte []data=new byte[length];
			int readlen=fileInputStream.read(data);
			str=new String(data, 0, readlen);
		} catch (OutOfMemoryError error) {
			throw new Exception("getTextFromFile OOM!");
		}
		finally
		{
			fileInputStream.close();
		}
		return str;
	}
}
class MD5 {
	public static String getMD5(String val) {  
		try {
			MessageDigest md5;
			md5 = MessageDigest.getInstance("MD5");
			md5.update(val.getBytes());  
			byte[] m = md5.digest();//加密  
			return getString(m);  
		} catch (NoSuchAlgorithmException e) {
			e.printStackTrace();
		} 
		return "1234567890";
	}  
	private static String getString(byte[] hash){
		StringBuilder hex = new StringBuilder(); 
		for (byte b : hash) { 
			int number = b & 0xFF;
			if (number < 0x10) 
				hex.append("0"); 
			else
				hex.append(Integer.toHexString(number)); 
		}
	 return hex.toString();
	}  
}  
