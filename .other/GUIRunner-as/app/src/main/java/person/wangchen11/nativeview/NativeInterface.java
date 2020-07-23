package person.wangchen11.nativeview;

//by wangchen11

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.content.res.AssetFileDescriptor;
import android.graphics.Bitmap;
import android.graphics.Bitmap.Config;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Paint.Style;
import android.graphics.Rect;
import android.graphics.RectF;
import android.media.MediaPlayer;
import android.view.ViewGroup.LayoutParams;
import android.view.inputmethod.EditorInfo;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.Toast;

public class NativeInterface {
	static final String TAG="NativeInterface";
	private static Canvas mCanvas=null;
	private static Paint mPaint=new Paint();
	//字体下沉的值 
	private static float mDescent=0f;
	private static NativeView mNativeView;
	private static Activity mActivity;
	private static Toast mToast;
	private static AlertDialog mAlertDialog;
	private static EditText mEditText;
	static {
		try {
			System.loadLibrary("NativeActivity");
		} catch (Exception e) {
			e.printStackTrace();
		} catch (Error e) {
			e.printStackTrace();
		}
	}

	public static void initActivity(Activity activity)
	{
		mActivity=activity;
		try {
			init(mActivity);
		} catch (Throwable e) {
			Toast.makeText(mActivity, e.toString(), Toast.LENGTH_LONG).show();
		}
	}
	public static void initView(NativeView nativeView)
	{
		mNativeView=nativeView;
		mNativeView.postDelayed(new Runnable() {
			@Override
			public void run() {
				try {
					onLoopCall();
					mNativeView.postDelayed(this, 1);
				} catch (Throwable e) {
					Toast.makeText(mActivity, e.toString(), Toast.LENGTH_LONG).show();
				}
			}
		},1);
	}
	
	private static Rect mBounds = new Rect();
	public static void startDraw(Canvas canvas)
	{
		mCanvas=canvas;
		canvas.getClipBounds(mBounds);
		draw(mBounds.left,mBounds.top,mBounds.right,mBounds.bottom,canvas);
	}

	public static void stopDraw()
	{
		mCanvas=null;
	}
	

	public static void setStrokeWidth(float width)
	{
		mPaint.setStrokeWidth(width);
	}

	public static void setStroke(boolean stroke)
	{
		if(stroke)
			mPaint.setStyle(Style.STROKE);
		else
			mPaint.setStyle(Style.FILL);
	}
	
	public static void drawColor(int color)
	{
		mCanvas.drawColor(color);
	}
	
	public static void setColor(int color)
	{
		mPaint.setColor(color);
	}
	
	public static void setTextSize(float size)
	{
		mPaint.setTextSize(size);
		mDescent=mPaint.getFontMetrics().descent;
	}
	
	public static void drawLine(float startX,float startY,float stopX,float stopY)
	{
		mCanvas.drawLine(startX, startY, stopX, stopY, mPaint);
	}
	
	public static void drawText(String text,int start,int end,float x,float y)
	{
		mCanvas.drawText(text,start,end, x, y-mDescent, mPaint);
	}
	
	public static void drawRect(float left, float top, float right, float bottom)
	{
		mCanvas.drawRect(left, top, right, bottom, mPaint);
	}
	
	static private RectF mRectF=new RectF(); 
	public static void drawRoundRect(float left, float top, float right, float bottom,float rx,float ry)
	{
		mRectF.set(left, top, right, bottom);
		mCanvas.drawRoundRect(mRectF, rx, ry, mPaint);
	}

    public static void invalidate() 
    {
		mNativeView.invalidate();
    }
    
    public static void postInvalidate() 
    {
		mNativeView.postInvalidate();
    }
    
    public static void invalidateRect(int left,int top,int right,int bottom) 
    {
		mNativeView.invalidate(left,top,right,bottom);
    }
    
    public static void postInvalidateRect(int left,int top,int right,int bottom) 
    {
		mNativeView.postInvalidate(left,top,right,bottom);
    }
    
    public static Bitmap decodeBitmapFromAssets(String name)
    {
    	if(DebugInfo.mAssetsPath!=null)
    		return decodeBitmapFromFile(DebugInfo.mAssetsPath+"/"+name);
    	InputStream in;
		Bitmap bmp=null;
		try {
			in = mActivity.getAssets().open(name);
			try{
				bmp=BitmapFactory.decodeStream(in).copy(Config.ARGB_8888, true);
			}
			catch(OutOfMemoryError error)
			{
				error.printStackTrace();
			}
			in.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
    	return bmp;
    }

    public static Bitmap decodeBitmapFromFile(String path)
    {
    	InputStream in;
		Bitmap bmp=null;
		try {
			in = new FileInputStream(path);
			try{
				bmp=BitmapFactory.decodeStream(in).copy(Config.ARGB_8888, true);
			}
			catch(OutOfMemoryError error)
			{
				error.printStackTrace();
			}
			try {
				in.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}
    	return bmp;
    }

    public static boolean saveBitmapToFile(Bitmap bitmap,String path,int format,int quality)
    {
    	Bitmap.CompressFormat compressFormat = Bitmap.CompressFormat.PNG;
    	if(format==0){
    		compressFormat = Bitmap.CompressFormat.JPEG;
    	}
    	FileOutputStream fileOutputStream = null;
		try {
			fileOutputStream = new FileOutputStream(path);
	    	bitmap.compress(compressFormat, quality, fileOutputStream);
	    	return true;
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} finally{
	    	try {
	    		if(fileOutputStream!=null)
	    			fileOutputStream.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
    	return false;
    }
    
    public static Bitmap createBitmap(int width,int height)
    {
    	Bitmap bmp=null;
		try{
			bmp=Bitmap.createBitmap(width, height, Config.ARGB_8888);
		}
		catch(OutOfMemoryError e)
		{
			e.printStackTrace();
		}
    	return bmp;
    }
    
    public static float measureText(String text,int start,int end)
    {
    	return mPaint.measureText(text, start, end);
    }
    
    private static Rect mRectFrom=new Rect();
    private static RectF mRectTo=new RectF();
    public static void drawBitmap(Bitmap bitmap
    		,int fromLeft,int fromTop,int fromRight,int fromBottom
    		,float toLeft,float toTop,float toRight,float toBottom)
    {
    	mRectFrom.set(fromLeft, fromTop, fromRight, fromBottom);
    	mRectTo.set(toLeft, toTop, toRight, toBottom);
    	mCanvas.drawBitmap(bitmap, mRectFrom, mRectTo, mPaint);
    }
    
    public static void finish()
    {
    	mActivity.finish();
    }
    
    public static boolean requestEditText(String title,String string,int cur)
    {
    	if(mAlertDialog!=null && mAlertDialog.isShowing())
    	{
    		return false;
    	}
    	
    	AlertDialog.Builder builder=new AlertDialog.Builder(mActivity);
    	builder.setTitle(title);
    	mEditText=new EditText(mActivity);
    	mEditText.setText(string);
    	mEditText.setSelection(cur);
    	ScrollView scrollView=new ScrollView(mActivity);
    	LinearLayout linearLayout=new LinearLayout(mActivity);
    	linearLayout.addView(mEditText, LayoutParams.FILL_PARENT,LayoutParams.WRAP_CONTENT);
    	scrollView.addView(linearLayout);
    	builder.setView(scrollView);
		builder.setNegativeButton("取消", new OnClickListener() {
			@Override
			public void onClick(DialogInterface dialog, int which) {
				onEditTextCancel(mEditText.getText().toString(), mEditText.getSelectionEnd());
			}
		});
		builder.setPositiveButton("确定", new OnClickListener() {
			@Override
			public void onClick(DialogInterface dialog, int which) {
				onEditTextSure(mEditText.getText().toString(), mEditText.getSelectionEnd());
			}
		});
    	mAlertDialog = builder.create();
    	mAlertDialog.setCancelable(false);
    	mAlertDialog.show();
    	return true;
    }
    
    
    public static void showToast(String string,int time)
    {
    	if(mToast!=null)
    	{
    		mToast.cancel();
    	}
    	mToast=Toast.makeText(mActivity, string, time);
    	mToast.show();
    }
    
    public static long currentTimeMillis()
    {
    	return System.currentTimeMillis();
    }
    
    public static long nanoTime()
    {
    	return System.nanoTime();
    }

    public static byte []readAllFromAssets(String name)
    {
    	if(DebugInfo.mAssetsPath!=null)
    		return readAllFromFile(DebugInfo.mAssetsPath+"/"+name);
    	byte data[]=null;
    	try {
    		/*
    		AssetFileDescriptor fd = mActivity.getAssets().openFd(name);
			data=new byte[(int) fd.getLength()];
			InputStream inputStream=fd.createInputStream();
			*/
    		InputStream inputStream=mActivity.getAssets().open(name);
    		data=new byte[inputStream.available()];
			inputStream.read(data);
			inputStream.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
    	return data;
    }

    public static byte []readAllFromFile(String name)
    {
    	byte data[]=null;
    	try {
    		InputStream inputStream=new FileInputStream(new File(name));
    		data=new byte[inputStream.available()];
			inputStream.read(data);
			inputStream.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
    	return data;
    }
    
    public static MediaPlayer createMediaPlayer()
    {
    	MediaPlayer mediaPlayer=new MyMediaPlayer();
    	return mediaPlayer;
    }

    public static boolean mediaPlayerSetSourceFromAssert(MediaPlayer mediaPlayer,String path)
    {
    	if(DebugInfo.mAssetsPath!=null)
    		return mediaPlayerSetSourceFromPath(mediaPlayer,DebugInfo.mAssetsPath+"/"+path);
    	try {
    		AssetFileDescriptor assetFileDescriptor=mActivity.getAssets().openFd(path);
    		if(assetFileDescriptor==null)
    			return false;
    		mediaPlayer.setDataSource(assetFileDescriptor.getFileDescriptor(),
    				assetFileDescriptor.getStartOffset(), assetFileDescriptor.getLength() );//(assetFileDescriptor.getFileDescriptor());
			return true;
		} catch (IllegalArgumentException e) {
			e.printStackTrace();
		} catch (IllegalStateException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
    	return false;
    }
    
    public static boolean mediaPlayerSetSourceFromPath(MediaPlayer mediaPlayer,String path)
    {
    	try {
			mediaPlayer.setDataSource(path);
			return true;
		} catch (IllegalArgumentException e) {
			e.printStackTrace();
		} catch (IllegalStateException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
    	return false;
    }

    public static boolean mediaPlayerPrepare(MediaPlayer mediaPlayer)
    {
    	try {
			mediaPlayer.prepare();
			return true;
		} catch (IllegalStateException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
    	return false;
    }

    public static boolean mediaPlayerStart(MediaPlayer mediaPlayer)
    {
    	try {
			mediaPlayer.start();
			return true;
		} catch (IllegalStateException e) {
			e.printStackTrace();
		}
    	return false;
    }

    public static boolean mediaPlayerStop(MediaPlayer mediaPlayer)
    {
    	try {
			mediaPlayer.stop();
			return true;
		} catch (IllegalStateException e) {
			e.printStackTrace();
		}
    	return false;
    }
    
    public static boolean mediaPlayerReset(MediaPlayer mediaPlayer)
    {
    	try {
			mediaPlayer.reset();
			return true;
		} catch (IllegalStateException e) {
			e.printStackTrace();
		}
    	return false;
    }

    public static boolean mediaPlayerRlease(MediaPlayer mediaPlayer)
    {
    	try {
			mediaPlayer.release();
			return true;
		} catch (IllegalStateException e) {
			e.printStackTrace();
		}
    	return false;
    }

    public static boolean mediaPlayerPause(MediaPlayer mediaPlayer)
    {
    	try {
			mediaPlayer.pause();
			return true;
		} catch (IllegalStateException e) {
			e.printStackTrace();
		}
    	return false;
    }
    
    public static boolean mediaPlayerSetLooping(MediaPlayer mediaPlayer,boolean looping)
    {
    	try {
			mediaPlayer.setLooping(looping);
			return true;
		} catch (IllegalStateException e) {
			e.printStackTrace();
		}
    	return false;
    }

    public static boolean mediaPlayerIsLooping(MediaPlayer mediaPlayer)
    {
    	try {
			return mediaPlayer.isLooping();
		} catch (IllegalStateException e) {
			e.printStackTrace();
		}
    	return false;
    }

    public static boolean mediaPlayerIsPlaying(MediaPlayer mediaPlayer)
    {
    	try {
			return mediaPlayer.isPlaying();
		} catch (IllegalStateException e) {
			e.printStackTrace();
		}
    	return false;
    }

    public static boolean mediaPlayerSetVolume(MediaPlayer mediaPlayer,float leftVolume,float rightVolume)
    {
    	try {
			mediaPlayer.setVolume(leftVolume, rightVolume);
			return true;
		} catch (IllegalStateException e) {
			e.printStackTrace();
		}
    	return false;
    }

    public static boolean mediaPlayerSeekTo(MediaPlayer mediaPlayer,int msec)
    {
    	try {
			mediaPlayer.seekTo(msec);
			return true;
		} catch (IllegalStateException e) {
			e.printStackTrace();
		}
    	return false;
    }

    public static int mediaPlayerGetDuration(MediaPlayer mediaPlayer)
    {
    	try {
			return mediaPlayer.getDuration();
		} catch (IllegalStateException e) {
			e.printStackTrace();
		}
    	return -1;
    }
    
    public static int mediaPlayerGetCurrentPosition(MediaPlayer mediaPlayer)
    {
    	try {
			return mediaPlayer.getCurrentPosition();
		} catch (IllegalStateException e) {
			e.printStackTrace();
		}
    	return -1;
    }

    public static int mediaPlayerGetVedioWidth(MediaPlayer mediaPlayer)
    {
    	try {
			return mediaPlayer.getVideoWidth();
		} catch (Exception e) {
			e.printStackTrace();
		}
    	return 0;
    }
    
    public static int mediaPlayerGetVedioHeight(MediaPlayer mediaPlayer)
    {
    	try {
			return mediaPlayer.getVideoHeight();
		} catch (Exception e) {
			e.printStackTrace();
		}
    	return 0;
    }

	public static void canvasSave()
	{
		mCanvas.save();
	}
    
	public static void canvasRestore()
	{
		mCanvas.restore();
	}

	public static void canvasClipRect(float left,float top,float right,float bottom)
	{
		mCanvas.clipRect(left, top, right, bottom);
	}
	
	public static void canvasRotate(float degrees,float px,float py)
	{
		mCanvas.rotate(degrees, px, py);
	}

	public static void canvasTranslate(float dx,float dy)
	{
		mCanvas.translate(dx, dy);
	}
	
	public static void canvasScale(float sx, float sy, float px, float py) 
	{
		mCanvas.scale(sx, sy, px, py);
	}

	public static int mImeOption=EditorInfo.IME_FLAG_NO_ENTER_ACTION | EditorInfo.IME_ACTION_DONE | EditorInfo.IME_FLAG_NO_EXTRACT_UI;;
	public static int mInputType=EditorInfo.TYPE_CLASS_TEXT ;
	public static void showInputMethod(int imeOptions,int inputType)
	{
		mImeOption=imeOptions;
		mInputType=inputType;
		mNativeView.showSoftKeyboard();
	}
	
	public static void closeInputMethod()
	{
		mNativeView.closeInputMethod();
	}
	
	public static float getDensity()
	{
		return mActivity.getResources().getDisplayMetrics().density;
	}
	
	private static native void init(Activity activity);
	private static native void draw(int left,int top,int right,int bottom,Canvas canvas);
	public static native boolean touchEvent(int action,float x,float y,int pointerIndex,int pointerCount
			,float pointersX[],float pointersY[],int pointersId[]);
	public static native void sizeChange(int w, int h, int oldw, int oldh,float density);
	public static native void destroy();
	public static native boolean backPressed();
	public static native void onEditTextCancel(String string,int cur);
	public static native void onEditTextSure(String string,int cur);
	public static native void onLoopCall();
	public static native void onPause();
	public static native void onResume();
}





