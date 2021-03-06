package io.github.jzdayz.mbg.service;

import io.github.jzdayz.mbg.Arg;
import io.github.jzdayz.mbg.exception.ZipDuplicateFileException;
import io.github.jzdayz.mbg.util.ZipUtils;
import lombok.extern.slf4j.Slf4j;
import org.mybatis.generator.api.GeneratedJavaFile;
import org.mybatis.generator.api.GeneratedXmlFile;
import org.mybatis.generator.api.MyBatisGenerator;
import org.mybatis.generator.config.*;
import org.mybatis.generator.internal.DefaultShellCallback;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.io.ByteArrayOutputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.zip.ZipException;
import java.util.zip.ZipOutputStream;

@Slf4j
@Service
public class MbGenerator implements Generator {

    @Override
    public boolean canProcessor(Type type) {
        return Objects.equals(Type.MB, type);
    }

    public byte[] gen(Arg arg) throws Exception {

        List<String> warnings = new ArrayList<>();
        boolean overwrite = true;
        Configuration config = new Configuration();
        Context context = new Context(null);
        context.setId("ID");
        context.setTargetRuntime("MyBatis3");
        String DEFAULT = "DEFAULT";

        JDBCConnectionConfiguration jdbcConnectionConfiguration = new JDBCConnectionConfiguration();
        jdbcConnectionConfiguration.setConnectionURL(arg.getJdbc());
        jdbcConnectionConfiguration.setUserId(arg.getUser());
        jdbcConnectionConfiguration.setPassword(arg.getPwd());
        jdbcConnectionConfiguration.setDriverClass(arg.getDbType().getDriver());
        jdbcConnectionConfiguration.addProperty("nullCatalogMeansCurrent", "true");
        context.setJdbcConnectionConfiguration(jdbcConnectionConfiguration);

        TableConfiguration tableConfiguration = new TableConfiguration(context);
        tableConfiguration.setTableName(arg.getTable());
        if (!StringUtils.isEmpty(arg.getSchema())){
            tableConfiguration.setSchema(arg.getSchema());
        }
        context.addTableConfiguration(tableConfiguration);
        JavaClientGeneratorConfiguration javaClientGeneratorConfiguration = new JavaClientGeneratorConfiguration();
        javaClientGeneratorConfiguration.setConfigurationType("XMLMAPPER");
        javaClientGeneratorConfiguration.setTargetPackage(arg.getDao());
        javaClientGeneratorConfiguration.setTargetProject(DEFAULT);
        context.setJavaClientGeneratorConfiguration(javaClientGeneratorConfiguration);

        SqlMapGeneratorConfiguration sqlMapGeneratorConfiguration = new SqlMapGeneratorConfiguration();
        sqlMapGeneratorConfiguration.setTargetProject(DEFAULT);
        sqlMapGeneratorConfiguration.setTargetPackage(arg.getXml());
        context.setSqlMapGeneratorConfiguration(sqlMapGeneratorConfiguration);

        JavaModelGeneratorConfiguration javaModelGeneratorConfiguration = new JavaModelGeneratorConfiguration();
        javaModelGeneratorConfiguration.setTargetPackage(arg.getModel());
        javaModelGeneratorConfiguration.setTargetProject(DEFAULT);
        context.setJavaModelGeneratorConfiguration(javaModelGeneratorConfiguration);

        JavaTypeResolverConfiguration javaTypeResolverConfiguration = new JavaTypeResolverConfiguration();
        javaTypeResolverConfiguration.addProperty("forceBigDecimals", "false");
        context.setJavaTypeResolverConfiguration(javaTypeResolverConfiguration);

        CommentGeneratorConfiguration commentGeneratorConfiguration = new CommentGeneratorConfiguration();
        commentGeneratorConfiguration.addProperty("suppressAllComments", "true");
        commentGeneratorConfiguration.addProperty("suppressDate", "true");
        context.setCommentGeneratorConfiguration(commentGeneratorConfiguration);

        config.addContext(context);

        DefaultShellCallback callback = new DefaultShellCallback(overwrite);
        MyBatisGenerator myBatisGenerator = new MyBatisGenerator(config, callback, warnings);
        myBatisGenerator.generate(null, null, null, false);
        List<GeneratedJavaFile> generatedJavaFiles = myBatisGenerator.getGeneratedJavaFiles();
        List<GeneratedXmlFile> generatedXmlFiles = myBatisGenerator.getGeneratedXmlFiles();
        if (generatedJavaFiles.size() == 0){
            throw new RuntimeException("没有文件生成");
        }

        ByteArrayOutputStream out = new ByteArrayOutputStream(1024 * 1024);
        try (ZipOutputStream zp = new ZipOutputStream(out)) {
            ZipUtils.xmlJavaZip(generatedXmlFiles, generatedJavaFiles, zp);
            zp.flush();
        } catch (ZipException e) {
            throw new ZipDuplicateFileException(e);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        return out.toByteArray();
    }

}
