package br.com.tucunare.apoiodigital.atalho.service;

import br.com.tucunare.apoiodigital.atalho.data.Atalho;
import br.com.tucunare.apoiodigital.atalho.exception.AtalhoDoesNotExistException;
import br.com.tucunare.apoiodigital.atalho.repository.AtalhoRepository;
import br.com.tucunare.apoiodigital.findbestapp.agents.DefineAtalhoTitle.DefineAtalhoTitleResponseDTO;
import br.com.tucunare.apoiodigital.findbestapp.agents.DefineAtalhoTitle.DefineAtalhoTitleService;
import br.com.tucunare.apoiodigital.pedido.data.Pedido;
import br.com.tucunare.apoiodigital.pedido.repository.PedidoRepository;
import br.com.tucunare.apoiodigital.usuario.repository.UsuarioRepository;
import br.com.tucunare.apoiodigital.usuario.exception.UsuarioDoesNotExistException;

import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.util.Arrays;
import java.util.List;
import java.util.UUID;

@Service
public class AtalhoService {

    private final AtalhoRepository atalhoRepository;
    private final PedidoRepository requisicaoRepository;
    private final UsuarioRepository usuarioRepository;
    private final DefineAtalhoTitleService defineAtalhoTitleService;


    public AtalhoService(
            AtalhoRepository atalhoRepository,
            PedidoRepository requisicaoRepository,
            UsuarioRepository usuarioRepository, DefineAtalhoTitleService defineAtalhoTitleService
    ) {
        this.atalhoRepository = atalhoRepository;
        this.requisicaoRepository = requisicaoRepository;
        this.usuarioRepository = usuarioRepository;
        this.defineAtalhoTitleService = defineAtalhoTitleService;
    }

    public Atalho findById(UUID id) {
        return atalhoRepository.findById(id)
                .orElseThrow(AtalhoDoesNotExistException::new);
    }

    public Atalho save(Atalho atalho) {
        return atalhoRepository.save(atalho);
    }

    public void deleteById(UUID id) {
        atalhoRepository.deleteById(id);
    }

    public void salvarAtalhosIniciais(List<Pedido> requisicoes) {
        if (requisicoes == null || requisicoes.size() < 3) {
            throw new IllegalArgumentException("Lista de requisições insuficiente");
        }

        Atalho atalho1 = new Atalho(requisicoes.get(0), "Quero pedir comida");
        Atalho atalho2 = new Atalho(requisicoes.get(1), "Quero mandar mensagem");
        Atalho atalho3 = new Atalho(requisicoes.get(2), "Quero pedir Uber");

        atalhoRepository.saveAll(
                Arrays.asList(atalho1, atalho2, atalho3)
        );
    }

    @Async
    public void criarAtalho(Pedido requisicao, Pedido reqMatch) {

        if(reqMatch != null){

            Atalho opAtalho = atalhoRepository.findByPedido(reqMatch).orElseThrow(AtalhoDoesNotExistException::new);
            String titulo = opAtalho.getTitulo();
            if(titulo == null || titulo.isEmpty()){
                DefineAtalhoTitleResponseDTO defineAtalhoTitleResponseDTO = defineAtalhoTitleService.executeTask(requisicao.getPrompt());
                titulo = defineAtalhoTitleResponseDTO.titulo();
            }

            Atalho atalho = new Atalho(requisicao, titulo);
            Atalho atalhoPersistido = atalhoRepository.save(atalho);

            System.out.println("ATALHO CRIADO! " + atalhoPersistido.getTitulo());
            return;
        }

        DefineAtalhoTitleResponseDTO defineAtalhoTitleResponseDTO = defineAtalhoTitleService.executeTask(requisicao.getPrompt());

        Atalho atalho = new Atalho(requisicao, defineAtalhoTitleResponseDTO.titulo());
        Atalho atalhoPersistido = atalhoRepository.save(atalho);

        System.out.println("ATALHO CRIADO! " + atalhoPersistido.getTitulo());

    }

    public Pedido iniciarAtalho(UUID idAtalho) {
        Atalho atalho = findById(idAtalho);

        Pedido requisicaoBase = atalho.getPedido();

        Pedido novaPedido = new Pedido(
                requisicaoBase.getUsuario(),
                requisicaoBase.getPrompt()
        );

        return requisicaoRepository.save(novaPedido);
    }

    public List<Atalho> carregarAtalhos(UUID idUsuario) {
        usuarioRepository.findById(idUsuario)
                .orElseThrow(UsuarioDoesNotExistException::new);

        List<Atalho> atalhos =
                atalhoRepository.findByPedidoUsuarioId(idUsuario);

        return atalhos.subList(0, atalhos.size());
    }
}
