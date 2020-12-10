package io.github.jzdayz.mbg.service;

import com.baomidou.mybatisplus.annotation.DbType;
import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.core.toolkit.StringPool;
import com.baomidou.mybatisplus.generator.AutoGenerator;
import com.baomidou.mybatisplus.generator.config.*;
import com.baomidou.mybatisplus.generator.config.builder.ConfigBuilder;
import com.baomidou.mybatisplus.generator.config.po.LikeTable;
import com.baomidou.mybatisplus.generator.config.po.TableField;
import com.baomidou.mybatisplus.generator.config.po.TableInfo;
import com.baomidou.mybatisplus.generator.config.rules.FileType;
import com.baomidou.mybatisplus.generator.config.rules.NamingStrategy;
import com.baomidou.mybatisplus.generator.engine.AbstractTemplateEngine;
import io.github.jzdayz.mbg.Arg;
import io.github.jzdayz.mbg.util.ThreadLocalUtils;
import io.github.jzdayz.mbg.util.Utils;
import io.github.jzdayz.mbg.util.ZipUtils;
import org.apache.velocity.Template;
import org.apache.velocity.VelocityContext;
import org.apache.velocity.app.Velocity;
import org.apache.velocity.app.VelocityEngine;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.io.BufferedWriter;
import java.io.ByteArrayOutputStream;
import java.io.OutputStreamWriter;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.Properties;
import java.util.zip.ZipOutputStream;

import static com.baomidou.mybatisplus.generator.config.rules.DateType.ONLY_DATE;

@Service
public class MbpGenerator implements Generator {

    @Override
    public boolean canProcessor(Type type) {
        return Objects.equals(Type.MBP, type);
    }

    public byte[] gen(Arg arg) throws Exception {
        AutoGenerator mpg = new AutoGenerator();
        // 全局配置
        GlobalConfig gc = new GlobalConfig();
        gc.setIdType(IdType.INPUT);
        String projectPath = "NONE";
        gc.setOutputDir(projectPath);
        gc.setOpen(false);
        gc.setDateType(ONLY_DATE);
        gc.setBaseResultMap(true);
        gc.setBaseColumnList(true);
        gc.setFileOverride(true);
        gc.setAuthor("MBP");
        gc.setSwagger2(arg.isSwagger2());
        mpg.setGlobalConfig(gc);

        // 数据源配置
        DataSourceConfig dsc = new DataSourceConfig();
        dsc.setUrl(arg.getJdbc());
        dsc.setDriverName(arg.getDbType().getDriver());
        dsc.setUsername(arg.getUser());
        dsc.setPassword(arg.getPwd());
        if (!StringUtils.isEmpty(arg.getSchema())) {
            dsc.setSchemaName(arg.getSchema());
        }
        mpg.setDataSource(dsc);

        // 包配置
        PackageConfig pc = new PackageConfig();
        pc.setParent(arg.getMbpPackage());
        mpg.setPackageInfo(pc);

        // 配置模板
        TemplateConfig templateConfig = new TemplateConfig();
        mpg.setTemplate(templateConfig);

        // 策略配置
        StrategyConfig strategy = new StrategyConfig();
        strategy.setNaming(NamingStrategy.underline_to_camel);
        strategy.setColumnNaming(NamingStrategy.underline_to_camel);
        strategy.setRestControllerStyle(false);
        strategy.setEntityLombokModel(arg.isLombok());
        strategy.setEntityTableFieldAnnotationEnable(true);
        // 微软sqlServer不支持sql过滤
        if (Objects.equals(DbType.SQL_SERVER, dsc.getDbType()) || Objects.equals("REGEX", arg.getTfType())) {
            strategy.setEnableSqlFilter(false);
            strategy.setInclude(arg.getTable());
        }
        // 公共父类
        strategy.setLikeTable(new LikeTable(arg.getTable()));
        strategy.setNameConvert(new INameConvert() {

            // 这些都是原本的
            @Override
            public String entityNameConvert(TableInfo tableInfo) {
                return arg.getTableNameFormat().replaceAll(
                        "\\$\\{entity}"
                        ,NamingStrategy.capitalFirst(Utils.processName(tableInfo.getName(),strategy.getNaming(), strategy.getTablePrefix())));
            }
            // 这些都是原本的
            @Override
            public String propertyNameConvert(TableField field) {
                return Utils.processName(field.getName(),strategy.getColumnNaming(),strategy.getFieldPrefix());
            }

        });
        mpg.setStrategy(strategy);
        mpg.setTemplateEngine(new VelocityTemplateEngineCustom());
        mpg.execute();

        if (!ZipUtils.showZip()) {
            return null;
        }

        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream(1024 * 1024);
        try (ZipOutputStream zipOutputStream = new ZipOutputStream(byteArrayOutputStream)) {
            ZipUtils.zip(zipOutputStream);
            zipOutputStream.flush();
        }
        return byteArrayOutputStream.toByteArray();
    }


    private static class VelocityTemplateEngineCustom extends AbstractTemplateEngine {

        private static final String DOT_VM = ".vm";

        private VelocityEngine velocityEngine;

        @Override
        protected boolean isCreate(FileType fileType, String filePath) {
            return true;
        }

        @Override
        public AbstractTemplateEngine init(ConfigBuilder configBuilder) {
            configBuilder.getPathInfo().remove(ConstVal.CONTROLLER_PATH);
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
            if (com.baomidou.mybatisplus.core.toolkit.StringUtils.isBlank(templatePath)) {
                return;
            }
            Optional.ofNullable(objectMap.get("table"))
                    .ifPresent(e->{
                        final TableInfo ti = (TableInfo) e;
                        ti.getFields().forEach(ee->ee.setConvert(true));
                        ti.setImportPackages(com.baomidou.mybatisplus.annotation.TableField.class.getCanonicalName());
                    });
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

}
