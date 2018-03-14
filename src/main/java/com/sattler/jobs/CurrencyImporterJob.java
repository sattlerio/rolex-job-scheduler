package com.sattler.jobs;

import com.codahale.metrics.annotation.ExceptionMetered;
import com.fasterxml.jackson.databind.JsonNode;
import com.mongodb.MongoException;
import com.sattler.db.mongodb.Currency;
import com.sattler.client.MongoDBHandler;
import com.sattler.client.OpenExchangeRate;
import com.sattler.exceptions.MongoDBException;
import com.sattler.exceptions.OpenExchangeRateException;
import org.knowm.sundial.Job;
import org.knowm.sundial.annotations.CronTrigger;
import org.knowm.sundial.annotations.SimpleTrigger;
import org.knowm.sundial.exceptions.JobInterruptException;
import org.mongodb.morphia.Datastore;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Iterator;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

@CronTrigger(cron = "0 30 2 1/1 * ? *")
public class CurrencyImporterJob extends Job {

    private final static Logger log = LoggerFactory.getLogger(CurrencyImporterJob.class);

    @Override
    @ExceptionMetered
    public void doRun() throws JobInterruptException {
        String transaction_id = UUID.randomUUID().toString();
        log.info(String.format("Currency Importer Job just woke up with ID: %s", transaction_id));

        try {
            Datastore mongo_client = new MongoDBHandler().getDatastore();
            log.info(String.format("Curreny Importer %s: opened mongodb connection", transaction_id));

            OpenExchangeRate oer_client = new OpenExchangeRate(System.getenv("OPENEXCHANGE_RATE_API_KEY"));
            JsonNode data = oer_client.fetchCurrencies();
            Iterator<Map.Entry<String, JsonNode>> nodes = data.fields();

            log.info(String.format("Currency Importer %s successfully fetched data from openexchange rate", transaction_id));

            while (nodes.hasNext()) {
                try {

                    log.info(String.format("Currency Importer %s -- takes next iteration round", transaction_id));
                    Map.Entry<String, JsonNode> entry = (Map.Entry<String, JsonNode>) nodes.next();
                    if (entry.getValue().fields().hasNext()) {
                        log.info(String.format("missformed json detected %s", transaction_id));
                        throw new OpenExchangeRateException("missformed json + " + entry.getValue().toString());
                    }
                    Currency currency = new Currency(entry.getKey(), entry.getValue().asText());
                    log.info(String.format("Currency Importer %s - is trying to save %s", transaction_id, entry.getValue()));

                    Long entries = mongo_client.createQuery(Currency.class)
                            .filter("currency_id ==", currency.getCurrency_id()).count();

                    if (entries != 0) {
                        log.info(String.format("Currency Importer %s: currency %s exists already, skip this round", transaction_id, currency.getCurrency_id()));
                        continue;
                    }
                    log.info(String.format("Currency Importer %s: does not exist yet going to save it", transaction_id));

                    mongo_client.save(currency);
                    log.info(String.format("Currency Importer %s - saved currency - going to the next round", transaction_id));

                } catch (Exception e) {
                    if (e instanceof MongoException) {
                        log.error(String.format("Currency Importer %s has fatal exception with mongodb, giving up", transaction_id));
                        throw new MongoDBException(e.toString(), e);
                    } else if (e instanceof OpenExchangeRateException) {
                        log.warn(String.format("Currency Importer %s has problems with json, skip this currency", transaction_id));
                    } else {
                        log.error(String.format("Unkown exception %s giving up", transaction_id));
                        throw new Exception(e.toString(), e);
                    }

                }
            }
            log.info(String.format("Currency Importer Job with ID %s, finished and is going to sleep now...", transaction_id
            ));
        } catch (Exception e)

        {
            log.error("error not possible to continue, giving up for this round.");
            log.error(e.toString());
        }
    }

}
