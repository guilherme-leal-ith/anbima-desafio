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
    - Módulo B completo e testado - Concluído
    - Testes unitários - Concluído
    - Frontent - Concluído

---

## Sobre o histórico de commits

Optei por commits pequenos e incrementais seguindo o padrão Conventional Commits, para padronizar e deixar o histórico legível.

A quantidade de commits foi intencional, quis deixar claro o progresso e o raciocínio por trás de cada decisão, já que o case pede exatamente isso. Cada commit representa uma etapa ou decisão concluída.

Quis que os commits seguissem um padrão profissional, então fiz assim:
branch por feature -> commits incrementais -> PR com descrição -> merge na main

---

## Decisões técnicas

### .gitignore
Analisei e pesquisei cuidadosamente o que não deveria ser enviado para o repo. Decidi, excepcionalmente, enviar o arquivo application.properties, mesmo que possa conter dados sensíveis como a senha do banco de dados, a fim de facilitar a execução do avaliador.

## Banco de dados
Escolhi o PostgrSQL, por ser robusto, muito utilizado no mercado e adequado ao schema definido no desafio. Para a criação do MVP dos módulos, usei o H2, para facilitar a resolver o essencial, já que é mais simples configurar e de visualizar. Mas depois do MVP estar pronto adicionei dockerizei o PostgreSQL para mais profissionalismo na entrega.

## Fila e mensageria
Fui atrás de entender melhor sobre a mensageria no sistema, li um post sobre mensagens assíncronas no stackoverflow e vi um vídeo no youtube sobre mensageria em Spring Boot. Compreendi que o correto seria usar um Message Broker, foi ai que cheguei no RabbitMQ que é a solução padrão do ecossistema Spring.
Para o MVP implementei uma fila em memória com a mesma interface (publicar/consumir) que o RabbitMQ teria, e o Módulo B simulava o listener com @Scheduled. Após o MVP funcionar, refatorei para RabbitMQ real, o Módulo A publica na fila do broker e o Módulo B consome com @RabbitListener, exatamente como o case pede.

---

## O que aprendi nesse desafio
- Como funciona comunicação assíncrona entre serviços e por que
  message brokers existem
- Como H2 se comporta em memória vs arquivo e as implicações
  de cada modo
- Política de CORS e como configurar no Spring Boot
- Fluxo profissional de Git com branches, PRs e Conventional Commits
- Conceitos básicos de Angular aproveitando base de React

---

## Desafio enfrentado

### H2 em memória vs arquivo e comunicação entre módulos

A fim de elucidar um dos problemas que enfrentei durante a implementação do código. Ao terminar o Módulo A e começar o Módulo B, percebi que o Módulo B precisava acessar os mesmos dados que o Módulo A persistia. Parecia simples, sendo que os dois usavam H2 e apontavam para o mesmo nome de banco. Porém ao subir os dois módulos juntos, o Módulo B não enxergava nenhum dado. Fui investigar e entendi o problema: H2 em modo memória (jdbc:h2:mem) só existe dentro do processo que o criou. Aprendi que cada aplicação Spring Boot é um processo separado na JVM, então cada um tinha seu próprio banco em memória, completamente isolados.

Optei resolver pelo H2 em arquivo por ser mais alinhado para o MVP já que ele já estava em produção, não precisando subir infraestrutura externa. Mas ao tentar conectar os dois módulos ao mesmo arquivo, outro erro apareceu: Database may be already in use
O H2 em arquivo por padrão só permite uma conexão por vez. Pesquisei novamente e encontrei a solução: AUTO_SERVER=TRUE, a quak habilita
um servidor interno no H2 permitindo múltiplas conexões simultâneas ao mesmo arquivo.

properties
spring.datasource.url=jdbc:h2:file:./pedidosdb;AUTO_SERVER=TRUE

Com isso os dois módulos passaram a compartilhar o banco corretamente. Foi um problema que me fez entender na prática a diferença entre
processos, memória e como bancos de dados funcionam em diferentes modos.

### Comunicação entre frontend e backends (CORS)
Ao integrar o frontend Angular com os dois módulos, as chamadas HTTP falhavam no navegador. Fui investigar e entendi o problema: navegadores bloqueiam requisições entre origens diferentes por padrão, o Angular bloqueia em localhost:4200 para os módulos em localhost:8080 e localhost:8081. Esse mecanismo se chama CORS. Pesquisei como resolver e optei por configurar nos backends, pois quem decide quais origens podem acessar seus recursos é o servidor, não o cliente. Criei uma classe 'CorsConfig' em cada módulo liberando explicitamente a origem do Angular. Com isso as chamadas passaram a funcionar corretamente para ambos os módulos.

---

## Estado atual

### Módulo A - concluído
Fluxo completo funcionando:
POST /pedidos/posicional -> Parser -> Service -> Banco -> Fila -> 201

### Módulo B - concluído
RabbitMQ consome mensagens da fila pedidos.recebidos e atualiza pedidos para ENTREGUE.
GET /pedidos e GET /pedidos/{id} funcionando.

### Frontend - concluído
Tela Novo Pedido e tela Pedidos com filtro por status funcionando.

## Como rodar

### Primeiro passo - PostgreSQL e RabbitMQ
Subir PostgreSQL e RabbitMQ via docker, dentro da pasta raíz:
1. docker-compose up -d
Painel do RabbitMQ disponível em localhost:15672 (guest/guest)

### Segundo passo - Módulo A

Dentro da pasta modulo-a
- mvn spring-boot:run - roda a aplicação

Porta: 8080

### Terceiro passo - Módulo B

Dentro da pasta modulo-b
- mvn spring-boot:run - roda a aplicação

Porta: 8081

### Quarto passo - Frontend

Dentro da pasta frontend
- npm install - baixa os pacotes
- ng serve - roda a aplicação

Porta: 4200

Ordem para rodar as aplicações: Módulo A -> Módulo B -> Frontend

## Considerações finais

Esse desafio me fez aprender muito na prática, desde conceitos de mensageria assíncrona até fluxo profissional de Git. Cada problema que encontrei virou aprendizado que documentei aqui.
O código atual está funcional e cobre tudo que o case pede, mas sei que ainda há espaço para melhorar, tratamento de erros global, Swagger, casas decimais no valor e pretendo continuar evoluindo mesmo após a entrega, pois estou sempre buscando escrever código mais limpo e profissional.
No fim das contas, quis sempre mostrar minha paixão por tecnologia e o motivo de eu ser a pessoa certa para a ANBIMA e estou muito ansioso para trabalhar com vocês!
