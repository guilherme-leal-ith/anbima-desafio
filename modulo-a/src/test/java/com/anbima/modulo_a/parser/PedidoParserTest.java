package com.anbima.modulo_a.parser;

import com.anbima.modulo_a.entity.Pedido;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;
// Testes unitarios do parser de string posicional
class PedidoParserTest {

    private PedidoParser parser;

    // Executado antes de cada teste para inicializar o parser
    @BeforeEach
    void setUp() {
        parser = new PedidoParser();
    }

    @Test
    void deveParsearStringValida() {
        String linha = "HAMBURGUER" + "CARNE     " + "SALADA    " + "01" + "COCA    ";

        System.out.println(linha.length()); // Evidencia que tem 40 caracteres
        System.out.println(linha.substring(30, 32)); // Deve imprimir "01"

        Pedido pedido = parser.parsearString(linha);

        // Testa se cada campo foi extraido e convertido corretamente
        assertEquals("HAMBURGUER", pedido.getTipoLanche());
        assertEquals("CARNE", pedido.getProteina());
        assertEquals("SALADA", pedido.getAcompanhamento());
        assertEquals(1, pedido.getQuantidade());
        assertEquals("COCA", pedido.getBebida());
    }

    @Test
    void deveLancarExcecaoQuandoStringInvalida() {
        //Verifica se o parser rejeita String com menos de 40 caracteres
        assertThrows(IllegalArgumentException.class,
                () -> parser.parsearString("XYZ")
        );
    }

    @Test
    void deveLancarExcecaoQuandoQuantidadeInvalida() {
        //Verifica se o parser rejeita String com o campo 'quantidade' menor que 1 ou maior que 99
        assertThrows(IllegalArgumentException.class,
                () -> parser.parsearString("HAMBURGUER CARNE     SALADA    00COCA    ")
        );
        assertThrows(IllegalArgumentException.class,
                () -> parser.parsearString("HAMBURGUER CARNE     SALADA    100COCA   ")
        );
    }

    @Test
    void deveLancarExcecaoQuandoStringNula() {
        // Verifica se o parser rejeita null explicitamente
        assertThrows(IllegalArgumentException.class,
                () -> parser.parsearString(null)
        );
    }

    @Test
    void deveLancarExcecaoQuandoStringMaiorQue40() {
        // Verifica se o parser rejeita String com mais de 40 caracteres
        assertThrows(IllegalArgumentException.class,
                () -> parser.parsearString("HAMBURGUER" + "CARNE     " + "SALADA    " + "01" + "COCA     ")
        );
    }
}