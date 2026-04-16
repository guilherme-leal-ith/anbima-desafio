# Módulo A — Entrada/Gateway

Ele que recebe a string posicional, valida, converte,
calcula o valor e persiste o pedido no banco.

## Fluxo

Usei a arquitetura em camadas para dividir as responsabilidades, a fim de manter um código limpo e fácil de refatorar

POST /pedidos/posicional -> Parser valida e converte a string -> Service aplica regras de negócio e calcula valor -> Salva no banco com status RECEBIDO -> Publica pedidoId na fila -> Retorna 201 com JSON do pedido

## Decisões técnicas

1. Fiz uso dos conceitos de arquitetura em camadas e adicionei a entidade Pedido, com os tipos de dados de acordo com o modelo enviado no case, usei
2. a documentação oficial do Spring Boot para lembrar como declarar as annotations corretamente.

## Como rodar

1. Roda a aplicação
2. Acessa o H2 Console disponível em: "localhost:8080/h2-console"
3. insere em JDBC URL: jdbc:h2:mem:pedidosdb
