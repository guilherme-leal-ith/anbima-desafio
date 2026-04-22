package com.anbima.modulo_a.fila;

import com.anbima.modulo_a.config.RabbitConfig;
import lombok.RequiredArgsConstructor;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class FilaPedido {

    private final RabbitTemplate rabbitTemplate;

    // Publica o id do pedido na fila do RabbitMQ
    public void publicar(Long pedidoId) {
        String mensagem = "{\"pedidoId\": " + pedidoId + "}";

        rabbitTemplate.convertAndSend(RabbitConfig.FILA_PEDIDOS, mensagem);

        System.out.println("Publicado na fila: pedidoId=" + pedidoId);
    }
}