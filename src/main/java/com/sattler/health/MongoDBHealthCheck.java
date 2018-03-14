package com.sattler.health;

import com.codahale.metrics.health.HealthCheck;
import com.sattler.client.MongoDBHandler;
import com.sattler.db.mongodb.Currency;
import org.mongodb.morphia.Datastore;

public class MongoDBHealthCheck extends HealthCheck {
    private String host;
    private String database;

    public MongoDBHealthCheck(String host, String database) {
        this.host = host;
        this.database = database;
    }

    @Override
    protected Result check() throws Exception {
        try {
            Datastore datastore = new MongoDBHandler(this.host, this.database).getDatastore();
            datastore.createQuery(Currency.class).count();
            Long entries = datastore.createQuery(Currency.class)
                    .filter("currency_id ==", "EUR").count();

            if(entries >= 0) {
               return Result.healthy();
            }

            throw new Exception("could not collect data from mongodb");
        } catch (Exception e) {
            return Result.unhealthy(e);
        }
    }

}

