import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { PedidoService } from '../services/pedido.service';

@Component({
  selector: 'app-pedidos',
  standalone: true,
  imports: [CommonModule, FormsModule],
  templateUrl: './pedidos.component.html'
})

// Uso do OnInit para executar logica ao iniciar o componente
export class PedidosComponent implements OnInit {

  pedidos: any[] = [];
  filtro = '';

  constructor(private pedidoService: PedidoService) {}

  // Carrega pedidos ao iniciar o componente
  ngOnInit() {
    this.carregar();
  }

  carregar() {
    this.pedidoService.listarPedidos().subscribe({
      next: (res) => this.pedidos = res,
      error: () => alert('Erro ao carregar pedidos')
    });
  }

  // Filtra pedidos por status no frontend, evitando chamada extra a API, novamente pensando em otimizacao
  get pedidosFiltrados() {
    if (!this.filtro) return this.pedidos;
    return this.pedidos.filter(p => p.status === this.filtro);
  }
}