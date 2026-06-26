package com.forecourt.global.exception;

/** 수집 과정(API 호출·파싱)에서 발생하는 공통 예외. */
public class CollectorException extends RuntimeException {

    public CollectorException(String message) {
        super(message);
    }

    public CollectorException(String message, Throwable cause) {
        super(message, cause);
    }
}
