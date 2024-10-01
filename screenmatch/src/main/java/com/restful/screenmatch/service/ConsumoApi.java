package com.restful.screenmatch.service;

import org.slf4j.Logger;

import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;

import static java.net.URI.create;
import static java.time.Duration.ofSeconds;
import static org.slf4j.LoggerFactory.getLogger;
import static java.net.http.HttpClient.newBuilder;
import static java.net.http.HttpResponse.BodyHandlers.ofString;

public class ConsumoApi {

    private static final Logger logger = getLogger(ConsumoApi.class);

    public String obterDados(String url) throws Exception {
        try (HttpClient client = newBuilder()
                .connectTimeout(ofSeconds(10))
                .build()) {

            HttpRequest request = HttpRequest.newBuilder()
                    .uri(create(url))
                    .GET()
                    .build();

            HttpResponse<String> response = client.send(request, ofString());
            return response.body();
        } catch (Exception e) {
            logger.error("Error occurred while fetching data from URL: {}", url, e);
            throw e;
        }
    }
}