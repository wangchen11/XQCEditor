package person.wangchen11.plugins;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.HashMap;
import java.util.Map;
import java.util.Scanner;

import android.util.Log;

public class Plugin {
	private File   mFile;
	private String mName;
	private String mAlias;
	private String mVersion;
	private Map<String,String> mKeyValue = null;
	
	
	public String getName(){
		return mName;
	}
	
	public String getAlias(){
		return mAlias;
	}
	
	public String getVersion(){
		return mVersion;
	}
	
	public Plugin(File file) {
		mFile = file;
		mName = file.getName();
		mKeyValue = getVarsReal();
	}
	
	public String getHome(){
		return mFile.getPath();
	}
	
	public String getExportPathCmd(){
		String cmd = "export PLUGHOME=\'"+mFile.getPath()+"/\'\n";
		return cmd;
	}
	
	public String getInstallCmd(){
		try {
			String all = readAll(new File(mFile, "install.sh"));
			if(all == null)
				return "";
			return getExportPathCmd()+all;
		} catch (Exception e) {
			return "";
		}
	}
	
	public String getSourceCmd(){
		try {
			String all = readAll(new File(mFile, "source.sh"));
			if(all == null)
				return "";
			return getExportPathCmd()+all;
		} catch (Exception e) {
			return "";
		}
	}

	public Map<String,String> getVars(){
		return mKeyValue;
	}
	
	private Map<String,String> getVarsReal(){
		Map<String,String> map = new HashMap<String, String>();
		putValueEx(map,"PLUGHOME", getHome());
		try {
			String all = readAll(new File(mFile, "vars.ini"));
			if(all != null){
				Scanner scanner = new Scanner(all);
				while(scanner.hasNextLine()){
					String line = scanner.nextLine();
					Log.i("TAG", "line:"+line);
					int index = line.indexOf("=");
					if(index>0){
						String key = line.substring(0,index).trim();
						String value = line.substring(index+1,line.length()).trim();
						putValueEx(map,key,value);
					}
				}
				scanner.close();
			}
		} catch (Exception e) {
		}
		return map;
	}
	
	public static void putValueEx(Map<String,String> map,String key,String value){
		for(String k : map.keySet()){
			if(k==null)
				continue;
			String v = map.get(k);
			if(v==null)
				continue;
			value = value.replaceAll("\\$"+k, v);
		}
		map.put(key, value);
	}
	
	private String readAll(File file){ 
        Long filelength = file.length();  
        byte[] filecontent = new byte[filelength.intValue()];  
        try {  
            FileInputStream in = new FileInputStream(file);  
            in.read(filecontent);  
            in.close();  
        } catch (FileNotFoundException e) {  
            e.printStackTrace();  
        } catch (IOException e) {  
            e.printStackTrace();  
        }
        String encoding = guessEncoding(filecontent,0,filecontent.length);
        if(encoding == null)
        	encoding = "UTF-8";
        try {  
            return new String(filecontent, encoding);  
        } catch (UnsupportedEncodingException e) {  
            System.err.println("The OS does not support " + encoding);  
            e.printStackTrace();  
            return null;  
        }  
    }

	private static String guessEncoding(byte[] bytes,int offset,int length) {
	    org.mozilla.universalchardet.UniversalDetector detector =  
	        new org.mozilla.universalchardet.UniversalDetector(null);  
	    detector.handleData(bytes, offset, length);  
	    detector.dataEnd();  
	    String encoding = detector.getDetectedCharset();  
	    detector.reset();
	    return encoding;  
	}
	
}
