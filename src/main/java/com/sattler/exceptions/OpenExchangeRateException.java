package com.sattler.exceptions;

import com.sattler.client.OpenExchangeRate;

public class OpenExchangeRateException extends RolexException {

    public OpenExchangeRateException() {}

    public OpenExchangeRateException(String s) { super(s); }

    public OpenExchangeRateException(String s, Throwable throwable) { super(s, throwable); }

    public OpenExchangeRateException(Throwable throwable) { super(throwable); }
}
