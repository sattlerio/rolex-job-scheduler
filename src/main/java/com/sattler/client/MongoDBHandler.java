package com.sattler.client;

import com.mongodb.MongoClient;
import com.sattler.exceptions.MongoDBException;
import org.mongodb.morphia.Datastore;
import org.mongodb.morphia.Morphia;

public class MongoDBHandler  {

    final Morphia morphia = new Morphia();
    final Datastore datastore;

    public MongoDBHandler() throws MongoDBException {
        try {
            morphia.mapPackage("com.sattler.db.mongodb", false);
            datastore = morphia.createDatastore(new MongoClient("0.0.0.0", 27017), "currencies");

            datastore.ensureIndexes();
        } catch (Exception e) {
            throw new MongoDBException(e.toString(), e);
        }
    }

    public Datastore getDatastore() {
        return datastore;
    }
}
