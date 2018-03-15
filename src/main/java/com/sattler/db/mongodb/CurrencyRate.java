package com.sattler.db.mongodb;

import org.bson.types.ObjectId;
import org.mongodb.morphia.annotations.*;

import java.math.BigDecimal;

@Entity("currency_rates")
@Indexes(
        @Index(value = "date", fields = @Field("date"))
)
public class CurrencyRate {

    @Id
    private ObjectId id;

    private long date;
    private Double currency_rate;
    private String currency_id;

    public CurrencyRate(long date, Double currency_rate, String currency_id) {
        this.date = date;
        this.currency_rate = currency_rate;
        this.currency_id = currency_id;
    }

    public ObjectId getId() {
        return id;
    }

    public Double getCurrency_rate() {
        return currency_rate;
    }

    public long getDate() {
        return date;
    }

    public String getCurrency_id() {
        return currency_id;
    }
}
