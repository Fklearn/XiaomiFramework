package android.webkit;

import android.content.Context;
import org.egret.plugin.mi.runtime.EgretLoader;

class WebViewInjector
{
  static void initEgretLoader(WebView paramWebView, Context paramContext)
  {
    paramContext = new EgretLoader(paramContext);
    if (!paramContext.checkEgretContext()) {
      paramWebView.addJavascriptInterface(paramContext, "GameEngine");
    }
  }
}


/* Location:              /Users/sanbo/Desktop/framework/miui/framework/classes3-dex2jar.jar!/android/webkit/WebViewInjector.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */