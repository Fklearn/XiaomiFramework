package miui.securityspace;

import android.app.Fragment;
import android.content.ContentResolver;
import android.content.Context;
import android.content.ContextWrapper;
import android.content.Intent;
import android.os.Bundle;
import android.os.UserHandle;
import android.util.Log;
import miui.app.Activity;

public class CrossUserPickerActivity
  extends Activity
{
  private static final String TAG = "CrossUserPickerActivity";
  public static final int USER_ID_INVALID = -1;
  private volatile ContentResolver mCrossUserContentResolver;
  private volatile ContextWrapper mCrossUserContextWrapper;
  private final Object mLockObject = new Object();
  
  private boolean validateCallingPackage()
  {
    return (getPackageName().equals(getCallingPackage())) || (CrossUserUtils.checkUidPermission(this, getCallingPackage()));
  }
  
  private int validateCrossUser()
  {
    if (getIntent() == null) {
      return -1;
    }
    int i = getIntent().getIntExtra("android.intent.extra.picked_user_id", -1);
    if (validateCallingPackage()) {
      return i;
    }
    return -1;
  }
  
  public Context getApplicationContext()
  {
    if (isCrossUserPick())
    {
      if (this.mCrossUserContextWrapper == null) {
        synchronized (this.mLockObject)
        {
          if (this.mCrossUserContextWrapper == null)
          {
            CrossUserContextWrapper localCrossUserContextWrapper = new miui/securityspace/CrossUserPickerActivity$CrossUserContextWrapper;
            Context localContext = super.getApplicationContext();
            UserHandle localUserHandle = new android/os/UserHandle;
            localUserHandle.<init>(validateCrossUser());
            localCrossUserContextWrapper.<init>(this, localContext, localUserHandle);
            this.mCrossUserContextWrapper = localCrossUserContextWrapper;
          }
        }
      }
      Log.d("CrossUserPickerActivity", "getApplicationContext: WrapperedApplication");
      return this.mCrossUserContextWrapper;
    }
    Log.d("CrossUserPickerActivity", "getApplicationContext: NormalApplication");
    return super.getApplicationContext();
  }
  
  public ContentResolver getContentResolver()
  {
    if (isCrossUserPick())
    {
      if (this.mCrossUserContentResolver == null) {
        synchronized (this.mLockObject)
        {
          if (this.mCrossUserContentResolver == null)
          {
            UserHandle localUserHandle = new android/os/UserHandle;
            localUserHandle.<init>(validateCrossUser());
            this.mCrossUserContentResolver = getContentResolverForUser(localUserHandle);
          }
        }
      }
      Log.d("CrossUserPickerActivity", "getContentResolver: CrossUserContentResolver");
      return this.mCrossUserContentResolver;
    }
    Log.d("CrossUserPickerActivity", "getContentResolver: NormalContentResolver");
    return super.getContentResolver();
  }
  
  public boolean isCrossUserPick()
  {
    boolean bool;
    if (validateCrossUser() != -1) {
      bool = true;
    } else {
      bool = false;
    }
    return bool;
  }
  
  public void startActivity(Intent paramIntent)
  {
    if (isCrossUserPick()) {
      paramIntent.putExtra("android.intent.extra.picked_user_id", validateCrossUser());
    }
    super.startActivity(paramIntent);
  }
  
  public void startActivity(Intent paramIntent, Bundle paramBundle)
  {
    if (isCrossUserPick()) {
      paramIntent.putExtra("android.intent.extra.picked_user_id", validateCrossUser());
    }
    super.startActivity(paramIntent, paramBundle);
  }
  
  public void startActivityForResult(Intent paramIntent, int paramInt)
  {
    if (isCrossUserPick()) {
      paramIntent.putExtra("android.intent.extra.picked_user_id", validateCrossUser());
    }
    super.startActivityForResult(paramIntent, paramInt);
  }
  
  public void startActivityForResult(Intent paramIntent, int paramInt, Bundle paramBundle)
  {
    if (isCrossUserPick()) {
      paramIntent.putExtra("android.intent.extra.picked_user_id", validateCrossUser());
    }
    super.startActivityForResult(paramIntent, paramInt, paramBundle);
  }
  
  public void startActivityFromFragment(Fragment paramFragment, Intent paramIntent, int paramInt, Bundle paramBundle)
  {
    if (isCrossUserPick()) {
      paramIntent.putExtra("android.intent.extra.picked_user_id", validateCrossUser());
    }
    super.startActivityFromFragment(paramFragment, paramIntent, paramInt, paramBundle);
  }
  
  class CrossUserContextWrapper
    extends ContextWrapper
  {
    Context mBase;
    UserHandle mCrossUser;
    
    public CrossUserContextWrapper(Context paramContext, UserHandle paramUserHandle)
    {
      super();
      this.mBase = paramContext;
      this.mCrossUser = paramUserHandle;
    }
    
    public ContentResolver getContentResolver()
    {
      return this.mBase.getContentResolverForUser(this.mCrossUser);
    }
  }
}


/* Location:              /Users/sanbo/Desktop/framework/miui/framework/classes4-dex2jar.jar!/miui/securityspace/CrossUserPickerActivity.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */