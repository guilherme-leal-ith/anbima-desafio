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
- HAMBURGUER = R$ 20,00 | PASTEL = R$ 15,00 | Outros = R$ 12,00
- Desconto de 10% quando HAMBURGUER + CARNE + SALADA

### Controller
Expondo endpoint do recebimento do pedido
- consumes = "text/plain" conforme especificado no case
- ResponseEntity<?> permite retornar tipos diferentes: Pedido com HTTP 201 em caso de sucesso, ou mensagem de erro com HTTP 400 em caso de validação inválida
- Erros lançados pelo parser são capturados e devolvidos como 400 com mensagem legível ao invés de estourar um 500

### H2
Inicialmente usei H2 em memória jdbc:h2:mem por ser simples de configurar. Durante o desenvolvimento do Módulo B identifiquei um problema: H2 em memória só existe dentro do processo que o criou, então o Módulo B não conseguia acessar o banco do Módulo A, logo pesquisei e migrei para H2 em modo arquivo com AUTO_SERVER=TRUE, que permite múltiplas conexões simultâneas ao mesmo arquivo

### Fila em memória
Ao estudar o case identifiquei que a comunicação entre os módulos precisava ser assíncrona, em outras palavras: o Módulo A não deveria esperar o Módulo B processar para retornar a resposta. Pesquisei sobre esse padrão e cheguei no conceito de message broker, e dentro do  Spring o RabbitMQ via Spring AMQP é a solução padrão.

Com isso em vista, para o MVP optei por implementar uma fila em memória com a mesma funcionalidade (publicar/consumir) que o RabbitMQ teria. Essa decisão foi consciente por dois motivos:
1. Permitiu criar o fluxo completo sem dependência de infraestrutura externa
2. A classe FilaPedido está isolada em pacote próprio, a troca por RabbitMQ não afeta o PedidoService, só a implementação da fila, já que quis criar um código limpo.

Porém identifiquei um problema: o Módulo A e o Módulo B são processos separados, assim a fila em memória, do Módulo A não é 
acessível pelo Módulo B. Tendo em vista que com RabbitMQ isso seria resolvido naturalmente, já que o Modulo A publicaria na fila do broker e o Módulo B consumiria com o Listener, resolvi que para o MVP o Módulo B iria consultar o banco periodicamente com @Scheduled, buscando
pedidos com status = RECEBIDO e os atualizando para ENTREGUE, descobri como fazer essa solução através do post do StackOverflow:
Como agendar várias tarefas no spring boot de forma dinamica?

## Evidências

### Controller
Uso do cURL para enviar HTTP Request
<img width="1059" height="101" alt="image" src="https://github.com/user-attachments/assets/cdefdf82-3148-4655-8082-770d44e97a41" />

### Fila
Temos que dentro do método da fila 'publicar(Long pedidoId)' temo 'System.out.println("Publicado na fila: pedidoId=" + pedidoId)' e ao mandar um cURL percebemos a resposta
<img width="481" height="301" alt="image" src="https://github.com/user-attachments/assets/6ad178a7-93a2-4f60-8454-ca3dc0d563ca" />

## Testes unitários

### PedidoParser
- String com 40 caracteres → campos parseados corretamente
- String sem 40 caracteres exatos → exceção lançada
- Quantidade fora do range 01-99 → exceção lançada
<img width="692" height="183" alt="image" src="https://github.com/user-attachments/assets/2543909c-e837-4b0c-b002-a10aa5bfd9e8" />

### PedidoService
- Cálculo com desconto (HAMBURGUER + CARNE + SALADA, 1 unid) → R$ 18,00
- Cálculo sem desconto (PASTEL + FRANGO + BACON, 2 unid) → R$ 30,00
- Status definido como RECEBIDO em ambos os casos
- Parser e repository isolados com Mockito para testar só a lógica do service
- assertTrue com compareTo ao invés de assertEquals no valor para contornar o problema de casas decimais do BigDecimal
<img width="1162" height="266" alt="image" src="https://github.com/user-attachments/assets/4c540fb8-c2d2-46fb-967a-e07c8a01cc9a" />

## Como rodar

1. Roda a aplicação
2. H2 Console: localhost:8080/h2-console
3. JDBC URL: jdbc:h2:file:./pedidosdb

## Melhorias e refatorações planejadas

- Validação da quantidade: Campo numérico deve rejeitar " 1" ou "1 "
- Casas decimais do valor: retornar sempre com 2 casas decimais
- PostgreSQL: migrar do H2 para PostgreSQL
- Swagger: já trabalhei com Swagger anteriormente e seria interessante para automatizar a documentação e tests dos endpoints
- Tratamento de erros global: Centralizar o tratamento de exceções ao invés de try/catch no controller
