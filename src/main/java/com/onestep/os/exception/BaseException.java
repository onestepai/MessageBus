//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//

package com.onestep.os.exception;

public class BaseException extends RuntimeException {
    private String message;

    public String getMessage() {
        return this.message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public BaseException(String message) {
        this.message = message;
    }
}
