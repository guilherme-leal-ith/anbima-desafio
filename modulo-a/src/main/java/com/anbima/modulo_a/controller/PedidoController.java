package com.anbima.modulo_a.controller;

import com.anbima.modulo_a.entity.Pedido;
import com.anbima.modulo_a.service.PedidoService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

// Expõe os endpoints REST do Módulo A
@RestController
@RequestMapping("/pedidos")
@RequiredArgsConstructor
public class PedidoController {
    // Injeta dependência
    private final PedidoService service;

    // Consome o text/plain conforme especificado no case
    // ResponseEntity<?> pois permite retornar tanto Pedido (201) quanto uma String de erro (400)
    @PostMapping(value = "/posicional", consumes = "text/plain")
    public ResponseEntity<?> receberPosicional(@RequestBody String linha) {
        try {
            Pedido pedido = service.processar(linha);
            return ResponseEntity.status(HttpStatus.CREATED).body(pedido);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }
}