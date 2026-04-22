# Módulo B — Entrega/Processor

Responsável por consumir a fila, atualizar pedidos para ENTREGUE e expor endpoints de consulta conforme o case pede.

## Fluxo
ProcessadorPedidos (@Scheduled a cada 3s) -> Busca todos os pedidos com status RECEBIDO -> Atualiza cada um para ENTREGUE -> Persiste no banco

## Decisões técnicas

### Entidade Pedido e enum StatusPedido
- Cópia do Módulo A, já que módulos separados não compartilham código no MVP. O mapeamento é igual a fim de garantir compatibilidade com a 
tabela compartilhada.

### Repository
- O uso do findByStatus() permite buscar diretamente os pedidos com status RECEBIDO sem filtrar em memória.

### Service
- O método processarPedidosRecebidos() busca todos os pedidos com status RECEBIDO e atualiza para ENTREGUE como se fosse o listener da fila.
- Uso do orElseThrow com mensagem clara para facilitar debug quando pedido não encontrado

### Scheduler no lugar do RabbitMQ
- O case pede um listener da fila 'pedidos.recebidos', no MVP usei @Scheduled que consulta o banco a cada 3 segundos buscando pedidos RECEBIDOS, dessa forma simulando o comportamento do listener.
- Logo, de 3 em 3 segundos é checado se possui pedidos com o status=RECEBIDO, caso tenha eles são atualizados para status=ENTREGUE

### Controller
- GET /pedidos -> lista todos os pedidos
- GET /pedidos/{id} -> consulta pedido específico, fiz que retornasse 404 se o id não for encontrado, ao invés de um erro 500, que só indica que houve um erro interno do servidor

### CORS
Configurei o CorsConfig para permitir requisições do frontend (localhost:4200). Sem essa configuração o browser bloqueia as chamadas identifiquei o erro no console do browser e pesquisei a solução.

### Banco de dados
Compartilha o mesmo PostgreSQL do Módulo A via Docker Compose. A leitura dos pedidos persistidos pelo Módulo A funciona corretamente
pois ambos apontam para a mesma instância e banco. Preferi não mudar as evidências feitas usando H2, pois o H2 ajuda a visualizar e a lógica por trás da persistência não mudou.

## Evidências

### Scheduler + Service
Após subir o Módulo B com um pedido RECEBIDO no banco, o scheduler executa automaticamente e atualiza o status:

1. Fiz um POST para o Módulo A, usando cURL de um pedido com status=RECEBIDO
<img width="1096" height="94" alt="image" src="https://github.com/user-attachments/assets/e47e022d-9b05-4913-b450-2f3d209b40f7" />

2. Após 3 segundos, o service do Módulo B busca na fila por status=RECEBIDO e atualiza para status=ENTREGUE e persiste no banco.
<img width="516" height="280" alt="image" src="https://github.com/user-attachments/assets/ce98083a-b49c-4070-9619-5737b5d38203" />

### Banco atualizado
Confirmação via H2 Console que o status foi persistido como ENTREGUE:
<img width="975" height="263" alt="image" src="https://github.com/user-attachments/assets/5aae5c5b-2238-45ea-9d4a-c2f283f54361" />

### Controller
O banco possui 2 pedidos: <img width="758" height="107" alt="image" src="https://github.com/user-attachments/assets/ef28a107-e672-445d-8b6e-49ca838c6a59" />

- Testa o retorno de todos os pedidos, já que é GET podemos acessar via localhost:8081/pedidos:
<img width="1333" height="162" alt="image" src="https://github.com/user-attachments/assets/4caafdd3-6b2a-437c-81d3-f48b2eb57b49" />

- Testa o retorno de pedido específico (nesse caso o de id 4), já que é GET podemos acessar via localhost:8081/pedidos/4
<img width="1332" height="144" alt="image" src="https://github.com/user-attachments/assets/467c8246-c3b3-4b5d-b701-0c4a8d084d3d" />

- Testa o retorno do erro 404, ao acessar o localhost:8081/pedidos/10
<img width="611" height="244" alt="image" src="https://github.com/user-attachments/assets/e55df5cd-d166-4221-b128-e054c32529c0" />

## Testes unitários

### EntregaService
- Pedido RECEBIDO -> atualizado para ENTREGUE e salvo
- Nenhum pedido RECEBIDO -> save não é chamado
- Pedido não encontrado -> exceção lançada
- Todos os pedidos listados -> findAll() chamado e lista retornada corretamente
- Pedido encontrado por id -> campos retornados corretamente
<img width="630" height="286" alt="image" src="https://github.com/user-attachments/assets/31779666-7111-47a6-9cf2-29601c6ba478" />

## Como rodar

Rodar aplicação
1. Dentro da pasta modulo-b
2. mvn spring-boot:run - roda a aplicação

Rodar testes
1. Dentro da pasta modulo-b
2. mvn test - roda os testes
