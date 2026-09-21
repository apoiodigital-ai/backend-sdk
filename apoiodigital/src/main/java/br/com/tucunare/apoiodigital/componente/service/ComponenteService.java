package br.com.tucunare.apoiodigital.componente.service;

import br.com.tucunare.apoiodigital.agent.CapturedElementDTO;
import br.com.tucunare.apoiodigital.cliente.data.Cliente;
import br.com.tucunare.apoiodigital.componente.data.Componente;
import br.com.tucunare.apoiodigital.componente.exception.ComponenteNaoEncontradoException;
import br.com.tucunare.apoiodigital.componente.repository.ComponenteRepository;
import br.com.tucunare.apoiodigital.resposta.data.Resposta;
import br.com.tucunare.apoiodigital.resposta.exception.RespostaNaoEncontradaException;
import br.com.tucunare.apoiodigital.resposta.repository.RespostaRepository;
import org.springframework.stereotype.Service;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Comparator;
import java.util.List;
import java.util.Objects;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
public class ComponenteService {

    private final ComponenteRepository componenteRepository;
    private final RespostaRepository respostaRepository;

    public ComponenteService(
            ComponenteRepository componenteRepository,
            RespostaRepository respostaRepository
    ) {
        this.componenteRepository = componenteRepository;
        this.respostaRepository = respostaRepository;
    }

    public String gerarAssinatura(List<CapturedElementDTO> elementos) {
        String canonical = elementos.stream()
                .sorted(Comparator.comparing(
                        CapturedElementDTO::viewId,
                        Comparator.nullsLast(String::compareTo)
                ))
                .map(e -> nullToEmpty(e.viewId()) + "|" + nullToEmpty(e.className()) + "|" + nullToEmpty(e.text()))
                .collect(Collectors.joining(";"));

        return sha256Hex(canonical);
    }

    public Componente salvar(Resposta resposta, List<CapturedElementDTO> elementos) {
        Componente componente = new Componente(gerarAssinatura(elementos), resposta);
        return componenteRepository.save(componente);
    }

    public boolean comparar(UUID idResposta, List<CapturedElementDTO> elementosAtuais, Cliente cliente) {
        Resposta resposta = respostaRepository.findByIdAndPedido_Usuario_Cliente_Id(idResposta, cliente.getId())
                .orElseThrow(RespostaNaoEncontradaException::new);

        Componente componente = componenteRepository.findFirstByRespostaIdOrderByIdDesc(resposta.getId())
                .orElseThrow(ComponenteNaoEncontradoException::new);

        String assinaturaAtual = gerarAssinatura(elementosAtuais);
        return assinaturaAtual.equals(componente.getAssinatura());
    }

    private static String nullToEmpty(String value) {
        return value == null ? "" : value;
    }

    private static String sha256Hex(String input) {
        try {
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            byte[] hash = digest.digest(input.getBytes(StandardCharsets.UTF_8));
            StringBuilder hex = new StringBuilder(hash.length * 2);
            for (byte b : hash) {
                hex.append(String.format("%02x", b));
            }
            return hex.toString();
        } catch (NoSuchAlgorithmException e) {
            throw new IllegalStateException(e);
        }
    }
}
