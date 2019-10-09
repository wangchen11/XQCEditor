package person.wangchen11.packageapk;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.Scanner;

import android.util.Log;

class Info
{
	public Info(String line) {
		String []strs=line.split(":");
		if(strs.length>1)
		{
			name=strs[0];
			info=strs[1];
			name=name.replaceAll("\t", "");
			if(info.startsWith(" "));
				info=info.substring(1);
		}
	}
	String name;
	String info;
}

public class CPUInfo {
	private static String CPUIfoFile="/proc/cpuinfo";
	private static CPUInfo mCPUInfo=null;
	private LinkedList<Info> mInfos=new LinkedList<Info>();
	
	private CPUInfo() {
		try {
			Scanner sc=new Scanner(new File(CPUIfoFile));
			while(sc.hasNextLine())
			{
				mInfos.add(new Info(sc.nextLine()));
			}
			sc.close();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}
	}
	
	public static CPUInfo getInstance()
	{
		if(mCPUInfo==null)
		{
			mCPUInfo=new CPUInfo();
		}
		return mCPUInfo;
	}
	
	public String getInfo(String name)
	{
		Iterator<Info> infos = mInfos.iterator();
		while(infos.hasNext())
		{
			Info info=infos.next();
			if(info.name!=null)
			{
				if(info.name.startsWith(name))
				{
					Log.i("CPUInfo", "sure");
					return info.info;
				}
			}
		}
		return null;
	}
}
