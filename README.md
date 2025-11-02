# Sistema de Moeda Estudantil
## Integrantes
* Carlos José Gomes Batista Figueiredo
* Gabriela Alvarenga Cardoso
* Luísa Oliveira Jardim

## Orientador
* João Pedro Oliveira Batisteli


## *História de Usuários*
##**Histórias de alunos:**

**HU001**: Como aluno, quero me cadastrar no sistema para usar todos os recursos disponíveis.
**HU002**: Como aluno, desejo receber moedas dos professores, para ser reconhecido pelo meu desempenho.
**HU003**: Como aluno, desejo receber notificações no email sempre que receber uma moeda, incluindo o motivo do reconhecimento.
**HU004**: Como aluno, desejo consultar meu saldo de moedas e histórico de transações.
**HU005**: Como aluno, desejo trocar moedas recebidas por vantagens cadastradas no sistema, como descontos ou materiais específicos.
**HU006**: Como aluno, desejo receber um cupom no email contendo um código para realizar as trocas presenciais.

**Histórias do Professor:**
**HU007**: Como professor, desejo enviar moedas para meus alunos em forma de reconhecimento de desempenho.
**HU008**: Como professor, desejo consultar meu saldo de moedas e histórico de transações.

**Histórias da Empresa Parceira:**
**HU009**: Como empresa, desejo realizar cadastro no sistema para oferecer vantagens aos alunos em troca de moedas.
**HU010**: Como empresa, desejo cadastrar vantagens no sistema, informando descrição, foto e custo em moedas.
**HU011**: Como empresa, desejo receber um email com detalhes e o código de troca realizada pelo aluno, para facilitar a conferência.

**História de Acesso ao sistema:**
**HU012**: Como usuário (aluno, professor ou empresa), desejo realizar login no sistema para acessar as funcionalidades disponíveis.
---

## Requisitos

### Requisitos Funcionais (RF)

| RF   | Descrição                                                                                      | Complexidade |
|:-----|:----------------------------------------------------------------------------------------------|:------------|
| RF001 | O sistema deve permitir o cadastro de alunos, incluindo nome, email, CPF, RG, endereço, instituição de ensino e curso. | Média |
| RF002 | O sistema deve permitir que professores enviem moedas para alunos, indicando o montante e o motivo do reconhecimento. | Alta |
| RF003 | O sistema deve notificar os alunos por email sempre que receberem moedas, incluindo o motivo do reconhecimento. | Média |
| RF004 | O sistema deve permitir que alunos e professores consultem o saldo de moedas e o histórico de transações. | Média |
| RF005 | O sistema deve permitir que alunos troquem moedas por vantagens cadastradas no sistema, como descontos ou materiais específicos. | Alta |
| RF006 | O sistema deve permitir o cadastro de empresas parceiras, incluindo as vantagens que desejam oferecer, com descrição, foto e custo em moedas. | Alta |
| RF007 | O sistema deve enviar um email com um cupom ao aluno ao resgatar uma vantagem, contendo um código para realizar a troca presencial. | Média |
| RF008 | O sistema deve enviar um email à empresa parceira com os detalhes e o código da troca realizada pelo aluno. | Média |
| RF009 | O sistema deve permitir que alunos, professores e empresas realizem login e autenticação para acessar suas funcionalidades. | Alta |
| RF010 | O sistema deve permitir que a instituição pré-cadastre professores e esteja vinculada aos alunos. | Média |

### Requisitos Não Funcionais (RNF)

| RNF  | Descrição                                                                                      | Complexidade |
|:-----|:----------------------------------------------------------------------------------------------|:------------|
| RNF001 | O sistema deve ser acessível via navegador web.                                              | Baixa       |
| RNF002 | O sistema deve enviar notificações por email em até 5 segundos após a transação.             | Média       |
| RNF003 | O sistema deve armazenar dados de forma segura, utilizando criptografia para senhas.         | Alta        |

---

## Projeto

### Diagrama de Casos de Uso

![UseCaseDiagram]( )

### Diagrama de Classes

![UML]( )

### Diagrama de Pacotes

![PackageDiagram]( )
