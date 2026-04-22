package com.anbima.modulo_b.scheduler;

import com.anbima.modulo_b.config.RabbitConfig;
import com.anbima.modulo_b.service.EntregaService;
import lombok.RequiredArgsConstructor;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class ProcessadorPedido {

    private final EntregaService entregaService;

    // Substitui o @Scheduled — agora consome a fila real do RabbitMQ
    @RabbitListener(queues = RabbitConfig.FILA_PEDIDOS)
    public void processar(String mensagem) {
        // Extrai o pedidoId do JSON {"pedidoId": 1}
        Long pedidoId = Long.parseLong(mensagem.replaceAll("[^0-9]", ""));
        entregaService.processarPorId(pedidoId);
    }
}