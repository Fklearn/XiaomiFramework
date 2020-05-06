package miui.util;

import android.app.Notification;
import android.service.notification.StatusBarNotification;

public class NotificationInjector
{
  public static String getChannelId(StatusBarNotification paramStatusBarNotification)
  {
    return paramStatusBarNotification.getNotification().getChannelId();
  }
}


/* Location:              /Users/sanbo/Desktop/framework/miui/framework/classes4-dex2jar.jar!/miui/util/NotificationInjector.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */