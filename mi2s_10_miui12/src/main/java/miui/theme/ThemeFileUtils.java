package miui.theme;

import android.util.Log;
import java.io.BufferedWriter;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.CopyOption;
import java.nio.file.FileVisitResult;
import java.nio.file.FileVisitor;
import java.nio.file.Files;
import java.nio.file.LinkOption;
import java.nio.file.OpenOption;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.SimpleFileVisitor;
import java.nio.file.StandardCopyOption;
import java.nio.file.attribute.BasicFileAttributes;
import java.nio.file.attribute.FileAttribute;
import miui.content.res.ThemeNativeUtils;
import miui.util.IOUtils;

public class ThemeFileUtils
{
  private static final String TAG = "ThemeFileUtils";
  
  public static boolean copy(String paramString1, String paramString2)
  {
    Object localObject = null;
    boolean bool = true;
    try
    {
      Path localPath = Files.copy(Paths.get(paramString1, new String[0]), Paths.get(paramString2, new String[0]), new CopyOption[] { StandardCopyOption.REPLACE_EXISTING });
      paramString1 = localPath;
    }
    catch (IOException localIOException)
    {
      StringBuilder localStringBuilder = new StringBuilder();
      localStringBuilder.append("Failed to copy( ");
      localStringBuilder.append(paramString1);
      localStringBuilder.append(" to + ");
      localStringBuilder.append(paramString2);
      localStringBuilder.append(" ): ");
      localStringBuilder.append(localIOException);
      Log.e("ThemeFileUtils", localStringBuilder.toString());
      paramString1 = (String)localObject;
    }
    if (paramString1 == null) {
      bool = false;
    }
    return bool;
  }
  
  public static void deleteContents(String paramString)
  {
    Path localPath = Paths.get(paramString, new String[0]);
    try
    {
      if (Files.isDirectory(localPath, new LinkOption[0]))
      {
        localObject = new miui/theme/ThemeFileUtils$1;
        ((1)localObject).<init>(localPath);
        Files.walkFileTree(localPath, (FileVisitor)localObject);
      }
    }
    catch (IOException localIOException)
    {
      Object localObject = new StringBuilder();
      ((StringBuilder)localObject).append("Failed to deleteContentsExceptDir error ( ");
      ((StringBuilder)localObject).append(paramString);
      ((StringBuilder)localObject).append(" ) : ");
      ((StringBuilder)localObject).append(localIOException);
      Log.e("ThemeFileUtils", ((StringBuilder)localObject).toString());
    }
  }
  
  private static void deleteContentsAndDir(String paramString)
    throws IOException
  {
    deleteContents(paramString);
    Files.deleteIfExists(Paths.get(paramString, new String[0]));
  }
  
  public static void link(String paramString1, String paramString2)
    throws IOException
  {
    Files.createSymbolicLink(Paths.get(paramString2, new String[0]), Paths.get(paramString1, new String[0]), new FileAttribute[0]);
  }
  
  public static boolean mkdirs(String paramString)
  {
    Object localObject = Paths.get(paramString, new String[0]);
    if (!Files.exists((Path)localObject, new LinkOption[0])) {
      try
      {
        Files.createDirectories((Path)localObject, new FileAttribute[0]);
        ThemeNativeUtils.updateFilePermissionWithThemeContext(paramString);
        return true;
      }
      catch (IOException localIOException)
      {
        localObject = new StringBuilder();
        ((StringBuilder)localObject).append("Failed to mkdirs( ");
        ((StringBuilder)localObject).append(paramString);
        ((StringBuilder)localObject).append(" ): ");
        ((StringBuilder)localObject).append(localIOException);
        Log.e("ThemeFileUtils", ((StringBuilder)localObject).toString());
        return false;
      }
    }
    return false;
  }
  
  public static boolean remove(String paramString)
  {
    try
    {
      deleteContentsAndDir(paramString);
      return true;
    }
    catch (IOException localIOException)
    {
      StringBuilder localStringBuilder = new StringBuilder();
      localStringBuilder.append("Failed to remove( ");
      localStringBuilder.append(paramString);
      localStringBuilder.append(" ): ");
      localStringBuilder.append(localIOException);
      Log.e("ThemeFileUtils", localStringBuilder.toString());
    }
    return false;
  }
  
  public static void write(String paramString1, String paramString2)
    throws IOException
  {
    String str = null;
    try
    {
      paramString1 = Files.newBufferedWriter(Paths.get(paramString1, new String[0]), StandardCharsets.UTF_8, new OpenOption[0]);
      str = paramString1;
      paramString1.write(paramString2);
      str = paramString1;
      paramString1.flush();
      return;
    }
    finally
    {
      IOUtils.closeQuietly(str);
    }
  }
}


/* Location:              /Users/sanbo/Desktop/framework/miui/framework/classes4-dex2jar.jar!/miui/theme/ThemeFileUtils.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */