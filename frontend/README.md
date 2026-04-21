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

### Tela Novo Pedido
Validação de 40 chars feita no frontend antes de chamar a API, já pensando em evitar sobrecarga do backend com dados inválidos. Também quis fazer um Contador de caracteres em tempo real para facilitar a visualização do avaliador.

### Navegação
Navegação simples entre telas via botões sem roteador. Uso do @if ao invés de *ngIf conforme pesquisei e é recomendado no Angular 21.

### Tela Pedidos
Filtro por status implementado no frontend via getter para evitar chamada extra de endpoint. Usei o que ngOnInit carrega os pedidos automaticamente ao abrir a tela. O valor é formatado com 'number' do Angular.
