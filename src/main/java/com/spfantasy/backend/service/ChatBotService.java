package com.spfantasy.backend.service;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.Optional;

import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import com.spfantasy.backend.model.Usuario;
import com.spfantasy.backend.repository.UsuarioRepository;

import jakarta.annotation.PostConstruct;

@Service
public class ChatBotService {

    private static final Logger logger = LoggerFactory.getLogger(ChatBotService.class);

    private final UsuarioRepository usuarioRepository;

    @Value("${openai.api.key}")
    private String openaiApiKey;

    public ChatBotService(UsuarioRepository usuarioRepository) {
        this.usuarioRepository = usuarioRepository;
    }

    public String procesarPregunta(String username, String pregunta) throws Exception {
        logger.info("Procesando pregunta para usuario '{}': {}", username, pregunta);

        Optional<Usuario> optionalUser = usuarioRepository.findByUsername(username);
        if (optionalUser.isEmpty()) {
            logger.warn("Usuario '{}' no encontrado", username);
            throw new Exception("Usuario no encontrado");
        }

        Usuario usuario = optionalUser.get();
        logger.info("Usuario encontrado: {}", usuario.getUsername());

        if (usuario.getPreguntasIaUsadas() >= 2) {
            logger.info("Usuario '{}' ha alcanzado límite de preguntas", username);
            return "❌ Has alcanzado el límite de 2 preguntas. ¡Hazte VIP para desbloquear más!";
        }

        String respuesta = llamarAOpenAI(pregunta);
        logger.info("Respuesta obtenida de OpenAI: {}", respuesta);

        usuario.setPreguntasIaUsadas(usuario.getPreguntasIaUsadas() + 1);
        usuarioRepository.save(usuario);
        logger.info("Se actualizó contador preguntas IA para usuario '{}'", username);

        return respuesta;
    }

    private String llamarAOpenAI(String pregunta) throws Exception {
        logger.info("Llamando a OpenAI con pregunta: {}", pregunta);

        HttpClient client = HttpClient.newHttpClient();

        String contexto = """
                Eres el asistente oficial de la app SPFantasy, una aplicación de Fantasy Basketball. Tu función es resolver dudas de los usuarios explicando de forma clara y útil cómo funciona la app. Estas son sus secciones principales:

                - Inicio de sesión y registro
                - Mi Plantilla (organizar equipo por posiciones: Base, Escolta, Alero, Ala-Pívot, Pívot. Solo los titulares puntúan)
                - Mercado (comprar y vender jugadores)
                - Estadísticas (ver rendimiento real de los jugadores)
                - Noticias (publicaciones relevantes sobre la liga)
                - Chat (mensajes entre usuarios en tiempo real por alias o grupo)
                - VIP (acceso a scouting avanzado y ventajas)
                - Perfil (avatar, puntos, nivel, experiencia, progreso)

                Los usuarios pueden arrastrar jugadores entre titulares y suplentes. El saldo inicial es de 3.000.000. Los VIP acceden al scouting, comparador mejorado y más funciones.

                Siempre responde como si fueras parte del equipo oficial de soporte SPFantasy. No inventes funcionalidades no existentes.
                """;

        JSONObject requestJson = new JSONObject();
        requestJson.put("model", "gpt-3.5-turbo");
        requestJson.put("messages", new org.json.JSONArray()
                .put(new JSONObject().put("role", "system").put("content", contexto))
                .put(new JSONObject().put("role", "user").put("content", pregunta)));

        String json = requestJson.toString();

        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create("https://api.openai.com/v1/chat/completions"))
                .header("Content-Type", "application/json")
                .header("Authorization", "Bearer " + openaiApiKey)
                .POST(HttpRequest.BodyPublishers.ofString(json))
                .build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        logger.info("Respuesta de OpenAI recibida: {}", response.body());

        JSONObject jsonObject = new JSONObject(response.body());

        if (jsonObject.has("choices")) {
            return jsonObject
                    .getJSONArray("choices")
                    .getJSONObject(0)
                    .getJSONObject("message")
                    .getString("content")
                    .trim();
        } else if (jsonObject.has("error")) {
            String message = jsonObject.getJSONObject("error").optString("message", "Error desconocido");
            logger.error("Error API OpenAI: {}", message);
            throw new RuntimeException("OpenAI API error: " + message);
        } else {
            logger.error("Respuesta inesperada de OpenAI: {}", response.body());
            throw new RuntimeException("Respuesta inesperada de OpenAI: " + response.body());
        }
    }

    @PostConstruct
    public void mostrarApiKey() {
        logger.info("ChatBotService inicializado. API key cargada: {}", (openaiApiKey != null ? "[PROTECTED]" : "NO"));
    }
}
