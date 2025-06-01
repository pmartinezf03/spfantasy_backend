package com.spfantasy.backend.service;

import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import java.util.*;

@Service
public class CodigoRecompensaService {

    @Value("${odoo.api.url}")
    private String odooBaseUrl;

    @Value("${odoo.api.db}")
    private String db;

    @Value("${odoo.api.username}")
    private String username;

    @Value("${odoo.api.password}")
    private String password;

    public Map<String, Object> verificarCodigo(String codigo) {
        RestTemplate restTemplate = new RestTemplate();

        // 1. Login a Odoo
        String loginUrl = odooBaseUrl + "/web/session/authenticate";
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);

        Map<String, Object> authParams = new HashMap<>();
        authParams.put("login", username);
        authParams.put("password", password);
        authParams.put("db", db);

        Map<String, Object> payload = new HashMap<>();
        payload.put("params", authParams);

        HttpEntity<Map<String, Object>> loginRequest = new HttpEntity<>(payload, headers);
        ResponseEntity<String> loginResponse = restTemplate.postForEntity(loginUrl, loginRequest, String.class);

        // 🔍 Logs para depurar
        System.out.println("📥 Body completo recibido:");
        System.out.println(loginResponse.getBody());
        System.out.println("📦 Cabeceras:");
        loginResponse.getHeaders().forEach((k, v) -> System.out.println(k + ": " + v));

        // 2. Obtener la cookie desde la cabecera
        String cookie = loginResponse.getHeaders().getFirst(HttpHeaders.SET_COOKIE);

        if (cookie == null || !cookie.contains("session_id")) {
            // Intentar fallback desde el body si existe
            try {
                JSONObject json = new JSONObject(loginResponse.getBody());
                if (json.has("result") && json.getJSONObject("result").has("session_id")) {
                    String sessionId = json.getJSONObject("result").getString("session_id");
                    cookie = "session_id=" + sessionId;
                    System.out.println("✅ Cookie reconstruida desde el body: " + cookie);
                } else {
                    throw new RuntimeException("❌ No se encontró session_id ni en headers ni en body.");
                }
            } catch (Exception e) {
                throw new RuntimeException("❌ No se pudo obtener la cookie de sesión desde /web/session/authenticate");
            }
        } else {
            System.out.println("✅ Cookie obtenida de la cabecera: " + cookie);
        }

        // 3. Buscar el código
        String endpointUrl = UriComponentsBuilder.fromHttpUrl(odooBaseUrl + "/api/codigo.recompensa")
                .queryParam("filters", "[[\"code\",\"=\",\"" + codigo + "\"]]")
                .toUriString();

        System.out.println("🔎 Consultando código: " + codigo);
        System.out.println("➡️ Endpoint: " + endpointUrl);

        HttpHeaders authHeaders = new HttpHeaders();
        authHeaders.set("Cookie", cookie);
        authHeaders.setContentType(MediaType.APPLICATION_JSON);

        HttpEntity<Void> request = new HttpEntity<>(authHeaders);
        ResponseEntity<Map> response = restTemplate.exchange(endpointUrl, HttpMethod.GET, request, Map.class);

        Map body = response.getBody();
        System.out.println("📦 Respuesta recibida de Odoo:");
        System.out.println(body);

        List<Map<String, Object>> results = (List<Map<String, Object>>) body.get("result");

        if (results == null || results.isEmpty()) {
            throw new RuntimeException("❌ Código no encontrado");
        }

        // ✅ Filtrar por coincidencia exacta
        Map<String, Object> codeData = results.stream()
                .filter(item -> codigo.equals(item.get("code")))
                .findFirst()
                .orElseThrow(() -> new RuntimeException("❌ Código no encontrado en los resultados"));

        System.out.println("✅ Código encontrado: " + codeData);

        Map<String, Object> resultado = new HashMap<>();
        resultado.put("codigo", codeData.get("code"));
        resultado.put("usado", codeData.get("usado")); // ⚠ Asegúrate que es 'usado' en Odoo, no 'used'
        resultado.put("pedido_id", codeData.get("sale_order_id"));
        resultado.put("producto_id", codeData.get("product_id"));

        System.out.println("🎁 Resultado entregado al frontend: " + resultado);

        return resultado;
    }
}
