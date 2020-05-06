package android.view.inputmethod;

import android.os.Bundle;
import android.os.Handler;
import android.view.KeyEvent;

public class InputConnectionWrapper
  implements InputConnection
{
  private int mMissingMethodFlags;
  final boolean mMutable;
  private InputConnection mTarget;
  
  public InputConnectionWrapper(InputConnection paramInputConnection, boolean paramBoolean)
  {
    this.mMutable = paramBoolean;
    this.mTarget = paramInputConnection;
    this.mMissingMethodFlags = InputConnectionInspector.getMissingMethodFlags(paramInputConnection);
  }
  
  public boolean beginBatchEdit()
  {
    return this.mTarget.beginBatchEdit();
  }
  
  public boolean clearMetaKeyStates(int paramInt)
  {
    return this.mTarget.clearMetaKeyStates(paramInt);
  }
  
  public void closeConnection()
  {
    this.mTarget.closeConnection();
  }
  
  public boolean commitCompletion(CompletionInfo paramCompletionInfo)
  {
    return this.mTarget.commitCompletion(paramCompletionInfo);
  }
  
  public boolean commitContent(InputContentInfo paramInputContentInfo, int paramInt, Bundle paramBundle)
  {
    return this.mTarget.commitContent(paramInputContentInfo, paramInt, paramBundle);
  }
  
  public boolean commitCorrection(CorrectionInfo paramCorrectionInfo)
  {
    return this.mTarget.commitCorrection(paramCorrectionInfo);
  }
  
  public boolean commitText(CharSequence paramCharSequence, int paramInt)
  {
    return this.mTarget.commitText(paramCharSequence, paramInt);
  }
  
  public boolean deleteSurroundingText(int paramInt1, int paramInt2)
  {
    return this.mTarget.deleteSurroundingText(paramInt1, paramInt2);
  }
  
  public boolean deleteSurroundingTextInCodePoints(int paramInt1, int paramInt2)
  {
    return this.mTarget.deleteSurroundingTextInCodePoints(paramInt1, paramInt2);
  }
  
  public boolean endBatchEdit()
  {
    return this.mTarget.endBatchEdit();
  }
  
  public boolean finishComposingText()
  {
    return this.mTarget.finishComposingText();
  }
  
  public int getCursorCapsMode(int paramInt)
  {
    return this.mTarget.getCursorCapsMode(paramInt);
  }
  
  public ExtractedText getExtractedText(ExtractedTextRequest paramExtractedTextRequest, int paramInt)
  {
    return this.mTarget.getExtractedText(paramExtractedTextRequest, paramInt);
  }
  
  public Handler getHandler()
  {
    return this.mTarget.getHandler();
  }
  
  public int getMissingMethodFlags()
  {
    return this.mMissingMethodFlags;
  }
  
  public CharSequence getSelectedText(int paramInt)
  {
    return this.mTarget.getSelectedText(paramInt);
  }
  
  public CharSequence getTextAfterCursor(int paramInt1, int paramInt2)
  {
    return this.mTarget.getTextAfterCursor(paramInt1, paramInt2);
  }
  
  public CharSequence getTextBeforeCursor(int paramInt1, int paramInt2)
  {
    return this.mTarget.getTextBeforeCursor(paramInt1, paramInt2);
  }
  
  public boolean performContextMenuAction(int paramInt)
  {
    return this.mTarget.performContextMenuAction(paramInt);
  }
  
  public boolean performEditorAction(int paramInt)
  {
    return this.mTarget.performEditorAction(paramInt);
  }
  
  public boolean performPrivateCommand(String paramString, Bundle paramBundle)
  {
    return this.mTarget.performPrivateCommand(paramString, paramBundle);
  }
  
  public boolean reportFullscreenMode(boolean paramBoolean)
  {
    return this.mTarget.reportFullscreenMode(paramBoolean);
  }
  
  public boolean requestCursorUpdates(int paramInt)
  {
    return this.mTarget.requestCursorUpdates(paramInt);
  }
  
  public boolean sendKeyEvent(KeyEvent paramKeyEvent)
  {
    return this.mTarget.sendKeyEvent(paramKeyEvent);
  }
  
  public boolean setComposingRegion(int paramInt1, int paramInt2)
  {
    return this.mTarget.setComposingRegion(paramInt1, paramInt2);
  }
  
  public boolean setComposingText(CharSequence paramCharSequence, int paramInt)
  {
    return this.mTarget.setComposingText(paramCharSequence, paramInt);
  }
  
  public boolean setSelection(int paramInt1, int paramInt2)
  {
    return this.mTarget.setSelection(paramInt1, paramInt2);
  }
  
  public void setTarget(InputConnection paramInputConnection)
  {
    if ((this.mTarget != null) && (!this.mMutable)) {
      throw new SecurityException("not mutable");
    }
    this.mTarget = paramInputConnection;
    this.mMissingMethodFlags = InputConnectionInspector.getMissingMethodFlags(paramInputConnection);
  }
}


/* Location:              /Users/sanbo/Desktop/framework/miui/framework/classes3-dex2jar.jar!/android/view/inputmethod/InputConnectionWrapper.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */