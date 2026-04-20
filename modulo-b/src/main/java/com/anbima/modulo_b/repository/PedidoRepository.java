package com.anbima.modulo_b.repository;

import com.anbima.modulo_b.entity.Pedido;
import com.anbima.modulo_b.entity.StatusPedido;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface PedidoRepository extends JpaRepository<Pedido, Long> {
    // Busca todos os pedidos com status RECEBIDO para simular o consumo da fila
    List<Pedido> findByStatus(StatusPedido status);
}