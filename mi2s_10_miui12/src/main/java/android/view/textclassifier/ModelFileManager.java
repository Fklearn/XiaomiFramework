package android.view.textclassifier;

import android.os.LocaleList;
import android.os.ParcelFileDescriptor;
import android.text.TextUtils;
import com.android.internal.annotations.VisibleForTesting;
import com.android.internal.annotations.VisibleForTesting.Visibility;
import com.android.internal.util.Preconditions;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.Locale.LanguageRange;
import java.util.Objects;
import java.util.StringJoiner;
import java.util.function.Function;
import java.util.function.Supplier;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@VisibleForTesting(visibility=VisibleForTesting.Visibility.PACKAGE)
public final class ModelFileManager
{
  private final Object mLock = new Object();
  private final Supplier<List<ModelFile>> mModelFileSupplier;
  private List<ModelFile> mModelFiles;
  
  public ModelFileManager(Supplier<List<ModelFile>> paramSupplier)
  {
    this.mModelFileSupplier = ((Supplier)Preconditions.checkNotNull(paramSupplier));
  }
  
  public ModelFile findBestModelFile(LocaleList paramLocaleList)
  {
    if ((paramLocaleList != null) && (!paramLocaleList.isEmpty())) {
      paramLocaleList = paramLocaleList.toLanguageTags();
    } else {
      paramLocaleList = LocaleList.getDefault().toLanguageTags();
    }
    List localList = Locale.LanguageRange.parse(paramLocaleList);
    paramLocaleList = null;
    Iterator localIterator = listModelFiles().iterator();
    while (localIterator.hasNext())
    {
      ModelFile localModelFile = (ModelFile)localIterator.next();
      Object localObject = paramLocaleList;
      if (localModelFile.isAnyLanguageSupported(localList))
      {
        localObject = paramLocaleList;
        if (localModelFile.isPreferredTo(paramLocaleList)) {
          localObject = localModelFile;
        }
      }
      paramLocaleList = (LocaleList)localObject;
    }
    return paramLocaleList;
  }
  
  public List<ModelFile> listModelFiles()
  {
    synchronized (this.mLock)
    {
      if (this.mModelFiles == null) {
        this.mModelFiles = Collections.unmodifiableList((List)this.mModelFileSupplier.get());
      }
      List localList = this.mModelFiles;
      return localList;
    }
  }
  
  public static final class ModelFile
  {
    public static final String LANGUAGE_INDEPENDENT = "*";
    private final File mFile;
    private final boolean mLanguageIndependent;
    private final List<Locale> mSupportedLocales;
    private final String mSupportedLocalesStr;
    private final int mVersion;
    
    public ModelFile(File paramFile, int paramInt, List<Locale> paramList, String paramString, boolean paramBoolean)
    {
      this.mFile = ((File)Preconditions.checkNotNull(paramFile));
      this.mVersion = paramInt;
      this.mSupportedLocales = ((List)Preconditions.checkNotNull(paramList));
      this.mSupportedLocalesStr = ((String)Preconditions.checkNotNull(paramString));
      this.mLanguageIndependent = paramBoolean;
    }
    
    public boolean equals(Object paramObject)
    {
      if (this == paramObject) {
        return true;
      }
      if ((paramObject instanceof ModelFile))
      {
        paramObject = (ModelFile)paramObject;
        return TextUtils.equals(getPath(), ((ModelFile)paramObject).getPath());
      }
      return false;
    }
    
    public String getName()
    {
      return this.mFile.getName();
    }
    
    public String getPath()
    {
      return this.mFile.getAbsolutePath();
    }
    
    public List<Locale> getSupportedLocales()
    {
      return Collections.unmodifiableList(this.mSupportedLocales);
    }
    
    public String getSupportedLocalesStr()
    {
      return this.mSupportedLocalesStr;
    }
    
    public int getVersion()
    {
      return this.mVersion;
    }
    
    public int hashCode()
    {
      return Objects.hash(new Object[] { getPath() });
    }
    
    public boolean isAnyLanguageSupported(List<Locale.LanguageRange> paramList)
    {
      Preconditions.checkNotNull(paramList);
      boolean bool;
      if ((!this.mLanguageIndependent) && (Locale.lookup(paramList, this.mSupportedLocales) == null)) {
        bool = false;
      } else {
        bool = true;
      }
      return bool;
    }
    
    public boolean isPreferredTo(ModelFile paramModelFile)
    {
      if (paramModelFile == null) {
        return true;
      }
      if ((!this.mLanguageIndependent) && (paramModelFile.mLanguageIndependent)) {
        return true;
      }
      if ((this.mLanguageIndependent) && (!paramModelFile.mLanguageIndependent)) {
        return false;
      }
      return this.mVersion > paramModelFile.getVersion();
    }
    
    public String toString()
    {
      StringJoiner localStringJoiner = new StringJoiner(",");
      Iterator localIterator = this.mSupportedLocales.iterator();
      while (localIterator.hasNext()) {
        localStringJoiner.add(((Locale)localIterator.next()).toLanguageTag());
      }
      return String.format(Locale.US, "ModelFile { path=%s name=%s version=%d locales=%s }", new Object[] { getPath(), getName(), Integer.valueOf(this.mVersion), localStringJoiner.toString() });
    }
  }
  
  public static final class ModelFileSupplierImpl
    implements Supplier<List<ModelFileManager.ModelFile>>
  {
    private final File mFactoryModelDir;
    private final Pattern mModelFilenamePattern;
    private final Function<Integer, String> mSupportedLocalesSupplier;
    private final File mUpdatedModelFile;
    private final Function<Integer, Integer> mVersionSupplier;
    
    public ModelFileSupplierImpl(File paramFile1, String paramString, File paramFile2, Function<Integer, Integer> paramFunction, Function<Integer, String> paramFunction1)
    {
      this.mUpdatedModelFile = ((File)Preconditions.checkNotNull(paramFile2));
      this.mFactoryModelDir = ((File)Preconditions.checkNotNull(paramFile1));
      this.mModelFilenamePattern = Pattern.compile((String)Preconditions.checkNotNull(paramString));
      this.mVersionSupplier = ((Function)Preconditions.checkNotNull(paramFunction));
      this.mSupportedLocalesSupplier = ((Function)Preconditions.checkNotNull(paramFunction1));
    }
    
    /* Error */
    private ModelFileManager.ModelFile createModelFile(File paramFile)
    {
      // Byte code:
      //   0: aload_1
      //   1: invokevirtual 64	java/io/File:exists	()Z
      //   4: ifne +5 -> 9
      //   7: aconst_null
      //   8: areturn
      //   9: aconst_null
      //   10: astore_2
      //   11: aconst_null
      //   12: astore_3
      //   13: aload_1
      //   14: ldc 65
      //   16: invokestatic 71	android/os/ParcelFileDescriptor:open	(Ljava/io/File;I)Landroid/os/ParcelFileDescriptor;
      //   19: astore 4
      //   21: aload 4
      //   23: ifnonnull +10 -> 33
      //   26: aload 4
      //   28: invokestatic 75	android/view/textclassifier/ModelFileManager$ModelFileSupplierImpl:maybeCloseAndLogError	(Landroid/os/ParcelFileDescriptor;)V
      //   31: aconst_null
      //   32: areturn
      //   33: aload 4
      //   35: astore_3
      //   36: aload 4
      //   38: astore_2
      //   39: aload 4
      //   41: invokevirtual 79	android/os/ParcelFileDescriptor:getFd	()I
      //   44: istore 5
      //   46: aload 4
      //   48: astore_3
      //   49: aload 4
      //   51: astore_2
      //   52: aload_0
      //   53: getfield 51	android/view/textclassifier/ModelFileManager$ModelFileSupplierImpl:mVersionSupplier	Ljava/util/function/Function;
      //   56: iload 5
      //   58: invokestatic 85	java/lang/Integer:valueOf	(I)Ljava/lang/Integer;
      //   61: invokeinterface 88 2 0
      //   66: checkcast 81	java/lang/Integer
      //   69: invokevirtual 91	java/lang/Integer:intValue	()I
      //   72: istore 6
      //   74: aload 4
      //   76: astore_3
      //   77: aload 4
      //   79: astore_2
      //   80: aload_0
      //   81: getfield 53	android/view/textclassifier/ModelFileManager$ModelFileSupplierImpl:mSupportedLocalesSupplier	Ljava/util/function/Function;
      //   84: iload 5
      //   86: invokestatic 85	java/lang/Integer:valueOf	(I)Ljava/lang/Integer;
      //   89: invokeinterface 88 2 0
      //   94: checkcast 39	java/lang/String
      //   97: astore 7
      //   99: aload 4
      //   101: astore_3
      //   102: aload 4
      //   104: astore_2
      //   105: aload 7
      //   107: invokevirtual 94	java/lang/String:isEmpty	()Z
      //   110: ifeq +78 -> 188
      //   113: aload 4
      //   115: astore_3
      //   116: aload 4
      //   118: astore_2
      //   119: new 96	java/lang/StringBuilder
      //   122: astore 8
      //   124: aload 4
      //   126: astore_3
      //   127: aload 4
      //   129: astore_2
      //   130: aload 8
      //   132: invokespecial 97	java/lang/StringBuilder:<init>	()V
      //   135: aload 4
      //   137: astore_3
      //   138: aload 4
      //   140: astore_2
      //   141: aload 8
      //   143: ldc 99
      //   145: invokevirtual 103	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
      //   148: pop
      //   149: aload 4
      //   151: astore_3
      //   152: aload 4
      //   154: astore_2
      //   155: aload 8
      //   157: aload_1
      //   158: invokevirtual 107	java/io/File:getAbsolutePath	()Ljava/lang/String;
      //   161: invokevirtual 103	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
      //   164: pop
      //   165: aload 4
      //   167: astore_3
      //   168: aload 4
      //   170: astore_2
      //   171: ldc 109
      //   173: aload 8
      //   175: invokevirtual 112	java/lang/StringBuilder:toString	()Ljava/lang/String;
      //   178: invokestatic 118	android/view/textclassifier/Log:d	(Ljava/lang/String;Ljava/lang/String;)V
      //   181: aload 4
      //   183: invokestatic 75	android/view/textclassifier/ModelFileManager$ModelFileSupplierImpl:maybeCloseAndLogError	(Landroid/os/ParcelFileDescriptor;)V
      //   186: aconst_null
      //   187: areturn
      //   188: aload 4
      //   190: astore_3
      //   191: aload 4
      //   193: astore_2
      //   194: new 120	java/util/ArrayList
      //   197: astore 8
      //   199: aload 4
      //   201: astore_3
      //   202: aload 4
      //   204: astore_2
      //   205: aload 8
      //   207: invokespecial 121	java/util/ArrayList:<init>	()V
      //   210: aload 4
      //   212: astore_3
      //   213: aload 4
      //   215: astore_2
      //   216: aload 7
      //   218: ldc 123
      //   220: invokevirtual 127	java/lang/String:split	(Ljava/lang/String;)[Ljava/lang/String;
      //   223: astore 9
      //   225: aload 4
      //   227: astore_3
      //   228: aload 4
      //   230: astore_2
      //   231: aload 9
      //   233: arraylength
      //   234: istore 10
      //   236: iconst_0
      //   237: istore 5
      //   239: iload 5
      //   241: iload 10
      //   243: if_icmpge +31 -> 274
      //   246: aload 4
      //   248: astore_3
      //   249: aload 4
      //   251: astore_2
      //   252: aload 8
      //   254: aload 9
      //   256: iload 5
      //   258: aaload
      //   259: invokestatic 133	java/util/Locale:forLanguageTag	(Ljava/lang/String;)Ljava/util/Locale;
      //   262: invokeinterface 139 2 0
      //   267: pop
      //   268: iinc 5 1
      //   271: goto -32 -> 239
      //   274: aload 4
      //   276: astore_3
      //   277: aload 4
      //   279: astore_2
      //   280: new 141	android/view/textclassifier/ModelFileManager$ModelFile
      //   283: dup
      //   284: aload_1
      //   285: iload 6
      //   287: aload 8
      //   289: aload 7
      //   291: ldc -113
      //   293: aload 7
      //   295: invokevirtual 146	java/lang/String:equals	(Ljava/lang/Object;)Z
      //   298: invokespecial 149	android/view/textclassifier/ModelFileManager$ModelFile:<init>	(Ljava/io/File;ILjava/util/List;Ljava/lang/String;Z)V
      //   301: astore 8
      //   303: aload 4
      //   305: invokestatic 75	android/view/textclassifier/ModelFileManager$ModelFileSupplierImpl:maybeCloseAndLogError	(Landroid/os/ParcelFileDescriptor;)V
      //   308: aload 8
      //   310: areturn
      //   311: astore_1
      //   312: goto +61 -> 373
      //   315: astore 8
      //   317: aload_2
      //   318: astore_3
      //   319: new 96	java/lang/StringBuilder
      //   322: astore 4
      //   324: aload_2
      //   325: astore_3
      //   326: aload 4
      //   328: invokespecial 97	java/lang/StringBuilder:<init>	()V
      //   331: aload_2
      //   332: astore_3
      //   333: aload 4
      //   335: ldc -105
      //   337: invokevirtual 103	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
      //   340: pop
      //   341: aload_2
      //   342: astore_3
      //   343: aload 4
      //   345: aload_1
      //   346: invokevirtual 107	java/io/File:getAbsolutePath	()Ljava/lang/String;
      //   349: invokevirtual 103	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
      //   352: pop
      //   353: aload_2
      //   354: astore_3
      //   355: ldc 109
      //   357: aload 4
      //   359: invokevirtual 112	java/lang/StringBuilder:toString	()Ljava/lang/String;
      //   362: aload 8
      //   364: invokestatic 155	android/view/textclassifier/Log:e	(Ljava/lang/String;Ljava/lang/String;Ljava/lang/Throwable;)V
      //   367: aload_2
      //   368: invokestatic 75	android/view/textclassifier/ModelFileManager$ModelFileSupplierImpl:maybeCloseAndLogError	(Landroid/os/ParcelFileDescriptor;)V
      //   371: aconst_null
      //   372: areturn
      //   373: aload_3
      //   374: invokestatic 75	android/view/textclassifier/ModelFileManager$ModelFileSupplierImpl:maybeCloseAndLogError	(Landroid/os/ParcelFileDescriptor;)V
      //   377: aload_1
      //   378: athrow
      // Local variable table:
      //   start	length	slot	name	signature
      //   0	379	0	this	ModelFileSupplierImpl
      //   0	379	1	paramFile	File
      //   10	358	2	localObject1	Object
      //   12	362	3	localObject2	Object
      //   19	339	4	localObject3	Object
      //   44	225	5	i	int
      //   72	214	6	j	int
      //   97	197	7	str	String
      //   122	187	8	localObject4	Object
      //   315	48	8	localFileNotFoundException	java.io.FileNotFoundException
      //   223	32	9	arrayOfString	String[]
      //   234	10	10	k	int
      // Exception table:
      //   from	to	target	type
      //   13	21	311	finally
      //   39	46	311	finally
      //   52	74	311	finally
      //   80	99	311	finally
      //   105	113	311	finally
      //   119	124	311	finally
      //   130	135	311	finally
      //   141	149	311	finally
      //   155	165	311	finally
      //   171	181	311	finally
      //   194	199	311	finally
      //   205	210	311	finally
      //   216	225	311	finally
      //   231	236	311	finally
      //   252	268	311	finally
      //   280	303	311	finally
      //   319	324	311	finally
      //   326	331	311	finally
      //   333	341	311	finally
      //   343	353	311	finally
      //   355	367	311	finally
      //   13	21	315	java/io/FileNotFoundException
      //   39	46	315	java/io/FileNotFoundException
      //   52	74	315	java/io/FileNotFoundException
      //   80	99	315	java/io/FileNotFoundException
      //   105	113	315	java/io/FileNotFoundException
      //   119	124	315	java/io/FileNotFoundException
      //   130	135	315	java/io/FileNotFoundException
      //   141	149	315	java/io/FileNotFoundException
      //   155	165	315	java/io/FileNotFoundException
      //   171	181	315	java/io/FileNotFoundException
      //   194	199	315	java/io/FileNotFoundException
      //   205	210	315	java/io/FileNotFoundException
      //   216	225	315	java/io/FileNotFoundException
      //   231	236	315	java/io/FileNotFoundException
      //   252	268	315	java/io/FileNotFoundException
      //   280	303	315	java/io/FileNotFoundException
    }
    
    private static void maybeCloseAndLogError(ParcelFileDescriptor paramParcelFileDescriptor)
    {
      if (paramParcelFileDescriptor == null) {
        return;
      }
      try
      {
        paramParcelFileDescriptor.close();
      }
      catch (IOException paramParcelFileDescriptor)
      {
        Log.e("androidtc", "Error closing file.", paramParcelFileDescriptor);
      }
    }
    
    public List<ModelFileManager.ModelFile> get()
    {
      ArrayList localArrayList = new ArrayList();
      Object localObject1;
      if (this.mUpdatedModelFile.exists())
      {
        localObject1 = createModelFile(this.mUpdatedModelFile);
        if (localObject1 != null) {
          localArrayList.add(localObject1);
        }
      }
      if ((this.mFactoryModelDir.exists()) && (this.mFactoryModelDir.isDirectory())) {
        for (Object localObject2 : this.mFactoryModelDir.listFiles()) {
          if ((this.mModelFilenamePattern.matcher(((File)localObject2).getName()).matches()) && (((File)localObject2).isFile()))
          {
            localObject2 = createModelFile((File)localObject2);
            if (localObject2 != null) {
              localArrayList.add(localObject2);
            }
          }
        }
      }
      return localArrayList;
    }
  }
}


/* Location:              /Users/sanbo/Desktop/framework/miui/framework/classes3-dex2jar.jar!/android/view/textclassifier/ModelFileManager.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */