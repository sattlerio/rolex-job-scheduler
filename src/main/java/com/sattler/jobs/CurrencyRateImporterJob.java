package com.sattler.jobs;

import com.codahale.metrics.annotation.ExceptionMetered;
import com.fasterxml.jackson.databind.JsonNode;
import com.mongodb.MongoException;
import com.sattler.client.MongoDBHandler;
import com.sattler.client.OpenExchangeRate;
import com.sattler.db.mongodb.Currency;
import com.sattler.db.mongodb.CurrencyRate;
import com.sattler.exceptions.MongoDBException;
import com.sattler.exceptions.OpenExchangeRateException;
import org.knowm.sundial.Job;
import org.knowm.sundial.annotations.SimpleTrigger;
import org.knowm.sundial.exceptions.JobInterruptException;
import org.mongodb.morphia.Datastore;
import org.mongodb.morphia.query.Query;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.Instant;
import java.util.*;
import java.util.concurrent.TimeUnit;

@SimpleTrigger(repeatInterval = 30, timeUnit = TimeUnit.SECONDS)
public class CurrencyRateImporterJob extends Job {

    private final static Logger log = LoggerFactory.getLogger(CurrencyRateImporterJob.class);
    private String transaction_id;

    @Override
    @ExceptionMetered
    public void doRun() throws JobInterruptException {
        this.transaction_id = UUID.randomUUID().toString();
        logInfoWithTransactionID("job woke up...");

        long unixTime = Instant.now().getEpochSecond();
        Date date = Date.from(Instant.ofEpochSecond(unixTime));

        logInfoWithTransactionID("starting import for date " + date.toString());

        try {
            Datastore mongo_client = new MongoDBHandler(System.getenv("MONGODB_HOST"),
                    System.getenv("MONGODB_DATABASE")).getDatastore();

            OpenExchangeRate oer_client = new OpenExchangeRate(System.getenv("OPENEXCHANGE_RATE_API_KEY"));
            JsonNode data = oer_client.fetchCurrencyRates();
            Iterator<Map.Entry<String, JsonNode>> nodes = data.fields();
            logInfoWithTransactionID("got successfully all rates from open api - iterate over currencies");

            while (nodes.hasNext()) {
                try {
                    logInfoWithTransactionID("next iteration round");

                    Map.Entry<String, JsonNode> entry = (Map.Entry<String, JsonNode>) nodes.next();

                    if (entry.getValue().fields().hasNext()) {
                        log.info(String.format("missformed json detected %s", transaction_id));
                        throw new OpenExchangeRateException("missformed json + " + entry.getValue().toString());
                    }

                    CurrencyRate rate = new CurrencyRate(unixTime, entry.getValue().asDouble(),
                            entry.getKey());

                    logInfoWithTransactionID("trying to save " + entry.getKey());

                    Query<Currency> query = mongo_client.createQuery(Currency.class)
                            .filter("currency_id ==", entry.getKey());


                    if (query.count() == 0) {
                        logInfoWithTransactionID("currency " + entry.getKey() + " is unkown -- skip this one");
                        continue;
                    }

                    logInfoWithTransactionID("everything is fine with currency " + entry.getKey() + " going to save the new rate");

                    mongo_client.save(rate);

                    logInfoWithTransactionID("rate update for " + entry.getKey() + "done going to next one");

                } catch (Exception e) {
                    if (e instanceof MongoException) {
                        logInfoWithTransactionID("fatal exception with mongodb");
                        log.error(e.toString());
                        throw new MongoDBException(e.toString(), e);
                    } else if (e instanceof OpenExchangeRateException) {
                        log.error(e.toString());
                        logInfoWithTransactionID("critical exception with OpenExchange Rate Just skip this round");
                    } else {
                        log.error(e.toString());
                        logInfoWithTransactionID("unkown exception going to die " + e.toString());
                        throw new Exception(e.toString(), e);
                    }

                }
            }
            logInfoWithTransactionID("finished currency rate import... good night...");
        } catch (Exception e) {
            log.error(e.toString());
            log.error("stupid");
        }

    }

    private void logInfoWithTransactionID(String message) {
        log.info(String.format("Currency Rate Importer with ID %s: %s", transaction_id, message));
    }

}
