package io.github.jzdayz.mbg.service;

import com.baomidou.mybatisplus.annotation.DbType;
import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.generator.AutoGenerator;
import com.baomidou.mybatisplus.generator.config.*;
import com.baomidou.mybatisplus.generator.config.po.LikeTable;
import com.baomidou.mybatisplus.generator.config.rules.NamingStrategy;
import io.github.jzdayz.mbg.Arg;
import io.github.jzdayz.mbg.mbp.VelocityTemplateEngineCustom;
import io.github.jzdayz.mbg.util.ZipUtils;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.io.ByteArrayOutputStream;
import java.util.Objects;
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
        if (!StringUtils.isEmpty(arg.getCatalog())) {
            dsc.setSchemaName(arg.getCatalog());
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
}
