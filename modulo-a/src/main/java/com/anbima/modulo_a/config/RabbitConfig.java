package com.anbima.modulo_a.config;

import org.springframework.amqp.core.Queue;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class RabbitConfig {

    public static final String FILA_PEDIDOS = "pedidos.recebidos";

    // Declara a fila como duravel, ou seja, sobrevive a restart do RabbitMQ
    @Bean
    public Queue filaPedidos() {
        return new Queue(FILA_PEDIDOS, true);
    }
}