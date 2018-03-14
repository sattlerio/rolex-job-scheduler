package com.sattler.exceptions;

public class MongoDBException extends RolexException {

    public MongoDBException() {}

    public MongoDBException(String s) { super(s); }

    public MongoDBException(String s, Throwable throwable) { super(s, throwable); }

    public MongoDBException(Throwable throwable) { super(throwable); }
}
