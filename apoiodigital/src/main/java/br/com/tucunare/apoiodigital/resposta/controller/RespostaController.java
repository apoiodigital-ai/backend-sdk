package br.com.tucunare.apoiodigital.resposta.controller;

import br.com.tucunare.apoiodigital.cliente.data.Cliente;
import br.com.tucunare.apoiodigital.limite.LimiteRequisicoesService;
import br.com.tucunare.apoiodigital.resposta.data.*;
import br.com.tucunare.apoiodigital.resposta.service.AcharRespostaService;
import br.com.tucunare.apoiodigital.resposta.service.NecessidadeInformacoesService;
import br.com.tucunare.apoiodigital.resposta.service.RespostaNecessidadeService;
import br.com.tucunare.apoiodigital.resposta.service.RespostaService;
import br.com.tucunare.apoiodigital.security.TenantContext;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;
import java.util.UUID;

@RestController
@RequestMapping("/resposta")
public class RespostaController {

    private final RespostaService respostaService;
    private final NecessidadeInformacoesService necessidadeInformacoesService;
    private final RespostaNecessidadeService respostaNecessidadeService;
    private final AcharRespostaService acharRespostaService;
    private final TenantContext tenantContext;
    private final LimiteRequisicoesService limiteRequisicoesService;

    public RespostaController(
            RespostaService respostaService,
            NecessidadeInformacoesService necessidadeInformacoesService,
            RespostaNecessidadeService respostaNecessidadeService,
            AcharRespostaService acharRespostaService,
            TenantContext tenantContext,
            LimiteRequisicoesService limiteRequisicoesService
    ) {
        this.respostaService = respostaService;
        this.necessidadeInformacoesService = necessidadeInformacoesService;
        this.respostaNecessidadeService = respostaNecessidadeService;
        this.acharRespostaService = acharRespostaService;
        this.tenantContext = tenantContext;
        this.limiteRequisicoesService = limiteRequisicoesService;
    }

    @PostMapping("/validar/necessidade-informacoes")
    public ResponseEntity<NecessidadeInformacoesResponseDTO> validarNecessidadeInformacoes(
            @RequestBody NecessidadeInformacoesRequestDTO request
    ) {
        Cliente cliente = tenantContext.getClienteAtual();
        limiteRequisicoesService.verificarUsuario(cliente, request.userId());
        return ResponseEntity.ok(necessidadeInformacoesService.validar(request, cliente));
    }

    @PostMapping("/validar/resposta-necessidade")
    public ResponseEntity<RespostaNecessidadeResponseDTO> validarRespostaNecessidade(
            @RequestBody RespostaNecessidadeRequestDTO request
    ) {
        Cliente cliente = tenantContext.getClienteAtual();
        limiteRequisicoesService.verificarUsuario(cliente, request.userId());
        return ResponseEntity.ok(respostaNecessidadeService.validar(request, cliente));
    }

    @PostMapping("/achar-resposta")
    public ResponseEntity<AcharRespostaResponseDTO> acharResposta(
            @RequestBody AcharRespostaRequestDTO request
    ) {
        Cliente cliente = tenantContext.getClienteAtual();
        limiteRequisicoesService.verificarUsuario(cliente, request.userId());
        return ResponseEntity.ok(acharRespostaService.acharResposta(request, cliente));
    }

    @GetMapping("/listar/{idPedido}")
    public ResponseEntity<List<Map<String, String>>> listarRespostas(@PathVariable UUID idPedido) {
        Cliente cliente = tenantContext.getClienteAtual();
        return ResponseEntity.ok(respostaService.listarRespostaPorPedido(idPedido, cliente));
    }
}
