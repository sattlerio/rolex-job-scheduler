package com.sattler.client;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.sattler.exceptions.OpenExchangeRateException;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

import java.io.IOException;

public class OpenExchangeRate {
    private String api_key;
    private String oer_url = "https://openexchangerates.org/api/";
    private String oer_currency_endpoint = "currencies.json";
    private String oer_rate_endpoint = "latest.json?app_id=";

    public OpenExchangeRate(String api_key) {
        this.api_key = api_key;

    }

    public JsonNode fetchCurrencyRates() throws IOException, OpenExchangeRateException {
        String url = String.format("%s%s%s", this.oer_url,
                this.oer_rate_endpoint, this.api_key);
        OkHttpClient client = new OkHttpClient();

        Request request = new Request.Builder()
                .url(url).build();
        Response response = client.newCall(request).execute();

        if(!response.isSuccessful()) {
            throw new OpenExchangeRateException(response.toString(),
                    new Throwable(response.toString()));
        }
        String data = response.body().string();
        try {
            JsonNode node =  jsonStringToObject(data);
            if(node.has("rates")) {
                return node.get("rates");
            }
            throw new OpenExchangeRateException("rate field not in JSON");
        } catch (IOException e) {
            throw new OpenExchangeRateException(e.toString(), e);
        }
    }

    public JsonNode fetchCurrencies() throws IOException, OpenExchangeRateException {
        String url = String.format("%s%s", this.oer_url, this.oer_currency_endpoint);
        OkHttpClient client = new OkHttpClient();

        Request request = new Request.Builder()
                .url(url).build();

        Response response = client.newCall(request).execute();

        if(!response.isSuccessful()) {
            throw new OpenExchangeRateException("not possible to communicate with OpenExchangeRate",
                    new Throwable(response.toString()));
        }
        String data = response.body().string();
        try {
            return jsonStringToObject(data);
        } catch (IOException e) {
            throw new OpenExchangeRateException("not possible to parse json", e);
        }
    }

    private JsonNode jsonStringToObject(String data) throws IOException {
        ObjectMapper mapper = new ObjectMapper();
        JsonNode rootNode = mapper.readTree(data);

        return rootNode;
    }


}
