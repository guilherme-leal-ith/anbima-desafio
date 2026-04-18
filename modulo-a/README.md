# Módulo A — Entrada/Gateway

Ele que recebe a string posicional, valida, converte,
calcula o valor e persiste o pedido no banco.

## Fluxo

Usei a arquitetura em camadas para dividir as responsabilidades, a fim de manter um código limpo e fácil de refatorar

POST /pedidos/posicional -> Parser valida e converte a string -> Service aplica regras de negócio e calcula valor -> Salva no banco com status RECEBIDO -> Publica pedidoId na fila -> Retorna 201 com JSON do pedido

## Decisões técnicas

### Entidade Pedido
Mapeada conforme o banco do case. @CreationTimestamp com updatable=false para preencher criadoEm automaticamente e garantir
que nunca seja alterado, já que não faz sentido com o dado que salva a data de criação do pedido. 
StatusPedido foi implementado como enum com @Enumerated(EnumType.STRING) para salvar o valor legível no banco ao invés do índice referente
ao valor do enum.

### Parser
Fatia a string nas posições fixas necessárias com substring() e aplica trim() nos campos alfanuméricos, removendo os espaços em branco.

### Testes
Testes unitários do PedidoParser:
- String com 40 caracteres -> campos parseados corretamente
- String sem 40 caracteres exatos -> exceção lançada
- Quantidade fora do range 01-99 -> exceção lançada

## Como rodar

1. Roda a aplicação
2. Acessa o H2 Console disponível em: "localhost:8080/h2-console"
3. insere em JDBC URL: jdbc:h2:mem:pedidosdb
