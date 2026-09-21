package br.com.tucunare.apoiodigital.tts;

import java.util.Optional;

public interface TtsService {

    Optional<String> synthesize(String texto);
}
