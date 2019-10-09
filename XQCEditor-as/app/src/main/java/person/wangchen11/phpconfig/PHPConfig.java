package person.wangchen11.phpconfig;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Scanner;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.os.Environment;

@SuppressLint("SdCardPath")
public class PHPConfig {
	public final static String PHP_SERVER_ACTION_START_SERVER="person.wangchen11.phpserver.action_start_server";
	public final static String PHP_SERVER_ACTION_STOP_SERVER="person.wangchen11.phpserver.action_stop_server";
	public final static String PHP_SERVER_ACTION_STATE_CHANGE="person.wangchen11.phpserver.action_state_change";
	public final static String PHP_SERVER_ACTION_MSG="person.wangchen11.phpserver.action_message";
	public final static String PHP_SERVER_STATE="person.wangchen11.phpserver.state";
	public final static String PHP_SERVER_MSG="person.wangchen11.phpserver.message";
	public final static String PHP_SERVER_STATE_CLOSED="person.wangchen11.phpserver.state_closed";
	public final static String PHP_SERVER_STATE_CLOSING="person.wangchen11.phpserver.state_closing";
	public final static String PHP_SERVER_STATE_CLOSE_FAIL="person.wangchen11.phpserver.state_close_fail";
	public final static String PHP_SERVER_STATE_OPENED="person.wangchen11.phpserver.state_opened";
	public final static String PHP_SERVER_STATE_OPENING="person.wangchen11.phpserver.state_opening";
	public final static String PHP_SERVER_STATE_OPEN_FAIL="person.wangchen11.phpserver.state_open_fail";
	public static String getTipByState(Context context,String state)
	{
		if(PHP_SERVER_STATE_CLOSED.endsWith(state))
			return "已关闭";
		if(PHP_SERVER_STATE_CLOSING.endsWith(state))
			return "关闭中";
		if(PHP_SERVER_STATE_CLOSE_FAIL.endsWith(state))
			return "关闭失败";
		if(PHP_SERVER_STATE_OPENED.endsWith(state))
			return "已开启";
		if(PHP_SERVER_STATE_OPENING.endsWith(state))
			return "开启中";
		if(PHP_SERVER_STATE_OPEN_FAIL.endsWith(state))
			return "开启失败";
		return "UNKNOW";
	}
	public String  HTTPD_DOC_ROOT = "/sdcard/qeditor/workspace/www";
	public String  HTTPD_ERR_LOG = "/sdcard/qeditor/data/lighttpd.log";
	public String  HTTPD_UPLOAD_DIR = "/sdcard/qeditor/data/upload";
	public int     HTTPD_PORT = 8000;
	public int     MYSQL_PORT = 3306;
	public String  MYSQL_DATEBASE_DIR = "/sdcard/qeditor/data/db";
	
	public PHPConfig() {
		String sdcard=Environment.getExternalStorageDirectory().getPath();
		HTTPD_DOC_ROOT=sdcard+"/qeditor/workspace/www";
		HTTPD_ERR_LOG=sdcard+"/qeditor/data/lighttpd.log";
		HTTPD_UPLOAD_DIR=sdcard+"/qeditor/data/upload";
		MYSQL_DATEBASE_DIR=sdcard+"/qeditor/data/db";
	}
	
	public boolean configHttpd(String fromPath,String toPath)
	{
		try {
			Scanner scanner=new Scanner(new File(fromPath));
			FileOutputStream fileOutputStream=new FileOutputStream(new File(toPath));
			while(scanner.hasNextLine())
			{
				String line=scanner.nextLine();
				String writeData="";
				if(line.startsWith("server.document-root"))
				{
					writeData="server.document-root = "+"\""+HTTPD_DOC_ROOT+"\"\n";
				} else
				if(line.startsWith("server.errorlog"))
				{
					writeData="server.errorlog = "+"\""+HTTPD_ERR_LOG+"\"\n";
				} else
				if(line.startsWith("server.upload-dirs"))
				{
					writeData="server.upload-dirs = "+"(\""+HTTPD_UPLOAD_DIR+"\")\n";
				} else
				if(line.startsWith("server.port"))
				{
					writeData="server.port = "+HTTPD_PORT+"\n";
				} else
				{
					writeData=line+"\n";
				}
				fileOutputStream.write(writeData.getBytes());
			}
			fileOutputStream.close();
			scanner.close();
			return true;
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return false;
	}
	public boolean configMysql(String fromPath,String toPath)
	{
		try {
			Scanner scanner=new Scanner(new File(fromPath));
			FileOutputStream fileOutputStream=new FileOutputStream(new File(toPath));
			while(scanner.hasNextLine())
			{
				String line=scanner.nextLine();
				String writeData="";
				if(line.startsWith("port"))
				{
					writeData="port="+MYSQL_PORT+"\n";
				} else
				if(line.startsWith("datadir"))
				{
					writeData="datadir="+"\""+MYSQL_DATEBASE_DIR+"\"\n";
				} else
				{
					writeData=line+"\n";
				}
				fileOutputStream.write(writeData.getBytes());
			}
			fileOutputStream.close();
			scanner.close();
			return true;
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return false;
	}
	public boolean configPhp(String fromPath,String toPath)
	{
		try {
			Scanner scanner=new Scanner(new File(fromPath));
			FileOutputStream fileOutputStream=new FileOutputStream(new File(toPath));
			while(scanner.hasNextLine())
			{
				String line=scanner.nextLine();
				String writeData="";
				if(line.startsWith("mysqli.default_port"))
				{
					writeData="mysqli.default_port = "+MYSQL_PORT+"\n";
				} else
				{
					writeData=line+"\n";
				}
				fileOutputStream.write(writeData.getBytes());
			}
			fileOutputStream.close();
			scanner.close();
			return true;
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return false;
	}
	public void putToIntent(Intent intent)
	{
		intent.putExtra("HTTPD_DOC_ROOT", HTTPD_DOC_ROOT);
		intent.putExtra("HTTPD_ERR_LOG", HTTPD_ERR_LOG);
		intent.putExtra("HTTPD_UPLOAD_DIR", HTTPD_UPLOAD_DIR);
		intent.putExtra("HTTPD_PORT", HTTPD_PORT);
		intent.putExtra("MYSQL_PORT", MYSQL_PORT);
		intent.putExtra("MYSQL_DATEBASE_DIR", MYSQL_DATEBASE_DIR);
	}
	public static PHPConfig getConfigFromIntent(Intent intent)
	{
		PHPConfig phpConfig=new PHPConfig();
		phpConfig.HTTPD_DOC_ROOT=intent.getStringExtra("HTTPD_DOC_ROOT");
		phpConfig.HTTPD_ERR_LOG=intent.getStringExtra("HTTPD_ERR_LOG");
		phpConfig.HTTPD_UPLOAD_DIR=intent.getStringExtra("HTTPD_UPLOAD_DIR");
		phpConfig.HTTPD_PORT=intent.getIntExtra("HTTPD_PORT",8000);
		phpConfig.MYSQL_PORT=intent.getIntExtra("MYSQL_PORT",3306);
		phpConfig.MYSQL_DATEBASE_DIR=intent.getStringExtra("MYSQL_DATEBASE_DIR");
		return phpConfig;
	}
	
	public void makeNeedDirs()
	{
		new File(HTTPD_DOC_ROOT).mkdirs();
		new File(HTTPD_ERR_LOG).getParentFile().mkdirs();
		new File(HTTPD_UPLOAD_DIR).mkdirs();
		new File(MYSQL_DATEBASE_DIR).mkdirs();
	}
	
	public static PHPConfig load(Context context)
	{
		PHPConfig config=new PHPConfig();
		SharedPreferences preferences=context.getSharedPreferences("PHPConfig", Context.MODE_PRIVATE);
		config.HTTPD_DOC_ROOT=preferences.getString("HTTPD_DOC_ROOT", config.HTTPD_DOC_ROOT);
		config.HTTPD_ERR_LOG=preferences.getString("HTTPD_ERR_LOG", config.HTTPD_ERR_LOG);
		config.HTTPD_UPLOAD_DIR=preferences.getString("HTTPD_UPLOAD_DIR", config.HTTPD_UPLOAD_DIR);
		config.HTTPD_PORT=preferences.getInt("HTTPD_PORT", config.HTTPD_PORT);
		config.MYSQL_PORT=preferences.getInt("MYSQL_PORT", config.MYSQL_PORT);
		config.MYSQL_DATEBASE_DIR=preferences.getString("MYSQL_DATEBASE_DIR", config.MYSQL_DATEBASE_DIR);
		return config;
	}
	
	public void save(Context context)
	{
		SharedPreferences preferences=context.getSharedPreferences("PHPConfig", Context.MODE_PRIVATE);
		Editor editor=preferences.edit();
		editor.putString("HTTPD_DOC_ROOT", HTTPD_DOC_ROOT);
		editor.putString("HTTPD_ERR_LOG", HTTPD_ERR_LOG);
		editor.putString("HTTPD_UPLOAD_DIR", HTTPD_UPLOAD_DIR);
		editor.putInt("HTTPD_PORT", HTTPD_PORT);
		editor.putInt("MYSQL_PORT", MYSQL_PORT);
		editor.putString("MYSQL_DATEBASE_DIR", MYSQL_DATEBASE_DIR);
		editor.commit();
	}
	
	public String getUrl(File file)
	{
		File docDir=new File(HTTPD_DOC_ROOT);
		try {
			String dir=docDir.getCanonicalPath();
			String path=file.getCanonicalPath();
			if(path.startsWith(dir))
			{
				return path.substring(dir.length());
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
		return null;
	}
	
}
