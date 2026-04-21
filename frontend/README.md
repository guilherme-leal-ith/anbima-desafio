# Frontend

Interface Angular para interagir com os Módulos A e B.

## Primeiros passos no Angular

Não tinha muita experiência com Angular antes. Para aprender o básico necessário para o desafio, assisti vídeos no YouTube e li a documentação oficial
do Angular. Dois conhecimentos prévios me ajudaram bastante:

- **React**: já havia trabalhado com React anteriormente, então os conceitos de componentização, props e estado foram familiares, pois o
Angular segue a mesma ideia de dividir a interface em componentes reutilizáveis
- **TypeScript**: já tinha alguma experiência com TypeScript, o que facilitou entender a tipagem dos componentes e services

## Decisões técnicas

### PedidoService
Camada de comunicação com os backends. Implementei enviarPosicional() que chama o Módulo A com Content-Type: text/plain, conforme especificado no case. Também implementei listarPedidos(), que chama o Módulo B para buscar todos os pedidos.
