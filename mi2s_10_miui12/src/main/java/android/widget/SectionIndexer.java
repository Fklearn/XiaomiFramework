package android.widget;

public abstract interface SectionIndexer
{
  public abstract int getPositionForSection(int paramInt);
  
  public abstract int getSectionForPosition(int paramInt);
  
  public abstract Object[] getSections();
}


/* Location:              /Users/sanbo/Desktop/framework/miui/framework/classes3-dex2jar.jar!/android/widget/SectionIndexer.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */