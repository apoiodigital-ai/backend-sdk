package br.com.tucunare.apoiodigital.tts;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.test.web.client.MockRestServiceServer;
import org.springframework.web.client.RestClient;

import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Base64;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.*;
import static org.springframework.test.web.client.response.MockRestResponseCreators.withSuccess;

class GeminiTtsServiceTest {

    @TempDir
    Path audioDir;

    @Test
    @DisplayName("sends the Gemini key in the x-goog-api-key header and never in the URL")
    void enviaChaveNoCabecalho() throws Exception {
        RestClient.Builder builder = RestClient.builder();
        MockRestServiceServer servidor = MockRestServiceServer.bindTo(builder).build();

        String audioBase64 = Base64.getEncoder().encodeToString(new byte[]{0, 1, 2, 3});
        String respostaGemini = """
                {"candidates":[{"content":{"parts":[{"inlineData":{"mimeType":"audio/L16;rate=24000","data":"%s"}}]}}]}
                """.formatted(audioBase64);

        servidor.expect(requestTo("https://generativelanguage.googleapis.com/v1beta/models/modelo-tts:generateContent"))
                .andExpect(method(HttpMethod.POST))
                .andExpect(header(GeminiTtsService.API_KEY_HEADER, "chave-secreta"))
                .andExpect(request -> assertNull(request.getURI().getQuery()))
                .andRespond(withSuccess(respostaGemini, MediaType.APPLICATION_JSON));

        GeminiTtsService tts = new GeminiTtsService(
                true, "chave-secreta", "modelo-tts", "Kore", audioDir.toString(), builder
        );

        Optional<String> arquivo = tts.synthesize("Toque no botão Pagar boleto");

        servidor.verify();
        assertTrue(arquivo.isPresent());
        assertTrue(Files.isRegularFile(audioDir.resolve(arquivo.get())));
    }

    @Test
    @DisplayName("skips synthesis without calling Gemini when no key is configured")
    void semChaveNaoChamaGemini() {
        RestClient.Builder builder = RestClient.builder();
        MockRestServiceServer servidor = MockRestServiceServer.bindTo(builder).build();

        GeminiTtsService tts = new GeminiTtsService(true, "", "modelo-tts", "Kore", audioDir.toString(), builder);

        assertTrue(tts.synthesize("Olá").isEmpty());
        servidor.verify();
    }
}
