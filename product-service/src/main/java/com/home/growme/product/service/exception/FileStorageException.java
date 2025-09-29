package com.home.growme.product.service.exception;

import com.home.growme.common.module.exceptions.BaseException;
import org.springframework.http.HttpStatus;

public class FileStorageException extends BaseException {
    public FileStorageException(String message) {
        super(message, "FILE_STORAGE_ERROR", HttpStatus.INSUFFICIENT_STORAGE);
    }
}