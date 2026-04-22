package com.anbima.modulo_b.config;

import org.springframework.amqp.core.Queue;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class RabbitConfig {

    public static final String FILA_PEDIDOS = "pedidos.recebidos";

    @Bean
    public Queue filaPedidos() {
        return new Queue(FILA_PEDIDOS, true);
    }
}