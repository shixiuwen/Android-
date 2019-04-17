package com.yitong.homeland.view;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.net.Uri;
import android.net.http.SslError;
import android.os.Build;
import android.os.Handler;
import android.os.Message;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;
import android.webkit.ConsoleMessage;
import android.webkit.SslErrorHandler;
import android.webkit.WebResourceError;
import android.webkit.WebResourceRequest;
import android.webkit.WebResourceResponse;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.widget.ProgressBar;
import android.widget.ZoomButtonsController;

import com.yitong.homeland.R;
import com.yitong.homeland.main.home.FindWebActivity;
import com.yitong.homeland.utils.CommonUtils;

import java.lang.reflect.Method;

public class CustomWebView extends WebView {
    private ProgressBar progressbar;
    private View error_layout;
    private static final String APP_CACAHE_DIRNAME = "/webcache";

    boolean blockLoadingNetworkImage = false;

    public CustomWebView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context);
        progressbar = new ProgressBar(context, null, android.R.attr.progressBarStyleHorizontal);
        progressbar.setProgressDrawable(context.getResources().getDrawable(R.drawable.progressbar));
        progressbar.setLayoutParams(new LayoutParams(LayoutParams.FILL_PARENT, 5, 0, 0));
        error_layout = View.inflate(context, R.layout.net_error_layout, null);
        addView(progressbar);
    }

    private void init(Context context) {
        setWebChromeClient(new WebChromeClient());
        setWebViewClient(new WebViewClient());

        getSettings().setBlockNetworkImage(true);
        blockLoadingNetworkImage = true;
        getSettings().setBuiltInZoomControls(true);
        getSettings().setJavaScriptEnabled(true);
        getSettings().setBlockNetworkImage(false);
        getSettings().setSavePassword(true);
        getSettings().setSaveFormData(true);
        getSettings().setLoadWithOverviewMode(true);

        if (Build.VERSION.SDK_INT >= 8) {
            getSettings().setPluginState(WebSettings.PluginState.ON);
        }

        // 去掉缩放按钮
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
            // Use the API 11+ calls to disable the controls
            this.getSettings().setBuiltInZoomControls(true);
            this.getSettings().setDisplayZoomControls(false);
        } else {
            // Use the reflection magic to make it work on earlier APIs
            getControlls();
        }

        getSettings().setRenderPriority(WebSettings.RenderPriority.HIGH);
        // 设置缓存模式 
        getSettings().setCacheMode(WebSettings.LOAD_DEFAULT);

        // 开启 DOM storage API 功能
        getSettings().setDomStorageEnabled(true);
        // 开启 database storage API 功能
        getSettings().setDatabaseEnabled(true);
        String cacheDirPath = context.getFilesDir().getAbsolutePath() + APP_CACAHE_DIRNAME;
        // 设置数据库缓存路径
        getSettings().setDatabasePath(cacheDirPath);
        // 设置  Application Caches 缓存目录
        getSettings().setAppCachePath(cacheDirPath);
        // 开启 Application Caches 功能 
        getSettings().setAppCacheEnabled(true);

        //设置载入页面自适应手机屏幕，居中显示
        getSettings().setUseWideViewPort(true);
        getSettings().setLoadWithOverviewMode(true);

        setOverScrollMode(OVER_SCROLL_NEVER);// 去掉阴影

//        getSettings().setJavaScriptEnabled(true);
//        getSettings().setRenderPriority(WebSettings.RenderPriority.HIGH);
//        getSettings().setCacheMode(WebSettings.LOAD_DEFAULT);  //设置 缓存模式
//        // 开启 DOM storage API 功能
//        getSettings().setDomStorageEnabled(true);
//        //开启 database storage API 功能
//        getSettings().setDatabaseEnabled(true);
//        String cacheDirPath = context.getFilesDir().getAbsolutePath()+APP_CACAHE_DIRNAME;
//        //      String cacheDirPath = getCacheDir().getAbsolutePath()+Constant.APP_DB_DIRNAME;
//        //设置数据库缓存路径
//        getSettings().setDatabasePath(cacheDirPath);
//        //设置  Application Caches 缓存目录
//        getSettings().setAppCachePath(cacheDirPath);
//        //开启 Application Caches 功能
//        getSettings().setAppCacheEnabled(true);
    }

    /**
     * This is where the magic happens :D
     */
    private void getControlls() {
        try {
            Class webview = Class.forName("android.webkit.WebView");
            Method method = webview.getMethod("getZoomButtonsController");
            ZoomButtonsController zoom_controll = (ZoomButtonsController) method.invoke(this, true);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public class WebChromeClient extends android.webkit.WebChromeClient {
        @Override
        public void onProgressChanged(WebView view, int newProgress) {
            if(progressListener != null) {
                progressListener.onProgress(newProgress);
            }
            if (newProgress >= 100) {
                progressbar.setVisibility(GONE);
            } else {
                if (progressbar.getVisibility() == GONE)
                    progressbar.setVisibility(VISIBLE);
                progressbar.setProgress(newProgress);
            }
            super.onProgressChanged(view, newProgress);
        }

        public boolean onConsoleMessage(ConsoleMessage msg) {// API 8
            return true;
        }

        @Override
        public void onReceivedTitle(WebView view, String title) {
            super.onReceivedTitle(view, title);
            if (listener != null) {
                listener.onReceivedTitle(title);
            }
        }
    }

    @Override
    protected void onScrollChanged(int l, int t, int oldl, int oldt) {
        LayoutParams lp = (LayoutParams) progressbar.getLayoutParams();
        lp.x = l;
        lp.y = t;
        progressbar.setLayoutParams(lp);
        super.onScrollChanged(l, t, oldl, oldt);
    }

    public class WebViewClient extends android.webkit.WebViewClient {
        @Override
        public boolean shouldOverrideUrlLoading(WebView view, String url) {
            Log.i("test", "url:" + url);
            view.loadUrl(url);
            if (url.startsWith("tel:")) {
                Intent intent = new Intent(Intent.ACTION_VIEW,
                        Uri.parse(url));
                view.getContext().startActivity(intent);
                stopLoading();
            }
            if (onUrlLoadingListener != null) onUrlLoadingListener.shouldOverrideUrlLoading(view, url);
            return true;
        }

        @Override
        public void onReceivedSslError(WebView view, SslErrorHandler handler, SslError error) {
            handler.proceed();
        }

        @Override
        public void onPageFinished(WebView view, String url) {
            super.onPageFinished(view, url);
            if (listener != null) {
                listener.onReceivedTitle(view.getTitle());
            }
        }
    }

    private WebViewListener listener;
    private WebViewProgressListener progressListener;
    private OnUrlLoadingListener onUrlLoadingListener;

    public void setListener(WebViewListener listener) {
        this.listener = listener;
    }

    public void setProgressListener(WebViewProgressListener progressListener) {
        this.progressListener = progressListener;
    }

    public void setOnUrlLoadingListener(OnUrlLoadingListener onUrlLoadingListener) {
        this.onUrlLoadingListener = onUrlLoadingListener;
    }

    public interface WebViewListener {
        void onReceivedTitle(String title);// 获得界面title
    }

    public interface WebViewProgressListener {
        void onProgress(int progress);
    }

    public interface OnUrlLoadingListener {
        void shouldOverrideUrlLoading(WebView view, String url);
    }
}
