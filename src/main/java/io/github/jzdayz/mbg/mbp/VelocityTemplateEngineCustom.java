package io.github.jzdayz.mbg.mbp;

import com.baomidou.mybatisplus.core.toolkit.StringPool;
import com.baomidou.mybatisplus.core.toolkit.StringUtils;
import com.baomidou.mybatisplus.generator.config.ConstVal;
import com.baomidou.mybatisplus.generator.config.builder.ConfigBuilder;
import com.baomidou.mybatisplus.generator.config.rules.FileType;
import com.baomidou.mybatisplus.generator.engine.AbstractTemplateEngine;
import com.baomidou.mybatisplus.generator.engine.VelocityTemplateEngine;
import io.github.jzdayz.mbg.util.ThreadLocalUtils;
import org.apache.velocity.Template;
import org.apache.velocity.VelocityContext;
import org.apache.velocity.app.Velocity;
import org.apache.velocity.app.VelocityEngine;

import java.io.BufferedWriter;
import java.io.ByteArrayOutputStream;
import java.io.OutputStreamWriter;
import java.util.Map;
import java.util.Properties;

public class VelocityTemplateEngineCustom extends AbstractTemplateEngine {
    
    private static final String DOT_VM = ".vm";
    
    private VelocityEngine velocityEngine;
    
    @Override
    protected boolean isCreate(FileType fileType, String filePath) {
        return true;
    }
    
    @Override
    public AbstractTemplateEngine init(ConfigBuilder configBuilder) {
        super.init(configBuilder);
        if (null == velocityEngine) {
            Properties p = new Properties();
            p.setProperty("resource.loader.file.class", ConstVal.VM_LOAD_PATH_VALUE);
            p.setProperty(Velocity.FILE_RESOURCE_LOADER_PATH, StringPool.EMPTY);
            p.setProperty(Velocity.ENCODING_DEFAULT, ConstVal.UTF8);
            p.setProperty(Velocity.INPUT_ENCODING, ConstVal.UTF8);
            p.setProperty("resource.loader.file.unicode", StringPool.TRUE);
            velocityEngine = new VelocityEngine(p);
        }
        return this;
    }
    
    @Override
    public void writer(Map<String, Object> objectMap, String templatePath, String outputFile) throws Exception {
        if (StringUtils.isBlank(templatePath)) {
            return;
        }
        Template template = velocityEngine.getTemplate(templatePath, ConstVal.UTF8);
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream(1024 * 1024);
        try (OutputStreamWriter ow = new OutputStreamWriter(byteArrayOutputStream,
                ConstVal.UTF8); BufferedWriter writer = new BufferedWriter(ow)) {
            template.merge(new VelocityContext(objectMap), writer);
        }
        outputFile = outputFile.substring(4);
        ThreadLocalUtils.ZIP_ENTRY.get()
                .add(ThreadLocalUtils.Zip.builder().data(byteArrayOutputStream.toByteArray()).name(outputFile).build());
    }
    
    @Override
    public String templateFilePath(String filePath) {
        if (null == filePath || filePath.contains(DOT_VM)) {
            return filePath;
        }
        return filePath + DOT_VM;
    }
    
    @Override
    public AbstractTemplateEngine mkdirs() {
        // nothing to do
        return this;
    }
}
