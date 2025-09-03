package com.example.mygrowth.global.config;

import org.springframework.ai.chat.client.ChatClient;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class AiConfig {

    private final ChatClient.Builder chatClientBuilder;

    public AiConfig(ChatClient.Builder chatClientBuilder) {
        this.chatClientBuilder = chatClientBuilder;
    }

    @Bean
    public ChatClient chatClient() {
        return chatClientBuilder.build();
    }
}