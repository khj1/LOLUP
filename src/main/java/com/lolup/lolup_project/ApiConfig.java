package com.lolup.lolup_project;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.reactive.function.client.WebClient;

@Configuration
public class ApiConfig {

    @Bean
    public WebClient riot_api(WebClient.Builder builder) {
        return builder.baseUrl("https://kr.api.riotgames.com").build();
    }
}
