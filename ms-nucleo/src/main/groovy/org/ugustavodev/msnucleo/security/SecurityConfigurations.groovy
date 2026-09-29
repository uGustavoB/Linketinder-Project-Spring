package org.ugustavodev.msnucleo.security

import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.http.HttpMethod
import org.springframework.security.authentication.AuthenticationManager
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity
import org.springframework.security.config.annotation.web.builders.HttpSecurity
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity
import org.springframework.security.config.http.SessionCreationPolicy
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder
import org.springframework.security.crypto.password.PasswordEncoder
import org.springframework.security.web.SecurityFilterChain
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter
import org.springframework.web.cors.CorsConfiguration
import org.springframework.web.cors.CorsConfigurationSource
import org.springframework.web.cors.UrlBasedCorsConfigurationSource

@Configuration
@EnableWebSecurity
@EnableMethodSecurity
class SecurityConfigurations {

    final SecurityFilter filtroSeguranca
    final CustomAuthenticationEntryPoint customAuthenticationEntryPoint
    final CustomAccessDeniedHandler customAccessDeniedHandler

    SecurityConfigurations(SecurityFilter filtroSeguranca, CustomAuthenticationEntryPoint customAuthenticationEntryPoint, CustomAccessDeniedHandler customAccessDeniedHandler) {
        this.filtroSeguranca = filtroSeguranca
        this.customAuthenticationEntryPoint = customAuthenticationEntryPoint
        this.customAccessDeniedHandler = customAccessDeniedHandler
    }

    @Bean
    SecurityFilterChain correnteFiltrosSeguranca(HttpSecurity segurancaHttp) throws Exception {
        return segurancaHttp
                .cors(cors -> cors.configurationSource(corsConfigurationSource()))
                .csrf(csrf -> csrf.disable())
                .sessionManagement(sessao -> sessao.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .authorizeHttpRequests(autorizacao -> autorizacao
                        .requestMatchers(HttpMethod.POST, "/api/auth/login").permitAll()
                        .requestMatchers(HttpMethod.POST, "/api/candidato").permitAll()
                        .requestMatchers(HttpMethod.POST, "/api/empresa").permitAll()
                        .anyRequest().authenticated()
                )
                .exceptionHandling(excecoes -> excecoes
                        .authenticationEntryPoint(customAuthenticationEntryPoint)
                        .accessDeniedHandler(customAccessDeniedHandler)
                )
                .addFilterBefore(filtroSeguranca, UsernamePasswordAuthenticationFilter.class)
                .build()
    }

    @Bean
    AuthenticationManager gerenciadorAutenticacao(AuthenticationConfiguration configuracaoAutenticacao) throws Exception {
        return configuracaoAutenticacao.getAuthenticationManager()
    }

    @Bean
    PasswordEncoder decodificadorSenha() {
        return new BCryptPasswordEncoder()
    }

    @Bean
    CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration configuracao = new CorsConfiguration()
        configuracao.setAllowedOrigins(List.of("http://localhost:4200"))
        configuracao.setAllowedMethods(List.of("GET", "POST", "PUT", "DELETE", "OPTIONS", "PATCH"))
        configuracao.setAllowedHeaders(List.of("*"))
        configuracao.setAllowCredentials(true)
        
        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource()
        source.registerCorsConfiguration("/**", configuracao)
        return source
    }
}
