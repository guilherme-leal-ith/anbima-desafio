package com.anbima.modulo_b.scheduler;

import com.anbima.modulo_b.service.EntregaService;
import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class ProcessadorPedido {

    private final EntregaService entregaService;

    // Executa a cada 3 segundos simulando o @RabbitListener da fila pedidos.recebidos
    // Em producao seria substituido por @RabbitListener(queues = "pedidos.recebidos")
    @Scheduled(fixedDelay = 3000)
    public void processar() {
        entregaService.processarPedidosRecebidos();
    }
}