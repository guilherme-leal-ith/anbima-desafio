import { Component } from '@angular/core';
import { NovoPedidoComponent } from './novo-pedido/novo-pedido.component';
import { PedidosComponent } from './pedidos/pedidos.component';

@Component({
  selector: 'app-root',
  standalone: true,
  imports: [NovoPedidoComponent, PedidosComponent],
  templateUrl: './app.component.html'
})

// Controle de qual tela esta visivel
export class AppComponent {
  tela: 'novo-pedido' | 'pedidos' = 'novo-pedido';
}