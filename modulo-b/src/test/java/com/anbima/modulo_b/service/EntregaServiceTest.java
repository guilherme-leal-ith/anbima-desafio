package com.anbima.modulo_b.service;

import com.anbima.modulo_b.entity.Pedido;
import com.anbima.modulo_b.entity.StatusPedido;
import com.anbima.modulo_b.repository.PedidoRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class EntregaServiceTest {

    @Mock
    private PedidoRepository repository;

    @InjectMocks
    private EntregaService service;

    @Test
    void deveAtualizarStatusParaEntregue() {
        // Simula pedido RECEBIDO no banco
        Pedido pedido = new Pedido();
        pedido.setId(1L);
        pedido.setStatus(StatusPedido.RECEBIDO);

        when(repository.findByStatus(StatusPedido.RECEBIDO))
                .thenReturn(List.of(pedido));
        when(repository.save(any())).thenAnswer(i -> i.getArgument(0));

        service.processarPedidosRecebidos();

        // Checa que o status foi atualizado para ENTREGUE
        assertEquals(StatusPedido.ENTREGUE, pedido.getStatus());
        // Checa que o save foi chamado uma vez
        verify(repository, times(1)).save(pedido);
    }

    @Test
    void deveRetornarListaVaziaQuandoNaoHouverRecebidos() {
        when(repository.findByStatus(StatusPedido.RECEBIDO))
                .thenReturn(List.of());

        service.processarPedidosRecebidos();

        // Checa que save nao foi chamado
        //O never() vai garantir que o save nunca foi chamado
        verify(repository, never()).save(any());
    }

    @Test
    void deveLancarExcecaoQuandoPedidoNaoEncontrado() {
        // Simula pedido inexistente retornando Optional vazio
        when(repository.findById(10L)).thenReturn(Optional.empty());

        assertThrows(RuntimeException.class, () ->
                service.buscarPorId(10L)
        );
    }

    @Test
    void deveRetornarTodosOsPedidos() {
        // Simula dois pedidos no banco e verifica se ambos sao retornados
        Pedido p1 = new Pedido();
        p1.setId(1L);
        Pedido p2 = new Pedido();
        p2.setId(2L);

        when(repository.findAll()).thenReturn(List.of(p1, p2));

        List<Pedido> resultado = service.listarTodos();

        assertEquals(2, resultado.size());
        verify(repository, times(1)).findAll();
    }

    @Test
    void deveRetornarPedidoPorIdQuandoEncontrado() {
        // Simula pedido existente e verifica se e retornado corretamente
        Pedido pedido = new Pedido();
        pedido.setId(5L);
        pedido.setStatus(StatusPedido.ENTREGUE);

        when(repository.findById(5L)).thenReturn(Optional.of(pedido));

        Pedido resultado = service.buscarPorId(5L);

        assertEquals(5L, resultado.getId());
        assertEquals(StatusPedido.ENTREGUE, resultado.getStatus());
    }
}