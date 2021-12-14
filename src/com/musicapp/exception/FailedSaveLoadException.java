package com.musicapp.exception;

public class FailedSaveLoadException extends Exception {
    //Custom exception class (Throwable = cause of the exception)
    public FailedSaveLoadException(String msg, Throwable throwable){
        super("FailedSaveLoadException occurred - " + msg, throwable);
    }
}
