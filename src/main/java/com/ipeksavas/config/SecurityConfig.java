package com.ipeksavas.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
public class SecurityConfig {
	   @Bean
	    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
	        http
	            .csrf().disable() // CSRF korumasını devre dışı bırak
	            .authorizeHttpRequests()
	            .anyRequest().permitAll(); // Tüm endpoint'lere şifresiz erişim
	        return http.build();
	    }
}
