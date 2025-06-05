package com.spfantasy.backend.config;

import java.util.HashMap;
import java.util.Map;

import org.apache.hc.client5.http.cookie.BasicCookieStore;
import org.apache.hc.client5.http.cookie.CookieStore;
import org.apache.hc.client5.http.impl.classic.CloseableHttpClient;
import org.apache.hc.client5.http.impl.classic.HttpClients;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.web.client.RestTemplate;

@Configuration
public class OdooApiConfig {

    @Value("${odoo.api.url}")
    private String apiUrl;

    @Value("${odoo.api.db}")
    private String db;

    @Value("${odoo.api.username}")
    private String username;

    @Value("${odoo.api.password}")
    private String password;

    private final RestTemplate restTemplate;

    public OdooApiConfig() {
        CookieStore cookieStore = new BasicCookieStore();
        CloseableHttpClient httpClient = HttpClients.custom()
                .setDefaultCookieStore(cookieStore)
                .build();

        this.restTemplate = new RestTemplate(
                new HttpComponentsClientHttpRequestFactory(httpClient));
    }

    public String getApiUrl() {
        return apiUrl;
    }

    public HttpHeaders authenticate() {
        // ️ Solo para pruebas
        String sessionId = "3bcdb50e1e9763cb20d842c4996e858f8c6257ba";
        System.out.println("️ Usando session_id fijo para pruebas: " + sessionId);

        HttpHeaders authHeaders = new HttpHeaders();
        authHeaders.set("Cookie", "session_id=" + sessionId);
        authHeaders.setContentType(MediaType.APPLICATION_JSON);

        return authHeaders;
    }

}
