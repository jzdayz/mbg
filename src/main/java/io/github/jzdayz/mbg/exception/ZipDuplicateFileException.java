package io.github.jzdayz.mbg.exception;

public class ZipDuplicateFileException extends RuntimeException {

    public ZipDuplicateFileException(String message) {
        super(message);
    }

    public ZipDuplicateFileException(Throwable cause) {
        super(cause);
    }
}
