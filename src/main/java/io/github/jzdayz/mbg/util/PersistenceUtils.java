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
import java.nio.file.Files;
import java.util.concurrent.atomic.AtomicReference;

@Service
public class PersistenceUtils {

    private final ObjectMapper mapper;

    private static final String path = System.getProperty("user.dir") + File.separator + "MB.json";

    private AtomicReference<Arg> arg = new AtomicReference<>();

    public PersistenceUtils(ObjectMapper mapper) {
        this.mapper = mapper;
    }

    public AtomicReference<Arg> getArg() {
        return arg;
    }

    @PostConstruct
    private void init() {
        File file = new File(path);
        if (file.exists()) {
            try (FileInputStream fileInputStream = new FileInputStream(file)) {
                arg.set(
                        mapper.readValue(StreamUtils.copyToString(fileInputStream, StandardCharsets.UTF_8), Arg.class)
                );
            } catch (Exception r) {
                r.printStackTrace();
                throw new RuntimeException(r);
            }
        }
    }

    public synchronized void persistence(Arg o) throws Exception {
        arg.set(o);
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
