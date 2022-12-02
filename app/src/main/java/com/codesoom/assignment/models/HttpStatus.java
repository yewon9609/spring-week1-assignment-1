package com.codesoom.assignment.models;

public enum HttpStatus2{
    OK(200),
    CREATED(201),
    NO_CONTENT(204),
    NOT_FOUND(404);

    final int code;

    HttpStatus2(int code){
        this.code = code;
    }

    public int getCode(){
        return code;
    }
}
