package com.taa.tshirtsatis.config;

import com.taa.tshirtsatis.service.CustomOAuth2Service;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import java.util.List;

@Configuration
@EnableWebSecurity
public class SecurityConfiguration {
    private final AuthenticationProvider authenticationProvider;
    private final JwtAuthenticationFilter jwtAuthenticationFilter;

    private final CustomOAuth2Service customOAuth2UserService;
    private final OAuth2LoginSuccessHandler oAuth2LoginSuccessHandler;

    public SecurityConfiguration(
            JwtAuthenticationFilter jwtAuthenticationFilter,
            AuthenticationProvider authenticationProvider,
            CustomOAuth2Service customOAuth2UserService,
            OAuth2LoginSuccessHandler oAuth2LoginSuccessHandler) {
        this.authenticationProvider = authenticationProvider;
        this.jwtAuthenticationFilter = jwtAuthenticationFilter;
        this.customOAuth2UserService = customOAuth2UserService;
        this.oAuth2LoginSuccessHandler = oAuth2LoginSuccessHandler;
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                .csrf().disable()
                .cors()
                .and()
                .authorizeHttpRequests()
                // Herkese açık yollar
                .requestMatchers("/auth/**").permitAll()
                .requestMatchers("/product/all", "/product/{id}", "/product/category/**", "/product/{id}/image")
                .permitAll()
                .requestMatchers("/category", "/category/all", "/category/{id}").permitAll()
                .requestMatchers("/review/product/**").permitAll()

                // Auth olmuş kullanıcılar (USER veya ADMIN)
                .requestMatchers("/order/**").authenticated()
                .requestMatchers("/user/me").authenticated()

                // Sadece ADMIN rolü
                .requestMatchers("/product/create", "/product/update/**", "/product/delete/**").hasRole("ADMIN")
                .requestMatchers("/category/create", "/category/update/**", "/category/delete/**").hasRole("ADMIN")
                .requestMatchers("/user/all").hasRole("ADMIN")
                .requestMatchers("/review/**").hasRole("ADMIN")

                // Geri kalan her istek için auth zorunlu
                .anyRequest().authenticated()

                .and()
                .sessionManagement().sessionCreationPolicy(SessionCreationPolicy.STATELESS)
                .and()
                .authenticationProvider(authenticationProvider)
                .addFilterBefore(jwtAuthenticationFilter, UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }

    @Bean
    CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration configuration = new CorsConfiguration();

        configuration.setAllowedOrigins(List.of(
                "http://localhost:3000", // Ana uygulama
                "http://localhost:3001",
                "*" // Admin paneli
        ));
        configuration.setAllowedMethods(List.of("GET", "POST", "PUT", "DELETE", "OPTIONS"));
        configuration.setAllowedHeaders(List.of("Authorization", "Content-Type"));
        configuration.setAllowCredentials(true);

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", configuration);

        return source;
    }
}
