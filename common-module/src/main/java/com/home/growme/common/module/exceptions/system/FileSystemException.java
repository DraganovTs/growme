package com.home.growme.common.module.exceptions.system;

import com.home.growme.common.module.exceptions.BaseException;
import org.springframework.http.HttpStatus;

public class FileSystemException extends BaseException {
    public FileSystemException(String operation) {
        super("Filesystem operation failed: " + operation,
                "FILESYSTEM_ERROR",
                HttpStatus.INTERNAL_SERVER_ERROR);
    }
}