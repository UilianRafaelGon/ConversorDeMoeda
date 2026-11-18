# Carteira Virtual - Conversor de Moeda (DS151)

Aplicativo Android desenvolvido como parte da disciplina **DS151 – Desenvolvimento para Dispositivos Móveis**, seguindo os padrões e práticas ministradas nas aulas.

## 🚀 Funcionalidades

1. **Tela Inicial (MainActivity)**:
   - Exibe o saldo principal em **R$ (Real)** persistido no banco de dados.
   - Navegação direta para:
     - Depositar R$
     - Listar Recursos
     - Converter Recursos

2. **Activity de Depósito (DepositoActivity)**:
   - Permite informar um valor em R$ para crédito na carteira virtual.
   - Atualiza de forma persistente o saldo no banco de dados SQLite (`carteira_virtual.db`).
   - Valida entradas inválidas ou menores/iguais a zero.

3. **Activity de Listar Recursos (ListarRecursosActivity)**:
   - Exibe em um `ListView` com layout customizado (`RecursosAdapter`) todos os recursos financeiros:
     - **Real (BRL)**
     - **Dólar (USD)**
     - **Euro (EUR)**
     - **Bitcoin (BTC)**
     - **Ethereum (ETH)**
   - Exibe nome da moeda, código e saldo formatado.

4. **Activity de Converter Recursos (ConverterRecursosActivity)**:
   - Permite selecionar moeda de origem e destino via `Spinner`.
   - Exibe o saldo disponível em tempo real para ambas as moedas selecionadas.
   - Validação de saldo suficiente antes da conversão.
   - Integração com a **Awesome API** (`https://economia.awesomeapi.com.br/`) via **Retrofit** e **Kotlin Coroutines**.
   - Exibição de `ProgressBar` enquanto a requisição à API é processada.
   - Atualização e persistência atômica dos saldos de débito e crédito no SQLite após a conversão.

## 👥 Equipe
- Nome: [Preencher com os nomes dos integrantes]
- GRR: [Preencher com os GRRs]
- Repositório: [Link do GitHub]

