package android.webkit;

import android.annotation.SystemApi;
import android.app.Activity;
import android.app.AlertDialog.Builder;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnCancelListener;
import android.content.DialogInterface.OnClickListener;
import android.os.Bundle;
import android.os.Message;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import java.net.MalformedURLException;
import java.net.URL;

@SystemApi
public class JsDialogHelper
{
  public static final int ALERT = 1;
  public static final int CONFIRM = 2;
  public static final int PROMPT = 3;
  private static final String TAG = "JsDialogHelper";
  public static final int UNLOAD = 4;
  private final String mDefaultValue;
  private final String mMessage;
  private final JsPromptResult mResult;
  private final int mType;
  private final String mUrl;
  
  public JsDialogHelper(JsPromptResult paramJsPromptResult, int paramInt, String paramString1, String paramString2, String paramString3)
  {
    this.mResult = paramJsPromptResult;
    this.mDefaultValue = paramString1;
    this.mMessage = paramString2;
    this.mType = paramInt;
    this.mUrl = paramString3;
  }
  
  public JsDialogHelper(JsPromptResult paramJsPromptResult, Message paramMessage)
  {
    this.mResult = paramJsPromptResult;
    this.mDefaultValue = paramMessage.getData().getString("default");
    this.mMessage = paramMessage.getData().getString("message");
    this.mType = paramMessage.getData().getInt("type");
    this.mUrl = paramMessage.getData().getString("url");
  }
  
  private static boolean canShowAlertDialog(Context paramContext)
  {
    return paramContext instanceof Activity;
  }
  
  private String getJsDialogTitle(Context paramContext)
  {
    String str = this.mUrl;
    if (URLUtil.isDataUrl(this.mUrl)) {
      paramContext = paramContext.getString(17040204);
    } else {
      try
      {
        URL localURL = new java/net/URL;
        localURL.<init>(this.mUrl);
        StringBuilder localStringBuilder = new java/lang/StringBuilder;
        localStringBuilder.<init>();
        localStringBuilder.append(localURL.getProtocol());
        localStringBuilder.append("://");
        localStringBuilder.append(localURL.getHost());
        paramContext = paramContext.getString(17040203, new Object[] { localStringBuilder.toString() });
      }
      catch (MalformedURLException paramContext)
      {
        paramContext = str;
      }
    }
    return paramContext;
  }
  
  public boolean invokeCallback(WebChromeClient paramWebChromeClient, WebView paramWebView)
  {
    int i = this.mType;
    if (i != 1)
    {
      if (i != 2)
      {
        if (i != 3)
        {
          if (i == 4) {
            return paramWebChromeClient.onJsBeforeUnload(paramWebView, this.mUrl, this.mMessage, this.mResult);
          }
          paramWebChromeClient = new StringBuilder();
          paramWebChromeClient.append("Unexpected type: ");
          paramWebChromeClient.append(this.mType);
          throw new IllegalArgumentException(paramWebChromeClient.toString());
        }
        return paramWebChromeClient.onJsPrompt(paramWebView, this.mUrl, this.mMessage, this.mDefaultValue, this.mResult);
      }
      return paramWebChromeClient.onJsConfirm(paramWebView, this.mUrl, this.mMessage, this.mResult);
    }
    return paramWebChromeClient.onJsAlert(paramWebView, this.mUrl, this.mMessage, this.mResult);
  }
  
  public void showDialog(Context paramContext)
  {
    if (!canShowAlertDialog(paramContext))
    {
      Log.w("JsDialogHelper", "Cannot create a dialog, the WebView context is not an Activity");
      this.mResult.cancel();
      return;
    }
    Object localObject;
    String str;
    int i;
    int j;
    if (this.mType == 4)
    {
      localObject = paramContext.getString(17040202);
      str = paramContext.getString(17040199, new Object[] { this.mMessage });
      i = 17040201;
      j = 17040200;
    }
    else
    {
      localObject = getJsDialogTitle(paramContext);
      str = this.mMessage;
      i = 17039370;
      j = 17039360;
    }
    AlertDialog.Builder localBuilder = new AlertDialog.Builder(paramContext);
    localBuilder.setTitle((CharSequence)localObject);
    localBuilder.setOnCancelListener(new CancelListener(null));
    if (this.mType != 3)
    {
      localBuilder.setMessage(str);
      localBuilder.setPositiveButton(i, new PositiveListener(null));
    }
    else
    {
      paramContext = LayoutInflater.from(paramContext).inflate(17367170, null);
      localObject = (EditText)paramContext.findViewById(16909583);
      ((EditText)localObject).setText(this.mDefaultValue);
      localBuilder.setPositiveButton(i, new PositiveListener((EditText)localObject));
      ((TextView)paramContext.findViewById(16908299)).setText(this.mMessage);
      localBuilder.setView(paramContext);
    }
    if (this.mType != 1) {
      localBuilder.setNegativeButton(j, new CancelListener(null));
    }
    localBuilder.show();
  }
  
  private class CancelListener
    implements DialogInterface.OnCancelListener, DialogInterface.OnClickListener
  {
    private CancelListener() {}
    
    public void onCancel(DialogInterface paramDialogInterface)
    {
      JsDialogHelper.this.mResult.cancel();
    }
    
    public void onClick(DialogInterface paramDialogInterface, int paramInt)
    {
      JsDialogHelper.this.mResult.cancel();
    }
  }
  
  private class PositiveListener
    implements DialogInterface.OnClickListener
  {
    private final EditText mEdit;
    
    public PositiveListener(EditText paramEditText)
    {
      this.mEdit = paramEditText;
    }
    
    public void onClick(DialogInterface paramDialogInterface, int paramInt)
    {
      if (this.mEdit == null) {
        JsDialogHelper.this.mResult.confirm();
      } else {
        JsDialogHelper.this.mResult.confirm(this.mEdit.getText().toString());
      }
    }
  }
}


/* Location:              /Users/sanbo/Desktop/framework/miui/framework/classes3-dex2jar.jar!/android/webkit/JsDialogHelper.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */