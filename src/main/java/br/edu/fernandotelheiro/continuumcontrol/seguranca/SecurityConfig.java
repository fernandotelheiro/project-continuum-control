package br.edu.fernandotelheiro.continuumcontrol.seguranca;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
@EnableMethodSecurity
public class SecurityConfig {

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public SecurityFilterChain securityFilterChain(
            HttpSecurity http,
            RespostasDeSeguranca respostasDeSeguranca
    ) throws Exception {

        http
                .csrf(csrf -> csrf.disable())

                .sessionManagement(session ->
                        session.sessionCreationPolicy(
                                SessionCreationPolicy.STATELESS
                        )
                )

                .httpBasic(basic ->
                        basic.authenticationEntryPoint(
                                respostasDeSeguranca
                        )
                )

                .exceptionHandling(exceptions -> exceptions
                        .authenticationEntryPoint(
                                respostasDeSeguranca
                        )
                        .accessDeniedHandler(
                                respostasDeSeguranca
                        )
                )

                .authorizeHttpRequests(auth -> auth

                        .requestMatchers(
                                "/actuator/health",
                                "/actuator/health/**"
                        ).permitAll()

                        .requestMatchers(
                                HttpMethod.GET,
                                "/**"
                        ).authenticated()

                        // Ações exclusivas de supervisor
                        .requestMatchers(
                                HttpMethod.POST,
                                "/missoes/*/autorizacao",
                                "/missoes/*/iniciar",
                                "/missoes/*/concluir",
                                "/missoes/*/falhar",
                                "/missoes/*/agentes"
                        ).hasRole("SUPERVISOR")

                        // Intervenção:
                        // filtro libera AGENTE ou SUPERVISOR;
                        // @PreAuthorize faz a regra por recurso.
                        .requestMatchers(
                                HttpMethod.POST,
                                "/missoes/*/intervencoes"
                        ).hasAnyRole(
                                "AGENTE",
                                "SUPERVISOR"
                        )

                        // Criação de agente
                        .requestMatchers(
                                HttpMethod.POST,
                                "/agentes"
                        ).hasRole("ADMINISTRADOR")

                        // Criações operacionais
                        .requestMatchers(
                                HttpMethod.POST,
                                "/eventos",
                                "/anomalias",
                                "/missoes"
                        ).hasAnyRole(
                                "OPERADOR",
                                "SUPERVISOR"
                        )

                        // Alterações operacionais
                        .requestMatchers(
                                HttpMethod.PUT,
                                "/eventos/*",
                                "/anomalias/*",
                                "/missoes/*"
                        ).hasAnyRole(
                                "OPERADOR",
                                "SUPERVISOR"
                        )

                        // Alteração de agente
                        .requestMatchers(
                                HttpMethod.PUT,
                                "/agentes/*"
                        ).hasRole("ADMINISTRADOR")

                        // Exclusão de agente
                        .requestMatchers(
                                HttpMethod.DELETE,
                                "/agentes/*"
                        ).hasRole("ADMINISTRADOR")

                        .anyRequest().denyAll()
                );

        return http.build();
    }
}