package android.webkit;

import android.net.Uri;

public abstract class PermissionRequest
{
  public static final String RESOURCE_AUDIO_CAPTURE = "android.webkit.resource.AUDIO_CAPTURE";
  public static final String RESOURCE_MIDI_SYSEX = "android.webkit.resource.MIDI_SYSEX";
  public static final String RESOURCE_PROTECTED_MEDIA_ID = "android.webkit.resource.PROTECTED_MEDIA_ID";
  public static final String RESOURCE_VIDEO_CAPTURE = "android.webkit.resource.VIDEO_CAPTURE";
  
  public abstract void deny();
  
  public abstract Uri getOrigin();
  
  public abstract String[] getResources();
  
  public abstract void grant(String[] paramArrayOfString);
}


/* Location:              /Users/sanbo/Desktop/framework/miui/framework/classes3-dex2jar.jar!/android/webkit/PermissionRequest.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */