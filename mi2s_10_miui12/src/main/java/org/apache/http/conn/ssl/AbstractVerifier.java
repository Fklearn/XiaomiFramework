package org.apache.http.conn.ssl;

import android.annotation.UnsupportedAppUsage;
import java.io.IOException;
import java.security.cert.CertificateParsingException;
import java.security.cert.X509Certificate;
import java.util.Arrays;
import java.util.Collection;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Locale;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import javax.net.ssl.SSLException;
import javax.net.ssl.SSLSession;
import javax.net.ssl.SSLSocket;

@Deprecated
public abstract class AbstractVerifier
  implements X509HostnameVerifier
{
  @UnsupportedAppUsage
  private static final String[] BAD_COUNTRY_2LDS;
  private static final Pattern IPV4_PATTERN = Pattern.compile("^(25[0-5]|2[0-4]\\d|[0-1]?\\d?\\d)(\\.(25[0-5]|2[0-4]\\d|[0-1]?\\d?\\d)){3}$");
  
  static
  {
    BAD_COUNTRY_2LDS = new String[] { "ac", "co", "com", "ed", "edu", "go", "gouv", "gov", "info", "lg", "ne", "net", "or", "org" };
    Arrays.sort(BAD_COUNTRY_2LDS);
  }
  
  public static boolean acceptableCountryWildcard(String paramString)
  {
    int i = paramString.length();
    boolean bool = true;
    if ((i >= 7) && (i <= 9) && (paramString.charAt(i - 3) == '.'))
    {
      paramString = paramString.substring(2, i - 3);
      if (Arrays.binarySearch(BAD_COUNTRY_2LDS, paramString) >= 0) {
        bool = false;
      }
      return bool;
    }
    return true;
  }
  
  public static int countDots(String paramString)
  {
    int i = 0;
    int j = 0;
    while (j < paramString.length())
    {
      int k = i;
      if (paramString.charAt(j) == '.') {
        k = i + 1;
      }
      j++;
      i = k;
    }
    return i;
  }
  
  public static String[] getCNs(X509Certificate paramX509Certificate)
  {
    List localList = new AndroidDistinguishedNameParser(paramX509Certificate.getSubjectX500Principal()).getAllMostSpecificFirst("cn");
    if (!localList.isEmpty())
    {
      paramX509Certificate = new String[localList.size()];
      localList.toArray(paramX509Certificate);
      return paramX509Certificate;
    }
    return null;
  }
  
  public static String[] getDNSSubjectAlts(X509Certificate paramX509Certificate)
  {
    LinkedList localLinkedList = new LinkedList();
    Iterator localIterator = null;
    try
    {
      paramX509Certificate = paramX509Certificate.getSubjectAlternativeNames();
    }
    catch (CertificateParsingException paramX509Certificate)
    {
      Logger.getLogger(AbstractVerifier.class.getName()).log(Level.FINE, "Error parsing certificate.", paramX509Certificate);
      paramX509Certificate = localIterator;
    }
    if (paramX509Certificate != null)
    {
      localIterator = paramX509Certificate.iterator();
      while (localIterator.hasNext())
      {
        paramX509Certificate = (List)localIterator.next();
        if (((Integer)paramX509Certificate.get(0)).intValue() == 2) {
          localLinkedList.add((String)paramX509Certificate.get(1));
        }
      }
    }
    if (!localLinkedList.isEmpty())
    {
      paramX509Certificate = new String[localLinkedList.size()];
      localLinkedList.toArray(paramX509Certificate);
      return paramX509Certificate;
    }
    return null;
  }
  
  private static boolean isIPv4Address(String paramString)
  {
    return IPV4_PATTERN.matcher(paramString).matches();
  }
  
  public final void verify(String paramString, X509Certificate paramX509Certificate)
    throws SSLException
  {
    verify(paramString, getCNs(paramX509Certificate), getDNSSubjectAlts(paramX509Certificate));
  }
  
  public final void verify(String paramString, SSLSocket paramSSLSocket)
    throws IOException
  {
    if (paramString != null)
    {
      verify(paramString, (X509Certificate)paramSSLSocket.getSession().getPeerCertificates()[0]);
      return;
    }
    throw new NullPointerException("host to verify is null");
  }
  
  public final void verify(String paramString, String[] paramArrayOfString1, String[] paramArrayOfString2, boolean paramBoolean)
    throws SSLException
  {
    Object localObject = new LinkedList();
    if ((paramArrayOfString1 != null) && (paramArrayOfString1.length > 0) && (paramArrayOfString1[0] != null)) {
      ((LinkedList)localObject).add(paramArrayOfString1[0]);
    }
    int j;
    if (paramArrayOfString2 != null)
    {
      int i = paramArrayOfString2.length;
      for (j = 0; j < i; j++)
      {
        paramArrayOfString1 = paramArrayOfString2[j];
        if (paramArrayOfString1 != null) {
          ((LinkedList)localObject).add(paramArrayOfString1);
        }
      }
    }
    if (!((LinkedList)localObject).isEmpty())
    {
      paramArrayOfString1 = new StringBuffer();
      paramArrayOfString2 = paramString.trim().toLowerCase(Locale.ENGLISH);
      boolean bool1 = false;
      localObject = ((LinkedList)localObject).iterator();
      while (((Iterator)localObject).hasNext())
      {
        String str = ((String)((Iterator)localObject).next()).toLowerCase(Locale.ENGLISH);
        paramArrayOfString1.append(" <");
        paramArrayOfString1.append(str);
        paramArrayOfString1.append('>');
        if (((Iterator)localObject).hasNext()) {
          paramArrayOfString1.append(" OR");
        }
        bool1 = str.startsWith("*.");
        boolean bool2 = true;
        if ((bool1) && (str.indexOf('.', 2) != -1) && (acceptableCountryWildcard(str)) && (!isIPv4Address(paramString))) {
          j = 1;
        } else {
          j = 0;
        }
        if (j != 0)
        {
          boolean bool3 = paramArrayOfString2.endsWith(str.substring(1));
          bool1 = bool3;
          if (bool3)
          {
            bool1 = bool3;
            if (paramBoolean) {
              if (countDots(paramArrayOfString2) == countDots(str)) {
                bool1 = bool2;
              } else {
                bool1 = false;
              }
            }
          }
        }
        else
        {
          bool1 = paramArrayOfString2.equals(str);
        }
        if (bool1) {
          break;
        }
      }
      if (bool1) {
        return;
      }
      paramArrayOfString2 = new StringBuilder();
      paramArrayOfString2.append("hostname in certificate didn't match: <");
      paramArrayOfString2.append(paramString);
      paramArrayOfString2.append("> !=");
      paramArrayOfString2.append(paramArrayOfString1);
      throw new SSLException(paramArrayOfString2.toString());
    }
    paramArrayOfString1 = new StringBuilder();
    paramArrayOfString1.append("Certificate for <");
    paramArrayOfString1.append(paramString);
    paramArrayOfString1.append("> doesn't contain CN or DNS subjectAlt");
    throw new SSLException(paramArrayOfString1.toString());
  }
  
  public final boolean verify(String paramString, SSLSession paramSSLSession)
  {
    try
    {
      verify(paramString, (X509Certificate)paramSSLSession.getPeerCertificates()[0]);
      return true;
    }
    catch (SSLException paramString) {}
    return false;
  }
}


/* Location:              /Users/sanbo/Desktop/framework/miui/framework/classes4-dex2jar.jar!/org/apache/http/conn/ssl/AbstractVerifier.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */