package android.webkit;

import android.annotation.SystemApi;
import android.content.Intent;
import android.content.res.Configuration;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Picture;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.net.http.SslCertificate;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.print.PrintDocumentAdapter;
import android.util.SparseArray;
import android.view.DragEvent;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup.LayoutParams;
import android.view.ViewStructure;
import android.view.accessibility.AccessibilityEvent;
import android.view.accessibility.AccessibilityNodeInfo;
import android.view.accessibility.AccessibilityNodeProvider;
import android.view.autofill.AutofillValue;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputConnection;
import android.view.textclassifier.TextClassifier;
import java.io.BufferedWriter;
import java.io.File;
import java.util.Map;
import java.util.concurrent.Executor;

@SystemApi
public abstract interface WebViewProvider
{
  public abstract void addJavascriptInterface(Object paramObject, String paramString);
  
  public abstract boolean canGoBack();
  
  public abstract boolean canGoBackOrForward(int paramInt);
  
  public abstract boolean canGoForward();
  
  public abstract boolean canZoomIn();
  
  public abstract boolean canZoomOut();
  
  public abstract Picture capturePicture();
  
  public abstract void clearCache(boolean paramBoolean);
  
  public abstract void clearFormData();
  
  public abstract void clearHistory();
  
  public abstract void clearMatches();
  
  public abstract void clearSslPreferences();
  
  public abstract void clearView();
  
  public abstract WebBackForwardList copyBackForwardList();
  
  public abstract PrintDocumentAdapter createPrintDocumentAdapter(String paramString);
  
  public abstract WebMessagePort[] createWebMessageChannel();
  
  public abstract void destroy();
  
  public abstract void documentHasImages(Message paramMessage);
  
  public abstract void dumpViewHierarchyWithProperties(BufferedWriter paramBufferedWriter, int paramInt);
  
  public abstract void evaluateJavaScript(String paramString, ValueCallback<String> paramValueCallback);
  
  public abstract int findAll(String paramString);
  
  public abstract void findAllAsync(String paramString);
  
  public abstract View findHierarchyView(String paramString, int paramInt);
  
  public abstract void findNext(boolean paramBoolean);
  
  public abstract void flingScroll(int paramInt1, int paramInt2);
  
  public abstract void freeMemory();
  
  public abstract SslCertificate getCertificate();
  
  public abstract int getContentHeight();
  
  public abstract int getContentWidth();
  
  public abstract Bitmap getFavicon();
  
  public abstract WebView.HitTestResult getHitTestResult();
  
  public abstract String[] getHttpAuthUsernamePassword(String paramString1, String paramString2);
  
  public abstract String getOriginalUrl();
  
  public abstract int getProgress();
  
  public abstract boolean getRendererPriorityWaivedWhenNotVisible();
  
  public abstract int getRendererRequestedPriority();
  
  public abstract float getScale();
  
  public abstract ScrollDelegate getScrollDelegate();
  
  public abstract WebSettings getSettings();
  
  public TextClassifier getTextClassifier()
  {
    return TextClassifier.NO_OP;
  }
  
  public abstract String getTitle();
  
  public abstract String getTouchIconUrl();
  
  public abstract String getUrl();
  
  public abstract ViewDelegate getViewDelegate();
  
  public abstract int getVisibleTitleHeight();
  
  public abstract WebChromeClient getWebChromeClient();
  
  public abstract WebViewClient getWebViewClient();
  
  public abstract WebViewRenderProcess getWebViewRenderProcess();
  
  public abstract WebViewRenderProcessClient getWebViewRenderProcessClient();
  
  public abstract View getZoomControls();
  
  public abstract void goBack();
  
  public abstract void goBackOrForward(int paramInt);
  
  public abstract void goForward();
  
  public abstract void init(Map<String, Object> paramMap, boolean paramBoolean);
  
  public abstract void insertVisualStateCallback(long paramLong, WebView.VisualStateCallback paramVisualStateCallback);
  
  public abstract void invokeZoomPicker();
  
  public abstract boolean isPaused();
  
  public abstract boolean isPrivateBrowsingEnabled();
  
  public abstract void loadData(String paramString1, String paramString2, String paramString3);
  
  public abstract void loadDataWithBaseURL(String paramString1, String paramString2, String paramString3, String paramString4, String paramString5);
  
  public abstract void loadUrl(String paramString);
  
  public abstract void loadUrl(String paramString, Map<String, String> paramMap);
  
  public abstract void notifyFindDialogDismissed();
  
  public abstract void onPause();
  
  public abstract void onResume();
  
  public abstract boolean overlayHorizontalScrollbar();
  
  public abstract boolean overlayVerticalScrollbar();
  
  public abstract boolean pageDown(boolean paramBoolean);
  
  public abstract boolean pageUp(boolean paramBoolean);
  
  public abstract void pauseTimers();
  
  public abstract void postMessageToMainFrame(WebMessage paramWebMessage, Uri paramUri);
  
  public abstract void postUrl(String paramString, byte[] paramArrayOfByte);
  
  public abstract void reload();
  
  public abstract void removeJavascriptInterface(String paramString);
  
  public abstract void requestFocusNodeHref(Message paramMessage);
  
  public abstract void requestImageRef(Message paramMessage);
  
  public abstract boolean restorePicture(Bundle paramBundle, File paramFile);
  
  public abstract WebBackForwardList restoreState(Bundle paramBundle);
  
  public abstract void resumeTimers();
  
  public abstract void savePassword(String paramString1, String paramString2, String paramString3);
  
  public abstract boolean savePicture(Bundle paramBundle, File paramFile);
  
  public abstract WebBackForwardList saveState(Bundle paramBundle);
  
  public abstract void saveWebArchive(String paramString);
  
  public abstract void saveWebArchive(String paramString, boolean paramBoolean, ValueCallback<String> paramValueCallback);
  
  public abstract void setCertificate(SslCertificate paramSslCertificate);
  
  public abstract void setDownloadListener(DownloadListener paramDownloadListener);
  
  public abstract void setFindListener(WebView.FindListener paramFindListener);
  
  public abstract void setHorizontalScrollbarOverlay(boolean paramBoolean);
  
  public abstract void setHttpAuthUsernamePassword(String paramString1, String paramString2, String paramString3, String paramString4);
  
  public abstract void setInitialScale(int paramInt);
  
  public abstract void setMapTrackballToArrowKeys(boolean paramBoolean);
  
  public abstract void setNetworkAvailable(boolean paramBoolean);
  
  public abstract void setPictureListener(WebView.PictureListener paramPictureListener);
  
  public abstract void setRendererPriorityPolicy(int paramInt, boolean paramBoolean);
  
  public void setTextClassifier(TextClassifier paramTextClassifier) {}
  
  public abstract void setVerticalScrollbarOverlay(boolean paramBoolean);
  
  public abstract void setWebChromeClient(WebChromeClient paramWebChromeClient);
  
  public abstract void setWebViewClient(WebViewClient paramWebViewClient);
  
  public abstract void setWebViewRenderProcessClient(Executor paramExecutor, WebViewRenderProcessClient paramWebViewRenderProcessClient);
  
  public abstract boolean showFindDialog(String paramString, boolean paramBoolean);
  
  public abstract void stopLoading();
  
  public abstract boolean zoomBy(float paramFloat);
  
  public abstract boolean zoomIn();
  
  public abstract boolean zoomOut();
  
  public static abstract interface ScrollDelegate
  {
    public abstract int computeHorizontalScrollOffset();
    
    public abstract int computeHorizontalScrollRange();
    
    public abstract void computeScroll();
    
    public abstract int computeVerticalScrollExtent();
    
    public abstract int computeVerticalScrollOffset();
    
    public abstract int computeVerticalScrollRange();
  }
  
  public static abstract interface ViewDelegate
  {
    public void autofill(SparseArray<AutofillValue> paramSparseArray) {}
    
    public abstract boolean dispatchKeyEvent(KeyEvent paramKeyEvent);
    
    public abstract View findFocus(View paramView);
    
    public abstract AccessibilityNodeProvider getAccessibilityNodeProvider();
    
    public abstract Handler getHandler(Handler paramHandler);
    
    public boolean isVisibleToUserForAutofill(int paramInt)
    {
      return true;
    }
    
    public abstract void onActivityResult(int paramInt1, int paramInt2, Intent paramIntent);
    
    public abstract void onAttachedToWindow();
    
    public boolean onCheckIsTextEditor()
    {
      return false;
    }
    
    public abstract void onConfigurationChanged(Configuration paramConfiguration);
    
    public abstract InputConnection onCreateInputConnection(EditorInfo paramEditorInfo);
    
    public abstract void onDetachedFromWindow();
    
    public abstract boolean onDragEvent(DragEvent paramDragEvent);
    
    public abstract void onDraw(Canvas paramCanvas);
    
    public abstract void onDrawVerticalScrollBar(Canvas paramCanvas, Drawable paramDrawable, int paramInt1, int paramInt2, int paramInt3, int paramInt4);
    
    public abstract void onFinishTemporaryDetach();
    
    public abstract void onFocusChanged(boolean paramBoolean, int paramInt, Rect paramRect);
    
    public abstract boolean onGenericMotionEvent(MotionEvent paramMotionEvent);
    
    public abstract boolean onHoverEvent(MotionEvent paramMotionEvent);
    
    public abstract void onInitializeAccessibilityEvent(AccessibilityEvent paramAccessibilityEvent);
    
    public abstract void onInitializeAccessibilityNodeInfo(AccessibilityNodeInfo paramAccessibilityNodeInfo);
    
    public abstract boolean onKeyDown(int paramInt, KeyEvent paramKeyEvent);
    
    public abstract boolean onKeyMultiple(int paramInt1, int paramInt2, KeyEvent paramKeyEvent);
    
    public abstract boolean onKeyUp(int paramInt, KeyEvent paramKeyEvent);
    
    public abstract void onMeasure(int paramInt1, int paramInt2);
    
    public void onMovedToDisplay(int paramInt, Configuration paramConfiguration) {}
    
    public abstract void onOverScrolled(int paramInt1, int paramInt2, boolean paramBoolean1, boolean paramBoolean2);
    
    public void onProvideAutofillVirtualStructure(ViewStructure paramViewStructure, int paramInt) {}
    
    public void onProvideContentCaptureStructure(ViewStructure paramViewStructure, int paramInt) {}
    
    public abstract void onProvideVirtualStructure(ViewStructure paramViewStructure);
    
    public abstract void onScrollChanged(int paramInt1, int paramInt2, int paramInt3, int paramInt4);
    
    public abstract void onSizeChanged(int paramInt1, int paramInt2, int paramInt3, int paramInt4);
    
    public abstract void onStartTemporaryDetach();
    
    public abstract boolean onTouchEvent(MotionEvent paramMotionEvent);
    
    public abstract boolean onTrackballEvent(MotionEvent paramMotionEvent);
    
    public abstract void onVisibilityChanged(View paramView, int paramInt);
    
    public abstract void onWindowFocusChanged(boolean paramBoolean);
    
    public abstract void onWindowVisibilityChanged(int paramInt);
    
    public abstract boolean performAccessibilityAction(int paramInt, Bundle paramBundle);
    
    public abstract boolean performLongClick();
    
    public abstract void preDispatchDraw(Canvas paramCanvas);
    
    public abstract boolean requestChildRectangleOnScreen(View paramView, Rect paramRect, boolean paramBoolean);
    
    public abstract boolean requestFocus(int paramInt, Rect paramRect);
    
    public abstract void setBackgroundColor(int paramInt);
    
    public abstract boolean setFrame(int paramInt1, int paramInt2, int paramInt3, int paramInt4);
    
    public abstract void setLayerType(int paramInt, Paint paramPaint);
    
    public abstract void setLayoutParams(ViewGroup.LayoutParams paramLayoutParams);
    
    public abstract void setOverScrollMode(int paramInt);
    
    public abstract void setScrollBarStyle(int paramInt);
    
    public abstract boolean shouldDelayChildPressedState();
  }
}


/* Location:              /Users/sanbo/Desktop/framework/miui/framework/classes3-dex2jar.jar!/android/webkit/WebViewProvider.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */