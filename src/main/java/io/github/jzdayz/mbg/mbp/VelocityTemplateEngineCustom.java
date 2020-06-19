package io.github.jzdayz.mbg.mbp;

import com.baomidou.mybatisplus.core.toolkit.StringPool;
import com.baomidou.mybatisplus.core.toolkit.StringUtils;
import com.baomidou.mybatisplus.generator.config.ConstVal;
import com.baomidou.mybatisplus.generator.config.builder.ConfigBuilder;
import com.baomidou.mybatisplus.generator.config.po.TableInfo;
import com.baomidou.mybatisplus.generator.config.rules.FileType;
import com.baomidou.mybatisplus.generator.engine.VelocityTemplateEngine;
import io.github.jzdayz.mbg.util.ThreadLocalUtils;
import java.io.BufferedWriter;
import java.io.ByteArrayOutputStream;
import java.io.OutputStreamWriter;
import java.util.Map;
import java.util.Properties;
import org.apache.velocity.Template;
import org.apache.velocity.VelocityContext;
import org.apache.velocity.app.Velocity;
import org.apache.velocity.app.VelocityEngine;

public class VelocityTemplateEngineCustom extends VelocityTemplateEngine {

  private static final String DOT_VM = ".vm";
  private VelocityEngine velocityEngine;

  @Override
  protected boolean isCreate(FileType fileType, String filePath) {
    return true;
  }

  @Override
  public VelocityTemplateEngine init(ConfigBuilder configBuilder) {
    super.init(configBuilder);
    if (null == velocityEngine) {
      Properties p = new Properties();
      p.setProperty(ConstVal.VM_LOAD_PATH_KEY, ConstVal.VM_LOAD_PATH_VALUE);
      p.setProperty(Velocity.FILE_RESOURCE_LOADER_PATH, StringPool.EMPTY);
      p.setProperty(Velocity.ENCODING_DEFAULT, ConstVal.UTF8);
      p.setProperty(Velocity.INPUT_ENCODING, ConstVal.UTF8);
      p.setProperty("file.resource.loader.unicode", StringPool.TRUE);
      velocityEngine = new VelocityEngine(p);
    }
    return this;
  }

  @Override
  public void writer(Map<String, Object> objectMap, String templatePath, String outputFile)
      throws Exception {
    if (StringUtils.isBlank(templatePath)) {
      return;
    }
    Template template = velocityEngine.getTemplate(templatePath, ConstVal.UTF8);
    ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream(1024 * 1024);
    try (
        OutputStreamWriter ow = new OutputStreamWriter(byteArrayOutputStream, ConstVal.UTF8);
        BufferedWriter writer = new BufferedWriter(ow)) {
      TableInfo table = (TableInfo) objectMap.get("table");
      table.setConvert(true);
      template.merge(new VelocityContext(objectMap), writer);
    }
    outputFile = outputFile.substring(4);
    ThreadLocalUtils.ZIP_ENTRY.get().add(
        ThreadLocalUtils.Zip.builder().data(byteArrayOutputStream.toByteArray()).name(outputFile)
            .build());
  }
}
