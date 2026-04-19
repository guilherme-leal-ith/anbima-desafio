package com.anbima.modulo_a.service;

import com.anbima.modulo_a.entity.Pedido;
import com.anbima.modulo_a.entity.StatusPedido;
import com.anbima.modulo_a.fila.FilaPedido;
import com.anbima.modulo_a.parser.PedidoParser;
import com.anbima.modulo_a.repository.PedidoRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;

@Service
@RequiredArgsConstructor
public class PedidoService {
    // Injenta dependencias
    private final PedidoRepository repository;
    private final PedidoParser parser;
    private final FilaPedido fila;

    // Parseia a string, calcula o valor e salva com status RECEBIDO
    public Pedido processar(String linha) {
        Pedido pedido = parser.parsearString(linha);

        BigDecimal valor = calcularValor(pedido);
        pedido.setValor(valor);
        pedido.setStatus(StatusPedido.RECEBIDO);

        Pedido salvo = repository.save(pedido);
        //Publica o id do pedido salvo na fila apos persistir
        fila.publicar(salvo.getId());
        return salvo;
    }

    // Identifica o tipo de lanche para calcular o valor de acordo
    private BigDecimal calcularValor(Pedido pedido) {
        BigDecimal precoBase = switch (pedido.getTipoLanche().toUpperCase()) {
            case "HAMBURGUER" -> new BigDecimal("20.00");
            case "PASTEL" -> new BigDecimal("15.00");
            default -> new BigDecimal("12.00");
        };

        BigDecimal total = precoBase.multiply(
                new BigDecimal(pedido.getQuantidade()));

        // Verifica combinacao HAMBURGUER + CARNE + SALADA para aplicar ou nao desconto
        boolean temDesconto = "HAMBURGUER".equalsIgnoreCase(pedido.getTipoLanche())
                && "CARNE".equalsIgnoreCase(pedido.getProteina())
                && "SALADA".equalsIgnoreCase(pedido.getAcompanhamento());

        if (temDesconto) {
            BigDecimal desconto = total.multiply(new BigDecimal("0.10"));
            total = total.subtract(desconto);
        }

        return total;
    }
}