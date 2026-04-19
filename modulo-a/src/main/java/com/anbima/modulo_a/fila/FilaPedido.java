package com.anbima.modulo_a.fila;

import org.springframework.stereotype.Service;
import java.util.LinkedList;
import java.util.Queue;

// E camada service, mas nao de negocio, logo optei por deixar fora do pacote service
@Service
public class FilaPedido {
    // Fila FIFO dos ids
    private final Queue<Long> fila = new LinkedList<>();

    // Adiciona o Id do pedido na fila
    public void publicar(Long pedidoId) {
        fila.add(pedidoId);
        System.out.println("Publicado na fila: pedidoId=" + pedidoId);
    }

    // Remove e retorna o primeiro elemento da fila (FIFO)
    public Long consumir() {
        return fila.poll();
    }
}