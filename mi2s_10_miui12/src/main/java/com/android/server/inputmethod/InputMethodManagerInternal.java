package com.android.server.inputmethod;

import android.view.inputmethod.InputMethodInfo;
import com.android.server.LocalServices;
import java.util.Collections;
import java.util.List;

public abstract class InputMethodManagerInternal {
    private static final InputMethodManagerInternal NOP = new InputMethodManagerInternal() {
        public void setInteractive(boolean interactive) {
        }

        public void hideCurrentInputMethod() {
        }

        public List<InputMethodInfo> getInputMethodListAsUser(int userId) {
            return Collections.emptyList();
        }

        public List<InputMethodInfo> getEnabledInputMethodListAsUser(int userId) {
            return Collections.emptyList();
        }
    };

    public abstract List<InputMethodInfo> getEnabledInputMethodListAsUser(int i);

    public abstract List<InputMethodInfo> getInputMethodListAsUser(int i);

    public abstract void hideCurrentInputMethod();

    public abstract void setInteractive(boolean z);

    public static InputMethodManagerInternal get() {
        InputMethodManagerInternal instance = (InputMethodManagerInternal) LocalServices.getService(InputMethodManagerInternal.class);
        return instance != null ? instance : NOP;
    }
}
