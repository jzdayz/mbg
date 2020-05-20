package io.github.jzdayz.mbg.web;

import io.github.jzdayz.mbg.Arg;
import io.github.jzdayz.mbg.mb.PersistenceUtils;
import io.github.jzdayz.mbg.service.MbGenerator;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import static org.springframework.http.MediaType.APPLICATION_OCTET_STREAM;

@org.springframework.stereotype.Controller
public class Controller {

    public static volatile Arg lastUse = null;

    private MbGenerator mbGenerator;

    private PersistenceUtils persistenceUtils;

    public Controller(MbGenerator mbGenerator, PersistenceUtils persistenceUtils) {
        this.mbGenerator = mbGenerator;
        this.persistenceUtils = persistenceUtils;
    }

    @RequestMapping("/")
    public Object index(){
        return ResponseEntity.status(HttpStatus.MOVED_PERMANENTLY).header(HttpHeaders.LOCATION,"/index.html").build();
    }

    @ResponseBody
    @GetMapping("/mbJson")
    public Object mbJson(){
        return lastUse;
    }

    @RequestMapping("/mbg")
    public Object g(String jdbc,
                    String user,
                    String pwd) throws Exception{
        Arg.ArgBuilder toUse = Arg.builder();
        if (lastUse!=null){
            toUse.pwd(chose(pwd,lastUse.getPwd()))
                    .user(chose(user,lastUse.getUser()))
                    .jdbc(chose(jdbc,lastUse.getJdbc()));
        }else{
            toUse = Arg.builder().jdbc(jdbc).user(user).pwd(pwd);
        }
        Arg arg = toUse.build();
        check(arg);
        lastUse = arg;
        persistenceUtils.persistence(lastUse);
        HttpHeaders httpHeaders = new HttpHeaders();
        httpHeaders.add(HttpHeaders.CONTENT_DISPOSITION,"attachment; filename=\"mbg.zip\"");
        return  ResponseEntity.ok()
                .headers(httpHeaders)
                .contentType(APPLICATION_OCTET_STREAM).body(mbGenerator.gen(arg));
    }

    private void check(Arg arg) {
        if (arg.getJdbc()==null||arg.getPwd()==null||arg.getUser()==null){
            throw new RuntimeException("need arg");
        }
    }

    private <T> T chose(T t1,T t2){
        if (t1==null){
            return t2;
        }
        return t1;
    }

}
