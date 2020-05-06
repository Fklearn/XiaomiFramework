package org.ksoap2.serialization;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Hashtable;
import java.util.Vector;
import miui.provider.ExtraContacts;
import miui.telephony.phonenumber.Prefix;
import org.ksoap2.SoapEnvelope;
import org.ksoap2.SoapFault;
import org.ksoap2.SoapFault12;
import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;
import org.xmlpull.v1.XmlSerializer;

public class SoapSerializationEnvelope extends SoapEnvelope {
    private static final String ANY_TYPE_LABEL = "anyType";
    private static final String ARRAY_MAPPING_NAME = "Array";
    private static final String ARRAY_TYPE_LABEL = "arrayType";
    static final Marshal DEFAULT_MARSHAL = new DM();
    private static final String HREF_LABEL = "href";
    private static final String ID_LABEL = "id";
    private static final String ITEM_LABEL = "item";
    protected static final String NIL_LABEL = "nil";
    protected static final String NULL_LABEL = "null";
    protected static final int QNAME_MARSHAL = 3;
    protected static final int QNAME_NAMESPACE = 0;
    protected static final int QNAME_TYPE = 1;
    private static final String ROOT_LABEL = "root";
    private static final String TYPE_LABEL = "type";
    protected boolean addAdornments = true;
    public boolean avoidExceptionForUnknownProperty;
    protected Hashtable classToQName = new Hashtable();
    public boolean dotNet;
    Hashtable idMap = new Hashtable();
    public boolean implicitTypes;
    Vector multiRef;
    public Hashtable properties = new Hashtable();
    protected Hashtable qNameToClass = new Hashtable();
    public boolean skipNullProperties;

    public SoapSerializationEnvelope(int version) {
        super(version);
        addMapping(this.enc, ARRAY_MAPPING_NAME, PropertyInfo.VECTOR_CLASS);
        DEFAULT_MARSHAL.register(this);
    }

    public boolean isAddAdornments() {
        return this.addAdornments;
    }

    public void setAddAdornments(boolean addAdornments2) {
        this.addAdornments = addAdornments2;
    }

    public void setBodyOutEmpty(boolean emptyBody) {
        if (emptyBody) {
            this.bodyOut = null;
        }
    }

    public void parseBody(XmlPullParser parser) throws IOException, XmlPullParserException {
        SoapFault fault;
        this.bodyIn = null;
        parser.nextTag();
        if (parser.getEventType() != 2 || !parser.getNamespace().equals(this.env) || !parser.getName().equals("Fault")) {
            while (parser.getEventType() == 2) {
                String rootAttr = parser.getAttributeValue(this.enc, ROOT_LABEL);
                Object o = read(parser, (Object) null, -1, parser.getNamespace(), parser.getName(), PropertyInfo.OBJECT_TYPE);
                if ("1".equals(rootAttr) || this.bodyIn == null) {
                    this.bodyIn = o;
                }
                parser.nextTag();
            }
            return;
        }
        if (this.version < 120) {
            fault = new SoapFault(this.version);
        } else {
            fault = new SoapFault12(this.version);
        }
        fault.parse(parser);
        this.bodyIn = fault;
    }

    /* access modifiers changed from: protected */
    public void readSerializable(XmlPullParser parser, SoapObject obj) throws IOException, XmlPullParserException {
        for (int counter = 0; counter < parser.getAttributeCount(); counter++) {
            obj.addAttribute(parser.getAttributeName(counter), parser.getAttributeValue(counter));
        }
        readSerializable(parser, (KvmSerializable) obj);
    }

    /* access modifiers changed from: protected */
    public void readSerializable(XmlPullParser parser, KvmSerializable obj) throws IOException, XmlPullParserException {
        int tag;
        int tag2;
        int i;
        XmlPullParser xmlPullParser = parser;
        KvmSerializable kvmSerializable = obj;
        try {
            tag = parser.nextTag();
        } catch (XmlPullParserException e) {
            XmlPullParserException xmlPullParserException = e;
            if (kvmSerializable instanceof HasInnerText) {
                ((HasInnerText) kvmSerializable).setInnerText(parser.getText() != null ? parser.getText() : Prefix.EMPTY);
            }
            tag = parser.nextTag();
        }
        while (tag != 3) {
            String name = parser.getName();
            if (!this.implicitTypes || !(kvmSerializable instanceof SoapObject)) {
                PropertyInfo info = new PropertyInfo();
                int propertyCount = obj.getPropertyCount();
                boolean propertyFound = false;
                int i2 = 0;
                while (i2 < propertyCount && !propertyFound) {
                    info.clear();
                    kvmSerializable.getPropertyInfo(i2, this.properties, info);
                    if ((!name.equals(info.name) || info.namespace != null) && (!name.equals(info.name) || !parser.getNamespace().equals(info.namespace))) {
                        i = i2;
                    } else {
                        i = i2;
                        kvmSerializable.setProperty(i, read(parser, obj, i2, (String) null, (String) null, info));
                        propertyFound = true;
                    }
                    i2 = i + 1;
                }
                int i3 = i2;
                if (!propertyFound) {
                    if (this.avoidExceptionForUnknownProperty) {
                        while (true) {
                            if (parser.next() != 3 || !name.equals(parser.getName())) {
                            }
                        }
                        tag2 = parser.nextTag();
                    } else {
                        throw new RuntimeException("Unknown Property: " + name);
                    }
                } else if (kvmSerializable instanceof HasAttributes) {
                    HasAttributes soapObject = (HasAttributes) kvmSerializable;
                    int cnt = parser.getAttributeCount();
                    for (int counter = 0; counter < cnt; counter++) {
                        AttributeInfo attributeInfo = new AttributeInfo();
                        attributeInfo.setName(xmlPullParser.getAttributeName(counter));
                        attributeInfo.setValue(xmlPullParser.getAttributeValue(counter));
                        attributeInfo.setNamespace(xmlPullParser.getAttributeNamespace(counter));
                        attributeInfo.setType(xmlPullParser.getAttributeType(counter));
                        soapObject.setAttribute(attributeInfo);
                    }
                }
            } else {
                ((SoapObject) kvmSerializable).addProperty(parser.getName(), read(parser, obj, obj.getPropertyCount(), ((SoapObject) kvmSerializable).getNamespace(), name, PropertyInfo.OBJECT_TYPE));
            }
            try {
                tag2 = parser.nextTag();
            } catch (XmlPullParserException e2) {
                XmlPullParserException xmlPullParserException2 = e2;
                if (kvmSerializable instanceof HasInnerText) {
                    ((HasInnerText) kvmSerializable).setInnerText(parser.getText() != null ? parser.getText() : Prefix.EMPTY);
                }
                tag2 = parser.nextTag();
            }
        }
        xmlPullParser.require(3, (String) null, (String) null);
    }

    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r0v3, resolved type: org.ksoap2.serialization.SoapObject} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r0v16, resolved type: org.ksoap2.serialization.SoapObject} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r2v4, resolved type: org.ksoap2.serialization.SoapPrimitive} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r0v18, resolved type: org.ksoap2.serialization.SoapObject} */
    /* access modifiers changed from: protected */
    /* JADX WARNING: Multi-variable type inference failed */
    /* JADX WARNING: Removed duplicated region for block: B:21:0x009b  */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public java.lang.Object readUnknown(org.xmlpull.v1.XmlPullParser r21, java.lang.String r22, java.lang.String r23) throws java.io.IOException, org.xmlpull.v1.XmlPullParserException {
        /*
            r20 = this;
            r7 = r21
            r8 = r22
            r9 = r23
            java.lang.String r10 = r21.getName()
            java.lang.String r11 = r21.getNamespace()
            java.util.Vector r0 = new java.util.Vector
            r0.<init>()
            r12 = r0
            r0 = 0
        L_0x0015:
            int r1 = r21.getAttributeCount()
            if (r0 >= r1) goto L_0x0042
            org.ksoap2.serialization.AttributeInfo r1 = new org.ksoap2.serialization.AttributeInfo
            r1.<init>()
            java.lang.String r2 = r7.getAttributeName(r0)
            r1.setName(r2)
            java.lang.String r2 = r7.getAttributeValue(r0)
            r1.setValue(r2)
            java.lang.String r2 = r7.getAttributeNamespace(r0)
            r1.setNamespace(r2)
            java.lang.String r2 = r7.getAttributeType(r0)
            r1.setType(r2)
            r12.addElement(r1)
            int r0 = r0 + 1
            goto L_0x0015
        L_0x0042:
            r21.next()
            r0 = 0
            r1 = 0
            int r2 = r21.getEventType()
            r3 = 4
            r13 = 3
            if (r2 != r3) goto L_0x0070
            java.lang.String r1 = r21.getText()
            org.ksoap2.serialization.SoapPrimitive r2 = new org.ksoap2.serialization.SoapPrimitive
            r2.<init>(r8, r9, r1)
            r0 = r2
            r3 = 0
        L_0x005a:
            int r4 = r12.size()
            if (r3 >= r4) goto L_0x006c
            java.lang.Object r4 = r12.elementAt(r3)
            org.ksoap2.serialization.AttributeInfo r4 = (org.ksoap2.serialization.AttributeInfo) r4
            r2.addAttribute(r4)
            int r3 = r3 + 1
            goto L_0x005a
        L_0x006c:
            r21.next()
            goto L_0x0092
        L_0x0070:
            int r2 = r21.getEventType()
            if (r2 != r13) goto L_0x0092
            org.ksoap2.serialization.SoapObject r2 = new org.ksoap2.serialization.SoapObject
            r2.<init>(r8, r9)
            r3 = 0
        L_0x007c:
            int r4 = r12.size()
            if (r3 >= r4) goto L_0x008e
            java.lang.Object r4 = r12.elementAt(r3)
            org.ksoap2.serialization.AttributeInfo r4 = (org.ksoap2.serialization.AttributeInfo) r4
            r2.addAttribute(r4)
            int r3 = r3 + 1
            goto L_0x007c
        L_0x008e:
            r0 = r2
            r15 = r0
            r14 = r1
            goto L_0x0094
        L_0x0092:
            r15 = r0
            r14 = r1
        L_0x0094:
            int r0 = r21.getEventType()
            r1 = 2
            if (r0 != r1) goto L_0x0103
            if (r14 == 0) goto L_0x00b0
            java.lang.String r0 = r14.trim()
            int r0 = r0.length()
            if (r0 != 0) goto L_0x00a8
            goto L_0x00b0
        L_0x00a8:
            java.lang.RuntimeException r0 = new java.lang.RuntimeException
            java.lang.String r1 = "Malformed input: Mixed content"
            r0.<init>(r1)
            throw r0
        L_0x00b0:
            org.ksoap2.serialization.SoapObject r0 = new org.ksoap2.serialization.SoapObject
            r0.<init>(r8, r9)
            r6 = r0
            r0 = 0
        L_0x00b7:
            int r1 = r12.size()
            if (r0 >= r1) goto L_0x00c9
            java.lang.Object r1 = r12.elementAt(r0)
            org.ksoap2.serialization.AttributeInfo r1 = (org.ksoap2.serialization.AttributeInfo) r1
            r6.addAttribute(r1)
            int r0 = r0 + 1
            goto L_0x00b7
        L_0x00c9:
            int r0 = r21.getEventType()
            if (r0 == r13) goto L_0x0101
            java.lang.String r5 = r21.getNamespace()
            java.lang.String r4 = r21.getName()
            int r3 = r6.getPropertyCount()
            r16 = 0
            r17 = 0
            org.ksoap2.serialization.PropertyInfo r18 = org.ksoap2.serialization.PropertyInfo.OBJECT_TYPE
            r0 = r20
            r1 = r21
            r2 = r6
            r13 = r4
            r4 = r16
            r19 = r5
            r5 = r17
            r8 = r6
            r6 = r18
            java.lang.Object r0 = r0.read(r1, r2, r3, r4, r5, r6)
            r1 = r19
            r8.addProperty(r1, r13, r0)
            r21.nextTag()
            r6 = r8
            r13 = 3
            r8 = r22
            goto L_0x00c9
        L_0x0101:
            r8 = r6
            r15 = r8
        L_0x0103:
            r0 = 3
            r7.require(r0, r11, r10)
            return r15
        */
        throw new UnsupportedOperationException("Method not decompiled: org.ksoap2.serialization.SoapSerializationEnvelope.readUnknown(org.xmlpull.v1.XmlPullParser, java.lang.String, java.lang.String):java.lang.Object");
    }

    private int getIndex(String value, int start, int dflt) {
        if (value == null) {
            return dflt;
        }
        try {
            return value.length() - start < 3 ? dflt : Integer.parseInt(value.substring(start + 1, value.length() - 1));
        } catch (Exception e) {
            return dflt;
        }
    }

    /* access modifiers changed from: protected */
    public void readVector(XmlPullParser parser, Vector v, PropertyInfo elementType) throws IOException, XmlPullParserException {
        String name;
        String namespace;
        String prefix;
        PropertyInfo elementType2;
        int size;
        XmlPullParser xmlPullParser = parser;
        Vector vector = v;
        int size2 = v.size();
        String type = xmlPullParser.getAttributeValue(this.enc, ARRAY_TYPE_LABEL);
        int i = 0;
        if (type != null) {
            int cut0 = type.indexOf(58);
            int cut1 = type.indexOf("[", cut0);
            String name2 = type.substring(cut0 + 1, cut1);
            String namespace2 = xmlPullParser.getNamespace(cut0 == -1 ? Prefix.EMPTY : type.substring(0, cut0));
            size2 = getIndex(type, cut1, -1);
            if (size2 != -1) {
                vector.setSize(size2);
                namespace = namespace2;
                name = name2;
                prefix = null;
            } else {
                namespace = namespace2;
                name = name2;
                prefix = 1;
            }
        } else {
            namespace = null;
            name = null;
            prefix = 1;
        }
        if (elementType == null) {
            elementType2 = PropertyInfo.OBJECT_TYPE;
        } else {
            elementType2 = elementType;
        }
        parser.nextTag();
        int position = getIndex(xmlPullParser.getAttributeValue(this.enc, ExtraContacts.ConferenceCalls.OFFSET_PARAM_KEY), 0, 0);
        while (parser.getEventType() != 3) {
            int position2 = getIndex(xmlPullParser.getAttributeValue(this.enc, "position"), i, position);
            if (prefix == null || position2 < size2) {
                size = size2;
            } else {
                int size3 = position2 + 1;
                vector.setSize(size3);
                size = size3;
            }
            int position3 = position2;
            vector.setElementAt(read(parser, v, position2, namespace, name, elementType2), position3);
            position = position3 + 1;
            parser.nextTag();
            size2 = size;
            i = 0;
        }
        xmlPullParser.require(3, (String) null, (String) null);
    }

    /* access modifiers changed from: protected */
    public String getIdFromHref(String hrefValue) {
        return hrefValue.substring(1);
    }

    public Object read(XmlPullParser parser, Object owner, int index, String namespace, String name, PropertyInfo expected) throws IOException, XmlPullParserException {
        Object obj;
        String name2;
        String prefix;
        XmlPullParser xmlPullParser = parser;
        Object obj2 = owner;
        PropertyInfo propertyInfo = expected;
        String elementName = parser.getName();
        String href = xmlPullParser.getAttributeValue((String) null, HREF_LABEL);
        if (href == null) {
            int i = index;
            String nullAttr = xmlPullParser.getAttributeValue(this.xsi, NIL_LABEL);
            String id = xmlPullParser.getAttributeValue((String) null, ID_LABEL);
            if (nullAttr == null) {
                nullAttr = xmlPullParser.getAttributeValue(this.xsi, NULL_LABEL);
            }
            if (nullAttr == null || !SoapEnvelope.stringToBoolean(nullAttr)) {
                String type = xmlPullParser.getAttributeValue(this.xsi, "type");
                if (type != null) {
                    int cut = type.indexOf(58);
                    String name3 = type.substring(cut + 1);
                    prefix = xmlPullParser.getNamespace(cut == -1 ? Prefix.EMPTY : type.substring(0, cut));
                    name2 = name3;
                } else if (name != null || namespace != null) {
                    prefix = namespace;
                    name2 = name;
                } else if (xmlPullParser.getAttributeValue(this.enc, ARRAY_TYPE_LABEL) != null) {
                    prefix = this.enc;
                    name2 = ARRAY_MAPPING_NAME;
                } else {
                    Object[] names = getInfo(propertyInfo.type, (Object) null);
                    prefix = (String) names[0];
                    name2 = (String) names[1];
                }
                if (type == null) {
                    this.implicitTypes = true;
                }
                obj = readInstance(xmlPullParser, prefix, name2, propertyInfo);
                if (obj == null) {
                    obj = readUnknown(xmlPullParser, prefix, name2);
                }
            } else {
                parser.nextTag();
                xmlPullParser.require(3, (String) null, elementName);
                String str = namespace;
                String str2 = name;
                obj = null;
            }
            if (id != null) {
                resolveReference(id, obj);
            }
        } else if (obj2 != null) {
            String href2 = getIdFromHref(href);
            Object obj3 = this.idMap.get(href2);
            if (obj3 == null || (obj3 instanceof FwdRef)) {
                FwdRef f = new FwdRef();
                f.next = (FwdRef) obj3;
                f.obj = obj2;
                f.index = index;
                this.idMap.put(href2, f);
                obj3 = null;
            } else {
                int i2 = index;
            }
            parser.nextTag();
            xmlPullParser.require(3, (String) null, elementName);
            String str3 = namespace;
            String str4 = name;
            obj = obj3;
        } else {
            int i3 = index;
            throw new RuntimeException("href at root level?!?");
        }
        xmlPullParser.require(3, (String) null, elementName);
        return obj;
    }

    /* access modifiers changed from: protected */
    public void resolveReference(String id, Object obj) {
        Object hlp = this.idMap.get(id);
        if (hlp instanceof FwdRef) {
            FwdRef f = (FwdRef) hlp;
            do {
                if (f.obj instanceof KvmSerializable) {
                    ((KvmSerializable) f.obj).setProperty(f.index, obj);
                } else {
                    ((Vector) f.obj).setElementAt(obj, f.index);
                }
                f = f.next;
            } while (f != null);
        } else if (hlp == null) {
            this.idMap.put(id, obj);
        } else {
            throw new RuntimeException("double ID");
        }
        this.idMap.put(id, obj);
    }

    public Object readInstance(XmlPullParser parser, String namespace, String name, PropertyInfo expected) throws IOException, XmlPullParserException {
        Object obj;
        Object obj2 = this.qNameToClass.get(new SoapPrimitive(namespace, name, (Object) null));
        if (obj2 == null) {
            return null;
        }
        if (obj2 instanceof Marshal) {
            return ((Marshal) obj2).readInstance(parser, namespace, name, expected);
        }
        if (obj2 instanceof SoapObject) {
            obj = ((SoapObject) obj2).newInstance();
        } else if (obj2 == SoapObject.class) {
            obj = new SoapObject(namespace, name);
        } else {
            try {
                obj = ((Class) obj2).newInstance();
            } catch (Exception e) {
                throw new RuntimeException(e.toString());
            }
        }
        if (obj instanceof HasAttributes) {
            HasAttributes soapObject = (HasAttributes) obj;
            int cnt = parser.getAttributeCount();
            for (int counter = 0; counter < cnt; counter++) {
                AttributeInfo attributeInfo = new AttributeInfo();
                attributeInfo.setName(parser.getAttributeName(counter));
                attributeInfo.setValue(parser.getAttributeValue(counter));
                attributeInfo.setNamespace(parser.getAttributeNamespace(counter));
                attributeInfo.setType(parser.getAttributeType(counter));
                soapObject.setAttribute(attributeInfo);
            }
        }
        if (obj instanceof SoapObject) {
            readSerializable(parser, (SoapObject) obj);
        } else if (obj instanceof KvmSerializable) {
            if (obj instanceof HasInnerText) {
                ((HasInnerText) obj).setInnerText(parser.getText() != null ? parser.getText() : Prefix.EMPTY);
            }
            readSerializable(parser, (KvmSerializable) obj);
        } else if (obj instanceof Vector) {
            readVector(parser, (Vector) obj, expected.elementType);
        } else {
            throw new RuntimeException("no deserializer for " + obj.getClass());
        }
        return obj;
    }

    public Object[] getInfo(Object type, Object instance) {
        Object[] tmp;
        if (type == null) {
            if ((instance instanceof SoapObject) || (instance instanceof SoapPrimitive)) {
                type = instance;
            } else {
                type = instance.getClass();
            }
        }
        if (type instanceof SoapObject) {
            SoapObject so = (SoapObject) type;
            return new Object[]{so.getNamespace(), so.getName(), null, null};
        } else if (type instanceof SoapPrimitive) {
            SoapPrimitive sp = (SoapPrimitive) type;
            return new Object[]{sp.getNamespace(), sp.getName(), null, DEFAULT_MARSHAL};
        } else if ((type instanceof Class) && type != PropertyInfo.OBJECT_CLASS && (tmp = (Object[]) this.classToQName.get(((Class) type).getName())) != null) {
            return tmp;
        } else {
            return new Object[]{this.xsd, ANY_TYPE_LABEL, null, null};
        }
    }

    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r3v0, resolved type: java.lang.Class} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r3v3, resolved type: java.lang.Class} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r3v4, resolved type: java.lang.Class} */
    /* JADX WARNING: Multi-variable type inference failed */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public void addMapping(java.lang.String r6, java.lang.String r7, java.lang.Class r8, org.ksoap2.serialization.Marshal r9) {
        /*
            r5 = this;
            java.util.Hashtable r0 = r5.qNameToClass
            org.ksoap2.serialization.SoapPrimitive r1 = new org.ksoap2.serialization.SoapPrimitive
            r2 = 0
            r1.<init>(r6, r7, r2)
            if (r9 != 0) goto L_0x000c
            r3 = r8
            goto L_0x000d
        L_0x000c:
            r3 = r9
        L_0x000d:
            r0.put(r1, r3)
            java.util.Hashtable r0 = r5.classToQName
            java.lang.String r1 = r8.getName()
            r3 = 4
            java.lang.Object[] r3 = new java.lang.Object[r3]
            r4 = 0
            r3[r4] = r6
            r4 = 1
            r3[r4] = r7
            r4 = 2
            r3[r4] = r2
            r2 = 3
            r3[r2] = r9
            r0.put(r1, r3)
            return
        */
        throw new UnsupportedOperationException("Method not decompiled: org.ksoap2.serialization.SoapSerializationEnvelope.addMapping(java.lang.String, java.lang.String, java.lang.Class, org.ksoap2.serialization.Marshal):void");
    }

    public void addMapping(String namespace, String name, Class clazz) {
        addMapping(namespace, name, clazz, (Marshal) null);
    }

    public void addTemplate(SoapObject so) {
        this.qNameToClass.put(new SoapPrimitive(so.namespace, so.name, (Object) null), so);
    }

    public Object getResponse() throws SoapFault {
        if (this.bodyIn == null) {
            return null;
        }
        if (!(this.bodyIn instanceof SoapFault)) {
            KvmSerializable ks = (KvmSerializable) this.bodyIn;
            if (ks.getPropertyCount() == 0) {
                return null;
            }
            if (ks.getPropertyCount() == 1) {
                return ks.getProperty(0);
            }
            Vector ret = new Vector();
            for (int i = 0; i < ks.getPropertyCount(); i++) {
                ret.add(ks.getProperty(i));
            }
            return ret;
        }
        throw ((SoapFault) this.bodyIn);
    }

    public void writeBody(XmlSerializer writer) throws IOException {
        if (this.bodyOut != null) {
            this.multiRef = new Vector();
            this.multiRef.addElement(this.bodyOut);
            Object[] qName = getInfo((Object) null, this.bodyOut);
            boolean z = this.dotNet;
            String str = Prefix.EMPTY;
            writer.startTag(z ? str : (String) qName[0], (String) qName[1]);
            if (this.dotNet) {
                writer.attribute((String) null, "xmlns", (String) qName[0]);
            }
            if (this.addAdornments) {
                writer.attribute((String) null, ID_LABEL, qName[2] == null ? "o0" : (String) qName[2]);
                writer.attribute(this.enc, ROOT_LABEL, "1");
            }
            writeElement(writer, this.bodyOut, (PropertyInfo) null, qName[3]);
            if (!this.dotNet) {
                str = (String) qName[0];
            }
            writer.endTag(str, (String) qName[1]);
        }
    }

    private void writeAttributes(XmlSerializer writer, HasAttributes obj) throws IOException {
        HasAttributes soapObject = obj;
        int cnt = soapObject.getAttributeCount();
        for (int counter = 0; counter < cnt; counter++) {
            AttributeInfo attributeInfo = new AttributeInfo();
            soapObject.getAttributeInfo(counter, attributeInfo);
            soapObject.getAttribute(counter, attributeInfo);
            if (attributeInfo.getValue() != null) {
                writer.attribute(attributeInfo.getNamespace(), attributeInfo.getName(), attributeInfo.getValue().toString());
            }
        }
    }

    public void writeArrayListBodyWithAttributes(XmlSerializer writer, KvmSerializable obj) throws IOException {
        if (obj instanceof HasAttributes) {
            writeAttributes(writer, (HasAttributes) obj);
        }
        writeArrayListBody(writer, (ArrayList) obj);
    }

    public void writeObjectBodyWithAttributes(XmlSerializer writer, KvmSerializable obj) throws IOException {
        if (obj instanceof HasAttributes) {
            writeAttributes(writer, (HasAttributes) obj);
        }
        writeObjectBody(writer, obj);
    }

    public void writeObjectBody(XmlSerializer writer, KvmSerializable obj) throws IOException {
        String name;
        String namespace;
        int cnt = obj.getPropertyCount();
        PropertyInfo propertyInfo = new PropertyInfo();
        for (int i = 0; i < cnt; i++) {
            Object prop = obj.getProperty(i);
            obj.getPropertyInfo(i, this.properties, propertyInfo);
            if (prop instanceof SoapObject) {
                SoapObject nestedSoap = (SoapObject) prop;
                Object[] qName = getInfo((Object) null, nestedSoap);
                String str = (String) qName[0];
                String type = (String) qName[1];
                if (propertyInfo.name == null || propertyInfo.name.length() <= 0) {
                    name = (String) qName[1];
                } else {
                    name = propertyInfo.name;
                }
                if (propertyInfo.namespace == null || propertyInfo.namespace.length() <= 0) {
                    namespace = (String) qName[0];
                } else {
                    namespace = propertyInfo.namespace;
                }
                writer.startTag(namespace, name);
                if (!this.implicitTypes) {
                    String prefix = writer.getPrefix(namespace, true);
                    String str2 = this.xsi;
                    writer.attribute(str2, "type", prefix + ":" + type);
                }
                writeObjectBodyWithAttributes(writer, nestedSoap);
                writer.endTag(namespace, name);
            } else if ((propertyInfo.flags & 1) == 0) {
                Object objValue = obj.getProperty(i);
                if ((prop != null || !this.skipNullProperties) && objValue != SoapPrimitive.NullSkip) {
                    writer.startTag(propertyInfo.namespace, propertyInfo.name);
                    writeProperty(writer, objValue, propertyInfo);
                    writer.endTag(propertyInfo.namespace, propertyInfo.name);
                }
            }
        }
        writeInnerText(writer, obj);
    }

    private void writeInnerText(XmlSerializer writer, KvmSerializable obj) throws IOException {
        Object value;
        if ((obj instanceof HasInnerText) && (value = ((HasInnerText) obj).getInnerText()) != null) {
            if (value instanceof ValueWriter) {
                ((ValueWriter) value).write(writer);
            } else {
                writer.cdsect(value.toString());
            }
        }
    }

    /* access modifiers changed from: protected */
    public void writeProperty(XmlSerializer writer, Object obj, PropertyInfo type) throws IOException {
        String str;
        if (obj == null || obj == SoapPrimitive.NullNilElement) {
            writer.attribute(this.xsi, this.version >= 120 ? NIL_LABEL : NULL_LABEL, "true");
            return;
        }
        Object[] qName = getInfo((Object) null, obj);
        if (type.multiRef || qName[2] != null) {
            int i = this.multiRef.indexOf(obj);
            if (i == -1) {
                i = this.multiRef.size();
                this.multiRef.addElement(obj);
            }
            if (qName[2] == null) {
                str = "#o" + i;
            } else {
                str = "#" + qName[2];
            }
            writer.attribute((String) null, HREF_LABEL, str);
            return;
        }
        if (!this.implicitTypes || obj.getClass() != type.type) {
            String prefix = writer.getPrefix((String) qName[0], true);
            writer.attribute(this.xsi, "type", prefix + ":" + qName[1]);
        }
        writeElement(writer, obj, type, qName[3]);
    }

    /* access modifiers changed from: protected */
    public void writeElement(XmlSerializer writer, Object element, PropertyInfo type, Object marshal) throws IOException {
        if (marshal != null) {
            ((Marshal) marshal).writeInstance(writer, element);
        } else if ((element instanceof KvmSerializable) || element == SoapPrimitive.NullNilElement || element == SoapPrimitive.NullSkip) {
            if (element instanceof ArrayList) {
                writeArrayListBodyWithAttributes(writer, (KvmSerializable) element);
            } else {
                writeObjectBodyWithAttributes(writer, (KvmSerializable) element);
            }
        } else if (element instanceof HasAttributes) {
            writeAttributes(writer, (HasAttributes) element);
        } else if (element instanceof Vector) {
            writeVectorBody(writer, (Vector) element, type.elementType);
        } else {
            throw new RuntimeException("Cannot serialize: " + element);
        }
    }

    /* access modifiers changed from: protected */
    public void writeArrayListBody(XmlSerializer writer, ArrayList list) throws IOException {
        String name;
        String namespace;
        XmlSerializer xmlSerializer = writer;
        KvmSerializable obj = (KvmSerializable) list;
        int cnt = list.size();
        PropertyInfo propertyInfo = new PropertyInfo();
        for (int i = 0; i < cnt; i++) {
            Object prop = obj.getProperty(i);
            obj.getPropertyInfo(i, this.properties, propertyInfo);
            if (prop instanceof SoapObject) {
                SoapObject nestedSoap = (SoapObject) prop;
                Object[] qName = getInfo((Object) null, nestedSoap);
                String str = (String) qName[0];
                String type = (String) qName[1];
                if (propertyInfo.name == null || propertyInfo.name.length() <= 0) {
                    name = (String) qName[1];
                } else {
                    name = propertyInfo.name;
                }
                if (propertyInfo.namespace == null || propertyInfo.namespace.length() <= 0) {
                    namespace = (String) qName[0];
                } else {
                    namespace = propertyInfo.namespace;
                }
                xmlSerializer.startTag(namespace, name);
                if (!this.implicitTypes) {
                    String prefix = xmlSerializer.getPrefix(namespace, true);
                    String str2 = this.xsi;
                    xmlSerializer.attribute(str2, "type", prefix + ":" + type);
                }
                writeObjectBodyWithAttributes(xmlSerializer, nestedSoap);
                xmlSerializer.endTag(namespace, name);
            } else if ((propertyInfo.flags & 1) == 0) {
                Object objValue = obj.getProperty(i);
                if ((prop != null || !this.skipNullProperties) && objValue != SoapPrimitive.NullSkip) {
                    xmlSerializer.startTag(propertyInfo.namespace, propertyInfo.name);
                    writeProperty(xmlSerializer, objValue, propertyInfo);
                    xmlSerializer.endTag(propertyInfo.namespace, propertyInfo.name);
                }
            }
        }
        writeInnerText(xmlSerializer, obj);
    }

    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r3v1, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r9v2, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r7v7, resolved type: java.lang.Object[]} */
    /* JADX WARNING: type inference failed for: r4v8 */
    /* access modifiers changed from: protected */
    /* JADX WARNING: Multi-variable type inference failed */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public void writeVectorBody(org.xmlpull.v1.XmlSerializer r12, java.util.Vector r13, org.ksoap2.serialization.PropertyInfo r14) throws java.io.IOException {
        /*
            r11 = this;
            java.lang.String r0 = "item"
            r1 = 0
            if (r14 != 0) goto L_0x0008
            org.ksoap2.serialization.PropertyInfo r14 = org.ksoap2.serialization.PropertyInfo.OBJECT_TYPE
            goto L_0x0014
        L_0x0008:
            boolean r2 = r14 instanceof org.ksoap2.serialization.PropertyInfo
            if (r2 == 0) goto L_0x0014
            java.lang.String r2 = r14.name
            if (r2 == 0) goto L_0x0014
            java.lang.String r0 = r14.name
            java.lang.String r1 = r14.namespace
        L_0x0014:
            int r2 = r13.size()
            java.lang.Object r3 = r14.type
            r4 = 0
            java.lang.Object[] r3 = r11.getInfo(r3, r4)
            boolean r4 = r11.implicitTypes
            java.lang.String r5 = "]"
            java.lang.String r6 = "["
            r7 = 0
            if (r4 != 0) goto L_0x0058
            java.lang.String r4 = r11.enc
            java.lang.StringBuilder r8 = new java.lang.StringBuilder
            r8.<init>()
            r9 = r3[r7]
            java.lang.String r9 = (java.lang.String) r9
            java.lang.String r7 = r12.getPrefix(r9, r7)
            r8.append(r7)
            java.lang.String r7 = ":"
            r8.append(r7)
            r7 = 1
            r7 = r3[r7]
            r8.append(r7)
            r8.append(r6)
            r8.append(r2)
            r8.append(r5)
            java.lang.String r7 = r8.toString()
            java.lang.String r8 = "arrayType"
            r12.attribute(r4, r8, r7)
            goto L_0x005f
        L_0x0058:
            if (r1 != 0) goto L_0x005f
            r4 = r3[r7]
            r1 = r4
            java.lang.String r1 = (java.lang.String) r1
        L_0x005f:
            r4 = 0
            r7 = 0
        L_0x0061:
            if (r7 >= r2) goto L_0x0097
            java.lang.Object r8 = r13.elementAt(r7)
            if (r8 != 0) goto L_0x006b
            r4 = 1
            goto L_0x0094
        L_0x006b:
            r12.startTag(r1, r0)
            if (r4 == 0) goto L_0x008a
            java.lang.String r8 = r11.enc
            java.lang.StringBuilder r9 = new java.lang.StringBuilder
            r9.<init>()
            r9.append(r6)
            r9.append(r7)
            r9.append(r5)
            java.lang.String r9 = r9.toString()
            java.lang.String r10 = "position"
            r12.attribute(r8, r10, r9)
            r4 = 0
        L_0x008a:
            java.lang.Object r8 = r13.elementAt(r7)
            r11.writeProperty(r12, r8, r14)
            r12.endTag(r1, r0)
        L_0x0094:
            int r7 = r7 + 1
            goto L_0x0061
        L_0x0097:
            return
        */
        throw new UnsupportedOperationException("Method not decompiled: org.ksoap2.serialization.SoapSerializationEnvelope.writeVectorBody(org.xmlpull.v1.XmlSerializer, java.util.Vector, org.ksoap2.serialization.PropertyInfo):void");
    }
}
