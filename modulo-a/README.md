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

### Service
Implementa lógica de processamento da String recebida e salva com os dados já processados no banco.
Regras de negócio implementadas:
- HAMBURGUER = R$ 20,00 | PASTEL = R$ 15,00 | Outros = R$ 12,00
- Desconto de 10% quando HAMBURGUER + CARNE + SALADA

### Controller
Expondo endpoint do recebimento do pedido
- `consumes = "text/plain"` conforme especificado no case.
- `ResponseEntity<?>` permite retornar tipos diferentes: Pedido com HTTP 201 em caso de sucesso, ou mensagem de erro com HTTP 400 em caso de validação inválida.
- Erros lançados pelo parser são capturados e devolvidos como 400 com mensagem legível ao invés de estourar um 500.

## Testes unitários

### PedidoParser
- String com 40 caracteres → campos parseados corretamente
- String sem 40 caracteres exatos → exceção lançada
- Quantidade fora do range 01-99 → exceção lançada

### PedidoService
- Cálculo com desconto (HAMBURGUER + CARNE + SALADA, 1 unid) → R$ 18,00
- Cálculo sem desconto (PASTEL + FRANGO + BACON, 2 unid) → R$ 30,00
- Status definido como RECEBIDO em ambos os casos
- Parser e repository isolados com Mockito para testar só a lógica do service
- assertTrue com compareTo ao invés de assertEquals no valor para contornar o problema de casas decimais do BigDecimal

### Controller
Uso do cURL para enviar HTTP Request
<img width="1059" height="101" alt="image" src="https://github.com/user-attachments/assets/cdefdf82-3148-4655-8082-770d44e97a41" />

## Como rodar

1. Roda a aplicação
2. Acessa o H2 Console disponível em: "localhost:8080/h2-console"
3. insere em JDBC URL: jdbc:h2:mem:pedidosdb

## Melhorias e refatorações planejadas

- Validação da quantidade: Campo numérico deve rejeitar " 1" ou "1 "
- Casas decimais do valor: retornar sempre com 2 casas decimais
- PostgreSQL: migrar do H2 para PostgreSQL
- Swagger: já trabalhei com Swagger anteriormente e seria interessante para automatizar a documentação e tests dos endpoints
- Tratamento de erros global: Centralizar o tratamento de exceções ao invés de try/catch no controller
