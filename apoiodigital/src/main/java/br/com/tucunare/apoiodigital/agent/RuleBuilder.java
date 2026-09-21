package br.com.tucunare.apoiodigital.agent;

import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Component;
import org.springframework.util.StreamUtils;

import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;

@Component
public class RuleBuilder {

    public String getRules(String classpathRelativePath) {
        try (InputStream inputStream = new ClassPathResource(classpathRelativePath).getInputStream()) {
            return StreamUtils.copyToString(inputStream, StandardCharsets.UTF_8);
        } catch (IOException e) {
            throw new RuntimeException("Falha ao carregar regra do agente: " + classpathRelativePath, e);
        }
    }

}
