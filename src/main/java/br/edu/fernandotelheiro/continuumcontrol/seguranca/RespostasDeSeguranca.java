package br.edu.fernandotelheiro.continuumcontrol.seguranca;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ProblemDetail;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.security.web.access.AccessDeniedHandler;
import org.springframework.stereotype.Component;
import tools.jackson.databind.json.JsonMapper;

import java.io.IOException;
import java.net.URI;

@Component
public class RespostasDeSeguranca
        implements AuthenticationEntryPoint, AccessDeniedHandler {

    private final JsonMapper jsonMapper;

    public RespostasDeSeguranca(
            JsonMapper jsonMapper
    ) {
        this.jsonMapper = jsonMapper;
    }

    @Override
    public void commence(
            HttpServletRequest request,
            HttpServletResponse response,
            AuthenticationException authException
    ) throws IOException {

        ProblemDetail problem =
                ProblemDetail.forStatusAndDetail(
                        HttpStatus.UNAUTHORIZED,
                        "Autenticação necessária."
                );

        problem.setTitle("Unauthorized");
        problem.setInstance(
                URI.create(request.getRequestURI())
        );

        response.setHeader(
                "WWW-Authenticate",
                "Basic realm=\"continuum\""
        );

        escrever(response, problem);
    }

    @Override
    public void handle(
            HttpServletRequest request,
            HttpServletResponse response,
            AccessDeniedException accessDeniedException
    ) throws IOException {

        ProblemDetail problem =
                ProblemDetail.forStatusAndDetail(
                        HttpStatus.FORBIDDEN,
                        "Você não tem permissão para realizar esta operação."
                );

        problem.setTitle("Forbidden");
        problem.setInstance(
                URI.create(request.getRequestURI())
        );

        escrever(response, problem);
    }

    private void escrever(
            HttpServletResponse response,
            ProblemDetail problem
    ) throws IOException {

        response.setStatus(problem.getStatus());

        response.setContentType(
                MediaType.APPLICATION_PROBLEM_JSON_VALUE
        );

        jsonMapper.writeValue(
                response.getOutputStream(),
                problem
        );
    }
}