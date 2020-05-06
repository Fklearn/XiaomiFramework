package android.widget;

import android.graphics.Rect;
import android.text.Layout;
import android.text.Spannable;
import android.view.AccessibilityIterators.AbstractTextSegmentIterator;

final class AccessibilityIterators
{
  static class LineTextSegmentIterator
    extends AccessibilityIterators.AbstractTextSegmentIterator
  {
    protected static final int DIRECTION_END = 1;
    protected static final int DIRECTION_START = -1;
    private static LineTextSegmentIterator sLineInstance;
    protected Layout mLayout;
    
    public static LineTextSegmentIterator getInstance()
    {
      if (sLineInstance == null) {
        sLineInstance = new LineTextSegmentIterator();
      }
      return sLineInstance;
    }
    
    public int[] following(int paramInt)
    {
      if (this.mText.length() <= 0) {
        return null;
      }
      if (paramInt >= this.mText.length()) {
        return null;
      }
      if (paramInt < 0)
      {
        paramInt = this.mLayout.getLineForOffset(0);
      }
      else
      {
        int i = this.mLayout.getLineForOffset(paramInt);
        if (getLineEdgeIndex(i, -1) == paramInt) {
          paramInt = i;
        } else {
          paramInt = i + 1;
        }
      }
      if (paramInt >= this.mLayout.getLineCount()) {
        return null;
      }
      return getRange(getLineEdgeIndex(paramInt, -1), getLineEdgeIndex(paramInt, 1) + 1);
    }
    
    protected int getLineEdgeIndex(int paramInt1, int paramInt2)
    {
      if (paramInt2 * this.mLayout.getParagraphDirection(paramInt1) < 0) {
        return this.mLayout.getLineStart(paramInt1);
      }
      return this.mLayout.getLineEnd(paramInt1) - 1;
    }
    
    public void initialize(Spannable paramSpannable, Layout paramLayout)
    {
      this.mText = paramSpannable.toString();
      this.mLayout = paramLayout;
    }
    
    public int[] preceding(int paramInt)
    {
      if (this.mText.length() <= 0) {
        return null;
      }
      if (paramInt <= 0) {
        return null;
      }
      if (paramInt > this.mText.length())
      {
        paramInt = this.mLayout.getLineForOffset(this.mText.length());
      }
      else
      {
        int i = this.mLayout.getLineForOffset(paramInt);
        if (getLineEdgeIndex(i, 1) + 1 == paramInt) {
          paramInt = i;
        } else {
          paramInt = i - 1;
        }
      }
      if (paramInt < 0) {
        return null;
      }
      return getRange(getLineEdgeIndex(paramInt, -1), getLineEdgeIndex(paramInt, 1) + 1);
    }
  }
  
  static class PageTextSegmentIterator
    extends AccessibilityIterators.LineTextSegmentIterator
  {
    private static PageTextSegmentIterator sPageInstance;
    private final Rect mTempRect = new Rect();
    private TextView mView;
    
    public static PageTextSegmentIterator getInstance()
    {
      if (sPageInstance == null) {
        sPageInstance = new PageTextSegmentIterator();
      }
      return sPageInstance;
    }
    
    public int[] following(int paramInt)
    {
      if (this.mText.length() <= 0) {
        return null;
      }
      if (paramInt >= this.mText.length()) {
        return null;
      }
      if (!this.mView.getGlobalVisibleRect(this.mTempRect)) {
        return null;
      }
      int i = Math.max(0, paramInt);
      paramInt = this.mLayout.getLineForOffset(i);
      paramInt = this.mLayout.getLineTop(paramInt) + (this.mTempRect.height() - this.mView.getTotalPaddingTop() - this.mView.getTotalPaddingBottom());
      if (paramInt < this.mLayout.getLineTop(this.mLayout.getLineCount() - 1)) {
        paramInt = this.mLayout.getLineForVertical(paramInt);
      } else {
        paramInt = this.mLayout.getLineCount();
      }
      return getRange(i, getLineEdgeIndex(paramInt - 1, 1) + 1);
    }
    
    public void initialize(TextView paramTextView)
    {
      super.initialize((Spannable)paramTextView.getIterableTextForAccessibility(), paramTextView.getLayout());
      this.mView = paramTextView;
    }
    
    public int[] preceding(int paramInt)
    {
      if (this.mText.length() <= 0) {
        return null;
      }
      if (paramInt <= 0) {
        return null;
      }
      if (!this.mView.getGlobalVisibleRect(this.mTempRect)) {
        return null;
      }
      int i = Math.min(this.mText.length(), paramInt);
      int j = this.mLayout.getLineForOffset(i);
      paramInt = this.mLayout.getLineTop(j) - (this.mTempRect.height() - this.mView.getTotalPaddingTop() - this.mView.getTotalPaddingBottom());
      if (paramInt > 0) {
        paramInt = this.mLayout.getLineForVertical(paramInt);
      } else {
        paramInt = 0;
      }
      int k = paramInt;
      if (i == this.mText.length())
      {
        k = paramInt;
        if (paramInt < j) {
          k = paramInt + 1;
        }
      }
      return getRange(getLineEdgeIndex(k, -1), i);
    }
  }
}


/* Location:              /Users/sanbo/Desktop/framework/miui/framework/classes3-dex2jar.jar!/android/widget/AccessibilityIterators.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */