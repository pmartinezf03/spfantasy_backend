package com.spfantasy.backend.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.messaging.simp.config.MessageBrokerRegistry;
import org.springframework.web.socket.config.annotation.EnableWebSocketMessageBroker;
import org.springframework.web.socket.config.annotation.StompEndpointRegistry;
import org.springframework.web.socket.config.annotation.WebSocketMessageBrokerConfigurer;

@Configuration
@EnableWebSocketMessageBroker
public class WebSocketConfig implements WebSocketMessageBrokerConfigurer {

    @Override
    public void registerStompEndpoints(StompEndpointRegistry registry) {
        registry.addEndpoint("/ws"  )  // ✅ Punto de conexión WebSocket
                .setAllowedOrigins("http://localhost:4200")  // Permitir Angular
                .withSockJS();  // Habilitar compatibilidad con clientes sin WebSocket nativo
    }

    @Override
    public void configureMessageBroker(MessageBrokerRegistry registry) {
        registry.enableSimpleBroker("/chat");  // ✅ Prefijo para recibir mensajes
        registry.setApplicationDestinationPrefixes("/app");  // ✅ Prefijo para enviar mensajes
    }
}
