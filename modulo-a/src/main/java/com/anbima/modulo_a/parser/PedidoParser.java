package com.anbima.modulo_a.parser;

import com.anbima.modulo_a.entity.Pedido;
import org.springframework.stereotype.Component;

// Annotation ecessario para o gerenciamento e injecao dessa classe como dependência
@Component
public class PedidoParser {
    public Pedido parsearString(String linhaPosicional) {
        // Checa null para evitar exception e a string posicional PRECISA ter 40 caracteres
        if (linhaPosicional == null || linhaPosicional.length() != 40) {
            throw new IllegalArgumentException("A linha recebida deve ter exatamente 40 caracteres");
        }
        // Refatorar para usar regex.
        // Separando os blocos da String recebida
        String tipoLanche = linhaPosicional.substring(0, 10).trim();
        String proteina = linhaPosicional.substring(10, 20).trim();
        String acompanhamento = linhaPosicional.substring(20, 30).trim();
        String quantidadeStr= linhaPosicional.substring(30, 32).trim();
        String bebida = linhaPosicional.substring(32, 40).trim();
        int quantidadeInt;

        //Passando a quantidade de String para um dado numerico, caso não consiga lanca exception
        try {
            quantidadeInt = Integer.parseInt(quantidadeStr);
        } catch (NumberFormatException e) {
            throw new IllegalArgumentException(
                    "Quantidade deve ser numérica");
        }

        // Checa a regra de que a quantidade deve estar entre 1 e 99, caso nao esteja lança exception
        if (quantidadeInt < 1 || quantidadeInt > 99) {
            throw new IllegalArgumentException(
                    "Quantidade deve estar entre 01 e 99"
            );
        }

        Pedido pedido = new Pedido();
        pedido.setTipoLanche(tipoLanche);
        pedido.setProteina(proteina);
        pedido.setAcompanhamento(acompanhamento);
        pedido.setQuantidade(quantidadeInt);
        pedido.setBebida(bebida);

        return pedido;
    }
}
