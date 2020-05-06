package android.view;

public abstract interface WindowInsetsController
{
  public void controlInputMethodAnimation(WindowInsetsAnimationControlListener paramWindowInsetsAnimationControlListener)
  {
    controlWindowInsetsAnimation(WindowInsets.Type.ime(), paramWindowInsetsAnimationControlListener);
  }
  
  public abstract void controlWindowInsetsAnimation(int paramInt, WindowInsetsAnimationControlListener paramWindowInsetsAnimationControlListener);
  
  public abstract void hide(int paramInt);
  
  public void hideInputMethod()
  {
    hide(WindowInsets.Type.ime());
  }
  
  public abstract void show(int paramInt);
  
  public void showInputMethod()
  {
    show(WindowInsets.Type.ime());
  }
}


/* Location:              /Users/sanbo/Desktop/framework/miui/framework/classes3-dex2jar.jar!/android/view/WindowInsetsController.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */