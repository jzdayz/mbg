package io.github.jzdayz.mbg.util;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;

public class ThreadLocalUtils {
    
    public static ThreadLocal<List<Zip>> ZIP_ENTRY = ThreadLocal.withInitial(ArrayList::new);
    
    
    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    @Builder
    public static class Zip {
        
        private String name;
        
        private byte[] data;
    }
}
