package android.view.inputmethod;

import android.os.Bundle;
import android.os.Handler;
import android.view.KeyEvent;

public abstract interface InputConnection
{
  public static final int CURSOR_UPDATE_IMMEDIATE = 1;
  public static final int CURSOR_UPDATE_MONITOR = 2;
  public static final int GET_EXTRACTED_TEXT_MONITOR = 1;
  public static final int GET_TEXT_WITH_STYLES = 1;
  public static final int INPUT_CONTENT_GRANT_READ_URI_PERMISSION = 1;
  
  public abstract boolean beginBatchEdit();
  
  public abstract boolean clearMetaKeyStates(int paramInt);
  
  public abstract void closeConnection();
  
  public abstract boolean commitCompletion(CompletionInfo paramCompletionInfo);
  
  public abstract boolean commitContent(InputContentInfo paramInputContentInfo, int paramInt, Bundle paramBundle);
  
  public abstract boolean commitCorrection(CorrectionInfo paramCorrectionInfo);
  
  public abstract boolean commitText(CharSequence paramCharSequence, int paramInt);
  
  public abstract boolean deleteSurroundingText(int paramInt1, int paramInt2);
  
  public abstract boolean deleteSurroundingTextInCodePoints(int paramInt1, int paramInt2);
  
  public abstract boolean endBatchEdit();
  
  public abstract boolean finishComposingText();
  
  public abstract int getCursorCapsMode(int paramInt);
  
  public abstract ExtractedText getExtractedText(ExtractedTextRequest paramExtractedTextRequest, int paramInt);
  
  public abstract Handler getHandler();
  
  public abstract CharSequence getSelectedText(int paramInt);
  
  public abstract CharSequence getTextAfterCursor(int paramInt1, int paramInt2);
  
  public abstract CharSequence getTextBeforeCursor(int paramInt1, int paramInt2);
  
  public abstract boolean performContextMenuAction(int paramInt);
  
  public abstract boolean performEditorAction(int paramInt);
  
  public abstract boolean performPrivateCommand(String paramString, Bundle paramBundle);
  
  public abstract boolean reportFullscreenMode(boolean paramBoolean);
  
  public abstract boolean requestCursorUpdates(int paramInt);
  
  public abstract boolean sendKeyEvent(KeyEvent paramKeyEvent);
  
  public abstract boolean setComposingRegion(int paramInt1, int paramInt2);
  
  public abstract boolean setComposingText(CharSequence paramCharSequence, int paramInt);
  
  public abstract boolean setSelection(int paramInt1, int paramInt2);
}


/* Location:              /Users/sanbo/Desktop/framework/miui/framework/classes3-dex2jar.jar!/android/view/inputmethod/InputConnection.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */