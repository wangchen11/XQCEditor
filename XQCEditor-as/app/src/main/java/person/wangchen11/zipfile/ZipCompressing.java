package person.wangchen11.zipfile;
 
import java.io.*;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;
 
/**
 * Function : 文件压缩成zip
 * @author  : lqf
 * @Date    : 2015-12-15
 */
public class ZipCompressing {
	static final String TAG="ZipCompressing";
	private static final int BUFFER_SIZE = 4096;
    static int k = 1; // 定义递归次数变量
 
    /**
     * 压缩指定的单个或多个文件，如果是目录，则遍历目录下所有文件进行压缩
     * @param zipFileName ZIP文件名包含全路径
     * @param files  文件列表
     */
    public static boolean zip(String zipFileName, File... files) {
        ZipOutputStream out = null;
        try {
            createDir(zipFileName);
            out = new ZipOutputStream(new FileOutputStream(zipFileName));
            for (int i = 0; i < files.length; i++) {
                if (null != files[i]) {
                    zip(out, files[i], files[i].getName());
                }
            }
            out.close(); // 输出流关闭
            return true;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }
 
    /**
     * 执行压缩
     * @param out ZIP输入流
     * @param f   被压缩的文件
     * @param base  被压缩的文件名
     */
    private static void zip(ZipOutputStream out, File f, String base) { // 方法重载
        try {
            if (f.isDirectory()) {//压缩目录
                try {
                    File[] fl = f.listFiles();
                    if (fl.length == 0) {
                        out.putNextEntry(new ZipEntry(base + "/"));  // 创建zip实体
                    }
                    for (int i = 0; i < fl.length; i++) {
                        zip(out, fl[i], base + "/" + fl[i].getName()); // 递归遍历子文件夹
                    }
                    //System.out.println("第" + k + "次递归");
                    k++;
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }else{ //压缩单个文件 
            	ZipEntry entry=new ZipEntry(base);
                out.putNextEntry(entry); // 创建zip实体
                FileInputStream in = new FileInputStream(f);
                BufferedInputStream bi = new BufferedInputStream(in);
                int readLen=0;
                byte []data=new byte[BUFFER_SIZE];
                while ( (readLen=bi.read(data))>0 ) {
                    out.write(data,0,readLen); // 将字节流写入当前zip目录
				}
                out.closeEntry(); //关闭zip实体
                in.close(); // 输入流关闭
            }
 
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
 
 
    /**
     * 目录不存在时，先创建目录
     * @param zipFileName
     */
    private static void createDir(String zipFileName){
    	new File(zipFileName).getParentFile().mkdirs();
    }
}