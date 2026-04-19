# Desafio ANBIMA - Sistema de pedidos

## Primeiros passos

Ao receber o case meu processo foi:
1. **Entendendo o problema**: Li atentamente todo o case, adquirindo o máximo de informações possíveis, assimilando o que é pedido e as partes principais, para que eu pudesse internalizar o que é pedido e fazer uma priorização de etapas do desenvolvimento.
2. **Dividindo em partes menores**: Separei o problema assim
  - Parsing e validação da string recebida
  - Aplicar regras de negócio
  - Persistência e mensageria (parte que mais pesquisei)
  - Front
3. **Prorizando**: Como o foco da avaliação é o backend, conforme foi alinhado na vaga e na entrevista, priorizei assim
    - Módulo A completo e testado - Concluído
    - Módulo B completo e testado - Em andamento
    - Testes unitários
    - Frontent

---

## Sobre o histórico de commits

Optei por commits pequenos e incrementais seguindo o padrão Conventional Commits, para padronizar e deixar o histórico legível.

A quantidade de commits foi intencional, quis deixar claro o progresso e o raciocínio por trás de cada decisão, já que o case pede exatamente isso. Cada commit representa uma etapa ou decisão concluída.

Quis que os commits seguissem um padrão profissional, então fiz assim:
branch por feature → commits incrementais → PR com descrição → merge na main

---

## Decisões técnicas

### .gitignore
Analisei e pesquisei cuidadosamente o que não deveria ser enviado para o repo. Decidi, excepcionalmente, enviar o arquivo application.properties, mesmo que possa conter dados sensíveis como a senha do banco de dados, a fim de facilitar a execução do avaliador.

## Banco de dados
Escolhi o PostgrSQL, por ser robusto, muito utilizado no mercado e adequado ao schema definido no desafio. Para a criação do MVP dos módulos, vou usar o H2, para facilitar a resolver o essencial, já que é mais simples configurar o H2.
Durante o desenvolvimento do Módulo B identifiquei um problema: H2 em memória só existe dentro do processo que o criou, então o Módulo B não conseguia acessar o banco do Módulo A. Resolvi migrando para H2 em modo arquivo com várias conexões ao mesmo o tempo para o mesmo arquivo.

## Fila e mensageria
Fui atrás de entender melhor sobre a mensageria no sistema, li um post sobre mensagens assíncronas no stackoverflow e vi um vídeo no youtube sobre mensageria em Spring Boot. Compreendi que o correto seria usar um Message Broker, foi ai que cheguei no RabbitMQ que é a solução padrão do ecossistema Spring. Para o MVP vou implementar um fila em memória que publica/consome, igual o RabbitMq teria. Posteriormente, refatorar para RabbitMQ, sendo uma solução mais profissional.

---

## Estado atual

### Módulo A — concluído
Fluxo completo funcionando: POST /pedidos/posicional -> Parser -> Service -> Banco -> Fila -> 201

### Módulo B — em andamento
Consumidor da fila e endpoints de consulta.

### Frontend - não comecei
A ser implementado após o Módulo B.
