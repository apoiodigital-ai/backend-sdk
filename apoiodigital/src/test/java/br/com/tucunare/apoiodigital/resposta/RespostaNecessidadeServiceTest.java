package br.com.tucunare.apoiodigital.resposta;

import br.com.tucunare.apoiodigital.agent.TiposPendencia;
import br.com.tucunare.apoiodigital.cliente.data.Cliente;
import br.com.tucunare.apoiodigital.pedido.data.Pedido;
import br.com.tucunare.apoiodigital.pedido.data.PedidoStatus;
import br.com.tucunare.apoiodigital.pedido.repository.PedidoRepository;
import br.com.tucunare.apoiodigital.resposta.data.RespostaNecessidadeRequestDTO;
import br.com.tucunare.apoiodigital.resposta.data.RespostaNecessidadeResponseDTO;
import br.com.tucunare.apoiodigital.resposta.service.RespostaNecessidadeService;
import br.com.tucunare.apoiodigital.tutorial.agents.UserAnswerValidator.AgentResponseDTO;
import br.com.tucunare.apoiodigital.tutorial.agents.UserAnswerValidator.UserAnswerValidatorService;
import br.com.tucunare.apoiodigital.usuario.data.Usuario;
import br.com.tucunare.apoiodigital.usuario.service.UsuarioService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

class RespostaNecessidadeServiceTest {

    @Mock
    private UsuarioService usuarioService;

    @Mock
    private PedidoRepository pedidoRepository;

    @Mock
    private UserAnswerValidatorService userAnswerValidatorService;

    private RespostaNecessidadeService service;
    private Cliente cliente;
    private Pedido pedido;

    @BeforeEach
    void setup() {
        MockitoAnnotations.openMocks(this);
        service = new RespostaNecessidadeService(usuarioService, pedidoRepository, userAnswerValidatorService);

        cliente = new Cliente("Parceiro Teste", "chave-teste", "saude");
        cliente.setId(UUID.randomUUID());
        Usuario usuario = new Usuario("usr_anon_teste", null, cliente);
        usuario.setId(UUID.randomUUID());

        pedido = new Pedido(usuario, "Quero pagar uma conta");
        pedido.setId(UUID.randomUUID());
        pedido.marcarAguardandoInformacao(TiposPendencia.ambiguidade, "Boleto ou Pix", "Boleto ou Pix?");

        when(usuarioService.resolverOuCriar("usr_anon_teste", cliente)).thenReturn(usuario);
        when(pedidoRepository.findByIdAndUsuarioId(pedido.getId(), usuario.getId())).thenReturn(Optional.of(pedido));
        when(pedidoRepository.save(any(Pedido.class))).thenAnswer(invocation -> invocation.getArgument(0));
    }

    @Test
    @DisplayName("stores the user's answer in the Pedido prompt when it satisfies the question")
    void registraRespostaAceita() {
        when(userAnswerValidatorService.executeTask(any())).thenReturn(new AgentResponseDTO(null, true));

        RespostaNecessidadeResponseDTO resposta = service.validar(
                new RespostaNecessidadeRequestDTO("usr_anon_teste", pedido.getId(), "Boleto"),
                cliente
        );

        assertFalse(resposta.interromper());
        assertEquals(PedidoStatus.PRONTO, pedido.getStatus());
        assertTrue(pedido.getPrompt().startsWith("Quero pagar uma conta"));
        assertTrue(pedido.getPrompt().contains("Pergunta do assistente: Boleto ou Pix?"));
        assertTrue(pedido.getPrompt().contains("Resposta do usuário: Boleto"));
    }

    @Test
    @DisplayName("keeps the answer as context even when a follow-up question is needed")
    void registraRespostaInsuficiente() {
        when(userAnswerValidatorService.executeTask(any()))
                .thenReturn(new AgentResponseDTO("Qual boleto você quer pagar?", false));

        RespostaNecessidadeResponseDTO resposta = service.validar(
                new RespostaNecessidadeRequestDTO("usr_anon_teste", pedido.getId(), "aquele ali"),
                cliente
        );

        assertTrue(resposta.interromper());
        assertEquals("Qual boleto você quer pagar?", pedido.getPerguntaPendente());
        assertTrue(pedido.getPrompt().contains("Resposta do usuário: aquele ali"));
    }
}
