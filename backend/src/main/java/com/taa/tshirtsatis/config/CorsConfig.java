package com.taa.tshirtsatis.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;
import org.springframework.web.filter.CorsFilter;

import java.util.Arrays;

@Configuration
public class CorsConfig {

    @Bean
    public CorsFilter corsFilter() {
        CorsConfiguration config = new CorsConfiguration();

        // Frontend uygulamalarının portları
        config.setAllowedOrigins(Arrays.asList(
            "http://localhost:3000",  // Ana uygulama
            "http://localhost:3001"   // Admin paneli
        ));

        // Gerekirse tüm origin'lere izin vermek için:
        // config.addAllowedOriginPattern("*");

        config.setAllowedMethods(Arrays.asList("GET", "POST", "PUT", "DELETE", "OPTIONS"));
        config.setAllowedHeaders(Arrays.asList("Authorization", "Content-Type"));
        config.setAllowCredentials(true); // Cookie vb. bilgiler için

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", config); // Tüm endpoint'ler için geçerli

        return new CorsFilter(source);
    }
}


