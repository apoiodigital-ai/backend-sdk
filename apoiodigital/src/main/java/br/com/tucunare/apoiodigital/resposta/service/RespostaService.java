package br.com.tucunare.apoiodigital.resposta.service;

import br.com.tucunare.apoiodigital.resposta.repository.RespostaRepository;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
public class RespostaService {

    private final RespostaRepository respostaRepository;

    public RespostaService(RespostaRepository respostaRepository) {
        this.respostaRepository = respostaRepository;
    }

    public List<Map<String, String>> listarRespostaPorPedido(UUID pedidoId) {

        List<Object[]> resultados =
                respostaRepository.listarRespostaPorIdPedido(pedidoId);

        if (resultados == null || resultados.isEmpty()) {
            throw new RuntimeException("Pedido does not exist");
        }

        List<Map<String, String>> respostas = new ArrayList<>();

        for (Object[] row : resultados) {

            if (row.length < 3) {
                continue;
            }

            Map<String, String> respostaMap = new HashMap<>();
            respostaMap.put("iaMessage", Objects.toString(row[0], ""));
            respostaMap.put("timestamp", Objects.toString(row[1], ""));
            respostaMap.put("raciocinio", Objects.toString(row[2], ""));

            respostas.add(respostaMap);
        }

        return respostas;
    }
}
