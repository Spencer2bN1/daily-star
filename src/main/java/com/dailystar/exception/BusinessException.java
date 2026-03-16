package com.dailystar.exception;

import com.dailystar.enums.MessageCodeEnum;
import lombok.Getter;

@Getter
public class BusinessException extends RuntimeException {

    private final int code;
    private final MessageCodeEnum messageCode;

    public BusinessException(MessageCodeEnum messageCode) {
        super(messageCode.getMessage());
        this.code = messageCode.getCode();
        this.messageCode = messageCode;
    }

    public BusinessException(MessageCodeEnum messageCode, String message) {
        super(message);
        this.code = messageCode.getCode();
        this.messageCode = messageCode;
    }
}
