package person.wangchen11.browser;

import person.wangchen11.xqceditor.R;
import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.net.http.SslError;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.TranslateAnimation;
import android.webkit.SslErrorHandler;
import android.webkit.WebView;
import android.webkit.WebViewClient;

@SuppressLint("SetJavaScriptEnabled")
public class Browser extends Fragment {

	public WebView mWebView = null;
	public String mUrl="";
	
	public Browser(String url) {
		mUrl=url;
	}
	
	@SuppressLint("InflateParams")
	@Override
	public View onCreateView(LayoutInflater inflater,
			@Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
		ViewGroup viewGroup = (ViewGroup) inflater.inflate(R.layout.fragment_browser, null);
        mWebView=(WebView) (viewGroup.findViewById(R.id.apiWebView));

        //是否启用javaScript支持
        mWebView.getSettings().setJavaScriptEnabled(true);
        //是否开启缩放
        mWebView.getSettings().setSupportZoom(true);
        //是否开启缩放符号
        mWebView.getSettings().setBuiltInZoomControls(true);
        //将载入得页面缩放为分辨率合适
        mWebView.getSettings().setUseWideViewPort(false); 
        mWebView.getSettings().setLoadWithOverviewMode(true); 
        //设置字体大小
        mWebView.getSettings().setDefaultFontSize(16);
        //加载html页面
        mWebView.loadUrl(mUrl);
        //设置为用我的mWebWiew打开URL
        mWebView.setWebViewClient(new WebViewClient(){
        	@Override
        	public void onReceivedSslError(WebView view,
        			final SslErrorHandler handler, SslError error) {
        		final AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        		builder.setCancelable(false);
    		    builder.setMessage(R.string.notification_error_ssl_cert_invalid);
    		    builder.setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
    		        @Override
    		        public void onClick(DialogInterface dialog, int which) {
    		            handler.proceed();
    		        }
    		    });
    		    builder.setNegativeButton(android.R.string.cancel, new DialogInterface.OnClickListener() {
    		        @Override
    		        public void onClick(DialogInterface dialog, int which) {
    		            handler.cancel();
    		        }
    		    });
    		    final AlertDialog dialog = builder.create();
    		    dialog.show();
        	}
        });
        return viewGroup;
	}
	
	public void refresh()
	{
		if(mWebView!=null){
			mWebView.clearCache(true);
			mWebView.loadUrl(mUrl);
		}
	}
	
	public boolean back() {
		if(mWebView==null)
			return false;
		if(mWebView.canGoBack())
		{
			View view=mWebView;
			//位移动画特效引入
			TranslateAnimation translate = new TranslateAnimation(mWebView.getWidth(),0,0,0);
			//设置过渡时间
			translate.setDuration(300);
			//开始执行
			view.startAnimation(translate);
			mWebView.goBack();// 返回前一个页面
			return true;
		}
		return false;
	}
}