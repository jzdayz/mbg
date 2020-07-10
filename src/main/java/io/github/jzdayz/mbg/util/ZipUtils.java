package io.github.jzdayz.mbg.util;

import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;
import org.mybatis.generator.api.GeneratedJavaFile;
import org.mybatis.generator.api.GeneratedXmlFile;

public class ZipUtils {

  public static void xmlJavaZip(List<GeneratedXmlFile> xml, List<GeneratedJavaFile> java,
      ZipOutputStream zp) throws Exception {
    for (GeneratedXmlFile x : xml) {
      zp.putNextEntry(
          new ZipEntry(x.getTargetPackage().replaceAll("\\.", "/") + "/" + x.getFileName()));
      zp.write(x.getFormattedContent().getBytes(StandardCharsets.UTF_8));
    }
    for (GeneratedJavaFile j : java) {
      zp.putNextEntry(
          new ZipEntry(j.getTargetPackage().replaceAll("\\.", "/") + "/" + j.getFileName()));
      zp.write(j.getFormattedContent().getBytes(StandardCharsets.UTF_8));
    }
  }

  public static void zip(ZipOutputStream zipOutputStream) throws Exception {
    for (ThreadLocalUtils.Zip zip : ThreadLocalUtils.ZIP_ENTRY.get()) {
      zipOutputStream.putNextEntry(new ZipEntry(zip.getName()));
      zipOutputStream.write(zip.getData());
    }
    ThreadLocalUtils.ZIP_ENTRY.remove();
  }

  public static boolean showZip(){
    List<ThreadLocalUtils.Zip> zips = ThreadLocalUtils.ZIP_ENTRY.get();
    if ( zips.size() > 0){
      return true;
    }else {
      ThreadLocalUtils.ZIP_ENTRY.remove();
      return false;
    }
  }

}
