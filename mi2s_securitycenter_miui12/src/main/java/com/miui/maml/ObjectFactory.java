package com.miui.maml;

import com.miui.maml.elements.BitmapProvider;
import com.miui.maml.elements.ScreenElement;
import org.w3c.dom.Element;

public abstract class ObjectFactory {

    public static abstract class ActionCommandFactory extends ObjectFactoryBase<ActionCommandFactory> {
        public static final String NAME = "ActionCommand";

        protected ActionCommandFactory() {
            super("ActionCommand");
        }

        public final ActionCommand create(ScreenElement screenElement, Element element) {
            ActionCommand doCreate = doCreate(screenElement, element);
            if (doCreate != null) {
                return doCreate;
            }
            T t = this.mOld;
            if (t == null) {
                return null;
            }
            return ((ActionCommandFactory) t).create(screenElement, element);
        }

        /* access modifiers changed from: protected */
        public abstract ActionCommand doCreate(ScreenElement screenElement, Element element);
    }

    public static abstract class BitmapProviderFactory extends ObjectFactoryBase<BitmapProviderFactory> {
        public static final String NAME = "BitmapProvider";

        protected BitmapProviderFactory() {
            super(NAME);
        }

        public final BitmapProvider create(ScreenElementRoot screenElementRoot, String str) {
            BitmapProvider doCreate = doCreate(screenElementRoot, str);
            if (doCreate != null) {
                return doCreate;
            }
            T t = this.mOld;
            if (t == null) {
                return null;
            }
            return ((BitmapProviderFactory) t).create(screenElementRoot, str);
        }

        /* access modifiers changed from: protected */
        public abstract BitmapProvider doCreate(ScreenElementRoot screenElementRoot, String str);
    }

    public static abstract class ObjectFactoryBase<T extends ObjectFactory> extends ObjectFactory {
        private String mName;
        protected T mOld;

        protected ObjectFactoryBase(String str) {
            this.mName = str;
        }

        public String getName() {
            return this.mName;
        }

        public T getOld() {
            return this.mOld;
        }

        public void setOld(ObjectFactory objectFactory) {
            this.mOld = objectFactory;
        }
    }

    public abstract String getName();

    public abstract ObjectFactory getOld();

    public abstract void setOld(ObjectFactory objectFactory);
}
