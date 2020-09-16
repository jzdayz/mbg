package io.github.jzdayz.mbg.util;

import com.fasterxml.jackson.databind.ObjectMapper;
import io.github.jzdayz.mbg.Arg;
import io.github.jzdayz.mbg.web.Controller;
import org.springframework.stereotype.Service;
import org.springframework.util.StreamUtils;

import javax.annotation.PostConstruct;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.nio.charset.StandardCharsets;

@Service
public class PersistenceUtils {

    private final ObjectMapper mapper;

    private String path;

    public PersistenceUtils(ObjectMapper mapper) {
        this.mapper = mapper;
    }

    @PostConstruct
    private void init() {
        String property = System.getProperty("user.dir");
        String separator = System.getProperty("file.separator");
        path = property + separator + "MB.json";
        File file = new File(path);
        if (file.exists()) {
            try (FileInputStream fileInputStream = new FileInputStream(file)) {
                Controller.lastUse = mapper
                        .readValue(StreamUtils.copyToString(fileInputStream, StandardCharsets.UTF_8), Arg.class);
            } catch (Exception r) {
                r.printStackTrace();
                throw new RuntimeException(r);
            }
        }
    }

    public synchronized void persistence(Arg o) throws Exception {
        File file = new File(path);
        if (!file.exists() && file.createNewFile()) {
        }
        try (FileOutputStream fileOutputStream = new FileOutputStream(file)) {
            fileOutputStream.write(mapper.writeValueAsBytes(o));
            fileOutputStream.flush();
        } catch (Exception r) {
            r.printStackTrace();
            throw new RuntimeException(r);
        }
    }
}
