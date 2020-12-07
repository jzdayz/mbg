package io.github.jzdayz.mbg.util;

import com.baomidou.mybatisplus.core.toolkit.ArrayUtils;
import com.baomidou.mybatisplus.generator.config.rules.NamingStrategy;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;

import static org.springframework.http.MediaType.APPLICATION_OCTET_STREAM;

public class Utils {

    public static boolean ex(Action action) {
        try {
            action.doSomething();
            return false;
        } catch (Exception e) {
            return true;
        }
    }

    public static ResponseEntity<byte[]> fileResponse(byte[] body, String file) {
        file = file == null ? "mbg.zip" : file;
        HttpHeaders httpHeaders = new HttpHeaders();
        httpHeaders.add(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + file + "\"");
        return ResponseEntity.ok().headers(httpHeaders).contentType(APPLICATION_OCTET_STREAM).body(body);
    }

    interface Action {

        void doSomething();
    }

    public static String processName(String name, NamingStrategy strategy, String[] prefix) {
        String propertyName;
        if (ArrayUtils.isNotEmpty(prefix)) {
            if (strategy == NamingStrategy.underline_to_camel) {
                // 删除前缀、下划线转驼峰
                propertyName = NamingStrategy.removePrefixAndCamel(name, prefix);
            } else {
                // 删除前缀
                propertyName = NamingStrategy.removePrefix(name, prefix);
            }
        } else if (strategy == NamingStrategy.underline_to_camel) {
            // 下划线转驼峰
            propertyName = NamingStrategy.underlineToCamel(name);
        } else {
            // 不处理
            propertyName = name;
        }
        return propertyName;
    }
}
