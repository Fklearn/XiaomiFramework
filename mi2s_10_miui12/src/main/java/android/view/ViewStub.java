package android.view;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.util.AttributeSet;
import android.widget.RemoteViews.RemoteView;
import com.android.internal.R.styleable;
import java.lang.ref.WeakReference;

@RemoteViews.RemoteView
public final class ViewStub
  extends View
{
  private OnInflateListener mInflateListener;
  private int mInflatedId;
  private WeakReference<View> mInflatedViewRef;
  private LayoutInflater mInflater;
  private int mLayoutResource;
  
  public ViewStub(Context paramContext)
  {
    this(paramContext, 0);
  }
  
  public ViewStub(Context paramContext, int paramInt)
  {
    this(paramContext, null);
    this.mLayoutResource = paramInt;
  }
  
  public ViewStub(Context paramContext, AttributeSet paramAttributeSet)
  {
    this(paramContext, paramAttributeSet, 0);
  }
  
  public ViewStub(Context paramContext, AttributeSet paramAttributeSet, int paramInt)
  {
    this(paramContext, paramAttributeSet, paramInt, 0);
  }
  
  public ViewStub(Context paramContext, AttributeSet paramAttributeSet, int paramInt1, int paramInt2)
  {
    super(paramContext);
    TypedArray localTypedArray = paramContext.obtainStyledAttributes(paramAttributeSet, R.styleable.ViewStub, paramInt1, paramInt2);
    saveAttributeDataForStyleable(paramContext, R.styleable.ViewStub, paramAttributeSet, localTypedArray, paramInt1, paramInt2);
    this.mInflatedId = localTypedArray.getResourceId(2, -1);
    this.mLayoutResource = localTypedArray.getResourceId(1, 0);
    this.mID = localTypedArray.getResourceId(0, -1);
    localTypedArray.recycle();
    setVisibility(8);
    setWillNotDraw(true);
  }
  
  private View inflateViewNoAdd(ViewGroup paramViewGroup)
  {
    LayoutInflater localLayoutInflater;
    if (this.mInflater != null) {
      localLayoutInflater = this.mInflater;
    } else {
      localLayoutInflater = LayoutInflater.from(this.mContext);
    }
    paramViewGroup = localLayoutInflater.inflate(this.mLayoutResource, paramViewGroup, false);
    int i = this.mInflatedId;
    if (i != -1) {
      paramViewGroup.setId(i);
    }
    return paramViewGroup;
  }
  
  private void replaceSelfWithView(View paramView, ViewGroup paramViewGroup)
  {
    int i = paramViewGroup.indexOfChild(this);
    paramViewGroup.removeViewInLayout(this);
    ViewGroup.LayoutParams localLayoutParams = getLayoutParams();
    if (localLayoutParams != null) {
      paramViewGroup.addView(paramView, i, localLayoutParams);
    } else {
      paramViewGroup.addView(paramView, i);
    }
  }
  
  protected void dispatchDraw(Canvas paramCanvas) {}
  
  public void draw(Canvas paramCanvas) {}
  
  public int getInflatedId()
  {
    return this.mInflatedId;
  }
  
  public LayoutInflater getLayoutInflater()
  {
    return this.mInflater;
  }
  
  public int getLayoutResource()
  {
    return this.mLayoutResource;
  }
  
  public View inflate()
  {
    Object localObject1 = getParent();
    if ((localObject1 != null) && ((localObject1 instanceof ViewGroup)))
    {
      if (this.mLayoutResource != 0)
      {
        Object localObject2 = (ViewGroup)localObject1;
        localObject1 = inflateViewNoAdd((ViewGroup)localObject2);
        replaceSelfWithView((View)localObject1, (ViewGroup)localObject2);
        this.mInflatedViewRef = new WeakReference(localObject1);
        localObject2 = this.mInflateListener;
        if (localObject2 != null) {
          ((OnInflateListener)localObject2).onInflate(this, (View)localObject1);
        }
        return (View)localObject1;
      }
      throw new IllegalArgumentException("ViewStub must have a valid layoutResource");
    }
    throw new IllegalStateException("ViewStub must have a non-null ViewGroup viewParent");
  }
  
  protected void onMeasure(int paramInt1, int paramInt2)
  {
    setMeasuredDimension(0, 0);
  }
  
  @RemotableViewMethod(asyncImpl="setInflatedIdAsync")
  public void setInflatedId(int paramInt)
  {
    this.mInflatedId = paramInt;
  }
  
  public Runnable setInflatedIdAsync(int paramInt)
  {
    this.mInflatedId = paramInt;
    return null;
  }
  
  public void setLayoutInflater(LayoutInflater paramLayoutInflater)
  {
    this.mInflater = paramLayoutInflater;
  }
  
  @RemotableViewMethod(asyncImpl="setLayoutResourceAsync")
  public void setLayoutResource(int paramInt)
  {
    this.mLayoutResource = paramInt;
  }
  
  public Runnable setLayoutResourceAsync(int paramInt)
  {
    this.mLayoutResource = paramInt;
    return null;
  }
  
  public void setOnInflateListener(OnInflateListener paramOnInflateListener)
  {
    this.mInflateListener = paramOnInflateListener;
  }
  
  @RemotableViewMethod(asyncImpl="setVisibilityAsync")
  public void setVisibility(int paramInt)
  {
    Object localObject = this.mInflatedViewRef;
    if (localObject != null)
    {
      localObject = (View)((WeakReference)localObject).get();
      if (localObject != null) {
        ((View)localObject).setVisibility(paramInt);
      } else {
        throw new IllegalStateException("setVisibility called on un-referenced view");
      }
    }
    else
    {
      super.setVisibility(paramInt);
      if ((paramInt == 0) || (paramInt == 4)) {
        inflate();
      }
    }
  }
  
  public Runnable setVisibilityAsync(int paramInt)
  {
    if ((paramInt != 0) && (paramInt != 4)) {
      return null;
    }
    return new ViewReplaceRunnable(inflateViewNoAdd((ViewGroup)getParent()));
  }
  
  public static abstract interface OnInflateListener
  {
    public abstract void onInflate(ViewStub paramViewStub, View paramView);
  }
  
  public class ViewReplaceRunnable
    implements Runnable
  {
    public final View view;
    
    ViewReplaceRunnable(View paramView)
    {
      this.view = paramView;
    }
    
    public void run()
    {
      ViewStub localViewStub = ViewStub.this;
      localViewStub.replaceSelfWithView(this.view, (ViewGroup)localViewStub.getParent());
    }
  }
}


/* Location:              /Users/sanbo/Desktop/framework/miui/framework/classes3-dex2jar.jar!/android/view/ViewStub.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */