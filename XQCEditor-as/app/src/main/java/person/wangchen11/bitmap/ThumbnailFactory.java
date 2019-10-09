package person.wangchen11.bitmap;

import java.io.IOException;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.media.ExifInterface;
import android.media.ThumbnailUtils;

public class ThumbnailFactory {

    public static boolean isLandScapeImage(String imgpath) {
        int digree = 0;
        ExifInterface exif = null;
        try {
            exif = new ExifInterface(imgpath);
        } catch (IOException e) {
            e.printStackTrace();
            exif = null;
        }
        if (exif != null) {
            int ori = exif.getAttributeInt(ExifInterface.TAG_ORIENTATION,
                    ExifInterface.ORIENTATION_UNDEFINED);
            switch (ori) {
                case ExifInterface.ORIENTATION_ROTATE_90:
                    digree = 90;
                    break;
                case ExifInterface.ORIENTATION_ROTATE_180:
                    digree = 180;
                    break;
                case ExifInterface.ORIENTATION_ROTATE_270:
                    digree = 270;
                    break;
                default:
                    digree = 0;
                    break;
            }
        }
        
        if(digree == 0 || digree == 180){
            return true;
        }
        return false;
    }  
    
	public static PhotoInfo getPhotoInfo(String imagePath)
	{
		PhotoInfo photoInfo=new PhotoInfo();
        BitmapFactory.Options options = new BitmapFactory.Options();  
        options.inJustDecodeBounds = true;  
        BitmapFactory.decodeFile(imagePath, options);  
        if(!isLandScapeImage(imagePath)){
            photoInfo.mWidth = options.outHeight > 0 ? options.outHeight : 1;
            photoInfo.mHeight = options.outWidth > 0 ? options.outWidth : 1;
        } else {
            photoInfo.mHeight = options.outHeight > 0 ? options.outHeight : 1;
            photoInfo.mWidth = options.outWidth > 0 ? options.outWidth : 1;
        }
        return photoInfo;
	}
	
	public static void resizeInfo(PhotoInfo photoInfo,int maxWidth,int maxHeight)
	{
		double scale1=((double)maxHeight)/photoInfo.mHeight;
		double scale2=((double)maxWidth)/photoInfo.mWidth;
		double scale=scale1<scale2?scale1:scale2;
		photoInfo.mResizedHeight=(int) (scale*photoInfo.mHeight);
		if(photoInfo.mResizedHeight<2)
			photoInfo.mResizedHeight=2;
		photoInfo.mResizedWidth=(int) (scale*photoInfo.mWidth);
		if(photoInfo.mResizedWidth<2)
			photoInfo.mResizedWidth=2;
		//System.out.println("w:"+photoInfo.mWidth+" h:"+photoInfo.mHeight+" rw:"+photoInfo.mResizedWidth+" rh:"+photoInfo.mResizedHeight+" scale:"+scale);
	}
	
    public static Bitmap getImageThumbnail(String imagePath, int width, int height) {  
        Bitmap bitmap = null;  
        BitmapFactory.Options options = new BitmapFactory.Options();  
        options.inJustDecodeBounds = true;  
        bitmap = BitmapFactory.decodeFile(imagePath, options);  
        options.inJustDecodeBounds = false; 
        int h = options.outHeight;  
        int w = options.outWidth;  
        float beWidth = w / width-1;  
        float beHeight = h / height-1;  
        float be = 1;
        if (beWidth < beHeight) {  
            be = beWidth;  
        } else {  
            be = beHeight;  
        }  
        if (be <= 1) {  
            be = 1;  
        }  
        options.inSampleSize = (int) be;  
        
        bitmap = BitmapFactory.decodeFile(imagePath, options);  
        bitmap = loadOrientationBitmap(imagePath, bitmap);
        bitmap = ThumbnailUtils.extractThumbnail(bitmap, width, height,  
                ThumbnailUtils.OPTIONS_RECYCLE_INPUT);
        
        return bitmap;  
    }
    
    public static Bitmap getVideoThumbnail(String videoPath, int width, int height,  
            int kind) {  
        Bitmap bitmap = null;  
        bitmap = ThumbnailUtils.createVideoThumbnail(videoPath, kind);  
        System.out.println("w"+bitmap.getWidth());  
        System.out.println("h"+bitmap.getHeight());  
        bitmap = ThumbnailUtils.extractThumbnail(bitmap, width, height,  
                ThumbnailUtils.OPTIONS_RECYCLE_INPUT);  
        return bitmap;  
    }  
    
    public static Bitmap loadOrientationBitmap(String imgpath, Bitmap bm) {  
        if (bm == null || imgpath == null) {  
            return bm;  
        } else {  
            int digree = 0;  
            ExifInterface exif = null;  
            try {  
                exif = new ExifInterface(imgpath);  
            } catch (IOException e) {  
                e.printStackTrace();  
                exif = null;  
            }  
            if (exif != null) {  
                int ori = exif.getAttributeInt(ExifInterface.TAG_ORIENTATION,  
                        ExifInterface.ORIENTATION_UNDEFINED);  
                switch (ori) {  
                case ExifInterface.ORIENTATION_ROTATE_90:  
                    digree = 90;  
                    break;  
                case ExifInterface.ORIENTATION_ROTATE_180:  
                    digree = 180;  
                    break;  
                case ExifInterface.ORIENTATION_ROTATE_270:  
                    digree = 270;  
                    break;  
                default:  
                    digree = 0;  
                    break;  
                }  
            }  
            if (digree != 0) { 
                Matrix m = new Matrix();  
                m.postRotate(digree);  
                bm = Bitmap.createBitmap(bm, 0, 0, bm.getWidth(),  
                        bm.getHeight(), m, true);  
            }  
            return bm;  
        }  
    }  

      
}
