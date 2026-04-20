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
