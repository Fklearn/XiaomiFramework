package android.webkit;

import android.app.AlertDialog;
import android.app.AlertDialog.Builder;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;

@Deprecated
public class Plugin
{
  private String mDescription;
  private String mFileName;
  private PreferencesClickHandler mHandler;
  private String mName;
  private String mPath;
  
  @Deprecated
  public Plugin(String paramString1, String paramString2, String paramString3, String paramString4)
  {
    this.mName = paramString1;
    this.mPath = paramString2;
    this.mFileName = paramString3;
    this.mDescription = paramString4;
    this.mHandler = new DefaultClickHandler(null);
  }
  
  @Deprecated
  public void dispatchClickEvent(Context paramContext)
  {
    PreferencesClickHandler localPreferencesClickHandler = this.mHandler;
    if (localPreferencesClickHandler != null) {
      localPreferencesClickHandler.handleClickEvent(paramContext);
    }
  }
  
  @Deprecated
  public String getDescription()
  {
    return this.mDescription;
  }
  
  @Deprecated
  public String getFileName()
  {
    return this.mFileName;
  }
  
  @Deprecated
  public String getName()
  {
    return this.mName;
  }
  
  @Deprecated
  public String getPath()
  {
    return this.mPath;
  }
  
  @Deprecated
  public void setClickHandler(PreferencesClickHandler paramPreferencesClickHandler)
  {
    this.mHandler = paramPreferencesClickHandler;
  }
  
  @Deprecated
  public void setDescription(String paramString)
  {
    this.mDescription = paramString;
  }
  
  @Deprecated
  public void setFileName(String paramString)
  {
    this.mFileName = paramString;
  }
  
  @Deprecated
  public void setName(String paramString)
  {
    this.mName = paramString;
  }
  
  @Deprecated
  public void setPath(String paramString)
  {
    this.mPath = paramString;
  }
  
  @Deprecated
  public String toString()
  {
    return this.mName;
  }
  
  @Deprecated
  private class DefaultClickHandler
    implements Plugin.PreferencesClickHandler, DialogInterface.OnClickListener
  {
    private AlertDialog mDialog;
    
    private DefaultClickHandler() {}
    
    @Deprecated
    public void handleClickEvent(Context paramContext)
    {
      if (this.mDialog == null) {
        this.mDialog = new AlertDialog.Builder(paramContext).setTitle(Plugin.this.mName).setMessage(Plugin.this.mDescription).setPositiveButton(17039370, this).setCancelable(false).show();
      }
    }
    
    @Deprecated
    public void onClick(DialogInterface paramDialogInterface, int paramInt)
    {
      this.mDialog.dismiss();
      this.mDialog = null;
    }
  }
  
  public static abstract interface PreferencesClickHandler
  {
    public abstract void handleClickEvent(Context paramContext);
  }
}


/* Location:              /Users/sanbo/Desktop/framework/miui/framework/classes3-dex2jar.jar!/android/webkit/Plugin.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */