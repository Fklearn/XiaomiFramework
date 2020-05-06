package android.view;

public abstract interface FallbackEventHandler
{
  public abstract boolean dispatchKeyEvent(KeyEvent paramKeyEvent);
  
  public abstract void preDispatchKeyEvent(KeyEvent paramKeyEvent);
  
  public abstract void setView(View paramView);
}


/* Location:              /Users/sanbo/Desktop/framework/miui/framework/classes3-dex2jar.jar!/android/view/FallbackEventHandler.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */