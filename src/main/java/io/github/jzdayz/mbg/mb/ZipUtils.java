package io.github.jzdayz.mbg.mb;

import org.mybatis.generator.api.GeneratedJavaFile;
import org.mybatis.generator.api.GeneratedXmlFile;

import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

public class ZipUtils {
    public static void xmlJavaZip(List<GeneratedXmlFile> xml, List<GeneratedJavaFile> java, ZipOutputStream zp) throws Exception{
        for (GeneratedXmlFile x : xml) {
            zp.putNextEntry(new ZipEntry(x.getTargetPackage().replaceAll("\\.","/")+"/"+x.getFileName()));
            zp.write(x.getFormattedContent().getBytes(StandardCharsets.UTF_8));
        }
        for (GeneratedJavaFile j : java) {
            zp.putNextEntry(new ZipEntry(j.getTargetPackage().replaceAll("\\.","/")+"/"+j.getFileName()));
            zp.write(j.getFormattedContent().getBytes(StandardCharsets.UTF_8));
        }
    }
}
