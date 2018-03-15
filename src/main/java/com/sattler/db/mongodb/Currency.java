package com.sattler.db.mongodb;

import org.bson.types.ObjectId;
import org.mongodb.morphia.annotations.*;

import java.util.List;

@Entity("currencies")
@Indexes(
        @Index(value = "currency_id", fields = @Field("currency_id"), unique = true)
)
public class Currency {

    @Id
    private ObjectId id;

    @Indexed(unique = false)
    private String currency_id;
    private String name;

    public Currency() {

    }

    public Currency(String id, String name) {
        this.currency_id = id;
        this.name = name;
    }

    public String getCurrency_id() {
        return currency_id;
    }


    public String getName() {
        return name;
    }
}
