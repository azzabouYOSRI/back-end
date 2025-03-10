package com.yosri.defensy.backend.modules.ingestion.exception;

/**
 * ✅ Custom Exception for CSV Parsing Errors
 */
public class ParsingException extends RuntimeException {
    public ParsingException(String message, Throwable cause) {
        super(message, cause);
    }

    public ParsingException(String message) {
        super(message);
    }
}
