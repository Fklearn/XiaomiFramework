package android.view.textclassifier;

import android.text.Spannable;
import android.text.style.ClickableSpan;
import android.text.util.Linkify;
import android.util.EventLog;
import com.android.internal.util.Preconditions;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.function.Function;

public final class TextLinksParams
{
  private static final Function<TextLinks.TextLink, TextLinks.TextLinkSpan> DEFAULT_SPAN_FACTORY = _..Lambda.TextLinksParams.km8pN8nazHT6NQiHykIrRALWbkE.INSTANCE;
  private final int mApplyStrategy;
  private final TextClassifier.EntityConfig mEntityConfig;
  private final Function<TextLinks.TextLink, TextLinks.TextLinkSpan> mSpanFactory;
  
  private TextLinksParams(int paramInt, Function<TextLinks.TextLink, TextLinks.TextLinkSpan> paramFunction)
  {
    this.mApplyStrategy = paramInt;
    this.mSpanFactory = paramFunction;
    this.mEntityConfig = TextClassifier.EntityConfig.createWithHints(null);
  }
  
  private static int checkApplyStrategy(int paramInt)
  {
    if ((paramInt != 0) && (paramInt != 1)) {
      throw new IllegalArgumentException("Invalid apply strategy. See TextLinksParams.ApplyStrategy for options.");
    }
    return paramInt;
  }
  
  public static TextLinksParams fromLinkMask(int paramInt)
  {
    ArrayList localArrayList = new ArrayList();
    if ((paramInt & 0x1) != 0) {
      localArrayList.add("url");
    }
    if ((paramInt & 0x2) != 0) {
      localArrayList.add("email");
    }
    if ((paramInt & 0x4) != 0) {
      localArrayList.add("phone");
    }
    if ((paramInt & 0x8) != 0) {
      localArrayList.add("address");
    }
    return new Builder().setEntityConfig(TextClassifier.EntityConfig.createWithExplicitEntityList(localArrayList)).build();
  }
  
  public int apply(Spannable paramSpannable, TextLinks paramTextLinks)
  {
    Preconditions.checkNotNull(paramSpannable);
    Preconditions.checkNotNull(paramTextLinks);
    Object localObject = paramSpannable.toString();
    if (Linkify.containsUnsupportedCharacters((String)localObject))
    {
      EventLog.writeEvent(1397638484, new Object[] { "116321860", Integer.valueOf(-1), "" });
      return 4;
    }
    if (!((String)localObject).startsWith(paramTextLinks.getText())) {
      return 3;
    }
    if (paramTextLinks.getLinks().isEmpty()) {
      return 1;
    }
    int i = 0;
    localObject = paramTextLinks.getLinks().iterator();
    while (((Iterator)localObject).hasNext())
    {
      TextLinks.TextLink localTextLink = (TextLinks.TextLink)((Iterator)localObject).next();
      TextLinks.TextLinkSpan localTextLinkSpan = (TextLinks.TextLinkSpan)this.mSpanFactory.apply(localTextLink);
      int j = i;
      if (localTextLinkSpan != null)
      {
        paramTextLinks = (ClickableSpan[])paramSpannable.getSpans(localTextLink.getStart(), localTextLink.getEnd(), ClickableSpan.class);
        if (paramTextLinks.length > 0)
        {
          j = i;
          if (this.mApplyStrategy == 1)
          {
            int k = paramTextLinks.length;
            for (j = 0; j < k; j++) {
              paramSpannable.removeSpan(paramTextLinks[j]);
            }
            paramSpannable.setSpan(localTextLinkSpan, localTextLink.getStart(), localTextLink.getEnd(), 33);
            j = i + 1;
          }
        }
        else
        {
          paramSpannable.setSpan(localTextLinkSpan, localTextLink.getStart(), localTextLink.getEnd(), 33);
          j = i + 1;
        }
      }
      i = j;
    }
    if (i == 0) {
      return 2;
    }
    return 0;
  }
  
  public TextClassifier.EntityConfig getEntityConfig()
  {
    return this.mEntityConfig;
  }
  
  public static final class Builder
  {
    private int mApplyStrategy = 0;
    private Function<TextLinks.TextLink, TextLinks.TextLinkSpan> mSpanFactory = TextLinksParams.DEFAULT_SPAN_FACTORY;
    
    public TextLinksParams build()
    {
      return new TextLinksParams(this.mApplyStrategy, this.mSpanFactory, null);
    }
    
    public Builder setApplyStrategy(int paramInt)
    {
      this.mApplyStrategy = TextLinksParams.checkApplyStrategy(paramInt);
      return this;
    }
    
    public Builder setEntityConfig(TextClassifier.EntityConfig paramEntityConfig)
    {
      return this;
    }
    
    public Builder setSpanFactory(Function<TextLinks.TextLink, TextLinks.TextLinkSpan> paramFunction)
    {
      if (paramFunction == null) {
        paramFunction = TextLinksParams.DEFAULT_SPAN_FACTORY;
      }
      this.mSpanFactory = paramFunction;
      return this;
    }
  }
}


/* Location:              /Users/sanbo/Desktop/framework/miui/framework/classes3-dex2jar.jar!/android/view/textclassifier/TextLinksParams.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */