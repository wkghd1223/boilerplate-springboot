package com.example.boilerplatespringboot.config.security;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.example.boilerplatespringboot.api.auth.repository.UserRepository;
import com.example.boilerplatespringboot.api.auth.service.CustomUserDetailsService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.annotation.web.configurers.HeadersConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.encrypt.AesBytesEncryptor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.security.web.header.writers.StaticHeadersWriter;
import org.springframework.stereotype.Component;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.CorsUtils;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import java.util.List;

@RequiredArgsConstructor
@Configuration
@EnableWebSecurity
@Component
public class SecurityConfig {

    @Value("${my.encrypt.secret}")
    private String secret;
    @Value("${my.encrypt.salt}")
    private String salt;

    private final ObjectMapper objectMapper;
    private final JwtAuthenticationEntryPoint jwtAuthenticationEntryPoint;
    private final JwtAccessDeniedHandler jwtAccessDeniedHandler;
    private final UserRepository userRepository;

    @Bean
    public SecurityFilterChain filterChain(
            HttpSecurity http,
            JwtTokenProvider jwtTokenProvider,
            AuthenticationEntryPoint entryPoint
    ) throws Exception {
        http
            .authorizeHttpRequests(authorizeRequests -> {
                authorizeRequests
                    .requestMatchers(HttpMethod.HEAD).denyAll()
                    .requestMatchers(HttpMethod.TRACE).denyAll()
                    .requestMatchers(HttpMethod.OPTIONS, "/**").permitAll()
                    .requestMatchers(CorsUtils::isPreFlightRequest).permitAll()
                    .requestMatchers("/**").permitAll()
                    .requestMatchers("/", "/error", "/favicon.ico", "/webjars/**").permitAll()
                    .requestMatchers("/v3/api-docs", "/v3/api-docs/**",
                            "/swagger-ui/**", "/swagger/**", "/error", "/swagger-ui.html",
                            "/api-docs/json/**", "/swagger.json").permitAll()
                    .anyRequest().authenticated();
            })
            .cors(cors -> cors.configurationSource(corsConfigurationSource()))
            .formLogin(AbstractHttpConfigurer::disable)
            .csrf(AbstractHttpConfigurer::disable)
            .logout(AbstractHttpConfigurer::disable)
            .sessionManagement(s -> s.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
            .headers(header -> header
                    .addHeaderWriter(new StaticHeadersWriter("server", "None"))
                    .httpStrictTransportSecurity(HeadersConfigurer.HstsConfig::disable)
                    .xssProtection(HeadersConfigurer.XXssConfig::disable)
                    .frameOptions(HeadersConfigurer.FrameOptionsConfig::disable)
                    .contentTypeOptions(HeadersConfigurer.ContentTypeOptionsConfig::disable)
            )
            .exceptionHandling(exceptionHandling -> {
                exceptionHandling
                        .authenticationEntryPoint(jwtAuthenticationEntryPoint)
                        .accessDeniedHandler(jwtAccessDeniedHandler);
            })
            .addFilterBefore(new JwtFilter(jwtTokenProvider, entryPoint, userRepository), UsernamePasswordAuthenticationFilter.class);
        ;

        return http.build();
    }

    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration configuration = new CorsConfiguration();
        configuration.setAllowedOrigins(List.of("http://localhost:3000"));
        configuration.setAllowedMethods(List.of("GET", "POST", "PATCH", "PUT", "DELETE", "OPTIONS"));
        configuration.setAllowedHeaders(
                List.of("Content-Type", "X-Requested-With", "Authorization", "withCredentials",
                        "Access-Control-Allow-Origin"));
        configuration.setAllowCredentials(true);

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", configuration);
        return source;
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public CustomUserDetailsService customUserDetailsService() {
        return new CustomUserDetailsService();
    }

    @Bean
    public AuthenticationManager authenticationManager(
            AuthenticationConfiguration authenticationConfiguration) throws Exception {
        return authenticationConfiguration.getAuthenticationManager();
    }

    @Bean
    public AesBytesEncryptor aesBytesEncryptor() {
        return new AesBytesEncryptor(secret, salt);
    }
}
