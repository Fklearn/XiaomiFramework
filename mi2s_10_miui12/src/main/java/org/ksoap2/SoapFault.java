package org.ksoap2;

import java.io.IOException;
import miui.provider.Weather;
import miui.telephony.phonenumber.Prefix;
import org.ksoap2.kdom.Node;
import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;
import org.xmlpull.v1.XmlSerializer;

public class SoapFault extends IOException {
    private static final long serialVersionUID = 1011001;
    public Node detail;
    public String faultactor;
    public String faultcode;
    public String faultstring;
    public int version;

    public SoapFault() {
        this.version = SoapEnvelope.VER11;
    }

    public SoapFault(int version2) {
        this.version = version2;
    }

    public void parse(XmlPullParser parser) throws IOException, XmlPullParserException {
        parser.require(2, SoapEnvelope.ENV, "Fault");
        while (parser.nextTag() == 2) {
            String name = parser.getName();
            if (name.equals(Weather.AlertInfo.DETAIL)) {
                this.detail = new Node();
                this.detail.parse(parser);
                if (parser.getNamespace().equals(SoapEnvelope.ENV) && parser.getName().equals("Fault")) {
                    break;
                }
            } else {
                if (name.equals("faultcode")) {
                    this.faultcode = parser.nextText();
                } else if (name.equals("faultstring")) {
                    this.faultstring = parser.nextText();
                } else if (name.equals("faultactor")) {
                    this.faultactor = parser.nextText();
                } else {
                    throw new RuntimeException("unexpected tag:" + name);
                }
                parser.require(3, (String) null, name);
            }
        }
        parser.require(3, SoapEnvelope.ENV, "Fault");
        parser.nextTag();
    }

    public void write(XmlSerializer xw) throws IOException {
        xw.startTag(SoapEnvelope.ENV, "Fault");
        xw.startTag((String) null, "faultcode");
        xw.text(Prefix.EMPTY + this.faultcode);
        xw.endTag((String) null, "faultcode");
        xw.startTag((String) null, "faultstring");
        xw.text(Prefix.EMPTY + this.faultstring);
        xw.endTag((String) null, "faultstring");
        xw.startTag((String) null, Weather.AlertInfo.DETAIL);
        Node node = this.detail;
        if (node != null) {
            node.write(xw);
        }
        xw.endTag((String) null, Weather.AlertInfo.DETAIL);
        xw.endTag(SoapEnvelope.ENV, "Fault");
    }

    public String getMessage() {
        return this.faultstring;
    }

    public String toString() {
        return "SoapFault - faultcode: '" + this.faultcode + "' faultstring: '" + this.faultstring + "' faultactor: '" + this.faultactor + "' detail: " + this.detail;
    }
}
