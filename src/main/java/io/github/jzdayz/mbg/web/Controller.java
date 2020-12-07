package io.github.jzdayz.mbg.web;

import com.fasterxml.jackson.databind.ObjectMapper;
import io.github.jzdayz.mbg.Arg;
import io.github.jzdayz.mbg.service.Generator;
import io.github.jzdayz.mbg.service.MbpGenerator;
import io.github.jzdayz.mbg.util.PersistenceUtils;
import io.github.jzdayz.mbg.util.Utils;
import lombok.AllArgsConstructor;
import org.mybatis.generator.api.MyBatisGenerator;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.List;
import java.util.Map;

import static org.springframework.http.MediaType.APPLICATION_JSON;
import static org.springframework.http.MediaType.APPLICATION_OCTET_STREAM;

@org.springframework.stereotype.Controller
@AllArgsConstructor
public class Controller {

    private final PersistenceUtils persistenceUtils;

    private final MbpGenerator mbpGenerator;

    private final ObjectMapper objectMapper;

    @RequestMapping("/")
    public Object index() {
        return ResponseEntity.status(HttpStatus.MOVED_PERMANENTLY).header(HttpHeaders.LOCATION, "/index.html").build();
    }

    @ResponseBody
    @GetMapping("/arg")
    public Object arg() {
        return persistenceUtils.getArg().get();
    }

    @RequestMapping("/mybatis-plus")
    public Object mbpG(@RequestParam Map<String, Object> map) throws Exception {

        Arg arg = objectMapper.readValue(objectMapper.writeValueAsString(map), Arg.class);
        arg.setDbType(jdbcTypeDeduce(arg.getJdbc()));
        persistenceUtils.persistence(arg);

        byte[] body = mbpGenerator.gen(arg);
        if (body == null) {
            return ResponseEntity.ok().contentType(APPLICATION_JSON)
                    .body(objectMapper.writeValueAsBytes("失败，没有找到对应的表"));
        }

        return Utils.fileResponse(body,null);
    }

    private Arg.DbType jdbcTypeDeduce(String jdbcUrl) {
        if (jdbcUrl.startsWith("jdbc:mysql")) {
            return Arg.DbType.MYSQL;
        }
        if (jdbcUrl.startsWith("jdbc:oracle")) {
            return Arg.DbType.ORACLE;
        }
        if (jdbcUrl.startsWith("jdbc:sqlserver")) {
            return Arg.DbType.SQL_SERVER;
        }
        throw new RuntimeException("not support");
    }

}
