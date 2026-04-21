import { Injectable } from '@angular/core';
import { HttpClient, HttpHeaders } from '@angular/common/http';
import { Observable } from 'rxjs';

@Injectable({ providedIn: 'root' })
export class PedidoService {

  private urlModuloA = 'http://localhost:8080';
  private urlModuloB = 'http://localhost:8081';

  constructor(private http: HttpClient) {}

  // Envia a string posicional para o Modulo A
  enviarPosicional(linha: string): Observable<any> {
    const headers = new HttpHeaders({ 'Content-Type': 'text/plain' });
    return this.http.post(
      `${this.urlModuloA}/pedidos/posicional`,
      linha,
      { headers }
    );
  }

  // Busca todos os pedidos do Modulo B
  listarPedidos(): Observable<any[]> {
    return this.http.get<any[]>(`${this.urlModuloB}/pedidos`);
  }
}