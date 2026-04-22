# Módulo A — Entrada/Gateway

Ele que recebe a string posicional, valida, converte, calcula o valor e persiste o pedido no banco.

## Fluxo

Usei a arquitetura em camadas para dividir as responsabilidades, a fim de manter um código limpo e fácil de refatorar

POST /pedidos/posicional -> Parser valida e converte a string -> Service aplica regras de negócio e calcula valor -> Salva no banco com status RECEBIDO -> Publica pedidoId na fila -> Retorna 201 com JSON do pedido

## Decisões técnicas

### Entidade Pedido
Mapeada conforme o  do case. @CreationTimestamp com updatable=false para preencher criadoEm automaticamente e garantir
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

### Banco de dados
Migrado do H2 para PostgreSQL. O banco sobe via Docker Compose na raiz do projeto.
A tabela é criada automaticamente pelo Hibernate via ddl-auto=update.

### Fila em memória
Ao estudar o case identifiquei que a comunicação entre os módulos precisava ser assíncrona, em outras palavras: o Módulo A não deveria esperar o Módulo B processar para retornar a resposta. Pesquisei sobre esse padrão e cheguei no conceito de message broker, e dentro do  Spring o RabbitMQ via Spring AMQP é a solução padrão.

Com isso em vista, para o MVP optei por implementar uma fila em memória com a mesma funcionalidade (publicar/consumir) que o RabbitMQ teria. Essa decisão foi consciente por dois motivos:
1. Permitiu criar o fluxo completo sem dependência de infraestrutura externa
2. A classe FilaPedido está isolada em pacote próprio, a troca por RabbitMQ não afeta o PedidoService, só a implementação da fila, já que quis criar um código limpo.

Porém identifiquei um problema: o Módulo A e o Módulo B são processos separados, assim a fila em memória, do Módulo A não é 
acessível pelo Módulo B. Tendo em vista que com RabbitMQ isso seria resolvido naturalmente, já que o Modulo A publicaria na fila do broker e o Módulo B consumiria com o Listener, resolvi que para o MVP o Módulo B iria consultar o banco periodicamente com @Scheduled, buscando
pedidos com status = RECEBIDO e os atualizando para ENTREGUE, descobri como fazer essa solução através do post do StackOverflow:
Como agendar várias tarefas no spring boot de forma dinamica?

### CORS
Configurei o CorsConfig para permitir requisições do frontend (localhost:4200). Sem essa configuração o browser bloqueia as chamadas identifiquei o erro no console do browser e pesquisei a solução.

## Evidências

### Controller
Uso do cURL para enviar HTTP Request
<img width="1059" height="101" alt="image" src="https://github.com/user-attachments/assets/cdefdf82-3148-4655-8082-770d44e97a41" />

### Fila
Temos que dentro do método da fila 'publicar(Long pedidoId)' temo 'System.out.println("Publicado na fila: pedidoId=" + pedidoId)' e ao mandar um cURL percebemos a resposta
<img width="481" height="301" alt="image" src="https://github.com/user-attachments/assets/6ad178a7-93a2-4f60-8454-ca3dc0d563ca" />

## Testes unitários

### PedidoParser
- String com 40 caracteres -> campos parseados corretamente
- String nula -> exceção lançada
- String sem 40 caracteres exatos (menor ou maior) -> exceção lançada
- Quantidade fora do range 01-99 -> exceção lançada
<img width="555" height="225" alt="image" src="https://github.com/user-attachments/assets/8f6fdf80-f0b4-44a8-b008-ed6886d428ad" />

### PedidoService
- Cálculo com desconto (HAMBURGUER + CARNE + SALADA, 1 unid) -> R$ 18,00
- Cálculo com desconto (HAMBURGUER + CARNE + SALADA, 2 unid) -> R$ 36,00
- Cálculo sem desconto (PASTEL + FRANGO + BACON, 2 unid) -> R$ 30,00
- Cálculo sem desconto (tipo 'Outros', 3 unid) -> R$ 36,00
- Status definido como RECEBIDO em todos os casos
- Verificação de que 'fila.publicar()' é chamado com o id correto após o save
- Parser e repository isolados com Mockito para testar só a lógica do service
- assertTrue com compareTo ao invés de assertEquals no valor para contornar o problema de casas decimais do BigDecimal
<img width="747" height="300" alt="image" src="https://github.com/user-attachments/assets/50073c75-d741-4b12-a0aa-8630388114de" />

## Como rodar

Rodar aplicação
1. Dentro da pasta modulo-a
2. mvn spring-boot:run - roda a aplicação

Rodar testes
1. Dentro da pasta modulo-a
2. mvn test - roda os testes

## Melhorias e refatorações planejadas

- Casas decimais do valor: retornar sempre com 2 casas decimais
- Swagger: já trabalhei com Swagger anteriormente e seria interessante para automatizar a documentação e tests dos endpoints
- Tratamento de erros global: Centralizar o tratamento de exceções ao invés de try/catch no controller
