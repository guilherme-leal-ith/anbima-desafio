import { Component } from '@angular/core';
import { FormsModule } from '@angular/forms';
import { CommonModule } from '@angular/common';
import { PedidoService } from '../services/pedido.service';

// Considerado componente standalone
@Component({
  selector: 'app-novo-pedido',
  standalone: true,
  imports: [FormsModule, CommonModule],
  templateUrl: './novo-pedido.component.html'
})
export class NovoPedidoComponent {

  linha = '';
  resultado: any = null;
  erro = '';

  // Injecao de dependencia do service via constructor
  constructor(private pedidoService: PedidoService) {}

  enviar() {
    this.resultado = null;
    this.erro = '';

    // Valida tamanho antes de chamar a API
    // Logica de negocio incrementada no front, a fim de evitar
    // sobrecarga do back
    if (this.linha.length !== 40) {
      this.erro = `String deve ter 40 caracteres. Atual: ${this.linha.length}`;
      return;
    }

    // subscribe() para lidar com a resposta assincrona do HttpClient
    this.pedidoService.enviarPosicional(this.linha).subscribe({
      next: (res) => this.resultado = res,
      error: (err) => this.erro = err.error || 'Erro ao enviar pedido'
    });
  }
}