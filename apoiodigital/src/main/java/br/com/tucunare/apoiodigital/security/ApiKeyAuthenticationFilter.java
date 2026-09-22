package br.com.tucunare.apoiodigital.security;

import br.com.tucunare.apoiodigital.cliente.data.Cliente;
import br.com.tucunare.apoiodigital.cliente.repository.ClienteRepository;
import br.com.tucunare.apoiodigital.handler.ExceptionDTO;
import br.com.tucunare.apoiodigital.limite.DecisaoLimite;
import br.com.tucunare.apoiodigital.limite.LimiteRequisicoesService;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.Optional;

@Component
public class ApiKeyAuthenticationFilter extends OncePerRequestFilter {

    public static final String API_KEY_HEADER = "x-api-key";

    private final ClienteRepository clienteRepository;
    private final LimiteRequisicoesService limiteRequisicoesService;
    private final ObjectMapper objectMapper = new ObjectMapper();

    public ApiKeyAuthenticationFilter(
            ClienteRepository clienteRepository,
            LimiteRequisicoesService limiteRequisicoesService
    ) {
        this.clienteRepository = clienteRepository;
        this.limiteRequisicoesService = limiteRequisicoesService;
    }

    @Override
    protected void doFilterInternal(
            HttpServletRequest request,
            HttpServletResponse response,
            FilterChain filterChain
    ) throws ServletException, IOException {

        String accessKey = request.getHeader(API_KEY_HEADER);

        if (accessKey != null && !accessKey.isBlank()) {
            Optional<Cliente> cliente = clienteRepository.findByAccessKey(accessKey);

            if (cliente.isPresent()) {
                DecisaoLimite decisao = limiteRequisicoesService.consumirChave(cliente.get());
                if (!decisao.permitido()) {
                    responderLimiteExcedido(response, decisao.segundosParaLiberar());
                    return;
                }

                Authentication authentication = new UsernamePasswordAuthenticationToken(
                        cliente.get(),
                        null,
                        List.of(new SimpleGrantedAuthority("ROLE_PARTNER"))
                );
                SecurityContextHolder.getContext().setAuthentication(authentication);
            }
        }

        filterChain.doFilter(request, response);
    }

    private void responderLimiteExcedido(HttpServletResponse response, long segundosParaLiberar) throws IOException {
        response.setStatus(HttpStatus.TOO_MANY_REQUESTS.value());
        response.setHeader(HttpHeaders.RETRY_AFTER, String.valueOf(segundosParaLiberar));
        response.setContentType(MediaType.APPLICATION_JSON_VALUE);
        response.setCharacterEncoding(StandardCharsets.UTF_8.name());
        ExceptionDTO body = new ExceptionDTO(
                HttpStatus.TOO_MANY_REQUESTS.value(),
                "TooManyRequests",
                "Limite de requisições da chave excedido. Tente novamente em " + segundosParaLiberar + " segundos."
        );
        response.getWriter().write(objectMapper.writeValueAsString(body));
    }
}
