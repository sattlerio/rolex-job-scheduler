package com.sattler.health;

import com.codahale.metrics.health.HealthCheck;
import okhttp3.HttpUrl;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;


public class PingHealthCheck extends HealthCheck {
    private final OkHttpClient client;
    private final String path;

    public PingHealthCheck(String  path) {
        this.client = new OkHttpClient();
        this.path = path;
    }

    @Override
    protected Result check() throws Exception {
        try {
            Request request = new Request.Builder()
                    .url(path)
                    .get()
                    .build();
            Response response = client.newCall(request).execute();

            if (!response.isSuccessful()) {
                throw new Exception("not possible to ping --> " + path);
            }
            return Result.healthy();
        } catch (Exception e) {
            return Result.unhealthy(e);
        }

    }
}
