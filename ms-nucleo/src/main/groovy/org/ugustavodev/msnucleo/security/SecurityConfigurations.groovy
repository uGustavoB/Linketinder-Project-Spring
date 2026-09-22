package org.ugustavodev.msnucleo.security

import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.http.HttpMethod
import org.springframework.security.authentication.AuthenticationManager
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration
import org.springframework.security.config.annotation.web.builders.HttpSecurity
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity
import org.springframework.security.config.http.SessionCreationPolicy
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder
import org.springframework.security.crypto.password.PasswordEncoder
import org.springframework.security.web.SecurityFilterChain
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter

@Configuration
@EnableWebSecurity
class SecurityConfigurations {

    private final SecurityFilter filtroSeguranca

    SecurityConfigurations(SecurityFilter filtroSeguranca) {
        this.filtroSeguranca = filtroSeguranca
    }

    @Bean
    SecurityFilterChain correnteFiltrosSeguranca(HttpSecurity segurancaHttp) throws Exception {
        return segurancaHttp
                .csrf(csrf -> csrf.disable())
                .sessionManagement(sessao -> sessao.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .authorizeHttpRequests(autorizacao -> autorizacao
                        .requestMatchers(HttpMethod.POST, "/api/auth/login").permitAll()
                        .requestMatchers(HttpMethod.POST, "/api/candidato").permitAll()
                        .requestMatchers(HttpMethod.POST, "/api/empresa").permitAll()
                        .anyRequest().authenticated()
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
}
