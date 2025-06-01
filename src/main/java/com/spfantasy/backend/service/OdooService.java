package com.spfantasy.backend.service;

import java.net.HttpCookie;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.Optional;

import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Service
public class OdooService {

    @Value("${odoo.api.url}")
    private String baseUrl;

    @Value("${odoo.api.db}")
    private String db;

    @Value("${odoo.api.username}")
    private String username;

    @Value("${odoo.api.password}")
    private String password;

    private HttpClient client = HttpClient.newHttpClient();
    private HttpCookie cookie;

    private String getCookie() throws Exception {
        if (cookie != null)
            return cookie.toString();

        JSONObject auth = new JSONObject();
        JSONObject params = new JSONObject();
        params.put("login", username);
        params.put("password", password);
        params.put("db", db);
        auth.put("params", params);

        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(baseUrl + "/auth/"))
                .header("Content-Type", "application/json")
                .POST(HttpRequest.BodyPublishers.ofString(auth.toString()))
                .build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        Optional<String> cookieHeader = response.headers().firstValue("Set-Cookie");
        if (cookieHeader.isEmpty()) {
            System.err.println("Login failed: " + response.statusCode());
            System.err.println("Response body: " + response.body());
            throw new RuntimeException("No session cookie from Odoo");
        }
        String setCookie = cookieHeader.get();

        cookie = HttpCookie.parse(setCookie).get(0);
        return cookie.toString();
    }

    public String getCodigosDisponibles() {
        try {
            String url = baseUrl + "/api/codigo.recompensa/?filter=[[\"used\",\"=\",false],[\"asignado\",\"=\",false]]";
            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create(url))
                    .header("Cookie", getCookie())
                    .GET()
                    .build();

            return client.send(request, HttpResponse.BodyHandlers.ofString()).body();
        } catch (Exception e) {
            throw new RuntimeException("Error al obtener códigos disponibles", e);
        }
    }

    public String getTodosCodigos() {
        try {
            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create(baseUrl + "/api/codigo.recompensa"))
                    .header("Cookie", getCookie())
                    .GET()
                    .build();

            return client.send(request, HttpResponse.BodyHandlers.ofString()).body();
        } catch (Exception e) {
            throw new RuntimeException("Error al obtener todos los códigos", e);
        }
    }

    public String getCodigoPorId(int id) {
        try {
            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create(baseUrl + "/api/codigo.recompensa/" + id))
                    .header("Cookie", getCookie())
                    .GET()
                    .build();

            return client.send(request, HttpResponse.BodyHandlers.ofString()).body();
        } catch (Exception e) {
            throw new RuntimeException("Error al obtener el código por id", e);
        }
    }

    public String crearCodigo(String json) {
        try {
            JSONObject payload = new JSONObject();
            payload.put("params", new JSONObject().put("data", new JSONObject(json)));

            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create(baseUrl + "/api/codigo.recompensa/"))
                    .header("Cookie", getCookie())
                    .header("Content-Type", "application/json")
                    .POST(HttpRequest.BodyPublishers.ofString(payload.toString()))
                    .build();

            return client.send(request, HttpResponse.BodyHandlers.ofString()).body();
        } catch (Exception e) {
            throw new RuntimeException("Error al crear código", e);
        }
    }

    public String marcarCodigoComoUsado(int id) {
        try {
            JSONObject body = new JSONObject();
            JSONObject params = new JSONObject();
            params.put("data", new JSONObject().put("used", true));
            body.put("params", params);

            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create(baseUrl + "/api/codigo.recompensa/" + id))
                    .header("Cookie", getCookie())
                    .header("Content-Type", "application/json")
                    .PUT(HttpRequest.BodyPublishers.ofString(body.toString()))
                    .build();

            return client.send(request, HttpResponse.BodyHandlers.ofString()).body();
        } catch (Exception e) {
            throw new RuntimeException("Error al marcar como usado", e);
        }
    }
}
