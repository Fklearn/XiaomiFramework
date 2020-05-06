package com.miui.maml.elements;

import android.content.Context;
import android.graphics.Canvas;
import android.os.Handler;
import android.text.TextUtils;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.JavascriptInterface;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import com.miui.maml.ScreenElementRoot;
import com.miui.maml.data.Expression;
import com.miui.maml.data.Variables;
import com.miui.maml.util.TextFormatter;
import miui.net.ConnectivityHelper;
import org.w3c.dom.Element;

public class WebViewScreenElement extends AnimatedScreenElement {
    private static final String LOG_TAG = "MAML WebViewScreenElement";
    public static final String TAG_NAME = "WebView";
    private static final int USE_NETWORK_ALL = 2;
    private static final int USE_NETWORK_WIFI = 1;
    /* access modifiers changed from: private */
    public boolean mCachePage;
    private String mCurUrl;
    private Handler mHandler;
    /* access modifiers changed from: private */
    public ViewGroup.LayoutParams mLayoutParams;
    private TextFormatter mUriFormatter;
    private int mUseNetwork = 2;
    private Expression mUseNetworkExp;
    /* access modifiers changed from: private */
    public boolean mViewAdded;
    /* access modifiers changed from: private */
    public WebView mWebView;
    private Context mWindowContext;

    private class MamlInterface {
        private MamlInterface() {
        }

        @JavascriptInterface
        public void doAction(String str) {
            WebViewScreenElement.this.performAction(str);
        }

        @JavascriptInterface
        public double getDouble(int i) {
            return WebViewScreenElement.this.getVariables().getDouble(i);
        }

        @JavascriptInterface
        public double getDouble(String str) {
            return WebViewScreenElement.this.getVariables().getDouble(str);
        }

        @JavascriptInterface
        public Object getObj(int i) {
            return WebViewScreenElement.this.getVariables().get(i);
        }

        @JavascriptInterface
        public Object getObj(String str) {
            return WebViewScreenElement.this.getVariables().get(str);
        }

        @JavascriptInterface
        public String getString(int i) {
            return WebViewScreenElement.this.getVariables().getString(i);
        }

        @JavascriptInterface
        public String getString(String str) {
            return WebViewScreenElement.this.getVariables().getString(str);
        }

        @JavascriptInterface
        public void putDouble(String str, double d2) {
            WebViewScreenElement.this.getVariables().put(str, d2);
        }

        @JavascriptInterface
        public void putInt(String str, int i) {
            WebViewScreenElement.this.getVariables().put(str, (double) i);
        }

        @JavascriptInterface
        public void putObj(String str, Object obj) {
            WebViewScreenElement.this.getVariables().put(str, obj);
        }

        @JavascriptInterface
        public void putString(String str, String str2) {
            WebViewScreenElement.this.getVariables().put(str, (Object) str2);
        }

        @JavascriptInterface
        public int registerDoubleVariable(String str) {
            return WebViewScreenElement.this.getVariables().registerDoubleVariable(str);
        }

        @JavascriptInterface
        public int registerVariable(String str) {
            return WebViewScreenElement.this.getVariables().registerVariable(str);
        }
    }

    public WebViewScreenElement(Element element, ScreenElementRoot screenElementRoot) {
        super(element, screenElementRoot);
        this.mWindowContext = screenElementRoot.getContext().mContext;
        this.mWebView = new WebView(this.mWindowContext);
        this.mWebView.setWebViewClient(new WebViewClient() {
            public boolean shouldOverrideUrlLoading(WebView webView, String str) {
                webView.loadUrl(str);
                return true;
            }
        });
        this.mWebView.setOnTouchListener(new View.OnTouchListener() {
            public boolean onTouch(View view, MotionEvent motionEvent) {
                WebViewScreenElement webViewScreenElement = WebViewScreenElement.this;
                webViewScreenElement.mRoot.onUIInteractive(webViewScreenElement, "touch");
                return false;
            }
        });
        this.mWebView.getSettings().setJavaScriptEnabled(true);
        this.mWebView.setInitialScale(100);
        String attribute = element.getAttribute("userAgent");
        if (!TextUtils.isEmpty(attribute)) {
            this.mWebView.getSettings().setUserAgentString(attribute);
        }
        this.mWebView.addJavascriptInterface(new MamlInterface(), "maml");
        this.mLayoutParams = new ViewGroup.LayoutParams(-1, -1);
        this.mHandler = getContext().getHandler();
        Variables variables = getVariables();
        this.mUriFormatter = new TextFormatter(variables, element.getAttribute("uri"), Expression.build(variables, element.getAttribute("uriExp")));
        this.mCachePage = Boolean.parseBoolean(element.getAttribute("cachePage"));
        String attribute2 = element.getAttribute("useNetwork");
        if (TextUtils.isEmpty(attribute2) || "all".equalsIgnoreCase(attribute2)) {
            this.mUseNetwork = 2;
        } else if ("wifi".equalsIgnoreCase(attribute2)) {
            this.mUseNetwork = 1;
        } else {
            this.mUseNetworkExp = Expression.build(variables, attribute2);
        }
    }

    private boolean canUseNetwork() {
        int i = this.mUseNetwork;
        if (i == 2) {
            return true;
        }
        return i == 1 && ConnectivityHelper.getInstance().isWifiConnected();
    }

    private final void finishWebView() {
        this.mHandler.post(new Runnable() {
            public void run() {
                WebViewScreenElement.this.mRoot.getViewManager().removeView(WebViewScreenElement.this.mWebView);
                boolean unused = WebViewScreenElement.this.mViewAdded = false;
                if (WebViewScreenElement.this.mCachePage) {
                    WebViewScreenElement.this.mWebView.onPause();
                } else {
                    WebViewScreenElement.this.mWebView.loadUrl("about:blank");
                }
            }
        });
    }

    private final void initWebView() {
        if (!this.mViewAdded || this.mCachePage) {
            this.mHandler.post(new Runnable() {
                public void run() {
                    if (!WebViewScreenElement.this.mViewAdded) {
                        WebViewScreenElement webViewScreenElement = WebViewScreenElement.this;
                        boolean unused = webViewScreenElement.updateLayoutParams(webViewScreenElement.mLayoutParams);
                        Log.d(WebViewScreenElement.LOG_TAG, "addWebView");
                        WebViewScreenElement.this.mRoot.getViewManager().addView(WebViewScreenElement.this.mWebView, WebViewScreenElement.this.mLayoutParams);
                        boolean unused2 = WebViewScreenElement.this.mViewAdded = true;
                    } else if (WebViewScreenElement.this.mCachePage) {
                        WebViewScreenElement.this.mWebView.onResume();
                    }
                }
            });
        }
    }

    private void pauseWebView(final boolean z) {
        this.mHandler.post(new Runnable() {
            public void run() {
                if (z) {
                    WebViewScreenElement.this.mWebView.onPause();
                } else {
                    WebViewScreenElement.this.mWebView.onResume();
                }
            }
        });
    }

    /* access modifiers changed from: private */
    public boolean updateLayoutParams(ViewGroup.LayoutParams layoutParams) {
        boolean z;
        int width = (int) getWidth();
        if (layoutParams.width != width) {
            layoutParams.width = width;
            z = true;
        } else {
            z = false;
        }
        int height = (int) getHeight();
        if (layoutParams.height == height) {
            return z;
        }
        layoutParams.height = height;
        return true;
    }

    private final void updateView() {
        if (this.mViewAdded) {
            this.mWebView.setX(getAbsoluteLeft());
            this.mWebView.setY(getAbsoluteTop());
            if (updateLayoutParams(this.mLayoutParams)) {
                this.mWebView.setLayoutParams(this.mLayoutParams);
            }
        }
    }

    /* access modifiers changed from: protected */
    public void doRender(Canvas canvas) {
    }

    /* access modifiers changed from: protected */
    public void doTick(long j) {
        super.doTick(j);
        String text = this.mUriFormatter.getText();
        if (!TextUtils.isEmpty(text) && !TextUtils.equals(this.mCurUrl, text)) {
            Log.d(LOG_TAG, "loadUrl: " + text);
            loadUrl(text);
        }
        updateView();
    }

    public void finish() {
        super.finish();
        finishWebView();
        if (!this.mCachePage) {
            this.mCurUrl = null;
        }
    }

    public void goBack() {
        this.mHandler.post(new Runnable() {
            public void run() {
                WebViewScreenElement.this.mWebView.goBack();
            }
        });
    }

    public void init() {
        super.init();
        Expression expression = this.mUseNetworkExp;
        if (expression != null) {
            this.mUseNetwork = (int) expression.evaluate();
        }
        if (this.mRoot.getViewManager() != null) {
            initWebView();
        } else {
            Log.e(LOG_TAG, "ViewManager must be set before init");
        }
    }

    public void loadUrl(final String str) {
        if (canUseNetwork() || !str.startsWith("http")) {
            this.mCurUrl = str;
            this.mHandler.post(new Runnable() {
                public void run() {
                    WebViewScreenElement.this.mWebView.loadUrl(str);
                }
            });
            return;
        }
        Log.d(LOG_TAG, "loadUrl canceled due to useNetwork setting." + str);
    }

    /* access modifiers changed from: protected */
    public void onVisibilityChange(final boolean z) {
        super.onVisibilityChange(z);
        this.mHandler.post(new Runnable() {
            public void run() {
                WebViewScreenElement.this.mWebView.setVisibility(z ? 0 : 4);
            }
        });
    }

    public void pause() {
        super.pause();
        if (this.mViewAdded) {
            pauseWebView(true);
        }
    }

    public void reload() {
        this.mHandler.post(new Runnable() {
            public void run() {
                WebViewScreenElement.this.mWebView.reload();
            }
        });
    }

    public void render(Canvas canvas) {
    }

    public void resume() {
        super.resume();
        if (this.mViewAdded) {
            pauseWebView(false);
        }
    }

    public void runjs(final String str) {
        this.mHandler.post(new Runnable() {
            public void run() {
                WebView access$100 = WebViewScreenElement.this.mWebView;
                access$100.loadUrl("javascript:" + str);
            }
        });
    }
}
