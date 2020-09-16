package io.github.jzdayz.mbg.service;

import io.github.jzdayz.mbg.Arg;

public interface Generator {

    boolean canProcessor(Type type);

    byte[] gen(Arg arg) throws Exception;

    enum Type {
        MB,
        MBP
    }
}
