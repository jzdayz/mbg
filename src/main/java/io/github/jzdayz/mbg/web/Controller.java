package io.github.jzdayz.mbg.web;

import io.github.jzdayz.mbg.Arg;
import io.github.jzdayz.mbg.mb.PersistenceUtils;
import io.github.jzdayz.mbg.service.MbGenerator;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.util.StringUtils;
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
                    String dao,
                    String model,
                    String xml,
                    String user,
                    String pwd) throws Exception{
        Arg.ArgBuilder toUse = Arg.builder();
        if (lastUse!=null){
            toUse.pwd(chose(pwd,lastUse.getPwd()))
                    .user(chose(user,lastUse.getUser()))
                    .dao(chose(dao,lastUse.getDao()))
                    .xml(chose(xml,lastUse.getXml()))
                    .model(chose(model,lastUse.getModel()))
                    .jdbc(chose(jdbc,lastUse.getJdbc()));
        }else{
            toUse = Arg.builder().jdbc(jdbc).user(user).pwd(pwd).dao(dao).model(model).xml(xml);
        }
        Arg arg = toUse.build();
        filePackage(arg);
        check(arg);
        lastUse = arg;
        persistenceUtils.persistence(lastUse);
        HttpHeaders httpHeaders = new HttpHeaders();
        httpHeaders.add(HttpHeaders.CONTENT_DISPOSITION,"attachment; filename=\"mbg.zip\"");
        return  ResponseEntity.ok()
                .headers(httpHeaders)
                .contentType(APPLICATION_OCTET_STREAM).body(mbGenerator.gen(arg));
    }

    private void filePackage(Arg arg) {
        if (StringUtils.isEmpty(arg.getDao())){
            arg.setDao("test.dao");
        }
        if (StringUtils.isEmpty(arg.getModel())){
            arg.setModel("test.model");
        }
        if (StringUtils.isEmpty(arg.getXml())){
            arg.setXml("test.xml");
        }
    }

    private void check(Arg arg) {
        if (StringUtils.isEmpty(arg.getJdbc())||StringUtils.isEmpty(arg.getPwd())||StringUtils.isEmpty(arg.getUser())){
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
