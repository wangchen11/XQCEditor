package person.wangchen11.keygen;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Scanner;

public class KeyGen {
	public static String mKeyPath="/sdcard/qeditor/data/key.key";
	public static String mDefaultKey="1234567890";
	private static String getKey(String imei)
	{
		if(imei==null)
		{
			imei=mDefaultKey;
		}
		return MD5.getMD5(imei+mDefaultKey);
	}
	
	public static void main(String[] args) {
		System.out.println("input imei:");
		Scanner scanner = new Scanner(System.in);
		String imei = scanner.next();
		String key = getKey(imei);

		System.out.println("imei:"+imei);
		System.out.println("key:"+key);
		System.out.println("");
		System.out.println("");
		System.out.println("intput fllow command in you consloe:");
		String cmd = "mkdir "+new File(mKeyPath).getParent().replace('\\', '/')+"\n"
				+"echo -e "+key+"\\\\c > "+mKeyPath+"\n";
		System.out.println(cmd);
		
		String localDir = "keys";
		new File(localDir).mkdir();
		try {
			FileOutputStream fileOutputStream = new FileOutputStream( new File("keys/"+imei+".sh") );
			fileOutputStream.write(cmd.getBytes());
			fileOutputStream.close();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		scanner.close();
	}
}

class MD5 {
	public static String getMD5(String val) {  
		try {
			MessageDigest md5;
			md5 = MessageDigest.getInstance("MD5");
			md5.update(val.getBytes());  
			byte[] m = md5.digest();//º”√‹  
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
