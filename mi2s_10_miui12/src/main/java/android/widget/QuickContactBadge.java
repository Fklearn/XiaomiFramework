package android.widget;

import android.annotation.UnsupportedAppUsage;
import android.content.AsyncQueryHandler;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.content.res.TypedArray;
import android.database.Cursor;
import android.graphics.Canvas;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.provider.ContactsContract.CommonDataKinds.Email;
import android.provider.ContactsContract.Contacts;
import android.provider.ContactsContract.PhoneLookup;
import android.provider.ContactsContract.QuickContact;
import android.util.AttributeSet;
import android.view.View;
import android.view.View.OnClickListener;
import com.android.internal.R.styleable;

public class QuickContactBadge
  extends ImageView
  implements View.OnClickListener
{
  static final int EMAIL_ID_COLUMN_INDEX = 0;
  static final String[] EMAIL_LOOKUP_PROJECTION = { "contact_id", "lookup" };
  static final int EMAIL_LOOKUP_STRING_COLUMN_INDEX = 1;
  private static final String EXTRA_URI_CONTENT = "uri_content";
  static final int PHONE_ID_COLUMN_INDEX = 0;
  static final String[] PHONE_LOOKUP_PROJECTION = { "_id", "lookup" };
  static final int PHONE_LOOKUP_STRING_COLUMN_INDEX = 1;
  private static final int TOKEN_EMAIL_LOOKUP = 0;
  private static final int TOKEN_EMAIL_LOOKUP_AND_TRIGGER = 2;
  private static final int TOKEN_PHONE_LOOKUP = 1;
  private static final int TOKEN_PHONE_LOOKUP_AND_TRIGGER = 3;
  private String mContactEmail;
  private String mContactPhone;
  private Uri mContactUri;
  private Drawable mDefaultAvatar;
  protected String[] mExcludeMimes = null;
  private Bundle mExtras = null;
  @UnsupportedAppUsage
  private Drawable mOverlay;
  private String mPrioritizedMimeType;
  private QueryHandler mQueryHandler;
  
  public QuickContactBadge(Context paramContext)
  {
    this(paramContext, null);
  }
  
  public QuickContactBadge(Context paramContext, AttributeSet paramAttributeSet)
  {
    this(paramContext, paramAttributeSet, 0);
  }
  
  public QuickContactBadge(Context paramContext, AttributeSet paramAttributeSet, int paramInt)
  {
    this(paramContext, paramAttributeSet, paramInt, 0);
  }
  
  public QuickContactBadge(Context paramContext, AttributeSet paramAttributeSet, int paramInt1, int paramInt2)
  {
    super(paramContext, paramAttributeSet, paramInt1, paramInt2);
    paramContext = this.mContext.obtainStyledAttributes(R.styleable.Theme);
    this.mOverlay = paramContext.getDrawable(325);
    paramContext.recycle();
    setOnClickListener(this);
  }
  
  private boolean isAssigned()
  {
    boolean bool;
    if ((this.mContactUri == null) && (this.mContactEmail == null) && (this.mContactPhone == null)) {
      bool = false;
    } else {
      bool = true;
    }
    return bool;
  }
  
  private void onContactUriChanged()
  {
    setEnabled(isAssigned());
  }
  
  public void assignContactFromEmail(String paramString, boolean paramBoolean)
  {
    assignContactFromEmail(paramString, paramBoolean, null);
  }
  
  public void assignContactFromEmail(String paramString, boolean paramBoolean, Bundle paramBundle)
  {
    this.mContactEmail = paramString;
    this.mExtras = paramBundle;
    if (!paramBoolean)
    {
      paramString = this.mQueryHandler;
      if (paramString != null)
      {
        paramString.startQuery(0, null, Uri.withAppendedPath(ContactsContract.CommonDataKinds.Email.CONTENT_LOOKUP_URI, Uri.encode(this.mContactEmail)), EMAIL_LOOKUP_PROJECTION, null, null, null);
        return;
      }
    }
    this.mContactUri = null;
    onContactUriChanged();
  }
  
  public void assignContactFromPhone(String paramString, boolean paramBoolean)
  {
    assignContactFromPhone(paramString, paramBoolean, new Bundle());
  }
  
  public void assignContactFromPhone(String paramString, boolean paramBoolean, Bundle paramBundle)
  {
    this.mContactPhone = paramString;
    this.mExtras = paramBundle;
    if (!paramBoolean)
    {
      paramString = this.mQueryHandler;
      if (paramString != null)
      {
        paramString.startQuery(1, null, Uri.withAppendedPath(ContactsContract.PhoneLookup.CONTENT_FILTER_URI, this.mContactPhone), PHONE_LOOKUP_PROJECTION, null, null, null);
        return;
      }
    }
    this.mContactUri = null;
    onContactUriChanged();
  }
  
  public void assignContactUri(Uri paramUri)
  {
    this.mContactUri = paramUri;
    this.mContactEmail = null;
    this.mContactPhone = null;
    onContactUriChanged();
  }
  
  public void drawableHotspotChanged(float paramFloat1, float paramFloat2)
  {
    super.drawableHotspotChanged(paramFloat1, paramFloat2);
    Drawable localDrawable = this.mOverlay;
    if (localDrawable != null) {
      localDrawable.setHotspot(paramFloat1, paramFloat2);
    }
  }
  
  protected void drawableStateChanged()
  {
    super.drawableStateChanged();
    Drawable localDrawable = this.mOverlay;
    if ((localDrawable != null) && (localDrawable.isStateful()) && (localDrawable.setState(getDrawableState()))) {
      invalidateDrawable(localDrawable);
    }
  }
  
  public CharSequence getAccessibilityClassName()
  {
    return QuickContactBadge.class.getName();
  }
  
  protected void onAttachedToWindow()
  {
    super.onAttachedToWindow();
    if (!isInEditMode()) {
      this.mQueryHandler = new QueryHandler(this.mContext.getContentResolver());
    }
  }
  
  public void onClick(View paramView)
  {
    Object localObject = this.mExtras;
    paramView = (View)localObject;
    if (localObject == null) {
      paramView = new Bundle();
    }
    if (this.mContactUri != null)
    {
      ContactsContract.QuickContact.showQuickContact(getContext(), this, this.mContactUri, this.mExcludeMimes, this.mPrioritizedMimeType);
    }
    else
    {
      localObject = this.mContactEmail;
      if ((localObject != null) && (this.mQueryHandler != null))
      {
        paramView.putString("uri_content", (String)localObject);
        this.mQueryHandler.startQuery(2, paramView, Uri.withAppendedPath(ContactsContract.CommonDataKinds.Email.CONTENT_LOOKUP_URI, Uri.encode(this.mContactEmail)), EMAIL_LOOKUP_PROJECTION, null, null, null);
      }
      else
      {
        localObject = this.mContactPhone;
        if ((localObject == null) || (this.mQueryHandler == null)) {
          return;
        }
        paramView.putString("uri_content", (String)localObject);
        this.mQueryHandler.startQuery(3, paramView, Uri.withAppendedPath(ContactsContract.PhoneLookup.CONTENT_FILTER_URI, this.mContactPhone), PHONE_LOOKUP_PROJECTION, null, null, null);
      }
    }
    return;
  }
  
  protected void onDraw(Canvas paramCanvas)
  {
    super.onDraw(paramCanvas);
    if (!isEnabled()) {
      return;
    }
    Drawable localDrawable = this.mOverlay;
    if ((localDrawable != null) && (localDrawable.getIntrinsicWidth() != 0) && (this.mOverlay.getIntrinsicHeight() != 0))
    {
      this.mOverlay.setBounds(0, 0, getWidth(), getHeight());
      if ((this.mPaddingTop == 0) && (this.mPaddingLeft == 0))
      {
        this.mOverlay.draw(paramCanvas);
      }
      else
      {
        int i = paramCanvas.getSaveCount();
        paramCanvas.save();
        paramCanvas.translate(this.mPaddingLeft, this.mPaddingTop);
        this.mOverlay.draw(paramCanvas);
        paramCanvas.restoreToCount(i);
      }
      return;
    }
  }
  
  public void setExcludeMimes(String[] paramArrayOfString)
  {
    this.mExcludeMimes = paramArrayOfString;
  }
  
  public void setImageToDefault()
  {
    if (this.mDefaultAvatar == null) {
      this.mDefaultAvatar = this.mContext.getDrawable(17302362);
    }
    setImageDrawable(this.mDefaultAvatar);
  }
  
  public void setMode(int paramInt) {}
  
  public void setOverlay(Drawable paramDrawable)
  {
    this.mOverlay = paramDrawable;
  }
  
  public void setPrioritizedMimeType(String paramString)
  {
    this.mPrioritizedMimeType = paramString;
  }
  
  private class QueryHandler
    extends AsyncQueryHandler
  {
    public QueryHandler(ContentResolver paramContentResolver)
    {
      super();
    }
    
    protected void onQueryComplete(int paramInt, Object paramObject, Cursor paramCursor)
    {
      Object localObject1 = null;
      Object localObject2 = null;
      Object localObject3 = null;
      Object localObject4 = null;
      int i = 0;
      int j = 0;
      int k = 0;
      Bundle localBundle;
      if (paramObject != null) {
        localBundle = (Bundle)paramObject;
      } else {
        localBundle = new Bundle();
      }
      paramObject = localObject2;
      if (paramInt != 0)
      {
        paramObject = localObject4;
        i = k;
        if (paramInt != 1)
        {
          if (paramInt != 2)
          {
            if (paramInt != 3)
            {
              localObject4 = localObject1;
              paramInt = j;
              break label260;
            }
            i = 1;
          }
          try
          {
            paramObject = Uri.fromParts("tel", localBundle.getString("uri_content"), null);
          }
          finally
          {
            break label248;
          }
          i = 1;
          paramObject = Uri.fromParts("mailto", localBundle.getString("uri_content"), null);
          break label187;
        }
        localObject4 = localObject1;
        localObject3 = paramObject;
        paramInt = i;
        if (paramCursor == null) {
          break label260;
        }
        localObject4 = localObject1;
        localObject3 = paramObject;
        paramInt = i;
        if (!paramCursor.moveToFirst()) {
          break label260;
        }
        localObject4 = ContactsContract.Contacts.getLookupUri(paramCursor.getLong(0), paramCursor.getString(1));
        localObject3 = paramObject;
        paramInt = i;
        break label260;
      }
      label187:
      localObject4 = localObject1;
      localObject3 = paramObject;
      paramInt = i;
      if (paramCursor != null)
      {
        localObject4 = localObject1;
        localObject3 = paramObject;
        paramInt = i;
        if (paramCursor.moveToFirst())
        {
          localObject4 = ContactsContract.Contacts.getLookupUri(paramCursor.getLong(0), paramCursor.getString(1));
          localObject3 = paramObject;
          paramInt = i;
          break label260;
          label248:
          if (paramCursor != null) {
            paramCursor.close();
          }
          throw ((Throwable)paramObject);
        }
      }
      label260:
      if (paramCursor != null) {
        paramCursor.close();
      }
      QuickContactBadge.access$002(QuickContactBadge.this, (Uri)localObject4);
      QuickContactBadge.this.onContactUriChanged();
      if ((paramInt != 0) && (QuickContactBadge.this.mContactUri != null))
      {
        paramObject = QuickContactBadge.this.getContext();
        paramCursor = QuickContactBadge.this;
        ContactsContract.QuickContact.showQuickContact((Context)paramObject, paramCursor, paramCursor.mContactUri, QuickContactBadge.this.mExcludeMimes, QuickContactBadge.this.mPrioritizedMimeType);
      }
      else if (localObject3 != null)
      {
        paramObject = new Intent("com.android.contacts.action.SHOW_OR_CREATE_CONTACT", (Uri)localObject3);
        localBundle.remove("uri_content");
        ((Intent)paramObject).putExtras(localBundle);
        QuickContactBadge.this.getContext().startActivity((Intent)paramObject);
      }
    }
  }
}


/* Location:              /Users/sanbo/Desktop/framework/miui/framework/classes3-dex2jar.jar!/android/widget/QuickContactBadge.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */