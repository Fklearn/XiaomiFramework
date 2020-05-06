package android.widget;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.text.TextUtils;
import android.util.DisplayMetrics;
import android.view.View;
import android.view.View.MeasureSpec;
import android.view.View.OnClickListener;
import android.view.ViewGroup.LayoutParams;
import com.miui.translationservice.provider.TranslationResult;
import com.miui.translationservice.provider.TranslationResult.Part;
import com.miui.translationservice.provider.TranslationResult.Symbol;
import java.util.List;
import miui.widget.ProgressBar;

class TranslationPresenter
{
  private boolean mAboveHandle;
  private Context mContext;
  private TextView mCopyright;
  private int mDefaultPaddingBottom;
  private DisplayMetrics mDisplayMetrics;
  private View mExtraInfo;
  private int mMaxHeight;
  private int mMinHeight;
  private TextView mMore;
  private int mPaddingOffset;
  private ProgressBar mProgressBar;
  private View mScrollContainer;
  private View mScrollView;
  private View mTextContainer;
  private View mTranslationPanel;
  private TextView mTranslations;
  private TextView mWord;
  
  public TranslationPresenter(Context paramContext, View paramView)
  {
    this.mContext = paramContext;
    this.mTranslationPanel = paramView;
    this.mScrollContainer = this.mTranslationPanel.findViewById(285802689);
    this.mScrollView = this.mTranslationPanel.findViewById(285802668);
    this.mTextContainer = this.mTranslationPanel.findViewById(285802690);
    this.mWord = ((TextView)this.mTranslationPanel.findViewById(16908308));
    this.mTranslations = ((TextView)this.mTranslationPanel.findViewById(16908309));
    this.mExtraInfo = this.mTranslationPanel.findViewById(285802584);
    this.mCopyright = ((TextView)this.mTranslationPanel.findViewById(285802691));
    this.mMore = ((TextView)this.mTranslationPanel.findViewById(285802693));
    this.mProgressBar = ((ProgressBar)this.mTranslationPanel.findViewById(16908301));
    this.mMinHeight = paramContext.getResources().getDimensionPixelSize(285606089);
    this.mMaxHeight = paramContext.getResources().getDimensionPixelSize(285606088);
    this.mDisplayMetrics = paramContext.getResources().getDisplayMetrics();
    this.mDefaultPaddingBottom = paramContext.getResources().getDimensionPixelSize(285606090);
    this.mPaddingOffset = paramContext.getResources().getDimensionPixelSize(285606091);
  }
  
  public void setAboveHandle(boolean paramBoolean)
  {
    this.mAboveHandle = paramBoolean;
  }
  
  public void setInProgress()
  {
    this.mWord.setVisibility(8);
    this.mTranslations.setVisibility(8);
    this.mExtraInfo.setVisibility(8);
    this.mProgressBar.setVisibility(0);
    Object localObject = this.mScrollView;
    ((View)localObject).setPadding(((View)localObject).getPaddingLeft(), this.mScrollView.getPaddingTop(), this.mScrollView.getPaddingRight(), this.mDefaultPaddingBottom);
    localObject = this.mScrollView.getLayoutParams();
    ((ViewGroup.LayoutParams)localObject).height = this.mMinHeight;
    this.mScrollView.setLayoutParams((ViewGroup.LayoutParams)localObject);
    localObject = new Rect();
    this.mScrollContainer.getBackground().getPadding((Rect)localObject);
    int i = ((Rect)localObject).top;
    int j = ((Rect)localObject).bottom;
    RelativeLayout.LayoutParams localLayoutParams = new RelativeLayout.LayoutParams(-1, this.mMinHeight + (i + j));
    localObject = (RelativeLayout.LayoutParams)localLayoutParams;
    if (this.mAboveHandle) {
      i = 12;
    } else {
      i = 10;
    }
    ((RelativeLayout.LayoutParams)localObject).addRule(i);
    this.mScrollContainer.setLayoutParams(localLayoutParams);
  }
  
  public void updatePanel(final TranslationResult paramTranslationResult)
  {
    final Object localObject1 = this.mWord;
    int i = 0;
    ((TextView)localObject1).setVisibility(0);
    this.mProgressBar.setVisibility(8);
    if ((paramTranslationResult != null) && (paramTranslationResult.getStatus() == 0))
    {
      this.mWord.setText(paramTranslationResult.getWordName());
      this.mTranslations.setVisibility(0);
      StringBuilder localStringBuilder = new StringBuilder();
      localObject1 = paramTranslationResult.getSymbols();
      for (j = 0; j < ((List)localObject1).size(); j++)
      {
        Object localObject2 = (TranslationResult.Symbol)((List)localObject1).get(j);
        int k = 0;
        if (!TextUtils.isEmpty(((TranslationResult.Symbol)localObject2).getWordSymbol()))
        {
          localStringBuilder.append("[");
          localStringBuilder.append(((TranslationResult.Symbol)localObject2).getWordSymbol());
          localStringBuilder.append("]\r\n");
          k = 1;
        }
        n = k;
        if (k == 0)
        {
          n = k;
          if (!TextUtils.isEmpty(((TranslationResult.Symbol)localObject2).getPhEn()))
          {
            localStringBuilder.append("[");
            localStringBuilder.append(((TranslationResult.Symbol)localObject2).getPhEn());
            localStringBuilder.append("]\r\n");
            n = 1;
          }
        }
        if ((n == 0) && (!TextUtils.isEmpty(((TranslationResult.Symbol)localObject2).getPhAm())))
        {
          localStringBuilder.append("[");
          localStringBuilder.append(((TranslationResult.Symbol)localObject2).getPhAm());
          localStringBuilder.append("]\r\n");
        }
        localObject2 = ((TranslationResult.Symbol)localObject2).getParts();
        for (k = 0; k < ((List)localObject2).size(); k++)
        {
          Object localObject3 = (TranslationResult.Part)((List)localObject2).get(k);
          if (!TextUtils.isEmpty(((TranslationResult.Part)localObject3).getPart()))
          {
            localStringBuilder.append("(");
            localStringBuilder.append(((TranslationResult.Part)localObject3).getPart());
            localStringBuilder.append(") ");
          }
          localObject3 = ((TranslationResult.Part)localObject3).getMeans();
          for (n = 0; n < ((List)localObject3).size(); n++)
          {
            localStringBuilder.append((String)((List)localObject3).get(n));
            if ((n == ((List)localObject3).size() - 1) && (k != ((List)localObject2).size() - 1)) {
              localStringBuilder.append("\r\n");
            } else {
              localStringBuilder.append("/");
            }
          }
        }
      }
      this.mTranslations.setText(localStringBuilder.toString());
      localObject1 = paramTranslationResult.getCopyright();
      boolean bool = TextUtils.isEmpty((CharSequence)localObject1) ^ true;
      if (bool)
      {
        this.mExtraInfo.setVisibility(0);
        this.mCopyright.setVisibility(0);
        this.mCopyright.setText((CharSequence)localObject1);
      }
      localObject1 = paramTranslationResult.getDetailLink();
      if ((TextUtils.isEmpty((CharSequence)localObject1) ^ true))
      {
        this.mExtraInfo.setVisibility(0);
        this.mMore.setVisibility(0);
        this.mMore.setOnClickListener(new View.OnClickListener()
        {
          public void onClick(View paramAnonymousView)
          {
            paramAnonymousView = new Intent("android.intent.action.VIEW", Uri.parse(String.format(localObject1, new Object[] { paramTranslationResult.getWordName() })));
            if (!(TranslationPresenter.this.mContext instanceof Activity)) {
              paramAnonymousView.addFlags(268435456);
            }
            TranslationPresenter.this.mContext.startActivity(paramAnonymousView);
          }
        });
      }
      paramTranslationResult = this.mExtraInfo.getLayoutParams();
      if (bool) {
        m = paramTranslationResult.height - this.mPaddingOffset;
      } else {
        m = this.mDefaultPaddingBottom;
      }
      paramTranslationResult = this.mScrollView;
      paramTranslationResult.setPadding(paramTranslationResult.getPaddingLeft(), this.mScrollView.getPaddingTop(), this.mScrollView.getPaddingRight(), m);
    }
    else
    {
      this.mTranslations.setVisibility(0);
      if (paramTranslationResult == null)
      {
        this.mWord.setVisibility(8);
        this.mTranslations.setText(this.mContext.getString(286130548));
      }
      else if (paramTranslationResult.getStatus() == -2)
      {
        this.mWord.setText(paramTranslationResult.getWordName());
        this.mTranslations.setText(this.mContext.getString(286130548));
      }
      else
      {
        this.mWord.setText(paramTranslationResult.getWordName());
        this.mTranslations.setText(this.mContext.getString(286130547));
      }
    }
    paramTranslationResult = new Rect();
    this.mScrollContainer.getBackground().getPadding(paramTranslationResult);
    int n = paramTranslationResult.left + paramTranslationResult.right;
    int i1 = paramTranslationResult.top;
    int m = paramTranslationResult.bottom;
    int i2 = this.mTranslationPanel.getMeasuredWidth();
    int j = this.mTranslationPanel.getMeasuredHeight();
    int i3 = this.mScrollView.getPaddingLeft();
    int i4 = this.mScrollView.getPaddingRight();
    this.mTextContainer.measure(View.MeasureSpec.makeMeasureSpec(i2 - n - i3 - i4, Integer.MIN_VALUE), View.MeasureSpec.makeMeasureSpec(this.mDisplayMetrics.heightPixels, 0));
    i3 = this.mTextContainer.getMeasuredHeight();
    paramTranslationResult = this.mScrollView.getLayoutParams();
    paramTranslationResult.width = (i2 - n);
    paramTranslationResult.height = Math.min(this.mMaxHeight, this.mScrollView.getPaddingTop() + i3 + this.mScrollView.getPaddingBottom());
    this.mScrollView.setLayoutParams(paramTranslationResult);
    paramTranslationResult = this.mScrollContainer.getLayoutParams();
    paramTranslationResult.height = (Math.min(this.mMaxHeight, this.mScrollView.getLayoutParams().height) + (i1 + m));
    localObject1 = (RelativeLayout.LayoutParams)paramTranslationResult;
    if (this.mAboveHandle) {
      m = 12;
    } else {
      m = 10;
    }
    ((RelativeLayout.LayoutParams)localObject1).addRule(m);
    this.mScrollContainer.setLayoutParams(paramTranslationResult);
    this.mScrollContainer.setLeft(0);
    localObject1 = this.mScrollContainer;
    m = i;
    if (this.mAboveHandle) {
      m = j - paramTranslationResult.height;
    }
    ((View)localObject1).setTop(m);
  }
}


/* Location:              /Users/sanbo/Desktop/framework/miui/framework/classes3-dex2jar.jar!/android/widget/TranslationPresenter.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */