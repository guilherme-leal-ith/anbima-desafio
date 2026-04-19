package com.anbima.modulo_a.service;

import com.anbima.modulo_a.entity.Pedido;
import com.anbima.modulo_a.entity.StatusPedido;
import com.anbima.modulo_a.fila.FilaPedido;
import com.anbima.modulo_a.parser.PedidoParser;
import com.anbima.modulo_a.repository.PedidoRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class PedidoServiceTest {
    // Uso do Mockito para simular a presenca das dependencias
    @Mock
    private PedidoRepository repository;

    @Mock
    private FilaPedido fila;

    @Mock
    private PedidoParser parser;
    // Cria o service e injeta nele
    @InjectMocks
    private PedidoService service;

    @Test
    void deveCalcularValorComDesconto() {
        Pedido pedido = new Pedido();
        pedido.setTipoLanche("HAMBURGUER");
        pedido.setProteina("CARNE");
        pedido.setAcompanhamento("SALADA");
        pedido.setQuantidade(1);

        //Quando alguem chamar parsearString retorna esse 'pedido' ja declarado, nao passando
        //de fato pelo parser, a fim de testar as regras/logica de negocio do service para
        //o teste unitario
        when(parser.parsearString(any())).thenReturn(pedido);
        //Quando salvar retorna uma resposta do mesmo argumento, como se fingisse que salvou
        when(repository.save(any())).thenAnswer(i -> i.getArgument(0));

        Pedido resultado = service.processar("qualquer");

        //Verifica resultado com desconto
        assertTrue(
                resultado.getValor().compareTo(new BigDecimal("18.00")) == 0
        );
        assertEquals(StatusPedido.RECEBIDO, resultado.getStatus());
    }

    @Test
    void deveCalcularValorSemDesconto() {
        Pedido pedido = new Pedido();
        pedido.setTipoLanche("PASTEL");
        pedido.setProteina("FRANGO");
        pedido.setAcompanhamento("BACON");
        pedido.setQuantidade(2);

        when(parser.parsearString(any())).thenReturn(pedido);
        when(repository.save(any())).thenAnswer(i -> i.getArgument(0));

        Pedido resultado = service.processar("qualquer");

        //Verifica resultado sem desconto
        assertTrue(
                resultado.getValor().compareTo(new BigDecimal("30.00")) == 0
        );
        assertEquals(StatusPedido.RECEBIDO, resultado.getStatus());
    }
}