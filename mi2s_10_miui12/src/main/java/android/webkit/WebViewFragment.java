package android.webkit;

import android.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

@Deprecated
public class WebViewFragment
  extends Fragment
{
  private boolean mIsWebViewAvailable;
  private WebView mWebView;
  
  public WebView getWebView()
  {
    WebView localWebView;
    if (this.mIsWebViewAvailable) {
      localWebView = this.mWebView;
    } else {
      localWebView = null;
    }
    return localWebView;
  }
  
  public View onCreateView(LayoutInflater paramLayoutInflater, ViewGroup paramViewGroup, Bundle paramBundle)
  {
    paramLayoutInflater = this.mWebView;
    if (paramLayoutInflater != null) {
      paramLayoutInflater.destroy();
    }
    this.mWebView = new WebView(getContext());
    this.mIsWebViewAvailable = true;
    return this.mWebView;
  }
  
  public void onDestroy()
  {
    WebView localWebView = this.mWebView;
    if (localWebView != null)
    {
      localWebView.destroy();
      this.mWebView = null;
    }
    super.onDestroy();
  }
  
  public void onDestroyView()
  {
    this.mIsWebViewAvailable = false;
    super.onDestroyView();
  }
  
  public void onPause()
  {
    super.onPause();
    this.mWebView.onPause();
  }
  
  public void onResume()
  {
    this.mWebView.onResume();
    super.onResume();
  }
}


/* Location:              /Users/sanbo/Desktop/framework/miui/framework/classes3-dex2jar.jar!/android/webkit/WebViewFragment.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */