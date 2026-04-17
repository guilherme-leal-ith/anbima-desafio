package com.anbima.modulo_a.repository;

import com.anbima.modulo_a.entity.Pedido;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PedidoRepository extends JpaRepository<Pedido, Long> {
}