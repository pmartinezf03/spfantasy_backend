package com.spfantasy.backend.service;

import com.spfantasy.backend.model.Usuario;
import com.spfantasy.backend.repository.UsuarioRepository;

import jakarta.annotation.PostConstruct;

import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.Optional;

@Service
public class ChatBotService {

    private final UsuarioRepository usuarioRepository;

    public ChatBotService(UsuarioRepository usuarioRepository) {
        this.usuarioRepository = usuarioRepository;
    }

    public String procesarPregunta(String username, String pregunta) throws Exception {
        Optional<Usuario> optionalUser = usuarioRepository.findByUsername(username);
        if (optionalUser.isEmpty()) {
            throw new Exception("Usuario no encontrado");
        }

        Usuario usuario = optionalUser.get();

        if (usuario.getPreguntasIaUsadas() >= 2) {
            return "❌ Has alcanzado el límite de 2 preguntas. ¡Hazte VIP para desbloquear más!";
        }

        String respuesta = llamarAOpenAI(pregunta);

        usuario.setPreguntasIaUsadas(usuario.getPreguntasIaUsadas() + 1);
        usuarioRepository.save(usuario);

        return respuesta;
    }

    private String llamarAOpenAI(String pregunta) throws Exception {
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

        String json = """
                {
                  "model": "gpt-3.5-turbo",
                  "messages": [
                    { "role": "system", "content": "%s" },
                    { "role": "user", "content": "%s" }
                  ]
                }
                """.formatted(contexto, pregunta);

        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create("https://api.openai.com/v1/chat/completions"))
                .header("Content-Type", "application/json")
                // .header("Authorization", "Bearer " + openaiApiKey)
                .POST(HttpRequest.BodyPublishers.ofString(json))
                .build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        JSONObject jsonObject = new JSONObject(response.body());

        if (jsonObject.has("choices")) {
            return jsonObject
                    .getJSONArray("choices")
                    .getJSONObject(0)
                    .getJSONObject("message")
                    .getString("content");
        } else if (jsonObject.has("error")) {
            String message = jsonObject.getJSONObject("error").optString("message", "Error desconocido");
            throw new RuntimeException("OpenAI API error: " + message);
        } else {
            throw new RuntimeException("Respuesta inesperada de OpenAI: " + response.body());
        }
    }

    @PostConstruct
    public void mostrarApiKey() {
    }

}
