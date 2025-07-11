package com.ipeksavas.config;

import java.security.PrivateKey;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import com.ipeksavas.jwt.CustomerUserDetailsService;
import com.ipeksavas.jwt.JwtAuthenticationFilter;
import com.ipeksavas.repository.CustomerRepository;

import lombok.RequiredArgsConstructor;

@Configuration
@RequiredArgsConstructor
public class SecurityConfig{
	
		private final JwtAuthenticationFilter jwtAuthenticationFilter;
		private final CustomerRepository customerRepository;
		
	    @Bean
	    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
	        http
	            .csrf().disable() // Disable CSRF protection
	            .authorizeHttpRequests()
	            	.requestMatchers("/auth/**").permitAll()
	            	.anyRequest().authenticated()
	            .and()
	            .sessionManagement()
	            	.sessionCreationPolicy(SessionCreationPolicy.STATELESS) // Unencrypted access to all endpoints
	        	.and()
	        	.authenticationProvider(authenticationProvider())
	        	.addFilterBefore(jwtAuthenticationFilter, UsernamePasswordAuthenticationFilter.class);
	        
	        return http.build();
	    }
	   
	   @Bean
	   public PasswordEncoder passwordEncoder() {
		   return new BCryptPasswordEncoder();
	   }
	   
	   @Bean
	   public UserDetailsService userDetailsService() {
	       return new CustomerUserDetailsService(customerRepository);
	   }
	   
	   @Bean
	   public AuthenticationProvider authenticationProvider() {
		   DaoAuthenticationProvider authProvider = new DaoAuthenticationProvider();
		    authProvider.setUserDetailsService(userDetailsService());
		    authProvider.setPasswordEncoder(passwordEncoder());
		    return authProvider;
	   }
	   
	   @Bean
	   public AuthenticationManager authenticationManager(AuthenticationConfiguration config) throws Exception {
	       return config.getAuthenticationManager();
	   }
}
