package com.anbima.modulo_b.service;

import com.anbima.modulo_b.entity.Pedido;
import com.anbima.modulo_b.entity.StatusPedido;
import com.anbima.modulo_b.repository.PedidoRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class EntregaService {

    private final PedidoRepository repository;

    // Busca todos RECEBIDOS e atualiza para ENTREGUE
    public void processarPedidosRecebidos() {
        List<Pedido> recebidos = repository.findByStatus(StatusPedido.RECEBIDO);

        // Simula o listener da fila
        for (Pedido pedido : recebidos) {
            pedido.setStatus(StatusPedido.ENTREGUE);
            repository.save(pedido);
            System.out.println("Pedido " + pedido.getId() + " atualizado para ENTREGUE");
        }
    }

    // Busca o pedido pelo id recebido da fila e atualiza para ENTREGUE
    public void processarPorId(Long pedidoId) {
        Pedido pedido = repository.findById(pedidoId)
                .orElseThrow(() -> new RuntimeException("Pedido não encontrado: " + pedidoId));
        pedido.setStatus(StatusPedido.ENTREGUE);
        repository.save(pedido);
        System.out.println("Pedido " + pedidoId + " atualizado para ENTREGUE");
    }

    // Consulta todos pedidos da fila
    public List<Pedido> listarTodos() {
        return repository.findAll();
    }

    // Consulta pedido por Id da fila
    public Pedido buscarPorId(Long id) {
        return repository
                .findById(id).orElseThrow(() -> new RuntimeException("Pedido não encontrado"));
    }
}