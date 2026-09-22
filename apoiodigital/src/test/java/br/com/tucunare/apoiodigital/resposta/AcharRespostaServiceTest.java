package br.com.tucunare.apoiodigital.resposta;

import br.com.tucunare.apoiodigital.agent.CapturedElementDTO;
import br.com.tucunare.apoiodigital.cliente.data.Cliente;
import br.com.tucunare.apoiodigital.componente.service.ComponenteService;
import br.com.tucunare.apoiodigital.pedido.data.Pedido;
import br.com.tucunare.apoiodigital.pedido.exception.PedidoDoesNotExistException;
import br.com.tucunare.apoiodigital.pedido.repository.PedidoRepository;
import br.com.tucunare.apoiodigital.resposta.data.AcharRespostaRequestDTO;
import br.com.tucunare.apoiodigital.resposta.data.Resposta;
import br.com.tucunare.apoiodigital.resposta.repository.RespostaRepository;
import br.com.tucunare.apoiodigital.resposta.service.AcharRespostaService;
import br.com.tucunare.apoiodigital.tts.TtsService;
import br.com.tucunare.apoiodigital.tutorial.agents.ElementSelector.ElementSelectorRequestDTO;
import br.com.tucunare.apoiodigital.tutorial.agents.ElementSelector.ElementSelectorResponseDTO;
import br.com.tucunare.apoiodigital.tutorial.agents.ElementSelector.ElementSelectorService;
import br.com.tucunare.apoiodigital.tutorial.agents.ScreenContextDefiner.ScreenContextDefinerRequestDTO;
import br.com.tucunare.apoiodigital.tutorial.agents.ScreenContextDefiner.ScreenContextDefinerResponseDTO;
import br.com.tucunare.apoiodigital.tutorial.agents.ScreenContextDefiner.ScreenContextDefinerService;
import br.com.tucunare.apoiodigital.usuario.data.Usuario;
import br.com.tucunare.apoiodigital.usuario.service.UsuarioService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

class AcharRespostaServiceTest {

    @Mock
    private UsuarioService usuarioService;

    @Mock
    private PedidoRepository pedidoRepository;

    @Mock
    private RespostaRepository respostaRepository;

    @Mock
    private ComponenteService componenteService;

    @Mock
    private ElementSelectorService elementSelectorService;

    @Mock
    private ScreenContextDefinerService screenContextDefinerService;

    @Mock
    private TtsService ttsService;

    private AcharRespostaService service;
    private Cliente cliente;
    private Usuario usuario;
    private final List<CapturedElementDTO> elementos = List.of(
            new CapturedElementDTO("btn-pagar-boleto", "Button", "Pagar boleto", false, true, 0.0, 0.0, 100.0, 40.0)
    );

    @BeforeEach
    void setup() {
        MockitoAnnotations.openMocks(this);
        service = new AcharRespostaService(
                usuarioService, pedidoRepository, respostaRepository, componenteService,
                elementSelectorService, screenContextDefinerService, ttsService
        );

        cliente = new Cliente("Parceiro Teste", "chave-teste", "saude");
        cliente.setId(UUID.randomUUID());
        usuario = new Usuario("usr_anon_teste", null, cliente);
        usuario.setId(UUID.randomUUID());

        when(usuarioService.resolverOuCriar("usr_anon_teste", cliente)).thenReturn(usuario);
        when(pedidoRepository.save(any(Pedido.class))).thenAnswer(invocation -> invocation.getArgument(0));
        when(respostaRepository.save(any(Resposta.class))).thenAnswer(invocation -> invocation.getArgument(0));
        when(elementSelectorService.executeTask(any()))
                .thenReturn(new ElementSelectorResponseDTO("btn-pagar-boleto", "raciocinio", 0.9));
        when(screenContextDefinerService.executeTask(any()))
                .thenReturn(new ScreenContextDefinerResponseDTO("Toque em Pagar boleto", "Toque no botão Pagar boleto"));
        when(ttsService.synthesize(any())).thenReturn(Optional.empty());
    }

    @Test
    @DisplayName("uses the clarified prompt stored in the Pedido when idPedido is sent")
    void usaPromptEsclarecidoDoPedido() {
        Pedido pedido = new Pedido(usuario, "Quero pagar uma conta");
        pedido.setId(UUID.randomUUID());
        pedido.registrarEsclarecimento("Boleto ou Pix?", "Boleto");
        when(pedidoRepository.findByIdAndUsuarioId(pedido.getId(), usuario.getId())).thenReturn(Optional.of(pedido));

        service.acharResposta(
                new AcharRespostaRequestDTO("usr_anon_teste", "prompt generico", elementos, pedido.getId()),
                cliente
        );

        ArgumentCaptor<Object> seletor = ArgumentCaptor.forClass(Object.class);
        verify(elementSelectorService).executeTask(seletor.capture());
        String promptEnviado = ((ElementSelectorRequestDTO) seletor.getValue()).prompt();
        assertTrue(promptEnviado.contains("Quero pagar uma conta"));
        assertTrue(promptEnviado.contains("Resposta do usuário: Boleto"));
        assertFalse(promptEnviado.contains("prompt generico"));

        ArgumentCaptor<Object> contexto = ArgumentCaptor.forClass(Object.class);
        verify(screenContextDefinerService).executeTask(contexto.capture());
        assertEquals(promptEnviado, ((ScreenContextDefinerRequestDTO) contexto.getValue()).prompt());

        verify(pedidoRepository, never()).save(any(Pedido.class));
    }

    @Test
    @DisplayName("creates a new Pedido when no idPedido is sent")
    void criaPedidoSemIdPedido() {
        service.acharResposta(
                new AcharRespostaRequestDTO("usr_anon_teste", "Quero pagar uma conta", elementos, null),
                cliente
        );

        verify(pedidoRepository).save(any(Pedido.class));
        ArgumentCaptor<Object> seletor = ArgumentCaptor.forClass(Object.class);
        verify(elementSelectorService).executeTask(seletor.capture());
        assertEquals("Quero pagar uma conta", ((ElementSelectorRequestDTO) seletor.getValue()).prompt());
    }

    @Test
    @DisplayName("rejects an idPedido that belongs to another user")
    void rejeitaPedidoDeOutroUsuario() {
        UUID idPedidoAlheio = UUID.randomUUID();
        when(pedidoRepository.findByIdAndUsuarioId(idPedidoAlheio, usuario.getId())).thenReturn(Optional.empty());

        assertThrows(PedidoDoesNotExistException.class, () -> service.acharResposta(
                new AcharRespostaRequestDTO("usr_anon_teste", "Quero pagar uma conta", elementos, idPedidoAlheio),
                cliente
        ));
        verifyNoInteractions(elementSelectorService);
    }
}
