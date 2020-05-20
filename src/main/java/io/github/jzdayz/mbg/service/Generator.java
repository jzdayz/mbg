package io.github.jzdayz.mbg.service;

import io.github.jzdayz.mbg.Arg;

public interface Generator {

    enum Type{MB,MBP}

    boolean canProcessor(Type type);

    byte[] gen(Arg arg) throws Exception;
}
