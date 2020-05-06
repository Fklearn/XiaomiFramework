package android.webkit;

import android.annotation.SystemApi;
import android.content.Context;
import android.content.res.Resources;
import android.graphics.Point;
import android.graphics.Rect;
import android.text.Editable;
import android.text.Selection;
import android.text.Spannable;
import android.text.TextWatcher;
import android.view.ActionMode;
import android.view.ActionMode.Callback;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.TextView;

@SystemApi
public class FindActionModeCallback
  implements ActionMode.Callback, TextWatcher, View.OnClickListener, WebView.FindListener
{
  private ActionMode mActionMode;
  private int mActiveMatchIndex;
  private View mCustomView;
  private EditText mEditText;
  private Point mGlobalVisibleOffset = new Point();
  private Rect mGlobalVisibleRect = new Rect();
  private InputMethodManager mInput;
  private TextView mMatches;
  private boolean mMatchesFound;
  private int mNumberOfMatches;
  private Resources mResources;
  private WebView mWebView;
  
  public FindActionModeCallback(Context paramContext)
  {
    this.mCustomView = LayoutInflater.from(paramContext).inflate(17367361, null);
    this.mEditText = ((EditText)this.mCustomView.findViewById(16908291));
    this.mEditText.setCustomSelectionActionModeCallback(new NoAction());
    this.mEditText.setOnClickListener(this);
    setText("");
    this.mMatches = ((TextView)this.mCustomView.findViewById(16909105));
    this.mInput = ((InputMethodManager)paramContext.getSystemService(InputMethodManager.class));
    this.mResources = paramContext.getResources();
  }
  
  private void findNext(boolean paramBoolean)
  {
    WebView localWebView = this.mWebView;
    if (localWebView != null)
    {
      if (!this.mMatchesFound)
      {
        findAll();
        return;
      }
      if (this.mNumberOfMatches == 0) {
        return;
      }
      localWebView.findNext(paramBoolean);
      updateMatchesString();
      return;
    }
    throw new AssertionError("No WebView for FindActionModeCallback::findNext");
  }
  
  private void updateMatchesString()
  {
    int i = this.mNumberOfMatches;
    if (i == 0) {
      this.mMatches.setText(17040538);
    } else {
      this.mMatches.setText(this.mResources.getQuantityString(18153496, i, new Object[] { Integer.valueOf(this.mActiveMatchIndex + 1), Integer.valueOf(this.mNumberOfMatches) }));
    }
    this.mMatches.setVisibility(0);
  }
  
  public void afterTextChanged(Editable paramEditable) {}
  
  public void beforeTextChanged(CharSequence paramCharSequence, int paramInt1, int paramInt2, int paramInt3) {}
  
  public void findAll()
  {
    if (this.mWebView != null)
    {
      Editable localEditable = this.mEditText.getText();
      if (localEditable.length() == 0)
      {
        this.mWebView.clearMatches();
        this.mMatches.setVisibility(8);
        this.mMatchesFound = false;
        this.mWebView.findAll(null);
      }
      else
      {
        this.mMatchesFound = true;
        this.mMatches.setVisibility(4);
        this.mNumberOfMatches = 0;
        this.mWebView.findAllAsync(localEditable.toString());
      }
      return;
    }
    throw new AssertionError("No WebView for FindActionModeCallback::findAll");
  }
  
  public void finish()
  {
    this.mActionMode.finish();
  }
  
  public int getActionModeGlobalBottom()
  {
    if (this.mActionMode == null) {
      return 0;
    }
    View localView1 = (View)this.mCustomView.getParent();
    View localView2 = localView1;
    if (localView1 == null) {
      localView2 = this.mCustomView;
    }
    localView2.getGlobalVisibleRect(this.mGlobalVisibleRect, this.mGlobalVisibleOffset);
    return this.mGlobalVisibleRect.bottom;
  }
  
  public boolean onActionItemClicked(ActionMode paramActionMode, MenuItem paramMenuItem)
  {
    paramActionMode = this.mWebView;
    if (paramActionMode != null)
    {
      this.mInput.hideSoftInputFromWindow(paramActionMode.getWindowToken(), 0);
      switch (paramMenuItem.getItemId())
      {
      default: 
        return false;
      case 16908931: 
        findNext(false);
        break;
      case 16908930: 
        findNext(true);
      }
      return true;
    }
    throw new AssertionError("No WebView for FindActionModeCallback::onActionItemClicked");
  }
  
  public void onClick(View paramView)
  {
    findNext(true);
  }
  
  public boolean onCreateActionMode(ActionMode paramActionMode, Menu paramMenu)
  {
    if (!paramActionMode.isUiFocusable()) {
      return false;
    }
    paramActionMode.setCustomView(this.mCustomView);
    paramActionMode.getMenuInflater().inflate(18087938, paramMenu);
    this.mActionMode = paramActionMode;
    paramActionMode = this.mEditText.getText();
    Selection.setSelection(paramActionMode, paramActionMode.length());
    this.mMatches.setVisibility(8);
    this.mMatchesFound = false;
    this.mMatches.setText("0");
    this.mEditText.requestFocus();
    return true;
  }
  
  public void onDestroyActionMode(ActionMode paramActionMode)
  {
    this.mActionMode = null;
    this.mWebView.notifyFindDialogDismissed();
    this.mWebView.setFindDialogFindListener(null);
    this.mInput.hideSoftInputFromWindow(this.mWebView.getWindowToken(), 0);
  }
  
  public void onFindResultReceived(int paramInt1, int paramInt2, boolean paramBoolean)
  {
    if (paramBoolean)
    {
      if (paramInt2 == 0) {
        paramBoolean = true;
      } else {
        paramBoolean = false;
      }
      updateMatchCount(paramInt1, paramInt2, paramBoolean);
    }
  }
  
  public boolean onPrepareActionMode(ActionMode paramActionMode, Menu paramMenu)
  {
    return false;
  }
  
  public void onTextChanged(CharSequence paramCharSequence, int paramInt1, int paramInt2, int paramInt3)
  {
    findAll();
  }
  
  public void setText(String paramString)
  {
    this.mEditText.setText(paramString);
    paramString = this.mEditText.getText();
    int i = paramString.length();
    Selection.setSelection(paramString, i, i);
    paramString.setSpan(this, 0, i, 18);
    this.mMatchesFound = false;
  }
  
  public void setWebView(WebView paramWebView)
  {
    if (paramWebView != null)
    {
      this.mWebView = paramWebView;
      this.mWebView.setFindDialogFindListener(this);
      return;
    }
    throw new AssertionError("WebView supplied to FindActionModeCallback cannot be null");
  }
  
  public void showSoftInput()
  {
    if (this.mEditText.requestFocus()) {
      this.mInput.showSoftInput(this.mEditText, 0);
    }
  }
  
  public void updateMatchCount(int paramInt1, int paramInt2, boolean paramBoolean)
  {
    if (!paramBoolean)
    {
      this.mNumberOfMatches = paramInt2;
      this.mActiveMatchIndex = paramInt1;
      updateMatchesString();
    }
    else
    {
      this.mMatches.setVisibility(8);
      this.mNumberOfMatches = 0;
    }
  }
  
  public static class NoAction
    implements ActionMode.Callback
  {
    public boolean onActionItemClicked(ActionMode paramActionMode, MenuItem paramMenuItem)
    {
      return false;
    }
    
    public boolean onCreateActionMode(ActionMode paramActionMode, Menu paramMenu)
    {
      return false;
    }
    
    public void onDestroyActionMode(ActionMode paramActionMode) {}
    
    public boolean onPrepareActionMode(ActionMode paramActionMode, Menu paramMenu)
    {
      return false;
    }
  }
}


/* Location:              /Users/sanbo/Desktop/framework/miui/framework/classes3-dex2jar.jar!/android/webkit/FindActionModeCallback.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */