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

