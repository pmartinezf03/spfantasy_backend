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
        System.out.println("🔎 [SERVICE] Verificando código: " + codigo);

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

        System.out.println("📥 [SERVICE] Login response body:");
        System.out.println(loginResponse.getBody());
        System.out.println("📤 [SERVICE] Login headers:");
        loginResponse.getHeaders().forEach((k, v) -> System.out.println(k + ": " + v));

        // 2. Obtener cookie de sesión
        String cookie = loginResponse.getHeaders().getFirst(HttpHeaders.SET_COOKIE);

        if (cookie == null || !cookie.contains("session_id")) {
            System.out.println("⚠️ [SERVICE] Cookie no encontrada en headers. Intentando desde body...");
            try {
                JSONObject json = new JSONObject(loginResponse.getBody());
                if (json.has("result") && json.getJSONObject("result").has("session_id")) {
                    String sessionId = json.getJSONObject("result").getString("session_id");
                    cookie = "session_id=" + sessionId;
                    System.out.println("✅ [SERVICE] Cookie reconstruida desde body: " + cookie);
                } else {
                    throw new RuntimeException("❌ No se encontró session_id ni en headers ni en body.");
                }
            } catch (Exception e) {
                throw new RuntimeException("❌ Error al reconstruir session_id desde el body: " + e.getMessage());
            }
        } else {
            System.out.println("✅ [SERVICE] Cookie obtenida desde headers: " + cookie);
        }

        // 3. Consultar código en Odoo
        String endpointUrl = UriComponentsBuilder.fromHttpUrl(odooBaseUrl + "/api/codigo.recompensa")
                .queryParam("filters", "[[\"code\",\"=\",\"" + codigo + "\"]]")
                .toUriString();

        System.out.println("➡️ [SERVICE] Consultando en: " + endpointUrl);

        HttpHeaders authHeaders = new HttpHeaders();
        authHeaders.set("Cookie", cookie);
        authHeaders.setContentType(MediaType.APPLICATION_JSON);

        HttpEntity<Void> request = new HttpEntity<>(authHeaders);
        ResponseEntity<Map> response = restTemplate.exchange(endpointUrl, HttpMethod.GET, request, Map.class);

        Map<String, Object> body = response.getBody();
        System.out.println("📦 [SERVICE] Respuesta del endpoint:");
        System.out.println(body);

        List<Map<String, Object>> results = (List<Map<String, Object>>) body.get("result");

        if (results == null || results.isEmpty()) {
            System.out.println("❌ [SERVICE] Código no encontrado");
            throw new RuntimeException("❌ Código no encontrado");
        }

        Map<String, Object> codeData = results.stream()
                .filter(item -> codigo.equals(item.get("code")))
                .findFirst()
                .orElseThrow(() -> new RuntimeException("❌ Código no encontrado en los resultados"));

        System.out.println("✅ [SERVICE] Código válido encontrado: " + codeData);

        Map<String, Object> resultado = new HashMap<>();
        resultado.put("codigo", codeData.get("code"));
        resultado.put("usado", codeData.get("usado"));
        resultado.put("pedido_id", codeData.get("sale_order_id"));
        resultado.put("producto_id", codeData.get("product_id"));

        System.out.println("📤 [SERVICE] Resultado preparado para el frontend:");
        System.out.println(resultado);

        return resultado;
    }

    public boolean canjearCodigo(String codigo) {
        System.out.println("🎯 [SERVICE] Iniciando canje de código: " + codigo);

        String cookie = autenticarYObtenerCookie();
        System.out.println("✅ [SERVICE] Cookie obtenida: " + cookie);

        // Buscar código
        String searchUrl = UriComponentsBuilder.fromHttpUrl(odooBaseUrl + "/api/codigo.recompensa")
                .queryParam("filters", "[[\"code\",\"=\",\"" + codigo + "\"]]")
                .toUriString();
        System.out.println("➡️ [SERVICE] Consultando código en: " + searchUrl);

        HttpHeaders headers = new HttpHeaders();
        headers.set("Cookie", cookie);
        headers.setContentType(MediaType.APPLICATION_JSON);

        HttpEntity<Void> searchRequest = new HttpEntity<>(headers);
        ResponseEntity<Map> searchResponse = new RestTemplate().exchange(searchUrl, HttpMethod.GET, searchRequest,
                Map.class);

        List<Map<String, Object>> resultados = (List<Map<String, Object>>) searchResponse.getBody().get("result");
        System.out.println("📦 [SERVICE] Resultados recibidos: " + resultados);

        if (resultados == null || resultados.isEmpty()) {
            System.out.println("❌ [SERVICE] Código no encontrado");
            return false;
        }

        Map<String, Object> resultado = resultados.get(0);
        System.out.println("✅ [SERVICE] Código encontrado: " + resultado);

        if (Boolean.TRUE.equals(resultado.get("usado"))) {
            System.out.println("⚠️ [SERVICE] El código ya está marcado como usado");
            return false;
        }

        Integer id = (Integer) resultado.get("id");
        String productName = ((List<Object>) resultado.get("product_id")).get(1).toString();
        System.out.println("📦 [SERVICE] ID del código: " + id + " | Producto: " + productName);

        // Recompensa según producto
        if (productName.equalsIgnoreCase("10.000.000 Monedas")) {
            System.out.println("🪙 [SERVICE] Se entregarían 10.000.000 monedas aquí");
        } else if (productName.equalsIgnoreCase("Membresía VIP (1 mes)")) {
            System.out.println("🎟️ [SERVICE] Se activaría membresía VIP aquí");
        } else {
            System.out.println("❌ [SERVICE] Producto no reconocido: " + productName);
            return false;
        }

        // Marcar como usado
        String putUrl = odooBaseUrl + "/api/codigo.recompensa/" + id;
        Map<String, Object> data = Map.of("params", Map.of("data", Map.of("usado", true)));

        HttpEntity<Map<String, Object>> putRequest = new HttpEntity<>(data, headers);
        ResponseEntity<Map> putResponse = new RestTemplate().exchange(putUrl, HttpMethod.PUT, putRequest, Map.class);

        System.out.println("📨 [SERVICE] PUT response: " + putResponse.getBody());

        boolean exito = Boolean.TRUE.equals(putResponse.getBody().get("result"));
        System.out.println(exito ? "✅ [SERVICE] Código marcado como usado" : "❌ [SERVICE] Fallo al marcar como usado");

        return exito;
    }

    private String autenticarYObtenerCookie() {
        RestTemplate restTemplate = new RestTemplate();

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

        String cookie = loginResponse.getHeaders().getFirst(HttpHeaders.SET_COOKIE);

        if (cookie == null || !cookie.contains("session_id")) {
            try {
                JSONObject json = new JSONObject(loginResponse.getBody());
                if (json.has("result") && json.getJSONObject("result").has("session_id")) {
                    String sessionId = json.getJSONObject("result").getString("session_id");
                    return "session_id=" + sessionId;
                } else {
                    throw new RuntimeException("❌ No se encontró session_id ni en headers ni en body.");
                }
            } catch (Exception e) {
                throw new RuntimeException("❌ Error al reconstruir session_id desde el body");
            }
        }

        return cookie;
    }

}
