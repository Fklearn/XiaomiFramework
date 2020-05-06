package miui.securityspace;

import android.app.Activity;
import android.app.ActivityManager;
import android.content.ComponentName;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.content.Intent;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.content.res.Resources;
import android.graphics.drawable.Drawable;
import android.os.Binder;
import android.os.UserHandle;
import android.provider.MiuiSettings.XSpace;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup.LayoutParams;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import com.android.internal.app.AlertController.AlertParams;
import java.util.ArrayList;

public class XSpaceResolverActivityHelper
{
  private static final String PACKAGE_GOOGLE_VOICEASSIST = "com.google.android.googlequicksearchbox";
  private static final String PACKAGE_VOICEASSIST = "com.miui.voiceassist";
  private static final String TAG = "XSpaceResolverActivity";
  private static final String XSPACE_SERVICE_COMPONENT = "com.miui.securitycore/com.miui.xspace.service.XSpaceService";
  
  public static boolean checkAndResolve(Activity paramActivity, Intent paramIntent, AlertController.AlertParams paramAlertParams)
  {
    if ((paramIntent != null) && ("miui.intent.action.ACTION_XSPACE_RESOLVER_ACTIVITY".equals(paramIntent.getAction())))
    {
      new ResolverActivityRunner(paramActivity, paramIntent, paramAlertParams).run();
      return true;
    }
    return false;
  }
  
  private static class ResolverActivityRunner
    implements Runnable
  {
    private Activity mActivity;
    private String mAimPackageName;
    private int mAimUserId = -1;
    private AlertController.AlertParams mAlertParams;
    private CheckBox mAlwaysOption;
    private int mAskType = 0;
    private String mCallingPackage;
    private Context mContext;
    private int mIconSize;
    private Intent mIntent;
    private String mKeyType;
    private View.OnClickListener mOnClickListener = new View.OnClickListener()
    {
      public void onClick(View paramAnonymousView)
      {
        XSpaceResolverActivityHelper.ResolverActivityRunner localResolverActivityRunner = XSpaceResolverActivityHelper.ResolverActivityRunner.this;
        int i;
        if (paramAnonymousView.getId() == 285802523) {
          i = 0;
        } else {
          i = 999;
        }
        XSpaceResolverActivityHelper.ResolverActivityRunner.access$102(localResolverActivityRunner, i);
        if ((XSpaceResolverActivityHelper.ResolverActivityRunner.this.mAlwaysOption != null) && (XSpaceResolverActivityHelper.ResolverActivityRunner.this.mAlwaysOption.isChecked()))
        {
          localResolverActivityRunner = XSpaceResolverActivityHelper.ResolverActivityRunner.this;
          if (paramAnonymousView.getId() == 285802523) {
            i = 1;
          } else {
            i = 2;
          }
          XSpaceResolverActivityHelper.ResolverActivityRunner.access$302(localResolverActivityRunner, i);
          MiuiSettings.XSpace.setAskType(XSpaceResolverActivityHelper.ResolverActivityRunner.this.mContext, XSpaceResolverActivityHelper.ResolverActivityRunner.this.mKeyType, XSpaceResolverActivityHelper.ResolverActivityRunner.this.mAskType);
        }
        paramAnonymousView = XSpaceResolverActivityHelper.ResolverActivityRunner.this;
        paramAnonymousView.forward(paramAnonymousView.mAimUserId);
      }
    };
    private Intent mOriginalIntent;
    private View mRootView;
    
    public ResolverActivityRunner(Activity paramActivity, Intent paramIntent, AlertController.AlertParams paramAlertParams)
    {
      this.mActivity = paramActivity;
      this.mContext = this.mActivity.getApplicationContext();
      this.mIntent = paramIntent;
      this.mAlertParams = paramAlertParams;
    }
    
    private void forward(int paramInt)
    {
      this.mOriginalIntent.putExtra("android.intent.extra.picked_user_id", paramInt);
      CrossUserUtilsCompat.startActivityAsCaller(this.mActivity, this.mOriginalIntent, null, false, paramInt);
      this.mActivity.finish();
    }
    
    private Drawable getAppIcon()
    {
      return CrossUserUtils.getOriginalAppIcon(this.mContext, this.mAimPackageName);
    }
    
    private void loadItem(int paramInt, Drawable paramDrawable, CharSequence paramCharSequence)
    {
      LinearLayout localLinearLayout = (LinearLayout)this.mRootView.findViewById(paramInt);
      ImageView localImageView = (ImageView)localLinearLayout.findViewById(16908294);
      Object localObject = localImageView.getLayoutParams();
      int i = this.mIconSize;
      ((ViewGroup.LayoutParams)localObject).height = i;
      ((ViewGroup.LayoutParams)localObject).width = i;
      TextView localTextView = (TextView)localLinearLayout.findViewById(16908308);
      localTextView.setMinLines(1);
      localObject = new StringBuilder();
      ((StringBuilder)localObject).append(paramCharSequence);
      ((StringBuilder)localObject).append("");
      String str = ((StringBuilder)localObject).toString();
      localObject = paramDrawable;
      if (paramInt == 285802524)
      {
        localObject = XSpaceUserHandle.getXSpaceIcon(this.mContext, paramDrawable);
        paramDrawable = new StringBuilder();
        paramDrawable.append(this.mActivity.getString(286130591));
        paramDrawable.append(paramCharSequence);
        str = paramDrawable.toString();
      }
      localLinearLayout.findViewById(16908309).setVisibility(8);
      localLinearLayout.setOnClickListener(this.mOnClickListener);
      localImageView.setImageDrawable((Drawable)localObject);
      localImageView.setContentDescription(str);
      localTextView.setText(paramCharSequence);
      localTextView.setImportantForAccessibility(2);
    }
    
    private static void startXSpaceServiceAsUser(Context paramContext, int paramInt1, int paramInt2, String paramString)
    {
      Intent localIntent = new Intent();
      localIntent.putExtra("param_intent_key_has_extra", "param_intent_value_has_extra");
      localIntent.putExtra("param_intent_key_default_asktype", paramInt1);
      localIntent.putExtra("package_name", paramString);
      localIntent.setComponent(ComponentName.unflattenFromString("com.miui.securitycore/com.miui.xspace.service.XSpaceService"));
      paramContext.startServiceAsUser(localIntent, new UserHandle(paramInt2));
    }
    
    public CharSequence getAppLabel()
    {
      try
      {
        Object localObject1 = this.mContext.getPackageManager();
        Object localObject2 = ((PackageManager)localObject1).getPackageInfo(this.mAimPackageName, 0);
        if (localObject2 != null)
        {
          localObject2 = ((PackageInfo)localObject2).applicationInfo;
          if (localObject2 != null)
          {
            localObject1 = ((ApplicationInfo)localObject2).loadLabel((PackageManager)localObject1);
            if (localObject1 != null) {
              return (CharSequence)localObject1;
            }
          }
        }
      }
      catch (PackageManager.NameNotFoundException localNameNotFoundException)
      {
        localNameNotFoundException.printStackTrace();
      }
      return this.mAimPackageName;
    }
    
    public boolean needShowDefaultSettingGuide()
    {
      boolean bool;
      if (MiuiSettings.XSpace.getGuideNotificationTimes(this.mContext, "key_default_guide_times") < 2) {
        bool = true;
      } else {
        bool = false;
      }
      return bool;
    }
    
    public void run()
    {
      this.mOriginalIntent = ((Intent)this.mIntent.getParcelableExtra("android.intent.extra.xspace_resolver_activity_original_intent"));
      this.mAimPackageName = this.mIntent.getStringExtra("android.intent.extra.xspace_resolver_activity_aim_package");
      Object localObject = this.mOriginalIntent;
      if ((localObject != null) && (((Intent)localObject).getComponent() != null) && (this.mOriginalIntent.getComponent().getClassName() != null))
      {
        this.mCallingPackage = this.mOriginalIntent.getStringExtra("miui.intent.extra.xspace_resolver_activity_calling_package");
        if ((!this.mCallingPackage.equals("com.miui.voiceassist")) && (!this.mCallingPackage.equals("com.google.android.googlequicksearchbox")))
        {
          this.mKeyType = this.mOriginalIntent.getComponent().getClassName();
        }
        else
        {
          localObject = new StringBuilder();
          ((StringBuilder)localObject).append(this.mCallingPackage);
          ((StringBuilder)localObject).append("-");
          ((StringBuilder)localObject).append(this.mAimPackageName);
          this.mKeyType = ((StringBuilder)localObject).toString();
        }
        this.mAskType = MiuiSettings.XSpace.getAskType(this.mContext, this.mKeyType);
        if (this.mAskType != 0)
        {
          XSpaceIntentCompat.prepareToLeaveUser(this.mOriginalIntent, Binder.getCallingUserHandle().getIdentifier());
          int i;
          if (this.mAskType == 1) {
            i = 0;
          } else {
            i = 999;
          }
          this.mAimUserId = i;
          if (needShowDefaultSettingGuide()) {
            startXSpaceServiceAsUser(this.mContext, this.mAskType, 0, this.mAimPackageName);
          }
          localObject = new StringBuilder();
          ((StringBuilder)localObject).append("Direct to ");
          ((StringBuilder)localObject).append(this.mAimUserId);
          Log.i("XSpaceResolverActivity", ((StringBuilder)localObject).toString());
          forward(this.mAimUserId);
        }
      }
      localObject = this.mAlertParams;
      ((AlertController.AlertParams)localObject).mMessage = null;
      ((AlertController.AlertParams)localObject).mTitle = this.mActivity.getString(286130592);
      this.mRootView = this.mActivity.getLayoutInflater().inflate(285933617, null);
      localObject = this.mAlertParams;
      ((AlertController.AlertParams)localObject).mView = this.mRootView;
      ((AlertController.AlertParams)localObject).mNegativeButtonText = this.mActivity.getResources().getString(17039360);
      this.mAlertParams.mNegativeButtonListener = new DialogInterface.OnClickListener()
      {
        public void onClick(DialogInterface paramAnonymousDialogInterface, int paramAnonymousInt)
        {
          XSpaceResolverActivityHelper.ResolverActivityRunner.this.mActivity.finish();
        }
      };
      this.mIconSize = ((ActivityManager)this.mContext.getSystemService("activity")).getLauncherLargeIconSize();
      if ((this.mOriginalIntent != null) && (this.mAimPackageName != null))
      {
        localObject = getAppIcon();
        CharSequence localCharSequence = getAppLabel();
        loadItem(285802523, (Drawable)localObject, localCharSequence);
        loadItem(285802524, (Drawable)localObject, localCharSequence);
      }
      this.mAlwaysOption = ((CheckBox)this.mRootView.findViewById(285802504));
      if ((this.mAlwaysOption != null) && (MiuiSettings.XSpace.sSupportDefaultSettingApps.contains(this.mKeyType)))
      {
        this.mAlwaysOption.setVisibility(0);
        this.mAlwaysOption.setChecked(false);
      }
    }
  }
}


/* Location:              /Users/sanbo/Desktop/framework/miui/framework/classes4-dex2jar.jar!/miui/securityspace/XSpaceResolverActivityHelper.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */