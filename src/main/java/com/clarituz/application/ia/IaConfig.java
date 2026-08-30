package com.clarituz.application.ia;

import dev.langchain4j.model.chat.ChatModel;
import dev.langchain4j.model.googleai.GoogleAiGeminiChatModel;
import java.time.Duration;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class IaConfig {

    @Value("${ia.google.api-key}")
    private String apiKey;

    @Value("${ia.google.model}")
    private String model;

    @Value("${ia.temperature}")
    private double temperature;

    @Value("${ia.timeout-seconds}")
    private long timeoutSeconds;

    @Bean
    public ChatModel chatModel() {
        return GoogleAiGeminiChatModel.builder()
                .apiKey(apiKey)
                .modelName(model)
                .temperature(temperature)
                .timeout(Duration.ofSeconds(timeoutSeconds))
                .build();
    }
}
