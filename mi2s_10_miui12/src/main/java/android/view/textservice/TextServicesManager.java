package android.view.textservice;

import android.annotation.UnsupportedAppUsage;
import android.content.Context;
import android.os.Bundle;
import android.os.RemoteException;
import android.os.ServiceManager;
import android.os.ServiceManager.ServiceNotFoundException;
import android.os.UserHandle;
import com.android.internal.textservice.ISpellCheckerSessionListener;
import com.android.internal.textservice.ITextServicesManager;
import com.android.internal.textservice.ITextServicesManager.Stub;
import java.util.Locale;

public final class TextServicesManager
{
  private static final boolean DBG = false;
  private static final String TAG = TextServicesManager.class.getSimpleName();
  @Deprecated
  private static TextServicesManager sInstance;
  private final ITextServicesManager mService = ITextServicesManager.Stub.asInterface(ServiceManager.getServiceOrThrow("textservices"));
  private final int mUserId;
  
  private TextServicesManager(int paramInt)
    throws ServiceManager.ServiceNotFoundException
  {
    this.mUserId = paramInt;
  }
  
  public static TextServicesManager createInstance(Context paramContext)
    throws ServiceManager.ServiceNotFoundException
  {
    return new TextServicesManager(paramContext.getUserId());
  }
  
  @UnsupportedAppUsage
  public static TextServicesManager getInstance()
  {
    try
    {
      TextServicesManager localTextServicesManager1 = sInstance;
      if (localTextServicesManager1 == null) {
        try
        {
          localTextServicesManager1 = new android/view/textservice/TextServicesManager;
          localTextServicesManager1.<init>(UserHandle.myUserId());
          sInstance = localTextServicesManager1;
        }
        catch (ServiceManager.ServiceNotFoundException localServiceNotFoundException)
        {
          IllegalStateException localIllegalStateException = new java/lang/IllegalStateException;
          localIllegalStateException.<init>(localServiceNotFoundException);
          throw localIllegalStateException;
        }
      }
      TextServicesManager localTextServicesManager2 = sInstance;
      return localTextServicesManager2;
    }
    finally {}
  }
  
  private static String parseLanguageFromLocaleString(String paramString)
  {
    int i = paramString.indexOf('_');
    if (i < 0) {
      return paramString;
    }
    return paramString.substring(0, i);
  }
  
  void finishSpellCheckerService(ISpellCheckerSessionListener paramISpellCheckerSessionListener)
  {
    try
    {
      this.mService.finishSpellCheckerService(this.mUserId, paramISpellCheckerSessionListener);
      return;
    }
    catch (RemoteException paramISpellCheckerSessionListener)
    {
      throw paramISpellCheckerSessionListener.rethrowFromSystemServer();
    }
  }
  
  @UnsupportedAppUsage
  public SpellCheckerInfo getCurrentSpellChecker()
  {
    try
    {
      SpellCheckerInfo localSpellCheckerInfo = this.mService.getCurrentSpellChecker(this.mUserId, null);
      return localSpellCheckerInfo;
    }
    catch (RemoteException localRemoteException)
    {
      throw localRemoteException.rethrowFromSystemServer();
    }
  }
  
  @UnsupportedAppUsage
  public SpellCheckerSubtype getCurrentSpellCheckerSubtype(boolean paramBoolean)
  {
    try
    {
      SpellCheckerSubtype localSpellCheckerSubtype = this.mService.getCurrentSpellCheckerSubtype(this.mUserId, paramBoolean);
      return localSpellCheckerSubtype;
    }
    catch (RemoteException localRemoteException)
    {
      throw localRemoteException.rethrowFromSystemServer();
    }
  }
  
  @UnsupportedAppUsage
  public SpellCheckerInfo[] getEnabledSpellCheckers()
  {
    try
    {
      SpellCheckerInfo[] arrayOfSpellCheckerInfo = this.mService.getEnabledSpellCheckers(this.mUserId);
      return arrayOfSpellCheckerInfo;
    }
    catch (RemoteException localRemoteException)
    {
      throw localRemoteException.rethrowFromSystemServer();
    }
  }
  
  @UnsupportedAppUsage
  public boolean isSpellCheckerEnabled()
  {
    try
    {
      boolean bool = this.mService.isSpellCheckerEnabled(this.mUserId);
      return bool;
    }
    catch (RemoteException localRemoteException)
    {
      throw localRemoteException.rethrowFromSystemServer();
    }
  }
  
  public SpellCheckerSession newSpellCheckerSession(Bundle paramBundle, Locale paramLocale, SpellCheckerSession.SpellCheckerSessionListener paramSpellCheckerSessionListener, boolean paramBoolean)
  {
    if (paramSpellCheckerSessionListener != null)
    {
      if ((!paramBoolean) && (paramLocale == null)) {
        throw new IllegalArgumentException("Locale should not be null if you don't refer settings.");
      }
      if ((paramBoolean) && (!isSpellCheckerEnabled())) {
        return null;
      }
      try
      {
        SpellCheckerInfo localSpellCheckerInfo = this.mService.getCurrentSpellChecker(this.mUserId, null);
        if (localSpellCheckerInfo == null) {
          return null;
        }
        Object localObject1 = null;
        Object localObject2;
        if (paramBoolean)
        {
          localObject1 = getCurrentSpellCheckerSubtype(true);
          if (localObject1 == null) {
            return null;
          }
          localObject2 = localObject1;
          if (paramLocale != null)
          {
            localObject2 = parseLanguageFromLocaleString(((SpellCheckerSubtype)localObject1).getLocale());
            if ((((String)localObject2).length() >= 2) && (paramLocale.getLanguage().equals(localObject2))) {
              localObject2 = localObject1;
            } else {
              return null;
            }
          }
        }
        else
        {
          String str1 = paramLocale.toString();
          int i = 0;
          for (;;)
          {
            localObject2 = localObject1;
            if (i >= localSpellCheckerInfo.getSubtypeCount()) {
              break;
            }
            localObject2 = localSpellCheckerInfo.getSubtypeAt(i);
            Object localObject3 = ((SpellCheckerSubtype)localObject2).getLocale();
            String str2 = parseLanguageFromLocaleString((String)localObject3);
            if (((String)localObject3).equals(str1)) {
              break;
            }
            localObject3 = localObject1;
            if (str2.length() >= 2)
            {
              localObject3 = localObject1;
              if (paramLocale.getLanguage().equals(str2)) {
                localObject3 = localObject2;
              }
            }
            i++;
            localObject1 = localObject3;
          }
        }
        if (localObject2 == null) {
          return null;
        }
        paramLocale = new SpellCheckerSession(localSpellCheckerInfo, this, paramSpellCheckerSessionListener);
        try
        {
          this.mService.getSpellCheckerService(this.mUserId, localSpellCheckerInfo.getId(), ((SpellCheckerSubtype)localObject2).getLocale(), paramLocale.getTextServicesSessionListener(), paramLocale.getSpellCheckerSessionListener(), paramBundle);
          return paramLocale;
        }
        catch (RemoteException paramBundle)
        {
          throw paramBundle.rethrowFromSystemServer();
        }
        throw new NullPointerException();
      }
      catch (RemoteException paramBundle)
      {
        return null;
      }
    }
  }
}


/* Location:              /Users/sanbo/Desktop/framework/miui/framework/classes3-dex2jar.jar!/android/view/textservice/TextServicesManager.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */